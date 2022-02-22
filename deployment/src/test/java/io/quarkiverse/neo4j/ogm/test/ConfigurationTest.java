package io.quarkiverse.neo4j.ogm.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.neo4j.ogm.driver.Driver;
import org.neo4j.ogm.session.SessionFactory;

import io.quarkiverse.neo4j.ogm.test.domain.SomeClass;
import io.quarkiverse.neo4j.ogm.test.ignored.SomeOtherClass;
import io.quarkus.test.QuarkusUnitTest;

class ConfigurationTest {

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(SomeClass.class)
                    .addClass(SomeOtherClass.class))
            .overrideConfigKey("org.neo4j.ogm.base-packages", "io.quarkiverse.neo4j.ogm.test.domain")
            .overrideConfigKey("org.neo4j.ogm.use-native-types", "true")
            .overrideConfigKey("org.neo4j.ogm.use-strict-querying", "true")
            .overrideConfigKey("org.neo4j.ogm.database", "aDatabase");

    @Inject
    SessionFactory sessionFactory;

    @Test
    void assertSessionFactoryIsPresentAndAwareOfClasses() {

        assertNotNull(sessionFactory.metaData().classInfo("SomeClass"));
        assertNull(sessionFactory.metaData().classInfo("SomeOtherClass"));
    }

    @Test
    void configShouldBeApplied() {

        var configuration = sessionFactory.unwrap(Driver.class).getConfiguration();
        assertTrue(configuration.getUseNativeTypes());
        assertTrue(configuration.getUseStrictQuerying());
        assertEquals("aDatabase", configuration.getDatabase());
    }
}
