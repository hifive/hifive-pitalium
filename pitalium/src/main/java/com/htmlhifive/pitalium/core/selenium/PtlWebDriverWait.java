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
package com.htmlhifive.pitalium.core.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htmlhifive.pitalium.common.exception.TestRuntimeException;

/**
 * ブラウザ操作のwaitを行うクラス
 */
public class PtlWebDriverWait extends WebDriverWait {

	private static final Logger LOG = LoggerFactory.getLogger(PtlWebDriverWait.class);
	private static final String DUMMY_TAG_ID = "dummy";
	private static final ExpectedCondition<WebElement> FIND_DUMMY_TAG_CONDITION = new ExpectedCondition<WebElement>() {
		@Override
		public WebElement apply(WebDriver webDriver) {
			return webDriver.findElement(By.id(DUMMY_TAG_ID));
		}
	};

	//@formatter:off
	//CHECKSTYLE:OFF
	private static final String LOAD_COMPLETE_SCRIPT = "function pitalium_loadComplete () {" +
			"  var element = document.createElement('div');" +
			"  element.id = '%s';" +
			"  document.body.appendChild(element);" +
			"}" +
			"if (document.readyState == 'complete') {" +
			"  pitalium_loadComplete();" +
			"} else {" +
			"  window.addEventListener('load', pitalium_loadComplete);" +
			"}";

	private static final String DELETE_TAG_SCRIPT =
			"document.getElementById('%s').parentNode.removeChild(document.getElementById('%s'));";
	//CHECKSTYLE:ON
	//@formatter:on

	private final PtlWebDriver driver;

	/**
	 * コンストラクタ
	 * 
	 * @param driver WebDriver
	 * @param timeOutInSeconds タイムアウト時間（秒）
	 */
	public PtlWebDriverWait(PtlWebDriver driver, long timeOutInSeconds) {
		super(driver, timeOutInSeconds);
		this.driver = driver;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param driver WebDriver
	 * @param timeOutInSeconds タイムアウト時間（秒）
	 * @param sleepInMillis 条件をチェックする時間間隔（ミリ秒）
	 */
	public PtlWebDriverWait(PtlWebDriver driver, long timeOutInSeconds, long sleepInMillis) {
		super(driver, timeOutInSeconds, sleepInMillis);
		this.driver = driver;
	}

	/**
	 * ページのLoadイベント終了まで待機します。
	 */
	public void untilLoad() {
		final int sleepMillis = 100;
		try {
			Thread.sleep(sleepMillis);
			String beforeURL = getURL();
			try {
				addElement(DUMMY_TAG_ID);

				// このタイミングでページが切り替わるとタイムアウトを待つため、時間がかかる。
				until(FIND_DUMMY_TAG_CONDITION);

				deleteElement(DUMMY_TAG_ID);
			} catch (Exception ex) {
				String afterURL = getURL();
				if (!beforeURL.equals(afterURL)) {
					LOG.warn("wait前と後でページが切り替わっているため、waitを継続します");

					// URLが違う場合、切り替わっていないため、もう一度実行する。
					untilLoad();
				} else {
					throw new TestRuntimeException(ex);
				}
			}
		} catch (InterruptedException e) {
			throw new TestRuntimeException(e);
		}
	}

	/**
	 * 現在のURLを取得します。
	 * 
	 * @return 現在のURL
	 */
	private String getURL() {
		return driver.executeJavaScript("return location.href");
	}

	/**
	 * 指定したidを持つdiv要素を追加します。
	 * 
	 * @param id 追加する要素のid
	 */
	private void addElement(String id) {
		driver.executeScript(String.format(LOAD_COMPLETE_SCRIPT, id));
	}

	/**
	 * 指定したidの要素を削除します。
	 * 
	 * @param id 削除する要素のid
	 */
	private void deleteElement(String id) {
		driver.executeScript(String.format(DELETE_TAG_SCRIPT, id, id));
	}

}
