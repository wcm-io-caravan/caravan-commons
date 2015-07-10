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

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A container object which may or may not contain a non-null value.
 * If a value is present, {@code isPresent()} will return {@code true} and {@code get()} will return the value.
 */
public final class Optional<T> {

  private final T value;

  /**
   * Constructs an instance with the value present.
   * @param value the non-null value to be present
   * @throws NullPointerException if value is null
   */
  private Optional(T value) {
    this.value = value;
  }

  /**
   * Returns an empty {@code Optional} instance. No value is present for this Optional.
   * @param <T> the class of the value
   * @return an {@code Optional} without value
   */
  public static <T> Optional<T> empty() {
    return new Optional<>(null);
  }

  /**
   * Returns an {@code Optional} with the specified present non-null value.
   * @param <T> the class of the value
   * @param value the value to be present, which must be non-null
   * @return an {@code Optional} with the value present
   * @throws NullPointerException if value is null
   */
  public static <T> Optional<T> of(T value) {
    return new Optional<>(Objects.requireNonNull(value));
  }

  /**
   * If a value is present in this {@code Optional}, returns the value, otherwise throws {@code NoSuchElementException}.
   * @return the non-null value held by this {@code Optional}
   * @throws NoSuchElementException if there is no value present
   * @see Optional#isPresent()
   */
  public T get() {
    if (value == null) {
      throw new NoSuchElementException("No value present.");
    }
    return value;
  }

  /**
   * Return {@code true} if there is a value present, otherwise {@code false}.
   * @return {@code true} if there is a value present, otherwise {@code false}
   */
  public boolean isPresent() {
    return value != null;
  }

  /**
   * Return the value if present, otherwise return {@code other}.
   * @param other the value to be returned if there is no value present, may be null
   * @return the value, if present, otherwise {@code other}
   */
  public T orElse(T other) {
    return value != null ? value : other;
  }

  /**
   * Return the value if present, otherwise invoke {@code other} and return the result of that invocation.
   * @param other a {@code Supplier} whose result is returned if no value is present
   * @return the value if present otherwise the result of {@code other.get()}
   * @throws NullPointerException if value is not present and {@code other} is null
   */
  public T orElseGet(Supplier<? extends T> other) {
    return value != null ? value : other.get();
  }

  /**
   * Return the contained value, if present, otherwise throw an exception to be created by the provided supplier.
   * @param <X> Type of the exception to be thrown
   * @param exceptionSupplier The supplier which will return the exception to be thrown
   * @return the present value
   * @throws X if there is no value present
   * @throws NullPointerException if no value is present and {@code exceptionSupplier} is null
   */
  public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
    if (value != null) {
      return value;
    }
    else {
      throw exceptionSupplier.get();
    }
  }

}
