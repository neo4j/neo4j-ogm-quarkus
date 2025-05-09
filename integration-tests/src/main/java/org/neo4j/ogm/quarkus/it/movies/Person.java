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

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * @author Michael J. Simons
 */
@NodeEntity
public final class Person {

	@Id @GeneratedValue
	private Long id;

	private String name;

	private Integer born;

	/**
	 * A new person with a
	 *
	 * @param name given name
	 * @param born and a year in which they have been born
	 */
	@JsonbCreator
	public Person(@JsonbProperty("name") String name, @JsonbProperty("born") Integer born) {
		this.name = name;
		this.born = born;
	}

	/**
	 * Make OGM happy.
	 */
	Person() {
	}

	/**
	 * @return The person id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/***
	 * @return birth year
	 */
	public Integer getBorn() {
		return born;
	}

	/**
	 * @param born a new birth year
	 */
	public void setBorn(Integer born) {
		this.born = born;
	}
}
