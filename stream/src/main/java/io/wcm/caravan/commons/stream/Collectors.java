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

import java.util.List;
import java.util.Set;

import org.osgi.annotation.versioning.ProviderType;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Implementations of {@link Collector} interface.
 * @deprecated Please use Java 8 API.
 */
@Deprecated
@ProviderType
public final class Collectors {

  private Collectors() {
    // static methods only
  }

  /**
   * Collect to array list.
   * @param <T> Streaming type
   * @return Array list
   */
  public static <T> Collector<T, List<T>> toList() {
    return new Collector<T, List<T>>() {
      @Override
      public List<T> collect(Stream<? extends T> stream) {
        return Lists.newArrayList(stream.iterator());
      }
    };
  }

  /**
   * Collect to hash set
   * @param <T> Streaming type
   * @return Hash set
   */
  public static <T> Collector<T, Set<T>> toSet() {
    return new Collector<T, Set<T>>() {
      @Override
      public Set<T> collect(Stream<? extends T> stream) {
        return Sets.newHashSet(stream.iterator());
      }
    };
  }

}
