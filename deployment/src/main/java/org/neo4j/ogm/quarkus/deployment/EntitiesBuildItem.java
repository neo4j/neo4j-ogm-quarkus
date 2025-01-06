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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * The build item containing the set of domain entities: All classes annotated with {@link org.neo4j.ogm.annotation.NodeEntity @NodeEntity},
 * {@link org.neo4j.ogm.annotation.RelationshipEntity @RelationshipEntity} or computed via additional means.
 *
 * @author Michael J. Simons
 * @since 1.0.0
 */
final class EntitiesBuildItem extends SimpleBuildItem {

	private final Set<Class<?>> classes;

	EntitiesBuildItem(Set<Class<?>> classes) {
		this.classes = classes;
	}

	/**
	 * @return A read only view of the known entities.
	 */
	public Collection<Class<?>> getValue() {
		return Collections.unmodifiableCollection(this.classes);
	}
}
