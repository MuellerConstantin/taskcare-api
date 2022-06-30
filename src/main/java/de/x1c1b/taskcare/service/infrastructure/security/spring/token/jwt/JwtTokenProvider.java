package de.x1c1b.taskcare.service.infrastructure.security.spring.token.jwt;

import de.x1c1b.taskcare.service.infrastructure.security.spring.token.AccessToken;
import de.x1c1b.taskcare.service.infrastructure.security.spring.token.InvalidTokenException;
import de.x1c1b.taskcare.service.infrastructure.security.spring.token.TokenProperties;
import de.x1c1b.taskcare.service.infrastructure.security.spring.token.TokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider implements TokenProvider<AccessToken> {

    private final TokenProperties tokenProperties;

    @Autowired
    public JwtTokenProvider(TokenProperties tokenProperties) {
        this.tokenProperties = tokenProperties;
    }

    @Override
    public AccessToken generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        String rawToken = Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + tokenProperties.getAccess().getExpiresIn()))
                .signWith(SignatureAlgorithm.HS512, tokenProperties.getAccess().getSecret())
                .compact();

        return AccessToken.builder()
                .rawToken(rawToken)
                .expiresIn(tokenProperties.getAccess().getExpiresIn())
                .principal(user.getUsername())
                .build();
    }

    @Override
    public AccessToken validateToken(String rawToken) {
        try {
            Claims claims = Jwts.parser().setSigningKey(tokenProperties.getAccess().getSecret())
                    .parseClaimsJws(rawToken)
                    .getBody();

            return AccessToken.builder()
                    .rawToken(rawToken)
                    .expiresIn(claims.getExpiration().getTime() - new Date().getTime())
                    .principal(claims.getSubject())
                    .build();
        } catch (JwtException exc) {
            throw new InvalidTokenException("Invalid token", exc);
        }
    }
}
