# crudmongopostgres

A product catalog REST API that runs the **same business core on two databases**: PostgreSQL and MongoDB.
It is built with **hexagonal architecture** (ports & adapters) organized into **feature packages**, and both stores support
dynamic, composable search through the **Specification pattern**.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17%2B-blue)
![MongoDB](https://img.shields.io/badge/MongoDB-8%2B-green)
![Tests](https://img.shields.io/badge/tests-46%20passing-success)

---

## Table of contents

- [Highlights](#highlights)
- [Tech stack](#tech-stack)
- [Architecture](#architecture)
- [Specification-based search](#specification-based-search)
- [Getting started](#getting-started)
- [Configuration](#configuration)
- [API reference](#api-reference)
- [Error handling](#error-handling)
- [Testing](#testing)
- [Project structure](#project-structure)
- [Design decisions](#design-decisions)

---

## Highlights

- **Two complete CRUDs** for products: one on PostgreSQL (`/api/v1/postgres/products`) and one on MongoDB (`/api/v1/mongo/products`).
- **One application core, two adapters.** The domain model and use cases are framework-free and wired once per store, so business rules are never duplicated.
- **Specification pattern on both stores:** JPA `Specification` for PostgreSQL and a custom `MongoSpecification` built on `Criteria` for MongoDB.
- **Pagination and sorting** with a whitelist of sortable fields.
- **RFC 9457 Problem Details** for every error response.
- **Versioned schema and sample data** with Flyway (PostgreSQL), plus a matching data file for MongoDB Compass.
- **Use case and controller tests** (46 tests) that run without any database.

## Tech stack

| Concern            | Technology                                              |
|--------------------|---------------------------------------------------------|
| Language / runtime | Java 21                                                 |
| Framework          | Spring Boot 4.1 (Spring MVC, Spring Data JPA, Spring Data MongoDB, Bean Validation) |
| Relational store   | PostgreSQL + Hibernate + Flyway                         |
| Document store     | MongoDB                                                 |
| Build              | Maven (wrapper included)                                |
| Testing            | JUnit 5, Mockito, AssertJ, Spring `@WebMvcTest` + MockMvc |
| Local infra        | Docker Compose                                          |

## Architecture

The code is organized **by feature** (`product`, plus a small `shared` kernel). Inside each feature, the code follows
**hexagonal architecture**: the domain and the application layer sit at the center and know nothing about Spring, HTTP or databases.

```
                 ┌──────────────────────────── product feature ────────────────────────────┐
                 │                                                                         │
 HTTP ──►  PostgresProductController ─┐                              ┌─► PostgresProductRepositoryAdapter ──► PostgreSQL
 (in)      MongoProductController ────┤                              │     (JPA + Specification)
                 │                    ▼                              │
                 │          ┌── port.in (use cases) ──┐    port.out  │
                 │          │ CreateProductUseCase    │  ProductRepositoryPort
                 │          │ GetProductByIdUseCase   ├──────────────┤
                 │          │ SearchProductsUseCase   │              │
                 │          │ UpdateProductUseCase    │              └─► MongoProductRepositoryAdapter ───► MongoDB
                 │          │ DeleteProductUseCase    │                    (MongoTemplate + MongoSpecification)
                 │          └──────────┬──────────────┘
                 │                     ▼
                 │              domain: Product, ProductSearchCriteria, ProductSortField
                 └─────────────────────────────────────────────────────────────────────────┘
```

| Layer | Package | Responsibility |
|-------|---------|----------------|
| Domain | `product.domain` | `Product` aggregate and its invariants, search criteria, domain exceptions. Pure Java. |
| Application | `product.application` | Input ports (one interface per use case), output port (`ProductRepositoryPort`) and use case implementations. Pure Java. |
| Driving adapters | `product.adapter.in.web` | REST controllers, request/response DTOs, mapping. |
| Driven adapters | `product.adapter.out.persistence.{postgres,mongo}` | Entities/documents, Spring Data repositories, specifications, mapping to the domain. |
| Configuration | `product.config` | Instantiates the use cases once per store and binds them to the right controller. |

### How one core serves two databases

`ProductUseCaseConfig` creates each use case twice, once with the PostgreSQL adapter and once with the MongoDB adapter,
and registers them under distinct bean names (`ProductBeanNames`). Each controller receives its own set through `@Qualifier`.
All endpoint logic lives in `AbstractProductController`, so the two controllers only declare a base path and their dependencies.

Adding a third store (e.g. Elasticsearch) means writing a new `ProductRepositoryPort` implementation, a controller subclass, and five bean definitions.
The domain and use cases stay unchanged.

## Specification-based search

Search filters are captured in the domain as a store-agnostic `ProductSearchCriteria`. Each persistence adapter translates
that criteria into **small, single-purpose specifications** and composes only the ones that apply (combined with `AND`):

| Filter      | PostgreSQL (`ProductJpaSpecifications`)      | MongoDB (`ProductMongoSpecifications`)          |
|-------------|----------------------------------------------|-------------------------------------------------|
| `name`      | `lower(name) LIKE %value%` (wildcards escaped) | case-insensitive regex, input quoted          |
| `category`  | `lower(category) = value`                    | anchored case-insensitive regex                 |
| `minPrice`  | `price >= value`                             | `price $gte Decimal128(value)`                  |
| `maxPrice`  | `price <= value`                             | `price $lte Decimal128(value)`                  |
| `active`    | `active = value`                             | `active $eq value`                              |

```java
// PostgreSQL – Spring Data JPA Specification
Specification<ProductJpaEntity> spec = ProductJpaSpecifications.fromCriteria(criteria);
productJpaRepository.findAll(spec, pageRequest);

// MongoDB – custom MongoSpecification built on Criteria
MongoSpecification<ProductDocument> spec = ProductMongoSpecifications.fromCriteria(criteria);
mongoTemplate.find(new Query(spec.toCriteria()).with(pageRequest), ProductDocument.class);
```

`MongoSpecification<T>` mirrors the JPA API (`and`, `allOf`, `unrestricted`), so both adapters read the same way.
User input is always escaped (`LIKE` wildcards on PostgreSQL, `Pattern.quote` on MongoDB), so a search for `100%_off` matches those characters literally.

## Getting started

### Prerequisites

- JDK 21 or newer
- Docker and Docker Compose. You can also use your own PostgreSQL and MongoDB instances.

### 1. Start the databases

```bash
docker compose up -d
```

This starts PostgreSQL and MongoDB, both with a database named **`crudmongopostgres`**.

> If ports `5432` or `27017` are already in use on your machine, override them:
> ```bash
> POSTGRES_PORT=55432 MONGO_PORT=57017 docker compose up -d
> ```
> Then export the same variables before starting the application.

### 2. Run the application

```bash
./mvnw spring-boot:run          # Linux / macOS
mvnw.cmd spring-boot:run        # Windows
```

On startup, Flyway runs the migrations in `src/main/resources/db/migration` and Spring Data creates the Mongo indexes.
The API listens on `http://localhost:8080`.

### 3. Sample data

Both stores ship with the **same 25 sample products**, using the same ids (`00000000-0000-4000-8000-000000000001` to `…025`).
The products cover 7 categories (Peripherals, Monitors, Laptops, Storage, Audio, Networking and Office) and include active, inactive and out-of-stock items.

#### PostgreSQL: automatic (Flyway)

[`V2__insert_sample_products.sql`](src/main/resources/db/migration/V2__insert_sample_products.sql) runs automatically on the first start.
Flyway records it in `flyway_schema_history`, so it runs only once per database.

```sql
SELECT count(*) FROM products;   -- 25
```

> To reload the data from scratch, drop and recreate the database (or run `docker compose down -v`) and start the application again.
> Never edit a migration that has already been applied. Flyway will refuse to start because of the checksum mismatch.
> To change the sample data, add a new migration (`V3__...sql`).

#### MongoDB: import with MongoDB Compass

MongoDB has no Flyway, so the same records are provided in [`mongo/products.json`](mongo/products.json).
The file is written in MongoDB Extended JSON, so `price` is imported as `Decimal128` and the dates as `Date`.

1. Open **MongoDB Compass** and connect to `mongodb://localhost:27017`.
2. Select the `crudmongopostgres` database. If it does not exist yet, start the application once, or click **Create database** with database `crudmongopostgres` and collection `products`.
3. Open the **`products`** collection.
4. Click **Add Data → Import JSON or CSV file** and select `mongo/products.json`.
5. Keep the file type as **JSON** and click **Import**. Compass should report **25 documents imported**.

To reload the data, delete the documents in the collection (or drop it) and import the file again.
Importing twice into a populated collection fails with duplicate key errors, because the `_id`s are fixed.

<details>
<summary>Alternative: command line with <code>mongoimport</code></summary>

```bash
mongoimport --uri "mongodb://localhost:27017/crudmongopostgres" \
  --collection products --jsonArray --file mongo/products.json
```

`mongoimport` is part of the [MongoDB Database Tools](https://www.mongodb.com/try/download/database-tools), which are installed separately from the server.
</details>

### 4. Try it

```bash
curl "http://localhost:8080/api/v1/postgres/products?category=audio&active=true&sortBy=price&direction=desc"

curl http://localhost:8080/api/v1/mongo/products/00000000-0000-4000-8000-000000000001

curl -X POST http://localhost:8080/api/v1/postgres/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop Stand","description":"Aluminum stand","price":149.90,"quantity":30,"category":"Office","active":true}'
```

Replace `postgres` with `mongo` in the path (or vice versa) to switch stores.

## Configuration

Every connection setting can be overridden with an environment variable:

| Variable            | Default             | Description               |
|---------------------|---------------------|---------------------------|
| `POSTGRES_HOST`     | `localhost`         | PostgreSQL host           |
| `POSTGRES_PORT`     | `5432`              | PostgreSQL port           |
| `POSTGRES_DB`       | `crudmongopostgres` | PostgreSQL database       |
| `POSTGRES_USER`     | `postgres`          | PostgreSQL user           |
| `POSTGRES_PASSWORD` | `postgres`          | PostgreSQL password       |
| `MONGO_HOST`        | `localhost`         | MongoDB host              |
| `MONGO_PORT`        | `27017`             | MongoDB port              |
| `MONGO_DB`          | `crudmongopostgres` | MongoDB database          |

See [`application.yml`](src/main/resources/application.yml) for the full configuration.

## API reference

Both stores expose the same contract under different base paths:

| Store      | Base path                    |
|------------|------------------------------|
| PostgreSQL | `/api/v1/postgres/products`  |
| MongoDB    | `/api/v1/mongo/products`     |

| Method   | Path     | Description                         | Success |
|----------|----------|-------------------------------------|---------|
| `POST`   | `/`      | Create a product                    | `201 Created` + `Location` header |
| `GET`    | `/{id}`  | Get a product by id                 | `200 OK` |
| `GET`    | `/`      | Search products (filters + paging)  | `200 OK` |
| `PUT`    | `/{id}`  | Replace a product                   | `200 OK` |
| `DELETE` | `/{id}`  | Delete a product                    | `204 No Content` |

### Request body (`POST` / `PUT`)

```json
{
  "name": "Mechanical Keyboard",
  "description": "RGB mechanical keyboard with brown switches",
  "price": 499.90,
  "quantity": 25,
  "category": "Peripherals",
  "active": true
}
```

| Field         | Rules                                      |
|---------------|--------------------------------------------|
| `name`        | required, max 120 characters               |
| `description` | optional, max 500 characters               |
| `price`       | required, `>= 0`, max 2 decimal places     |
| `quantity`    | required, `>= 0`                           |
| `category`    | required, max 60 characters                |
| `active`      | required                                   |

### Product response

```json
{
  "id": "7b0c2f3e-6f51-4b5a-9a4e-2f1d3c9e8a10",
  "name": "Mechanical Keyboard",
  "description": "RGB mechanical keyboard with brown switches",
  "price": 499.90,
  "quantity": 25,
  "category": "Peripherals",
  "active": true,
  "createdAt": "2026-01-10T10:00:00Z",
  "updatedAt": "2026-01-10T10:00:00Z"
}
```

### Search parameters (`GET /`)

All parameters are optional and can be combined.

| Parameter   | Type    | Default | Description                                                   |
|-------------|---------|---------|---------------------------------------------------------------|
| `name`      | string  |         | Case-insensitive "contains" match                             |
| `category`  | string  |         | Case-insensitive exact match                                  |
| `minPrice`  | decimal |         | Price greater than or equal to                                |
| `maxPrice`  | decimal |         | Price less than or equal to (must be `>= minPrice`)           |
| `active`    | boolean |         | Filter by active flag                                         |
| `page`      | int     | `0`     | Zero-based page index                                         |
| `size`      | int     | `20`    | Page size, from 1 to 100                                      |
| `sortBy`    | string  | `name`  | One of `name`, `price`, `quantity`, `category`, `createdAt`, `updatedAt` |
| `direction` | string  | `ASC`   | `ASC` or `DESC` (case-insensitive)                            |

```json
{
  "content": [ { "id": "…", "name": "Mechanical Keyboard", "…": "…" } ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

## Error handling

Errors use [RFC 9457 Problem Details](https://www.rfc-editor.org/rfc/rfc9457) (`application/problem+json`):

| Situation                                              | Status |
|--------------------------------------------------------|--------|
| Bean Validation failure (includes an `errors` map per field) | `400` |
| Business rule violation (invalid price range, unknown sort field, page size out of range, etc.) | `400` |
| Malformed JSON or an invalid UUID in the path          | `400`  |
| Product not found                                      | `404`  |

```json
{
  "type": "about:blank",
  "title": "Invalid request",
  "status": 400,
  "detail": "Request validation failed",
  "instance": "/api/v1/mongo/products",
  "errors": {
    "name": "must not be blank",
    "price": "must be greater than or equal to 0.00"
  }
}
```

## Testing

```bash
./mvnw test
```

The suite does **not** require any database or Docker:

| Suite | What it covers |
|-------|----------------|
| `application.usecase.*ServiceTest` | Each use case in isolation, with a mocked `ProductRepositoryPort` and a fixed `Clock`: happy paths, not-found cases, domain invariants, search validation and sort-field normalization. |
| `AbstractProductControllerTest` | A shared HTTP contract (15 scenarios) covering status codes, JSON payloads, `Location` header, request-to-command mapping, validation and Problem Details. |
| `PostgresProductControllerTest` / `MongoProductControllerTest` | Run the shared contract against each controller with `@WebMvcTest`, so both APIs are guaranteed to behave identically. |

## Project structure

```
src/main/java/br/com/joshua/crudmongopostgres
├── CrudmongopostgresApplication.java
├── product                                  # feature package
│   ├── domain
│   │   ├── model                            # Product, ProductSearchCriteria, ProductSortField
│   │   └── exception                        # ProductNotFoundException, InvalidProduct*Exception
│   ├── application
│   │   ├── port
│   │   │   ├── in                           # one interface per use case
│   │   │   └── out                          # ProductRepositoryPort
│   │   └── usecase                          # framework-free implementations
│   ├── adapter
│   │   ├── in/web                           # controllers, DTOs, web mapper
│   │   └── out/persistence
│   │       ├── postgres                     # JPA entity, repository, specifications, adapter
│   │       └── mongo                        # document, repository, MongoSpecification, adapter
│   └── config                               # use case wiring per store
└── shared                                   # cross-feature kernel
    ├── domain                               # PageQuery, PageResult, SortDirection, base exceptions
    ├── adapter/in/web                       # GlobalExceptionHandler, PageResponse
    └── config                               # Clock bean

src/main/resources
├── application.yml
└── db/migration
    ├── V1__create_products_table.sql        # schema
    └── V2__insert_sample_products.sql       # 25 sample products

mongo
└── products.json                            # the same 25 products, ready for MongoDB Compass import
```

## Design decisions

- **Framework-free core.** Use cases are plain classes registered in `ProductUseCaseConfig` instead of being annotated with `@Service`. The core has no Spring imports and can be tested with plain JUnit.
- **Aggregate owns its invariants.** `Product` validates its own state (non-blank name/category, non-negative price/quantity, length limits) and normalizes price to scale 2. Bean Validation at the edge gives fast feedback, and the domain still protects itself from any other entry point.
- **Application-generated UUIDs.** Ids are created by the domain, so both stores share the same identity format. MongoDB stores them as strings.
- **Decimal correctness on MongoDB.** `price` is stored as `Decimal128` so range queries compare numbers rather than strings.
- **Deterministic time.** A `Clock` bean is injected into use cases, and timestamps are truncated to milliseconds so both stores return the same precision.
- **Store-scoped repository scanning.** `@EnableJpaRepositories` and `@EnableMongoRepositories` are restricted to their adapter packages, so Spring Data never has to guess which store a repository belongs to.
