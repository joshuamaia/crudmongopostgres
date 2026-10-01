package br.com.joshua.crudmongopostgres.product.application.usecase;

import br.com.joshua.crudmongopostgres.product.ProductFixtures;
import br.com.joshua.crudmongopostgres.product.application.port.out.ProductRepositoryPort;
import br.com.joshua.crudmongopostgres.product.domain.exception.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteProductServiceTest {

    @Mock
    private ProductRepositoryPort productRepository;

    @InjectMocks
    private DeleteProductService deleteProductService;

    @Test
    void shouldDeleteExistingProduct() {
        when(productRepository.existsById(ProductFixtures.PRODUCT_ID)).thenReturn(true);

        deleteProductService.execute(ProductFixtures.PRODUCT_ID);

        verify(productRepository).deleteById(ProductFixtures.PRODUCT_ID);
    }

    @Test
    void shouldThrowNotFoundWhenProductDoesNotExist() {
        when(productRepository.existsById(ProductFixtures.PRODUCT_ID)).thenReturn(false);

        assertThatThrownBy(() -> deleteProductService.execute(ProductFixtures.PRODUCT_ID))
                .isInstanceOf(ProductNotFoundException.class);
        verify(productRepository, never()).deleteById(any());
    }
}
