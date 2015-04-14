wcm.io Caravan HAL
==================

A JSON HAL (Hypertext Application Language) library to document JSON output. Further information is available on the [HAL specification](http://stateless.co/hal_specification.html).

Central component is the HalResource class which wraps HAL operations around a Jackson ObjectNode. It offers methods to manipulate links and embedded resources. These work directly on the passed ObjectNode, hence no serializer is necessary anymore.

Creating a HAL resource
-----------------------

To augment a Jackson ObjectNode it just needs to create a new HalResource and pass the JSON to the constructor. Now it is possible to add links and embedded resources, where embedded resources are HalResources too.

```java
ObjectNode json = ...
ObjectNode childJson = ...
HalResource resource = new HalResource(json)
  .addLinks("self", HalResourceFactory.createLink(uri))
  .addEmbedded("children", new HalResource(childJson));
```
	  
HalResourceFactory and ResourceMapper
-------------------------------------

Creating HAL resources can be very struggling. Thats why the HalResourceFactory can work with any kind of input objects and a ResourceMapper to convert the input objects into another representation and create the corresponding link for them.

```java
Iterable<A> children = ...
ResourceMapper<A> mapper = ...
List<HalResource> embeddedChildren = HalResourceFactory.createEmbeddedResources(children, mapper);
```

HAL helper
----------

The HAL class offers a short and easy way to create/manipulate HAL resources.

```java
Object state = ...
HalResource resource = new HAL(state, uri)
  .link("more", moreUri)
  .embed("parent", parent, mapper)
  .get();

```