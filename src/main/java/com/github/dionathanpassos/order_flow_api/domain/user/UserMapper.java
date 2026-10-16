package com.github.dionathanpassos.order_flow_api.domain.user;

import com.github.dionathanpassos.order_flow_api.domain.auth.dto.RegisterRequestDTO;
import com.github.dionathanpassos.order_flow_api.domain.auth.dto.RegisterResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "role", source = "role")
    @Mapping(target = "password", source = "encodedPassword")
    @Mapping(target = "email", source = "normalizedEmail")
    User toEntity(RegisterRequestDTO requestDTO, String normalizedEmail, String encodedPassword, Role role);


    RegisterResponseDTO fromEntity(User user);
}
