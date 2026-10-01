package br.com.joshua.crudmongopostgres.product.adapter.in.web;

import br.com.joshua.crudmongopostgres.product.application.port.in.CreateProductUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.in.DeleteProductUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.in.GetProductByIdUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.in.SearchProductsUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.in.UpdateProductUseCase;
import br.com.joshua.crudmongopostgres.product.config.ProductBeanNames;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(MongoProductController.class)
class MongoProductControllerTest extends AbstractProductControllerTest {

    @MockitoBean(name = ProductBeanNames.MONGO_CREATE_PRODUCT)
    private CreateProductUseCase createProductUseCase;

    @MockitoBean(name = ProductBeanNames.MONGO_GET_PRODUCT_BY_ID)
    private GetProductByIdUseCase getProductByIdUseCase;

    @MockitoBean(name = ProductBeanNames.MONGO_SEARCH_PRODUCTS)
    private SearchProductsUseCase searchProductsUseCase;

    @MockitoBean(name = ProductBeanNames.MONGO_UPDATE_PRODUCT)
    private UpdateProductUseCase updateProductUseCase;

    @MockitoBean(name = ProductBeanNames.MONGO_DELETE_PRODUCT)
    private DeleteProductUseCase deleteProductUseCase;

    @Override
    protected String basePath() {
        return MongoProductController.BASE_PATH;
    }

    @Override
    protected CreateProductUseCase createProductUseCase() {
        return createProductUseCase;
    }

    @Override
    protected GetProductByIdUseCase getProductByIdUseCase() {
        return getProductByIdUseCase;
    }

    @Override
    protected SearchProductsUseCase searchProductsUseCase() {
        return searchProductsUseCase;
    }

    @Override
    protected UpdateProductUseCase updateProductUseCase() {
        return updateProductUseCase;
    }

    @Override
    protected DeleteProductUseCase deleteProductUseCase() {
        return deleteProductUseCase;
    }
}
