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
package org.neo4j.ogm.quarkus.it.movies;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

/**
 * @author Michael J. Simons
 */
@ApplicationScoped
class PeopleRepository {

	private final SessionFactory sessionFactory;

	PeopleRepository(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	Person save(Person person) {

		var session = sessionFactory.openSession();
		session.save(person);
		return person;
	}

	public List<ActorRecommendation> recommendCoActor(String name) {
		Session session = sessionFactory.openSession();
		return session.queryDto("""
			MATCH (actor:Person {name: $name})-[:ACTED_IN]->(m)<-[:ACTED_IN]-(coActors),
			      (coActors)-[:ACTED_IN]->(m2)<-[:ACTED_IN]-(cocoActors)
			WHERE NOT (actor)-[:ACTED_IN]->()<-[:ACTED_IN]-(cocoActors) AND actor <> cocoActors
			RETURN cocoActors.name AS actor, count(*) AS strength ORDER BY strength DESC""", Map.of("name", name), ActorRecommendation.class);
	}
}
