/**
 * Date: November 10, 2025
 * @author Jose Bastidas
 *
 * Algorithms / Design Decisions:
 * - JWT tokens are generated using HMAC-SHA256 for integrity and authenticity
 * - Token expiration is set to 10 hours (configurable)
 * - Google OAuth ID tokens are validated against Google's public keys (JWKs)
 *   using RSA key reconstruction from modulus/exponent
 * - Designed for stateless authentication and secure token verification
 * - Chosen because JWT is standard for secure stateless authentication and
 *   works well with web/mobile applications
 */

package com.musicApp.backend.features.authentication.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for generating, validating, and extracting claims from JSON Web Tokens (JWTs).
 * Handles authentication tokens for users in the MusicApp backend, as well as validation of
 * Google OAuth ID tokens using public keys fetched from Google's JWK endpoint.
 */
@Component
public class JsonWebToken {
    private final RestTemplate restTemplate;
    @Value("${jwt.secret.key}")
    private String secret;

    public JsonWebToken(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Generates a SecretKey from the configured secret for HMAC-SHA signing.
     * @return {@link SecretKey} used for signing JWT tokens
     */
    public SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generates a JWT token with the user's email as the subject.
     * @param email the user's email to embed as the token subject
     * @return {@link String} representing the signed JWT token, valid for 10 hours
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(getKey())
                .compact();
    }

    /**
     *     Extracts all claims from a JWT.
     * @param token the JWT token string
     * @return {@link Claims} object containing all claims in the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }



    /**
     *     Retrieves the email (subject) from a JWT token.
     * @param token the JWT token string
     * @return {@link String} representing the email embedded in the token
     */
    public String getEmailFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from a JWT token using a claim resolver function.
     *
     * @param token          the JWT token string
     * @param claimResolver  function to extract a specific claim from {@link Claims}
     * @param <T>            type of the extracted claim
     * @return the claim extracted using the claimResolver
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }


    /**
     *     Checks whether a JWT token has expired.
     * @param token the JWT token string
     * @return {@code true} if the token is expired, {@code false} otherwise
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token the JWT token string
     * @return {@link Date} representing the token's expiration time
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    /**
     * Validates a Google OAuth ID token using JWKs from Google and returns the claims.
     *
     * @param idToken the Google ID token string
     * @return {@link Claims} extracted from the validated Google ID token
     * @throws IllegalArgumentException if token validation fails or JWK cannot be fetched
     */
    public Claims getClaimsFromGoogleOauthIdToken(String idToken) {
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange("https://www.googleapis.com/oauth2/v3/certs", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
            });

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new IllegalArgumentException("Failed to fetch JWKs from Google.");
            }

            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> keys = (List<Map<String, Object>>) body.get("keys");

            JwtParser jwtParser = Jwts.parser().keyLocator(header -> {
                String kid = (String) header.get("kid");

                for (Map<String, Object> key : keys) {
                    if (kid.equals(key.get("kid"))) {
                        try {
                            BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode((String) key.get("n")));
                            BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode((String) key.get("e")));
                            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, exponent);
                            return KeyFactory.getInstance(
                                    key.get("kty").toString()
                            ).generatePublic(rsaPublicKeySpec);
                        } catch (Exception e) {
                            throw new IllegalArgumentException("Failed to parse RSA public key.", e);
                        }
                    }
                }
                throw new IllegalArgumentException("Failed to locate JWK with kid: " + kid);
            }).build();

            return jwtParser.parseSignedClaims(idToken).getPayload();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to validate ID token.", e);
        }
    }
}