package de.mueller_constantin.taskcare.api.core.common.application.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE_USE, METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = UUIDValidatorForString.class)
@Documented
public @interface UUID {
    String message() default "{de.mueller_constantin.taskcare.api.core.common.application.validation.UUID.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
