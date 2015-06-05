/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.it.screenshot.fullpage;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * CompareEntirePageTestを実行するクラス
 */
@RunWith(Suite.class)
@SuiteClasses(value = { AssertViewOfEntirePageTest.class, AssertViewOfEntirePageCheckResultTest.class })
public class AssertViewOfEntirePageTestSuite {
}
