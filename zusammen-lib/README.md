Introduction
============

This zusammen library is a library which encapsulate access to Zusammen collaborative database based on cassandra.  

Components
==========

The onboarding is comprised of the following deployment units:

- Designer backend is the core component. It exposes RESTful APIs for managing vsp. The backend
currently supports VNFD packages of ETSI SOL001 standard only.

- Designer frontend serves static content of a Web application for creating and managing vsps, and forwards API
requests to the backend. The static content includes JavaScript, images, CSS, etc.

- Translator from Tosca SOL001 standard to Onboarding internal model is used by the designer backend.

- Cassandra database is used by the designer backend as the main storage for onboarding data. A dedicated instance of
Cassandra can be deployed, or an existing cluster may be used.

- Database initialization scripts run once per deployment to create the necessary Cassandra keyspaces and tables,
pre-populate data, etc.

Execute Backend from IntelliJ
=============================
Create a copy of `application.properties` (located in `vnf-onboarding-backend\src\main\resources`) and name it `application-dev.properties`.

In this file, populate the required properties with your Cassandra, Translation and SDC Catalog info.

Run `org.onap.sdc.onboarding.SpringBootWebApplication` with the VM options: `-Dspring.profiles.active=dev`.  

Deployment on Docker
====================

The procedure below describes manual deployment on plain Docker for development or a demo.

## 1. Database

Create a dedicated instance of Cassandra. This step is optional if you already have a Cassandra cluster.
The designer is not expected to have problems working with Cassandra 3.x, but has been tested with 2.1.x because this
is the version used by SDC.

An easy way to spin up a Cassandra instance is using a Cassandra Docker image as described in the
[official documentation](https://hub.docker.com/_/cassandra/).

### Example

`docker run -d --name onboard-cassandra cassandra:2.1`

## 2. Database Initialization

**WARNING**: *This step must be executed only once.*

the designer requires two Cassandra namespaces:

- ONBOARDING
- ZUSAMMEN_ONBOARDING

By default, these keyspaces are configured to use a simple replication strategy (`'class' : 'SimpleStrategy'`)
and the replication factor of one (`'replication_factor' : 1`). In order to override this configuration, override
the *create_keyspaces.cql* file at the root of the initialization container using
[Docker volume mapping](https://docs.docker.com/storage/volumes/). Include `IF NOT EXISTS` clause in the keyspace
creation statements to prevent accidental data loss.

`docker run -ti -e CS_HOST=<cassandra-host> -e CS_PORT=<cassandra-port> -e CS_AUTHENTICATE=true/false
-e CS_USER=<cassandra-user> -e CS_PASSWORD=<cassandra-password> nexus3.onap.org:10001/NPO/vnf-onboard-init:latest`

### Environment Variables

- CS_HOST &mdash; Cassandra hostname or IP address.

- CS_PORT &mdash; Cassandra Thrift client port. If not specified, the default of 9160 will be used.

- CS_AUTHENTICATE &mdash; whether password authentication must be used to connect to Cassandra. A *false* will be
assumed if this variable is not specified.

- CS_USER &mdash; Cassandra username if CS_AUTHENTICATE is *true*.

- CS_PASSWORD &mdash; Cassandra password if CS_AUTHENTICATE is *true*.

### Example

Assuming you have created a dedicated Cassandra container as described in Database section, and the access to it is not
protected with a password, the following command will initialize the database:

`docker run -d --name vnf-onboard-init
-e CS_HOST=$(docker inspect vnf-onboard-cassandra --format={{.NetworkSettings.IPAddress}})
nexus3.onap.org:10001/onap/vnf-onboard-init:latest`

### Troubleshooting

In order to see if the the designer was successfully initialized, make sure the console does not contain error
messages. You can also see the logs of the initialization container using `docker logs vnf-onboard-init` command.
## 3. Translation

`docker run -d --name vnfd-sol001-translation -p 8080:8080 npo/vnfd-sol001-translation:latest`

## 4. Backend

`docker run -d --name vnf-onboard-backend 
-e SERVER_SSL_ENABLED=true/false 
-e SERVER_SSL_KEY_PASSWORD=<ssl_key_password> 
-e SERVER_SSL_KEYSTORE_PATH=<ssl_keystore_path> 
-e SERVER_SSL_KEYSTORE_TYPE=<ssl_keystore_type> 
-e SDC_PROTOCL=http/https
-e CS_HOSTS=<cassandra-hosts>
-e CS_PORT=<cassandra-port>
-e CS_AUTHENTICATE=true/false
-e CS_USER=<cassandra user> 
-e CS_PASSWORD=<cassandra password>
-e CS_SSL_ENABLED=true/false
--volume <cassandra-truststore-path_container>:<cassandra-truststore-path_local>
-e CS_TRUST_STORE_PATH=<cassandra-truststore-path_container> 
-e CS_TRUST_STORE_PASSWORD=<cassandra-truststore-password>
-e TRANSLATION_HOST=<translation ip>
-e TRANSLATION_PORT=<translation port> 
-e SDC_HOST=<sdc catalog ip> 
-e SDC_PORT=<sdc catalog port>
-e SDC_USER=<sdc consumer user>
-e SDC_PASSWORD=<secret> 
-e JAVA_OPTIONS="-Xmx1536m -Xms1536m"
-p 8443:8443
npo/vnf-onboard-backend:latest`

### Environment Variables

- SERVER_SSL_ENABLED &mdash; whether ssl authentication must be used to connect to application. A *false* will be
assumed if this variable is not specified.

- SERVER_SSL_KEY_PASSWORD &mdash; SSL key password if SERVER_SSL_ENABLED is *true*.

- SERVER_SSL_KEYSTORE_PATH &mdash; SSL Keystore path if SERVER_SSL_ENABLED is *true*.

- SERVER_SSL_KEYSTORE_TYPE &mdash; SSL Keystore type if SERVER_SSL_ENABLED is *true*.

- CS_HOSTS &mdash; comma-separated list of Cassandra hostnames or IP addresses.

- CS_PORT &mdash; CQL native client port. If not specified, the default of 9042 will be used.

- CS_AUTHENTICATE &mdash; whether password authentication must be used to connect to Cassandra. A *false* will be
assumed if this variable is not specified.

- CS_USER &mdash; Cassandra username if CS_AUTHENTICATE is *true*.

- CS_PASSWORD &mdash; Cassandra password if CS_AUTHENTICATE is *true*.

- CS_SSL_ENABLED &mdash; whether ssl authentication must be used to connect to Cassandra. A *false* will be
assumed if this variable is not specified.

- CS_TRUST_STORE_PATH &mdash; Cassandra Truststore path if CS_SSL_ENABLED is *true*.

- CS_TRUST_STORE_PASSWORD &mdash; Cassandra Truststore password if CS_SSL_ENABLED is *true*.

- TRANSLATION_PROTOCOL &mdash; protocol to be used for calling Translation APIs (http or https).

- TRANSLATION_HOST &mdash;  a Translation server.

- TRANSLATION_PORT &mdash;  a Translation server port, usually 8080.

- SDC_PROTOCOL &mdash; protocol to be used for calling SDC APIs (http or https).

- SDC_HOST &mdash;  a SDC backend server.

- SDC_PORT &mdash;  a SDC backend server port, usually 8080.

- SDC_USER &mdash; Onboarding consumer username

- SDC_PASSWORD &mdash; Onboarding consumer password

- JAVA_OPTIONS &mdash; optionally, JVM (Java Virtual Machine) arguments.

### Example

Assuming you have a dedicated Cassandra container as described in Database section, and the access to it is not
protected with a password. The following command will start a backend container without SSL support:

`docker run -d --name vnf-onboard-backend 
-e CS_HOSTS=$(docker inspect vnf-onboard-cassandra --format={{.NetworkSettings.IPAddress}})
-e TRANSLATION_HOST=<translation ip>
-e TRANSLATION_PORT=<translation port> 
-e SDC_HOST=<sdc catalog ip> 
-e SDC_PORT=<sdc catalog port>
-e SDC_USER=<sdc consumer user>
-e SDC_PASSWORD=<secret> 
-e JAVA_OPTIONS="-Xmx1536m -Xms1536m"
-p 8443:8443
npo/vnf-onboard-backend:latest`

### Troubleshooting

In order to verify that the backend has started successfully, check the logs of the
backend container. For example, by running `docker logs vnf-onboard-backend`. The logs must not contain any
error messages.

Application logs are located in the */var/log/... directory of a backend
container. For example, you can view the audit log by running
`docker exec -ti vnf-onboard-backend less /var/log/npo/vnf-onboard-backend/backend/audit.log`.

## 5. Frontend

`docker run -d -e BACKEND=http://<backend-host>:<backend-port> -e JAVA_OPTIONS=<jvm-options>
nexus3.onap.org:10001/npo/vnf-onboard-frontend:latest`

- BACKEND &mdash; root endpoint of the RESTful APIs exposed by a backend server.

- JAVA_OPTIONS &mdash; optionally, JVM (Java Virtual Machine) arguments.

### Example

`docker run -d --name vnf-onboard-frontend
-e BACKEND=http://$(docker inspect vnf-onboard-backend --format={{.NetworkSettings.IPAddress}}):8080
-e JAVA_OPTIONS="-Xmx64m -Xms64m -Xss1m" -p 9088:8080 nexus3.onap.org:10001/npo/vnf-onboard-frontend:latest`

Notice that port 8080 of the frontend container has been
[mapped]( https://docs.docker.com/config/containers/container-networking/#published-ports) to port 9088 of the host
machine. This makes the Designer Web application accessible from the outside world via the host machine's
IP address/hostname.

### Troubleshooting

In order to check if the Designer frontend has successfully started, look at the logs of the
frontend container. For example, by running `docker logs vnf-onboard-frontend`. The logs should not contain
error messages.

Frontend does not have backend logic, therefore there are no application logs.


SDC Plugin Configuration
========================

In order to run as an SDC pluggable designer, the designer must be added to SDC configuration as described in
[Generic plugin support](https://wiki.onap.org/display/DW/Generic+Designer+Support).

If you are deploying SDC using a standard procedure (OOM or the
[SDC shell script](https://wiki.onap.org/display/DW/Deploying+SDC+on+a+Linux+VM+for+Development)),
the easiest way to configure the Onboarding plugin is to edit the *plugins-configuration.yaml*.

### Plugin Source

The main endpoint to load the designer Web application is defined by `"pluginSourceUrl": "http://<host>:<port>"`.

Keep in mind that the URL **must be accessible from a user's browser**. In most cases, `<host>` will be the hostname or
IP address of the machine that runs Docker engine, and `<port>` will be a host port to which you have published port
8080 of the Onboarding frontend container.

### Plugin Discovery

In order to check the availability of a plugin, SDC uses `"pluginDiscoveryUrl"`. For Onboarding the value is
`http://<host>:<port>/ping`.

### Example

Let's assume that hostname of the machine that runs Docker containers with the Onboarding application is
*onboard.example.com*, and port 8080 of the Onboarding frontend is mapped to 9088 on the host. In this case the
corresponding section of *plugins-configuration.yaml* will look like below:

```

- pluginId: ONBOARD
     pluginDiscoveryUrl: "http://onboard.example.com:9088/ping"
     pluginSourceUrl: "http://onboard.example.com:9088"
     pluginStateUrl: "onboarding"
     pluginDisplayOptions:
        tab:
            displayName: "ONBOARD"
            displayRoles: ["DESIGNER", "TESTER"]

```

In a development or demo environment, the designer will run on the same host as SDC, so that its IP address will
be the one of the Docker host.