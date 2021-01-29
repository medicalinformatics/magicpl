# MagicPL (paths)

The component MagicPL is intended for building pseudonymisation processes.
It implements multiple Processors which enable users to describe their pseudonymisation process.

> Note: Due to the naming during development (paths) some files are still not named correctly for MagicPL.
> In future releases we will rename those files to fit the official name MagicPL.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

- Java
- [OpenJDK 8](https://openjdk.java.net/projects/jdk8/)
- the build tool [maven](https://maven.apache.org/)
- [Git](https://git-scm.com/), which is currently needed for completing a build with maven.  
- [Tomcat Server](https://tomcat.apache.org/) (version 8 is recommended)

> Alternatively you can use docker for building and deploying the container. All steps will be described with and without docker

### Installing without docker
First you need to build a WAR file from your copy of the source code. You can do this by using the build tool maven:
```shell script
mvn clean install
```
The command will build the WAR file for you and put it in the "./target/" directory of your local copy. 
You can now deploy the generated WAR file by putting it in the "webapps" directory of your local tomcat instance. For more information on how to deploy a WAR into Tomcat, you can refer to this [link](https://tomcat.apache.org/tomcat-8.0-doc/deployer-howto.html).

### Installing with docker
> NOTE: In this section images from torbenbrenner on dockerhub are used. We will transfer these images in the near future to medicalinformatics.

To build MagicPL we need to use a little alternated version of the official [maven image](https://hub.docker.com/_/maven). This version installs git additionally, because otherwise the build would not complete.
```shell script
docker run -it --rm --name magicpl-build -v /$(pwd)/://usr/src/magicpl-build/ -w //usr/src/magicpl-build/ -v magicpl-build-cache://root/.m2 torbenbrenner/maven-git:0.2.0 mvn clean install;
```
This command is nearly equivalent to running the maven build locally. Only the local cache of maven is not stored inside your home directory, but inside a docker volume (magicpl-build-cache).

Now with the exploded WAR beeing located in "./target/paths/", we can build the docker image with following command:
```shell script
docker build -t magicpl:test -f ../../Dockerfile ./target/paths/
```

To run the docker container you can now use:
```shell script
docker run -p 8080:8080 --rm -it magicpl:test
```

The image will also be available at [medicalinformatics/magicpl](https://hub.docker.com/repository/docker/medicalinformatics/magicpl) in Docker Hub.
## Running the tests
The [basic configuration](./src/main/resources/paths.docker.xml), which is also by default used in the docker image, enables a single path for generating controllnumbers from a configured passphrase.
You can test your installation by running following curl command:
```shell
curl --header "Content-Type: application/json" \
     --request POST \
     --data '{"vorname":"Max","nachname":"Mustermann", "geburtsname": "", "geburtstag": 01, "geburtsmonat": 01, "geburtsjahr": 2000, "geburtsort": "Musterstadt"}' \
     http://localhost:8080/paths/getKN
```

## Usage

The image for MagicPL is available on docker hub at [medicalinformatics/magicpl](https://hub.docker.com/repository/docker/medicalinformatics/magicpl)

The basic component image currently supports following environment variables:

|variable name|description|default value|
|-------------|-----------|-------------|
|MAGICPL_API_KEY|the api key used to access paths interface|PATHS_API_KEY|
|MAGICPL_PASSPHRASE|the passphrase used to generate controllnumbers|PATHS_PASSPHRASE|
|PATHS_ALLOWED_ORIGINS|sets the [**Access-Control-Allow-Origin**](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Access-Control-Allow-Origin) Header of the Responses|-|
|PATHS_ALLOWED_CORS_HEADERS|sets the [**Access-Control-Allow-Headers**](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Access-Control-Allow-Headers) Header of the Response. Needs PATHS_ALLOWED_ORIGINS to be defined.|-|

Additionally, some environment variables are available for configuring tomcat:

|variable name|description|default value|
|-------------|-----------|-------------|
|DEBUG|if set to "true", the container will start with remote debugging enabled on port 1099| - |
|TOMCAT_REVERSEPROXY_FQDN|Fully-qualified domain name to be used for access to this container, e.g. patientlist.example.org|-|
|TOMCAT_REVERSEPROXY_PORT|The corresponding port number|80 or 443 according to TOMCAT_REVERSEPROXY_SSL|
|TOMCAT_REVERSEPROXY_SSL|Set to true if container is accessed via SSL/TLS; false otherwise|false|

You can pass your own configuration to the MagicPL image, by configuring a secret named "paths.docker.xml". This secret should refer to a local file
in the configuration format for magicpl. Some examples are available in [resources directory](./src/main/resources).

Here is an example in the docker-compose yaml format:
```yaml
version: "3.7"
services:
  magicpl:
    image: medicalinformatics/magicpl:0.0.1
    environment:
      MAGICPL_API_KEY: pleaseChangeMe
      MAGICPL_PASSPHRASE: pleaseChangeMe
    ports:
      - 8080:8080 
    secrets:
      - paths.docker.xml
secrets:
  paths.docker.xml:
    file: ./src/main/resources/pathsDKTK.xml
```

It is possible to define your own environment variables in your custom configuration, by writting the environment variable name in your configuration.
All custom environment variables must be prefixed with "MAGICPL_". You can review the default [docker configuration](./src/main/resources/paths.docker.xml) for an example. 

For further information on how to configure MagicPL, please refer to the Documentation. For each release in the [releases page](https://github.com/medicalinformatics/magicpl/releases)

## Roadmap

Also refer to [open issues](https://github.com/medicalinformatics/magicpl/issues) for a list of proposed features (and known issues).

## Built With

* [Java](https://www.java.com/de/) - The programming language used
* [Maven](https://maven.apache.org/) - Dependency Management
* [Docker](https://www.docker.com/) - Tool for building and running containers 
* [Docker Hub](https://hub.docker.com/) - Used for automatic builds and delivery of images

## Contributing

Please read [Contributing](./docs/contributing.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/medicalinformatics/magicpl/tags). 

## Authors

* **Andreas Borg** - *Initial work*
* **Christian Koch** - *Initial work*
* **Galina Tremper** - *Initial work*,*Team Member*

* **Torben Brenner** - *Team Member*
* **Moanes Ben Amor** - *Team Member*

See also the list of [contributors](https://github.com/medicalinformatics/magicpl/contributors) who participated in this project.

## License

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[https://www.apache.org/licenses/LICENSE-2.0](https://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

## Acknowledgments

* [Mainzelliste.Client](https://bitbucket.org/medicalinformatics/mainzelliste.client/src/master/)
* [Log4j](https://logging.apache.org/log4j/2.x/)
