package com.BackEndTeam1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class BackEndTeam1Application {

    public static void main(String[] args) {
        SpringApplication.run(BackEndTeam1Application.class, args);
    }

}
