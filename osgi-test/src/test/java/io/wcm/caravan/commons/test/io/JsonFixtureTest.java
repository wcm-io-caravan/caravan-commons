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
package io.wcm.caravan.commons.test.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.wcm.caravan.commons.test.TestConfiguration;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;


public class JsonFixtureTest {

  private JsonFixture underTest;

  @Before
  public void setUp() {
    TestConfiguration.init();
    underTest = new JsonFixture(getClass().getResourceAsStream("/example.json"));
  }

  @Test
  public void testAdd() throws Exception {
    JsonFixture newFixture = underTest.add("$.store.book", new ObjectMapper().createObjectNode().put("key", "value"));
    assertEquals(4, underTest.toJsonNode().at("/store/book").size());
    JsonNode books = newFixture.toJsonNode().at("/store/book");
    assertEquals(5, books.size());
    assertEquals("value", books.get(4).get("key").asText());
  }

  @Test
  public void testDelete() throws Exception {
    assertEquals(4, underTest.toJsonNode().at("/store/book").size());
    JsonFixture newFixture = underTest.delete("$.store.book.[1]");
    assertEquals(4, underTest.toJsonNode().at("/store/book").size());
    assertEquals(3, newFixture.toJsonNode().at("/store/book").size());
  }

  @Test
  public void testPut() throws Exception {
    JsonFixture newFixture = underTest.put("$.store", "key", new TextNode("value"));
    assertFalse(underTest.toJsonNode().has("key"));
    assertEquals("value", newFixture.toJsonNode().at("/store/key").asText());
  }

  @Test
  public void testRead() throws Exception {
    JsonFixture newFixture = underTest.read("$.store.book");
    assertTrue(underTest.toJsonNode().isObject());
    JsonNode books = newFixture.toJsonNode();
    assertTrue(books.isArray());
    assertEquals(4, books.size());
  }

  @Test
  public void testSet() throws Exception {
    JsonFixture newFixture = underTest.set("$.store.bicycle", new TextNode("value"));
    assertTrue(underTest.toJsonNode().at("/store/bicycle").isObject());
    assertEquals("value", newFixture.toJsonNode().at("/store/bicycle").asText());
  }


}
