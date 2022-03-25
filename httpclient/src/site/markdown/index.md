## About Commons HTTP Client

Provides preconfigured [Apache HTTP Client](http://hc.apache.org/) instances.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm.caravan/io.wcm.caravan.commons.httpclient/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm.caravan/io.wcm.caravan.commons.httpclient)


### Documentation

* [API documentation][apidocs]
* [Changelog][changelog]


[apidocs]: apidocs/
[changelog]: changes-report.html


### Overview

* Allows to configure pooled HTTP client instance for groups of host names identified by patterns
* Supports configuration of timeouts, max. connections, proxy and proxy authentication
* Supports mutual authentication with SSL client certificates
* Also compatible with Adobe Experience Manager (AEM 6.3-6.5, AEMaaCS)

This OSGi Bundle depends on further bundles (or higher versions):

* https://repo1.maven.org/maven2/org/apache/httpcomponents/httpcore-osgi/4.3.2/
* https://repo1.maven.org/maven2/org/apache/httpcomponents/httpclient-osgi/4.3.4/
