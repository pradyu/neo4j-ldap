package com.vmware.horizon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@ComponentScan
@ImportResource(value = {"classpath:/ldapcontext.xml"})
@Import(value = {Neo4JConfig.class})
@EnableAutoConfiguration
public class ApplicationConfig {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationConfig.class, args);
    }
}
