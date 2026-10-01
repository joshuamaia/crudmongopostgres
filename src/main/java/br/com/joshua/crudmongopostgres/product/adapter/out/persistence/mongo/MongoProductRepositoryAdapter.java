package br.com.joshua.crudmongopostgres.product.adapter.out.persistence.mongo;

import br.com.joshua.crudmongopostgres.product.application.port.out.ProductRepositoryPort;
import br.com.joshua.crudmongopostgres.product.domain.model.Product;
import br.com.joshua.crudmongopostgres.product.domain.model.ProductSearchCriteria;
import br.com.joshua.crudmongopostgres.shared.domain.PageQuery;
import br.com.joshua.crudmongopostgres.shared.domain.PageResult;
import br.com.joshua.crudmongopostgres.shared.domain.SortDirection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class MongoProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductMongoRepository productMongoRepository;
    private final MongoTemplate mongoTemplate;

    public MongoProductRepositoryAdapter(ProductMongoRepository productMongoRepository, MongoTemplate mongoTemplate) {
        this.productMongoRepository = productMongoRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Product save(Product product) {
        ProductDocument saved = productMongoRepository.save(ProductDocumentMapper.toDocument(product));
        return ProductDocumentMapper.toDomain(saved);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return productMongoRepository.findById(id.toString()).map(ProductDocumentMapper::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return productMongoRepository.existsById(id.toString());
    }

    @Override
    public void deleteById(UUID id) {
        productMongoRepository.deleteById(id.toString());
    }

    @Override
    public PageResult<Product> search(ProductSearchCriteria criteria, PageQuery pageQuery) {
        Query query = new Query(ProductMongoSpecifications.fromCriteria(criteria).toCriteria());
        long totalElements = mongoTemplate.count(query, ProductDocument.class);

        Sort.Direction direction = pageQuery.direction() == SortDirection.DESC ? Sort.Direction.DESC : Sort.Direction.ASC;
        query.with(PageRequest.of(pageQuery.page(), pageQuery.size(), Sort.by(direction, pageQuery.sortBy())));

        List<Product> content = mongoTemplate.find(query, ProductDocument.class).stream()
                .map(ProductDocumentMapper::toDomain)
                .toList();

        return PageResult.of(content, pageQuery.page(), pageQuery.size(), totalElements);
    }
}
