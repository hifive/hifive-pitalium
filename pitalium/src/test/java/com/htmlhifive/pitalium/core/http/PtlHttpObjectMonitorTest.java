/*
 * Copyright (C) 2015 NS Solutions Corporation
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

package com.htmlhifive.pitalium.core.http;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.htmlhifive.pitalium.common.exception.PtlTimeoutException;

public class PtlHttpObjectMonitorTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	/**
	 * HttpObjectMonitorのスレッドロックテスト
	 */
	@Test
	public void lock() throws Exception {
		final PtlHttpObjectMonitor monitor = PtlHttpObjectMonitor.getMonitor(0, "hoge");
		final StringBuffer sb = new StringBuffer();
		Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
			@Override
			public void run() {
				sb.append("unlock ");
				monitor.unlock();
			}
		}, 500L, TimeUnit.MILLISECONDS);

		sb.append("before_lock ");
		monitor.lock(1000L);
		sb.append("after_lock ");

		assertThat(sb.toString(), is("before_lock unlock after_lock "));
	}

	/**
	 * 同一キーだが別に取得したMonitorインスタンスでロック処理をするテスト
	 */
	@Test
	public void lock_sameKey() throws Exception {
		PtlHttpObjectMonitor monitorForLock = PtlHttpObjectMonitor.getMonitor(0, "hoge");
		final StringBuffer sb = new StringBuffer();
		Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
			@Override
			public void run() {
				sb.append("unlock ");

				PtlHttpObjectMonitor monitorForUnlock = PtlHttpObjectMonitor.getMonitor(0, "hoge");
				monitorForUnlock.unlock();
			}
		}, 500L, TimeUnit.MILLISECONDS);

		sb.append("before_lock ");
		monitorForLock.lock(1000L);
		sb.append("after_lock ");

		assertThat(sb.toString(), is("before_lock unlock after_lock "));
	}

	/**
	 * 異なるキーで取得したMonitorインスタンスでロック処理をするテスト
	 */
	@Test
	public void lock_differentKey() throws Exception {
		PtlHttpObjectMonitor monitorForLock = PtlHttpObjectMonitor.getMonitor(0, "hoge");
		Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
			@Override
			public void run() {
				PtlHttpObjectMonitor.getMonitor(1, "hoge").unlock();
				PtlHttpObjectMonitor.getMonitor(0, "fuga").unlock();
			}
		}, 500L, TimeUnit.MILLISECONDS);

		exception.expect(PtlTimeoutException.class);
		monitorForLock.lock(1000L);
	}

	/**
	 * HttpObjectMonitorのスレッドロックのタイムアウトテスト
	 */
	@Test
	public void lock_timeout() throws Exception {
		final PtlHttpObjectMonitor monitor = PtlHttpObjectMonitor.getMonitor(0, "hoge");
		Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
			@Override
			public void run() {
				monitor.unlock();
			}
		}, 1000L, TimeUnit.MILLISECONDS);

		exception.expect(PtlTimeoutException.class);
		monitor.lock(500L);
	}

}