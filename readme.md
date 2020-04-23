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
curl https://bitbucket.org/brennert/docker.common/raw/9b8f6b559076a97caec544f7e65f9a2433be5d65/tomcat/Dockerfile | docker build -t paths:wip --build-arg COMPONENT=paths --build-arg COMMIT_HASH=9b8f6b559076a97caec544f7e65f9a2433be5d65 -f - /$(pwd)/target
```

TODO: Test if the system is running

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

TODO: Add additional notes about how to deploy this on a live system

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
