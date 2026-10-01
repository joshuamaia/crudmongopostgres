package br.com.joshua.crudmongopostgres.product.application.usecase;

import br.com.joshua.crudmongopostgres.product.application.port.in.SearchProductsUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.out.ProductRepositoryPort;
import br.com.joshua.crudmongopostgres.product.domain.exception.InvalidProductSearchException;
import br.com.joshua.crudmongopostgres.product.domain.model.Product;
import br.com.joshua.crudmongopostgres.product.domain.model.ProductSearchCriteria;
import br.com.joshua.crudmongopostgres.product.domain.model.ProductSortField;
import br.com.joshua.crudmongopostgres.shared.domain.PageQuery;
import br.com.joshua.crudmongopostgres.shared.domain.PageResult;

import java.math.BigDecimal;
import java.util.Arrays;

public class SearchProductsService implements SearchProductsUseCase {

    private final ProductRepositoryPort productRepository;

    public SearchProductsService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public PageResult<Product> execute(ProductSearchCriteria criteria, PageQuery pageQuery) {
        ProductSearchCriteria effectiveCriteria = criteria == null ? ProductSearchCriteria.empty() : criteria;
        validatePriceRange(effectiveCriteria);
        PageQuery normalizedPageQuery = normalizeSortField(pageQuery);
        return productRepository.search(effectiveCriteria, normalizedPageQuery);
    }

    private static void validatePriceRange(ProductSearchCriteria criteria) {
        BigDecimal minPrice = criteria.minPrice();
        BigDecimal maxPrice = criteria.maxPrice();
        if (minPrice != null && minPrice.signum() < 0 || maxPrice != null && maxPrice.signum() < 0) {
            throw new InvalidProductSearchException("Price filters must not be negative");
        }
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new InvalidProductSearchException("minPrice must be less than or equal to maxPrice");
        }
    }

    private static PageQuery normalizeSortField(PageQuery pageQuery) {
        ProductSortField sortField = ProductSortField.fromProperty(pageQuery.sortBy())
                .orElseThrow(() -> new InvalidProductSearchException(
                        "Invalid sort field '%s'. Allowed values: %s".formatted(
                                pageQuery.sortBy(),
                                Arrays.stream(ProductSortField.values()).map(ProductSortField::property).toList())));
        return new PageQuery(pageQuery.page(), pageQuery.size(), sortField.property(), pageQuery.direction());
    }
}
