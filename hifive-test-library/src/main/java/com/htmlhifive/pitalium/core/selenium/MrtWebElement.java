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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebElementの実装クラス。{@link org.openqa.selenium.remote.RemoteWebElement}の機能に加え、いくつかの追加情報を提供します。<br/>
 * {@link MrtWebDriver#findElements(org.openqa.selenium.By)}から返されるオブジェクトは、このクラスを拡張しています。
 */
public abstract class MrtWebElement extends RemoteWebElement {

	private static final Logger LOG = LoggerFactory.getLogger(MrtWebElement.class);

	//@formatter:off
	//CHECKSTYLE:OFF
	private static final String GET_ELEMENT_RECT_SCRIPT =
			"var _obj = {"
					+ "  \"left\": arguments[0].getBoundingClientRect().left,"
					+ "  \"top\": arguments[0].getBoundingClientRect().top,"
					+ "  \"width\": arguments[0].getBoundingClientRect().width,"
					+ "  \"scrollWidth\": arguments[0].scrollWidth,"
					+ "  \"height\": arguments[0].getBoundingClientRect().bottom - arguments[0].getBoundingClientRect().top"
					+ "};"
					+ "return _obj;";
	private static final String GET_ELEMENT_MARGIN_SCRIPT =
			"var style;"
					+ "if (!document.defaultView) {"
					+ "  style = arguments[0].currentStyle;"
					+ "} else {"
					+ "  style = document.defaultView.getComputedStyle(arguments[0], '');"
					+ "}"
					+ "var _obj = {"
					+ "  \"top\":    style.marginTop,"
					+ "  \"right\":  style.marginRight,"
					+ "  \"bottom\": style.marginBottom,"
					+ "  \"left\":   style.marginLeft"
					+ "};"
					+ "return _obj;";
	private static final String GET_ELEMENT_BORDER_WIDTH_SCRIPT =
			"var style;"
					+ "if (!document.defaultView) {"
					+ "  style = arguments[0].currentStyle;"
					+ "} else {"
					+ "  style = document.defaultView.getComputedStyle(arguments[0], '');"
					+ "}"
					+ "var _obj = {"
					+ "  \"top\":    style.borderTopWidth,"
					+ "  \"right\":  style.borderRightWidth,"
					+ "  \"bottom\": style.borderBottomWidth,"
					+ "  \"left\":   style.borderLeftWidth"
					+ "};"
					+ "return _obj;";
	//CHECKSTYLE:ON
	//@formatter:on

	private static final Pattern PATTERN_NUMBER = Pattern.compile("-?[\\d\\.]+");
	private static final long SHOW_HIDE_TIMEOUT = 30L;

	private MrtWebDriver driver;
	private String tagName;

	/**
	 * コンストラクタ
	 */
	protected MrtWebElement() {
	}

	/**
	 * 親driverを設定します。
	 * 
	 * @param parent 親driver
	 */
	@Override
	public void setParent(RemoteWebDriver parent) {
		super.setParent(parent);
		driver = (MrtWebDriver) parent;
	}

	/**
	 * 要素のタグ名を取得します。
	 * 
	 * @return タグ名
	 */
	@Override
	public String getTagName() {
		if (tagName != null) {
			return tagName;
		}
		tagName = super.getTagName();
		return tagName;
	}

	/**
	 * 親driverを取得します。
	 * 
	 * @return 親driver
	 */
	@Override
	public MrtWebDriver getWrappedDriver() {
		return (MrtWebDriver) super.getWrappedDriver();
	}

	/**
	 * 要素の位置・サイズを矩形領域として取得します。
	 * 
	 * @return 矩形領域を表す{@link WebElementRect}オブジェクト
	 */
	public WebElementRect getRect() {
		// 現在のスクロール位置を取得
		double scrollTop = driver.getCurrentScrollTop();
		double scrollLeft = driver.getCurrentScrollLeft();

		// ページ最上部からの座標を取得
		driver.scrollTo(0d, 0d);

		Map<String, Object> object = driver.executeJavaScript(GET_ELEMENT_RECT_SCRIPT, this);
		LOG.trace("rectangle JS object: {}", object);

		double left = getDoubleOrDefault(object.get("left"), 0d);
		double top = getDoubleOrDefault(object.get("top"), 0d);

		// scrollWidthはdisplay: inlineのときに要素の幅が取得できないため、BoundingClientRect#widthも使う
		double width = getDoubleOrDefault(object.get("scrollWidth"), 0d);
		if (width == 0d) {
			width = getDoubleOrDefault(object.get("width"), 0d);
		}
		// widthはボーダーを含める
		WebElementBorderWidth borderWidth = getBorderWidth();
		width += borderWidth.getLeft() + borderWidth.getRight();

		// bodyの場合はページ全体の高さを返す
		double height = getTagName().equals("body") ? driver.getCurrentPageHeight() : getDoubleOrDefault(
				object.get("height"), 0d);

		// スクロール位置を元に戻す
		driver.scrollTo(scrollLeft, scrollTop);

		return new WebElementRect(left, top, width, height);
	}

	/**
	 * 要素の四辺のMarginを取得します。
	 * 
	 * @return 四辺のMarginを表す{@link WebElementMargin}オブジェクト
	 */
	public WebElementMargin getMargin() {
		Map<String, Object> object = driver.executeJavaScript(GET_ELEMENT_MARGIN_SCRIPT, this);
		LOG.trace("margin JS object: {}", object);

		double top = getDoubleOrDefault(object.get("top"), 0d);
		double left = getDoubleOrDefault(object.get("left"), 0d);
		double bottom = getDoubleOrDefault(object.get("bottom"), 0d);
		double right = getDoubleOrDefault(object.get("right"), 0d);

		return new WebElementMargin(top, right, bottom, left);
	}

	/**
	 * 要素の四辺のBorderWidthを取得します。
	 * 
	 * @return 四辺のBorderWidthを表す{@link WebElementBorderWidth}オブジェクト
	 */
	public WebElementBorderWidth getBorderWidth() {
		Map<String, Object> object = driver.executeJavaScript(GET_ELEMENT_BORDER_WIDTH_SCRIPT, this);
		LOG.trace("borderWidth JS object: {}", object);

		double top = getDoubleOrDefault(object.get("top"), 0d);
		double left = getDoubleOrDefault(object.get("left"), 0d);
		double bottom = getDoubleOrDefault(object.get("bottom"), 0d);
		double right = getDoubleOrDefault(object.get("right"), 0d);

		return new WebElementBorderWidth(top, right, bottom, left);
	}

	/**
	 * 要素を非表示状態にします。
	 */
	public void hide() {
		if (isVisibilityHidden() && !isDisplayed()) {
			LOG.debug("element \"{}\" already hidden", this);
			return;
		}

		driver.executeScript("return arguments[0].style.visibility = 'hidden'", this);
		try {
			new WebDriverWait(driver, SHOW_HIDE_TIMEOUT).until(new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver webDriver) {
					return isVisibilityHidden() && !isDisplayed();
				}
			});
		} catch (TimeoutException e) {
			LOG.warn("Hide element timeout. {}", this);
		}
	}

	/**
	 * 要素を表示状態にします。
	 */
	public void show() {
		if (!isVisibilityHidden() && isDisplayed()) {
			LOG.debug("element \"{}\" already displayed", this);
			return;
		}

		driver.executeScript("return arguments[0].style.visibility = 'visible'", this);
		try {
			new WebDriverWait(driver, SHOW_HIDE_TIMEOUT).until(new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver webDriver) {
					return !isVisibilityHidden() && isDisplayed();
				}
			});
		} catch (TimeoutException e) {
			LOG.warn("Show element timeout. {}", this);
		}
	}

	/**
	 * 要素のvisibilityスタイル値がhiddenかどうかを取得します。
	 * 
	 * @return visibilityがhiddenの場合true、それ以外の値の場合false
	 */
	public boolean isVisibilityHidden() {
		return driver.executeJavaScript("return arguments[0].style.visibility == 'hidden'", this);
	}

	/**
	 * 値をdoubleに変換します。変換できない場合は指定されたデフォルト値を返します。
	 * 
	 * @param object 変換する値
	 * @param defaultValue デフォルト値
	 * @return doubleに変換した値。変換できなかった場合はdefaultValue
	 */
	static double getDoubleOrDefault(Object object, double defaultValue) {
		if (object == null) {
			return defaultValue;
		}
		if (object instanceof Number) {
			return ((Number) object).doubleValue();
		}

		Matcher matcher = PATTERN_NUMBER.matcher(object.toString());
		if (!matcher.find()) {
			LOG.debug("Parse double failed. Not number: {}", object);
			return defaultValue;
		}

		try {
			return Double.parseDouble(matcher.group());
		} catch (Exception e) {
			LOG.debug("Parse double failed: {}", object);
			return defaultValue;
		}
	}

}
