/*
 * Copyright (C) 2016 NS Solutions Corporation
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
package com.htmlhifive.pitalium.it.exec.base;

import static org.junit.Assert.*;

import org.junit.Test;

import com.htmlhifive.pitalium.core.PtlTestBase;

/**
 * PtlTestBaseを利用したテストの実行のテスト
 */
public class WithBaseTest extends PtlTestBase {

	/**
	 * PtlTestBaseを利用したテストの実行のテスト<br>
	 * 前提条件：このテストをSET_EXPECTEDモードで1度実行し、スクリーンショットを取得している<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、assertViewの比較で一致する
	 */
	@Test
	public void checkBaseField() {
		assertNotNull(driver);
		driver.get(null);

		// driverから取得できるcapabailitiesをプロパティに持つ
		assertEquals(driver.getCapabilities(), capabilities);

		assertNotNull(assertionView);
		assertionView.assertView("withBaseTest");

		assertNotNull(collector);
	}
}
