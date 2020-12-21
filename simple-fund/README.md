# README.md

 1. Run application   
    1.1. Standalone
      ```bash
	      ./gradlew bootRun
      ```
    1.2. Run with specific port
      ```bash
        ./gradlew bootRun --args='--server.port=8091'
      ```

 2. Upgrade integrated gradle
    - Replace the version in gradle/wrapper/gradle-wrapper.properties
    - Run ./gradlew.bat

 3. Swagger-ui
    - This project uses openAPI 3 (which is swagger 3)
    - To access swagger ui : http://localhost:8091/swagger-ui.html

