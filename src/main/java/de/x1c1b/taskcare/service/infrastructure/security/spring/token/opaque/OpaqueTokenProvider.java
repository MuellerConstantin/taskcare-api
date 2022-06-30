package de.x1c1b.taskcare.service.infrastructure.security.spring.token.opaque;

import de.x1c1b.taskcare.service.infrastructure.security.spring.token.InvalidTokenException;
import de.x1c1b.taskcare.service.infrastructure.security.spring.token.RefreshToken;
import de.x1c1b.taskcare.service.infrastructure.security.spring.token.TokenProperties;
import de.x1c1b.taskcare.service.infrastructure.security.spring.token.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Component
public class OpaqueTokenProvider implements TokenProvider<RefreshToken> {

    private final TokenProperties tokenProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public OpaqueTokenProvider(TokenProperties tokenProperties, RedisTemplate<String, Object> redisTemplate) {
        this.tokenProperties = tokenProperties;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public RefreshToken generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        SecureRandom secureRandom = new SecureRandom();
        byte[] secret = new byte[tokenProperties.getRefresh().getLength()];
        secureRandom.nextBytes(secret);

        String rawToken = Base64.getEncoder().encodeToString(secret);
        String storageKey = String.format("refreshToken:%s", rawToken);

        redisTemplate.opsForValue().set(storageKey, user.getUsername(),
                tokenProperties.getRefresh().getExpiresIn(), TimeUnit.MILLISECONDS);

        return RefreshToken.builder()
                .rawToken(rawToken)
                .expiresIn(tokenProperties.getRefresh().getExpiresIn())
                .principal(user.getUsername())
                .build();
    }

    @Override
    public RefreshToken validateToken(String rawToken) throws InvalidTokenException {
        String storageKey = String.format("refreshToken:%s", rawToken);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(storageKey))) {
            String principal = (String) redisTemplate.opsForValue().get(storageKey);
            long expiresIn = redisTemplate.getExpire(storageKey) * 1000;

            return RefreshToken.builder()
                    .rawToken(rawToken)
                    .expiresIn(expiresIn)
                    .principal(principal)
                    .build();
        } else {
            throw new InvalidTokenException("Invalid token");
        }
    }
}
