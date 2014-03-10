package com.vmware.horizon;

import java.io.File;


import com.vmware.horizon.repo.GroupRepository;
import org.neo4j.graphdb.GraphDatabaseService;

import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.data.neo4j.support.Neo4jTemplate;

@Configuration
@EnableNeo4jRepositories
public class Neo4JConfig extends Neo4jConfiguration {

    @Autowired Neo4jTemplate neo4jTemplate;

    @Bean(destroyMethod = "shutdown")
    GraphDatabaseService graphDatabaseService() {
        return new GraphDatabaseFactory().newEmbeddedDatabase("accessingdataneo4j.db");
    }

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    GraphDatabase graphDatabase;

}
