package com.kr.neoshop.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {
	 private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJwtToken(org.springframework.security.core.userdetails.UserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities().stream()
            .map(a -> a.getAuthority())
            .toList();

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    public List<String> getRolesFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("roles", List.class);
    }

    	public boolean validateJwtToken(String token) {
            try {
                Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
                return true;
            } catch (ExpiredJwtException e) {
                logger.warn("JWT token expired: {}", e.getMessage());
            } catch (UnsupportedJwtException e) {
                logger.warn("JWT token unsupported: {}", e.getMessage());
            } catch (MalformedJwtException e) {
                logger.warn("JWT token malformed: {}", e.getMessage());
            } catch (SignatureException e) {
                logger.warn("Invalid JWT signature: {}", e.getMessage());
            } catch (IllegalArgumentException e) {
                logger.warn("JWT token is empty or null: {}", e.getMessage());
            }
            return false;
        }


    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .setAllowedClockSkewSeconds(60)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
