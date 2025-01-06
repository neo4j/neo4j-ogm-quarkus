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
package org.neo4j.ogm.quarkus.deployment;

import io.quarkus.builder.item.SimpleBuildItem;
import io.quarkus.runtime.RuntimeValue;

import org.neo4j.ogm.session.SessionFactory;

/**
 * Allows access to the Neo4j-OGM {@link SessionFactory} instance from within other extensions.
 *
 * @author Michael J. Simons
 */
public final class Neo4jOgmSessionFactoryBuildItem extends SimpleBuildItem {

	private final RuntimeValue<SessionFactory> value;

	Neo4jOgmSessionFactoryBuildItem(RuntimeValue<SessionFactory> value) {
		this.value = value;
	}

	/**
	 * @return The actual value of this item
	 */
	public RuntimeValue<SessionFactory> getValue() {
		return value;
	}
}
