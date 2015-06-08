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
package com.htmlhifive.testlib.core.model;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * セレクタの種別を表す定数クラス
 */
public enum SelectorType {
	/**
	 * id
	 */
	ID {
		@Override
		By by(String selector) {
			return By.id(selector);
		}
	},
	/**
	 * class
	 */
	CLASS_NAME {
		@Override
		By by(String selector) {
			return By.className(selector);
		}
	},
	/**
	 * CSSセレクタ
	 */
	CSS_SELECTOR {
		@Override
		By by(String selector) {
			return By.cssSelector(selector);
		}
	},
	/**
	 * &lt;a&gt;&lt;/a&gt;で囲まれたテキスト（完全一致）
	 */
	LINK_TEXT {
		@Override
		By by(String selector) {
			return By.linkText(selector);
		}
	},
	/**
	 * name
	 */
	NAME {
		@Override
		By by(String selector) {
			return By.name(selector);
		}
	},
	/**
	 * &lt;a&gt;&lt;/a&gt;で囲まれたテキスト（部分一致）
	 */
	PARTIAL_LINK {
		@Override
		By by(String selector) {
			return By.partialLinkText(selector);
		}
	},
	/**
	 * タグ名
	 */
	TAG_NAME {
		@Override
		By by(String selector) {
			return By.tagName(selector);
		}
	},
	/**
	 * XPath
	 */
	XPATH {
		@Override
		By by(String selector) {
			return By.xpath(selector);
		}
	};

	/**
	 * セレクタに一致する要素を一つ取得します。
	 * 
	 * @param driver WebDriverのインスタンス
	 * @param selector セレクタ
	 * @return セレクタに一致した最初の要素
	 */
	public WebElement findElement(WebDriver driver, String selector) {
		return driver.findElement(by(selector));
	}

	/**
	 * セレクタに一致する全ての要素を取得します。
	 * 
	 * @param driver WebDriverのインスタンス
	 * @param selector セレクタ
	 * @return セレクタに一致した要素のリスト
	 */
	public List<WebElement> findElements(WebDriver driver, String selector) {
		return driver.findElements(by(selector));
	}

	/**
	 * 指定の親要素下から、セレクタに一致する要素を一つ取得します。
	 * 
	 * @param element 親要素
	 * @param selector セレクタ
	 * @return セレクタに一致した最初の要素
	 */
	public WebElement findElement(WebElement element, String selector) {
		return element.findElement(by(selector));
	}

	/**
	 * 指定の親要素下から、セレクタに一致する要素を全て取得します。
	 * 
	 * @param element 親要素
	 * @param selector セレクタ
	 * @return セレクタに一致した要素のリスト
	 */
	public List<WebElement> findElements(WebElement element, String selector) {
		return element.findElements(by(selector));
	}

	/**
	 * セレクタに一致する要素を取得するためのByオブジェクトを返します。
	 * 
	 * @param selector セレクタ
	 * @return Byオブジェクト
	 */
	abstract By by(String selector);
}
