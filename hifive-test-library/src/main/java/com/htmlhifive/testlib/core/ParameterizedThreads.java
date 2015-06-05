/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.runners.Parameterized;
import org.junit.runners.model.RunnerScheduler;

import com.htmlhifive.testlib.common.exception.TestRuntimeException;
import com.htmlhifive.testlib.core.config.EnvironmentConfig;
import com.htmlhifive.testlib.core.config.MrtTestConfig;

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
			EnvironmentConfig environment = MrtTestConfig.getInstance().getEnvironment();

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