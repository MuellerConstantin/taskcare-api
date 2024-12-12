package de.mueller_constantin.taskcare.api.core.common.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullOrNotEmptyValidatorForString implements ConstraintValidator<NullOrNotEmpty, String> {
    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        return null == string || !string.isEmpty();
    }
}
