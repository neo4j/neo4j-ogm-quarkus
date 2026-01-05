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

import io.quarkus.neo4j.runtime.Neo4jConfiguration;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;

import org.neo4j.driver.Driver;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.drivers.bolt.driver.BoltDriver;
import org.neo4j.ogm.session.SessionFactory;

/**
 * Runtime recorder for all things OGM.
 * @author Michael J. Simons
 */
@Recorder
public class Neo4jOgmRecorder {

	private final RuntimeValue<Neo4jConfiguration> rtvNeo4jConfiguration;
	private final RuntimeValue<Neo4jOgmProperties> rtvOgmProperties;

	public Neo4jOgmRecorder(RuntimeValue<Neo4jConfiguration> neo4jConfiguration, RuntimeValue<Neo4jOgmProperties> ogmProperties) {
		this.rtvNeo4jConfiguration = neo4jConfiguration;
		this.rtvOgmProperties = ogmProperties;
	}

	/**
	 * Initializes a custom Neo4j-OGM session factory
	 *
	 * @param driverRuntimeValue The required java driver
	 * @param shutdownContext    Needed to close it
	 * @param allPackages        the list of packages already discovered
	 * @return A session factory
	 */
	public RuntimeValue<SessionFactory> initializeSessionFactory(
		RuntimeValue<Driver> driverRuntimeValue,
		ShutdownContext shutdownContext,
		String[] allPackages) {

		var neo4jConfiguration = this.rtvNeo4jConfiguration.getValue();
		var ogmProperties = this.rtvOgmProperties.getValue();

		var builder = new Configuration.Builder()
			// Actually not needed for the driver to work, but required for the config not to stumble upon null
			.uri(neo4jConfiguration.uri());

		ogmProperties.database().ifPresent(builder::database);
		if (ogmProperties.useNativeTypes()) {
			builder.useNativeTypes();
		}
		if (ogmProperties.useStrictQuerying()) {
			builder.strictQuerying();
		}
		builder.withBasePackages(allPackages);

		var driver = createConfigurableDriver(driverRuntimeValue);
		driver.configure(builder.build());
		var sessionFactory = new SessionFactory(driver, allPackages);
		shutdownContext.addLastShutdownTask(sessionFactory::close);
		return new RuntimeValue<>(sessionFactory);
	}

	/**
	 * Creates a configurable driver delegating to a bolt driver instance, that does not close itself on reconfiguring.
	 *
	 * @param driverRuntimeValue the actual java driver (low level connectivity)
	 * @return an OGM driver.
	 */
	private org.neo4j.ogm.driver.Driver createConfigurableDriver(RuntimeValue<Driver> driverRuntimeValue) {
		return new BoltDriver(driverRuntimeValue.getValue()) {


			@Override
			public synchronized void close() {
				// We must prevent the bolt driver from closing the driver bean
			}
		};
	}
}
