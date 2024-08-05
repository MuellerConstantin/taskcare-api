package de.x1c1b.taskcare.api.core.common.application.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsernameValidator implements ConstraintValidator<Username, String> {

    /**
     * Regular expression to validate a username.
     *
     * <ol>
     *     <li>A username must be between 5 and 15 characters long.</li>
     *     <li>A username may consist of uppercase and lowercase letters, numbers and the characters _ and -.</li>
     *     <li>A username must begin with a letter.</li>
     *     <li>A username must end with a letter or a number.</li>
     * </ol>
     */
    public static final String USERNAME_REGEX = "^[a-zA-Z]([_\\-a-zA-Z0-9]){3,13}[a-zA-Z0-9]$";

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        if (null == username) {
            return true;
        }

        Pattern pattern = Pattern.compile(USERNAME_REGEX);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }
}
