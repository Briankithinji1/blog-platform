package com.example.blog.api.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {

    private final SecretKey Key;

    public JwtService() {
        String secreteString = "eyJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoiQWRtaW4iLCJJc3N1ZXIiOiJJc3N1ZXIiLCJVc2VybmFtZSI6IkphdmFJblVzZSIsImV4cCI6MTcxMDI3MzMyMiwiaWF0IjoxNzEwMjczMzIyfQ.Qq2pp2wtPt63qgT8UwazkkUWSuoNp7HM8WASo4yQcQVqM8bywlwVgWGBhvCgB4XBtlQUvO8QF4U7F2o6_ix9_w";
        byte[] keyBytes = Base64.getDecoder().decode(secreteString.getBytes(StandardCharsets.UTF_8));
        this.Key = new SecretKeySpec(keyBytes, "HmacSHA512");
    }

    private static final long refreshTokenValidity = 5 * 60 * 60;
    private static final long accessTokenValidity = 15;
    private static final long resetPasswordTokenValidity = 60 * 60;

    public String issueToken(String subject) {
        return issueToken(subject, Map.of(), accessTokenValidity);
    }

    public String issueToken(
            String subject,
            Map<String, Object> claims,
            long expirationTime
    ) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer("self")
                .issuedAt(Date.from(Instant.now()))
                .expiration(
                        Date.from(Instant.now().plus(expirationTime, ChronoUnit.MINUTES))
                )
                .signWith(Key)
                .compact();
    }

    public String issueRefreshToken(
            String username
    ) {
        return generateToken(username);
    }

    private String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(Instant.now()))
                .expiration(
                        Date.from(Instant.now().plus(JwtService.refreshTokenValidity, ChronoUnit.MINUTES))
                )
                .signWith(Key)
                .compact();
    }

    public String getSubject(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token){
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = getSubject(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction){
        return claimsTFunction.apply(Jwts.parser().verifyWith(Key).build().parseSignedClaims(token).getPayload());
    }
}
