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

import io.wcm.dromas.commons.stream.function.Consumer;
import io.wcm.dromas.commons.stream.function.Function;

import java.util.Iterator;

/**
 * This is a (very) simplified version of Java 8 Stream API to use until the project can migate to Java 8.
 * Use the static methods from {@link Streams} to create stream instances.
 * @param <T> Item type
 */
public interface Stream<T> {

  /**
   * Performs an action for each element of this stream.
   * @param action Action
   */
  void forEach(Consumer<? super T> action);

  /**
   * Returns a stream consisting of the results of applying the given function to the elements of this stream.
   * @param mapper A function to apply to each element
   * @return the new stream
   */
  <R> Stream<R> map(Function<? super T, ? extends R> mapper);

  /**
   * Returns a stream consisting of the elements of this stream that match the given predicate.
   * @param function Predicate to apply to each element to determine if it should be included
   * @return the new stream
   */
  Stream<T> filter(Function<? super T, Boolean> function);

  /**
   * Returns an iterator for the elements of this stream.
   * @return the element iterator for this stream
   */
  Iterator<T> iterator();

  /**
   * Collects stream items to a iterable like list or set.
   * @param collector Collector
   * @return Iterable
   */
  <R> R collect(Collector<? super T, R> collector);

}
