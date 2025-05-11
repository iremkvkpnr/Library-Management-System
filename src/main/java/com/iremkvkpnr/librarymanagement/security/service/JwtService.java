package com.iremkvkpnr.librarymanagement.security.service;

import com.iremkvkpnr.librarymanagement.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service for handling JWT token operations such as generation, validation, and extraction.
 */
@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    /**
     * Extracts the username (email) from the JWT token.
     * @param token JWT token
     * @return Username (email)
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the JWT token.
     * @param token JWT token
     * @param claimsResolver Function to extract the claim
     * @return The extracted claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a JWT token for the given user.
     * @param user User entity
     * @return JWT token
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        return generateToken(claims, user);
    }

    /**
     * Generates a JWT token with extra claims for the given user.
     * @param extraClaims Additional claims
     * @param user User entity
     * @return JWT token
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            User user
    ) {
        return buildToken(extraClaims, user, jwtExpiration);
    }

    /**
     * Generates a refresh JWT token for the given user.
     * @param user User entity
     * @return Refresh JWT token
     */
    public String generateRefreshToken(
            User user
    ) {
        return buildToken(new HashMap<>(), user, refreshExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            User user,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates the JWT token for the given user details.
     * @param token JWT token
     * @param userDetails User details
     * @return true if valid, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the user ID from the JWT token.
     * @param token JWT token
     * @return User ID or null if not present
     */
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        Object userIdObj = claims.get("userId");
        if (userIdObj == null) {
            return null;
        }
        return Long.valueOf(userIdObj.toString());
    }
} 