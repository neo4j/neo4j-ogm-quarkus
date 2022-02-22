/*
 * Copyright 2020-2022 the original author or authors.
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
package io.quarkiverse.neo4j.ogm.runtime;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

/**
 * Shim between {@link org.neo4j.ogm.config.Configuration} and the Smallrye configuration.
 *
 * @author Michael J. Simons
 */
@ConfigRoot(prefix = "org.neo4j", name = "ogm", phase = ConfigPhase.RUN_TIME)
public class Neo4jOgmProperties {

    /**
     * Should Neo4j native types be used for dates, times and similar?
     */
    @ConfigItem(defaultValue = "false")
    public boolean useNativeTypes;

    /**
     * This flag instructs OGM to use all static labels when querying domain objects.
     */
    @ConfigItem(defaultValue = "false")
    public boolean useStrictQuerying;

    /**
     * The database that should be used (Neo4j EE 4.0+ only). Leave empty for using the default database.
     */
    @ConfigItem
    public Optional<String> database;
}
