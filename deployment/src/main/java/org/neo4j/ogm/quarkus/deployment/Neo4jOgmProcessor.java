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

import org.neo4j.ogm.quarkus.runtime.Neo4jOgmBuiltTimeProperties;
import org.neo4j.ogm.quarkus.runtime.Neo4jOgmProperties;
import org.neo4j.ogm.quarkus.runtime.Neo4jOgmRecorder;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.neo4j.deployment.Neo4jDriverBuildItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.neo4j.ogm.session.SessionFactory;

/**
 * Provides all build steps for creating a valid Neo4j-OGM {@link SessionFactory}.
 * @author Michael J. Simons
 */
public class Neo4jOgmProcessor {

	private static final String FEATURE_NAME = "neo4j-ogm";

	@BuildStep
	FeatureBuildItem feature() {
		return new FeatureBuildItem(FEATURE_NAME);
	}

	@BuildStep
	EntitiesBuildItem findAnnotatedClasses(CombinedIndexBuildItem indexBuildItem,
		Neo4jOgmBuiltTimeProperties buildTimeProperties)
		throws ClassNotFoundException {

		var classes = new TreeSet<Class<?>>(Comparator.comparing(Class::getName));
		var ccl = Thread.currentThread().getContextClassLoader();

		Predicate<DotName> packageFilter = buildTimeProperties.basePackages
			.map(packages -> (Predicate<DotName>) (DotName n) -> packages.contains(n.packagePrefix()))
			.orElseGet(() -> (DotName n) -> true);

		var index = indexBuildItem.getIndex();

		var nodeEntity = DotName.createSimple("org.neo4j.ogm.annotation.NodeEntity");
		classes.addAll(load(packageFilter, ccl, index.getAnnotations(nodeEntity).stream().map(ai -> ai.target().asClass()).collect(Collectors.toList())));

		var relationshipEntity = DotName.createSimple("org.neo4j.ogm.annotation.RelationshipEntity");
		classes.addAll(load(packageFilter, ccl, index.getAnnotations(relationshipEntity).stream().map(ai -> ai.target().asClass()).collect(Collectors.toList())));

		return new EntitiesBuildItem(classes);
	}

	private Collection<Class<?>> load(Predicate<DotName> filter, ClassLoader classLoader, Collection<ClassInfo> candidates)
		throws ClassNotFoundException {

		var result  = new ArrayList<Class<?>>(candidates.size());
		for (var classInfo : candidates) {
			if (filter.test(classInfo.name())) {
				result.add(classLoader.loadClass(classInfo.name().toString()));
			}
		}
		return result;
	}

	@BuildStep
	ReflectiveClassBuildItem registerAnnotatedClassesForReflection(EntitiesBuildItem entitiesBuildItem) {

		return ReflectiveClassBuildItem.builder(entitiesBuildItem.getValue().toArray(new Class<?>[0]))
			.constructors()
			.methods()
			.fields()
			.build();
	}

	@BuildStep
	ReflectiveClassBuildItem registerNativeTypes() throws ClassNotFoundException {

		var typeSystem = Class.forName("org.neo4j.ogm.drivers.bolt.types.BoltNativeTypes", false, Thread.currentThread().getContextClassLoader());
		return ReflectiveClassBuildItem.builder(typeSystem)
			.constructors()
			.build();
	}

	@BuildStep
	void createOGMIndex(EntitiesBuildItem entitiesBuildItem,
		BuildProducer<GeneratedResourceBuildItem> resourceProducer) {

		entitiesBuildItem.getValue().stream()
			.collect(Collectors.groupingBy(Class::getPackageName))
			.forEach((p, cl) -> {
				try (var os = new ByteArrayOutputStream()) {
					try (var w = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
						for (Class<?> implName : cl) {
							w.write(implName.getName());
							w.write(System.lineSeparator());
						}
						w.flush();
					}
					resourceProducer.produce(new GeneratedResourceBuildItem(packageAsIndexEntry(p), os.toByteArray()));

				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			});
	}

	@BuildStep
	NativeImageResourceBuildItem addIndizesToNativeImage(EntitiesBuildItem entitiesBuildItem) {

		var allGeneratedResources = entitiesBuildItem.getValue()
			.stream()
			.map(Class::getPackageName)
			.distinct()
			.map(Neo4jOgmProcessor::packageAsIndexEntry)
			.collect(Collectors.toList());

		return new NativeImageResourceBuildItem(allGeneratedResources);
	}

	static String packageAsIndexEntry(String p) {
		return "META-INF/resources/" + p.replace(".", "/") + "/neo4j-ogm.index";
	}

	@BuildStep
	@Record(ExecutionTime.RUNTIME_INIT)
	Neo4jOgmSessionFactoryBuildItem createSessionFactory(Neo4jOgmRecorder recorder,
		Neo4jDriverBuildItem driverBuildItem,
		ShutdownContextBuildItem shutdownContext,
		BuildProducer<SyntheticBeanBuildItem> syntheticBeans,
		Neo4jOgmProperties ogmProperties,
		EntitiesBuildItem allClasses) {

		var allPackages = allClasses.getValue().stream().map(Class::getPackageName)
			.distinct().toArray(String[]::new);
		var sessionFactoryRuntimeValue = recorder
			.initializeSessionFactory(driverBuildItem.getValue(), shutdownContext, ogmProperties, allPackages);

		var beanBuildItem = SyntheticBeanBuildItem.configure(SessionFactory.class)
			.runtimeValue(sessionFactoryRuntimeValue)
			.setRuntimeInit()
			.done();
		syntheticBeans.produce(beanBuildItem);

		return new Neo4jOgmSessionFactoryBuildItem(sessionFactoryRuntimeValue);
	}
}
