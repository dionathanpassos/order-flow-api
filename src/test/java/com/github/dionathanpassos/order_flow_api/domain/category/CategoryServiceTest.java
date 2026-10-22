package com.github.dionathanpassos.order_flow_api.domain.category;

import com.github.dionathanpassos.order_flow_api.domain.category.dto.CategoryRequestDTO;
import com.github.dionathanpassos.order_flow_api.domain.category.dto.CategoryResponseDTO;
import com.github.dionathanpassos.order_flow_api.domain.category.dto.CategoryUpdateRequestDTO;
import com.github.dionathanpassos.order_flow_api.exception.BusinessException;
import com.github.dionathanpassos.order_flow_api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Nested
    class CreateTests {
        @Test
        void shouldCreateCategorySuccessfully() {

            //Arrange
            CategoryRequestDTO requestDTO = new CategoryRequestDTO("Vestuario");

            CategoryResponseDTO expectedResponse = new CategoryResponseDTO(1L, "Vestuario", CategoryStatus.ACTIVE, Instant.now(), Instant.now());

            when(categoryRepository.existsByNameIgnoreCase("Vestuario")).thenReturn(false);

            when(categoryMapper.fromEntity(any(Category.class))).thenReturn(expectedResponse);

            //Act
            CategoryResponseDTO responseDTO = categoryService.create(requestDTO);

            //Assert
            assertSame(expectedResponse, responseDTO);

            verify(categoryRepository).existsByNameIgnoreCase("Vestuario");
            verify(categoryRepository).save(any(Category.class));
            verify(categoryMapper).fromEntity(any(Category.class));
        }

        @Test
        void shouldThrowExceptionWhenCategoryAlreadyExists() {
            //Arrange
            CategoryRequestDTO requestDTO = new CategoryRequestDTO("Vestuario");

            when(categoryRepository.existsByNameIgnoreCase("Vestuario")).thenReturn(true);

            //Act
            BusinessException exception = assertThrows(
                    BusinessException.class, () -> categoryService.create(requestDTO)
            );

            //Assert
            assertEquals("Categoria já existe", exception.getMessage());

            verify(categoryRepository).existsByNameIgnoreCase("Vestuario");
            verify(categoryRepository, never()).save(any(Category.class));
            verify(categoryMapper, never()).fromEntity(any(Category.class));
        }

        @Test
        void shouldTrimCategoryNameWhenCheckingIfExists() {

            CategoryRequestDTO requestDTO = new CategoryRequestDTO(" Vestuario ");

            when(categoryRepository.existsByNameIgnoreCase("Vestuario")).thenReturn(false);

            categoryService.create(requestDTO);

            verify(categoryRepository).existsByNameIgnoreCase("Vestuario");
        }
    }

    @Nested
    class UpdateTestes {

        @Test
        void shouldThrowExceptionWhenCategoryDoesNotExists() {
            //Categoria inexistente

            CategoryUpdateRequestDTO requestDTO =
                    new CategoryUpdateRequestDTO("Vestuario");

            when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> categoryService.update(1L, requestDTO)
            );

            assertEquals("Categoria não encontrada", exception.getMessage());

            verify(categoryRepository, never()).save(any(Category.class));
            verify(categoryMapper, never()).fromEntity(any(Category.class));
        }

        @Test
        void shouldThrowExceptionWhenCategoryIsInactive() {
            //Categoria existente, mas inativa

            Category category = Category.createCategory("Vestuario");
            category.deactivate();
            CategoryUpdateRequestDTO requestDTO = new CategoryUpdateRequestDTO("Vesturaios");

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> categoryService.update(1L, requestDTO)
            );

            assertEquals("Categoria inativa ou bloqueada, não é possível alterar", exception.getMessage());

            verify(categoryRepository, never()).save(any(Category.class));
            verify(categoryMapper, never()).fromEntity(any(Category.class));
        }

        @Test
        void shouldThrowExceptionWhenCategoryNameAlreadyExists() {
            //Categoria ativa, mas nome duplicado

            //Arrange
            Category category = Category.createCategory("Vestuario");

            CategoryUpdateRequestDTO requestDTO = new CategoryUpdateRequestDTO("Vestuario");

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryRepository.existsByNameIgnoreCaseAndIdNot("Vestuario", 1L)).thenReturn(true);

            //Act
            BusinessException exception = assertThrows(
                    BusinessException.class, () -> categoryService.update(1L, requestDTO)
            );

            //Assert
            assertEquals("Categoria já existe", exception.getMessage());

            verify(categoryRepository).existsByNameIgnoreCaseAndIdNot("Vestuario", 1L);
            verify(categoryRepository, never()).save(any(Category.class));
            verify(categoryMapper, never()).fromEntity(any(Category.class));
            verify(categoryRepository).findById(1L);
        }

        @Test
        void shouldUpdateCategorySuccessfully() {
            //Atualizacao permitida

            //Arrange
            Category category = Category.createCategory("Vestuario");

            CategoryUpdateRequestDTO requestDTO = new CategoryUpdateRequestDTO("Eletronicos");

            CategoryResponseDTO expectedResponse = new CategoryResponseDTO(
                    1L,
                    "Eletronicos",
                    CategoryStatus.ACTIVE,
                    Instant.now(),
                    Instant.now()
            );

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryRepository.existsByNameIgnoreCaseAndIdNot("Eletronicos", 1L)).thenReturn(false);
            when(categoryMapper.fromEntity(any(Category.class))).thenReturn(expectedResponse);

            //Act
            CategoryResponseDTO responseDTO = categoryService.update(1L, requestDTO);

            //Assert
            assertEquals(expectedResponse, responseDTO);

            ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);

            verify(categoryRepository).save(captor.capture());

            assertEquals("Eletronicos", captor.getValue().getName());

            verify(categoryMapper).fromEntity(any(Category.class));
        }
    }

    @Nested
    class activateTests {

        @Test
        void shouldThrowExceptionWhenCategoryDoesNotExists() {
            //Categoria inexistente

            CategoryUpdateRequestDTO requestDTO =
                    new CategoryUpdateRequestDTO("Vestuario");

            when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> categoryService.update(1L, requestDTO)
            );

            assertEquals("Categoria não encontrada", exception.getMessage());

            verify(categoryRepository, never()).save(any(Category.class));
            verify(categoryMapper, never()).fromEntity(any(Category.class));
        }

        @Test
        void shouldThrowExceptionWhenCategoryAlreadyIsActive() {
            //Categoria ja ativa throw ex

            Category category = Category.createCategory("Eletronicos");

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> categoryService.activate(1L)
            );

            assertEquals("Categoria já ativa", exception.getMessage());

            verify(categoryRepository, never()).save(any(Category.class));
            verify(categoryMapper, never()).fromEntity(any(Category.class));
        }

        @Test
        void shouldActivateCategorySuccessfully() {
            //Atualizacao permitida

            Category category = Category.createCategory("Eletronicos");
            category.deactivate();

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            CategoryResponseDTO expectedResponse = new CategoryResponseDTO(
                    1L,
                    "Eletronicos",
                    CategoryStatus.ACTIVE,
                    category.getCreatedAt(),
                    category.getUpdatedAt()
            );

            when(categoryMapper.fromEntity(any(Category.class)))
                    .thenReturn(expectedResponse);

            CategoryResponseDTO response = categoryService.activate(1L);

            assertEquals(CategoryStatus.ACTIVE, category.getStatus());
            assertEquals(expectedResponse, response);

            verify(categoryRepository).save(any(Category.class));
            verify(categoryMapper).fromEntity(any(Category.class));

        }

    }

    @Nested
    class deactivateTests {

        @Test
        void shouldThrowExceptionWhenCategoryDoesNotExists() {
            //Categoria inexistente

            CategoryUpdateRequestDTO requestDTO =
                    new CategoryUpdateRequestDTO("Vestuario");

            when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> categoryService.update(1L, requestDTO)
            );

            assertEquals("Categoria não encontrada", exception.getMessage());

            verify(categoryRepository, never()).save(any(Category.class));
            verify(categoryMapper, never()).fromEntity(any(Category.class));
        }

        @Test
        void shouldThrowExceptionWhenCategoryAlreadyIsAInactive() {
            //Categoria ja inativa throw ex

            Category category = Category.createCategory("Eletronicos");
            category.deactivate();

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> categoryService.deactivate(1L)
            );

            assertEquals("Categoria já inativa", exception.getMessage());

            verify(categoryRepository, never()).save(any(Category.class));
            verify(categoryMapper, never()).fromEntity(any(Category.class));
        }

        @Test
        void shouldActivateCategorySuccessfully() {
            //Atualizacao permitida

            Category category = Category.createCategory("Eletronicos");

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            CategoryResponseDTO expectedResponse = new CategoryResponseDTO(
                    1L,
                    "Eletronicos",
                    CategoryStatus.INACTIVE,
                    category.getCreatedAt(),
                    category.getUpdatedAt()
            );

            when(categoryMapper.fromEntity(any(Category.class)))
                    .thenReturn(expectedResponse);

            CategoryResponseDTO response = categoryService.deactivate(1L);

            assertEquals(CategoryStatus.INACTIVE, category.getStatus());
            assertEquals(expectedResponse, response);

            verify(categoryRepository).save(any(Category.class));
            verify(categoryMapper).fromEntity(any(Category.class));

        }

    }

    @Nested
    class findByIdTests {

        @Test
        void shouldThrowExceptionWhenCategoryDoesNotExists() {
            //Categoria inexistente

            when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> categoryService.findById(1L)
            );

            assertEquals("Categoria não encontrada", exception.getMessage());

            verify(categoryMapper, never()).fromEntity(any(Category.class));
        }

        @Test
        void shouldReturnCategorySuccessfully() {

            Category category = Category.createCategory("Eletronicos");

            CategoryResponseDTO expectedResponse = new CategoryResponseDTO(
                    1L,
                    "Eletronicos",
                    CategoryStatus.ACTIVE,
                    Instant.now(),
                    Instant.now()
            );


            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryMapper.fromEntity(category)).thenReturn(expectedResponse);

            //act
            CategoryResponseDTO responseDTO = categoryService.findById(1L);

            //Assert
            assertEquals(expectedResponse, responseDTO);

            verify(categoryMapper).fromEntity(category);
            verify(categoryRepository).findById(1L);

        }
    }

    @Nested
    class findAllTests {
        @Test
        void shouldReturnCategoriesSuccessfully() {
            // Arrange
            Category category = Category.createCategory("Eletronicos");

            Pageable pageable = PageRequest.of(0, 10);

            Page<Category> categoryPage = new PageImpl<>(
                    List.of(category),
                    pageable,
                    1
            );

            CategoryResponseDTO expectedResponse = new CategoryResponseDTO(
                    1L,
                    "Eletronicos",
                    CategoryStatus.ACTIVE,
                    category.getCreatedAt(),
                    category.getUpdatedAt()
            );

            when(categoryRepository.findAllFiltered(
                    null,
                    null,
                    pageable
            )).thenReturn(categoryPage);

            when(categoryMapper.fromEntity(category))
                    .thenReturn(expectedResponse);

            // Act
            Page<CategoryResponseDTO> response = categoryService.findAll(
                    null,
                    null,
                    pageable
            );

            // Assert
            assertEquals(1, response.getTotalElements());
            assertEquals("Eletronicos", response.getContent().get(0).name());
            assertEquals(expectedResponse, response.getContent().get(0));

            verify(categoryRepository).findAllFiltered(null, null, pageable);
            verify(categoryMapper).fromEntity(category);
        }
    }
}