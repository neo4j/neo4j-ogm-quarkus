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
package org.neo4j.ogm.quarkus.runtime;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;

import java.util.List;
import java.util.Optional;

/**
 * Shim between {@link org.neo4j.ogm.config.Configuration} and the Smallrye configuration.
 *
 * @author Michael J. Simons
 */
@ConfigMapping(prefix = "org.neo4j.ogm")
@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
public interface Neo4jOgmBuiltTimeProperties {

	/**
	 * An optional list of packages to scan. If empty, all classes annotated with
	 * {@link org.neo4j.ogm.annotation.NodeEntity @NodeEntity}
	 * or {@link org.neo4j.ogm.annotation.RelationshipEntity @RelationshipEntity} will be added to the index.
	 * @return the list of packages to scan
	 */
	Optional<List<String>> basePackages();
}
