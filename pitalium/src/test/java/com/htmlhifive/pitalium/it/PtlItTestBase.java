/*
 * Copyright (C) 2015-2017 NS Solutions Corporation
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

package com.htmlhifive.pitalium.it;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.config.ExecMode;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.io.PersistMetadata;
import com.htmlhifive.pitalium.core.io.Persister;
import com.htmlhifive.pitalium.core.model.TargetResult;
import com.htmlhifive.pitalium.core.result.TestResultManager;
import com.htmlhifive.pitalium.image.model.RectangleArea;
import com.htmlhifive.pitalium.image.util.ImageUtils;

/**
 * ITテストのベースクラス
 */
public class PtlItTestBase extends PtlTestBase {

	protected final Logger LOG = LoggerFactory.getLogger(getClass());

	@Rule
	public TestName testName = new TestName();

	private String currentId;
	private Persister persister;
	private String methodName;

	@Before
	public void initialize() throws Exception {
		TestResultManager testResultManager = TestResultManager.getInstance();
		currentId = testResultManager.getCurrentId();
		persister = testResultManager.getPersister();
		methodName = testName.getMethodName().split("\\[")[0];
	}

	//<editor-fold desc="Open URL">

	/**
	 * テスト標準ページを {@code text: true, color: false, image: false, date: false} の状態で開きます。
	 */
	public void openBasicTextPage() {
		openBasicTextPage(false);
	}

	/**
	 * テスト標準ページを {@code text: true, color: false, image: false, date: ?} の状態で開きます。
	 */
	public void openBasicTextPage(boolean date) {
		openBasicPage(true, false, false, date);
	}

	/**
	 * テスト標準ページを {@code text: false, color: true, image: false, date: false} の状態で開きます。
	 */
	public void openBasicColorPage() {
		openBasicPage(false, true, false, false);
	}

	/**
	 * テスト標準ページを {@code text: false, color: false, image: true, date: false} の状態で開きます。
	 */
	public void openBasicImagePage() {
		openBasicPage(false, false, true, false);
	}

	/**
	 * テスト標準ページを開きます。
	 *
	 * @param date subtitleを日付にするかどうか
	 */
	public void openBasicPage(boolean text, boolean color, boolean image, boolean date) {
		List<String> types = new ArrayList<>();
		if (text) {
			types.add("text");
		}
		if (color) {
			types.add("color");
		}
		if (image) {
			types.add("image");
		}
		String type = StringUtils.join(types.toArray(new String[1]));
		driver.get(String.format(Locale.US, "?type=%s&date=%s", type, date));
	}

	/**
	 * 全体グラデーションページを {@code width: 100%; height: 100%} で開きます。
	 */
	public void openGradationPage() {
		openGradationPage("100%", "100%");
	}

	/**
	 * 全体グラデーションページを {@code width: 80%; height: 80%} で開きます。
	 */
	public void openGradationPageSmall() {
		openGradationPage("80%", "80%");
	}

	/**
	 * 全体グラデーションページを {@code width: 160%; height: 160%} で開きます。
	 */
	public void openGradationPageMedium() {
		openGradationPage("160%", "160%");
	}

	/**
	 * 全体グラデーションページを {@code width: 240%; height: 240%} で開きます。
	 */
	public void openGradationPageLarge() {
		openGradationPage("240%", "240%");
	}

	/**
	 * 全体グラデーションページを開きます。
	 *
	 * @param width bodyの幅
	 * @param height bodyの高さ
	 */
	public void openGradationPage(String width, String height) {
		driver.get(String.format(Locale.US, "gradation.html?width=%s&height=%s", width, height));
	}

	/**
	 * 部分スクロールテスト用のページを開きます。
	 */
	public void openScrollPage() {
		driver.get("element-scroll.html");
	}

	/**
	 * iframeテスト用のページを開きます。
	 */
	public void openIFramePage() {
		driver.get("iframe.html");
	}

	//</editor-fold>

	/**
	 * 現在テスト中のブラウザがIEまたはEdgeであるかどうかを取得します。
	 */
	public boolean isMicrosoftBrowser() {
		return isInternetExplorer() || isMicrosoftEdge();
	}

	/**
	 * 現在テスト中のブラウザがIEかどうかを取得します。
	 */
	public boolean isInternetExplorer() {
		return BrowserType.IE.equals(capabilities.getBrowserName());
	}

	/**
	 * 現在テスト中のブラウザがIE8かどうかを取得します。
	 */
	public boolean isInternetExplorer8() {
		return isInternetExplorer() && "8".equals(capabilities.getVersion());
	}

	/**
	 * 現在テスト中のブラウザがIE9かどうかを取得します。
	 */
	public boolean isInternetExplorer9() {
		return isInternetExplorer() && "9".equals(capabilities.getVersion());
	}

	/**
	 * 現在テスト中のブラウザがEdgeかどうかを取得します。
	 */
	public boolean isMicrosoftEdge() {
		return BrowserType.EDGE.equals(capabilities.getBrowserName());
	}

	/**
	 * ブラウザのピクセル比を取得します。
	 *
	 * @return ピクセル比
	 */
	public double getPixelRatio() {
		return driver.<Number> executeJavaScript("return window.getPixelRatio();").doubleValue();
	}

	/**
	 * スクリーンショット撮影結果を取得します。
	 *
	 * @param ssid スクリーンショットID
	 * @return スクリーンショット撮影結果
	 */
	public List<TargetResult> loadTargetResults(String ssid) {
		PersistMetadata metadata = new PersistMetadata(currentId, getClass().getSimpleName(), methodName, ssid,
				capabilities);
		return persister.loadTargetResults(metadata);
	}

	/**
	 * 現在の実行モードを取得します。
	 *
	 * @return 現在の実行モード
	 */
	public static ExecMode getCurrentMode() {
		return PtlTestConfig.getInstance().getEnvironment().getExecMode();
	}

	/**
	 * 現在の実行モードが画像アサートをするモードかどうかを取得します。
	 *
	 * @return 画像アサートをするモードかどうか
	 */
	public static boolean isRunTest() {
		return getCurrentMode().isRunTest();
	}

	/**
	 * テスト対象がモバイル表示かどうかを取得します。
	 */
	public boolean isMobile() {
		// Bootstrapの表示がモバイル向けになる値
		return driver.executeJavaScript("return window.innerWidth < 768;");
	}

	/**
	 * devicePixelRatioからピクセル換算した要素の座標と大きさを取得します。
	 *
	 * @param id 要素のID
	 * @return ピクセル換算した要素の座標と大きさ
	 */
	public Rect getPixelRectById(String id) {
		Map<String, Number> rect = driver.executeJavaScript("" + "var id = arguments[0];"
				+ "var element = document.getElementById(id);" + "return element.getPixelRect();", id);
		return getPixelRect(rect);
	}

	/**
	 * devicePixelRatioからピクセル換算した要素の座標と大きさを取得します。
	 *
	 * @param selector 要素を示すセレクタ
	 * @return ピクセル換算した要素の座標と大きさ
	 */
	public Rect getPixelRectBySelector(String selector) {
		Map<String, Number> rect = driver.executeJavaScript("" + "var selector = arguments[0];"
				+ "var element = document.querySelector(selector);" + "return element.getPixelRect();", selector);
		return getPixelRect(rect);
	}

	/**
	 * devicePixelRatioからピクセル換算した要素の座標と大きさを取得します。
	 *
	 * @param element 要素
	 * @return ピクセル換算した要素の座標と大きさ
	 */
	public Rect getPixelRect(WebElement element) {
		Map<String, Number> rect = driver
				.executeJavaScript("" + "var element = arguments[0];" + "return element.getPixelRect();", element);
		return getPixelRect(rect);
	}

	/**
	 * devicePixelRatioからピクセル換算した要素の座標と大きさを取得します。
	 *
	 * @param rect 座標と大きさを含んだマップ
	 * @return ピクセル換算した要素の座標と大きさ
	 */
	public Rect getPixelRect(Map<String, Number> rect) {
		return getRect(rect).round();
	}

	/**
	 * 要素の座標と大きさを取得します。
	 *
	 * @param id 要素のID
	 * @return 要素の座標と大きさ
	 */
	public Rect getRectById(String id) {
		Map<String, Number> rect = driver.executeJavaScript(
				"" + "var id = arguments[0];" + "var element = document.getElementById(id);"
						+ "var rect = element.getPtlBoundingClientRect();" + "var result = {" + "  x: rect.left,"
						+ "  y: rect.top," + "  width: rect.width," + "  height: rect.height" + "};" + "return result;",
				id);
		return getRect(rect);
	}

	/**
	 * 要素の座標と大きさを取得します。
	 *
	 * @param selector 要素を示すセレクタ
	 * @return 要素の座標と大きさ
	 */
	public Rect getRectBySelector(String selector) {
		Map<String, Number> rect = driver.executeJavaScript(
				"" + "var selector = arguments[0];" + "var element = document.querySelector(selector);"
						+ "var rect = element.getPtlBoundingClientRect();" + "var result = {" + "  x: rect.left,"
						+ "  y: rect.top," + "  width: rect.width," + "  height: rect.height" + "};" + "return result;",
				selector);
		return getRect(rect);
	}

	/**
	 * 要素の座標と大きさを取得します。
	 *
	 * @param element 要素
	 * @return 要素の座標と大きさ
	 */
	public Rect getRect(WebElement element) {
		Map<String, Number> rect = driver.executeJavaScript("" + "var element = arguments[0];"
				+ "var rect = element.getPtlBoundingClientRect();" + "var result = {" + "  x: rect.left,"
				+ "  y: rect.top," + "  width: rect.width," + "  height: rect.height" + "};" + "return result;",
				element);
		return getRect(rect);
	}

	/**
	 * 要素の座標と大きさを取得します。
	 *
	 * @param rect 座標と大きさを含んだマップ
	 * @return 要素の座標と大きさ
	 */
	public Rect getRect(Map<String, Number> rect) {
		double x = rect.get("x").doubleValue();
		double y = rect.get("y").doubleValue();
		double width = rect.get("width").doubleValue();
		double height = rect.get("height").doubleValue();
		return new Rect(x, y, width, height);
	}

	/**
	 * 要素の座標と大きさ
	 */
	public static class Rect {

		/**
		 * 要素の左上のX座標
		 */
		public final double x;
		/**
		 * 要素の左上のY座標
		 */
		public final double y;
		/**
		 * 要素の幅
		 */
		public final double width;
		/**
		 * 要素の高さ
		 */
		public final double height;

		public Rect(double x, double y, double width, double height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		/**
		 * 各値を四捨五入します。
		 */
		public Rect round() {
			double x = Math.round(this.x);
			double y = Math.round(this.y);
			double width = Math.round(this.width);
			double height = Math.round(this.height);
			return new Rect(x, y, width, height);
		}

		public Rect toTargetRect() {
			double x = Math.ceil(this.x);
			double y = Math.ceil(this.y);
			double width = Math.floor(this.x + this.width) - x;
			double height = Math.floor(this.y + this.height) - y;
			return new Rect(x, y, width, height);
		}

		public Rect toExcludeRect() {
			double x = Math.floor(this.x);
			double y = Math.floor(this.y);
			double width = Math.ceil(this.x + this.width) - x;
			double height = Math.ceil(this.y + this.height) - y;
			return new Rect(x, y, width, height);
		}

		/**
		 * RectangleAreaへ変換します。
		 */
		public RectangleArea toRectangleArea() {
			return new RectangleArea(x, y, width, height);
		}

	}

	//<editor-fold desc="Color">

	/**
	 * 色情報
	 */
	public static class Color {

		public static final Color RED = Color.rgb(0xff, 0, 0);
		public static final Color GREEN = Color.rgb(0, 0xff, 0);
		public static final Color BLUE = Color.rgb(0, 0, 0xff);
		public static final Color BLACK = Color.rgb(0, 0, 0);
		public static final Color WHITE = Color.rgb(0xff, 0xff, 0xff);

		/**
		 * 色を表すintから赤色の情報を取り出します。
		 *
		 * @param color 色を表すint
		 * @return 赤色の情報
		 */
		public static int red(int color) {
			return (color >> 16) & 0xff;
		}

		/**
		 * 色を表すintから緑色の情報を取り出します。
		 *
		 * @param color 色を表すint
		 * @return 緑色の情報
		 */
		public static int green(int color) {
			return (color >> 8) & 0xff;
		}

		/**
		 * 色を表すintから青色の情報を取り出します。
		 *
		 * @param color 色を表すint
		 * @return 青色の情報
		 */
		public static int blue(int color) {
			return color & 0xff;
		}

		/**
		 * RGBそれぞれの値からColorを合成します。
		 *
		 * @param red 赤色
		 * @param green 緑色
		 * @param blue 青色
		 * @return 合成されたColor
		 */
		public static Color rgb(int red, int green, int blue) {
			return new Color(red, green, blue);
		}

		/**
		 * 色を表す文字列（{@code #RRGGBB}または{@code #RGB}形式）からColorを生成します。
		 *
		 * @param color 色を表す文字列
		 * @return 生成されたColor
		 */
		public static Color valueOf(String color) {
			if (color.charAt(0) != '#' && (color.length() != 4 || color.length() != 7)) {
				throw new IllegalArgumentException("invalid color: " + color);
			}

			color = color.substring(1);
			String red;
			String green;
			String blue;

			// #RGB
			if (color.length() == 3) {
				red = color.substring(0, 1) + color.substring(0, 1);
				green = color.substring(1, 2) + color.substring(1, 2);
				blue = color.substring(2, 3) + color.substring(2, 3);
			}
			// #RRGGBB
			else {
				red = color.substring(0, 2);
				green = color.substring(2, 4);
				blue = color.substring(4, 6);
			}

			return rgb(Integer.parseInt(red, 16), Integer.parseInt(green, 16), Integer.parseInt(blue, 16));
		}

		/**
		 * 色を表す数値からColorを生成します。
		 *
		 * @param color 色を表す数値
		 * @return 生成されたColor
		 */
		public static Color valueOf(int color) {
			return rgb(red(color), green(color), blue(color));
		}

		/**
		 * 元の色
		 */
		public final int color;
		/**
		 * 赤色
		 */
		public final int red;
		/**
		 * 緑色
		 */
		public final int green;
		/**
		 * 青色
		 */
		public final int blue;

		@SuppressWarnings("NumericOverflow")
		private Color(int red, int green, int blue) {
			this.color = (0xff << 24) | (red << 16) | (green << 15) | blue;
			this.red = red;
			this.green = green;
			this.blue = blue;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			Color color1 = (Color) o;

			return color == color1.color;
		}

		@Override
		public int hashCode() {
			return color;
		}

		@Override
		public String toString() {
			return String.format("0x%02x%02x%02x", red, green, blue);
		}

	}

	//</editor-fold>

	//<editor-fold desc="Matcher">

	/**
	 * Imageが等しいかどうかをチェックするMatcher
	 */
	public static class IsImage extends TypeSafeDiagnosingMatcher<BufferedImage> {

		public static IsImage image(BufferedImage image) {
			return new IsImage(image);
		}

		private final BufferedImage image;

		public IsImage(BufferedImage image) {
			this.image = image;
		}

		@Override
		protected boolean matchesSafely(BufferedImage image, Description description) {
			int width = this.image.getWidth();
			int height = this.image.getHeight();
			if (image.getWidth() != width) {
				description.appendText("image width mismatch.");
				return false;
			}
			if (image.getHeight() != height) {
				description.appendText("image height mismatch.");
				return false;
			}

			int[] expect = ImageUtils.getRGB(this.image, width, height);
			int[] actual = ImageUtils.getRGB(image, width, height);
			for (int i = 0; i < expect.length; i++) {
				if (expect[i] != actual[i]) {
					description.appendText("pixel mismatch.");
					return false;
				}
			}

			return true;
		}

		@Override
		public void describeTo(Description description) {
			description.appendText(String.format("image (%d x %d px)", image.getWidth(), image.getHeight()));
		}
	}

	/**
	 * グラデーションかどうかをチェックするMatcher。<br>
	 * 指定された画像が縦横それぞれ20pxのボックス上のグラデーションで塗りつぶされているか検証する。
	 */
	public static class IsGradation extends TypeSafeDiagnosingMatcher<BufferedImage> {

		private static Color expectedBorderColor;

		public static IsGradation gradation() {
			return new IsGradation();
		}

		/**
		 * グラデーションかどうかをチェックします。
		 *
		 * @param pixelRatio 論理ピクセル比率
		 */
		public static IsGradation gradation(double pixelRatio) {
			return new IsGradation(pixelRatio);
		}

		/**
		 * ボーダー付のグラデーションかどうかをチェックします。
		 *
		 * @param pixelRatio 論理ピクセル比率
		 */
		public static IsGradation gradationWithBorder(double pixelRatio) {
			return new IsGradation(pixelRatio, 20, Color.valueOf("#444"), 20);
		}

		/**
		 * ボーダー付のグラデーションかどうかをチェックします。
		 *
		 * @param pixelRatio 論理ピクセル比率
		 * @param borderStroke ボーダーの幅
		 */
		public static IsGradation gradationWithBorder(double pixelRatio, int borderStroke) {
			return new IsGradation(pixelRatio, 20, Color.valueOf("#444"), borderStroke);
		}

		/**
		 * ボーダー付のグラデーションかどうかをチェックします。
		 *
		 * @param pixelRatio 論理ピクセル比率
		 * @param color ボーダーの色
		 * @param borderStroke ボーダーの幅
		 */
		public static IsGradation gradationWithBorder(double pixelRatio, Color color, int borderStroke) {
			return new IsGradation(pixelRatio, 20, color, borderStroke);
		}

		private final double pixelRatio;
		private final int blockSize;
		private Color borderColor;
		private final int borderStroke;

		public IsGradation() {
			this(1.0);
		}

		public IsGradation(double pixelRatio) {
			this(pixelRatio, 20);
		}

		public IsGradation(double pixelRatio, int blockSize) {
			this(pixelRatio, blockSize, null, 0);
		}

		public IsGradation(double pixelRatio, int blockSize, Color borderColor, int borderStroke) {
			this.pixelRatio = pixelRatio;
			this.blockSize = blockSize;
			this.borderColor = borderColor;
			this.borderStroke = borderStroke;
		}

		@Override
		public boolean matchesSafely(BufferedImage image, Description mismatch) {

			// (0,0)の色をborderとみなす
			borderColor = Color.valueOf(image.getRGB(0, 0));

			// Border
			int width = image.getWidth();
			int height = image.getHeight();
			int border = (int) Math.round(borderStroke * pixelRatio);
			if (border > 0) {
				int centerX = width / 2;
				int centerY = height / 2;
				int start = Math.min(5, border);
				// 端っこがダメなブラウザが多いので、フレームの内側5px相当のみバリデーション
				for (int i = start; i < border; i++) {
					// top
					if (!borderMatch(image, mismatch, i, centerY)) {
						return false;
					}
					// bottom
					if (!borderMatch(image, mismatch, width - i - 1, centerY)) {
						return false;
					}
					// left
					if (!borderMatch(image, mismatch, centerX, i)) {
						return false;
					}
					// right
					if (!borderMatch(image, mismatch, centerX, height - i - 1)) {
						return false;
					}
				}
			}

			// IEで指定した色がブラウザ上では若干異なる色になってしまうため、
			// 端の値をベースにグラデーションになっているかを確認する

			int blockPixelSize = (int) (pixelRatio * blockSize);

			// Horizontal
			int red = Color.valueOf(image.getRGB(border, border)).red;

			for (int i = 0; i < width - border * 2; i++) {
				int x = border + i;
				int y = border;
				Color actual = Color.valueOf(image.getRGB(x, y));
				Color expected = Color.rgb(red, 0, 0xff);
				if (!actual.equals(expected)) {
					// IEでは色の変化が一定でなくなるため、次の値も確認
					if (!actual.equals(Color.rgb(red + 1, 0, 0xff))) {
						mismatch.appendText(
								String.format("%s @horizontal (%d, %d), expect=%s\n", actual.red, x, y, red));
						return false;
					}
					red++;
				}
				if (i % blockPixelSize == blockPixelSize - 1) {
					red += 8;
					red = red <= 0xff ? red : 0;
				}
			}

			// Vertical
			int green = Color.valueOf(image.getRGB(border, border)).green;

			for (int i = 0; i < height - border * 2; i++) {
				int x = border;
				int y = border + i;
				Color actual = Color.valueOf(image.getRGB(x, y));
				Color expected = Color.rgb(0, green, 0xff);
				if (!actual.equals(expected)) {
					// IEでは色の変化が一定でなくなるため、次の値も確認
					if (!actual.equals(Color.rgb(0, green + 1, 0xff))) {
						mismatch.appendText(
								String.format("%s @vertical (%d, %d), expect=%s\n", actual, x, y, expected));
						return false;
					}
					green++;
				}
				if (i % blockPixelSize == blockPixelSize - 1) {
					green += 8;
					green = green <= 0xff ? green : 0;
				}
			}

			return true;
		}

		@Override
		public void describeTo(Description description) {
			description.appendText(
					String.format("gradation page (pixelRatio=%f, blockSize=%d, borderColor=%s, borderStroke=%d)",
							pixelRatio, blockSize, borderColor, borderStroke));
		}

		private boolean borderMatch(BufferedImage image, Description mismatch, int x, int y) {
			Color actual = Color.valueOf(image.getRGB(x, y));
			if (actual.equals(borderColor)) {
				return true;
			}

			mismatch.appendText(String.format("%s @border (%d, %d), expect=%s\n", actual, x, y, borderColor));
			return false;
		}

	}

	//</editor-fold>

}
