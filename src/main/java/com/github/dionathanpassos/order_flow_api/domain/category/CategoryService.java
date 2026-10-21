package com.github.dionathanpassos.order_flow_api.domain.category;

import com.github.dionathanpassos.order_flow_api.domain.category.dto.CategoryFilterDTO;
import com.github.dionathanpassos.order_flow_api.domain.category.dto.CategoryRequestDTO;
import com.github.dionathanpassos.order_flow_api.domain.category.dto.CategoryResponseDTO;
import com.github.dionathanpassos.order_flow_api.domain.category.dto.CategoryUpdateRequestDTO;
import com.github.dionathanpassos.order_flow_api.exception.BusinessException;
import com.github.dionathanpassos.order_flow_api.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponseDTO create(CategoryRequestDTO requestDTO) {

        if (categoryRepository.existsByNameIgnoreCase(requestDTO.name().trim())) {
           throw new BusinessException("Categoria já existe");
        }

        Category category = Category.createCategory(requestDTO.name());

        categoryRepository.save(category);

        return categoryMapper.fromEntity(category);
    }

    @Transactional
    public CategoryResponseDTO update(Long id, CategoryUpdateRequestDTO requestDTO) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        if (!category.isActive()) {
            throw new BusinessException("Categoria inativa ou bloqueada, não é possível alterar");
        }

        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(requestDTO.name().trim(), id)) {
            throw new BusinessException("Categoria já existe");
        }

        category.update(
                requestDTO.name()
        );

        categoryRepository.save(category);
        return categoryMapper.fromEntity(category);

    }

    @Transactional
    public CategoryResponseDTO activate(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        category.activate();

        categoryRepository.save(category);
        return categoryMapper.fromEntity(category);
    }

    @Transactional
    public CategoryResponseDTO deactivate(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        category.deactivate();

        categoryRepository.save(category);
        return categoryMapper.fromEntity(category);
    }

    @Transactional(readOnly = true)
    public CategoryResponseDTO findById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        return categoryMapper.fromEntity(category);
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponseDTO> findAll(
            String name,
            CategoryStatus status,
            Pageable pageable
    ) {

        Page<Category> categories = categoryRepository.findAllFiltered(name, status, pageable);

        return categories.map(categoryMapper::fromEntity);
    }
}
