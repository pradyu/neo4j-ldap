package com.pradyu.repo;

import com.pradyu.entity.Entity;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface EntityRepository extends GraphRepository<Entity> {

    Entity findByName(String name);

    Iterable<Entity> findByType(Entity.Type type);

    Entity findByCanonicalName(String canonicalName);

}
