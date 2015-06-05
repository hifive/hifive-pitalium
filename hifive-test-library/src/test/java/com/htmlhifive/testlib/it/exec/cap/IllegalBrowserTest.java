/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.it.exec.cap;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriverException;

import com.htmlhifive.testlib.core.MrtTestBase;

/**
 * 不正なブラウザの設定を含む実行のテスト. <br>
 * 同時に実行するテストの数をそれぞれ1,2,3(illegalな設定はすべて1つずつ)で実行する.<br>
 * 実行時は、JVMの引数で<br>
 * -Dcom.htmlhifive.testlib.environmentConfig=com\htmlhifive\test\exec\cap\hifiveRunnerConfig_IllegalBrowserTest_X.json<br>
 * (X=1,2,3)<br>
 * を指定すること.
 */
public class IllegalBrowserTest extends MrtTestBase {

	private static final String[] ILLEGAL_BROWSER_STRS = { "illegal" };

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Rule
	public TestWatcher watchman = new TestWatcher() {

		/*
		 * (非 Javadoc)
		 * @see org.junit.rules.TestWatcher#starting(org.junit.runner.Description)
		 */
		@Override
		protected void starting(Description description) {
			if (isIllegalCapability()) {
				expectedException.expect(WebDriverException.class);
			}
		}
	};

	@Test
	public void testWhenRightCapability() {
		if (isIllegalCapability()) {
			Assert.fail("テストが実行されました");
		}
		driver.get(null);
		Assert.assertTrue(true);
	}

	private boolean isIllegalCapability() {
		for (String illegalBrowser : ILLEGAL_BROWSER_STRS) {
			if (illegalBrowser.equals(capabilities.getBrowserName())) {
				return true;
			}
		}
		return false;
	}
}
