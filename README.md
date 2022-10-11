# TaskCare Service

> Collaboration platform for managing tasks in a team.

## Table of contents

- [Introduction](#introduction)
- [Architecture](#architecture)
- [Deployment](#deployment)
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
documented [here](src/main/resources/docs/v1/openapi.yml), and a dynamic web socket interface based on STOMP, documented
[here](src/main/resources/docs/v1/asyncapi.yml). An external RabbitMQ message broker is used for Websocket communication
and to enable horizontal scaling.

## Deployment

Deployment can be done via Docker or standalone. Information on this can be found under [deployment](docs/deployment.md)
.
The configuration options of the application are also important for deployment and development. You can find it under
[configuration](docs/configuration.md).

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
