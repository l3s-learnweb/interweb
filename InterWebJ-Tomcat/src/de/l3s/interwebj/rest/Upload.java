package de.l3s.interwebj.rest;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import de.l3s.interwebj.InterWebException;
import de.l3s.interwebj.Parameters;
import de.l3s.interwebj.core.Engine;
import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.core.InterWebPrincipal;
import de.l3s.interwebj.jaxb.ErrorResponse;
import de.l3s.interwebj.jaxb.SearchResultEntity;
import de.l3s.interwebj.jaxb.UploadResponse;
import de.l3s.interwebj.jaxb.XMLResponse;
import de.l3s.interwebj.query.ResultItem;

@Path("/users/default/uploads")
public class Upload extends Endpoint
{

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_XML)
    public XMLResponse getQueryResult(@FormDataParam("title") String title, @FormDataParam("description") String description, @FormDataParam("tags") String tags, @FormDataParam("is_private") String privacy, @FormDataParam("content_type") String contentType,
	    @FormDataParam("data") FormDataContentDisposition disposition, @FormDataParam("data") byte[] data) throws IOException, InterWebException
    {
	Engine engine = Environment.getInstance().getEngine();
	InterWebPrincipal principal = getPrincipal();
	Environment.logger.info("principal: [" + principal + "]");
	Parameters params = new Parameters();
	if(title != null)
	{
	    params.add(Parameters.TITLE, title);
	}
	if(description != null)
	{
	    params.add(Parameters.DESCRIPTION, description);
	}
	if(tags != null)
	{
	    params.add(Parameters.TAGS, tags);
	}
	if(privacy != null)
	{
	    params.add(Parameters.PRIVACY, privacy);
	}
	String fileName = disposition.getFileName();
	if(fileName != null)
	{
	    params.add(Parameters.FILENAME, fileName);
	}

	ResultItem result = engine.upload(data, principal, engine.getConnectorNames(), contentType, params);

	if(null == result)
	    return ErrorResponse.FILE_NOT_ACCEPTED;
	else
	    return new UploadResponse(new SearchResultEntity(result));
    }

    /*
    public static void main(String[] args) throws IOException
    {
    
    AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
    AuthCredentials userCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
    
    MultiPart multiPart = new MultiPart();
    String title = "the title 1";
    String description = "the description 2";
    multiPart = multiPart.bodyPart(new FormDataBodyPart("title", title));
    multiPart = multiPart.bodyPart(new FormDataBodyPart("description", description));
    multiPart = multiPart.bodyPart(new FormDataBodyPart("content_type", "video"));
    //File f = new File("C:\\Programmieren\\bild.jpg");
    File f = new File("C:\\Programmieren\\anni002.mpg");
    multiPart = multiPart.bodyPart(new FileDataBodyPart("data", f, MediaType.MULTIPART_FORM_DATA_TYPE));
    multiPart = multiPart.bodyPart(new FormDataBodyPart("data", "the data"));
    
    //WebResource resource = createWebResource("http://localhost:8080/InterWebJ/api/users/default/uploads", consumerCredentials, userCredentials);
    WebResource resource = createWebResource("***REMOVED***_test/api/users/default/uploads", consumerCredentials, userCredentials);
    WebResource.Builder builder = resource.type(MediaType.MULTIPART_FORM_DATA);
    builder = builder.accept(MediaType.APPLICATION_XML);
    Environment.logger.info("testing upload to interwebj: " + resource.toString());
    ClientResponse response = builder.post(ClientResponse.class, multiPart);
    multiPart.close();
    try
    {
        CoreUtils.printClientResponse(response);
        System.out.println(CoreUtils.getClientResponseContent(response));
    }
    catch(IOException e)
    {
        e.printStackTrace();
    }
    
    }
    */
}
