package io.quarkiverse.neo4j.ogm.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.neo4j.ogm.test.domain.SomeClass;
import io.quarkiverse.neo4j.ogm.test.ignored.SomeOtherClass;
import io.quarkus.test.QuarkusDevModeTest;
import io.restassured.RestAssured;

public class Neo4jOgmDevModeTest {

    @RegisterExtension
    static final QuarkusDevModeTest devModeTest = new QuarkusDevModeTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(SomeClass.class)
                    .addClass(SomeOtherClass.class));
    private static final String DEV_TOOLS_ENDPOINT = "/q/dev/io.quarkiverse.neo4j.quarkus-neo4j-ogm/domainclasses";

    @Test
    public void listOfDomainClassesShouldBeAvailable() {

        var response = RestAssured
                .given()
                .when().get(DEV_TOOLS_ENDPOINT)
                .then().statusCode(200)
                .extract().response();

        var base = "html.body.div.table.tbody.";
        var p = response.getBody().htmlPath().getString(base + "tr[0].td[0]");
        var c = response.getBody().htmlPath().getString(base + "tr[0].td[1]");
        assertEquals(SomeClass.class.getPackageName(), p);
        assertEquals(SomeClass.class.getSimpleName(), c);

        p = response.getBody().htmlPath().getString(base + "tr[1].td[0]");
        c = response.getBody().htmlPath().getString(base + "tr[1].td[1]");
        assertEquals(SomeOtherClass.class.getPackageName(), p);
        assertEquals(SomeOtherClass.class.getSimpleName(), c);
    }
}
