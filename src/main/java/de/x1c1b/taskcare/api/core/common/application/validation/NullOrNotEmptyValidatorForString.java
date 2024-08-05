package de.x1c1b.taskcare.api.core.common.application.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NullOrNotEmptyValidatorForString implements ConstraintValidator<NullOrNotEmpty, String> {
    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        return null == string || !string.isEmpty();
    }
}
