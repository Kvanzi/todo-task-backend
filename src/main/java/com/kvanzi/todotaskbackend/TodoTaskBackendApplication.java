package com.kvanzi.todotaskbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.modulith.Modulith;
import org.springframework.scheduling.annotation.EnableScheduling;

@Modulith
@EnableJpaAuditing
@EnableScheduling
public class TodoTaskBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(TodoTaskBackendApplication.class, args);
    }
}
