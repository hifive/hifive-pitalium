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

import java.util.HashMap;
import java.util.Map;

import com.htmlhifive.pitalium.common.exception.PtlInterruptedException;
import com.htmlhifive.pitalium.common.exception.PtlTimeoutException;
import com.htmlhifive.pitalium.core.selenium.PtlCapabilities;

/**
 * HTTPサーバー経由の処理を行うためのWait処理を担当するクラス
 *
 * @author nakatani
 */
public class PtlHttpObjectMonitor {

	//<editor-fold desc="MonitorKey">

	static class MonitorKey {
		final int id;
		final String type;

		public MonitorKey(int id, String type) {
			if (type == null) {
				throw new NullPointerException();
			}

			this.id = id;
			this.type = type;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			MonitorKey that = (MonitorKey) o;

			if (id != that.id)
				return false;
			return type.equals(that.type);
		}

		@Override
		public int hashCode() {
			int result = id;
			result = 31 * result + type.hashCode();
			return result;
		}
	}

	//</editor-fold>

	private static final Map<MonitorKey, PtlHttpObjectMonitor> MONITORS = new HashMap<MonitorKey, PtlHttpObjectMonitor>();

	/**
	 * スレッドロック用のオブジェクトを取得します。
	 * 
	 * @param capabilities ブラウザ種別
	 * @param type ロック種別
	 * @return スレッドロック用のオブジェクト
	 */
	public static PtlHttpObjectMonitor getMonitor(PtlCapabilities capabilities, String type) {
		return getMonitor(capabilities.getId(), type);
	}

	/**
	 * スレッドロック用のオブジェクトを取得します。
	 *
	 * @param id ユニークID
	 * @param type ロック種別
	 * @return スレッドロック用のオブジェクト
	 */
	public static synchronized PtlHttpObjectMonitor getMonitor(int id, String type) {
		MonitorKey key = new MonitorKey(id, type);
		PtlHttpObjectMonitor monitor = MONITORS.get(key);
		if (monitor == null) {
			MONITORS.put(key, monitor = new PtlHttpObjectMonitor());
		}

		return monitor;
	}

	private boolean locked;

	private PtlHttpObjectMonitor() {
	}

	/**
	 * スレッドをロックします。{@code timeout}で設定した時間が経過する、または別のスレッドから{@link #unlock()}を呼ばれるまでスレッドはロックされます。 {@code timeout}
	 * で設定した時間が経過してロックが解除された場合、{@link PtlTimeoutException}がスローされます。
	 *
	 * @param timeout スレッドのロックが自動解除されるまでの時間（ミリ秒）
	 * @throws PtlTimeoutException {@code timeout}で設定した時間が経過してロックが解除された場合
	 */
	public synchronized void lock(long timeout) {
		locked = true;

		try {
			wait(timeout);
		} catch (InterruptedException e) {
			throw new PtlInterruptedException(e);
		}

		// When timeout
		if (locked) {
			locked = false;
			throw new PtlTimeoutException();
		}
	}

	/**
	 * スレッドのロック状態を解除します。
	 */
	public synchronized void unlock() {
		locked = false;
		notifyAll();
	}

}
