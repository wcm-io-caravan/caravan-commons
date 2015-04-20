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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;


public class HalResourceTest {

  private ObjectMapper mapper = new ObjectMapper();
  private ObjectNode model;
  private HalResource hal;

  @Before
  public void setUp() throws JsonParseException, JsonMappingException, IOException {
    model = new ObjectMapper().readValue(getClass().getResourceAsStream("/jackson_hal_resource_model.json"), ObjectNode.class);
    hal = new HalResource(model);
  }

  @Test
  public void test_hasLink() {
    assertTrue(hal.hasLink("self"));
    assertFalse(hal.hasLink("self2"));
  }

  @Test
  public void test_getLinks_relation() throws Exception {
    List<Link> links = hal.getLinks("self");
    assertEquals(1, links.size());
    Link link = links.get(0);
    assertEquals("/", link.getHref());
  }

  @Test
  public void test_getLinks_relationMissing() {
    ImmutableList<Link> links = hal.getLinks("self2");
    assertTrue(links.isEmpty());
  }

  @Test
  public void test_getLinks() throws Exception {
    ListMultimap<String, Link> links = hal.getLinks();
    assertEquals(4, links.size());
    assertEquals(1, links.get("self").size());
    assertEquals("/", links.get("self").get(0).getHref());
  }

  @Test
  public void test_setLink() throws Exception {
    Link link = new Link(mapper.createObjectNode()).setHref("/new");
    hal.setLink("new", link);
    List<Link> links = hal.getLinks("new");
    assertEquals(link, links.get(0));
    assertEquals(ObjectNode.class, model.at("/_links/new").getClass());
  }

  @Test
  public void test_setLink_override() throws Exception {
    Link link = new Link(mapper.createObjectNode()).setHref("/new");
    hal.setLink("children", link);
    List<Link> links = hal.getLinks("children");
    assertEquals(link, links.get(0));
    assertEquals(ObjectNode.class, model.at("/_links/children").getClass());
  }

  @Test
  public void test_addLinks_new() throws Exception {
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
  public void test_addLinks_existing() {
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
  public void test_addLinks_existingOne() {
    Link parent2 = new Link(mapper.createObjectNode()).setHref("/parent2");
    List<Link> parents = hal
        .addLinks("parent", parent2)
        .getLinks("parent");
    assertEquals(2, parents.size());
    assertEquals(parent2, parents.get(1));
    assertEquals(ArrayNode.class, model.at("/_links/parent").getClass());
  }

  @Test
  public void test_removeLinks_relation() throws Exception {
    assertFalse(hal.removeLinks("children").hasLink("children"));
    assertEquals(MissingNode.class, model.at("/_links/children").getClass());
  }

  @Test
  public void test_removeLink_relationIndex() throws Exception {
    assertTrue(hal.removeLink("children", 0).hasLink("children"));
    assertEquals(ArrayNode.class, model.at("/_links/children").getClass());
    assertFalse(hal.removeLink("children", 0).hasLink("children"));
    assertEquals(MissingNode.class, model.at("/_links/children").getClass());
  }

  @Test
  public void test_removeLinks() throws Exception {
    assertTrue(hal.removeLinks().getLinks().isEmpty());
    assertFalse(model.has("_links"));
    hal.removeLinks();
  }

}
