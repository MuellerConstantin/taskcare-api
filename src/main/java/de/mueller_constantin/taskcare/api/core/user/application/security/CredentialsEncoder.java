package de.mueller_constantin.taskcare.api.core.user.application.security;

/**
 * Allows encryption of sensitive data. A unique hash is generated from which the
 * original message cannot be inferred.
 */
@FunctionalInterface
public interface CredentialsEncoder {
    /**
     * Hash sensitive data.
     *
     * @param credential Raw data.
     * @return Hashed data.
     */
    String encode(String credential);
}
