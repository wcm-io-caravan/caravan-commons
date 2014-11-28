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
package io.wcm.dromas.commons.stream;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class StreamsTest {

  private static final List<String> SAMPLE_LIST = ImmutableList.of("item1", "item2");

  @Test
  public void testOfIterable() {
    Stream<String> stream = Streams.of(SAMPLE_LIST);
    assertEquals(SAMPLE_LIST, stream.collect(Collectors.toList()));
  }

  @Test(expected = NullPointerException.class)
  public void testOfNullIterable() {
    Streams.of((Iterable<String>)null);
  }

  @Test
  public void testOfArray() {
    String[] array = SAMPLE_LIST.toArray(new String[SAMPLE_LIST.size()]);
    Stream<String> stream = Streams.of(array);
    assertEquals(SAMPLE_LIST, stream.collect(Collectors.toList()));
  }

  @Test
  public void testOfEmptyArray() {
    Stream<String> stream = Streams.of(new String[0]);
    assertEquals(0, stream.collect(Collectors.toList()).size());

    stream = Streams.of();
    assertEquals(0, stream.collect(Collectors.toList()).size());
  }

  @Test(expected = NullPointerException.class)
  public void testOfNullArray() {
    Streams.of((String[])null);
  }

}
