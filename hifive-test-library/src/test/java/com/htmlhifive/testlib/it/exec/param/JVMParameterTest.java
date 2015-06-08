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
