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
package io.wcm.caravan.commons.stream.function;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Represents an operation that accepts a single input argument and returns no result.
 * @param <T> the type of the input to the operation
 */
@ConsumerType
public interface Consumer<T> {

  /**
   * Performs this operation on the given argument.
   * @param item input argument
   */
  void accept(T item);

}
