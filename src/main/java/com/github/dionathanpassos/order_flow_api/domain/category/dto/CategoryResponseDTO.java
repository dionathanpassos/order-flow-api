package com.github.dionathanpassos.order_flow_api.domain.category.dto;

import com.github.dionathanpassos.order_flow_api.domain.category.CategoryStatus;

import java.time.Instant;

public record CategoryResponseDTO(
        Long id,
        String name,
        CategoryStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
