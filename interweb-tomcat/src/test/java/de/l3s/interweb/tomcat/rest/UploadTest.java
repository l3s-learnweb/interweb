package de.l3s.interweb.tomcat.rest;

import java.io.File;
import java.io.IOException;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.tomcat.TestUtils;

@Disabled
class UploadTest {
    private static final Logger log = LogManager.getLogger(UploadTest.class);

    @Test
    void getQueryResult() throws IOException {
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

        WebTarget target = TestUtils.createWebTarget("users/default/uploads");
        log.info("testing upload to InterWeb: {}", target.toString());
        Response response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(multiPart, multiPart.getMediaType()));
        multiPart.close();

        System.out.println(response.readEntity(String.class));
    }
}
