package br.com.joshua.crudmongopostgres.product.config;

/**
 * Bean names used to bind each driving adapter to the use cases of its persistence store.
 */
public final class ProductBeanNames {

    public static final String POSTGRES_CREATE_PRODUCT = "postgresCreateProductUseCase";
    public static final String POSTGRES_GET_PRODUCT_BY_ID = "postgresGetProductByIdUseCase";
    public static final String POSTGRES_SEARCH_PRODUCTS = "postgresSearchProductsUseCase";
    public static final String POSTGRES_UPDATE_PRODUCT = "postgresUpdateProductUseCase";
    public static final String POSTGRES_DELETE_PRODUCT = "postgresDeleteProductUseCase";

    public static final String MONGO_CREATE_PRODUCT = "mongoCreateProductUseCase";
    public static final String MONGO_GET_PRODUCT_BY_ID = "mongoGetProductByIdUseCase";
    public static final String MONGO_SEARCH_PRODUCTS = "mongoSearchProductsUseCase";
    public static final String MONGO_UPDATE_PRODUCT = "mongoUpdateProductUseCase";
    public static final String MONGO_DELETE_PRODUCT = "mongoDeleteProductUseCase";

    private ProductBeanNames() {
    }
}
