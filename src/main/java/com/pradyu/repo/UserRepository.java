package com.pradyu.repo;

import com.pradyu.entity.Group;
import com.pradyu.entity.User;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface UserRepository extends GraphRepository<User> {

    Group findByName(String name);
}
