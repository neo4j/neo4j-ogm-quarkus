/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ac.simons.neo4j.ogm.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ac.simons.neo4j.ogm.test.domain.SomeClass;
import ac.simons.neo4j.ogm.test.ignored.SomeOtherClass;
import io.quarkus.test.QuarkusDevModeTest;
import io.restassured.RestAssured;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * @author Michael J. Simons
 */
public class Neo4jOgmDevModeIT {

	@RegisterExtension
	static final QuarkusDevModeTest devModeTest = new QuarkusDevModeTest()
		.withApplicationRoot(archive -> archive
			.addClass(SomeClass.class)
			.addClass(SomeOtherClass.class)
		);
	private static final String DEV_TOOLS_ENDPOINT = "/q/dev/eu.michael-simons.neo4j.neo4j-ogm-quarkus/entities";

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
