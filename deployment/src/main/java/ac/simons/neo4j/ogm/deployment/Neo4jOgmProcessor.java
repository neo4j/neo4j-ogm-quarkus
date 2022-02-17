/*
 * Copyright 2022 the original author or authors.
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
package ac.simons.neo4j.ogm.deployment;

import ac.simons.neo4j.ogm.runtime.Neo4jOgmBuiltTimeProperties;
import ac.simons.neo4j.ogm.runtime.Neo4jOgmProperties;
import ac.simons.neo4j.ogm.runtime.Neo4jOgmRecorder;
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
import java.util.Comparator;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
	@SuppressWarnings("unused")
	AnnotatedClassesBuildItem findAnnotatedClasses(CombinedIndexBuildItem combinedIndexBuildItem,
		Neo4jOgmBuiltTimeProperties buildTimeProperties)
		throws ClassNotFoundException {

		var classes = new TreeSet<Class<?>>(Comparator.comparing(Class::getName));
		var ccl = Thread.currentThread().getContextClassLoader();

		Predicate<DotName> packageFilter = buildTimeProperties.basePackages
			.map(packages -> (Predicate<DotName>) (DotName n) -> packages.contains(n.packagePrefix()))
			.orElseGet(() -> (DotName n) -> true);

		var nodeEntity = DotName.createSimple("org.neo4j.ogm.annotation.NodeEntity");
		for (var annotation : combinedIndexBuildItem.getIndex().getAnnotations(nodeEntity)) {
			var classInfo = annotation.target().asClass();
			if (packageFilter.test(classInfo.name())) {
				classes.add(ccl.loadClass(classInfo.name().toString()));
			}
		}

		var relationshipEntity = DotName.createSimple("org.neo4j.ogm.annotation.RelationshipEntity");
		for (var annotation : combinedIndexBuildItem.getIndex().getAnnotations(relationshipEntity)) {
			var classInfo = annotation.target().asClass();
			if (packageFilter.test(classInfo.name())) {
				classes.add(ccl.loadClass(classInfo.name().toString()));
			}
		}

		return new AnnotatedClassesBuildItem(classes);
	}

	@BuildStep
	ReflectiveClassBuildItem registerAnnotatedClassesForReflection(
		AnnotatedClassesBuildItem annotatedClassesBuildItem) {

		return new ReflectiveClassBuildItem(true, true, true,
			annotatedClassesBuildItem.getEntityClasses().toArray(new Class<?>[0]));
	}

	@BuildStep
	ReflectiveClassBuildItem registerNativeTypes() throws ClassNotFoundException {

		var typeSystem = Class.forName("org.neo4j.ogm.drivers.bolt.types.BoltNativeTypes", false, Thread.currentThread().getContextClassLoader());
		return new ReflectiveClassBuildItem(true, false, false, typeSystem);
	}

	@BuildStep
	void createOGMIndex(AnnotatedClassesBuildItem annotatedClassesBuildItem,
		BuildProducer<GeneratedResourceBuildItem> resourceProducer) {

		annotatedClassesBuildItem.getEntityClasses().stream()
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
					resourceProducer.produce(
						new GeneratedResourceBuildItem(
							"META-INF/resources/" + p.replace(".", "/") + "/neo4j-ogm.index",
							os.toByteArray()));

				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			});
	}

	@BuildStep
	NativeImageResourceBuildItem addIndizesToNativeImage(AnnotatedClassesBuildItem annotatedClassesBuildItem) {

		var allGeneratedResources = annotatedClassesBuildItem.getEntityClasses()
			.stream()
			.map(Class::getPackageName)
			.distinct()
			.map(p -> "META-INF/resources/" + p.replace(".", "/") + "/neo4j-ogm.index")
			.collect(Collectors.toList());

		return new NativeImageResourceBuildItem(allGeneratedResources);
	}

	@BuildStep
	@Record(ExecutionTime.RUNTIME_INIT)
	Neo4jOgmSessionFactoryBuildItem createSessionFactory(Neo4jOgmRecorder recorder,
		Neo4jDriverBuildItem driverBuildItem,
		ShutdownContextBuildItem shutdownContext,
		BuildProducer<SyntheticBeanBuildItem> syntheticBeans,
		Neo4jOgmProperties ogmProperties,
		AnnotatedClassesBuildItem allClasses) {

		var allPackages = allClasses.getEntityClasses().stream().map(Class::getPackageName)
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
