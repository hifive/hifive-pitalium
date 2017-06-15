/*
 * Copyright (C) 2015-2017 NS Solutions Corporation
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
package com.htmlhifive.pitalium.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.runners.Parameterized;
import org.junit.runners.model.RunnerScheduler;

import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
import com.htmlhifive.pitalium.core.config.EnvironmentConfig;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;

/**
 * {@link org.junit.runners.Parameterized}をマルチスレッドで実行するJUnit Runnerクラス
 */
public class ParameterizedThreads extends Parameterized {

	/**
	 * スレッドを並列実行するためのスケジューラクラス
	 */
	static class MultiThreadRunnerScheduler implements RunnerScheduler {

		private final ExecutorService executor;
		private final int maxThreadExecuteTime;

		/**
		 * コンストラクタ
		 */
		public MultiThreadRunnerScheduler() {
			EnvironmentConfig environment = PtlTestConfig.getInstance().getEnvironment();

			executor = Executors.newFixedThreadPool(environment.getMaxThreadCount());
			maxThreadExecuteTime = environment.getMaxThreadExecuteTime();
		}

		@Override
		public void finished() {
			executor.shutdown();
			try {
				executor.awaitTermination(maxThreadExecuteTime, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				throw new TestRuntimeException(e);
			}
		}

		@Override
		public void schedule(Runnable childStatement) {
			executor.submit(childStatement);
		}
	}

	/**
	 * コンストラクタ
	 * 
	 * @param clss クラス
	 * @throws Throwable スレッドで発生したエラーおよび例外
	 */
	public ParameterizedThreads(Class clss) throws Throwable {
		super(clss);
		setScheduler(new MultiThreadRunnerScheduler());
	}

}