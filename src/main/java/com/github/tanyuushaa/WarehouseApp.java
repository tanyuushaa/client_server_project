package com.github.tanyuushaa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Collections;

@SpringBootApplication
@EnableJpaRepositories("com.github.tanyuushaa.model.rep")
@EntityScan(basePackages = "com.github.tanyuushaa.model")
public class WarehouseApp {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WarehouseApp.class);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", "8080"));
        app.run(args);
    }

}
