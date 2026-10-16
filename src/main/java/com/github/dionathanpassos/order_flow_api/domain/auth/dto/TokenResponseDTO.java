package com.github.dionathanpassos.order_flow_api.domain.auth.dto;

import java.time.LocalDateTime;

public record TokenResponseDTO(
        String accessToken
) {
}
