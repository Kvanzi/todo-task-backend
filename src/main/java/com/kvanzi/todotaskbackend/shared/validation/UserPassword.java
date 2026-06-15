package com.kvanzi.todotaskbackend.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@ReportAsSingleViolation
@Pattern(
    regexp = "^(?=[^A-Z]*+[A-Z])(?=[^a-z]*+[a-z])(?=\\D*+\\d)(?=[^#?!@$%^&*-]*+[#?!@$%^&*-]).{8,72}$"
)
@Documented
@Constraint(validatedBy = {})
public @interface UserPassword {
    String message() default "{com.kvanzi.todotaskbackend.shared.validation.UserPassword.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
