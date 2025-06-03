# Sample App

This is an experimental Spring boot app cretaed with help of Juni AI plugin in IntelliJ 

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

#### Massive manual refactoring

#### Creating tests

Junie prompt:

    Add tests to the project

#### Creating Web UI

    yarn create nuxt rohlik-admin
    yarn run dev

Junie prompt:

    in src/main/webui create nuxt based web admin for the API defined in src/main/java/com/assigment/rohlik/api
