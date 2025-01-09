package de.mueller_constantin.taskcare.api.core.common.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UUIDValidatorForString implements ConstraintValidator<UUID, String> {
    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        if(string == null) {
            return true;
        }

        try {
            java.util.UUID.fromString(string);
            return true;
        } catch (IllegalArgumentException exc) {
            return false;
        }
    }
}
