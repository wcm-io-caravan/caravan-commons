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

import org.osgi.annotation.versioning.ProviderType;

import rx.Observable.Operator;
import rx.Subscriber;

import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;

/**
 * Operator measuring the time an operation needs from executing the {@link Operator#call(Object)} to
 * {@link Subscriber#onCompleted()} or {@link Subscriber#onError(Throwable)} method.
 * @param <R> Items type
 */
@ProviderType
public final class TimerMetricsOperator<R> implements Operator<R, R> {

  private final Timer timer;

  /**
   * @param timer Stop watch
   */
  public TimerMetricsOperator(Timer timer) {
    this.timer = timer;
  }


  @Override
  public Subscriber<? super R> call(Subscriber<? super R> subscriber) {
    final Context ctx = timer.time();
    return new Subscriber<R>() {

      @Override
      public void onCompleted() {
        ctx.stop();
        subscriber.onCompleted();
      }

      @Override
      public void onError(Throwable e) {
        ctx.stop();
        subscriber.onError(e);
      }

      @Override
      public void onNext(R next) {
        subscriber.onNext(next);
      }
    };
  }

}
