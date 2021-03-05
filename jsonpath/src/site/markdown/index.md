## About Commons JsonPath

Provides preconfigured [JsonPath][jsonpath] implementation based on [Jayway JsonPath][jayway-jsonpath].

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm.caravan/io.wcm.caravan.commons.jsonpath/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm.caravan/io.wcm.caravan.commons.jsonpath)


### Documentation

* [Changelog][changelog]


[changelog]: changes-report.html


### Overview

* Embeds JsonPath incl. dependencies
* Exports public JsonPath API in OSGi
* Preconfigured to use `JacksonJsonNodeJsonProvider` and `JacksonMappingProvider` with [Jackson][jackson]
* Also compatible with Adobe Experience Manager (AEM 6.3-6.5, AEMaaCS)


[jsonpath]: http://goessner.net/articles/JsonPath/
[jayway-jsonpath]: https://github.com/jayway/JsonPath
[jackson]: https://github.com/FasterXML/jackson
