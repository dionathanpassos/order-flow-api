package com.github.dionathanpassos.order_flow_api.domain.category.dto;

import com.github.dionathanpassos.order_flow_api.domain.category.CategoryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequestDTO(

        @NotBlank(message = "Nome da categoria é obrigatório")
        @Size(min = 4)
        String name
) {
}
