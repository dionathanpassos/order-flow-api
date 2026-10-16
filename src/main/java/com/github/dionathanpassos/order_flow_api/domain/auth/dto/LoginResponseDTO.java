package com.github.dionathanpassos.order_flow_api.domain.auth.dto;

import org.springframework.http.ResponseCookie;

public record LoginResponseDTO(
        String accessToken,
        ResponseCookie refreshToken
) {
}
