/*
 * Copyright 2022-2023 the original author or authors.
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

import org.neo4j.ogm.quarkus.runtime.Neo4jOgmDevConsoleRecorder;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
import io.quarkus.devconsole.spi.DevConsoleRuntimeTemplateInfoBuildItem;

import java.util.ArrayList;

/**
 * @author Michael J. Simons
 */
public class Neo4jOgmDevConsoleProcessor {

	/**
	 * Records the list of domain classes into the info space.
	 *
	 * @param entitiesBuildItem      The build item containing the annotated OGM classes
	 * @param curateOutcomeBuildItem The curated build item producer
	 * @param recorder               Recorder for the supplier
	 * @return A new template item
	 */
	@BuildStep(onlyIf = IsDevelopment.class)
	@Record(ExecutionTime.RUNTIME_INIT)
	@SuppressWarnings("unused")
	DevConsoleRuntimeTemplateInfoBuildItem addAnnotatedClasses(
		EntitiesBuildItem entitiesBuildItem,
		CurateOutcomeBuildItem curateOutcomeBuildItem,
		Neo4jOgmDevConsoleRecorder recorder) {

		var supplier = recorder.recordEntities(new ArrayList<>(entitiesBuildItem.getValue()));
		return new DevConsoleRuntimeTemplateInfoBuildItem("entities",
			supplier, this.getClass(),
			curateOutcomeBuildItem);
	}
}
