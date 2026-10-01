package br.com.joshua.crudmongopostgres.product.adapter.out.persistence.mongo;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Restricts MongoDB repository scanning to this adapter so Spring Data does not have to guess the store.
 */
@Configuration
@EnableMongoRepositories(basePackageClasses = MongoPersistenceConfig.class)
public class MongoPersistenceConfig {
}
