/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.it.exec.param;

import static org.junit.Assert.*;

import org.junit.Test;

import com.htmlhifive.testlib.core.MrtTestBase;
import com.htmlhifive.testlib.core.config.EnvironmentConfig;
import com.htmlhifive.testlib.core.config.ExecMode;
import com.htmlhifive.testlib.core.config.MrtTestConfig;

/**
 * JVMで引数を指定した場合のテスト
 */
public class JVMParameterTest extends MrtTestBase {

	/**
	 * JVMの実行モードの引数を指定した場合のテスト。<br>
	 * ファイルの指定値より優先される。 <br>
	 * 設定値：-Dcom.htmlhifive.testlib.execMode=RUN_TEST<br>
	 * 実行環境：IE11/Chrome<br>
	 * 期待結果：MrtRunnerConfigのexecModeがRUN_TESTとなっている
	 */
	@Test
	public void checkParameter() {
		driver.get(null);

		MrtTestConfig config = MrtTestConfig.getInstance();

		// 実行設定の内容のチェック
		EnvironmentConfig env = config.getEnvironment();
		assertEquals(ExecMode.RUN_TEST, env.getExecMode());
	}
}
