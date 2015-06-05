/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.it.exec.cap;

import static org.junit.Assert.*;

import org.junit.Test;

import com.htmlhifive.testlib.core.MrtTestBase;

/**
 * ブラウザの設定がされていない状態での実行のテスト
 */
public class NoBrowserTest extends MrtTestBase {

	/**
	 * ブラウザの設定がされていないため、例外がスローされる
	 */
	@Test
	public void alwaysFailTest() {
		fail("テストが実行されました");
	}
}
