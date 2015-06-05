/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.it.exec.base;

import static org.junit.Assert.*;

import org.junit.Test;

import com.htmlhifive.testlib.core.MrtTestBase;

/**
 * MrtBaseを利用したテストの実行のテスト
 */
public class WithBaseTest extends MrtTestBase {

	/**
	 * MrtBaseを利用したテストの実行のテスト<br>
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
