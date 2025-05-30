
## Development

### Steps

#### App foundation

App skeleton has been generated using Spring Initializr (including dependencies which set tech stack)

Junie prompt:

    create Spring boot application which exposes REST API based on requirements in file REQUIREMENTS.md  

#### Separate Model for the API

Junie prompt:

    create separate model for the API, and map to it from domain model. Implement it using Java records. Use MapStruct library for mapping

#### Exposing OpenApi Documentation

Junie prompt:

    Extract from API controllers interfaces named OrdersApi and ProductsApi, and add OpenApi annotations exposed uisng Swagger
