# CI-Backend

# Table of Contents

* [Prerequisites](#prerequisites)
* [Start Development](#start-development)
    * [Define Configuration](#define-configuration)
    * [Start the Database](#start-the-database)
    * [Start the Server](#start-the-server)
    * [Stop the Database](#stop-the-database-when-you-are-finished)
* [Build for Production](#build-for-production)
* [Problems](#problems)

## Prerequisites

[docker_desktop]: https://www.docker.com/products/docker-desktop/

[java_download]: https://www.oracle.com/java/technologies/downloads/

[maven_download]: https://maven.apache.org/download.cgi

* [Docker Desktop][docker_desktop]
* [Java 22][java_download]
* [Maven 3.9.7][maven_download]

Make sure to update your enviroment variables or PATH according to the instructions linked above,
to be able to execute all needed commands.

## Start Development

In this section I will explain how you can set up the development enviroment and run the application.

### Define Configuration

Create a file named application.properties at the following location

```
src/main/resources/application.properties
```

It needs to define the following values:

Spring Config:

* spring.application.name: application name
* spring.datasource.url: url on which to reach the database
* spring.datasource.username: db username
* spring.datasource.password: db password
* spring.jpa.defer-datasource-initialization=true
* spring.sql.init.mode=always

Jwt Config:

* ci.accessTokenSecret: secret Key used to sign accessTokens
* ci.refreshTokenSecret: secret Key used to sign refreshTokens

If you don't want to bother creating the config yourself, you can rename

```
src/main/resources/application.properties.sample
```

to

```
src/main/resources/application.properties
```

### Start the Database

Run the following command in the root directory of the project

```
docker compose up -d
```

This will start a postgres db which will listen on port 5432.

### Start the Server

Run the following command in the root directory of the project

```
mvn spring-boot:run
```

### Stop the Database (when you are finished)

Run the following command in the root directory of the project

```
docker compose down
```

## Build for Production

TODO

## Problems

No known problems yet.
