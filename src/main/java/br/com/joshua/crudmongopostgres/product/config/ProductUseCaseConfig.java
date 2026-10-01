package br.com.joshua.crudmongopostgres.product.config;

import br.com.joshua.crudmongopostgres.product.adapter.out.persistence.mongo.MongoProductRepositoryAdapter;
import br.com.joshua.crudmongopostgres.product.adapter.out.persistence.postgres.PostgresProductRepositoryAdapter;
import br.com.joshua.crudmongopostgres.product.application.port.in.CreateProductUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.in.DeleteProductUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.in.GetProductByIdUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.in.SearchProductsUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.in.UpdateProductUseCase;
import br.com.joshua.crudmongopostgres.product.application.usecase.CreateProductService;
import br.com.joshua.crudmongopostgres.product.application.usecase.DeleteProductService;
import br.com.joshua.crudmongopostgres.product.application.usecase.GetProductByIdService;
import br.com.joshua.crudmongopostgres.product.application.usecase.SearchProductsService;
import br.com.joshua.crudmongopostgres.product.application.usecase.UpdateProductService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Wires the framework-free use cases to each persistence adapter. The application core is instantiated
 * once per store, so the same business rules run on top of PostgreSQL and MongoDB.
 */
@Configuration
public class ProductUseCaseConfig {

    // PostgreSQL

    @Bean(ProductBeanNames.POSTGRES_CREATE_PRODUCT)
    public CreateProductUseCase postgresCreateProductUseCase(PostgresProductRepositoryAdapter repository, Clock clock) {
        return new CreateProductService(repository, clock);
    }

    @Bean(ProductBeanNames.POSTGRES_GET_PRODUCT_BY_ID)
    public GetProductByIdUseCase postgresGetProductByIdUseCase(PostgresProductRepositoryAdapter repository) {
        return new GetProductByIdService(repository);
    }

    @Bean(ProductBeanNames.POSTGRES_SEARCH_PRODUCTS)
    public SearchProductsUseCase postgresSearchProductsUseCase(PostgresProductRepositoryAdapter repository) {
        return new SearchProductsService(repository);
    }

    @Bean(ProductBeanNames.POSTGRES_UPDATE_PRODUCT)
    public UpdateProductUseCase postgresUpdateProductUseCase(PostgresProductRepositoryAdapter repository, Clock clock) {
        return new UpdateProductService(repository, clock);
    }

    @Bean(ProductBeanNames.POSTGRES_DELETE_PRODUCT)
    public DeleteProductUseCase postgresDeleteProductUseCase(PostgresProductRepositoryAdapter repository) {
        return new DeleteProductService(repository);
    }

    // MongoDB

    @Bean(ProductBeanNames.MONGO_CREATE_PRODUCT)
    public CreateProductUseCase mongoCreateProductUseCase(MongoProductRepositoryAdapter repository, Clock clock) {
        return new CreateProductService(repository, clock);
    }

    @Bean(ProductBeanNames.MONGO_GET_PRODUCT_BY_ID)
    public GetProductByIdUseCase mongoGetProductByIdUseCase(MongoProductRepositoryAdapter repository) {
        return new GetProductByIdService(repository);
    }

    @Bean(ProductBeanNames.MONGO_SEARCH_PRODUCTS)
    public SearchProductsUseCase mongoSearchProductsUseCase(MongoProductRepositoryAdapter repository) {
        return new SearchProductsService(repository);
    }

    @Bean(ProductBeanNames.MONGO_UPDATE_PRODUCT)
    public UpdateProductUseCase mongoUpdateProductUseCase(MongoProductRepositoryAdapter repository, Clock clock) {
        return new UpdateProductService(repository, clock);
    }

    @Bean(ProductBeanNames.MONGO_DELETE_PRODUCT)
    public DeleteProductUseCase mongoDeleteProductUseCase(MongoProductRepositoryAdapter repository) {
        return new DeleteProductService(repository);
    }
}
