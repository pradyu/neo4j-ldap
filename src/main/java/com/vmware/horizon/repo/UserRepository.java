package com.vmware.horizon.repo;

import com.vmware.horizon.entity.Group;
import com.vmware.horizon.entity.User;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface UserRepository extends GraphRepository<User> {

    Group findByName(String name);
}
