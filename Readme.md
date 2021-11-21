# ACCOUNT-WALLET MICROSERVICE PROJECT

It is a project which is a simple account microservice application that manages credit/debit transactions.

## Project Description

- This project was implemented for creating account/wallet microservice application. Wallet microservice application was implemented
with Java 11, Spring Boot and Maven.

- The concurrent requests and simultaneous debit/credit operations are handled with optimistic locking and the potential OptimisticLockException is handled with @Retryable spring boot
feature (with maxAttempts and backoff attributes) on the appropriate method

- A Rate Limit aspect is implemented via AOP to prevent concurrent requests from same IP in a short period of time. The annotation is method based and the cache manager which controls
ips and request number is configurable through application properties (expiration and size)

- An audit mechanism is implemented (via asynchronous call for performance) and tracks request headers, request method request payload, response payload, url path and API call duration

- Swagger is provided and configured http://localhost:8080/swagger-ui.html

- JUnit tests have been implemented (about 70% of the services)
