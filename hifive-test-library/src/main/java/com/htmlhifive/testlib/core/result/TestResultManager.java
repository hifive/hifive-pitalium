/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.result;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.htmlhifive.testlib.common.exception.TestRuntimeException;
import com.htmlhifive.testlib.core.config.EnvironmentConfig;
import com.htmlhifive.testlib.core.config.ExecMode;
import com.htmlhifive.testlib.core.config.MrtTestConfig;
import com.htmlhifive.testlib.core.io.PersistMetadata;
import com.htmlhifive.testlib.core.io.Persister;
import com.htmlhifive.testlib.core.io.ResourceUnavailableException;
import com.htmlhifive.testlib.core.model.ExecResult;
import com.htmlhifive.testlib.core.model.ScreenshotResult;
import com.htmlhifive.testlib.core.model.TestResult;

/**
 * テスト結果の管理クラス。
 */
public final class TestResultManager {

	private static final Logger LOG = LoggerFactory.getLogger(TestResultManager.class);

	private static TestResultManager instance;

	/**
	 * 現在のテストID
	 */
	private final String currentId;
	/**
	 * 現在のテスト実行モード
	 */
	private final ExecMode currentMode;
	private final Map<String, ScreenshotResultHolder> holders = new HashMap<String, ScreenshotResultHolder>();
	private final Persister persister;

	private final Map<String, ExpectIdHolder> expectedIdsForUpdate = new HashMap<String, ExpectIdHolder>();
	private final Map<String, Map<String, String>> mergedExpectedIds = new TreeMap<String, Map<String, String>>();
	private final Map<String, Map<String, String>> expectedIds;

	/**
	 * 初期化します。
	 */
	private TestResultManager() {
		this(MrtTestConfig.getInstance().getEnvironment());
	}

	/**
	 * 初期化します。
	 * 
	 * @param environment 環境設定情報
	 */
	@VisibleForTesting
	TestResultManager(EnvironmentConfig environment) {
		currentId = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
		currentMode = environment.getExecMode();

		// Create new Persister by using default constructor
		String persisterClassName = null;
		try {
			persisterClassName = environment.getPersister();
			@SuppressWarnings("unchecked")
			Class<? extends Persister> clss = (Class<? extends Persister>) Class.forName(persisterClassName);
			Constructor<? extends Persister> constructor = clss.getConstructor();
			constructor.setAccessible(true);
			persister = constructor.newInstance();
		} catch (Exception e) {
			throw new TestRuntimeException(persisterClassName + " cannot be instantiated.", e);
		}

		// ExpectedIds
		Map<String, Map<String, String>> expects;
		try {
			expects = persister.loadExpectedIds();

			// Make readonly
			for (Map.Entry<String, Map<String, String>> entry : expects.entrySet()) {
				expects.put(entry.getKey(), Collections.unmodifiableMap(entry.getValue()));
				mergedExpectedIds.put(entry.getKey(), new TreeMap<String, String>(entry.getValue()));
			}

			expects = Collections.unmodifiableMap(expects);
		} catch (ResourceUnavailableException e) {
			LOG.info("ExpectedIds is not exists.");

			expects = Collections.emptyMap();
		}

		expectedIds = expects;
	}

	/**
	 * {@link TestResultManager}のインスタンスを取得します。
	 * 
	 * @return {@link TestResultManager}のインスタンス
	 */
	public static synchronized TestResultManager getInstance() {
		if (instance != null) {
			return instance;
		}

		instance = new TestResultManager();
		return instance;
	}

	/**
	 * 現在のテストIDを取得します。
	 * 
	 * @return 現在のテストID
	 */
	public String getCurrentId() {
		return currentId;
	}

	/**
	 * データ永続化インターフェースを取得します。
	 * 
	 * @return {@link Persister}
	 */
	public Persister getPersister() {
		return persister;
	}

	//<editor-fold desc="TestResults">

	/**
	 * 指定したクラスのテスト結果を初期化します。
	 * 
	 * @param className 対象のクラス名
	 */
	public void initializeTestResult(String className) {
		synchronized (holders) {
			if (holders.containsKey(className)) {
				throw new TestRuntimeException("TestResult already exists for " + className);
			}

			holders.put(className, new ScreenshotResultHolder());
		}
	}

	/**
	 * 指定したクラスのテスト結果を保存します。
	 * 
	 * @param className 対象のクラス名
	 */
	public void exportTestResult(String className) {
		synchronized (holders) {
			if (!holders.containsKey(className)) {
				throw new TestRuntimeException("TestResult does not exist for " + className);
			}

			ScreenshotResultHolder holder = holders.get(className);
			ExecResult execResult = null;
			if (holder.result != null) {
				execResult = holder.result ? ExecResult.SUCCESS : ExecResult.FAILURE;
			}
			TestResult result = new TestResult(currentId, execResult, holder.screenshotResults);
			persister.saveTestResult(new PersistMetadata(currentId, className), result);
		}
	}

	/**
	 * 指定したクラスのスクリーンショット撮影・比較結果を追加します。
	 * 
	 * @param className 対象のクラス名
	 * @param result スクリーンショット撮影・比較結果
	 */
	public void addScreenshotResult(String className, ScreenshotResult result) {
		synchronized (holders) {
			if (!holders.containsKey(className)) {
				throw new TestRuntimeException("TestResult does not exist for " + className);
			}

			ScreenshotResultHolder holder = holders.get(className);
			holder.screenshotResults.add(result);
			if (result.getResult() != null) {
				if (result.getResult().isSuccess()) {
					if (holder.result == null) {
						holder.result = true;
					}
				} else {
					holder.result = false;
				}
			}
		}
	}

	//</editor-fold>

	//<editor-fold desc="ExpectedIds">

	/**
	 * ExpectedIdの一覧を取得します。
	 * 
	 * @return ExpectedIdの一覧
	 */
	@VisibleForTesting
	Map<String, Map<String, String>> getExpectedIds() {
		return expectedIds;
	}

	/**
	 * 指定したクラス・メソッドのExpectedIdを取得します。対象のIDが存在しない場合{@link TestRuntimeException}になります。
	 * 
	 * @param className 対象のテストクラス名
	 * @param methodName 対象のテストメソッド名
	 * @return テストクラス名、テストメソッド名に一致するExpectedId
	 * @throws TestRuntimeException 対象のIDが存在しない場合
	 */
	public String getExpectedId(String className, String methodName) throws TestRuntimeException {
		String message = "ExpectedId for [%s#%s] does not exist.";
		if (!expectedIds.containsKey(className)) {
			throw new TestRuntimeException(String.format(message, className, methodName));
		}

		Map<String, String> methodAndIds = expectedIds.get(className);
		if (!methodAndIds.containsKey(methodName)) {
			throw new TestRuntimeException(String.format(message, className, methodName));
		}

		return methodAndIds.get(methodName);
	}

	/**
	 * ExpectedIdを現在のIDで更新します。
	 * 
	 * @param className 対象のテストクラス名
	 * @param methodName 対象のテストメソッド名
	 */
	public void updateExpectedId(String className, String methodName) {
		if (currentMode != ExecMode.SET_EXPECTED) {
			return;
		}

		synchronized (expectedIdsForUpdate) {
			ExpectIdHolder holder;
			if (!expectedIdsForUpdate.containsKey(className)) {
				holder = new ExpectIdHolder();
				expectedIdsForUpdate.put(className, holder);
			} else {
				holder = expectedIdsForUpdate.get(className);
			}

			String oldId = holder.expectIds.put(methodName, currentId);
			LOG.debug("Update expectedId [{}#{}] {} => {}", className, methodName, oldId, currentId);
		}
	}

	/**
	 * 指定のテストクラスに対するExpectedIdの更新をキャンセルします。
	 * 
	 * @param className 対象のテストクラス名
	 */
	public void cancelUpdateExpectedId(String className) {
		if (currentMode != ExecMode.SET_EXPECTED) {
			return;
		}

		synchronized (expectedIdsForUpdate) {
			ExpectIdHolder holder;
			if (!expectedIdsForUpdate.containsKey(className)) {
				holder = new ExpectIdHolder();
				expectedIdsForUpdate.put(className, holder);
			} else {
				holder = expectedIdsForUpdate.get(className);
			}

			holder.failed = true;
		}
	}

	/**
	 * ExpectedIdの一覧を保存します。
	 * 
	 * @param className 対象のテストクラス名
	 */
	public void exportExpectedIds(String className) {
		if (currentMode != ExecMode.SET_EXPECTED) {
			return;
		}

		synchronized (expectedIdsForUpdate) {
			if (!expectedIdsForUpdate.containsKey(className)) {
				throw new TestRuntimeException(String.format("ExpectedId for %s does not exist.", className));
			}

			ExpectIdHolder holder = expectedIdsForUpdate.get(className);
			if (holder.failed) {
				LOG.debug("TestClass {} was failed. Update ExpectedIds cancelled.");
				return;
			}

			Map<String, String> values = mergedExpectedIds.get(className);
			if (values == null) {
				values = new TreeMap<String, String>();
				mergedExpectedIds.put(className, values);
			}

			values.putAll(holder.expectIds);
			persister.saveExpectedIds(mergedExpectedIds);
		}
	}

	private static class ScreenshotResultHolder {
		private final List<ScreenshotResult> screenshotResults = new ArrayList<ScreenshotResult>();
		private Boolean result;
	}

	private static class ExpectIdHolder {
		private final Map<String, String> expectIds = new HashMap<String, String>();
		// テスト成否。初期値はfalse
		private boolean failed;
	}

	//</editor-fold>

}
