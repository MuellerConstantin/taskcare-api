# Configuration

In order to operate the service successfully, a number of configurations are required. This affects security
settings, external services and scheduling jobs used by TaskCare. The configuration must be available at
runtime and can be done either classically via environment variables or via config data files. Config data files
are essentially YAML files that are located in the current working directory of the service and contain the
configuration. If you decide to configure the service using a config data file, the service expects a file
called `application.yml` in the current working directory.

---
**NOTE**

The configuration of the service is based on the technical possibilities of the Spring Boot framework, see
[Spring Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config).
In order to keep the configuration of the service as simple and straightforward as possible, TaskCare abstracts
the configuration process and only uses a part of what is technically possible. Nevertheless, the technical
principles of the Spring Boot framework still apply and are mentioned here for the sake of completeness.

---

## Configuration Options

### Datasource Configuration

The following configuration options are available:

| Option                     | Environment Variable       | Description                                                                            | Required |
|----------------------------|----------------------------|----------------------------------------------------------------------------------------|----------|
| spring.datasource.url      | SPRING_DATASOURCE_URL      | Url of the MySql database used. This is the storage primarily used by the application. | true     |
| spring.datasource.username | SPRING_DATASOURCE_USERNAME | The username of the MySql database user used to connect to the database.               | false    |
| spring.datasource.password | SPRING_DATASOURCE_PASSWORD | The password of the MySql database user used to connect to the database.               | false    |
| spring.data.redis.host     | SPRING_DATA_REDIS_HOST     | The host of the Redis server used by the application .                                 | true     |
| spring.data.redis.port     | SPRING_DATA_REDIS_PORT     | The port of the Redis server used by the application.                                  | true     |
| spring.data.redis.username | SPRING_DATA_REDIS_USERNAME | The username of the Redis server used by the application.                              | false    |
| spring.data.redis.password | SPRING_DATA_REDIS_PASSWORD | The password of the Redis server used by the application.                              | false    |
| spring.data.redis.database | SPRING_DATA_REDIS_DATABASE | The instance of the Redis server used by the application.                              | false    |

### Security Configuration

The following configuration options are available:

| Option                                           | Environment Variable                             | Description                                                                                       | Required |
|--------------------------------------------------|--------------------------------------------------|---------------------------------------------------------------------------------------------------|----------|
| taskcare.security.token.access.jwt.secret        | TASKCARE_SECURITY_TOKEN_ACCESS_JWT_SECRET        | The secret used for signing JWT access tokens. This is used for authentication and authorization. | true     |
| taskcare.security.token.access.jwt.expiresIn     | TASKCARE_SECURITY_TOKEN_ACCESS_JWT_EXPIRESIN     | The expiration time of JWT access tokens in milliseconds. Default is one hour.                    | false    |
| taskcare.security.token.refresh.opaque.length    | TASKCARE_SECURITY_TOKEN_REFRESH_OPAQUE_LENGTH    | The length of opaque refresh tokens. Default is 16.                                               | false    |
| taskcare.security.token.refresh.opaque.expiresIn | TASKCARE_SECURITY_TOKEN_REFRESH_OPAQUE_EXPIRESIN | The expiration time of opaque refresh tokens in milliseconds. Default is two days.                | false    |

### Automation Configuration

The following configuration options are available:

| Option                                                      | Environment Variable                                        | Description                                                                                       | Required |
|-------------------------------------------------------------|-------------------------------------------------------------|---------------------------------------------------------------------------------------------------|----------|
| taskcare.automation.default-admin-synchronization.enabled   | TASKCARE_AUTOMATION_DEFAULT_ADMIN_SYNCHRONIZATION_ENABLED   | If enabled, the default admin user will be created on startup and synchronized with the database. | false    |
| taskcare.automation.default-admin-synchronization.password  | TASKCARE_AUTOMATION_DEFAULT_ADMIN_SYNCHRONIZATION_PASSWORD  | The password of the default admin user. This is required if the task is enabled.                  | false    |
