# Sample App

This is an experimental Spring boot app created with help of Juni AI plugin in IntelliJ (with manual refactoring).

## How to Run

By default, in-mem persistence is used. For dev purposes file storage can be used instead, configuration is available in `application.properties`. Also, if `testdata` profile is activated, some testing data is loaded during startup (located in `resources/db/testdata`)

    mvn spring-boot:run

with sample data:

    mvn spring-boot:run -Dspring-boot.run.profiles=testdata

## Known Issues

* AI generated tests are low quality, refactoring needed
* AI generated web UI ends with error on startup

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

    In src/main/webui exists basic nuxt based web app with nuxtui. create there web admin for the API defined in src/main/java/com/assigment/rohlik/apiproject
