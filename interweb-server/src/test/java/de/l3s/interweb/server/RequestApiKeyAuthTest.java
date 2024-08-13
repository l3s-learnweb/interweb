package de.l3s.interweb.server;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.l3s.interweb.core.search.SearchQuery;
import de.l3s.interweb.core.search.SearchResults;
import de.l3s.interweb.server.features.search.SearchService;
import de.l3s.interweb.server.features.api.ApiKey;
import de.l3s.interweb.server.features.user.User;

@QuarkusTest
class RequestApiKeyAuthTest {

    @InjectMock
    SearchService searchService;

    @BeforeEach
    public void setup() {
        PanacheMock.mock(ApiKey.class);
        ApiKey testKey = Mockito.mock(ApiKey.class);
        Mockito.when(testKey.user).thenReturn(Mockito.mock(User.class));
        Mockito.when(ApiKey.findByApikey("testkey")).thenReturn(Uni.createFrom().item(testKey));

        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setQuery("hello world");
        searchQuery.setContentTypes(de.l3s.interweb.core.search.ContentType.image);
        Mockito.when(searchService.search(searchQuery)).thenReturn(Uni.createFrom().item(new SearchResults()));
    }

    @Test
    void unauthorized() {
        given()
            .when()
                .post("/search")
            .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(Matchers.emptyString());
    }

    @Test
    void wrongKey() {
        JSONObject requestParams = new JSONObject();

        given()
                .body(requestParams)
                .header("Api-Key", "testkey2")
            .when()
                .post("/search")
            .then()
                .body(Matchers.emptyString())
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    void goodKeyBadRequest() {
        Map<String, Object> query = new HashMap<>();
        query.put("query", "hello world");

        given()
                .header("Api-Key", "testkey")
                .contentType(ContentType.JSON)
                .body(query)
            .when()
                .post("/search")
            .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(containsString("Constraint Violation"));
    }

    @Test
    void goodKeyGoodRequest() {
        Map<String, Object> query = new HashMap<>();
        query.put("query", "hello world");
        query.put("content_types", List.of("image"));

        given()
                .header("Api-Key", "testkey")
                .contentType(ContentType.JSON)
                .body(query)
            .when()
                .post("/search")
            .then()
                .statusCode(200)
                .body(
                "elapsed_time", Matchers.greaterThan(0),
                "results", Matchers.notNullValue()
            );
    }
}
