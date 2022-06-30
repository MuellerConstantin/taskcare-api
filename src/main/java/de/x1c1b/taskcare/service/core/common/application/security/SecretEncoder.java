package de.x1c1b.taskcare.service.core.common.application.security;

/**
 * Allows encryption of sensitive data. A unique hash is generated from which the
 * original message cannot be inferred.
 */
@FunctionalInterface
public interface SecretEncoder {

    /**
     * Encrypts sensitive data.
     *
     * @param secret Raw data.
     * @return Encrypted data.
     */
    String encodeSecret(String secret);
}
