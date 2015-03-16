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
package io.wcm.caravan.commons.metrics.rx;

import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable.Operator;
import rx.Subscriber;

import com.codahale.metrics.Counter;

/**
 * Operator counting hits and misses. If no element passes the operator, the {@code missesCounter} gets increased. For
 * every passed element the {@code hitsCounter} gets increased.
 * @param <R> Items type
 */
public class HitsAndMissesCountingMetricsOperator<R> implements Operator<R, R> {

  private final Counter hitsCounter;
  private final Counter missesCounter;

  /**
   * @param hitsCounter Counter for hits
   * @param missesCounter Counter for misses
   */
  public HitsAndMissesCountingMetricsOperator(Counter hitsCounter, Counter missesCounter) {
    this.hitsCounter = hitsCounter;
    this.missesCounter = missesCounter;
  }

  @Override
  public Subscriber<? super R> call(Subscriber<? super R> subscriber) {

    final AtomicBoolean hit = new AtomicBoolean();

    return new Subscriber<R>() {

      @Override
      public void onCompleted() {
        (hit.get() ? hitsCounter : missesCounter).inc();
        subscriber.onCompleted();
      }

      @Override
      public void onError(Throwable e) {
        subscriber.onError(e);
      }

      @Override
      public void onNext(R next) {
        hit.set(true);
        subscriber.onNext(next);
      }
    };
  }

}
