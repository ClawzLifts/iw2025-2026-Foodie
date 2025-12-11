# Project Overview

## Purpose
This project is oriented towards small fast-food businesses that require accessible and affordable management tools. It is developed as an open-source, modular solution.

## Key Technologies Used
- Java 17/21
- Spring Framework 6
- Vaadin 24+

## High-Level Architecture
The application is structured with a clear separation of concerns:
- **Front Office**: Handles customer interactions.
  - Interactive Menu
  - Order Taking
  - Multi-method Payment
- **Back Office**: Manages internal operations.
  - Order Management
  - Order Status Control
  - Dynamic Menu Configuration
  - Business Analysis

# Getting Started

## Prerequisites
- Java Development Kit (JDK) 17/21
- Spring Boot CLI
- Vaadin Framework

## Installation Instructions
1. Clone the repository:
   ```sh
   git clone https://github.com/user/repo.git
   cd repo
   ```
2. Build the project using Maven or Gradle.

## Basic Usage Examples
- Start the application:
  ```sh
  ./mvnw spring-boot:run
  ```

## Running Tests
- Run unit tests:
  ```sh
  ./mvnw test
  ```

# Project Structure

## Main Directories and Their Purpose
- **src/main/java**: Contains the main Java source code.
- **src/main/resources**: Holds configuration files and static resources.
- **src/test/java**: Includes test cases.
- **src/test/resources**: Contains test-related resources.

## Key Files and Their Roles
- **README.md**: Project documentation.
- **pom.xml/gradle.build**: Build configuration files.
- **application.properties/application.yml**: Configuration settings.

# Development Workflow

## Coding Standards or Conventions
- Follow Java naming conventions.
- Use Spring Framework best practices.
- Adhere to Vaadin component usage guidelines.

## Testing Approach
- Unit tests for individual components.
- Integration tests for system-level interactions.

## Build and Deployment Process
1. Build the project:
   ```sh
   ./mvnw clean package
   ```
2. Deploy the generated JAR/WAR file to a server.

## Contribution Guidelines
- Fork the repository.
- Create a new branch for your feature or bug fix.
- Submit a pull request with detailed descriptions.

# Key Concepts

## Domain-Specific Terminology
- **Front Office**: Customer-facing operations.
- **Back Office**: Internal management operations.

## Core Abstractions
- **Order Management System**: Handles order creation, tracking, and fulfillment.
- **Menu Configuration**: Allows dynamic menu updates.

## Design Patterns Used
- MVC (Model-View-Controller)
- Singleton

# Common Tasks

## Step-by-Step Guides for Frequent Development Tasks
1. **Adding a New Feature**:
   - Create new Java classes in the appropriate package.
   - Update configuration files if necessary.
   - Write unit and integration tests.

2. **Deploying to Production**:
   - Build the project.
   - Deploy the JAR/WAR file to the production server.

# Troubleshooting

## Common Issues and Their Solutions
- **Build Errors**: Ensure all dependencies are correctly specified in `pom.xml` or `gradle.build`.
- **Runtime Errors**: Check logs for detailed error messages.

## Debugging Tips
- Use IDE debugging tools.
- Enable logging at a higher level to capture more details.

# References

## Links to Relevant Documentation
- [Spring Framework Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/)
- [Vaadin Framework Documentation](https://vaadin.com/docs)

## Important Resources
- Project repository: [GitHub Repository](https://github.com/user/repo)
- Issue tracker: [GitHub Issues](https://github.com/user/repo/issues)