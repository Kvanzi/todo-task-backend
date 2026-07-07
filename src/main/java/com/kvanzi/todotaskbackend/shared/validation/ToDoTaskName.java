package com.kvanzi.todotaskbackend.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Size;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ReportAsSingleViolation
@Constraint(validatedBy = {})
@Size(min = 1, max = 255)
public @interface ToDoTaskName {
    String message() default "{com.kvanzi.todotaskbackend.shared.validation.ToDoTaskName.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
