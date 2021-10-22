package de.l3s.interwebj.tomcat.rest;

import java.security.Principal;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.Parameters;
import de.l3s.interwebj.core.core.Engine;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.query.ContentType;
import de.l3s.interwebj.core.query.ResultItem;
import de.l3s.interwebj.tomcat.jaxb.UploadResponse;

@Path("/users/default/uploads")
public class Upload extends Endpoint {
    private static final Logger log = LogManager.getLogger(Upload.class);

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public UploadResponse getQueryResult(@FormDataParam("title") String title, @FormDataParam("description") String description,
        @FormDataParam("tags") String tags, @FormDataParam("is_private") String privacy,
        @FormDataParam("content_type") String contentType, @FormDataParam("data") FormDataContentDisposition disposition,
        @FormDataParam("data") byte[] data) throws InterWebException {

        Engine engine = Environment.getInstance().getEngine();
        Principal principal = getPrincipal();
        log.info("principal: [{}]", principal);

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

        ResultItem result = engine.upload(data, principal, engine.getConnectorNames(), ContentType.valueOf(contentType), params);

        if (null == result) {
            throw new WebApplicationException("The services did not accept the file", Response.Status.BAD_REQUEST);
        } else {
            return new UploadResponse(result);
        }
    }
}
