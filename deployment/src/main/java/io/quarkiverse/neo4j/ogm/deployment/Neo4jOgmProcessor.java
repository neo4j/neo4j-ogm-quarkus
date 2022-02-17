package io.quarkiverse.neo4j.ogm.deployment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.jboss.jandex.DotName;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

class Neo4jOgmProcessor {

    private static final String FEATURE_NAME = "neo4j-ogm";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE_NAME);
    }

    @BuildStep
    @SuppressWarnings("unused")
    AnnotatedClassesBuildItem createDiscoverer(CombinedIndexBuildItem combinedIndexBuildItem)
            throws ClassNotFoundException {

        var classes = new HashSet<Class<?>>();
        var ccl = Thread.currentThread().getContextClassLoader();

        var nodeEntity = DotName.createSimple("org.neo4j.ogm.annotation.NodeEntity");
        for (var annotation : combinedIndexBuildItem.getIndex().getAnnotations(nodeEntity)) {
            classes.add(ccl.loadClass(annotation.target().asClass().name().toString()));
        }

        var relationshipEntity = DotName.createSimple("org.neo4j.ogm.annotation.RelationshipEntity");
        for (var annotation : combinedIndexBuildItem.getIndex().getAnnotations(relationshipEntity)) {
            classes.add(ccl.loadClass(annotation.target().asClass().name().toString()));
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
    void createOGMINdex(AnnotatedClassesBuildItem annotatedClassesBuildItem,
            BuildProducer<GeneratedResourceBuildItem> resourceProducer) {
        System.out.println("found classes");
        annotatedClassesBuildItem.getEntityClasses().forEach(c -> System.out.println(c.getName()));

        annotatedClassesBuildItem.getEntityClasses().stream().collect(Collectors.groupingBy(c -> c.getPackageName()))
                .forEach((p, cl) -> {
                    try {

                        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                            try (OutputStreamWriter w = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
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
                        }
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
        annotatedClassesBuildItem.getEntityClasses().forEach(c -> {

        });
    }
}
