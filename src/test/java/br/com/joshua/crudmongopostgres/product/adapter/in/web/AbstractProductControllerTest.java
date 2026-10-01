package br.com.joshua.crudmongopostgres.product.adapter.in.web;

import br.com.joshua.crudmongopostgres.product.ProductFixtures;
import br.com.joshua.crudmongopostgres.product.application.port.in.CreateProductUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.in.DeleteProductUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.in.GetProductByIdUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.in.SearchProductsUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.in.UpdateProductUseCase;
import br.com.joshua.crudmongopostgres.product.domain.exception.InvalidProductSearchException;
import br.com.joshua.crudmongopostgres.product.domain.exception.ProductNotFoundException;
import br.com.joshua.crudmongopostgres.product.domain.model.Product;
import br.com.joshua.crudmongopostgres.product.domain.model.ProductSearchCriteria;
import br.com.joshua.crudmongopostgres.shared.domain.PageQuery;
import br.com.joshua.crudmongopostgres.shared.domain.PageResult;
import br.com.joshua.crudmongopostgres.shared.domain.SortDirection;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Contract shared by every product controller. Each subclass binds the scenarios to one store-specific controller.
 */
abstract class AbstractProductControllerTest {

    private static final String VALID_REQUEST = """
            {
              "name": "Mechanical Keyboard",
              "description": "RGB mechanical keyboard with brown switches",
              "price": 499.90,
              "quantity": 25,
              "category": "Peripherals",
              "active": true
            }
            """;

    @Autowired
    protected MockMvc mockMvc;

    protected abstract String basePath();

    protected abstract CreateProductUseCase createProductUseCase();

    protected abstract GetProductByIdUseCase getProductByIdUseCase();

    protected abstract SearchProductsUseCase searchProductsUseCase();

    protected abstract UpdateProductUseCase updateProductUseCase();

    protected abstract DeleteProductUseCase deleteProductUseCase();

    @Test
    void shouldCreateProduct() throws Exception {
        when(createProductUseCase().execute(any())).thenReturn(ProductFixtures.aProduct());

        mockMvc.perform(post(basePath()).contentType(MediaType.APPLICATION_JSON).content(VALID_REQUEST))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost" + basePath() + "/" + ProductFixtures.PRODUCT_ID))
                .andExpect(jsonPath("$.id").value(ProductFixtures.PRODUCT_ID.toString()))
                .andExpect(jsonPath("$.name").value("Mechanical Keyboard"))
                .andExpect(jsonPath("$.price").value(499.90))
                .andExpect(jsonPath("$.quantity").value(25))
                .andExpect(jsonPath("$.category").value("Peripherals"))
                .andExpect(jsonPath("$.active").value(true));

        ArgumentCaptor<CreateProductUseCase.Command> captor = ArgumentCaptor.forClass(CreateProductUseCase.Command.class);
        verify(createProductUseCase()).execute(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new CreateProductUseCase.Command(
                "Mechanical Keyboard", "RGB mechanical keyboard with brown switches",
                new BigDecimal("499.90"), 25, "Peripherals", true));
    }

    @Test
    void shouldReturnBadRequestWhenCreatePayloadIsInvalid() throws Exception {
        String invalidRequest = """
                {
                  "name": "",
                  "price": -10,
                  "quantity": -1,
                  "category": "Peripherals",
                  "active": true
                }
                """;

        mockMvc.perform(post(basePath()).contentType(MediaType.APPLICATION_JSON).content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request"))
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.price").exists())
                .andExpect(jsonPath("$.errors.quantity").exists());

        verifyNoInteractions(createProductUseCase());
    }

    @Test
    void shouldReturnBadRequestWhenBodyIsMalformed() throws Exception {
        mockMvc.perform(post(basePath()).contentType(MediaType.APPLICATION_JSON).content("{ not-json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(createProductUseCase());
    }

    @Test
    void shouldGetProductById() throws Exception {
        when(getProductByIdUseCase().execute(ProductFixtures.PRODUCT_ID)).thenReturn(ProductFixtures.aProduct());

        mockMvc.perform(get(basePath() + "/{id}", ProductFixtures.PRODUCT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ProductFixtures.PRODUCT_ID.toString()))
                .andExpect(jsonPath("$.name").value("Mechanical Keyboard"))
                .andExpect(jsonPath("$.description").value("RGB mechanical keyboard with brown switches"));
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(getProductByIdUseCase().execute(id)).thenThrow(new ProductNotFoundException(id));

        mockMvc.perform(get(basePath() + "/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.detail").value("Product with id '%s' was not found".formatted(id)));
    }

    @Test
    void shouldReturnBadRequestWhenIdIsNotAUuid() throws Exception {
        mockMvc.perform(get(basePath() + "/{id}", "not-a-uuid"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(getProductByIdUseCase());
    }

    @Test
    void shouldSearchProductsUsingFiltersAndPagination() throws Exception {
        PageResult<Product> page = PageResult.of(List.of(ProductFixtures.aProduct()), 1, 5, 6);
        when(searchProductsUseCase().execute(any(), any())).thenReturn(page);

        mockMvc.perform(get(basePath())
                        .param("name", "keyboard")
                        .param("category", "Peripherals")
                        .param("minPrice", "100")
                        .param("maxPrice", "600.50")
                        .param("active", "true")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sortBy", "price")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(ProductFixtures.PRODUCT_ID.toString()))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.totalPages").value(2));

        verify(searchProductsUseCase()).execute(
                new ProductSearchCriteria("keyboard", "Peripherals", new BigDecimal("100"), new BigDecimal("600.50"), true),
                new PageQuery(1, 5, "price", SortDirection.DESC));
    }

    @Test
    void shouldSearchWithDefaultPaginationWhenNoParamsAreGiven() throws Exception {
        when(searchProductsUseCase().execute(any(), any())).thenReturn(PageResult.of(List.of(), 0, 20, 0));

        mockMvc.perform(get(basePath()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(searchProductsUseCase()).execute(
                ProductSearchCriteria.empty(),
                new PageQuery(0, 20, "name", SortDirection.ASC));
    }

    @Test
    void shouldReturnBadRequestWhenSearchIsInvalid() throws Exception {
        when(searchProductsUseCase().execute(any(), any()))
                .thenThrow(new InvalidProductSearchException("minPrice must be less than or equal to maxPrice"));

        mockMvc.perform(get(basePath()).param("minPrice", "10").param("maxPrice", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Business rule violation"))
                .andExpect(jsonPath("$.detail").value("minPrice must be less than or equal to maxPrice"));
    }

    @Test
    void shouldReturnBadRequestWhenPageSizeIsOutOfRange() throws Exception {
        mockMvc.perform(get(basePath()).param("size", "1000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Page size must be between 1 and 100"));

        verifyNoInteractions(searchProductsUseCase());
    }

    @Test
    void shouldReturnBadRequestWhenSortDirectionIsInvalid() throws Exception {
        mockMvc.perform(get(basePath()).param("direction", "sideways"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(searchProductsUseCase());
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        when(updateProductUseCase().execute(any())).thenReturn(ProductFixtures.aProduct());

        mockMvc.perform(put(basePath() + "/{id}", ProductFixtures.PRODUCT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ProductFixtures.PRODUCT_ID.toString()));

        verify(updateProductUseCase()).execute(new UpdateProductUseCase.Command(
                ProductFixtures.PRODUCT_ID, "Mechanical Keyboard", "RGB mechanical keyboard with brown switches",
                new BigDecimal("499.90"), 25, "Peripherals", true));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingProduct() throws Exception {
        when(updateProductUseCase().execute(any())).thenThrow(new ProductNotFoundException(ProductFixtures.PRODUCT_ID));

        mockMvc.perform(put(basePath() + "/{id}", ProductFixtures.PRODUCT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        mockMvc.perform(delete(basePath() + "/{id}", ProductFixtures.PRODUCT_ID))
                .andExpect(status().isNoContent());

        verify(deleteProductUseCase()).execute(ProductFixtures.PRODUCT_ID);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingProduct() throws Exception {
        doThrow(new ProductNotFoundException(ProductFixtures.PRODUCT_ID))
                .when(deleteProductUseCase()).execute(ProductFixtures.PRODUCT_ID);

        mockMvc.perform(delete(basePath() + "/{id}", ProductFixtures.PRODUCT_ID))
                .andExpect(status().isNotFound());
    }
}
