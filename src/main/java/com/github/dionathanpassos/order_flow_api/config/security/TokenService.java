package com.github.dionathanpassos.order_flow_api.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.dionathanpassos.order_flow_api.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    private static final String ISSUER = "OrderFlow API";
    private static final String AUDIENCE = "order-flow-service";

    public String generateAccessToken(User user) {

        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer(ISSUER)
                .withAudience(AUDIENCE)
                .withClaim("type", "access")
                .withSubject(user.getId().toString())
                .withExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .sign(algorithm);

    }

    public ResponseCookie generateRefreshTokenCookie(User user) {

        Algorithm algorithm = Algorithm.HMAC256(secret);
        String refreshTokenJwt = JWT.create()
                .withIssuer(ISSUER)
                .withAudience(AUDIENCE)
                .withClaim("type", "refresh")
                .withSubject(user.getId().toString())
                .withExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .sign(algorithm);

        return ResponseCookie.from("refreshToken", refreshTokenJwt)
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/auth")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();


    }

    public String verifyAccessToken(String token) {

        DecodedJWT decodedJWT = verifyToken(token);
        String type = decodedJWT.getClaim("type").asString();

        if(!"access".equals(type)) {
            throw new JWTVerificationException("Tipo de token inválido. Esperado: access");
        }

        return decodedJWT.getSubject();
    }

    public DecodedJWT verifyRefreshToken(String token) {

        DecodedJWT decodedJWT = verifyToken(token);
        String type = decodedJWT.getClaim("type").asString();

        if(!"refresh".equals(type)) {
            throw new JWTVerificationException("Tipo de token inválido. Esperado: refresh");
        }

        return decodedJWT;
    }

    public DecodedJWT verifyToken(String token) {

        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.require(algorithm)
                .withIssuer(ISSUER)
                .withAudience(AUDIENCE)
                .build()
                .verify(token);
    }

    public ResponseCookie destroyTokenCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/auth")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }

    public String generateHash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao inicializar algoritmo de hash", e);
        }
    }
}
