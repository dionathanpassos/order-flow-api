package com.github.dionathanpassos.order_flow_api.domain.session;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.dionathanpassos.order_flow_api.config.security.TokenService;
import com.github.dionathanpassos.order_flow_api.domain.user.User;
import com.github.dionathanpassos.order_flow_api.domain.user.UserRepository;
import com.github.dionathanpassos.order_flow_api.exception.InvalidRefreshTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshSessionService {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final RefreshSessionRepository refreshSessionRepository;

    @Transactional
    public void createSession(String refreshToken) {

        DecodedJWT decodedJWT = tokenService.verifyRefreshToken(refreshToken);
        String subject = decodedJWT.getSubject();

        User user = userRepository.findById(Long.parseLong(subject))
                .orElseThrow(() -> new InvalidRefreshTokenException("Acesso negado. Sessão inválida ou expirada."));

        String tokenHash = tokenService.generateHash(refreshToken);
        Instant expiresAt = decodedJWT.getExpiresAtAsInstant();

        RefreshSession session = RefreshSession.createSession(tokenHash, user, expiresAt);

        refreshSessionRepository.save(session);
    }

    public void updateSession(String refreshToken, String newRefreshToken) {

        String tokenHash = tokenService.generateHash(refreshToken);

        RefreshSession session = refreshSessionRepository
                .findByTokenHashAndRevokedAtIsNullWithLock(tokenHash)
                .filter(s -> s.getExpiresAt().isAfter(Instant.now()))
                .orElseThrow(() -> new InvalidRefreshTokenException("Acesso negado. Sessão inválida ou expirada."));

        String newTokenHash = tokenService.generateHash(newRefreshToken);

        Instant expiresAt = tokenService.verifyRefreshToken(newRefreshToken).getExpiresAtAsInstant();

        session.updateSession(newTokenHash, expiresAt);


    }

    @Transactional
    public void revokeSession(String refreshToken) {

        String tokenHash = tokenService.generateHash(refreshToken);

        refreshSessionRepository
                .findByTokenHashAndRevokedAtIsNullWithLock(tokenHash)
                .ifPresent(RefreshSession::revokeToken);

    }
}
