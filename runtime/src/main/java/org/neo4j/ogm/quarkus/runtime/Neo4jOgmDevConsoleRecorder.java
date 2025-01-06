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

import io.quarkus.runtime.annotations.Recorder;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * @author Michael J. Simons
 */
@Recorder
public class Neo4jOgmDevConsoleRecorder {

	/**
	 * Creates a supplier for a list of classes
	 *
	 * @param entities The list of classes to be supplied
	 * @return A recordable supplier
	 */
	public Supplier<Collection<Class<?>>> recordEntities(Collection<Class<?>> entities) {

		var supplier = new EntitiesSupplier();
		supplier.setValue(entities);
		return supplier;
	}
}
