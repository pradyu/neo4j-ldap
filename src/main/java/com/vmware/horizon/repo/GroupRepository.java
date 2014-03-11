package com.vmware.horizon.repo;

import com.vmware.horizon.entity.Group;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface GroupRepository extends GraphRepository<Group> {

    Group findByName(String name);

    Group findByCanonicalName(String canonicalName);

}
