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

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * @author Sebastian Daschner
 */
@RegisterForReflection
public class ActorRecommendation {

	private String actor;
	private long strength;

	public ActorRecommendation(String actor, long strength) {
		this.actor = actor;
		this.strength = strength;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public long getStrength() {
		return strength;
	}

	public void setStrength(long strength) {
		this.strength = strength;
	}
}
