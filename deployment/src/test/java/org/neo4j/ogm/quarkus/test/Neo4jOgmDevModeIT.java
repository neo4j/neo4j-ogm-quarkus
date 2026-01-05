/*
 * Copyright 2022-2026 the original author or authors.
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
package org.neo4j.ogm.quarkus.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.neo4j.ogm.quarkus.test.domain.SomeClass;
import org.neo4j.ogm.quarkus.test.ignored.SomeOtherClass;
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

	private static final String DEV_TOOLS_ENDPOINT = "/q/dev-ui/neo4j-ogm-quarkus/entities";
	private static final String DATA_ENDPOINT = "/q/dev-ui/neo4j-ogm-quarkus-data.js";

	@Test
	public void listOfDomainClassesShouldBeAvailable() throws IOException {

		RestAssured
			.given()
			.when().get(DEV_TOOLS_ENDPOINT)
			.then().statusCode(200);

		var data = RestAssured
			.given()
			.when().get(DATA_ENDPOINT)
			.then().statusCode(200)
			.extract().response().body().asString();

		try (var context = Context.newBuilder("js")
			.allowExperimentalOptions(true)
			.option("engine.WarnInterpreterOnly", "false")
			.option("js.esm-eval-returns-exports", "true")
			.build()
		) {

			var result = context.eval(Source.newBuilder("js", data, "data.js")
				.mimeType("application/javascript+module")
				.build());
			var entities = result.getMember("entities");
			assertEquals(2, entities.getArraySize());

			var p = entities.getArrayElement(0).getMember("packageName").asString();
			var c = entities.getArrayElement(0).getMember("simpleName").asString();
			assertEquals(SomeClass.class.getPackageName(), p);
			assertEquals(SomeClass.class.getSimpleName(), c);

			p = entities.getArrayElement(1).getMember("packageName").asString();
			c = entities.getArrayElement(1).getMember("simpleName").asString();
			assertEquals(SomeOtherClass.class.getPackageName(), p);
			assertEquals(SomeOtherClass.class.getSimpleName(), c);
		}
	}
}
