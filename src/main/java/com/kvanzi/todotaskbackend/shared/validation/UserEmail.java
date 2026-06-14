package com.kvanzi.todotaskbackend.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
@Email
@Size(min = 5, max = 254)
public @interface UserEmail {
    String message() default "{com.kvanzi.todotaskbackend.shared.validation.UserEmail.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
