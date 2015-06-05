/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.it.exec.multitest;

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
@SuiteClasses({ FirstOfMultipleTest.class, SecondOfMultipleTest.class })
public class TestSuiteTest {
}
