package com.github.dionathanpassos.order_flow_api.domain.category;

import com.github.dionathanpassos.order_flow_api.domain.category.dto.CategoryFilterDTO;
import com.github.dionathanpassos.order_flow_api.domain.category.dto.CategoryRequestDTO;
import com.github.dionathanpassos.order_flow_api.domain.category.dto.CategoryResponseDTO;
import com.github.dionathanpassos.order_flow_api.domain.category.dto.CategoryUpdateRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(@RequestBody @Valid CategoryRequestDTO requestDTO) {
        CategoryResponseDTO created = categoryService.create(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> update(@PathVariable Long id, @RequestBody @Valid CategoryUpdateRequestDTO requestDTO) {
        CategoryResponseDTO updated = categoryService.update(id, requestDTO);

        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> findById(@PathVariable Long id) {
        CategoryResponseDTO responseDTO = categoryService.findById(id);

        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<CategoryResponseDTO> activate(@PathVariable Long id) {
        CategoryResponseDTO responseDTO = categoryService.activate(id);

        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<CategoryResponseDTO> deactivate(@PathVariable Long id) {
        CategoryResponseDTO responseDTO = categoryService.deactivate(id);

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<CategoryResponseDTO>> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false)CategoryStatus status,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CategoryResponseDTO> categories = categoryService.findAll(name, status, pageable);

        return ResponseEntity.ok(categories);
    }
}
