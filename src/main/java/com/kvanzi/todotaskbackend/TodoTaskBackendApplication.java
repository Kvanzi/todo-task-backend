package com.kvanzi.todotaskbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.modulith.Modulith;

@Modulith
@EnableJpaAuditing
public class TodoTaskBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(TodoTaskBackendApplication.class, args);
    }
}
