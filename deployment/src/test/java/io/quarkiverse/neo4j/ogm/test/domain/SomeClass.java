package io.quarkiverse.neo4j.ogm.test.domain;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class SomeClass {

    @Id
    @GeneratedValue
    private Long id;
}
