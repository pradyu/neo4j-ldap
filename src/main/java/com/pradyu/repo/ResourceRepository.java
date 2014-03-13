package com.pradyu.repo;

import com.pradyu.entity.Resource;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface ResourceRepository extends GraphRepository<Resource> {

    Resource findByName(String name);

    Resource findByCanonicalName(String canonicalName);

}
