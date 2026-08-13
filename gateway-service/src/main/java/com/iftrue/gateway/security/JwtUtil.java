package com.iftrue.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(
            @Value("${service.jwt.secret-key}") String secret
    ) {
        this.secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64URL.decode(secret)
        );
    }

    public Claims parseClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUserId(Claims claims) {
        return claims.getSubject();
    }

    public String getRole(Claims claims) {
        return claims.get("role", String.class);
    }

    public String getHubId(Claims claims) {
        return claims.get("hubId", String.class);
    }

    public String getCompanyId(Claims claims) {
        return claims.get("companyId", String.class);
    }
}
