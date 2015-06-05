/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.rules;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htmlhifive.testlib.core.MrtTestBase;
import com.htmlhifive.testlib.core.result.TestResultManager;

/**
 * テストクラス実行毎に、テスト結果の収集・出力を行う&#064;ClassRule用クラスです。<br/>
 * {@link MrtTestBase}を拡張した場合は、既に定義済みのため指定する必要はありません。
 */
public class ResultCollector extends TestWatcher {

	private static final Logger LOG = LoggerFactory.getLogger(ResultCollector.class);

	@Override
	protected void starting(Description description) {
		String className = description.getTestClass().getSimpleName();
		LOG.info("Test starting: {}", className);

		// TestResultManagerに対してテストの初期化を設定します。
		TestResultManager.getInstance().initializeTestResult(className);
	}

	@Override
	protected void finished(Description description) {
		String className = description.getTestClass().getSimpleName();
		LOG.info("Test finished: {}", className);

		// TestResultManagerに対してテスト結果のエクスポートを要求します。
		TestResultManager resultManager = TestResultManager.getInstance();
		resultManager.exportTestResult(className);
		resultManager.exportExpectedIds(className);
	}

}
