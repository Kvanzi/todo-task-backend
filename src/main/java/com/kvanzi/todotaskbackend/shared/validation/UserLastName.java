package com.kvanzi.todotaskbackend.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Pattern(
    regexp = "^[a-zA-Z\\u00C0-\\u00D6\\u00D8-\\u00F6\\u00F8-\\u01FF]+([ "
        + "\\-']?[a-zA-Z\\u00C0-\\u00D6\\u00D8-\\u00F6\\u00F8-\\u01FF]+){0,2}[.]?$"
)
@Size(min = 1, max = 30)
public @interface UserLastName {
    String message() default "{com.kvanzi.todotaskbackend.shared.validation.UserLastName.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
