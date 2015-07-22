/*
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

package com.htmlhifive.pitalium.it.exec.cap;

import static org.junit.Assert.*;

import org.junit.Test;

import com.htmlhifive.pitalium.core.PtlTestBase;

/**
 * ブラウザの設定がされていない状態での実行のテスト
 */
public class NoBrowserTest extends PtlTestBase {

	/**
	 * ブラウザの設定がされていないため、例外がスローされる
	 */
	@Test
	public void alwaysFailTest() {
		fail("テストが実行されました");
	}
}
