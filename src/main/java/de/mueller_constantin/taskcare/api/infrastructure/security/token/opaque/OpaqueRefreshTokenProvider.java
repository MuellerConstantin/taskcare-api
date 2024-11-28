package de.mueller_constantin.taskcare.api.infrastructure.security.token.opaque;

import de.mueller_constantin.taskcare.api.infrastructure.security.Principal;
import de.mueller_constantin.taskcare.api.infrastructure.security.token.RefreshToken;
import de.mueller_constantin.taskcare.api.infrastructure.security.token.InvalidTokenException;
import de.mueller_constantin.taskcare.api.infrastructure.security.token.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Component
@Primary
public class OpaqueRefreshTokenProvider implements TokenProvider<RefreshToken> {
    private final OpaqueRefreshTokenProperties tokenProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public OpaqueRefreshTokenProvider(OpaqueRefreshTokenProperties tokenProperties, RedisTemplate<String, Object> redisTemplate) {
        this.tokenProperties = tokenProperties;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public RefreshToken generateToken(Authentication authentication) {
        Principal user = (Principal) authentication.getPrincipal();

        SecureRandom secureRandom = new SecureRandom();
        byte[] secret = new byte[tokenProperties.getLength()];
        secureRandom.nextBytes(secret);

        String rawToken = Base64.getEncoder().encodeToString(secret);
        String storageKey = String.format("RefreshToken:%s", rawToken);

        redisTemplate.opsForValue().set(storageKey, user.getUsername(),
                tokenProperties.getExpiresIn(), TimeUnit.MILLISECONDS);

        return RefreshToken.builder()
                .rawToken(rawToken)
                .expiresIn(tokenProperties.getExpiresIn())
                .principal(user.getUsername())
                .build();
    }

    @Override
    public RefreshToken validateToken(String rawToken) throws InvalidTokenException {
        String storageKey = String.format("RefreshToken:%s", rawToken);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(storageKey))) {
            String principal = (String) redisTemplate.opsForValue().get(storageKey);
            long expiresIn = redisTemplate.getExpire(storageKey, TimeUnit.MILLISECONDS);

            return RefreshToken.builder()
                    .rawToken(rawToken)
                    .expiresIn(expiresIn)
                    .principal(principal)
                    .build();
        } else {
            throw new InvalidTokenException("Invalid token");
        }
    }

    @Override
    public void invalidateToken(String rawToken) {
        String storageKey = String.format("RefreshToken:%s", rawToken);
        redisTemplate.delete(storageKey);
    }
}
