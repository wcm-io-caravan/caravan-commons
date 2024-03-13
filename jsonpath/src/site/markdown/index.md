## About Commons JsonPath

Provides preconfigured [JsonPath][jsonpath] implementation based on [Jayway JsonPath][jayway-jsonpath].

[![Maven Central](https://img.shields.io/maven-central/v/io.wcm.caravan/io.wcm.caravan.commons.jsonpath)](https://repo1.maven.org/maven2/io/wcm/caravan/io.wcm.caravan.commons.jsonpath/)


### Documentation

* [API documentation][jayway-jsonpath-apidocs]
* [Changelog][changelog]


[changelog]: changes-report.html


### Overview

* Embeds JsonPath incl. dependencies
* Exports public JsonPath API in OSGi
* Preconfigured to use `JacksonJsonNodeJsonProvider` and `JacksonMappingProvider` with [Jackson][jackson]
* Also compatible with Adobe Experience Manager (AEM 6.3-6.5, AEMaaCS)


[jsonpath]: http://goessner.net/articles/JsonPath/
[jayway-jsonpath]: https://github.com/jayway/JsonPath
[jayway-jsonpath-apidocs]: http://www.javadoc.io/doc/com.jayway.jsonpath/json-path
[jackson]: https://github.com/FasterXML/jackson
