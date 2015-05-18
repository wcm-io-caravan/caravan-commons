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
package io.wcm.caravan.commons.hal.resource;

import io.wcm.caravan.commons.stream.Collectors;
import io.wcm.caravan.commons.stream.Streams;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.osgi.annotation.versioning.ProviderType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableListMultimap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;

/**
 * Bean representation of a HAL resource.
 */
@ProviderType
public final class HalResource implements HalObject {

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
    return getLink("self");
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
    Builder<String, X> resources = ImmutableListMultimap.builder();
    Iterable<String> iterable = Lists.newArrayList(model.get(type.toString()).fieldNames());
    Streams.of(iterable).forEach(field -> resources.putAll(field, getResources(clazz, type, field)));
    return resources.build();
  }

  /**
   * @param relation Link relation
   * @return Link for the given relation
   */
  public Link getLink(String relation) {
    return hasLink(relation) ? getLinks(relation).get(0) : null;
  }

  /**
   * @param relation Link relation
   * @return All links for the given relation
   */
  public ImmutableList<Link> getLinks(String relation) {
    return getResources(Link.class, Type.LINKS, relation);
  }

  /**
   * recursively collects links within this resource and all embedded resources
   * @param rel the relation your interested in
   * @return a list of all links
   */
  public List<Link> collectLinks(String rel) {

    List<Link> links = Lists.newArrayList(getLinks(rel));
    List<Link> embeddedLinks = Streams.of(getEmbedded().values())
        .flatMap(embedded -> Streams.of(embedded.collectLinks(rel)))
        .collect(Collectors.toList());
    links.addAll(embeddedLinks);
    return links;

  }

  /**
   * @param relation Embedded resource relation
   * @return Embedded resources for the given relation
   */
  public HalResource getEmbeddedResource(String relation) {
    return hasEmbedded(relation) ? getEmbedded(relation).get(0) : null;
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
          if (resource instanceof ObjectNode) {
            result.add(constructor.newInstance(resource));
          }
        }
        return result.build();
      }
    }
    catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Sets link for the {@code self} relation. Overwrites existing one.
   * @param link Link to set
   * @return HAL resource
   */
  public HalResource setLink(Link link) {
    return setLink("self", link);
  }

  /**
   * Sets link for the given relation. Overwrites existing one.
   * @param relation Link relation
   * @param link Link to add
   * @return HAL resource
   */
  public HalResource setLink(String relation, Link link) {
    return addResources(Type.LINKS, relation, false, new Link[] {
        link
    });
  }

  /**
   * Adds links for the given relation
   * @param relation Link relation
   * @param links Links to add
   * @return HAL resource
   */
  public HalResource addLinks(String relation, Link... links) {
    return addResources(Type.LINKS, relation, true, links);
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
   * Embed resource for the given relation. Overwrites existing one.
   * @param relation Embedded resource relation
   * @param resource Resource to embed
   * @return HAL resource
   */
  public HalResource setEmbedded(String relation, HalResource resource) {
    return addResources(Type.EMBEDDED, relation, false, new HalResource[] {
        resource
    });
  }

  /**
   * Embed resources for the given relation
   * @param relation Embedded resource relation
   * @param resources Resources to embed
   * @return HAL resource
   */
  public HalResource addEmbedded(String relation, HalResource... resources) {
    return addResources(Type.EMBEDDED, relation, true, resources);
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

  private <X extends HalObject> HalResource addResources(Type type, String relation, boolean asArray, X[] newResources) {
    if (newResources.length == 0) {
      return this;
    }
    ObjectNode resources = model.has(type.toString()) ? (ObjectNode)model.get(type.toString()) : model.putObject(type.toString());

    if (asArray) {
      ArrayNode container = getArrayNodeContainer(type, relation, resources);
      Streams.of(newResources).forEach(link -> container.add(link.getModel()));
    }
    else {
      resources.set(relation, newResources[0].getModel());
    }
    return this;
  }

  private ArrayNode getArrayNodeContainer(Type type, String relation, ObjectNode resources) {
    if (hasResource(type, relation)) {
      if (resources.get(relation).isArray()) {
        return (ArrayNode)resources.get(relation);
      }
      else {
        JsonNode temp = resources.get(relation);
        return resources.putArray(relation).add(temp);
      }
    }
    else {
      return resources.putArray(relation);
    }
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

  /**
   * Changes the rel of embedded resources
   * @param relToRename the rel that you want to change
   * @param newRel the new rel for all embedded items
   */
  public void renameEmbedded(String relToRename, String newRel) {
    List<HalResource> resourcesToRename = new ArrayList<>();
    resourcesToRename.addAll(getEmbedded(relToRename));
    removeEmbedded(relToRename);
    addEmbedded(newRel, resourcesToRename);
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

  /**
   * @return JSON field names for the state object
   */
  public List<String> getStateFieldNames() {
    UnmodifiableIterator<String> filtered = Iterators.filter(model.fieldNames(), new Predicate<String>() {

      @Override
      public boolean apply(String input) {
        return !"_links".equals(input) && !"_embedded".equals(input);
      }
    });
    return Lists.newArrayList(filtered);
  }

  /**
   * Removes all state attributes
   * @return HAL resource
   */
  public HalResource removeState() {
    Streams.of(getStateFieldNames()).forEach(field -> model.remove(field));
    return this;
  }

}
