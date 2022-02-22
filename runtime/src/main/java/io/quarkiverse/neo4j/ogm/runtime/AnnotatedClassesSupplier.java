package io.quarkiverse.neo4j.ogm.runtime;

import java.util.Collection;
import java.util.function.Supplier;

class AnnotatedClassesSupplier implements Supplier<Collection<Class<?>>> {

    private Collection<Class<?>> value;

    void setValue(Collection<Class<?>> value) {
        this.value = value;
    }

    @Override
    public Collection<Class<?>> get() {
        return this.value;
    }
}
