/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.it.exec.cap;

import org.junit.Test;

import com.htmlhifive.testlib.core.MrtTestBase;

/**
 * １つのブラウザでの実行のテスト
 */
public class SingleBrowserTest extends MrtTestBase {

	@Test
	public void test() {
		driver.get(null);
		driver.takeScreenshot("singleBrowser");
	}
}
