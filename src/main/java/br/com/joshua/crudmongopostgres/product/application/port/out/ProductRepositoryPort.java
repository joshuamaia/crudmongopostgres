package br.com.joshua.crudmongopostgres.product.application.port.out;

import br.com.joshua.crudmongopostgres.product.domain.model.Product;
import br.com.joshua.crudmongopostgres.product.domain.model.ProductSearchCriteria;
import br.com.joshua.crudmongopostgres.shared.domain.PageQuery;
import br.com.joshua.crudmongopostgres.shared.domain.PageResult;

import java.util.Optional;
import java.util.UUID;

/**
 * Driven port implemented by every product persistence adapter (PostgreSQL, MongoDB, ...).
 */
public interface ProductRepositoryPort {

    Product save(Product product);

    Optional<Product> findById(UUID id);

    boolean existsById(UUID id);

    void deleteById(UUID id);

    PageResult<Product> search(ProductSearchCriteria criteria, PageQuery pageQuery);
}
