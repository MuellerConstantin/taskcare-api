package de.x1c1b.taskcare.service.core.common.application;

/**
 * Allows encryption of sensitive data. A unique hash is generated from which the
 * original message cannot be inferred.
 */
@FunctionalInterface
public interface SecretEncoder {

    String encodeSecret(String secret);
}
