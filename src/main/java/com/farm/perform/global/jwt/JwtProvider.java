package com.farm.perform.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_TYPE = "type";

    private Key secretKey;
    private JwtParser jwtParser;

    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.access-token-expire-time}")
    private long accessTokenExpireTime;

    @Value("${jwt.refresh-token-expire-time}")
    private long refreshTokenExpireTime;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
    }

    private String generateToken(Long userId, String role, String type, long expireTime) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expireTime);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim(CLAIM_ROLE, role)   // 커스텀 Claim (role)
                .claim(CLAIM_TYPE, type)   // 커스텀 Claim (access/refresh)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessToken(Long userId, String role) {
        return generateToken(userId, role, "access", accessTokenExpireTime);
    }

    public String generateRefreshToken(Long userId, String role) {
        return generateToken(userId, role, "refresh", refreshTokenExpireTime);
    }

    public boolean validateToken(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT: {}", e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("잘못된 JWT: {}", e.getMessage());
        }
        return false;
    }

    public Long getUserIdFromToken(String token) {
        return Long.valueOf(jwtParser.parseClaimsJws(token).getBody().getSubject());
    }

    public String getRoleFromToken(String token) {
        return jwtParser.parseClaimsJws(token).getBody().get(CLAIM_ROLE, String.class);
    }

    public String getTypeFromToken(String token) {
        return jwtParser.parseClaimsJws(token).getBody().get(CLAIM_TYPE, String.class);
    }
}
