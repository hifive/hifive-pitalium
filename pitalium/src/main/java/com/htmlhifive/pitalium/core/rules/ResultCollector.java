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
package com.htmlhifive.pitalium.core.rules;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.result.TestResultManager;

/**
 * テストクラス実行毎に、テスト結果の収集・出力を行う&#064;ClassRule用クラスです。<br/>
 * {@link PtlTestBase}を拡張した場合は、既に定義済みのため指定する必要はありません。
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
