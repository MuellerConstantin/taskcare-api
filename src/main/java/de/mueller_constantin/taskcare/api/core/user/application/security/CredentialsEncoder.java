package de.mueller_constantin.taskcare.api.core.user.application.security;

/**
 * Allows encryption of sensitive data. A unique hash is generated from which the
 * original message cannot be inferred.
 */
public interface CredentialsEncoder {
    /**
     * Hash sensitive data.
     *
     * @param credential Raw data.
     * @return Hashed data.
     */
    String encode(String credential);

    /**
     * Checks if the original message can be inferred from the hash.
     *
     * @param credential The raw data.
     * @param encodedCredential The hashed data.
     * @return Returns true if the raw data matches the hashed data.
     */
    boolean matches(String credential, String encodedCredential);
}
