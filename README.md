# xmltools - Tooling for managing XML

---

[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/apache/maven.svg?label=License)][license]
[![Maven Central](https://img.shields.io/maven-central/v/de.elomagic/xmltools.svg?label=Maven%20Central)](https://mvnrepository.com/artifact/de.elomagic/xmltools)
[![build workflow](https://github.com/elomagic/xmltools/actions/workflows/maven.yml/badge.svg)](https://github.com/elomagic/xmltools/actions)
[![GitHub issues](https://img.shields.io/github/issues-raw/elomagic/xmltools)](https://github.com/elomagic/xmltools/issues)
[![GitHub tag](https://img.shields.io/github/tag/elomagic/xmltools.svg)](https://GitHub.com/elomagic/xmltools/tags/)
[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://github.com/elomagic/xmltools/graphs/commit-activity)
[![Buymeacoffee](https://badgen.net/badge/icon/buymeacoffee?icon=buymeacoffee&label)](https://www.buymeacoffee.com/elomagic)

## What is this xmltools ? ###

This project is a Java library to ease up the handling of XML files.

* Supports Java 17 or higher

### Current limitations

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
            <artifactId>xmltools</artifactId>
            <version>[1,]</version>
        </dependency>
    </dependencies>
    
    ...
    
</project>
```

## Using the API

### Convert XML to key values

```java
import de.elomagic.xmltools.Xml2KeyValueConverter;

import org.w3c.dom.Document;

import java.nio.file.Paths;

class Sample {

  void example() throws Exception {
    Xml2KeyValueConverter converter = new Xml2KeyValueConverter();
    Map<String, String> map = converter.convert(Paths.get("document.xml"));
  }

}
```

### Convert key values to XML

```java
import de.elomagic.xmltools.KeyValue2XmlConverter;
import org.w3c.dom.Document;
import java.util.Map;

class Sample {

    void example() throws Exception {
        Map<String, String> map = Map.of(
            "de.elomagic.a", "1234",
            "de.elomagic.b", "5678"
        );

        KeyValue2XmlConverter converter = new KeyValue2XmlConverter();
        Document document = converter.convert(map);
    }

}
```

## How to build artefact by myself?

What you need is an installed JDK at least version 17 and [Apache Maven](https://maven.apache.org).
Then clone this project to your local file system and execute `mvn clean install` in the project folder. After successful finish you find 
the artefact in the `target` folder.

Note, latest SNAPSHOTs can also be found under "releases" in the GitHub project page.

## Contributing

Pull requests and stars are always welcome. For bugs and feature requests, [please create an issue](../../issues/new).

### Versioning

Versioning follows the semantic of [Semantic Versioning 2.0.0](https://semver.org/)

### Releasing new version / hotfix (Only for users who have repository permissions)

#### Releasing new version / hotfix

Execute relevant GitHub actions
  
## Who do I talk to? ###

* Repo owner or admin

## License

The xmltools is distributed under [Apache License, Version 2.0][license]

[license]: https://www.apache.org/licenses/LICENSE-2.0