package de.l3s.interwebj.rest;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.util.CoreUtils;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class UploadTest {

    @Test
    void getQueryResult() {
        // AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        // AuthCredentials userCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        //
        // MultiPart multiPart = new MultiPart();
        // String title = "the title 1";
        // String description = "the description 2";
        // multiPart = multiPart.bodyPart(new FormDataBodyPart("title", title));
        // multiPart = multiPart.bodyPart(new FormDataBodyPart("description", description));
        // multiPart = multiPart.bodyPart(new FormDataBodyPart("content_type", "video"));
        // //File f = new File("C:\\Programmieren\\bild.jpg");
        // File f = new File("C:\\Programmieren\\anni002.mpg");
        // multiPart = multiPart.bodyPart(new FileDataBodyPart("data", f, MediaType.MULTIPART_FORM_DATA_TYPE));
        // multiPart = multiPart.bodyPart(new FormDataBodyPart("data", "the data"));
        //
        // //WebResource resource = createWebResource("http://localhost:8080/InterWebJ/api/users/default/uploads", consumerCredentials, userCredentials);
        // WebResource resource = createWebResource("***REMOVED***_test/api/users/default/uploads", consumerCredentials, userCredentials);
        // WebResource.Builder builder = resource.type(MediaType.MULTIPART_FORM_DATA);
        // builder = builder.accept(MediaType.APPLICATION_XML);
        // log.info("testing upload to interwebj: " + resource.toString());
        // ClientResponse response = builder.post(ClientResponse.class, multiPart);
        // multiPart.close();
        // try
        // {
        //     CoreUtils.printClientResponse(response);
        //     System.out.println(CoreUtils.getClientResponseContent(response));
        // }
        // catch(IOException e)
        // {
        //     e.printStackTrace();
        // }
    }
}