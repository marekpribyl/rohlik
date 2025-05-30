
## Steps

Generate app skeleton using Spring Initializr.

Junie prompt:

    create Spring boot application which exposes REST API based on requirements in file REQUIREMENTS.md  

Scenario:

1. Examine the current project structure and configuration
2. Check the main application class and existing test files
3. Check application properties
4. Design the data model (entities for Product and Order)
- Create Product entity
- Create OrderStatus enum
- Create OrderItem entity
- Update Order entity
5. Create repositories for data access
- Create ProductRepository
- Create OrderRepository
- Create OrderItemRepository
6. Implement service layer with business logic
- Create ProductService
- Create OrderService
7. Create REST controllers for the API endpoints
- Create ProductController
- Create OrderController
8. Configure application properties (database, etc.)
9. Create database schema with Flyway migrations
10. Implement scheduled task for handling expired orders
- Create OrderExpirationScheduler
- Enable scheduling in the main application class
11. Add validation and error handling
- Create global exception handler
- Create custom exceptions
- Update services to use custom exceptions
- Update GlobalExceptionHandler to handle custom exceptions
- Add validation dependencies to pom.xml
- Update ProductController to use validation
- Update GlobalExceptionHandler to handle validation errors
- Create OrderRequest DTO with validation
- Update OrderController to use validation
12. Test the implementation

### Separate Model for the API

Junie prompt:

    create separate model for the API, and map to it from domain model. Implement it using Java records. Use MapStruct library for mapping
