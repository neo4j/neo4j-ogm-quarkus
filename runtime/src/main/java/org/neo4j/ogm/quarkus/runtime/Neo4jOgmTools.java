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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import io.quarkus.runtime.annotations.JsonRpcDescription;
import jakarta.inject.Inject;
import org.neo4j.ogm.config.ObjectMapperFactory;
import org.neo4j.ogm.cypher.query.Pagination;
import org.neo4j.ogm.session.SessionFactory;

/**
 * A set of Neo4j-OGM tools, which can be enabled for the MCP Dev server.
 */
public final class Neo4jOgmTools {

	private static final TypeReference<List<Map<String, Object>>> TYPE_REF = new TypeReference<>() {
	};

	@Inject
	SessionFactory sessionFactory;

	/**
	 * Default constructor needed for Jakarta based injection.
	 */
	public Neo4jOgmTools() {
	}

	/**
	 * Finds OGM instances for the given label. Only a limited amount of entities is returned by default. That number can be configured.
	 * @param label the label of the entity to find
	 * @param limit the number of instances returned
	 * @param offset the offset from which instances should be returned, can be {@literal null}
	 * @return a list of JSON mappings created through OGMs ObjectMapper
	 */
	@JsonRpcDescription("Finds OGM instances for the given label. Only a limited amount of entities is returned by default. That number can be configured.")
	public  List<Map<String, Object>> findAll(
			@JsonRpcDescription("The label for which the OGM entity should be find. Must either be the label as present in the graph or the simple class name onto which the label is mapped.") String label,
			@JsonRpcDescription("The amount of entities to return. Set this value to -1 to return all entities. The value must not be 0.")
			Integer limit,
			@JsonRpcDescription("Set this parameter to a value equal or great then zero to define the offset from which on entities should be returned")
			Integer offset
	) {
		var classInfo = sessionFactory.metaData().classInfo(label);
		if (classInfo == null) {
			return List.of();
		}

		var session = sessionFactory.openSession();
		try {
			Collection<?> result;
			if (limit < 0) {
				result = session.loadAll(classInfo.getUnderlyingClass());
			} else {
				var pagination = new Pagination(0, limit);
				pagination.setOffset((offset != null) ? offset : 0);
				result = session.loadAll(classInfo.getUnderlyingClass(), pagination);
			}
			return ObjectMapperFactory.objectMapper().convertValue(result, TYPE_REF);
		} finally {
			session.clear();
		}
	}
}
