package de.x1c1b.taskcare.service.infrastructure.security.spring.token;

public interface Token {

    String getRawToken();

    long getExpiresIn();

    String getPrincipal();
}
