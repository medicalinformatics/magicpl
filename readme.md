# mainzelliste.paths

The component mainzelliste.paths is intended for the description of different pseudonymization processes. It should also offer a replacement for the idmanager component known from DKTK. 

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

- Java
- JDK 8
- the build tool [maven](https://maven.apache.org/)
- [Tomcat Server](https://tomcat.apache.org/) (version 8 is recommended)

> Alternatively you can use docker for building and deploying the container. All steps will be described with and without docker

### Installing

**Building the WAR file**:
```shell script
mvn clean install
```
**or with docker**:

First you will need to build the builder container:
```shell script
docker build -t samply-builder -<<EOF
ARG MAVEN_CONTAINER_VERSION=3.6.3-jdk-8-openj9
FROM maven:3.6.3-jdk-8-openj9
RUN apt-get update && apt-get install -y git
EOF
```

After building the builder container you can run this to build the WAR File.
> Note:
> With this environment variables the container will use the current code and your local maven registry and maven settings to build. 
```shell script
docker run -it --rm --name paths-build -v /$(pwd)/://usr/src/paths-build/ -w //usr/src/paths-build/ -v /$HOME/.m2/://root/.m2 samply-builder mvn clean install;
```

With the WAR File build, it now should be possible to build a new tomcat container with following command:
```shell script
curl https://bitbucket.org/brennert/docker.common/raw/027e064f4f6832b0405886a298a9e6305015a027/tomcat/Dockerfile | docker build -t paths:latest --build-arg COMPONENT=paths --build-arg COMMON_REPOSITORY_URN=https://bitbucket.org/brennert/docker.common/raw/027e064f4f6832b0405886a298a9e6305015a027 -f - ~/IdeaProjects/mainzelliste.paths/target
```

To run the docker container you can now use:
```shell script
docker run -p 8080:8080 --rm -it paths:latest
```

## Running the tests

> Note: Currently no tests exist

TODO: Explain how to run the automated tests for this system. 

### Break down into end to end tests

TODO: Explain what these tests test and why

```
Give an example
```

### And coding style tests

TODO: Explain what these tests test and why

```
Give an example
```

## Deployment

> Note: Currently there is now downloadable version of the docker container, so you will need to build it yourself using the steps described above.

The image for 
> Please note that currently only a standardized version of paths is available via docker image. The passing of configuration is still in testing.

The component image currently supports following environment variables:

|variable name|description|default value|
|-------------|-----------|-------------|
|PATHS_API_KEY|the api key used to access paths interface|PATHS_API_KEY|
|PATHS_PASSPHRASE|the passphrase used to generate controllnumbers|PATHS_PASSPHRASE|

For tomcat the following environment variables are supported by the image:

|variable name|description|default value|
|-------------|-----------|-------------|
|DEBUG|if set to "true", the container will start with remote debugging enabled on port 1099| - |
|TOMCAT_REVERSEPROXY_FQDN|Fully-qualified domain name to be used for access to this container, e.g. patientlist.example.org|-|
|TOMCAT_REVERSEPROXY_PORT|The corresponding port number|80 or 443 according to TOMCAT_REVERSEPROXY_SSL|
|TOMCAT_REVERSEPROXY_SSL|Set to true if container is accessed via SSL/TLS; false otherwise|false|

TODO: move this to docker.common readme.md 

## Built With

TODO: 
* [Maven](https://maven.apache.org/) - Dependency Management

## Contributing

TODO: 
Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Andreas Borg** - *Initial work*
* **Galina Tremper** - *Initial work*
* **Christian Koch** - *Initial work*

* **Torben Brenner** - *Further development*
* **Moanes Ben Amor** - *Further development*

See also the list of [contributors](TODO) who participated in this project.

## License

TODO: 

## Acknowledgments

TODO:
* Hat tip to anyone whose code was used
* Inspiration
* etc
