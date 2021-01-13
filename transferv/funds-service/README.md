# README.md

 1. Run application   
    1.1. Standalone
      ```bash
          ./mvnw spring-boot:run
      ```
    1.2. Run with specific port
      ```bash
        ./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8091
      ```

 2. Swagger-ui
    - This project uses openAPI 3 (which is swagger 3)
    - To access swagger ui : http://localhost:8091/swagger-ui.html

