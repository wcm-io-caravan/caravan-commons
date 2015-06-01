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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;


public class HalResourceTest {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private ObjectMapper mapper = OBJECT_MAPPER;
  private ObjectNode model;
  private HalResource hal;

  @Before
  public void setUp() throws JsonParseException, JsonMappingException, IOException {
    model = OBJECT_MAPPER.readValue(getClass().getResourceAsStream("/jackson_hal_resource_model.json"), ObjectNode.class);
    hal = new HalResource(model);
  }

  @Test
  public void hasLink_shouldReturnTrueForRelation() {
    assertTrue(hal.hasLink("self"));
  }

  @Test
  public void hasLink_shouldReturnFalseForUnknownRelation() {
    assertFalse(hal.hasLink("self2"));
  }

  @Test
  public void getLinksString_shouldReturnLinksForRelation() throws Exception {
    List<Link> links = hal.getLinks("self");
    assertEquals(1, links.size());
    Link link = links.get(0);
    assertEquals("/", link.getHref());
  }

  @Test
  public void getLinksString_shouldReturnNamedLinksForRelation() throws Exception {
    List<Link> links = hal.getLinks("children");
    assertEquals(2, links.size());
    Link link0 = links.get(0);
    assertEquals("/children1", link0.getHref());
    assertEquals("children 1", link0.getName());
    Link link1 = links.get(1);
    assertEquals("/children2", link1.getHref());
    assertEquals("children 2", link1.getName());
  }


  @Test
  public void getLinksString_shouldReturnEmptyListForUnkwnownRelation() {
    List<Link> links = hal.getLinks("self2");
    assertTrue(links.isEmpty());
  }

  @Test
  public void getLinks_shouldReturnMapOfLinks() throws Exception {
    ListMultimap<String, Link> links = hal.getLinks();
    assertEquals(4, links.size());
    assertEquals(1, links.get("self").size());
    assertEquals("/", links.get("self").get(0).getHref());
  }

  @Test
  public void setLinkStringLink_shouldStoreLinkInJson() throws Exception {
    Link link = new Link(mapper.createObjectNode()).setHref("/new");
    hal.setLink("new", link);
    List<Link> links = hal.getLinks("new");
    assertEquals(link, links.get(0));
    assertEquals(ObjectNode.class, model.at("/_links/new").getClass());
  }

  @Test
  public void setLinkStringLink_shouldOverrideExistingLink() throws Exception {
    Link link = new Link(mapper.createObjectNode()).setHref("/new");
    hal.setLink("children", link);
    List<Link> links = hal.getLinks("children");
    assertEquals(link, links.get(0));
    assertEquals(ObjectNode.class, model.at("/_links/children").getClass());
  }

  @Test
  public void setLinkStringLink_shouldIgnoreNullInput() {
    hal.setLink("new", null);
    assertFalse(hal.getModel().get("_links").has("new"));
  }

  @Test
  public void addLinks_shouldCreateJsonArrayNode() throws Exception {
    Link link1 = new Link(mapper.createObjectNode()).setHref("/new1");
    Link link2 = new Link(mapper.createObjectNode()).setHref("/new2");
    hal.addLinks("new", new Link[] {
        link1, link2
    });
    link1.setName("new 1");
    List<Link> links = hal.getLinks("new");
    assertEquals(link1, links.get(0));
    assertEquals(link2, links.get(1));
    assertEquals(ArrayNode.class, model.at("/_links/new").getClass());
  }

  @Test
  public void addLinks_shouldAppendLinksToExistingJsonArrayNode() {
    Link children3 = new Link(mapper.createObjectNode()).setHref("/children3");
    Link children4 = new Link(mapper.createObjectNode()).setHref("/children4");
    List<Link> children = hal
        .addLinks("children", new Link[] {
            children3, children4
        })
        .getLinks("children");
    assertEquals(4, children.size());
    assertEquals(children3, children.get(2));
    assertEquals(children4, children.get(3));
    assertEquals(ArrayNode.class, model.at("/_links/children").getClass());
  }

  @Test
  public void addLinks_shouldConvertExistingJsonObjectToArrayNode() {
    Link parent2 = new Link(mapper.createObjectNode()).setHref("/parent2");
    List<Link> parents = hal
        .addLinks("parent", parent2)
        .getLinks("parent");
    assertEquals(2, parents.size());
    assertEquals(parent2, parents.get(1));
    assertEquals(ArrayNode.class, model.at("/_links/parent").getClass());
  }

  @Test
  public void removeLinksString_shouldDeleteAllLinksForTheRelation() throws Exception {
    assertFalse(hal.removeLinks("children").hasLink("children"));
    assertEquals(MissingNode.class, model.at("/_links/children").getClass());
  }

  @Test
  public void removeLinkStringInt_shouldDeleteOneLink() throws Exception {
    assertTrue(hal.removeLink("children", 0).hasLink("children"));
    assertEquals(ArrayNode.class, model.at("/_links/children").getClass());
    assertFalse(hal.removeLink("children", 0).hasLink("children"));
    assertEquals(MissingNode.class, model.at("/_links/children").getClass());
  }

  @Test
  public void removeLinks_shouldDeleteAllLinks() throws Exception {
    assertTrue(hal.removeLinks().getLinks().isEmpty());
    assertFalse(model.has("_links"));
    hal.removeLinks();
  }

  @Test
  public void getStateFieldNames_shouldReturnOnlyFieldNamesOfTheState() throws Exception {
    List<String> fieldNames = hal.getStateFieldNames();
    assertEquals(2, fieldNames.size());
    assertTrue(fieldNames.contains("property1"));
    assertTrue(fieldNames.contains("property2"));
  }

  @Test
  public void removeState_shouldOnlyDeleteStateFields() throws Exception {
    hal.removeState();
    assertFalse(hal.getModel().has("property1"));
    assertFalse(hal.getModel().has("property2"));
    assertEquals(4, hal.getLinks().size());
  }

  @Test
  public void hasEmbedded_shouldReturnTrueIfHasEmbeddedResource() {
    assertTrue(hal.hasEmbedded("one"));
  }

  @Test
  public void hasEmbedded_shouldReturnFalseIfHasNoEmbeddedResource() {
    assertFalse(hal.hasEmbedded("unknown"));
  }

  @Test
  public void getEmbedded_shouldReturnEmbeddedResourcesMap() {
    ListMultimap<String, HalResource> resources = hal.getEmbedded();
    assertEquals(3, resources.size());
    assertEquals(1, resources.get("one").size());
    assertEquals("value", resources.get("one").get(0).getModel().get("att").asText());
    assertTrue(resources.containsKey("multiple"));
  }

  @Test
  public void getLink_shouldReturnOneLink() {
    Link link = hal.getLink("children");
    assertEquals("/children1", link.getHref());
  }

  @Test
  public void getLink_shouldReturnNullIfNoLinkExists() {
    assertNull(hal.getLink("unknown"));
  }

  @Test
  public void collectLinks_shouldReturnAllLinks() {
    List<Link> links = hal.collectLinks("self");
    assertEquals(4, links.size());
    assertEquals("/", links.get(0).getHref());
  }

  @Test
  public void getEmbeddedResource_shouldReturnOneEmbeddedResource() {
    HalResource embedded = hal.getEmbeddedResource("multiple");
    assertEquals("/multiple1", embedded.getLink().getHref());
  }

  @Test
  public void getEmbeddedResource_shouldReturnNullForMissingResource() {
    assertNull(hal.getEmbeddedResource("unknown"));
  }

  @Test
  public void getEmbedded_shouldReturnEmbeddedResourcesList() {
    List<HalResource> embedded = hal.getEmbedded("multiple");
    assertEquals(2, embedded.size());
    assertEquals("/multiple1", embedded.get(0).getLink().getHref());
  }

  @Test
  public void addLinksStringIterable_shouldAddLinks() {
    Link children3 = new Link(OBJECT_MAPPER.createObjectNode()).setHref("/children3").setName("children 3");
    Link children4 = new Link(OBJECT_MAPPER.createObjectNode()).setHref("/children4").setName("children 4");
    hal.addLinks("children", Lists.newArrayList(children3, children4));
    JsonNode children = hal.getModel().get("_links").get("children");
    assertEquals(4, children.size());
    assertEquals("/children3", children.get(2).get("href").asText());
  }

  @Test
  public void setEmbedded_shouldSetOneEmbeddedResource() {
    hal.setEmbedded("new", new HalResource(OBJECT_MAPPER.createObjectNode()).setLink("self", new Link(OBJECT_MAPPER.createObjectNode()).setHref("/new")));
    assertEquals("/new", hal.getModel().get("_embedded").get("new").get("_links").get("self").get("href").asText());
  }

  @Test
  public void setEmbedded_shouldIgnoreNullInput() {
    hal.setEmbedded("new", null);
    assertFalse(hal.getModel().get("_embedded").has("new"));
  }

  @Test
  public void addEmbeddedStringArray_shouldAddEmbeddedResources() {
    HalResource embedded1 = new HalResource(OBJECT_MAPPER.createObjectNode()).setLink(new Link(OBJECT_MAPPER.createObjectNode()).setHref("/new1"));
    HalResource embedded2 = new HalResource(OBJECT_MAPPER.createObjectNode()).setLink(new Link(OBJECT_MAPPER.createObjectNode()).setHref("/new2"));
    hal.addEmbedded("new", embedded1, embedded2);
    JsonNode embedded = hal.getModel().get("_embedded").get("new");
    assertEquals(2, embedded.size());
    assertEquals("/new1", embedded.get(0).get("_links").get("self").get("href").asText());
  }

  @Test
  public void addEmbeddedStringArray_shouldHandleEmptyArray() {
    hal.addEmbedded("new");
    assertFalse(hal.getModel().get("_embedded").has("new"));
  }

  @Test
  public void addEmbeddedStringIterable_shouldAddEmbeddedResources() {
    HalResource embedded1 = new HalResource(OBJECT_MAPPER.createObjectNode()).setLink(new Link(OBJECT_MAPPER.createObjectNode()).setHref("/new1"));
    HalResource embedded2 = new HalResource(OBJECT_MAPPER.createObjectNode()).setLink(new Link(OBJECT_MAPPER.createObjectNode()).setHref("/new2"));
    hal.addEmbedded("new", Lists.newArrayList(embedded1, embedded2));
    JsonNode embedded = hal.getModel().get("_embedded").get("new");
    assertEquals(2, embedded.size());
    assertEquals("/new1", embedded.get(0).get("_links").get("self").get("href").asText());
  }

  @Test
  public void addEmbeddedStringIterable_shouldHandleEmptyIterable() {
    hal.addEmbedded("new", Collections.emptyList());
    assertFalse(hal.getModel().get("_embedded").has("new"));
  }

  @Test
  public void removeEmbeddedString_shouldRemoveAllEmbeddedResources() {
    hal.removeEmbedded("children");
    assertFalse(hal.getModel().get("_embedded").has("children"));
  }

  @Test
  public void removeEmbeddedString_shouldIgnoreUnknownRelation() {
    hal.removeEmbedded("unknown");
  }

  @Test
  public void removeEmbeddedStringInt_shouldRemoveOneEmbeddedResource() {
    hal.removeEmbedded("multiple", 0);
    JsonNode embedded = hal.getModel().get("_embedded").get("multiple");
    assertEquals(1, embedded.size());
    assertEquals("/multiple2", embedded.get(0).get("_links").get("self").get("href").asText());
  }

  @Test
  public void removeEmbeddedStringInt_shouldignoreTooHighIndex() {
    hal.removeEmbedded("multiple", 10);
    JsonNode embedded = hal.getModel().get("_embedded").get("multiple");
    assertEquals(2, embedded.size());
  }

  @Test
  public void removeEmbeddedStringInt_shouldignoreUnknownRelation() {
    hal.removeEmbedded("unknown", 0);
  }

  @Test
  public void removeEmbedded_shouldRemoveAllEmbeddedResources() {
    hal.removeEmbedded();
    assertFalse(hal.getModel().has("_embedded"));
  }

  @Test
  public void renameEmbedded_shouldChangeTheRelationOfAnEmbeddedResource() {
    hal.renameEmbedded("multiple", "multiple2");
    assertFalse(hal.getModel().get("_embedded").has("multiple"));
    assertTrue(hal.getModel().get("_embedded").has("multiple2"));
  }

  @Test
  public void renameEmbedded_shouldIgnoreMissingRelation() {
    hal.renameEmbedded("unknown", "unknown2");
    assertFalse(hal.getModel().get("_embedded").has("unknown2"));
  }

  @Test
  public void addState() {
    ObjectNode state = OBJECT_MAPPER.createObjectNode().put("property3", "value3");
    hal.addState(state);
    assertEquals("value3", hal.getModel().get("property3").asText());
  }

}
