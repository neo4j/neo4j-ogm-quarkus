/*
 * Copyright 2022-2025 the original author or authors.
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
package org.neo4j.ogm.quarkus.it.movies;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * @author Michael J. Simons
 */
@RequestScoped
@Path("/api/people")
public class PeopleResource {

	private final PeopleRepository peopleRepository;

	/**
	 * @param peopleRepository the repository to retrieve people from
	 */
	public PeopleResource(PeopleRepository peopleRepository) {
		this.peopleRepository = peopleRepository;
	}

	/**
	 * Creates a new person
	 *
	 * @param newPerson the new person
	 * @return response containing the new person
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createNewPerson(Person newPerson) {

		var savedPerson = peopleRepository.save(newPerson);
		return Response.status(Response.Status.CREATED).entity(savedPerson).build();
	}
}
