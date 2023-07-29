package de.l3s.interweb.server;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

import io.quarkus.test.junit.QuarkusTest;

import org.junit.jupiter.api.Test;

@QuarkusTest
public class WelcomeResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/q/health")
          .then()
             .statusCode(200)
             .body(containsString("\"status\": \"UP\""));
    }

}