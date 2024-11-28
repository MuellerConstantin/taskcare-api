package de.mueller_constantin.taskcare.api.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;

@Target({TYPE_USE, METHOD, FIELD, ANNOTATION_TYPE})
@Documented
@Constraint(validatedBy = EnumeratedValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Enumerated {
    String message() default "{de.mueller_constantin.taskcare.api.infrastructure.validation.Enumerated.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends Enum<?>> enumClass();
}
