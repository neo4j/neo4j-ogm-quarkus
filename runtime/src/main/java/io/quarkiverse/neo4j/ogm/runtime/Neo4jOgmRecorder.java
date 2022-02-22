package io.quarkiverse.neo4j.ogm.runtime;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.neo4j.driver.Driver;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.driver.AbstractConfigurableDriver;
import org.neo4j.ogm.drivers.bolt.driver.BoltDriver;
import org.neo4j.ogm.request.Request;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.transaction.Transaction;
import org.neo4j.ogm.transaction.TransactionManager;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class Neo4jOgmRecorder {

    public RuntimeValue<SessionFactory> initializeSessionFactory(
            RuntimeValue<Driver> driverRuntimeValue,
            ShutdownContext shutdownContext,
            Neo4jOgmProperties ogmProperties, String[] allPackages) {

        var builder = new Configuration.Builder();

        ogmProperties.database.ifPresent(builder::database);
        if (ogmProperties.useNativeTypes) {
            builder.useNativeTypes();
        }
        if (ogmProperties.useStrictQuerying) {
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
        var delegate = new BoltDriver(driverRuntimeValue.getValue());
        return new AbstractConfigurableDriver() {
            @Override
            protected String getTypeSystemName() {
                return "org.neo4j.ogm.drivers.bolt.types.BoltNativeTypes";
            }

            @Override
            public Function<TransactionManager, BiFunction<Transaction.Type, Iterable<String>, Transaction>> getTransactionFactorySupplier() {
                return delegate.getTransactionFactorySupplier();
            }

            @Override
            public void close() {
                delegate.close();
            }

            @Override
            public Request request(Transaction transaction) {
                return delegate.request(transaction);
            }
        };
    }
}
