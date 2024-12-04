# Operation

The TaskCare service is generally operated on-premise. This means that both the actual application server
and required third-party services must be installed and operated in their own environment, either
standalone or containerized.

## System Environment

The application uses a number of third-party services and also requires them to function correctly. It is therefore
necessary to make these services available and to make them known accordingly. The following third-party services
are required, the exact versions are TaskCare release dependent:

**[MySQL](https://www.mysql.com/)**

MySQL is a database that is used for the storage of application data. It is used as primary storage for
business data and event sourcing.

**[Redis](https://redis.io/)**

Redis is a key-value store that is used for the storage of temporary application state. This includes
the tokens used for authentication and authorization as well as the state of the application itself.

## Deployment

As mentioned above, the TaskCare service is generally operated on-premise. After providing required third-party
services and the appropriate configuration, the service can be started. Because the application is written in
Java and based on the Java Virtual Machine (JVM), a Java Runtime Environment (JRE) is required. The exact
version depends on the used TaskCare release.

---
**NOTE**

The application binary is delivered as a fat JAR file and sets up its own application server internally on
start-up. Because of this no Java EE application server is required, a JRE is sufficient.

---

For error-free operation, the application must also be configured accordingly. Third-party services must be
made known and settings made. For a detailed overview of the configuration, see [here](./configuration.md).
If all requirements have been met, the application can be started via JRE with the following command:

```shell
java -jar taskcare-api-<VERSION>.jar
```

### Build application

If for any reason a pre-built binary cannot be used, for example during development, it is possible to build the
application manually¹. The custom binary can be built with the Maven build system using a local installation.
The result is an executable Java archive (JAR), which can be started in standalone mode as described above. To
build it, run the following command:

```shell
mvn clean package
```

The resulting artifact, usually located in the project's target directory (`target`) and named
`taskcare-api-<VERSION>.jar`, can be executed by the above command.

<small>
    ¹If the application is to be built directly from the source code instead of obtaining a pre-built executable, the
    <a href="https://adoptium.net/">Java Development Kit (JDK)</a> and <a href="https://maven.apache.org/">Apache Maven</a>
    are required.
</small>
