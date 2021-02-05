# README.md

 1. Run application   
    1.1. Standalone
      ```bash
          ./gradlew bootRun
          Or
          ./mvnw spring-boot:run
      ```
    1.2. Run with specific port
      ```bash
        ./gradlew bootRun --args='--server.port=8001'
        Or
        ./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8001
      ```

 2. Upgrade integrated gradle
    - Replace the version in gradle/wrapper/gradle-wrapper.properties
    - Run ./gradlew.bat

 3. Swagger-ui
    - This project uses openAPI 3 (which is swagger 3)
    - To access swagger ui : http://localhost:8001/swagger-ui.html

