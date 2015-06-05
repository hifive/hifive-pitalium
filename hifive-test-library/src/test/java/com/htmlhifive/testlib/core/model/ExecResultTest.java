/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.model;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class ExecResultTest {

	/**
	 * {@link ExecResult#isSuccess()}のテスト
	 */
	@Test
	public void testIsSuccess() throws Exception {
		assertThat(ExecResult.SUCCESS.isSuccess(), is(true));
		assertThat(ExecResult.FAILURE.isSuccess(), is(false));
	}

}