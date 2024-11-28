package de.mueller_constantin.taskcare.api.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;

import java.util.Arrays;
import java.util.stream.Collectors;

public class EnumeratedValidator implements ConstraintValidator<Enumerated, String> {
    private Class<? extends Enum<?>> enumClass;
    private String allowedValues;

    @Override
    public void initialize(Enumerated constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
        this.allowedValues = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean isValid = Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(enumConstant -> enumConstant.name().equals(value));

        if (!isValid) {
            ((ConstraintValidatorContextImpl) context)
                    .addMessageParameter("0", allowedValues);
        }

        return isValid;
    }
}
