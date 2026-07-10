package com.lecturboxd.auth;

import com.lecturboxd.config.JwtProperties;
import com.lecturboxd.entity.User;
import com.lecturboxd.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * EN: Creates, validates, and parses JWT access tokens for LecturBoxd users.
 * KA: ქმნის, ამოწმებს და პარსავს JWT წვდომის ტოკენებს LecturBoxd მომხმარებლებისთვის.
 */
@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    /**
     * EN: Builds an HMAC signing key from the configured JWT secret.
     * KA: აგებს HMAC ხელმოწერის გასაღებს კონფიგურირებული JWT საიდუმლოდან.
     */
    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * EN: Issues a signed JWT containing the user's email, id, and name with configured expiry.
     * KA: გამოსცემს ხელმოწერილ JWT-ს მომხმარებლის ელფოსტით, id-ით და სახელით, კონფიგურირებული ვადით.
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getExpirationMs());

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId().toString())
                .claim("name", user.getName())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    /**
     * EN: Returns true if the token signature and claims can be parsed successfully.
     * KA: აბრუნებს true-ს, თუ ტოკენის ხელმოწერა და claims წარმატებით იპარსება.
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * EN: Reads the subject (email) claim from a valid JWT.
     * KA: კითხულობს subject (ელფოსტის) claim-ს ვალიდური JWT-დან.
     */
    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * EN: Reads the userId claim from a valid JWT and converts it to a UUID.
     * KA: კითხულობს userId claim-ს ვალიდური JWT-დან და გარდაქმნის UUID-ად.
     */
    public UUID getUserIdFromToken(String token) {
        String userId = parseClaims(token).get("userId", String.class);
        return UUID.fromString(userId);
    }

    /**
     * EN: Verifies the signature and returns the JWT payload claims, or throws UnauthorizedException.
     * KA: ამოწმებს ხელმოწერას და აბრუნებს JWT payload claims-ს, ან ისვრის UnauthorizedException-ს.
     */
    private Claims parseClaims(String token) {
        try {
            // EN: Verify HMAC signature then extract payload | KA: HMAC ხელმოწერის შემოწმება და შემდეგ payload-ის ამოღება
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception ex) {
            throw new UnauthorizedException("Invalid or expired token");
        }
    }
}
