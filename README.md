# DMS Backend (Spring Boot)

If you're not sure how to run this project locally, use the steps below.

## 1) Prerequisites

- Java 17 (`java -version`)
- MySQL 8+
- Internet access the first time Maven dependencies are downloaded

## 2) Configure environment variables (optional)

The app reads config from `src/main/resources/application.properties` and provides defaults. You can override them:

```bash
export DB_URL='jdbc:mysql://localhost:3306/DMS_DIRECTORY?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8'
export DB_USER='springstudent'
export DB_PASS='SpringStudent@14$'
export PORT='8080'
```

## 3) Start MySQL

Make sure your MySQL server is running and the target schema/database exists.

## 4) Run the app

From the project root:

```bash
./mvnw spring-boot:run
```

On Windows:

```bat
mvnw.cmd spring-boot:run
```

## 5) Build without running

```bash
./mvnw clean package
```

The packaged jar is created under `target/`.

## Troubleshooting

- If Maven wrapper download fails, check network/proxy settings.
- If DB connection fails, verify `DB_URL`, `DB_USER`, `DB_PASS`, and MySQL accessibility.
- If Java version errors appear, install Java 17 and ensure it is on your `PATH`.
