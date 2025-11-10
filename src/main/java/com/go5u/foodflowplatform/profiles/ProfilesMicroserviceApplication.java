package com.go5u.foodflowplatform.profiles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = "com.go5u.foodflowplatform.profiles")
public class ProfilesMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProfilesMicroserviceApplication.class, args);
    }

}
