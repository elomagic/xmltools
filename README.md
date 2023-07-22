# xml-tools - Tooling for managing XML

---

[![build workflow](https://github.com/elomagic/xml-tools/actions/workflows/maven.yml/badge.svg)](https://github.com/elomagic/xml-tools/actions)
[![GitHub issues](https://img.shields.io/github/issues-raw/elomagic/xml-tools)](https://github.com/elomagic/xml-tools/issues)
[![GitHub tag](https://img.shields.io/github/tag/elomagic/xml-tools.svg)](https://GitHub.com/elomagic/xml-tools/tags/)
[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://github.com/elomagic/xml-tools/graphs/commit-activity)
[![Buymeacoffee](https://badgen.net/badge/icon/buymeacoffee?icon=buymeacoffee&label)](https://www.buymeacoffee.com/elomagic)

## What is this xml-tools ? ###

This project is a Java library to ease up the handling of XML files.

* Supports Java 17 or higher

### Limitations

* XML mixed content not supported

## Using the library

### Maven

Add following dependency to your project. Replace the value of the attribute ```version``` according to the used
version in your project.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/maven-v4_0_0.xsd">

    ...

    <dependencies>
        <dependency>
            <groupId>de.elomagic</groupId>
            <artifactId>xml-tools</artifactId>
            <version>[3,]</version>
        </dependency>
    </dependencies>
    
    ...
    
</project>
```

## How to build artefact by myself?

What you need is an installed JDK at least version 17 and [Apache Maven](https://maven.apache.org).
Then clone this project to your local file system and execute `mvn clean install` in the project folder. After successful finish you find 
the artefact in the `target` folder.

## Contributing

Pull requests and stars are always welcome. For bugs and feature requests, [please create an issue](../../issues/new).

### Versioning

Versioning follows the semantic of [Semantic Versioning 2.0.0](https://semver.org/)

### Releasing new version / hotfix (Only for users who have repository permissions)

#### Releasing new version / hotfix

Execute following steps:

* Are the [CHANGELOG.md](https://github.com/elomagic/xml-tools/blob/main/CHANGELOG.md) up to date?
* Check the version in the ```pom.xml``` and [CHANGELOG.md](https://github.com/elomagic/xml-tools/blob/main/CHANGELOG.md)
* Set release date in the [CHANGELOG.md](https://github.com/elomagic/xml-tools/blob/main/CHANGELOG.md)
* Credentials (Nexus, GitHub) up to date?
* Execute Maven release process
  ```bash
  mvn clean install release:prepare -P release
  mvn release:perform -P release
  ```

#### Releasing snapshot package on GitHub 

Execute Maven deploy process

```bash
mvn clean install deploy
```

  
## Who do I talk to? ###

* Repo owner or admin

## License

The xsd-model is distributed under [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
