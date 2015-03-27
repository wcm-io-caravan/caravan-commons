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

import io.wcm.caravan.commons.stream.Streams;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableListMultimap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Bean representation of a HAL resource.
 */
public class HalResource implements HalObject {

  /**
   * The mime content type
   */
  public static final String CONTENT_TYPE = "application/hal+json";

  private enum Type {
    LINKS("_links"), EMBEDDED("_embedded");


    private final String value;

    Type(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  private final ObjectNode model;

  /**
   * @param model JSON model
   */
  public HalResource(ObjectNode model) {
    this.model = model;
  }

  @Override
  public ObjectNode getModel() {
    return model;
  }

  /**
   * @param relation Link relation
   * @return True if has link for the given relation
   */
  public boolean hasLink(String relation) {
    return hasResource(Type.LINKS, relation);
  }

  /**
   * @param relation Embedded resource relation
   * @return True if has embedded resource for the given relation
   */
  public boolean hasEmbedded(String relation) {
    return hasResource(Type.EMBEDDED, relation);
  }

  private boolean hasResource(Type type, String relation) {
    return !model.at("/" + type + "/" + relation).isMissingNode();
  }

  /**
   * @return Self link for the resource. Can be null
   */
  public Link getLink() {
    return hasLink("self") ? getLinks("self").get(0) : null;
  }

  /**
   * @return All links
   */
  public ImmutableListMultimap<String, Link> getLinks() {
    return getResources(Link.class, Type.LINKS);
  }

  /**
   * @return All embedded resources
   */
  public ImmutableListMultimap<String, HalResource> getEmbedded() {
    return getResources(HalResource.class, Type.EMBEDDED);
  }

  private <X extends HalObject> ImmutableListMultimap<String, X> getResources(Class<X> clazz, Type type) {
    if (!model.has(type.toString())) {
      return ImmutableListMultimap.of();
    }
    Builder<String, X> links = ImmutableListMultimap.builder();
    Iterable<String> iterable = Lists.newArrayList(model.get(type.toString()).fieldNames());
    Streams.of(iterable).forEach(field -> links.putAll(field, getResources(clazz, type, field)));
    return links.build();
  }

  /**
   * @param relation Link relation
   * @return All links for the given relation
   */
  public ImmutableList<Link> getLinks(String relation) {
    return getResources(Link.class, Type.LINKS, relation);
  }

  /**
   * @param relation Embedded resource relation
   * @return All embedded resources for the given relation
   */
  public ImmutableList<HalResource> getEmbedded(String relation) {
    return getResources(HalResource.class, Type.EMBEDDED, relation);
  }

  private <X extends HalObject> ImmutableList<X> getResources(Class<X> clazz, Type type, String relation) {
    if (!hasResource(type, relation)) {
      return ImmutableList.of();
    }
    JsonNode resources = model.at("/" + type + "/" + relation);
    try {
      Constructor<X> constructor = clazz.getConstructor(ObjectNode.class);
      if (resources instanceof ObjectNode) {
        return ImmutableList.of(constructor.newInstance(resources));
      }
      else {
        ImmutableList.Builder<X> result = ImmutableList.builder();
        for (JsonNode resource : resources) {
          result.add(constructor.newInstance(resource));
        }
        return result.build();
      }
    }
    catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Adds links for the given relation
   * @param relation Link relation
   * @param links Links to add
   * @return HAL resource
   */
  public HalResource addLinks(String relation, Link... links) {
    return addResources(Type.LINKS, relation, links);
  }

  /**
   * Adds links for the given relation
   * @param relation Link relation
   * @param links Links to add
   * @return HAL resource
   */
  public HalResource addLinks(String relation, Iterable<Link> links) {
    return addLinks(relation, Iterables.toArray(links, Link.class));
  }

  /**
   * Embed resources for the given relation
   * @param relation Embedded resource relation
   * @param resources Resources to embed
   * @return HAL resource
   */
  public HalResource addEmbedded(String relation, HalResource... resources) {
    return addResources(Type.EMBEDDED, relation, resources);
  }

  /**
   * Embed resources for the given relation
   * @param relation Embedded resource relation
   * @param resources Resources to embed
   * @return HAL resource
   */
  public HalResource addEmbedded(String relation, Iterable<HalResource> resources) {
    return addEmbedded(relation, Iterables.toArray(resources, HalResource.class));
  }

  private <X extends HalObject> HalResource addResources(Type type, String relation, X[] newResources) {
    if (newResources.length == 0) {
      return this;
    }
    ObjectNode resources = model.has(type.toString()) ? (ObjectNode)model.get(type.toString()) : model.putObject(type.toString());
    if (!hasResource(type, relation)) {
      if (!resources.has(relation) && newResources.length == 1) {
        resources.set(relation, newResources[0].getModel());
      }
      else if (!resources.has(relation) && newResources.length > 1) {
        ArrayNode container = resources.putArray(relation);
        Streams.of(newResources).forEach(link -> container.add(link.getModel()));
      }
    }
    else {
      JsonNode relationLinks = resources.get(relation);
      ArrayNode container = relationLinks instanceof ArrayNode ? (ArrayNode)relationLinks : resources.putArray(relation).add(relationLinks);
      Streams.of(newResources).forEach(link -> container.add(link.getModel()));
    }
    return this;
  }

  /**
   * Removes all links for the given relation.
   * @param relation Link relation
   * @return HAL resource
   */
  public HalResource removeLinks(String relation) {
    return removeResource(Type.LINKS, relation);
  }

  /**
   * Removes all embedded resources for the given relation.
   * @param relation Embedded resource relation
   * @return HAL resource
   */
  public HalResource removeEmbedded(String relation) {
    return removeResource(Type.EMBEDDED, relation);
  }

  private HalResource removeResource(Type type, String relation) {
    if (hasResource(type, relation)) {
      ((ObjectNode)model.get(type.toString())).remove(relation);
    }
    return this;
  }

  /**
   * Removes one link for the given relation and index.
   * @param relation Link relation
   * @param index Array index
   * @return HAL resource
   */
  public HalResource removeLink(String relation, int index) {
    return removeResource(Type.LINKS, relation, index);
  }

  /**
   * Removes one embedded resource for the given relation and index.
   * @param relation Embedded resource relation
   * @param index Array index
   * @return HAL resource
   */
  public HalResource removeEmbedded(String relation, int index) {
    return removeResource(Type.EMBEDDED, relation, index);
  }

  private HalResource removeResource(Type type, String relation, int index) {
    if (hasResource(type, relation)) {
      JsonNode resources = model.at("/" + type + "/" + relation);
      if (resources instanceof ObjectNode || resources.size() <= 1) {
        ((ObjectNode)model.get(type.toString())).remove(relation);
      }
      else {
        ((ArrayNode)resources).remove(index);
        if (resources.size() == 1) {
          ((ObjectNode)model.get(type.toString())).set(relation, resources.get(0));
        }
      }
    }
    return this;
  }

  /**
   * Removes all links.
   * @return HAL resource
   */
  public HalResource removeLinks() {
    return removeResources(Type.LINKS);
  }

  /**
   * Removes all embedded resources.
   * @return HAL resource
   */
  public HalResource removeEmbedded() {
    return removeResources(Type.EMBEDDED);
  }

  private HalResource removeResources(Type type) {
    model.remove(type.toString());
    return this;
  }

  /**
   * Adds state to the resource.
   * @param state Resource state
   * @return HAL resource
   */
  public HalResource addState(ObjectNode state) {
    Iterable<Entry<String, JsonNode>> iterable = Lists.newArrayList(state.fields());
    Streams.of(iterable).forEach(entry -> model.set(entry.getKey(), entry.getValue()));
    return this;
  }

}
