package com.htmlhifive.pitalium.it.assertion.exclude;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.assertion.PtlItAssertionTestBase;

public class CompareExcludeAreaTest extends PtlItAssertionTestBase {
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * 変更がない部位を除外して比較を行う。<br>
	 * targetをDOMで指定し、除外範囲を座標で指定する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareNotChangedElementWithExcludeByCoordinateOption() throws Exception {
		openBasicTextPage(false);

		Rect targetRect = getPixelRectById("textColumn0");
		Rect excludeRect = getPixelRectBySelector("#textColumn0 > h2");
		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("textColumn0")
				.addExclude(excludeRect.x - targetRect.x, excludeRect.y - targetRect.y, excludeRect.width,
						excludeRect.height)
				.build();

		assertionView.assertView(arg);
	}

	/**
	 * 変更がない部位を除外して比較を行う。<br>
	 * targetを座標で指定し、除外範囲を座標で指定する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareNotChangedAreaWithExcludeByCoordinateOption() throws Exception {
		openBasicTextPage(false);

		Rect targetRect = getPixelRectById("textColumn0");
		Rect excludeRect = getPixelRectBySelector("#textColumn0 > h2");
		ScreenshotArgument arg = ScreenshotArgument.builder("s")
				.addNewTarget(targetRect.x, targetRect.y, targetRect.width, targetRect.height)
				.addExclude(excludeRect.x - targetRect.x, excludeRect.y - targetRect.y, excludeRect.width,
						excludeRect.height)
				.build();

		assertionView.assertView(arg);
	}

	/**
	 * 変更された部位を除外して比較を行う。<br>
	 * targetをDOMで指定し、除外範囲を座標で指定する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareChangedElementWithExcludeByCoordinateOption() throws Exception {
		openBasicTextPage(true);

		Rect targetRect = getPixelRectById("textColumn0");
		Rect excludeRect = getPixelRectBySelector("#textColumn0 > h2");
		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("textColumn0")
				.addExclude(excludeRect.x - targetRect.x, excludeRect.y - targetRect.y, excludeRect.width,
						excludeRect.height)
				.build();

		assertionView.assertView(arg);
	}

	/**
	 * 変更された部位を除外して比較を行う。<br>
	 * targetを座標で指定し、除外範囲を座標で指定する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareChangeAreaWithExcludeByCoordinateOption() throws Exception {
		openBasicTextPage(true);

		Rect targetRect = getPixelRectById("textColumn0");
		Rect excludeRect = getPixelRectBySelector("#textColumn0 > h2");
		ScreenshotArgument arg = ScreenshotArgument.builder("s")
				.addNewTarget(targetRect.x, targetRect.y, targetRect.width, targetRect.height)
				.addExclude(excludeRect.x - targetRect.x, excludeRect.y - targetRect.y, excludeRect.width,
						excludeRect.height)
				.build();

		assertionView.assertView(arg);
	}

}
