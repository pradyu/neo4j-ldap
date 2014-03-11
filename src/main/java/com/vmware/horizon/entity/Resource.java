package com.vmware.horizon.entity;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Resource extends Entity {

    @RelatedTo(type="ENTITLED", elementClass = Entity.class, direction = Direction.OUTGOING)
    public @Fetch
    Set<Entity> entitlements;

    public Resource() { }

    public Resource(String name) {
       super(Type.RESOURCE, name, name);
    }


    public Set<Entity> getEntitlements() {
        return entitlements;
    }

    public void setEntitlements(Set<Entity> entitlements) {
        this.entitlements = entitlements;
    }

    public void addEntitlement(Entity entity) {
        if (entitlements == null) {
            entitlements = new HashSet<Entity>();
        }
        entitlements.add(entity);
    }


}
