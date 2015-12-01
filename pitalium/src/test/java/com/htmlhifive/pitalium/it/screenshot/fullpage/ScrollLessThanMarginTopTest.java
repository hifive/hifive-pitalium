package com.htmlhifive.pitalium.it.screenshot.fullpage;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.awt.image.BufferedImage;

import org.junit.Test;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.io.PersistMetadata;
import com.htmlhifive.pitalium.core.model.IndexDomSelector;
import com.htmlhifive.pitalium.core.model.SelectorType;
import com.htmlhifive.pitalium.core.result.TestResultManager;

/**
 * ページ全体のスクリーンショットを撮影する際に、スクロールの量がMarginTopよりも小さい場合のテスト
 */
public class ScrollLessThanMarginTopTest extends PtlTestBase {

	/**
	 * スクロール量に対してMarginTopの方が大きいページの全体スクリーンショットを撮影する。<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：スクリーンショットが正常に撮影されていること。 TODO モバイル対応
	 */
	@Test
	public void testScrollLessThanMarginTop() throws Exception {
		driver.get(getClass().getPackage().getName().replaceAll("\\.", "/") + "/testScrollLessThanMarginTop.html");
		assertionView.assertView("testScrollLessThanMarginTop");

		PersistMetadata metadata = new PersistMetadata(TestResultManager.getInstance().getCurrentId(), getClass()
				.getSimpleName(), "testScrollLessThanMarginTop", "testScrollLessThanMarginTop", new IndexDomSelector(
				SelectorType.TAG_NAME, "body", 0), null, capabilities);
		BufferedImage image = TestResultManager.getInstance().getPersister().loadScreenshot(metadata);

		// 0-300は赤、300-は色が正常に変わっている画像が撮れていることの確認
		// でも0から見るとIEがダメなので、3から。

		assertThat(image.getHeight(), is(1000));
		assertThat(1000L - driver.getWindowHeight() < 300L, is(true));

		final int x = 10;
		for (int y = 3; y < 300; y++) {
			int color = image.getRGB(x, y);
			assertThat(color, is(0xffff0000));
		}

		for (int y = 300; y < 1000; y++) {
			int color = image.getRGB(x, y);

			int count = (y - 300) / 20;
			int red;
			int green;
			int blue;

			red = green = blue = (count / 3) * 10;
			switch (count % 3) {
				case 2:
					blue += 10;

				case 1:
					green += 10;

				case 0:
					red += 10;
			}

			int expected = 0xff << 24 | red << 16 | green << 8 | blue;
			assertThat(color, is(expected));
		}
	}

}
