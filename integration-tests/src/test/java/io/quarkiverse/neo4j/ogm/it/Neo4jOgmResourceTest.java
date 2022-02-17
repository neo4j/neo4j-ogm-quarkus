package io.quarkiverse.neo4j.ogm.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class Neo4jOgmResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/neo4j-ogm")
                .then()
                .statusCode(200)
                .body(is("Hello neo4j-ogm"));
    }
}
