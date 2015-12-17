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

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Collects stream items into a iterable class like list or set.
 * @param <T> the type of input elements to the reduction operation
 * @param <R> the result type of the reduction operation
 * @deprecated Please use Java 8 API.
 */
@Deprecated
@ConsumerType
public interface Collector<T, R> {

  /**
   * Collect items
   * @param stream stream
   * @return Iterable class
   */
  R collect(Stream<? extends T> stream);

}
