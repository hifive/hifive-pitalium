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
package com.htmlhifive.pitalium.core.model;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htmlhifive.pitalium.core.selenium.PtlWebDriver;
import com.htmlhifive.pitalium.core.selenium.PtlWebElement;
import com.htmlhifive.pitalium.core.selenium.DoubleValueRect;
import com.htmlhifive.pitalium.image.model.RectangleArea;

/**
 * {@link ScreenArea}を処理するためのラッパークラス
 */
public abstract class ScreenAreaWrapper {

	/**
	 * 生成元のScreenArea
	 */
	protected final ScreenArea parent;
	/**
	 * ブラウザにアクセスするためのWebDriver
	 */
	protected final PtlWebDriver driver;
	/**
	 * ScreenAreaに対応するWebElement
	 */
	protected final PtlWebElement element;

	/**
	 * ScreenAreaの矩形領域
	 */
	protected RectangleArea area;

	/**
	 * コンストラクタ
	 * 
	 * @param parent 生成元のScreenArea
	 * @param driver WebDriver
	 * @param element 対応するWebElement
	 */
	protected ScreenAreaWrapper(ScreenArea parent, PtlWebDriver driver, PtlWebElement element) {
		this.parent = parent;
		this.driver = driver;
		this.element = element;
	}

	/**
	 * 生成元の{@link ScreenArea}を取得します。
	 * 
	 * @return ScreenAreaオブジェクト
	 */
	public ScreenArea getParent() {
		return parent;
	}

	/**
	 * WebDriverを取得します。
	 * 
	 * @return WebDriver
	 */
	public PtlWebDriver getDriver() {
		return driver;
	}

	/**
	 * 対応するWebElementを取得します。
	 * 
	 * @return WebElement
	 */
	public PtlWebElement getElement() {
		return element;
	}

	/**
	 * セレクターを取得します。
	 * 
	 * @return セレクタ
	 */
	public abstract DomSelector getSelector();

	/**
	 * 指定のエリア情報を取得します。
	 * 
	 * @return 指定されたエリア
	 */
	public abstract RectangleArea getTargetArea();

	/**
	 * スクリーンショット上のエリア情報を取得します。
	 * 
	 * @return 撮影したエリア
	 */
	public RectangleArea getArea() {
		return area;
	}

	/**
	 * 矩形領域を設定します。
	 * 
	 * @param area 矩形領域
	 */
	public void setArea(RectangleArea area) {
		this.area = area;
	}

	/**
	 * 指定対象がbodyであるかどうかを取得します。
	 * 
	 * @return bodyならtrue、それ以外の場合はfalse
	 */
	public abstract boolean isBody();

	/**
	 * 現在の位置を更新します。
	 * 
	 * @param scale 表示スケール
	 */
	public abstract void updatePosition(double scale);

	/**
	 * 現在の位置を更新します。
	 * 
	 * @param scale 表示スケール
	 * @param moveX X方向の移動量
	 * @param moveY Y方向の移動量
	 */
	public abstract void updatePosition(double scale, double moveX, double moveY);

	/**
	 * 対象要素の子要素のラッパーを取得します。
	 * 
	 * @param target 対象要素
	 * @return ラッパーのリスト
	 */
	public abstract List<ScreenAreaWrapper> getChildWrapper(ScreenArea target);

	/**
	 * {@link ScreenArea}を受け取ってラッパーを提供します。セレクタに一致する要素が複数ある場合は、その数だけラッパーを生成します。
	 * 
	 * @param screenArea 対象の{@link ScreenArea}
	 * @param driver WebDriver
	 * @param element 親要素。この要素以下でセレクタに一致する要素を探索します。
	 * @return 処理を実行するためのラッパーのリスト
	 */
	public static List<ScreenAreaWrapper> fromArea(ScreenArea screenArea, PtlWebDriver driver, PtlWebElement element) {
		if (screenArea.getSelector() == null) {
			return createRectangleWrapper(screenArea, driver, element);
		} else {
			return createDomWrapper(screenArea, driver, element);
		}
	}

	/**
	 * 矩形領域を表現するためのScreenAreaWrapperクラスを生成します。
	 * 
	 * @param screenArea 生成元のScreenArea
	 * @param driver WebDriver
	 * @param element 対応するWebElement
	 * @return ScreenAreaWrapperオブジェクトのリスト
	 */
	private static List<ScreenAreaWrapper> createRectangleWrapper(ScreenArea screenArea, PtlWebDriver driver,
			PtlWebElement element) {
		List<ScreenAreaWrapper> list = new ArrayList<ScreenAreaWrapper>();
		list.add(new RectangleScreenAreaWrapper(screenArea, driver, element));
		return list;
	}

	/**
	 * DOM要素を表現するためのScreenAreaWrapperクラスを生成します。
	 * 
	 * @param screenArea 生成元のScreenArea
	 * @param driver WebDriver
	 * @param element 対応するWebElement
	 * @return ScreenAreaWrapperオブジェクトのリスト
	 */
	private static List<ScreenAreaWrapper> createDomWrapper(ScreenArea screenArea, PtlWebDriver driver,
			PtlWebElement element) {
		DomSelector selector = screenArea.getSelector();
		List<WebElement> elements;
		if (element == null) {
			elements = selector.getType().findElements(driver, selector.getValue());
		} else {
			elements = selector.getType().findElements(element, selector.getValue());
		}

		if (elements.isEmpty()) {
			return new ArrayList<ScreenAreaWrapper>();
		}

		List<ScreenAreaWrapper> results = new ArrayList<ScreenAreaWrapper>(elements.size());
		for (WebElement el : elements) {
			results.add(new DomScreenAreaWrapper(screenArea, driver, (PtlWebElement) el));
		}

		return results;
	}

	//<editor-fold desc="DOM">
	/**
	 * DOM要素を表現するためのScreenAreaWrapperクラス
	 */
	static class DomScreenAreaWrapper extends ScreenAreaWrapper {

		private static final Logger LOG = LoggerFactory.getLogger(DomScreenAreaWrapper.class);

		/**
		 * DOM要素のセレクタ
		 */
		private final DomSelector selector;

		/**
		 * コンストラクタ
		 * 
		 * @param parent 生成元のScreenArea
		 * @param driver WebDriver
		 * @param element 対応するWebElement
		 */
		DomScreenAreaWrapper(ScreenArea parent, PtlWebDriver driver, PtlWebElement element) {
			super(parent, driver, element);

			selector = parent.getSelector();
		}

		@Override
		public DomSelector getSelector() {
			return selector;
		}

		@Override
		public RectangleArea getTargetArea() {
			return null;
		}

		@Override
		public boolean isBody() {
			return "body".equalsIgnoreCase(element.getTagName());
		}

		@Override
		public void updatePosition(double scale) {
			DoubleValueRect rect = element.getDoubleValueRect();
			LOG.trace("Position update. scale: {}; rect: {} ({})", scale, rect, element);

			area = new RectangleArea(rect.getLeft(), rect.getTop(), rect.getWidth(), rect.getHeight());
			if (scale != PtlWebDriver.DEFAULT_SCREENSHOT_SCALE) {
				area = area.applyScale(scale);
			}
			LOG.debug("Position updated. {} ({})", area, element);
		}

		@Override
		public void updatePosition(double scale, double moveX, double moveY) {
			updatePosition(scale);
		}

		@Override
		public List<ScreenAreaWrapper> getChildWrapper(ScreenArea target) {
			return fromArea(target, driver, element);
		}
	}

	//</editor-fold>

	//<editor-fold desc="Rectangle">

	/**
	 * 矩形領域を表現するためのScreenAreaWrapperクラス
	 */
	static class RectangleScreenAreaWrapper extends ScreenAreaWrapper {

		private static final Logger LOG = LoggerFactory.getLogger(RectangleScreenAreaWrapper.class);

		/**
		 * 指定された矩形領域
		 */
		private final RectangleArea target;

		/**
		 * コンストラクタ
		 * 
		 * @param parent 生成元のScreenArea
		 * @param driver WebDriver
		 * @param element 対応するWebElement
		 */
		RectangleScreenAreaWrapper(ScreenArea parent, PtlWebDriver driver, PtlWebElement element) {
			super(parent, driver, element);

			target = parent.getRectangle();
		}

		@Override
		public DomSelector getSelector() {
			return null;
		}

		@Override
		public RectangleArea getTargetArea() {
			return target;
		}

		@Override
		public boolean isBody() {
			return false;
		}

		@Override
		public void updatePosition(double scale) {
			area = scale == 0d ? target : target.applyScale(scale);
			LOG.debug("Position updated. {} ({})", area, target);
		}

		@Override
		public void updatePosition(double scale, double moveX, double moveY) {
			LOG.trace("Position update. scale: {}; x: {}; y: {} ({})", scale, moveX, moveY, element);
			RectangleArea area = new RectangleArea(target.getX() - moveX, target.getY() - moveX, target.getWidth(),
					target.getHeight());
			this.area = scale == PtlWebDriver.DEFAULT_SCREENSHOT_SCALE ? area : area.applyScale(scale);
			LOG.debug("Position updated. {} ({})", area, target);
		}

		@Override
		public List<ScreenAreaWrapper> getChildWrapper(ScreenArea screenArea) {
			return fromArea(screenArea, driver, null);
		}
	}
	//</editor-fold>

}
