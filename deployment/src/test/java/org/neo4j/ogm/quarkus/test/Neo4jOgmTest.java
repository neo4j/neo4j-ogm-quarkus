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
package org.neo4j.ogm.quarkus.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.neo4j.ogm.quarkus.test.ignored.SomeOtherClass;
import org.neo4j.ogm.quarkus.test.domain.SomeClass;
import io.quarkus.test.QuarkusUnitTest;

import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.neo4j.ogm.driver.Driver;
import org.neo4j.ogm.session.SessionFactory;

/**
 * @author Michael J. Simons
 */
class Neo4jOgmTest {

	@RegisterExtension
	static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
		.setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
			.addClass(SomeClass.class)
			.addClass(SomeOtherClass.class));

	@Inject
	SessionFactory sessionFactory;

	@Test
	void assertSessionFactoryIsPresentAndAwareOfClasses() {
		assertNotNull(sessionFactory.metaData().classInfo("SomeClass"));
		assertNotNull(sessionFactory.metaData().classInfo("SomeOtherClass"));
	}

	@Test
	void configShouldBeDefault() {

		var configuration = sessionFactory.unwrap(Driver.class).getConfiguration();
		assertFalse(configuration.getUseNativeTypes());
		assertTrue(configuration.getUseStrictQuerying());
		assertNull(configuration.getDatabase());
	}
}
