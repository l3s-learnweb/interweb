package de.l3s.interwebj.tomcat.rest;

import java.io.IOException;
import java.security.Principal;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.Parameters;
import de.l3s.interwebj.core.core.Engine;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.query.ResultItem;
import de.l3s.interwebj.tomcat.jaxb.ErrorResponse;
import de.l3s.interwebj.tomcat.jaxb.SearchResultEntity;
import de.l3s.interwebj.tomcat.jaxb.UploadResponse;
import de.l3s.interwebj.tomcat.jaxb.XMLResponse;

@Path("/users/default/uploads")
public class Upload extends Endpoint {
    private static final Logger log = LogManager.getLogger(Upload.class);

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public XMLResponse getQueryResult(@FormDataParam("title") String title, @FormDataParam("description") String description,
                                      @FormDataParam("tags") String tags, @FormDataParam("is_private") String privacy,
                                      @FormDataParam("content_type") String contentType, @FormDataParam("data") FormDataContentDisposition disposition,
                                      @FormDataParam("data") byte[] data) throws IOException, InterWebException {
        Engine engine = Environment.getInstance().getEngine();
        Principal principal = getPrincipal();
        log.info("principal: [" + principal + "]");
        Parameters params = new Parameters();
        if (title != null) {
            params.add(Parameters.TITLE, title);
        }
        if (description != null) {
            params.add(Parameters.DESCRIPTION, description);
        }
        if (tags != null) {
            params.add(Parameters.TAGS, tags);
        }
        if (privacy != null) {
            params.add(Parameters.PRIVACY, privacy);
        }
        String fileName = disposition.getFileName();
        if (fileName != null) {
            params.add(Parameters.FILENAME, fileName);
        }

        ResultItem result = engine.upload(data, principal, engine.getConnectorNames(), contentType, params);

        if (null == result) {
            return ErrorResponse.FILE_NOT_ACCEPTED;
        } else {
            return new UploadResponse(new SearchResultEntity(result));
        }
    }
}
