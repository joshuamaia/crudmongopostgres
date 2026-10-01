package br.com.joshua.crudmongopostgres.product.adapter.out.persistence.postgres;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Restricts JPA repository scanning to this adapter so Spring Data does not have to guess the store.
 */
@Configuration
@EnableJpaRepositories(basePackageClasses = PostgresPersistenceConfig.class)
public class PostgresPersistenceConfig {
}
