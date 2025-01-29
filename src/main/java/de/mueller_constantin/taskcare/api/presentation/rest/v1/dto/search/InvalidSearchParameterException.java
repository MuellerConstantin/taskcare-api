package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.search;

import lombok.Getter;

/**
 * Thrown if a parameter used in a search filter is invalid.
 */
@Getter
public class InvalidSearchParameterException extends RuntimeException {
    private final String parameterName;

    public InvalidSearchParameterException(String parameterName) {
        this.parameterName = parameterName;
    }

    public InvalidSearchParameterException(String message, String parameterName) {
        super(message);
        this.parameterName = parameterName;
    }

    public InvalidSearchParameterException(String message, Throwable cause, String parameterName) {
        super(message, cause);
        this.parameterName = parameterName;
    }

    public InvalidSearchParameterException(Throwable cause, String parameterName) {
        super(cause);
        this.parameterName = parameterName;
    }

    public InvalidSearchParameterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String parameterName) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.parameterName = parameterName;
    }
}
