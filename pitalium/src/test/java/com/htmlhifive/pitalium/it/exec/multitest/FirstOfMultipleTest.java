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
package com.htmlhifive.pitalium.it.exec.multitest;

import org.junit.Test;

import com.htmlhifive.pitalium.core.MrtTestBase;
import com.htmlhifive.pitalium.core.model.DomSelector;
import com.htmlhifive.pitalium.core.model.SelectorType;

/**
 * 同時実行のテストの１つ目のテストクラス
 */
public class FirstOfMultipleTest extends MrtTestBase {

	private static final DomSelector[] HIDDEN_ELEMENTS = new DomSelector[] { new DomSelector(SelectorType.CLASS_NAME,
			"gototop") };

	/**
	 * 設定ファイルの内容が設定されているかのテスト<br>
	 * 前提条件：このテストをSET_EXPECTEDモードで1度実行し、スクリーンショットを取得している<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、assertViewの比較で一致する
	 */
	@Test
	public void test() {
		driver.get(null);
		assertionView.assertView("first", null, HIDDEN_ELEMENTS);
	}
}
