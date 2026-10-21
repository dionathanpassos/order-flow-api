package com.github.dionathanpassos.order_flow_api.domain.category;

import com.github.dionathanpassos.order_flow_api.domain.category.dto.CategoryResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponseDTO fromEntity(Category category);
}
