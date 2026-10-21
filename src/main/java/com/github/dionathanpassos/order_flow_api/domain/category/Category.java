package com.github.dionathanpassos.order_flow_api.domain.category;

import com.github.dionathanpassos.order_flow_api.exception.BusinessException;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "tb_categories")
@Getter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private CategoryStatus status;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant updatedAt;

    private Category(){};

    public static Category createCategory(
            String name
    ) {
        Category category = new Category();

        category.name = name.trim();
        category.status = CategoryStatus.ACTIVE;
        category.createdAt = Instant.now();
        category.updatedAt = Instant.now();

        return category;
    }

    public boolean isActive() {
        return this.status == CategoryStatus.ACTIVE;
    }

    public void update(String name) {
        this.name = name.trim();
        this.updatedAt = Instant.now();
    }

    public void activate() {
        if (this.status == CategoryStatus.ACTIVE) {
            throw new BusinessException("Categoria já ativa");
        }

        this.status = CategoryStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        if (this.status == CategoryStatus.INACTIVE) {
            throw new BusinessException("Categoria já inativa");
        }

        this.status = CategoryStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }
}
