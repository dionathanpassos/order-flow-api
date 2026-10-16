package com.github.dionathanpassos.order_flow_api.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseCookie;

public record RefreshTokenResponseDTO(
        String accessToken,
        ResponseCookie refreshToken
) {
}
