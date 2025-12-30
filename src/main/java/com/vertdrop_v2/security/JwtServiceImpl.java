package com.vertdrop_v2.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken. Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j. LoggerFactory;
import org. springframework.beans.factory.annotation. Value;
import org.springframework. security.core.userdetails. UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream. Collectors;

@Service
public class JwtServiceImpl implements JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret non configuré (propriété 'jwt.secret').");
        }
        byte[] keyBytes = Decoders. BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    @Override
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());

        // Extract roles and permissions from authorities
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());
        claims.put("roles", authorities);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey())
                .compact();

        logger.debug("🔑 JWT token generated for user: {} (expires at: {})", userDetails.getUsername(), expiry);

        return token;
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            boolean isValid = username.equals(userDetails. getUsername()) && !isTokenExpired(token);

            if (isValid) {
                logger.debug("✅ JWT token is valid for user: {}", username);
            } else {
                logger.warn("❌ JWT token is invalid for user: {}", username);
            }

            return isValid;
        } catch (Exception e) {
            logger.error("❌ Error validating JWT token: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public long getExpirationTime() {
        return jwtExpirationMs;
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}