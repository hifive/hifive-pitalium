/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.it.exec.cap;

import org.junit.Test;

import com.htmlhifive.testlib.core.MrtTestBase;

/**
 * 複数のブラウザでの実行のテスト
 */
public class MultipleBrowserTest extends MrtTestBase {

	@Test
	public void test() {
		driver.get(null);
		driver.takeScreenshot("multipleBrowser");
	}
}
