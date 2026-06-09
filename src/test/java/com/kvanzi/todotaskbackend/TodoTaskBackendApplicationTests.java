package com.kvanzi.todotaskbackend;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class TodoTaskBackendApplicationTests {
    private static final ApplicationModules modules = ApplicationModules.of(TodoTaskBackendApplication.class);

    @Test
    void applicationModulesAreCompliant() {
        modules.verify();
    }
}
