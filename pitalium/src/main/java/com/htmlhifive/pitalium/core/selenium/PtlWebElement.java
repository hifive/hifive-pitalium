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
 * {@link PtlWebDriver#findElements(org.openqa.selenium.By)}から返されるオブジェクトは、このクラスを拡張しています。
 */
public abstract class PtlWebElement extends RemoteWebElement {

	private static final Logger LOG = LoggerFactory.getLogger(PtlWebElement.class);

	//@formatter:off
	//CHECKSTYLE:OFF
	private static final String GET_ELEMENT_RECT_SCRIPT =
			"var _obj = {"
					+ "  \"left\": arguments[0].getBoundingClientRect().left,"
					+ "  \"top\": arguments[0].getBoundingClientRect().top,"
//					+ "  \"width\": arguments[0].getBoundingClientRect().width,"
					+ "  \"width\": arguments[0].getBoundingClientRect().right - arguments[0].getBoundingClientRect().left,"
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
	private static final String GET_ELEMENT_PADDING_SCRIPT =
			"var style;"
					+ "if (!document.defaultView) {"
					+ "  style = arguments[0].currentStyle;"
					+ "} else {"
					+ "  style = document.defaultView.getComputedStyle(arguments[0], '');"
					+ "}"
					+ "var _obj = {"
					+ "  \"top\":    style.paddingTop,"
					+ "  \"right\":  style.paddingRight,"
					+ "  \"bottom\": style.paddingBottom,"
					+ "  \"left\":   style.paddingLeft"
					+ "};"
					+ "return _obj;";
	private static final String[] SCRIPTS_SCROLL_TOP = {
				"arguments[0].contentWindow.document.documentElement.scrollTop",
				"arguments[0].contentWindow.document.body.scrollTop" };
	private static final String[] SCRIPTS_SCROLL_LEFT = {
				"arguments[0].contentWindow.document.documentElement.scrollLeft",
				"arguments[0].contentWindow.document.body.scrollLeft" };
	private static final String SCRIPT_HIDDEN_ELEMENT_OVERFLOW = "arguments[0].style.overflow='hidden'";
	private static final String SCRIPT_HIDDEN_FRAME_OVERFLOW = "arguments[0].contentWindow.document.documentElement.style.overflow='hidden'";
	private static final String SCRIPT_HIDDEN_ELEMENT_OVERFLOWY_IE10 = "arguments[0].style.overflowY='hidden'";
	private static final String SCRIPT_HIDDEN_ELEMENT_OVERFLOWX_IE10 = "arguments[0].style.overflowX='hidden'";

	private static final String SCRIPT_SET_RESIZE_NONE = "arguments[0].style.resize = 'none'";
	//CHECKSTYLE:ON
	//@formatter:on

	private static final Pattern PATTERN_NUMBER = Pattern.compile("-?[\\d\\.]+");
	private static final long SHOW_HIDE_TIMEOUT = 30L;

	private PtlWebDriver driver;
	private String tagName;

	/**
	 * コンストラクタ
	 */
	protected PtlWebElement() {
	}

	/**
	 * 親driverを設定します。
	 *
	 * @param parent 親driver
	 */
	@Override
	public void setParent(RemoteWebDriver parent) {
		super.setParent(parent);
		driver = (PtlWebDriver) parent;
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
	public PtlWebDriver getWrappedDriver() {
		return (PtlWebDriver) super.getWrappedDriver();
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
		//		double width = getDoubleOrDefault(object.get("scrollWidth"), 0d);
		//		if (width == 0d) {
		//			width = getDoubleOrDefault(object.get("width"), 0d);
		//		}
		double width = getTagName().equals("body") ? driver.getCurrentPageWidth() : getDoubleOrDefault(
				object.get("width"), 0d);
		// widthはボーダーを含める
		//		WebElementBorderWidth borderWidth = getBorderWidth();
		//		width += borderWidth.getLeft() + borderWidth.getRight();

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
	 * 要素の四辺のPaddingを取得します。
	 *
	 * @return 四辺のPaddingを表す{@link WebElementPadding}オブジェクト
	 */
	public WebElementPadding getPadding() {
		Map<String, Object> object = driver.executeJavaScript(GET_ELEMENT_PADDING_SCRIPT, this);
		LOG.trace("padding JS object: {}", object);

		double top = getDoubleOrDefault(object.get("top"), 0d);
		double left = getDoubleOrDefault(object.get("left"), 0d);
		double bottom = getDoubleOrDefault(object.get("bottom"), 0d);
		double right = getDoubleOrDefault(object.get("right"), 0d);

		return new WebElementPadding(top, right, bottom, left);
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

	/**
	 * 自身の部分スクロールのスクロール回数を返す。<br>
	 * 部分スクロールがない場合は0を返す。
	 *
	 * @return スクロール回数
	 */
	public int getScrollNum() {

		double clientHeight = getClientHeight();
		double scrollHeight = getScrollHeight();

		if (clientHeight >= scrollHeight) {
			return 0;
		}

		return (int) (Math.ceil(scrollHeight / clientHeight)) - 1;
	}

	/**
	 * 要素の可視範囲の高さを取得する。
	 *
	 * @return 高さ（整数px）
	 */
	public long getClientHeight() {
		WebElementRect rect = getRect();
		WebElementBorderWidth border = getBorderWidth();
		return Math.round(rect.getHeight() - border.getTop() - border.getBottom());
	}

	/**
	 * 要素の可視範囲の幅を取得する。
	 *
	 * @return 幅（整数px）
	 */
	public long getClientWidth() {
		WebElementRect rect = getRect();
		WebElementBorderWidth border = getBorderWidth();
		return Math.round(rect.getWidth() - border.getLeft() - border.getRight());
	}

	/**
	 * スクロールを含む要素全体の高さを取得する。
	 *
	 * @return 高さ（整数px）
	 */
	public long getScrollHeight() {
		String result;
		if (getTagName().equals("iframe") || getTagName().equals("frame")) {
			result = driver.executeScript("return arguments[0].contentWindow.document.documentElement.scrollHeight",
					this).toString();
		} else {
			result = driver.executeScript("return arguments[0].scrollHeight", this).toString();
		}
		return Long.parseLong(result);
	}

	/**
	 * スクロールを含む要素全体の幅を取得する。
	 *
	 * @return 幅（整数px）
	 */
	public long getScrollWidth() {
		String result;
		if (getTagName().equals("iframe") || getTagName().equals("frame")) {
			result = driver.executeScript("return arguments[0].contentWindow.document.documentElement.scrollWidth",
					this).toString();
		} else {
			result = driver.executeScript("return arguments[0].scrollWidth", this).toString();
		}
		return Long.parseLong(result);
	}

	/**
	 * 要素を1回分スクロールする。
	 *
	 * @return 今回のスクロール量
	 */
	public int scrollNext() {
		long initialScrollTop = getCurrentScrollTop();
		long clientHeight = getClientHeight();
		scrollTo(0, initialScrollTop + clientHeight);
		long currentScrollTop = getCurrentScrollTop();
		return (int) (currentScrollTop - initialScrollTop);
	}

	/**
	 * 現在のスクロール位置（y座標）を取得する。
	 *
	 * @return スクロール位置（実数px）
	 */
	long getCurrentScrollTop() {
		long top = 0;
		if (getTagName().equals("iframe") || getTagName().equals("frame")) {
			long max = 0L;
			for (String value : SCRIPTS_SCROLL_TOP) {
				long current = Long.parseLong(driver.executeScript("return " + value, this).toString());
				max = Math.max(max, current);
			}
			top = max;
		} else {
			top = Long.parseLong(driver.executeScript("return arguments[0].scrollTop", this).toString());
		}
		return top;
	}

	/**
	 * 現在のスクロール位置（x座標）を取得する。
	 *
	 * @return スクロール位置（実数px）
	 */
	double getCurrentScrollLeft() {
		double top = 0;
		if (getTagName().equals("iframe") || getTagName().equals("frame")) {
			double max = 0d;
			for (String value : SCRIPTS_SCROLL_LEFT) {
				double current = Double.parseDouble(driver.executeScript("return " + value, this).toString());
				max = Math.max(max, current);
			}
			top = max;
		} else {
			top = Double.parseDouble(driver.executeScript("return arguments[0].scrollLeft", this).toString());
		}
		return top;
	}

	/**
	 * 指定位置までスクロールする。
	 *
	 * @param x x座標
	 * @param y y座標
	 */
	public void scrollTo(double x, double y) {
		if (getTagName().equals("iframe") || getTagName().equals("frame")) {
			driver.executeScript("arguments[0].contentWindow.scrollTo(arguments[1], arguments[2])", this, x, y);
		} else {
			driver.executeScript("arguments[0].scrollLeft = arguments[1]", this, x);
			driver.executeScript("arguments[0].scrollTop = arguments[1]", this, y);
		}

	}

	/**
	 * スクロールバーを非表示にする。
	 */
	public void hideScrollBar() {
		if (getTagName().equals("iframe") || getTagName().equals("frame")) {
			driver.executeScript(SCRIPT_HIDDEN_FRAME_OVERFLOW, this);
		} else if (driver.getCapabilities().getBrowserName().equals("internet explorer")
		//TODO: IE10・11用コードの切り出し
				&& (driver.getCapabilities().getVersion().equals("10") || driver.getCapabilities().getVersion()
						.equals("11"))) {
			driver.executeScript(SCRIPT_HIDDEN_ELEMENT_OVERFLOWY_IE10, this);
			driver.executeScript(SCRIPT_HIDDEN_ELEMENT_OVERFLOWX_IE10, this);
		} else {
			driver.executeScript(SCRIPT_HIDDEN_ELEMENT_OVERFLOW, this);
		}
	}

	/**
	 * 要素をリサイズ不可にする。<br>
	 * 要素がテキストエリア以外の場合はなにもしない。
	 */
	public void setNoResizable() {
		if (getTagName().equals("textarea")) {
			driver.executeScript(SCRIPT_SET_RESIZE_NONE, this);
		}
	}

}
