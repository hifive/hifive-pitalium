package com.htmlhifive.pitalium.it.exec.frame;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.io.PersistMetadata;
import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.core.model.SelectorType;
import com.htmlhifive.pitalium.core.model.TargetResult;
import com.htmlhifive.pitalium.core.result.TestResultManager;
import com.htmlhifive.pitalium.image.model.RectangleArea;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class FrameScreenshotTest extends PtlTestBase {

	// TODO appiumで画像サイズどうなるのか。同じだったらいいなぁ、、

	private TargetResult loadResult(String methodName, String screenshotId) {
		try {
			TestResultManager resultManager = TestResultManager.getInstance();
			String expectedId = resultManager.getCurrentId();
			PersistMetadata metadata = new PersistMetadata(expectedId, getClass().getSimpleName(), methodName, screenshotId, capabilities);
			return resultManager.getPersister().loadTargetResults(metadata).get(0);
		} catch (Exception e) {
			fail("TestResult JSON load error: " + e.getMessage());

			// never
			throw new RuntimeException();
		}
	}

	/**
	 * 通常のキャプチャ
	 */
	@Test
	public void captureDefaultContentBody() throws Exception {
		driver.get("iframe/iframe-test.html");

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
		driver.get("iframe/iframe-test.html");

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
		driver.get("iframe/iframe-test.html");

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
		driver.get("iframe/iframe-test.html");

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
		driver.get("iframe/iframe-test.html");

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
		driver.get("iframe/iframe-test.html");

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
		driver.get("iframe/iframe-test.html");

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
		driver.get("iframe/iframe-test.html");

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

}
