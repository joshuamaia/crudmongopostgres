package br.com.joshua.crudmongopostgres.product.application.usecase;

import br.com.joshua.crudmongopostgres.product.ProductFixtures;
import br.com.joshua.crudmongopostgres.product.application.port.out.ProductRepositoryPort;
import br.com.joshua.crudmongopostgres.product.domain.exception.ProductNotFoundException;
import br.com.joshua.crudmongopostgres.product.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProductByIdServiceTest {

    @Mock
    private ProductRepositoryPort productRepository;

    @InjectMocks
    private GetProductByIdService getProductByIdService;

    @Test
    void shouldReturnProductWhenItExists() {
        Product product = ProductFixtures.aProduct();
        when(productRepository.findById(ProductFixtures.PRODUCT_ID)).thenReturn(Optional.of(product));

        Product result = getProductByIdService.execute(ProductFixtures.PRODUCT_ID);

        assertThat(result).isSameAs(product);
    }

    @Test
    void shouldThrowNotFoundWhenProductDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getProductByIdService.execute(id))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}
