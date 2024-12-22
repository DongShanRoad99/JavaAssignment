package com.doc.auth.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;

@Component
public class JwtUtils {

    private final SecretKey key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    private static final long MAX_INACTIVE_INTERVAL = 43200000; // 12小时的毫秒数

    public JwtUtils(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-token-expire}") long accessTokenExpiration,
            @Value("${security.jwt.refresh-token-expire}") long refreshTokenExpiration) throws Exception {

        try {

            // 对secret进行sha-256哈希
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedSecretBytes = md.digest(secret.getBytes(StandardCharsets.UTF_8));
            // 创建适合HMAC SHA-256的密钥
            this.key = Keys.hmacShaKeyFor(hashedSecretBytes);

        } catch (Exception e) {
            throw new Exception("sha-256 hash error");
        }

        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken(String userId) {
        return generateToken(userId, accessTokenExpiration, "ACCESS");
    }

    public String generateRefreshToken(String userId) {
        return generateToken(userId, refreshTokenExpiration, "REFRESH");
    }

    private String generateToken(String userId, long expiration, String type) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .expiration(expiryDate)
                .claim("type", type)
                .claim("lastActivityTime", now.getTime())
                .signWith(key)
                .compact();
    }

    public String updateAccessToken(Claims claims) {
        long currentTime = System.currentTimeMillis();
        long lastActivityTime = claims.get("lastActivityTime", Long.class);
        
        // 如果超过12小时没有活动，返回null表示需要重新登录
        if (currentTime - lastActivityTime > MAX_INACTIVE_INTERVAL) {
            return null;
        }
        
        // 生成新的访问令牌，更新最后活动时间
        return generateToken(claims.getSubject(), accessTokenExpiration, "ACCESS");
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new JwtException("Invalid JWT token");
        }
    }

    public boolean isRefreshToken(Claims claims) {
        return "REFRESH".equals(claims.get("type"));
    }

    public long getTokenRemainingTime(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

    public String getTokenFromJwt(String jwt) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload()
                    .getSubject();
        } catch (JwtException e) {
            return null;
        }
    }
} 