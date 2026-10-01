package br.com.joshua.crudmongopostgres.product.adapter.in.web;

import br.com.joshua.crudmongopostgres.product.application.port.in.CreateProductUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.in.DeleteProductUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.in.GetProductByIdUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.in.SearchProductsUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.in.UpdateProductUseCase;
import br.com.joshua.crudmongopostgres.product.config.ProductBeanNames;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MongoProductController.BASE_PATH)
public class MongoProductController extends AbstractProductController {

    public static final String BASE_PATH = "/api/v1/mongo/products";

    public MongoProductController(
            @Qualifier(ProductBeanNames.MONGO_CREATE_PRODUCT) CreateProductUseCase createProductUseCase,
            @Qualifier(ProductBeanNames.MONGO_GET_PRODUCT_BY_ID) GetProductByIdUseCase getProductByIdUseCase,
            @Qualifier(ProductBeanNames.MONGO_SEARCH_PRODUCTS) SearchProductsUseCase searchProductsUseCase,
            @Qualifier(ProductBeanNames.MONGO_UPDATE_PRODUCT) UpdateProductUseCase updateProductUseCase,
            @Qualifier(ProductBeanNames.MONGO_DELETE_PRODUCT) DeleteProductUseCase deleteProductUseCase) {
        super(createProductUseCase, getProductByIdUseCase, searchProductsUseCase, updateProductUseCase, deleteProductUseCase);
    }
}
