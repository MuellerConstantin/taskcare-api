package de.x1c1b.taskcare.api.core.common.application.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE_USE, METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = NullOrNotEmptyValidatorForString.class)
@Documented
public @interface NullOrNotEmpty {

    String message() default "{de.x1c1b.taskcare.service.core.common.application.validation.NullOrNotEmpty.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
