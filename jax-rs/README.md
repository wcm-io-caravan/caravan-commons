wcm.io Caravan OSGi JAX-RS Integration
======================================

Provides a [JAX-RS](https://jsr311.java.net/) integration based on [Jersey](https://jersey.java.net/) with OSGi. The implementation uses an OSGI Extender pattern. If a JAX-RS integration is configured for a bundle a JAX-RS application is created automatically and registered in a Servlet container to the Apache Felix HTTP whiteboard service. This is done transparently to the bundle, only the application path has to be configured and each JAX-RS components has to be registered as an OSGi service using a special marker interface.


Configure Application Path
--------------------------

To enable the JAX-RS integration for your OSGi bundle add a new instruction `Caravan-JaxRs-ApplicationPath` to the OSGi manifest. The best way for this is using the maven-bundle-plugin. Example:

```xml
<build>
  <plugins>

    <plugin>
      <groupId>org.apache.felix</groupId>
      <artifactId>maven-bundle-plugin</artifactId>
      <configuration>
        <instructions>
          <Caravan-JaxRs-ApplicationPath>/service/myJaxRsService</Caravan-JaxRs-ApplicationPath>
        </instructions>
      </configuration>
    </plugin>

  </plugins>
</build>
```


Register JAX-RS component
-------------------------

If you want to register a JAX-RS component to the JAX-RS application created for the bundle you have to define it as OSGi Service implementing the interface `io.wcm.caravan.commons.jaxrs.JaxRsComponent`. Example:

```java
@Component(immediate = true)
@Service(JaxRsComponent.class)
@Path("/{tenantId}/index")
public class HalEntryPoint implements JaxRsComponent {

  @GET
  public Response index(@PathParam("tenantId") String tenantId) {
    // your code...
  }

}
```

The application path must not to be added to the `@Path` annotation, it is added automatically. This example services can be reached via the URL `/service/myJaxRsService/tenant123/index`.


Register global JAX-RS components
---------------------------------

If you want to register a JAX-RS component for all JAX-RS application in the OSGi instance and not only for the current bundle you can define a OSGi factory service and set the special property `caravan.jaxrs.global.factory` to true. Example:

```java
@Component(factory = JaxRsComponent.GLOBAL_COMPONENT_FACTORY)
@Service(JaxRsComponent.class)
@Provider
public class StatusCodeAwareExceptionMapper implements ExceptionMapper<RuntimeException>, JaxRsComponent {

  @Override
  public Response toResponse(RuntimeException ex) {
    // your code...
  }

}

```


Dependencies
------------

* Jersey has to be deployed as additional bundle, wcm.io provides a wrapped version: http://repo1.maven.org/maven2/io/wcm/osgi/wrapper/io.wcm.osgi.wrapper.jersey/


Alternatives
------------

* [OSGi JAX-RS Connector](https://github.com/hstaudacher/osgi-jax-rs-connector) - basically similar concept as the Caravan JAX-RS integrations, but creates always one single big Jersey Application for all bundles which breaks isolation between the bundles e.g. concerning @Provider JAX-RS components. Besides this the implementation is very stable and well maintained, and does not only support publishing but also consuming JAX-RS services within OSGi. This comes at a price of a quite complex implementation and a lot of dependencies.
* [JAX-RS Extender Bundle for OSGi](https://github.com/njbartlett/jaxrs-osgi-extender) - quite old implementation based on the OSGi extender pattern. Builds one JAX-RS Application per module, but the JAX-RS components are not OSGi components, so OSGI dependency injection is not possible.
