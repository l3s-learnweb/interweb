package de.l3s.interwebj.rest;


import java.io.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.*;
import com.sun.jersey.core.header.*;
import com.sun.jersey.multipart.*;
import com.sun.jersey.multipart.file.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.util.*;


@Path("/users/default/uploads")
public class Upload
    extends Endpoint
{
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String getQueryResult(@FormDataParam("title") String title,
	                             @FormDataParam("description") String description,
	                             @FormDataParam("tags") String tags,
	                             @FormDataParam("is_private") String privacy,
	                             @FormDataParam("content_type") String contentType,
	                             @FormDataParam("data") FormDataContentDisposition disposition,
	                             @FormDataParam("data") byte[] data)
	    throws IOException, InterWebException
	{
		Engine engine = Environment.getInstance().getEngine();
		InterWebPrincipal principal = getPrincipal();
		Environment.logger.info("principal: [" + principal + "]");
		Parameters params = new Parameters();
		if (title != null)
		{
			params.add(Parameters.TITLE, title);
		}
		if (description != null)
		{
			params.add(Parameters.DESCRIPTION, description);
		}
		if (tags != null)
		{
			params.add(Parameters.TAGS, tags);
		}
		if (privacy != null)
		{
			params.add(Parameters.PRIVACY, privacy);
		}
		String fileName = disposition.getFileName();
		if (fileName != null)
		{
			params.add(Parameters.FILENAME, fileName);
		}
		engine.upload(data,
		              principal,
		              engine.getConnectorNames(),
		              contentType,
		              params);
		return "hello";
	}
	

	public static void main(String[] args)
	{
		
		AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***",
        "***REMOVED***");
		AuthCredentials userCredentials = new AuthCredentials("***REMOVED***",
        "***REMOVED***");

		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		
		UriBuilder uriBuilder = UriBuilder.fromUri("***REMOVED***/api/users/default/uploads");
		MultiPart multiPart = new MultiPart();
		String title = "the title 1";
		String description = "the description 2";
		multiPart = multiPart.bodyPart(new FormDataBodyPart("title", title));
		multiPart = multiPart.bodyPart(new FormDataBodyPart("description", description));
		multiPart = multiPart.bodyPart(new FormDataBodyPart("content_type", "image"));
		File f = new File("C:\\Programmieren\\bild.jpg");
		multiPart = multiPart.bodyPart(new FileDataBodyPart("data",
		                                                    f,
		                                                    MediaType.MULTIPART_FORM_DATA_TYPE));
		multiPart = multiPart.bodyPart(new FormDataBodyPart("data", "the data"));
		WebResource resource = createWebResource("***REMOVED***/api/users/default/uploads", consumerCredentials, userCredentials);
		WebResource.Builder builder = resource.type(MediaType.MULTIPART_FORM_DATA);
		builder = builder.accept(MediaType.TEXT_PLAIN);
		Environment.logger.info("testing upload to interwebj: "
		                        + resource.toString());
		ClientResponse response = builder.post(ClientResponse.class, multiPart);
		try
		{
			CoreUtils.printClientResponse(response);
			System.out.println(CoreUtils.getClientResponseContent(response));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
