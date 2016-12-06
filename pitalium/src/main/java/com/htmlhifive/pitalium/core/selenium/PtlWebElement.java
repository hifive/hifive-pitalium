/*
 * Copyright (C) 2015-2016 NS Solutions Corporation
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

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;
import com.htmlhifive.pitalium.common.exception.TestRuntimeException;

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
	private static final String SCRIPT_GET_ELEMENT_OVERFLOW = "var style = arguments[0].style;"
			+ "var _obj = {"
			+ "  \"x\": style.overflowX,"
			+ "  \"y\": style.overflowY,"
			+ "  \"both\": style.overflow"
			+ "};"
			+ "return _obj;";
	private static final String SCRIPT_GET_FRAME_OVERFLOW = "var style = arguments[0].contentWindow.document.documentElement.style;"
			+ "var _obj = {"
			+ "  \"x\": style.overflowX,"
			+ "  \"y\": style.overflowY,"
			+ "  \"both\": style.overflow"
			+ "};"
			+ "return _obj;";

	private static final String SCRIPT_SET_ELEMENT_OVERFLOW = "var style = arguments[0].style;"
			+ "style.overflowX = arguments[1];"
			+ "style.overflowY = arguments[2];";
	private static final String SCRIPT_SET_FRAME_OVERFLOW = "var style = arguments[0].contentWindow.document.documentElement.style;"
			+ "style.overflowX = arguments[1];"
			+ "style.overflowY = arguments[2];";
	private static final String SCRIPT_GET_ELEMENT_RESIZE = "return arguments[0].style.resize";
	private static final String SCRIPT_SET_ELEMENT_RESIZE = "var style = arguments[0].style;"
			+ "style.resize = arguments[1]";
	private static final long SCROLL_WAIT_MS = 100L;

	//CHECKSTYLE:ON
	//@formatter:on

	private static final Pattern PATTERN_NUMBER = Pattern.compile("-?[\\d\\.]+");
	private static final long SHOW_HIDE_TIMEOUT = 30L;

	private PtlWebDriver driver;
	private String tagName;
	private PtlWebElement frameParent;
	private boolean frameSwitching;

	/**
	 * コンストラクタ
	 */
	protected PtlWebElement() {
	}

	/**
	 * 自身がフレーム内コンテンツに所属する場合、該当の親フレーム要素を取得します。
	 * 
	 * @return 親フレーム要素
	 */
	public PtlWebElement getFrameParent() {
		return frameParent;
	}

	/**
	 * 自身がフレーム内コンテンツに所属する場合、該当の親フレーム要素を設定します。
	 * 
	 * @param frameParent 親フレーム要素
	 */
	public void setFrameParent(PtlWebElement frameParent) {
		this.frameParent = frameParent;
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

	@Override
	protected Response execute(String command, Map<String, ?> parameters) {
		if (frameParent == null) {
			return super.execute(command, parameters);
		}

		// in frame
		driver.switchTo().frame(frameParent);
		try {
			return super.execute(command, parameters);
		} finally {
			driver.switchTo().defaultContent();
		}
	}

	@Override
	protected WebElement findElement(final String using, final String value) {
		if (!isFrame()) {
			return super.findElement(using, value);
		}

		return executeInFrame(new Supplier<WebElement>() {
			@Override
			public WebElement get() {
				try {
					// TODO cache reflection object?
					Method findElement = RemoteWebDriver.class.getDeclaredMethod("findElement", String.class,
							String.class);
					findElement.setAccessible(true);

					PtlWebElement element = (PtlWebElement) findElement.invoke(driver, using, value);
					element.setFrameParent(PtlWebElement.this);
					return element;
				} catch (Exception e) {
					throw new TestRuntimeException(e);
				}
			}
		});
	}

	@Override
	protected List<WebElement> findElements(final String using, final String value) {
		if (!isFrame()) {
			return super.findElements(using, value);
		}

		return executeInFrame(new Supplier<List<WebElement>>() {
			@Override
			public List<WebElement> get() {
				try {
					// TODO cache reflection object?
					Method findElement = RemoteWebDriver.class.getDeclaredMethod("findElements", String.class,
							String.class);
					findElement.setAccessible(true);

					List<WebElement> elements = PtlWebElement.super.findElements(using, value);
					for (WebElement element : elements) {
						((PtlWebElement) element).setFrameParent(PtlWebElement.this);
					}

					return elements;
				} catch (Exception e) {
					throw new TestRuntimeException(e);
				}
			}
		});
	}

	/**
	 * frameまたはiframeの要素、またはフレーム内の要素において、WebDriverのフレームスイッチを行った状態で操作します。
	 * 
	 * @param doInFrame 操作内容
	 */
	public void executeInFrame(Runnable doInFrame) {
		PtlWebElement parent = getFrameParent();
		if (!isFrame() && parent == null) {
			doInFrame.run();
		}

		executeInFrame(parent != null ? parent : this, doInFrame);
	}

	/**
	 * frameまたはiframeの要素、またはフレーム内の要素において、WebDriverのフレームスイッチを行った状態で操作します。
	 * 
	 * @param doInFrame 操作内容
	 */
	public <T> T executeInFrame(Supplier<T> doInFrame) {
		PtlWebElement parent = getFrameParent();
		if (!isFrame() && parent == null) {
			return doInFrame.get();
		}

		return executeInFrame(parent != null ? parent : this, doInFrame);
	}

	private void executeInFrame(WebElement frameElement, Runnable doInFrame) {
		if (frameSwitching) {
			LOG.trace("(executeInFrame) already switched");
			doInFrame.run();
			return;
		}

		driver.switchTo().frame(frameElement);
		frameSwitching = true;
		LOG.trace("(executeInFrame) switch to frame [{}]", frameElement);
		try {
			doInFrame.run();
		} finally {
			frameSwitching = false;
			driver.switchTo().defaultContent();
			LOG.trace("(executeInFrame) switch to default content");
		}
	}

	private <T> T executeInFrame(WebElement frameElement, Supplier<T> doInFrame) {
		if (frameSwitching) {
			LOG.trace("(executeInFrame) already switched");
			return doInFrame.get();
		}

		driver.switchTo().frame(frameElement);
		LOG.trace("(executeInFrame) switch to frame [{}]", frameElement);
		frameSwitching = true;
		try {
			return doInFrame.get();
		} finally {
			frameSwitching = false;
			driver.switchTo().defaultContent();
			LOG.trace("(executeInFrame) switch to default content");
		}
	}

	/**
	 * JavaScriptコードを実行します。フレーム内要素の場合、要素が存在するフレームにスイッチしてから実行します。
	 * 
	 * @param script 実行するコード
	 * @param params 実行パラメータ
	 * @param <T> 戻り値の型
	 * @return JavaScriptコードの戻り値
	 */
	protected <T> T executeJavaScript(final String script, final Object... params) {
		PtlWebElement parent = getFrameParent();
		if (parent == null) {
			return driver.executeJavaScript(script, params);
		}

		return executeInFrame(parent, new Supplier<T>() {
			@Override
			public T get() {
				return driver.executeJavaScript(script, params);
			}
		});
	}

	/**
	 * 要素の位置・サイズを矩形領域として取得します。
	 * 
	 * @return 矩形領域を表す{@link DoubleValueRect}オブジェクト
	 */
	public DoubleValueRect getDoubleValueRect() {
		return executeInFrame(new Supplier<DoubleValueRect>() {
			@Override
			public DoubleValueRect get() {
				// 現在のスクロール位置を取得
				double scrollTop = driver.getCurrentScrollTop();
				double scrollLeft = driver.getCurrentScrollLeft();
				LOG.trace("(GetRect) current scroll position: (top: {}, left: {})", scrollTop, scrollLeft);

				double left = 0;
				double top = 0;
				double width = 0;
				double height = 0;

				// ページ最上部からの座標を取得
				try {
					driver.scrollTo(0d, 0d);
					Map<String, Object> object = executeJavaScript(GET_ELEMENT_RECT_SCRIPT, PtlWebElement.this);
					LOG.trace("(GetRect) js result: {}", object);

					left = getDoubleOrDefault(object.get("left"), 0d);
					top = getDoubleOrDefault(object.get("top"), 0d);

					// bodyの場合はページ全体の幅を返す
					width = isBody() ? driver.getCurrentPageWidth() : getDoubleOrDefault(object.get("width"), 0d);

					// bodyの場合はページ全体の高さを返す
					if (isBody()) {
						// bodyの場合はページ全体の高さを返す
						height = driver.getCurrentPageHeight();
					} else {
						// 小数値による描画ずれ対策として、要素の下端と上端の座標を丸めた値から高さを計算
						height = Math.round(top + getDoubleOrDefault(object.get("height"), 0d)) - Math.round(top);
					}

					// スクロール位置を元に戻す
					driver.scrollTo(scrollLeft, scrollTop);
				} catch (InterruptedException e) {
					throw new TestRuntimeException(e);
				}

				DoubleValueRect rect = new DoubleValueRect(left, top, width, height);
				LOG.debug("[Element Rect] {} ({})", rect, PtlWebElement.this);
				return rect;
			}
		});
	}

	/**
	 * 要素の四辺のMarginを取得します。
	 * 
	 * @return 四辺のMarginを表す{@link WebElementMargin}オブジェクト
	 */
	public WebElementMargin getMargin() {
		Map<String, Object> object = executeJavaScript(GET_ELEMENT_MARGIN_SCRIPT, this);
		LOG.trace("(GetMargin) js result: {}", object);

		double top = getDoubleOrDefault(object.get("top"), 0d);
		double left = getDoubleOrDefault(object.get("left"), 0d);
		double bottom = getDoubleOrDefault(object.get("bottom"), 0d);
		double right = getDoubleOrDefault(object.get("right"), 0d);

		WebElementMargin margin = new WebElementMargin(top, right, bottom, left);
		LOG.debug("[Element Margin] {} ({})", margin, this);
		return margin;
	}

	/**
	 * 要素の四辺のBorderWidthを取得します。
	 * 
	 * @return 四辺のBorderWidthを表す{@link WebElementBorderWidth}オブジェクト
	 */
	public WebElementBorderWidth getBorderWidth() {
		Map<String, Object> object = executeJavaScript(GET_ELEMENT_BORDER_WIDTH_SCRIPT, this);
		LOG.trace("(GetBorderWidth) js result: {}", object);

		double top = getDoubleOrDefault(object.get("top"), 0d);
		double left = getDoubleOrDefault(object.get("left"), 0d);
		double bottom = getDoubleOrDefault(object.get("bottom"), 0d);
		double right = getDoubleOrDefault(object.get("right"), 0d);

		WebElementBorderWidth borderWidth = new WebElementBorderWidth(top, right, bottom, left);
		LOG.debug("[Element BorderWidth] {} ({})", borderWidth, this);
		return borderWidth;
	}

	/**
	 * 要素の四辺のPaddingを取得します。
	 * 
	 * @return 四辺のPaddingを表す{@link WebElementPadding}オブジェクト
	 */
	public WebElementPadding getPadding() {
		Map<String, Object> object = executeJavaScript(GET_ELEMENT_PADDING_SCRIPT, this);
		LOG.trace("(GetPadding) js result: {}", object);

		double top = getDoubleOrDefault(object.get("top"), 0d);
		double left = getDoubleOrDefault(object.get("left"), 0d);
		double bottom = getDoubleOrDefault(object.get("bottom"), 0d);
		double right = getDoubleOrDefault(object.get("right"), 0d);

		WebElementPadding padding = new WebElementPadding(top, right, bottom, left);
		LOG.debug("[Element Padding] {} ({})", padding, this);
		return padding;
	}

	/**
	 * 要素を非表示状態にします。
	 */
	public void hide() {
		if (isVisibilityHidden() && !isDisplayed()) {
			LOG.debug("[Hide element] already hidden. ({})", this);
			return;
		}

		LOG.debug("[Hide element start] ({})", this);
		driver.executeScript("return arguments[0].style.visibility = 'hidden'", this);
		try {
			new WebDriverWait(driver, SHOW_HIDE_TIMEOUT).until(new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver webDriver) {
					return isVisibilityHidden() && !isDisplayed();
				}
			});
			LOG.debug("[Hide element finished] ({})", this);
		} catch (TimeoutException e) {
			LOG.warn("[Hide element timeout] ({})", this);
		}
	}

	/**
	 * 要素を表示状態にします。
	 */
	public void show() {
		if (!isVisibilityHidden() && isDisplayed()) {
			LOG.debug("[Show element] already visible. ({})", this);
			return;
		}

		LOG.debug("[Show element start] ({})", this);
		executeJavaScript("return arguments[0].style.visibility = 'visible'", this);
		try {
			new WebDriverWait(driver, SHOW_HIDE_TIMEOUT).until(new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver webDriver) {
					return !isVisibilityHidden() && isDisplayed();
				}
			});
			LOG.debug("[Show element finished] ({})", this);
		} catch (TimeoutException e) {
			LOG.warn("[Show element timeout] {}", this);
		}
	}

	/**
	 * 要素のvisibilityスタイル値がhiddenかどうかを取得します。
	 * 
	 * @return visibilityがhiddenの場合true、それ以外の値の場合false
	 */
	public boolean isVisibilityHidden() {
		return executeJavaScript("return arguments[0].style.visibility == 'hidden'", this);
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
	 * 自身の部分スクロールのスクロール回数を返します。<br>
	 * 部分スクロールがない場合は0を返します。
	 * 
	 * @return スクロール回数
	 */
	public int getScrollNum() {
		double clientHeight = getClientHeight();
		double scrollHeight = getScrollHeight() + 1;

		if (clientHeight >= scrollHeight) {
			LOG.trace("(GetScrollNum) [0] ({})", this);
			return 0;
		}

		int number = (int) (Math.ceil(scrollHeight / clientHeight)) - 1;
		LOG.trace("(GetScrollNum) [{}] ({})", number, this);
		return number;
	}

	/**
	 * 要素の可視範囲の高さを取得します。
	 * 
	 * @return 高さ（整数px）
	 */
	public long getClientHeight() {
		DoubleValueRect rect = getDoubleValueRect();
		WebElementBorderWidth border = getBorderWidth();
		long height = Math.round(rect.getHeight() - border.getTop() - border.getBottom());
		LOG.trace("(GetClientHeight) [{}] ({})", height, this);
		return height;
	}

	/**
	 * 要素の可視範囲の幅を取得します。
	 * 
	 * @return 幅（整数px）
	 */
	public long getClientWidth() {
		DoubleValueRect rect = getDoubleValueRect();
		WebElementBorderWidth border = getBorderWidth();
		long width = Math.round(rect.getWidth() - border.getLeft() - border.getRight());
		LOG.trace("(GetClientWidth) [{}] ({})", width, this);
		return width;
	}

	/**
	 * スクロールを含む要素全体の高さを取得します。
	 * 
	 * @return 高さ（整数px）
	 */
	public long getScrollHeight() {
		String result;
		if (isFrame()) {
			result = executeJavaScript("return arguments[0].contentWindow.document.documentElement.scrollHeight", this)
					.toString();
		} else {
			result = executeJavaScript("return arguments[0].scrollHeight", this).toString();
		}
		LOG.trace("(GetScrollHeight) [{}] ({})", result, this);
		return Long.parseLong(result);
	}

	/**
	 * スクロールを含む要素全体の幅を取得します。
	 * 
	 * @return 幅（整数px）
	 */
	public long getScrollWidth() {
		String result;
		if (isFrame()) {
			result = executeJavaScript("return arguments[0].contentWindow.document.documentElement.scrollWidth", this)
					.toString();
		} else {
			result = executeJavaScript("return arguments[0].scrollWidth", this).toString();
		}
		LOG.trace("(GetScrollWidth) [{}] ({})", result, this);
		return Long.parseLong(result);
	}

	/**
	 * 要素を1回分スクロールします。
	 * 
	 * @return 今回のスクロール量
	 * @throws InterruptedException スクロール中に例外が発生した場合
	 */
	public int scrollNext() throws InterruptedException {
		long initialScrollTop = (int) Math.round(getCurrentScrollTop());
		long clientHeight = getClientHeight();
		LOG.debug("[Scroll element] next to ({}, {}) ({})", 0, initialScrollTop + clientHeight, this);
		scrollTo(0, initialScrollTop + clientHeight);
		long currentScrollTop = (int) Math.round(getCurrentScrollTop());
		return (int) (currentScrollTop - initialScrollTop);
	}

	/**
	 * 現在のスクロール位置（y座標）を取得します。
	 * 
	 * @return スクロール位置（実数px）
	 */
	double getCurrentScrollTop() {
		double top = 0;
		if (isFrame()) {
			double max = 0d;
			for (String value : SCRIPTS_SCROLL_TOP) {
				double current = Double.parseDouble(executeJavaScript("return " + value, this).toString());
				max = Math.max(max, current);
			}
			top = max;
		} else {
			top = Double.parseDouble(executeJavaScript("return arguments[0].scrollTop", this).toString());
		}
		LOG.trace("(GetCurrentScrollTop) [{}] ({})", top, this);
		return top;
	}

	/**
	 * 現在のスクロール位置（x座標）を取得します。
	 * 
	 * @return スクロール位置（実数px）
	 */
	double getCurrentScrollLeft() {
		double top = 0;
		if (isFrame()) {
			double max = 0d;
			for (String value : SCRIPTS_SCROLL_LEFT) {
				double current = Double.parseDouble(executeJavaScript("return " + value, this).toString());
				max = Math.max(max, current);
			}
			top = max;
		} else {
			top = Double.parseDouble(executeJavaScript("return arguments[0].scrollLeft", this).toString());
		}
		LOG.trace("(GetCurrentScrollLeft) [{}] ({})", top, this);
		return top;
	}

	/**
	 * 指定位置までスクロールします。
	 * 
	 * @param x x座標
	 * @param y y座標
	 * @throws InterruptedException スクロール中に例外が発生した場合
	 */
	public void scrollTo(double x, double y) throws InterruptedException {
		// 要素がframeの場合
		if (isFrame()) {
			LOG.debug("[Scroll frame] to ({}, {})", x, y);
			executeJavaScript("arguments[0].contentWindow.scrollTo(arguments[1], arguments[2])", this, x, y);

			Thread.sleep(SCROLL_WAIT_MS);
			return;
		}

		// frame以外の場合
		LOG.debug("[Scroll element] to ({}, {})", x, y);
		executeJavaScript("arguments[0].scrollLeft = arguments[1]", this, x);
		executeJavaScript("arguments[0].scrollTop = arguments[1]", this, y);

		Thread.sleep(SCROLL_WAIT_MS);
	}

	/**
	 * スクロールバーを非表示にします。
	 */
	public void hideScrollBar() {
		setOverflowStatus("hidden", "hidden");
	}

	/**
	 * 要素をリサイズ不可にします。
	 */
	public void setNoResizable() {
		setResizeStatus("none");
	}

	/**
	 * styleに設定されているoverflowの値を返します。
	 * 
	 * @return overflowの設定値 {x, y}
	 */
	public String[] getOverflowStatus() {
		Map<String, Object> object;
		if (isFrame()) {
			object = executeJavaScript(SCRIPT_GET_FRAME_OVERFLOW, this);
		} else {
			object = executeJavaScript(SCRIPT_GET_ELEMENT_OVERFLOW, this);
		}

		String xStatus;
		String yStatus;
		if (object.get("both") != null && !"".equals(object.get("both"))) {
			xStatus = object.get("both").toString();
			yStatus = xStatus;
		} else {
			xStatus = object.get("x") != null ? object.get("x").toString() : "";
			yStatus = object.get("y") != null ? object.get("y").toString() : "";
		}

		String[] result = { xStatus, yStatus };
		LOG.trace("(GetOverflowStatus) [{}] ({})", result, this);
		return result;
	}

	/**
	 * Overflowのstyleを設定します。
	 * 
	 * @param xStatus x方向の設定
	 * @param yStatus y方向の設定
	 */
	public void setOverflowStatus(String xStatus, String yStatus) {
		LOG.trace("(SetOverflowStatus) overflowX: {}, overflowY: {} ({})", xStatus, yStatus, this);
		if (isFrame()) {
			executeJavaScript(SCRIPT_SET_FRAME_OVERFLOW, this, xStatus, yStatus);
		} else {
			executeJavaScript(SCRIPT_SET_ELEMENT_OVERFLOW, this, xStatus, yStatus);
		}
	}

	/**
	 * styleに設定されているresizeの値を返します。
	 * 
	 * @return resizeの設定値
	 */
	public String getResizeStatus() {
		Object resizeStatus = executeJavaScript(SCRIPT_GET_ELEMENT_RESIZE, this);
		return resizeStatus != null ? resizeStatus.toString() : "";
	}

	/**
	 * resizeのstyleを設定します。
	 * 
	 * @param status resizeの設定
	 */
	public void setResizeStatus(String status) {
		executeJavaScript(SCRIPT_SET_ELEMENT_RESIZE, this, status);
	}

	/**
	 * 要素がbody（およびframeset）か否かを返します。
	 * 
	 * @return この要素がbody（およびframeset）か否か。該当する場合はtrue。
	 */
	public boolean isBody() {
		return "body".equals(getTagName()) || "frameset".equals(getTagName());
	}

	/**
	 * 要素がframeおよびifameか否かを返します。
	 * 
	 * @return この要素がframeおよびiframeか否か。該当する場合はtrue。
	 */
	public boolean isFrame() {
		return "iframe".equals(getTagName()) || "frame".equals(getTagName());
	}

	/**
	 * 指定された位置（スクロールi回目）のスクリーンショットに含まれるPaddingの高さを返します。
	 * 
	 * @param i スクロール位置
	 * @param size 総スクロール回数
	 * @return Paddingの高さ
	 */
	protected int getContainedPaddingHeight(int i, int size) {
		return 0;
	}

	/**
	 * 指定された位置（スクロールi回目）のスクリーンショットに含まれるPaddingの幅を返します。
	 * 
	 * @param i スクロール位置
	 * @param size 総スクロール回数
	 * @return Paddingの幅
	 */
	protected int getContainedPaddingWidth(int i, int size) {
		return 0;
	}

}
