/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.selenium;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class MrtWebElementTest {

	/**
	 * Double変換テスト
	 */
	@Test
	public void testGetDoubleOrDefault() throws Exception {
		double value = MrtWebElement.getDoubleOrDefault(1d, Double.NaN);
		assertThat(value, is(1d));
	}

	/**
	 * Double変換テスト。数字、文字の混合
	 */
	@Test
	public void testGetDoubleOrDefault_mixed() throws Exception {
		double value = MrtWebElement.getDoubleOrDefault("1.0px", Double.NaN);
		assertThat(value, is(1d));
	}

	/**
	 * Double変換テスト。文字のみ
	 */
	@Test
	public void testGetDoubleOrDefault_not_number() throws Exception {
		double value = MrtWebElement.getDoubleOrDefault("middle", Double.NaN);
		assertThat(value, is(Double.NaN));
	}

	/**
	 * Double変換テスト・マイナスの値。
	 */
	@Test
	public void testGetDoubleOrDefault_minus_number() throws Exception {
		double value = MrtWebElement.getDoubleOrDefault("-1.5px", Double.NaN);
		assertThat(value, is(-1.5d));
	}

}