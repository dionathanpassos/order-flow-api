package com.github.dionathanpassos.order_flow_api.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record RegisterResponseDTO(
        String firstName,
        String lastName,
        String email,
        LocalDateTime createdAt
) {
}
