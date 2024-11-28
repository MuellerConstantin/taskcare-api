package de.mueller_constantin.taskcare.api.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    /**
     * Regular expression to validate a password.
     *
     * <ol>
     *     <li>A password must consist of at least 6 characters.</li>
     *     <li>A password must contain at least one lowercase and one uppercase letter.</li>
     *     <li>A password must contain at least one number.</li>
     *     <li>A password must consist of maximum 100 characters.</li>
     * </ol>
     */
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{6,100}$";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if (null == password) {
            return true;
        }

        Pattern pattern = Pattern.compile(PASSWORD_REGEX);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
