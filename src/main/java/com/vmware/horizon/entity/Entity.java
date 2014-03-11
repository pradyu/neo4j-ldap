package com.vmware.horizon.entity;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity(partial = true)
public class Entity {

    @GraphId
    Long id;

    public Type type;

    public String name;

    @Indexed(unique=true)
    public String canonicalName;

    public Entity() {
    }

    public Entity(Type type) {
        this.type = type;
    }

    public Entity(Type type, String name) {
        this.name = name;
    }

    public Entity(Type type, String name, String canonicalName) {
        this.type = type;
        this.name = name;
        this.canonicalName = canonicalName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public enum Type {
        USER, GROUP, RESOURCE
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }
}
