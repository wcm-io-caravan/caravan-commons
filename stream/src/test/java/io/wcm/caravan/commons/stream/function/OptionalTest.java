/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2015 wcm.io
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
package io.wcm.caravan.commons.stream.function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;

import org.junit.Test;

@SuppressWarnings("deprecation")
public class OptionalTest {

  @Test
  public void testGet() {
    Optional<String> underTest = Optional.of("abc");
    assertEquals("abc", underTest.get());
  }

  @Test(expected = NoSuchElementException.class)
  public void testGetEmpty() {
    Optional<String> underTest = Optional.empty();
    underTest.get();
  }

  @Test
  public void testIsPresent() {
    assertTrue(Optional.of("abc").isPresent());
    assertFalse(Optional.empty().isPresent());
  }

  @Test
  public void testOrElse() {
    assertEquals("abc", Optional.of("abc").orElse("def"));
    assertEquals("def", Optional.empty().orElse("def"));
  }

  @Test
  public void testOrElseGet() {
    assertEquals("abc", Optional.of("abc").orElseGet(() -> "def"));
    assertEquals("def", Optional.empty().orElseGet(() -> "def"));
  }

  @Test
  public void testOrElseThrow() {
    assertEquals("abc", Optional.of("abc").orElseThrow(() -> new IllegalArgumentException()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testOrElseThrowEmpty() {
    Optional.empty().orElseThrow(() -> new IllegalArgumentException());
  }

}
