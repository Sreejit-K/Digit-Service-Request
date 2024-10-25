# Digit Service Request

This repository contains the implementation of the Digit Service Request assignment, focused on creating a robust backend service using Spring Boot, JDBC, and PostgreSQL, following Digit’s coding standards.

## Project Overview

This service was designed to manage and search entities efficiently, following best practices for maintainability, performance, and coding standards. Key aspects of the implementation include:

### 1. Boilerplate Code Generation
The foundational code for this project was generated using the Digit Swagger code-gen JAR. I streamlined the project by removing unnecessary components, such as:
- **Kafka** configurations
- Redundant **configurations**

This cleanup was done to create a cleaner, more efficient file structure.

### 2. Entity Management
Entity creation and search functionalities are implemented using **Spring JDBC**, which provides direct interaction with PostgreSQL for optimized database operations.

### 3. Database Setup and Migrations
Database tables are managed with **Flyway** for schema migrations, and the migration scripts are located in the `resources` directory.

To set up the PostgreSQL database:
1. Use the provided `docker-compose.yml` file in the project root.
2. Run the following command to start the database:
   ```bash
   docker-compose up -d
