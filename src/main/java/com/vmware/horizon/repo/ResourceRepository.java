package com.vmware.horizon.repo;

import com.vmware.horizon.entity.Resource;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface ResourceRepository extends GraphRepository<Resource> {

    Resource findByName(String name);

    Resource findByCanonicalName(String canonicalName);

}
