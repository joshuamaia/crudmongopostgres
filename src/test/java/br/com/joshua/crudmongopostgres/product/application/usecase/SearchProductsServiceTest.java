package br.com.joshua.crudmongopostgres.product.application.usecase;

import br.com.joshua.crudmongopostgres.product.ProductFixtures;
import br.com.joshua.crudmongopostgres.product.application.port.out.ProductRepositoryPort;
import br.com.joshua.crudmongopostgres.product.domain.exception.InvalidProductSearchException;
import br.com.joshua.crudmongopostgres.product.domain.model.Product;
import br.com.joshua.crudmongopostgres.product.domain.model.ProductSearchCriteria;
import br.com.joshua.crudmongopostgres.shared.domain.PageQuery;
import br.com.joshua.crudmongopostgres.shared.domain.PageResult;
import br.com.joshua.crudmongopostgres.shared.domain.SortDirection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchProductsServiceTest {

    @Mock
    private ProductRepositoryPort productRepository;

    @InjectMocks
    private SearchProductsService searchProductsService;

    @Test
    void shouldDelegateCriteriaAndNormalizedPageQueryToRepository() {
        ProductSearchCriteria criteria = new ProductSearchCriteria(
                "keyboard", "Peripherals", new BigDecimal("100"), new BigDecimal("600"), true);
        PageQuery pageQuery = new PageQuery(0, 10, "PRICE", SortDirection.DESC);
        PageResult<Product> expected = PageResult.of(List.of(ProductFixtures.aProduct()), 0, 10, 1);
        when(productRepository.search(eq(criteria), any(PageQuery.class))).thenReturn(expected);

        PageResult<Product> result = searchProductsService.execute(criteria, pageQuery);

        assertThat(result).isSameAs(expected);
        verify(productRepository).search(criteria, new PageQuery(0, 10, "price", SortDirection.DESC));
    }

    @Test
    void shouldUseEmptyCriteriaWhenNoneIsGiven() {
        PageQuery pageQuery = new PageQuery(0, 20, "name", SortDirection.ASC);
        when(productRepository.search(any(), any())).thenReturn(PageResult.of(List.of(), 0, 20, 0));

        searchProductsService.execute(null, pageQuery);

        verify(productRepository).search(ProductSearchCriteria.empty(), pageQuery);
    }

    @Test
    void shouldRejectMinPriceGreaterThanMaxPrice() {
        ProductSearchCriteria criteria = new ProductSearchCriteria(
                null, null, new BigDecimal("500"), new BigDecimal("100"), null);
        PageQuery pageQuery = new PageQuery(0, 20, "name", SortDirection.ASC);

        assertThatThrownBy(() -> searchProductsService.execute(criteria, pageQuery))
                .isInstanceOf(InvalidProductSearchException.class)
                .hasMessage("minPrice must be less than or equal to maxPrice");
        verifyNoInteractions(productRepository);
    }

    @Test
    void shouldRejectNegativePriceFilters() {
        ProductSearchCriteria criteria = new ProductSearchCriteria(null, null, new BigDecimal("-1"), null, null);
        PageQuery pageQuery = new PageQuery(0, 20, "name", SortDirection.ASC);

        assertThatThrownBy(() -> searchProductsService.execute(criteria, pageQuery))
                .isInstanceOf(InvalidProductSearchException.class)
                .hasMessage("Price filters must not be negative");
        verifyNoInteractions(productRepository);
    }

    @Test
    void shouldRejectUnknownSortField() {
        PageQuery pageQuery = new PageQuery(0, 20, "password", SortDirection.ASC);

        assertThatThrownBy(() -> searchProductsService.execute(ProductSearchCriteria.empty(), pageQuery))
                .isInstanceOf(InvalidProductSearchException.class)
                .hasMessageStartingWith("Invalid sort field 'password'");
        verifyNoInteractions(productRepository);
    }
}
