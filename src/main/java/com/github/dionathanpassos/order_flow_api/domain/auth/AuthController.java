package com.github.dionathanpassos.order_flow_api.domain.auth;

import com.github.dionathanpassos.order_flow_api.domain.auth.dto.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody @Valid RegisterRequestDTO requestDTO) {
        RegisterResponseDTO created = authService.register(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginRequestDTO requestDTO) {
        LoginResponseDTO token = authService.login(requestDTO);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, token.refreshToken().toString())
                .body(new TokenResponseDTO(token.accessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDTO> renewAccessToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        RefreshTokenResponseDTO token = authService.renewAccessToken(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, token.refreshToken().toString())
                .body(new TokenResponseDTO(token.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        ResponseCookie deleteCookie = authService.logout(refreshToken);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();

    }

    @GetMapping("/teste")
    public ResponseEntity<String> teste() {
        return ResponseEntity.ok("Olas");
    }
}
