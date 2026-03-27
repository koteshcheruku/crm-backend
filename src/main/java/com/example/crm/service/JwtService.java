package com.example.crm.service;

import com.example.crm.Model.UsersModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    // protected String secretKey;
    private static final String SECRET_KEY_BASE64 = "YmFzZTY0LWVuY29kZWQtc2VjcmV0LWtleS1tdXN0LWJlLTMyLWJ5dGVzLWxvbmc"; // Just for

    // public JwtService() {
    // try {
    // KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
    //// SecretKey sk = keyGen.generateKey();
    //// secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
    // } catch (NoSuchAlgorithmException e) {
    // throw new RuntimeException(e);
    // }
    // }

    public String generateAccessToken(UsersModel employee) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("employeeUsername",employee.getUsername());
        claims.put("employeeId",employee.getId());
        String subject = (employee.getUsername() != null && !employee.getUsername().isEmpty())
                ? employee.getEmail()
                : employee.getUsername();

        return createToken(claims, subject, 1000 * 60 * 15); // 15 mins
    }

    public String generateRefreshToken(UsersModel employee) {
        String subject = (employee.getUsername() != null && !employee.getUsername().isEmpty())
                ? employee.getEmail()
                : employee.getUsername();
        return createToken(new HashMap<>(), subject, 1000L * 60 * 60 * 24 * 7); // 7 days
    }

    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        return Jwts.builder()
                .claims(claims) // This puts the map in the payload
                .subject(subject) // This puts "sub" in the payload
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getKey()) // Always use getKey() here
                .compact();
    }

    private SecretKey getKey() {
        // byte[] byteKey = Decoders.BASE64.decode(secretKey);
        // return Keys.hmacShaKeyFor(byteKey);
        byte[] keyBytes = java.util.Base64.getDecoder().decode(SECRET_KEY_BASE64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims ->(String) Claims.get("employeeUsername"));
    }
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey()) // Make sure this matches the signWith key
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
