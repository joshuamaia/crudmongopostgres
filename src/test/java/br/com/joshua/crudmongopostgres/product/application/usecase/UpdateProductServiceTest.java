package br.com.joshua.crudmongopostgres.product.application.usecase;

import br.com.joshua.crudmongopostgres.product.ProductFixtures;
import br.com.joshua.crudmongopostgres.product.application.port.in.UpdateProductUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.out.ProductRepositoryPort;
import br.com.joshua.crudmongopostgres.product.domain.exception.InvalidProductException;
import br.com.joshua.crudmongopostgres.product.domain.exception.ProductNotFoundException;
import br.com.joshua.crudmongopostgres.product.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProductServiceTest {

    @Mock
    private ProductRepositoryPort productRepository;

    private UpdateProductService updateProductService;

    @BeforeEach
    void setUp() {
        updateProductService = new UpdateProductService(productRepository, ProductFixtures.fixedClock());
    }

    @Test
    void shouldUpdateExistingProductAndRefreshUpdatedAt() {
        Product existing = ProductFixtures.aProduct();
        when(productRepository.findById(ProductFixtures.PRODUCT_ID)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UpdateProductUseCase.Command command = new UpdateProductUseCase.Command(
                ProductFixtures.PRODUCT_ID, "Keyboard Pro", "Updated", new BigDecimal("549.00"), 5, "Keyboards", false);

        Product updated = updateProductService.execute(command);

        verify(productRepository).save(existing);
        assertThat(updated.getId()).isEqualTo(ProductFixtures.PRODUCT_ID);
        assertThat(updated.getName()).isEqualTo("Keyboard Pro");
        assertThat(updated.getDescription()).isEqualTo("Updated");
        assertThat(updated.getPrice()).isEqualByComparingTo("549.00");
        assertThat(updated.getQuantity()).isEqualTo(5);
        assertThat(updated.getCategory()).isEqualTo("Keyboards");
        assertThat(updated.isActive()).isFalse();
        assertThat(updated.getCreatedAt()).isEqualTo(ProductFixtures.CREATED_AT);
        assertThat(updated.getUpdatedAt()).isEqualTo(ProductFixtures.NOW);
    }

    @Test
    void shouldThrowNotFoundWhenProductDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        UpdateProductUseCase.Command command = new UpdateProductUseCase.Command(
                id, "Name", null, BigDecimal.ONE, 1, "Category", true);

        assertThatThrownBy(() -> updateProductService.execute(command))
                .isInstanceOf(ProductNotFoundException.class);
        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldNotSaveWhenUpdateViolatesInvariants() {
        when(productRepository.findById(ProductFixtures.PRODUCT_ID)).thenReturn(Optional.of(ProductFixtures.aProduct()));
        UpdateProductUseCase.Command command = new UpdateProductUseCase.Command(
                ProductFixtures.PRODUCT_ID, "Name", null, BigDecimal.ONE, 1, "", true);

        assertThatThrownBy(() -> updateProductService.execute(command))
                .isInstanceOf(InvalidProductException.class)
                .hasMessage("Product category must not be blank");
        verify(productRepository, never()).save(any());
    }
}
