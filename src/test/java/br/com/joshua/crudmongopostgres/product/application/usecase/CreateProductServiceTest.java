package br.com.joshua.crudmongopostgres.product.application.usecase;

import br.com.joshua.crudmongopostgres.product.ProductFixtures;
import br.com.joshua.crudmongopostgres.product.application.port.in.CreateProductUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.out.ProductRepositoryPort;
import br.com.joshua.crudmongopostgres.product.domain.exception.InvalidProductException;
import br.com.joshua.crudmongopostgres.product.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateProductServiceTest {

    @Mock
    private ProductRepositoryPort productRepository;

    private CreateProductService createProductService;

    @BeforeEach
    void setUp() {
        createProductService = new CreateProductService(productRepository, ProductFixtures.fixedClock());
    }

    @Test
    void shouldCreateProductWithGeneratedIdAndTimestamps() {
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CreateProductUseCase.Command command = new CreateProductUseCase.Command(
                "  Gaming Mouse ", "Wireless mouse", new BigDecimal("199.9"), 10, "Peripherals", true);

        Product created = createProductService.execute(command);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        Product saved = captor.getValue();

        assertThat(created).isSameAs(saved);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Gaming Mouse");
        assertThat(saved.getDescription()).isEqualTo("Wireless mouse");
        assertThat(saved.getPrice()).isEqualByComparingTo("199.90");
        assertThat(saved.getPrice().scale()).isEqualTo(2);
        assertThat(saved.getQuantity()).isEqualTo(10);
        assertThat(saved.getCategory()).isEqualTo("Peripherals");
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getCreatedAt()).isEqualTo(ProductFixtures.NOW);
        assertThat(saved.getUpdatedAt()).isEqualTo(ProductFixtures.NOW);
    }

    @Test
    void shouldRejectBlankName() {
        CreateProductUseCase.Command command = new CreateProductUseCase.Command(
                " ", null, BigDecimal.TEN, 1, "Peripherals", true);

        assertThatThrownBy(() -> createProductService.execute(command))
                .isInstanceOf(InvalidProductException.class)
                .hasMessage("Product name must not be blank");
        verifyNoInteractions(productRepository);
    }

    @Test
    void shouldRejectNegativePrice() {
        CreateProductUseCase.Command command = new CreateProductUseCase.Command(
                "Mouse", null, new BigDecimal("-1"), 1, "Peripherals", true);

        assertThatThrownBy(() -> createProductService.execute(command))
                .isInstanceOf(InvalidProductException.class)
                .hasMessage("Product price must not be negative");
        verifyNoInteractions(productRepository);
    }

    @Test
    void shouldRejectNegativeQuantity() {
        CreateProductUseCase.Command command = new CreateProductUseCase.Command(
                "Mouse", null, BigDecimal.ONE, -5, "Peripherals", true);

        assertThatThrownBy(() -> createProductService.execute(command))
                .isInstanceOf(InvalidProductException.class)
                .hasMessage("Product quantity must not be negative");
        verifyNoInteractions(productRepository);
    }
}
