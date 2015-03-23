/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2014 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.caravan.commons.hal.domain;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

/**
 * Bean representation of a HAL resource.
 */
public class HalResource {

  /**
   * The mime content type
   */
  public static final String CONTENT_TYPE = "application/hal+json";

  private Object state;
  private final ListMultimap<String, Link> allLinks = ArrayListMultimap.create();
  private final ListMultimap<String, HalResource> embeddedResources = ArrayListMultimap.create();
  private final List<CompactUri> curies = Lists.newArrayList();

  /**
   * Sets the payload for the resource. Can be of any type.
   * @param payload The state of the resource
   * @return The HAL resource
   */
  public HalResource setState(final Object payload) {
    this.state = payload;
    return this;
  }

  /**
   * Sets a link for the resource identified by the relation
   * @param relation The link relation
   * @param link The resource link
   * @return The HAL resource
   */
  public HalResource setLink(final String relation, final Link link) {
    return setLinks(relation, ImmutableList.of(link));
  }

  /**
   * Sets links for the resource identified by the relation
   * @param relation The link relation
   * @param links The resource links
   * @return The HAL resource
   */
  public HalResource setLinks(final String relation, final List<Link> links) {
    allLinks.putAll(relation, links);
    return this;
  }

  /**
   * Adds an embedded resource to the HAL resource. Existing embedded resource(s) with same name get overwritten.
   * @param name The name of the embedded resource
   * @param resource The embedded resource
   * @return The HAL resource
   */
  public HalResource setEmbeddedResource(final String name, final HalResource resource) {
    embeddedResources.put(name, resource);
    return this;
  }

  /**
   * Adds embedded resources to the HAL resource. Existing embedded resource(s) with same name get overwritten.
   * @param name The name of the embedded resources
   * @param resources The embedded resources
   * @return The HAL resource
   */
  public HalResource setEmbeddedResources(String name, List<HalResource> resources) {
    embeddedResources.putAll(name, resources);
    return this;
  }

  /**
   * Adds a compact URI for documentation to the HAL resource.
   * @param curi The compact URI
   * @return The HAL resource
   */
  public HalResource addCuri(final CompactUri curi) {
    curies.add(curi);
    return this;
  }

  /**
   * @return the links
   */
  public ListMultimap<String, Link> getLinks() {
    return allLinks;
  }

  /**
   * @return the embeddedResources
   */
  public ListMultimap<String, HalResource> getEmbeddedResources() {
    return embeddedResources;
  }

  /**
   * @return the state
   */
  public Object getState() {
    return this.state;
  }

  /**
   * @return the curies
   */
  public Collection<CompactUri> getCuries() {
    return curies;
  }

  /**
   * @return The link for the resource
   */
  public Link getResourceLink() {
    return allLinks.containsKey("self") && allLinks.get("self") != null && !allLinks.get("self").isEmpty() ? allLinks.get("self").get(0) : null;
  }

}
