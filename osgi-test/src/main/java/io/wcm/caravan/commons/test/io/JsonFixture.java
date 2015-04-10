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

import java.io.InputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;

/**
 * A wrapper for JSON payload and JSON path operations which create a new {@link JsonFixture} object.
 */
public class JsonFixture {

  private final DocumentContext doc;

  /**
   * @param input The payload input stream
   */
  public JsonFixture(final InputStream input) {
    doc = JsonPath.parse(input);
  }

  private JsonFixture(final DocumentContext context) {
    this.doc = context;
  }

  /**
   * Adds the value to array at the given path.
   * @param path The JSON path
   * @param value The value to add
   * @param filters Optional JSON path filters
   * @return The new JSON fixture
   */
  public JsonFixture add(final String path, final Object value, final Predicate... filters) {
    return new JsonFixture(cloneContext().add(path, value, filters));
  }

  /**
   * Deletes JSON nodes at the given path.
   * @param path The JSON path
   * @param filters Optional JSON path filters
   * @return The new JSON fixture
   */
  public JsonFixture delete(final String path, final Predicate... filters) {
    return new JsonFixture(cloneContext().delete(JsonPath.compile(path, filters)));
  }

  /**
   * Adds or updates the value for a given key on the given path.
   * @param path The JSON path
   * @param key The JSON object key
   * @param value The new value
   * @param filters Optional JSON path filters
   * @return The new JSON fixture
   */
  public JsonFixture put(final String path, final String key, final Object value, final Predicate... filters) {
    return new JsonFixture(cloneContext().put(path, key, value, filters));
  }

  /**
   * Extracts the JSON node elements at the given path.
   * @param path The JSON path
   * @param filters Optional JSON path filters
   * @return The new JSON fixture
   */
  public JsonFixture read(final String path, final Predicate... filters) {
    JsonNode extracted = cloneContext().read(path, JsonNode.class, filters);
    return new JsonFixture(JsonPath.parse(extracted));
  }

  /**
   * Sets a new value at the given path.
   * @param path The JSON path
   * @param value The new value
   * @param filters Optional JSON path filters
   * @return The new JSON fixture
   */
  public JsonFixture set(final String path, final Object value, final Predicate... filters) {
    return new JsonFixture(cloneContext().set(path, value, filters));
  }

  private DocumentContext cloneContext() {
    // TODO: Check for better cloning possibility
    return JsonPath.parse(toJsonNode());
  }

  @Override
  public String toString() {
    return toJsonNode().toString();
  }

  /**
   * @return The JsonNode representation for the payload
   */
  public JsonNode toJsonNode() {
    return (JsonNode)doc.read("$");
  }

  DocumentContext getDocumentContext() {
    return doc;
  }

}
