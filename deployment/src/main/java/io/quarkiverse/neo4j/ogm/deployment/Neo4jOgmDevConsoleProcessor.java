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
package io.quarkiverse.neo4j.ogm.deployment;

import java.util.ArrayList;

import io.quarkiverse.neo4j.ogm.runtime.Neo4jOgmDevConsoleRecorder;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
import io.quarkus.devconsole.spi.DevConsoleRuntimeTemplateInfoBuildItem;

/**
 * @author Michael J. Simons
 */
public class Neo4jOgmDevConsoleProcessor {

    @BuildStep(onlyIf = IsDevelopment.class)
    @Record(ExecutionTime.RUNTIME_INIT)
    @SuppressWarnings("unused")
    public DevConsoleRuntimeTemplateInfoBuildItem addAnnotadedClasses(
            AnnotatedClassesBuildItem annotatedClassesBuildItem,
            CurateOutcomeBuildItem curateOutcomeBuildItem,
            Neo4jOgmDevConsoleRecorder recorder) {

        var supplier = recorder.recordAnnotatedClasses(new ArrayList<>(annotatedClassesBuildItem.getEntityClasses()));
        return new DevConsoleRuntimeTemplateInfoBuildItem("domainclasses",
                supplier, this.getClass(),
                curateOutcomeBuildItem);
    }
}