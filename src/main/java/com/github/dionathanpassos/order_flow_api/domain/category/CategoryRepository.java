package com.github.dionathanpassos.order_flow_api.domain.category;

import com.github.dionathanpassos.order_flow_api.domain.category.dto.CategoryFilterDTO;
import com.github.dionathanpassos.order_flow_api.domain.category.dto.CategoryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCase(String trim);

    boolean existsByNameIgnoreCaseAndIdNot(String trim, Long id);

    @Query("SELECT c FROM Category c " +
            "WHERE (CAST(:name AS string) IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%'))) " +
            "AND (CAST(:status AS STRING) IS NULL OR c.status = :status) ")
    Page<Category> findAllFiltered(
            @Param("name") String name,
            @Param("status") CategoryStatus status,
            Pageable pageable
    );
}
