package de.mueller_constantin.taskcare.api.infrastructure.security.token.jwt;

import de.mueller_constantin.taskcare.api.infrastructure.security.Principal;
import de.mueller_constantin.taskcare.api.infrastructure.security.token.AccessToken;
import de.mueller_constantin.taskcare.api.infrastructure.security.token.InvalidTokenException;
import de.mueller_constantin.taskcare.api.infrastructure.security.token.TokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Primary
public class JwtAccessTokenProvider implements TokenProvider<AccessToken> {
    private final JwtAccessTokenProperties jwtAccessTokenProperties;

    @Autowired
    public JwtAccessTokenProvider(JwtAccessTokenProperties jwtAccessTokenProperties) {
        this.jwtAccessTokenProperties = jwtAccessTokenProperties;
    }

    @Override
    public AccessToken generateToken(Authentication authentication) {
        Principal principal = (Principal) authentication.getPrincipal();

        SecretKey secretKey = Keys.hmacShaKeyFor(jwtAccessTokenProperties.getSecret().getBytes());

        String rawToken = Jwts.builder()
                .subject(principal.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtAccessTokenProperties.getExpiresIn()))
                .signWith(secretKey)
                .compact();

        return AccessToken.builder()
                .rawToken(rawToken)
                .expiresIn(jwtAccessTokenProperties.getExpiresIn())
                .principal(principal.getUsername())
                .build();
    }

    @Override
    public AccessToken validateToken(String rawToken) {
        try {
            SecretKey secretKey = Keys.hmacShaKeyFor(jwtAccessTokenProperties.getSecret().getBytes());

            Claims claims = Jwts.parser().verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(rawToken)
                    .getPayload();

            return AccessToken.builder()
                    .rawToken(rawToken)
                    .expiresIn(claims.getExpiration().getTime() - new Date().getTime())
                    .principal(claims.getSubject())
                    .build();
        } catch (JwtException exc) {
            throw new InvalidTokenException("Invalid token", exc);
        }
    }

    @Override
    public void invalidateToken(String rawToken) {
        throw new UnsupportedOperationException();
    }
}
