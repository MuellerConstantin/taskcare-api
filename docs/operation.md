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
business data and event sourcing. The application expects a specific table structure from the database,
which must be created before it can be used. The exact definition of the tables can be found
[here](./ddl/mysql.sql). It is the responsibility of the database administrator to ensure that the
application can access the database and that the required tables are created.

**[Redis](https://redis.io/)**

Redis is a key-value store that is used for the storage of temporary application state. This includes
the tokens used for authentication and authorization as well as the state of the application itself.

**[MinIO](https://min.io/)**

MinIO is a S3-compatible object storage service that is used for the storage of file attachments.

**[LDAP Server](https://www.rfc-editor.org/rfc/rfc4511)**

LDAP is a directory service that can be used for user authentication and authorization. Instead of manually
adding users to the application, users can be synchronized and used from an LDAP server. An LDAP compatible
directory service is required for this. However, the use of such a service is optional.

## Deployment

As mentioned above, the TaskCare service is generally operated on-premise. For error-free operation, the
application must also be configured accordingly. Third-party services must be made known and settings made.
For a detailed overview of the configuration, see [here](./configuration.md). The application can run
either as system software (standalone) or in a container. Depending on this, either Docker or a Java
Runtime Environment (JRE) is required.

### Standalone

Because the application is written in Java and based on the Java Virtual Machine (JVM), a Java Runtime
Environment (JRE) is required, if the application is to run in standalone mode. The exact version of the JRE
version depends on the used TaskCare release.

---
**NOTE**

The application binary is delivered as a fat JAR file and sets up its own application server internally on
start-up. Because of this no Java EE application server is required, a JRE is sufficient.

---

If all requirements have been met, the application can be started via JRE with the following command:

```shell
java -jar taskcare-api-<VERSION>.jar
```

#### Build application

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

### Container

The application can also be run in a container using the provided or self-built Docker image. This does not
require a Java Runtime Environment (JRE) installation on the target system, but an installation of the Docker
Engine.

Even with container deployment, the application still has to be configured. This is basically the same as for
standalone operation. When using a configuration file, however, it must be ensured that this is made accessible
to the container, for example by mounting a volume. Alternatively, the container can be configured using
system environment variables. For configuration details see [configuration](./configuration.md).

The release in the form of a Docker image can be started as follows:

```shell
docker run -d -p 8080:8080 -v <CONFIG_PATH>:/usr/local/etc/taskcare/api -v <LOGS_PATH>:/usr/local/var/log/taskcare/api taskcare/api:<VERSION>
```

#### Build image

Should it be necessary in the development phase or for other reasons to build the Docker image directly
from the source code, this is also possible. No Java development tools or installations are required for
this either, the image is built in multi-stage operation on a Docker basis. The provided Dockerfile can
be used to build:

```shell
docker build -t taskcare/api:<VERSION> .
```
