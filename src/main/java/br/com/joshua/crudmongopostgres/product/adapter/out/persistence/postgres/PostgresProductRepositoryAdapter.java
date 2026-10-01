package br.com.joshua.crudmongopostgres.product.adapter.out.persistence.postgres;

import br.com.joshua.crudmongopostgres.product.application.port.out.ProductRepositoryPort;
import br.com.joshua.crudmongopostgres.product.domain.model.Product;
import br.com.joshua.crudmongopostgres.product.domain.model.ProductSearchCriteria;
import br.com.joshua.crudmongopostgres.shared.domain.PageQuery;
import br.com.joshua.crudmongopostgres.shared.domain.PageResult;
import br.com.joshua.crudmongopostgres.shared.domain.SortDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PostgresProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository productJpaRepository;

    public PostgresProductRepositoryAdapter(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity saved = productJpaRepository.save(ProductJpaMapper.toEntity(product));
        return ProductJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return productJpaRepository.findById(id).map(ProductJpaMapper::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return productJpaRepository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        productJpaRepository.deleteById(id);
    }

    @Override
    public PageResult<Product> search(ProductSearchCriteria criteria, PageQuery pageQuery) {
        Sort.Direction direction = pageQuery.direction() == SortDirection.DESC ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(pageQuery.page(), pageQuery.size(), Sort.by(direction, pageQuery.sortBy()));

        Page<ProductJpaEntity> page = productJpaRepository.findAll(ProductJpaSpecifications.fromCriteria(criteria), pageRequest);

        return new PageResult<>(
                page.getContent().stream().map(ProductJpaMapper::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
