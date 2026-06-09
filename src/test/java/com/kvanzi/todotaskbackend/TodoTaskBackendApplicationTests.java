package com.kvanzi.todotaskbackend;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class TodoTaskBackendApplicationTests {
    private static final ApplicationModules modules = ApplicationModules.of(TodoTaskBackendApplicationTests.class);

    @Test
    void applicationModulesAreCompliant() {
        modules.verify();
    }
}
