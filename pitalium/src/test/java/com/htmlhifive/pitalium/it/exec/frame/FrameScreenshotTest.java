package com.htmlhifive.pitalium.it.exec.frame;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import com.htmlhifive.pitalium.image.util.ImageUtils;
import org.junit.Before;
import org.junit.Test;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.io.PersistMetadata;
import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.core.model.SelectorType;
import com.htmlhifive.pitalium.core.model.TargetResult;
import com.htmlhifive.pitalium.core.result.TestResultManager;
import com.htmlhifive.pitalium.image.model.RectangleArea;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class FrameScreenshotTest extends PtlTestBase {

	// TODO appiumで画像サイズどうなるのか。同じだったらいいなぁ、、

	private TargetResult loadResult(String methodName, String screenshotId) {
		try {
			TestResultManager resultManager = TestResultManager.getInstance();
			String expectedId = resultManager.getCurrentId();
			PersistMetadata metadata = new PersistMetadata(expectedId, getClass().getSimpleName(), methodName,
					screenshotId, capabilities);
			return resultManager.getPersister().loadTargetResults(metadata).get(0);
		} catch (Exception e) {
			fail("TestResult JSON load error: " + e.getMessage());

			// never
			throw new RuntimeException();
		}
	}

	@Before
	@Override
	public void setUp() {
		super.setUp();

		driver.get("iframe/iframe-test.html");
	}

	/**
	 * 通常のキャプチャ
	 */
	@Test
	public void captureDefaultContentBody() throws Exception {
// @formatter:off
		ScreenshotArgument args = ScreenshotArgument.builder("screenshot")
				.addNewTarget()
				.moveTarget(false)
				.build();
// @formatter:on
		assertionView.assertView(args);

		// JSONチェック
		TargetResult result = loadResult("captureDefaultContentBody", "screenshot");
		RectangleArea targetRect = result.getTarget().getRectangle();
		assertThat(targetRect.getX(), is(0.0));
		assertThat(targetRect.getY(), is(0.0));
		assertThat(targetRect.getHeight(), is(1000.0));
		assertThat(result.getExcludes().size(), is(0));

		// widthの大きさは不定
	}

	/**
	 * 通常のキャプチャ
	 */
	@Test
	public void captureDefaultContentBody_move() throws Exception {
// @formatter:off
		ScreenshotArgument args = ScreenshotArgument.builder("screenshot")
				.addNewTarget()
				.moveTarget(true)
				.build();
// @formatter:on
		assertionView.assertView(args);

		// JSONチェック
		TargetResult result = loadResult("captureDefaultContentBody_move", "screenshot");
		RectangleArea targetRect = result.getTarget().getRectangle();
		assertThat(targetRect.getX(), is(0.0));
		assertThat(targetRect.getY(), is(0.0));
		assertThat(targetRect.getHeight(), is(1000.0));
		assertThat(result.getExcludes().size(), is(0));

		// widthの大きさは不定
	}

	/**
	 * 通常のキャプチャ＋iframe内の要素を除外指定
	 */
	@Test
	public void captureDefaultContentBody_withExclude() throws Exception {
// @formatter:off
		ScreenshotArgument args = ScreenshotArgument.builder("screenshot")
				.addNewTarget()
				.moveTarget(false)
				.addExclude(SelectorType.CLASS_NAME, "content-left", SelectorType.CLASS_NAME, "content")
				.build();
// @formatter:on
		assertionView.assertView(args);

		// JSONチェック
		TargetResult result = loadResult("captureDefaultContentBody_withExclude", "screenshot");
		RectangleArea targetRect = result.getTarget().getRectangle();
		assertThat(targetRect.getX(), is(0.0));
		assertThat(targetRect.getY(), is(0.0));
		assertThat(targetRect.getHeight(), is(1000.0));
		assertThat(result.getExcludes().size(), is(1));

		// iframeの大きさで切り取られているべき
		RectangleArea excludeRect = result.getExcludes().get(0).getRectangle();
		assertThat(excludeRect, is(new RectangleArea(400.0, 300.0, 200.0, 200.0)));
	}

	/**
	 * 通常のキャプチャ＋iframe内の要素を除外指定
	 */
	@Test
	public void captureDefaultContentBody_move_withExclude() throws Exception {
// @formatter:off
		ScreenshotArgument args = ScreenshotArgument.builder("screenshot")
				.addNewTarget()
				.moveTarget(true)
				.addExclude(SelectorType.CLASS_NAME, "content-left", SelectorType.CLASS_NAME, "content")
				.build();
// @formatter:on
		assertionView.assertView(args);

		// JSONチェック
		TargetResult result = loadResult("captureDefaultContentBody_move_withExclude", "screenshot");
		RectangleArea targetRect = result.getTarget().getRectangle();
		assertThat(targetRect.getX(), is(0.0));
		assertThat(targetRect.getY(), is(0.0));
		assertThat(targetRect.getHeight(), is(1000.0));
		assertThat(result.getExcludes().size(), is(1));

		// iframeの大きさで切り取られているべき
		RectangleArea excludeRect = result.getExcludes().get(0).getRectangle();
		assertThat(excludeRect, is(new RectangleArea(400.0, 300.0, 200.0, 200.0)));
	}

	/**
	 * iframe全体をキャプチャ
	 */
	@Test
	public void captureFrameBody() throws Exception {
// @formatter:off
		ScreenshotArgument args = ScreenshotArgument.builder("screenshot")
				.addNewTargetByClassName("content")
				.moveTarget(false)
				.scrollTarget(true)
				.build();
// @formatter:on
		assertionView.assertView(args);

		// JSONチェック
		TargetResult result = loadResult("captureFrameBody", "screenshot");
		RectangleArea targetRect = result.getTarget().getRectangle();
		assertThat(targetRect.getX(), is(0.0));
		assertThat(targetRect.getY(), is(0.0));
		assertThat(targetRect.getWidth(), is(300.0));
		assertThat(targetRect.getHeight(), is(1004.0));
		assertThat(result.getExcludes().size(), is(0));
	}

	/**
	 * iframe全体をキャプチャ＋iframe内の要素を除外指定
	 */
	@Test
	public void captureFrameBody_withExclude() throws Exception {
// @formatter:off
		ScreenshotArgument args = ScreenshotArgument.builder("screenshot")
				.addNewTargetByClassName("content")
				.moveTarget(false)
				.scrollTarget(true)
				.addExclude(SelectorType.CLASS_NAME, "content-left")
				.build();
// @formatter:on
		assertionView.assertView(args);

		// JSONチェック
		TargetResult result = loadResult("captureFrameBody_withExclude", "screenshot");
		RectangleArea targetRect = result.getTarget().getRectangle();
		assertThat(targetRect.getX(), is(0.0));
		assertThat(targetRect.getY(), is(0.0));
		assertThat(targetRect.getWidth(), is(300.0));
		assertThat(targetRect.getHeight(), is(1004.0));
		assertThat(result.getExcludes().size(), is(1));

		RectangleArea excludeRect = result.getExcludes().get(0).getRectangle();
		assertThat(excludeRect, is(new RectangleArea(100.0, 100.0, 200.0, 300.0)));
	}

	/**
	 * iframe全体をキャプチャ
	 */
	@Test
	public void captureFrameBody_move() throws Exception {
// @formatter:off
		ScreenshotArgument args = ScreenshotArgument.builder("screenshot")
				.addNewTargetByClassName("content")
				.moveTarget(true)
				.scrollTarget(true)
				.build();
// @formatter:on
		assertionView.assertView(args);

		// JSONチェック
		TargetResult result = loadResult("captureFrameBody_move", "screenshot");
		RectangleArea targetRect = result.getTarget().getRectangle();
		assertThat(targetRect.getX(), is(0.0));
		assertThat(targetRect.getY(), is(0.0));
		assertThat(targetRect.getWidth(), is(300.0));
		assertThat(targetRect.getHeight(), is(1004.0));
		assertThat(result.getExcludes().size(), is(0));
	}

	/**
	 * iframe全体をキャプチャ＋iframe内の要素を除外指定
	 */
	@Test
	public void captureFrameBody_move_withExclude() throws Exception {
// @formatter:off
		ScreenshotArgument args = ScreenshotArgument.builder("screenshot")
				.addNewTargetByClassName("content")
				.moveTarget(true)
				.scrollTarget(true)
				.addExclude(SelectorType.CLASS_NAME, "content-left")
				.build();
// @formatter:on
		assertionView.assertView(args);

		// JSONチェック
		TargetResult result = loadResult("captureFrameBody_move_withExclude", "screenshot");
		RectangleArea targetRect = result.getTarget().getRectangle();
		assertThat(targetRect.getX(), is(0.0));
		assertThat(targetRect.getY(), is(0.0));
		assertThat(targetRect.getWidth(), is(300.0));
		assertThat(targetRect.getHeight(), is(1004.0));
		assertThat(result.getExcludes().size(), is(1));

		RectangleArea excludeRect = result.getExcludes().get(0).getRectangle();
		assertThat(excludeRect, is(new RectangleArea(100.0, 100.0, 200.0, 300.0)));
	}

	/**
	 * 通常のキャプチャ + iframe内hidden
	 */
	@Test
	public void captureDefaultContentBody_hidden() throws Exception {
// @formatter:off
		ScreenshotArgument args = ScreenshotArgument.builder("screenshot")
				.addNewTarget()
				.moveTarget(false)
				.addHiddenElementsByClassName("content-left").inFrameByClassName("content")
				.build();
// @formatter:on
		assertionView.assertView(args);

		// 画像チェック（#795548、#455A64が無いことのチェック）
		TargetResult result = loadResult("captureDefaultContentBody_hidden", "screenshot");
		BufferedImage image = result.getImage().get();
		int width = image.getWidth();
		int height = image.getHeight();
		int[] colors = ImageUtils.getRGB(image, width, height);
		int color1 = Color.decode("0x795548").getRGB();
		int color2 = Color.decode("0x455A64").getRGB();
		for (int color : colors) {
			if (color == color1) {
				fail("background-color found");
			}
			if (color == color2) {
				fail("border-color found");
			}
		}
	}

	/**
	 * 通常のキャプチャ + iframe内hidden
	 */
	@Test
	public void captureDefaultContentBody_move_hidden() throws Exception {
// @formatter:off
		ScreenshotArgument args = ScreenshotArgument.builder("screenshot")
				.addNewTarget()
				.moveTarget(true)
				.addHiddenElementsByClassName("content-left").inFrameByClassName("content")
				.build();
// @formatter:on
		assertionView.assertView(args);

		// 画像チェック（#795548、#455A64が無いことのチェック）
		TargetResult result = loadResult("captureDefaultContentBody_move_hidden", "screenshot");
		BufferedImage image = result.getImage().get();
		int width = image.getWidth();
		int height = image.getHeight();
		int[] colors = ImageUtils.getRGB(image, width, height);
		int color1 = Color.decode("0x795548").getRGB();
		int color2 = Color.decode("0x455A64").getRGB();
		for (int color : colors) {
			if (color == color1) {
				fail("background-color found");
			}
			if (color == color2) {
				fail("border-color found");
			}
		}
	}

	/**
	 * iframe全体をキャプチャ + iframe内hidden
	 */
	@Test
	public void captureFrameBody_hidden() throws Exception {
// @formatter:off
		ScreenshotArgument args = ScreenshotArgument.builder("screenshot")
				.addNewTargetByClassName("content")
				.moveTarget(false)
				.scrollTarget(true)
				.addHiddenElementsByClassName("content-left").inFrameByClassName("content")
				.build();
// @formatter:on
		assertionView.assertView(args);

		// 画像チェック（#795548、#455A64が無いことのチェック）
		TargetResult result = loadResult("captureFrameBody_hidden", "screenshot");
		BufferedImage image = result.getImage().get();
		int width = image.getWidth();
		int height = image.getHeight();
		int[] colors = ImageUtils.getRGB(image, width, height);
		int color1 = Color.decode("0x795548").getRGB();
		int color2 = Color.decode("0x455A64").getRGB();
		for (int color : colors) {
			if (color == color1) {
				fail("background-color found");
			}
			if (color == color2) {
				fail("border-color found");
			}
		}
	}

	/**
	 * iframe全体をキャプチャ + iframe内hidden
	 */
	@Test
	public void captureFrameBody_move_hidden() throws Exception {
// @formatter:off
		ScreenshotArgument args = ScreenshotArgument.builder("screenshot")
				.addNewTargetByClassName("content")
				.moveTarget(true)
				.scrollTarget(true)
				.addHiddenElementsByClassName("content-left").inFrameByClassName("content")
				.build();
// @formatter:on
		assertionView.assertView(args);

		// 画像チェック（#795548、#455A64が無いことのチェック）
		TargetResult result = loadResult("captureFrameBody_move_hidden", "screenshot");
		BufferedImage image = result.getImage().get();
		int width = image.getWidth();
		int height = image.getHeight();
		int[] colors = ImageUtils.getRGB(image, width, height);
		int color1 = Color.decode("0x795548").getRGB();
		int color2 = Color.decode("0x455A64").getRGB();
		for (int color : colors) {
			if (color == color1) {
				fail("background-color found");
			}
			if (color == color2) {
				fail("border-color found");
			}
		}
	}

}
