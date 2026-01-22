package it.unipi.tonightscall.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Service component responsible for JSON Web Token (JWT) management.
 * <p>
 * This service handles the creation (signing) of new tokens and the validation
 * of incoming tokens using the Auth0 java-jwt library.
 * </p>
 */
@Service
public class JWTService {

    /**
     * The secret key used to sign the tokens. Injected from application properties.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * The validity duration of the token in milliseconds.
     */
    @Value("${jwt.validityTime}")
    private long validityTime;

    /**
     * The issuer string to identify the application generating the token.
     */
    @Value("${jwt.issuer}")
    private String issuer;

    private Algorithm algorithm;

    /**
     * Initializes the cryptographic algorithm used for signing.
     * This method is called automatically after dependency injection.
     */
    @PostConstruct
    protected void init() {
        algorithm = Algorithm.HMAC256(secret);
    }

    /**
     * Generates a signed JWT for a specific user.
     *
     * @param username The user's username, which acts as the token's subject.
     * @return A String representation of the signed JWT.
     */
    public String generateToken(String username, String role) {

        return JWT.create()
                .withSubject(username) // // The "subject" of the token is the username
                .withClaim("role", role)
                .withIssuedAt(new Date()) // Creation Date
                .withExpiresAt(new Date(System.currentTimeMillis() + validityTime)) // Expiration Date
                .withIssuer(issuer) // Token issuer clain
                .sign(algorithm); // Sign with the configured algorithm
    }

    /**
     * Validates a given JWT and extracts the user's username (subject).
     *
     * @param token The raw JWT string (without "Bearer " prefix).
     * @return The username address (subject) contained in the token.
     * @throws RuntimeException if the token is invalid, expired, or tampered with.
     */
    public String validateTokenAndGetUsername(String token) {
        DecodedJWT decodedJWT = verify(token);
        return decodedJWT.getSubject();
    }

    /**
     * Extracts the specific "role" claim from a valid JWT.
     * <p>
     * This method first verifies the token integrity and then retrieves the custom claim.
     * </p>
     *
     * @param token The raw JWT string.
     * @return The role string stored in the token
     * @throws RuntimeException if the token is invalid or expired.
     */
    public String getRoleFromToken(String token) {
        DecodedJWT decodedJWT = verify(token);
        return decodedJWT.getClaim("role").asString();
    }

    /**
     * Internal helper method to verify the token's signature and claims.
     * <p>
     * It checks if the token was signed with the correct secret and issuer,
     * and if it has not expired.
     * </p>
     *
     * @param token The raw JWT string.
     * @return The {@link DecodedJWT} object if verification is successful.
     * @throws RuntimeException wrapping a {@link JWTVerificationException} if validation fails.
     */
    private DecodedJWT verify(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Invalid or expired token");
        }
    }


}
