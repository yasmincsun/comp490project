BACKEND requirements:

Java 17
Install Docker Desktop Application, video for setup: https://www.youtube.com/watch?v=JBEUKrjbWqg
Install Postman application for testing API responses

Backend Procedure:

Open project folder where backend is located.
Open Docker application.
Open terminal in desired IDE.
Make sure to access the backend folder.
For Windows users: cd backend
After enter: ./gradlew bootrun
Open another terminal, and redo step 4.
After enter: docker-compose up

Aiven Database requirements:

Install MySQL Workbench
truststore.jks file

Aiven Database procedure: 

Download the truststore.jks file and save it to any location such as Desktop.
Afterwards in your IDE paste the truststore.jks file path after: 
spring.datasource.hikari.data-source-properties.trustCertificateKeyStoreUrl=file:/
located in the application.properties file
Then in terminal enter: ./gradlew bootrun
