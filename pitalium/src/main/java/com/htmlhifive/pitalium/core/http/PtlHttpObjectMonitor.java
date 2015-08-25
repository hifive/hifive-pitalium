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

/**
 * HTTPサーバー経由の処理を行うためのWait処理を担当するクラス
 *
 * @author nakatani
 */
public class PtlHttpObjectMonitor {

	static class MonitorKey {
		final int capabilitiesId;
		final String type;

		public MonitorKey(int capabilitiesId, String type) {
			if (type == null) {
				throw new NullPointerException();
			}

			this.capabilitiesId = capabilitiesId;
			this.type = type;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			MonitorKey that = (MonitorKey) o;

			if (capabilitiesId != that.capabilitiesId)
				return false;
			return type.equals(that.type);
		}

		@Override
		public int hashCode() {
			int result = capabilitiesId;
			result = 31 * result + type.hashCode();
			return result;
		}
	}

	private static final Map<MonitorKey, PtlHttpObjectMonitor> MONITORS = new HashMap<MonitorKey, PtlHttpObjectMonitor>();

	public static synchronized PtlHttpObjectMonitor getMonitor(int id, String type) {
		MonitorKey key = new MonitorKey(id, type);
		PtlHttpObjectMonitor monitor = MONITORS.get(key);
		if (monitor == null) {
			MONITORS.put(key, monitor = new PtlHttpObjectMonitor());
		}

		return monitor;
	}

	private boolean locked;

	public synchronized void lock(long timeout) {
			locked = true;

		long beginTime = System.currentTimeMillis();

			try {
					wait(timeout);
			} catch (InterruptedException e) {
				throw new PtlInterruptedException(e);
			}

			long currentTime = System.currentTimeMillis();
			if (currentTime - beginTime > timeout) {
				throw new PtlInterruptedException(new InterruptedException());
			}
	}

	public synchronized void unlock() {
		locked = false;
		notifyAll();
	}

	//	private static final Map<MonitorKey, Object> MONITOR_OBJECTS = new HashMap<MonitorKey, Object>();
	//
	//	public static void wait(PtlCapabilities capabilities, String type, long timeout) {
	//		wait(capabilities.getId(), type, timeout);
	//	}
	//
	//	public static void wait(int id, String type, long timeout) {
	//		final Object lockObj;
	//		synchronized (MONITOR_OBJECTS) {
	//			MonitorKey key = new MonitorKey(id, type);
	//			if (MONITOR_OBJECTS.containsKey(key)) {
	//				throw new TestRuntimeException(
	//						String.format(Locale.US, "\"Capabilities id[%d] and type[%s]\" is already waiting", id, type));
	//			}
	//
	//			lockObj = new Object();
	//			MONITOR_OBJECTS.put(key, lockObj);
	//		}
	//
	//		synchronized (lockObj) {
	//			try {
	//				lockObj.wait(timeout);
	//			} catch (InterruptedException e) {
	//				throw new PtlInterruptedException(e);
	//			}
	//		}
	//	}
	//
	//	public static void notify(PtlCapabilities capabilities, String type) {
	//		notify(capabilities.getId(), type);
	//	}
	//
	//	public static void notify(int id, String type) {
	//		final Object lockObj;
	//		synchronized (MONITOR_OBJECTS) {
	//			MonitorKey key = new MonitorKey(id, type);
	//			lockObj = MONITOR_OBJECTS.remove(key);
	//			if (lockObj == null) {
	//				throw new TestRuntimeException(
	//						String.format(Locale.US, "\"Capabilities id[%d] and type[%s]\" is not waiting", id, type));
	//			}
	//		}
	//
	//		synchronized (lockObj) {
	//			lockObj.notifyAll();
	//		}
	//	}

}
