/* Copyright (c) pro!vision GmbH. All rights reserved. */
package io.wcm.caravan.io.hal;

import io.wcm.caravan.io.hal.domain.EmbeddedResource;
import io.wcm.caravan.io.hal.domain.HalResource;
import io.wcm.caravan.io.hal.domain.Link;
import io.wcm.caravan.io.hal.mapper.ResourceMapper;

/**
 * Short named helper for HAL resources.
 */
public class HAL {

  private final HalResource instance;

  /**
   * @see HalResourceFactory#createResource(String)
   * @param href Link HREF
   */
  public HAL(String href) {
    instance = HalResourceFactory.createResource(href);
  }

  /**
   * @see HalResourceFactory#createResource(Object, String)
   * @param state Resource state
   * @param href Link HREF
   */
  public HAL(Object state, String href) {
    instance = HalResourceFactory.createResource(state, href);
  }

  /**
   * @see HalResourceFactory#createResource(Object, ResourceMapper)
   * @param input Resource pre-mapped state
   * @param mapper Resource state mapper
   */
  public HAL(Object input, ResourceMapper<?, ?> mapper) {
    instance = HalResourceFactory.createResource(input, mapper);
  }

  /**
   * @see HalResourceFactory#createEmbeddedResource(Object, ResourceMapper)
   * @see HalResource#setEmbeddedResource(String, EmbeddedResource)
   * @param name Embedded resource name
   * @param input Embedded resource pre-mapped state
   * @param mapper Embedded resource state mapper
   * @return Helper
   */
  public HAL embed(String name, Object input, ResourceMapper<?, ?> mapper) {
    EmbeddedResource embeddedResource = HalResourceFactory.createEmbeddedResource(input, mapper);
    instance.setEmbeddedResource(name, embeddedResource);
    return this;
  }

  /**
   * @see HalResourceFactory#createEmbeddedResources(Iterable, ResourceMapper)
   * @see HalResource#setEmbeddedResource(String, EmbeddedResource)
   * @param name Embedded resources name
   * @param inputs Embedded resources pre-mapped state
   * @param mapper Embedded resources state mapper
   * @return Helper
   */
  public HAL embedAll(String name, Iterable<?> inputs, ResourceMapper<?, ?> mapper) {
    EmbeddedResource embeddedResource = HalResourceFactory.createEmbeddedResources(inputs, mapper);
    instance.setEmbeddedResource(name, embeddedResource);
    return this;
  }

  /**
   * @see HalResource#setLink(String, Link)
   * @param relation Link relation
   * @param href Link HREF
   * @return Helper
   */
  public HAL link(String relation, String href) {
    instance.setLink(relation, new Link(href));
    return this;
  }

  /**
   * @return The HAL resource
   */
  public HalResource get() {
    return instance;
  }

}
