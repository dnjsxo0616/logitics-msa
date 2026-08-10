package com.iftrue.user.global.security;


import com.iftrue.user.domain.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtUtil(
            @Value("${service.jwt.secret-key}") String secret,
            @Value("${service.jwt.access-expiration}") long accessExpiration,
            @Value("${service.jwt.refresh-expiration}") long refreshExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64URL.decode(secret)
        );
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    /**
     * JWT 생성
     */
    public String createAccessToken(
            UUID userId,
            UserRole role,
            UUID hubId,
            UUID companyId
    ) {
        Date now = new Date();

        return Jwts.builder()
                .subject(userId.toString())
                .claim("role", role)
                .claim("hubId", hubId != null ? hubId.toString() : null)
                .claim("companyId", companyId != null ? companyId.toString() : null)
                .issuedAt(now)
                .expiration(
                        new Date(now.getTime() + accessExpiration)
                )
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(UUID userId) {
        Date now = new Date();

        return Jwts.builder()
                .subject(userId.toString())
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(
                        new Date(now.getTime() + refreshExpiration)
                )
                .signWith(secretKey)
                .compact();
    }
}
