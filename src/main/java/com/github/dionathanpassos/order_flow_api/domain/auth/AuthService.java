package com.github.dionathanpassos.order_flow_api.domain.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.dionathanpassos.order_flow_api.config.security.TokenService;
import com.github.dionathanpassos.order_flow_api.domain.auth.dto.*;
import com.github.dionathanpassos.order_flow_api.domain.session.RefreshSessionService;
import com.github.dionathanpassos.order_flow_api.domain.user.Role;
import com.github.dionathanpassos.order_flow_api.domain.user.User;
import com.github.dionathanpassos.order_flow_api.domain.user.UserMapper;
import com.github.dionathanpassos.order_flow_api.domain.user.UserRepository;
import com.github.dionathanpassos.order_flow_api.exception.ConflictException;
import com.github.dionathanpassos.order_flow_api.exception.InvalidRefreshTokenException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RefreshSessionService refreshSessionService;

    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO requestDTO) {
        String normalizedEmail = requestDTO.email().trim().toLowerCase();

        if(userRepository.existsByEmail(normalizedEmail)) {
            throw new ConflictException("Email em utilização");
        }

        String encodedPassword = passwordEncoder.encode(requestDTO.password());

        User user = userMapper.toEntity(requestDTO, normalizedEmail, encodedPassword, Role.ROLE_CUSTOMER);
        User saved =userRepository.save(user);

        return userMapper.fromEntity(saved);
    }

    public LoginResponseDTO login(@Valid LoginRequestDTO requestDTO) {
        String normalizedEmail = requestDTO.email().trim().toLowerCase();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, requestDTO.password())
        );

        User user = (User) authentication.getPrincipal();

        String accessToken = tokenService.generateAccessToken(user);
        ResponseCookie refreshToken = tokenService.generateRefreshTokenCookie(user);

        String rawRefreshToken = refreshToken.getValue();

        refreshSessionService.createSession(rawRefreshToken);


        return new LoginResponseDTO(accessToken, refreshToken);
    }

    @Transactional
    public RefreshTokenResponseDTO renewAccessToken(String refreshToken) {

        if (refreshToken == null) {
            throw new InvalidRefreshTokenException("Acesso negado. Sessão inválida ou expirada.");
        }

        DecodedJWT decodedJWT = tokenService.verifyRefreshToken(refreshToken);
        String subject = decodedJWT.getSubject();

        User user = userRepository.findById(Long.parseLong(subject))
                .orElseThrow(() -> new InvalidRefreshTokenException("Acesso negado. Sessão inválida ou expirada."));

        String newAccessToken = tokenService.generateAccessToken(user);
        ResponseCookie newRefreshToken = tokenService.generateRefreshTokenCookie(user);

        String rawNewRefreshToken = newRefreshToken.getValue();

        refreshSessionService.updateSession(refreshToken, rawNewRefreshToken);

        return new RefreshTokenResponseDTO(newAccessToken, newRefreshToken);
    }

    public ResponseCookie logout(String refreshToken ) {

        if(refreshToken != null && !refreshToken.isBlank()) {
           refreshSessionService.revokeSession(refreshToken);
        }


        return tokenService.destroyTokenCookie();
    }
}
