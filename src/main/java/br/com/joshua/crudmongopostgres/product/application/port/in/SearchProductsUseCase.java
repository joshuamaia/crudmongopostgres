package br.com.joshua.crudmongopostgres.product.application.port.in;

import br.com.joshua.crudmongopostgres.product.domain.model.Product;
import br.com.joshua.crudmongopostgres.product.domain.model.ProductSearchCriteria;
import br.com.joshua.crudmongopostgres.shared.domain.PageQuery;
import br.com.joshua.crudmongopostgres.shared.domain.PageResult;

public interface SearchProductsUseCase {

    PageResult<Product> execute(ProductSearchCriteria criteria, PageQuery pageQuery);
}
