package com.github.dionathanpassos.order_flow_api.domain.category.dto;

import com.github.dionathanpassos.order_flow_api.domain.category.CategoryStatus;

public record CategoryFilterDTO(
        String name,
        CategoryStatus status
) {
}
