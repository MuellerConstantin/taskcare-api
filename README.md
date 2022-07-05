# TaskCare Service

> Collaboration platform for managing tasks in a team.

## Table of contents

- [Introduction](#introduction)
- [Architecture](#architecture)
- [License](#license)
  - [Forbidden](#forbidden)

## Introduction

This is the backend of the TaskCare platform. TaskCare is a platform, developed from a teaching project, for managing
tasks within a team. The collaboration platform enables the joint definition of tasks to be completed and the
continuous monitoring of the processing status, transparently visible to everyone. The architecture and design of the
platform enables the user to access TaskCare from any device.

Architecturally, the software is a monolithic web service based on HTTP. The generic RESTful interface allows control
from any environment. However, the browser-based web application [TaskCare Web](https://github.com/0x1C1B/taskcare-web)
is recommended as the officially developed front end.

## Architecture

![](https://img.shields.io/badge/dynamic/xml?color=red&label=Java&query=%2F%2A%5Blocal-name%28%29%3D%27project%27%5D%2F%2A%5Blocal-name%28%29%3D%27properties%27%5D%2F%2A%5Blocal-name%28%29%3D%27java.version%27%5D&url=https%3A%2F%2Fraw.githubusercontent.com%2F0x1C1B%2Ftaskcare-service%2Fmaster%2Fpom.xml&logo=openjdk)
![](https://img.shields.io/badge/dynamic/xml?color=green&label=Spring%20Boot&query=%2F%2A%5Blocal-name%28%29%3D%27project%27%5D%2F%2A%5Blocal-name%28%29%3D%27parent%27%5D%2F%2A%5Blocal-name%28%29%3D%27version%27%5D&url=https%3A%2F%2Fraw.githubusercontent.com%2F0x1C1B%2Ftaskcare-service%2Fmaster%2Fpom.xml&logo=spring-boot)
![](https://img.shields.io/badge/ORM-Hibernate-blue?logo=hibernate)
![](https://img.shields.io/badge/Database-MySQL-blue?logo=mysql)
![](https://img.shields.io/badge/Database-Redis-red?logo=redis)
![](https://img.shields.io/badge/OpenAPI-3.0.1-green?logo=openapi-initiative)

The application is based on a hexagonal architecture with ports and adapters pattern and also shows approaches from
domain-driven design, but does not implement this consistently. Basically, however, the application separates business
logic and implementation by subdividing it into an infrastructure, core and presentation layer.

![Architecture](docs/images/architecture.png)

The core layer is divided into the individual sub-domains, each consisting of an application and a domain layer. The
entire core has only minimal dependencies on external libraries. This allows the business logic to run database and
framework independently. In addition, there are no dependencies on the infrastructure and presentation layers, so they
can be exchanged as desired.

In the infrastructure layer, various Spring projects support the design of the application. For persistence, the JPA is
used with Hibernate as the ORM. In addition, access control is carried out using Spring Security. The interface to the
outside is represented by the presentation layer with a static RESTful interface according to OAS 3 standards,
documented [here](src/main/resources/openapi-v1.yml), and a dynamic web socket interface.

## Deployment

The artifact can be built using the Maven build system, which requires a Java Development Kit of version 17 or higher.
A Java archive (JAR) with all the required dependencies and libraries for system-independent deployment is created. To
build it, run the following command.

```shell
mvn clean package
```

Basically, no Java EE application server is required for deployment. The application automatically starts its own
application server instance internally. However, a number of configurations and a Java runtime environment of version
17 or higher are required. The service can be started natively via the Java runtime environment.

```shell
java -jar <NAME>.jar
```

### Configuration

The configuration can be done in different ways,
see [Spring Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
for details. Usually via JVM parameters or via so-called property files, the latter must be in the working directory of
the application and must be named *application.yml*. Basically, a number of components can be configured, including the
internal application server. The most common settings for this application are listed below.

| Property                                  | Description                                                                        | Required |
|-------------------------------------------|------------------------------------------------------------------------------------|----------|
| server.port                               | Port of the internal application server. By default it listens on port 8080.       | false    |
| spring.datasource.url                     | The URL to the relational database to use.¹                                        | true     |
| spring.datasource.driver-class-name       | The database driver used to control database.                                      | true     |
| spring.datasource.username                | The database user with which access is made.                                       | false    |
| spring.datasource.password                | An optional password associated with the database user.                            | false    |
| spring.redis.host                         | The host of the Redis database to use. This is mainly required for temporary data. | true     |
| spring.redis.port                         | The port of the Redis database.                                                    | true     |
| spring.redis.username                     | An optional user to authenticate with redis.                                       | false    |
| spring.redis.password                     | If authentication is required, the password for the user.                          | false    |
| spring.redis.database                     | The Redis Server database to use. By default, database 0 is used.                  | false    |
| taskcare.security.token.access.secret     | The secret used to sign the JWT access tokens.                                     | true     |
| taskcare.security.token.access.expiresIn  | The duration in milliseconds after which the access token expires.                 | false    |
| taskcare.security.token.refresh.length    | The length of the opaque refresh token.                                            | false    |
| taskcare.security.token.refresh.expiresIn | The duration in milliseconds after which the refresh token expires.                | false    |

¹In principle, any JPA/Hibernate capable relational database can be used. For this, however, the application must also
have the corresponding drivers as a dependency in the Java Classpath. By default, only the MySQL drivers are included
with the application.

## License

Copyright (c) 2022 0x1C1B

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

[MIT License](https://opensource.org/licenses/MIT) or [LICENSE](LICENSE) for
more details.

### Forbidden

**Hold Liable**: Software is provided without warranty and the software
author/license owner cannot be held liable for damages.