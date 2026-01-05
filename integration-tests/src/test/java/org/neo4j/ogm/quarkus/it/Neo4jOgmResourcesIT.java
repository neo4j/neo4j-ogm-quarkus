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
package org.neo4j.ogm.quarkus.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

/**
 * @author Michael J. Simons
 */
@QuarkusIntegrationTest
@DisabledIfSystemProperty(named = "native", matches = "true")
public class Neo4jOgmResourcesIT {

	private final static int NUMBER_OF_INITIAL_MOVIES = 38;

	@Test
	public void getMoviesShouldWork() {
		var response = RestAssured.given()
			.when().get("/api/movies")
			.then()
			.statusCode(200)
			.extract().response();

		var json = response.jsonPath();
		assertEquals(NUMBER_OF_INITIAL_MOVIES, json.<List<?>>getJsonObject("$").size());
		var allTitles = json.<List<String>>getJsonObject("title");
		assertTrue(allTitles.contains("Cloud Atlas"));
	}

	@Test
	public void getMovieWithANativeTypeShouldWork() {
		var response = RestAssured.given()
			.when().get("/api/movies/The Matrix")
			.then()
			.statusCode(200)
			.extract().response();

		var json = response.jsonPath();
		assertNotNull(json.get("watchedOn"));
	}

	@Test
	public void createPersonShouldWork() {

		var response = RestAssured.given()
			.body("{\"name\":\"Lieschen Müller\",\"born\":2020}")
			.contentType(ContentType.JSON)
			.when().post("/api/people")
			.then().statusCode(201)
			.extract().response();

		var json = response.jsonPath();
		assertNotNull(json.getObject("id", Long.class));
	}
}
