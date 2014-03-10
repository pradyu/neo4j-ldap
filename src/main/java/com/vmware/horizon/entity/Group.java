package com.vmware.horizon.entity;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.RelationshipType;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

@NodeEntity
public class Group extends Entity {

    public Group() {

    }

    public Group(String name) {
        super(Type.GROUP, name, name);
    }

    public Group(String canonicalName, String name) {
        super(Type.GROUP, name, canonicalName);
    }

    @RelatedTo(type="MEMBER", elementClass = Entity.class, direction = Direction.OUTGOING)
    public @Fetch Set<Entity> members;

    public Set<Entity> getMembers() {
        return members;
    }

    public void addMember(Entity entity) {
        if (members == null) {
            members = new HashSet<Entity>();
        }
        members.add(entity);
    }

    public String toString() {
        String results = name + "'s members include\n";
        if (members != null) {
            for (Entity entity : members) {
                results += "\t- " + entity.name + "\n";
            }
        }
        return results;
    }

    enum Rels implements RelationshipType {
        MEMBER
    }
}
