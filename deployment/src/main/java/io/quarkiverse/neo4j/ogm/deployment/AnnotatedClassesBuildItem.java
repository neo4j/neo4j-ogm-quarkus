package io.quarkiverse.neo4j.ogm.deployment;

import java.util.Collection;
import java.util.Set;

import io.quarkus.builder.item.SimpleBuildItem;

final class AnnotatedClassesBuildItem extends SimpleBuildItem {

    private final Set<Class<?>> classes;

    AnnotatedClassesBuildItem(Set<Class<?>> classes) {
        this.classes = classes;
    }

    Collection<Class<?>> getEntityClasses() {
        return this.classes;
    }
}
