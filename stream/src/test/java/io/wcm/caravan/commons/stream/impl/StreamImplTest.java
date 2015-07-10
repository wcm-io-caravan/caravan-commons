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
package io.wcm.caravan.commons.stream.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import io.wcm.caravan.commons.stream.Collectors;
import io.wcm.caravan.commons.stream.Stream;
import io.wcm.caravan.commons.stream.Streams;
import io.wcm.caravan.commons.stream.function.Consumer;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

@RunWith(MockitoJUnitRunner.class)
public class StreamImplTest {

  private static final List<String> SAMPLE_LIST = ImmutableList.of("item1", "item2");

  @Mock
  private Consumer<String> action;

  @Test
  public void testForEach() {
    Streams.of(SAMPLE_LIST).forEach(action);
    verify(action).accept("item1");
    verify(action).accept("item2");
  }

  @Test
  public void testMap() {
    Stream<Integer> stream = Streams.of(SAMPLE_LIST).map(item -> item.length());
    assertEquals(ImmutableList.of(5, 5), stream.collect(Collectors.toList()));
  }

  @Test
  public void testFilter() {
    Stream<String> stream = Streams.of(SAMPLE_LIST).filter(item -> "item1".equals(item));
    assertEquals(ImmutableList.of("item1"), stream.collect(Collectors.toList()));
  }

  @Test
  public void testToList() {
    assertEquals(SAMPLE_LIST, Streams.of(SAMPLE_LIST).collect(Collectors.toList()));
  }

  @Test
  public void testToSet() {
    assertEquals(ImmutableSet.copyOf(SAMPLE_LIST), Streams.of(SAMPLE_LIST).collect(Collectors.toSet()));
  }

  @Test
  public void testIterator() {
    assertEquals(SAMPLE_LIST, ImmutableList.copyOf(Streams.of(SAMPLE_LIST).iterator()));
  }

  @Test
  public void testFlatMap() {
    Stream<String> stream = Streams.of(SAMPLE_LIST).flatMap(e -> Streams.of(e, e + "-new"));
    assertEquals(ImmutableList.of("item1", "item1-new", "item2", "item2-new"), stream.collect(Collectors.toList()));
  }

  @Test
  public void testFlatMapNull() {
    Stream<String> stream = Streams.of(SAMPLE_LIST).flatMap(e -> "item1".equals(e) ? Streams.of(e) : null);
    assertEquals(ImmutableList.of("item1"), stream.collect(Collectors.toList()));
  }

  @Test
  public void testFindFirst() {
    assertEquals("item1", Streams.of(SAMPLE_LIST).findFirst().orElse("not-found"));
    assertEquals("not-found", Streams.of(ImmutableList.of()).findFirst().orElse("not-found"));
  }

  @Test
  public void testCount() {
    assertEquals(2, Streams.of(SAMPLE_LIST).count());
    assertEquals(0, Streams.of(ImmutableList.of()).count());
  }

}
