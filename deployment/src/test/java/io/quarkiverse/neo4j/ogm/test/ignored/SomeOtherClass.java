package io.quarkiverse.neo4j.ogm.test.ignored;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class SomeOtherClass {

    @Id
    @GeneratedValue
    private Long id;
}
