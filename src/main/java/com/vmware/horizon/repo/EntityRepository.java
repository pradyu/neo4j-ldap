package com.vmware.horizon.repo;

import com.vmware.horizon.entity.Entity;
import com.vmware.horizon.entity.Group;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface EntityRepository extends GraphRepository<Entity> {

    Entity findByName(String name);

    Iterable<Entity> findByType(Entity.Type type);

    Entity findByCanonicalName(String canonicalName);

}
