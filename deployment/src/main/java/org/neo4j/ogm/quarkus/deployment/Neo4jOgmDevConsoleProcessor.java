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

import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.devui.spi.page.CardPageBuildItem;
import io.quarkus.devui.spi.page.Page;

/**
 * @author Michael J. Simons
 */
public class Neo4jOgmDevConsoleProcessor {

	/**
	 * A record to serialize the entities for {@link #createOgmCard(EntitiesBuildItem)} properly.
	 *
	 * @param packageName The package name of the entity
	 * @param simpleName  The simple class name under that package
	 */
	record EntityDescription(String packageName, String simpleName) {
	}

	@BuildStep(onlyIf = IsDevelopment.class)
	@SuppressWarnings("unused")
	CardPageBuildItem createOgmCard(EntitiesBuildItem entitiesBuildItem) {

		var ogmCard = new CardPageBuildItem();
		var entities = entitiesBuildItem.getValue()
			.stream().map(e -> new EntityDescription(e.getPackageName(), e.getSimpleName())).toList();
		if (!entities.isEmpty()) {
			ogmCard.addBuildTimeData("entities", entities);
			ogmCard.addPage(Page.tableDataPageBuilder("Entities")
				.showColumn("packageName")
				.showColumn("simpleName")
				.buildTimeDataKey("entities")
				.icon("font-awesome-solid:egg")
				.staticLabel(String.valueOf(entities.size()))
			);
		}

		return ogmCard;
	}
}
