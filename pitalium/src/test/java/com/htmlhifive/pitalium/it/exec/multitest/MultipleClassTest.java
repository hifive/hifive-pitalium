/*
 * Copyright (C) 2016 NS Solutions Corporation
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
package com.htmlhifive.pitalium.it.exec.multitest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * テストスイートを実行するクラス.<br>
 * 前提条件：このテストをSET_EXPECTEDモードで1度実行し、スクリーンショットを取得している<br>
 * 実行環境：IE11/Chrome<br>
 * 期待結果：テストが正しく実行でき、assertViewの比較で一致する.<br>
 * 　　　　　　　１つのテスト結果IDのフォルダに２つのテストクラスのフォルダができ、それぞれの内容が格納されている。
 */
@RunWith(Suite.class)
@SuiteClasses({ FirstOfMultipleTest.class, SecondOfMultipleTest.class, MultipleClassCheckResultTest.class })
public class MultipleClassTest {

}
