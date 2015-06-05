/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.config;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class FilePersisterConfigTest {

	/**
	 * ビルダーテスト（デフォルト値）
	 */
	@Test
	public void testBuilder_defaultValue() throws Exception {
		FilePersisterConfig expected = new FilePersisterConfig();
		FilePersisterConfig actual = FilePersisterConfig.builder().build();

		assertThat(actual, is(expected));
	}

	/**
	 * ビルダーテスト（全プロパティ）
	 */
	@Test
	public void testBuilder_setAllProperties() throws Exception {
		FilePersisterConfig expected = new FilePersisterConfig();
		expected.setResultDirectory("1");
		expected.setTargetResultFileName("2");
		expected.setScreenshotFileName("3");
		expected.setDiffFileName("4");

		FilePersisterConfig actual = FilePersisterConfig.builder().resultDirectory("1").targetResultFileName("2")
				.screenshotFileName("3").diffFileName("4").build();

		assertThat(actual, is(expected));
	}

}