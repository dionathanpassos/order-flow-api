package com.github.dionathanpassos.order_flow_api.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        String firstName,

        @NotBlank(message = "Sobrebome é obrigatório")
        String lastName,

        @NotBlank(message = "Email é obrigatório")
        String email,

        @NotBlank(message = "Senha é obrigatório")
        String password
) {
}
