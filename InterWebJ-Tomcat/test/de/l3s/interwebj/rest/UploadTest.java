package de.l3s.interwebj.rest;

import de.l3s.interwebj.AuthCredentials;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.File;
import java.io.IOException;

import static de.l3s.interwebj.rest.Endpoint.createWebTarget;

class UploadTest {
    private static final Logger log = LogManager.getLogger(UploadTest.class);

    @Test
    void getQueryResult() throws IOException {
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

        WebTarget target = createWebTarget("users/default/uploads", consumerCredentials, userCredentials);
        log.info("testing upload to interwebj: " + target.toString());
        Response response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(multiPart, multiPart.getMediaType()));
        multiPart.close();

        System.out.println(response.readEntity(String.class));
    }
}