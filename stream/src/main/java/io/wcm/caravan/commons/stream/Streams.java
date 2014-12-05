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
package io.wcm.caravan.commons.stream;

import static com.google.common.base.Preconditions.checkNotNull;
import io.wcm.caravan.commons.stream.impl.StreamImpl;

import java.util.Arrays;

/**
 * Helper methods for creating stream.
 */
public final class Streams {

  private Streams() {
    // static methods only
  }

  /**
   * Creates a stream from an iterable.
   * @param iterable Iterable
   * @return Stream
   */
  public static <T> Stream<T> of(Iterable<T> iterable) {
    checkNotNull(iterable);
    return new StreamImpl<T>(iterable);
  }

  /**
   * Creates a stream from items.
   * @param items Stream items or array
   * @return Stream
   */
  @SafeVarargs
  public static <T> Stream<T> of(T... items) {
    checkNotNull(items);
    return new StreamImpl<T>(Arrays.asList(items));
  }

}
