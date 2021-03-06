/*
 * Copyright 2016 The Bazel Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.idea.blaze.base.async.executor;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.Callable;

/** Used in tests. */
public class MockBlazeExecutor extends BlazeExecutor {

  private final ListeningExecutorService executor = MoreExecutors.sameThreadExecutor();

  @Override
  public <T> ListenableFuture<T> submit(final Callable<T> callable) {
    return executor.submit(callable);
  }

  @Override
  public ListeningExecutorService getExecutor() {
    return executor;
  }
}
