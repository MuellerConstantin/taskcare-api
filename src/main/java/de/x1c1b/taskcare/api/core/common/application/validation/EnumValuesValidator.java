package de.x1c1b.taskcare.api.core.common.application.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnumValuesValidator implements ConstraintValidator<EnumValues, String> {

    private String[] validValues;

    @Override
    public void initialize(EnumValues constraintAnnotation) {
        this.validValues = constraintAnnotation.values();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (null == value) {
            return true;
        }

        Pattern pattern = Pattern.compile(String.format("^(%s)$", String.join("|", validValues)));
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
