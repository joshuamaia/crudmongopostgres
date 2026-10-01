package br.com.joshua.crudmongopostgres.product.adapter.in.web;

import br.com.joshua.crudmongopostgres.product.adapter.in.web.dto.ProductRequest;
import br.com.joshua.crudmongopostgres.product.adapter.in.web.dto.ProductResponse;
import br.com.joshua.crudmongopostgres.product.application.port.in.CreateProductUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.in.DeleteProductUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.in.GetProductByIdUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.in.SearchProductsUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.in.UpdateProductUseCase;
import br.com.joshua.crudmongopostgres.product.domain.model.Product;
import br.com.joshua.crudmongopostgres.product.domain.model.ProductSearchCriteria;
import br.com.joshua.crudmongopostgres.shared.adapter.in.web.PageResponse;
import br.com.joshua.crudmongopostgres.shared.domain.PageQuery;
import br.com.joshua.crudmongopostgres.shared.domain.SortDirection;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

/**
 * Driving adapter shared by every product store. Concrete subclasses only define the base path
 * and which set of use cases (bound to a specific persistence adapter) they delegate to.
 */
public abstract class AbstractProductController {

    private final CreateProductUseCase createProductUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final SearchProductsUseCase searchProductsUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;

    protected AbstractProductController(CreateProductUseCase createProductUseCase,
                                        GetProductByIdUseCase getProductByIdUseCase,
                                        SearchProductsUseCase searchProductsUseCase,
                                        UpdateProductUseCase updateProductUseCase,
                                        DeleteProductUseCase deleteProductUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
        this.searchProductsUseCase = searchProductsUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        Product product = createProductUseCase.execute(ProductWebMapper.toCreateCommand(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(product.getId())
                .toUri();
        return ResponseEntity.created(location).body(ProductWebMapper.toResponse(product));
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable("id") UUID id) {
        return ProductWebMapper.toResponse(getProductByIdUseCase.execute(id));
    }

    @GetMapping
    public PageResponse<ProductResponse> search(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "active", required = false) Boolean active,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction) {
        ProductSearchCriteria criteria = new ProductSearchCriteria(name, category, minPrice, maxPrice, active);
        PageQuery pageQuery = new PageQuery(page, size, sortBy, SortDirection.from(direction));
        return PageResponse.from(searchProductsUseCase.execute(criteria, pageQuery), ProductWebMapper::toResponse);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable("id") UUID id, @Valid @RequestBody ProductRequest request) {
        return ProductWebMapper.toResponse(updateProductUseCase.execute(ProductWebMapper.toUpdateCommand(id, request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") UUID id) {
        deleteProductUseCase.execute(id);
    }
}
