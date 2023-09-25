package de.l3s.interweb.server;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

import io.quarkus.test.junit.QuarkusTest;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class WelcomeResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/q/health/live")
          .then()
             .statusCode(HttpStatus.SC_OK)
             .body(containsString("\"status\": \"UP\""));
    }

}