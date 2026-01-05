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
package org.neo4j.ogm.quarkus.runtime;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.util.Optional;

/**
 * Shim between {@link org.neo4j.ogm.config.Configuration} and the Smallrye configuration.
 *
 * @author Michael J. Simons
 */
@ConfigMapping(prefix = "org.neo4j.ogm")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface Neo4jOgmProperties {

	/**
	 * {@return whether Neo4j should use native types be used for dates, times and similar}
	 */
	@WithDefault("false")
	boolean useNativeTypes();

	/**
	 * {@return a flag that instructs OGM to use all static labels when querying domain objects}
	 */
	@WithDefault("false")
	boolean useStrictQuerying();

	/**
	 * {@return the database that should be used (Neo4j EE 4.0+ only), Leave empty for using the default database}
	 */
	Optional<String> database();
}
