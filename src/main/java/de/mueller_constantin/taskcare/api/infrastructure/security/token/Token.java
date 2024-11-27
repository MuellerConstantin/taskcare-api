package de.mueller_constantin.taskcare.api.infrastructure.security.token;

public interface Token {
    String getRawToken();

    long getExpiresIn();

    String getPrincipal();
}
