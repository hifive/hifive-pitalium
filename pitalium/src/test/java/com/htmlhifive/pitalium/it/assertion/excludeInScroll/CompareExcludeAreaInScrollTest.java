package com.htmlhifive.pitalium.it.assertion.excludeInScroll;

import static org.junit.Assume.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.assertion.PtlItAssertionTestBase;

public class CompareExcludeAreaInScrollTest extends PtlItAssertionTestBase {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * 要素内スクロールの撮影において、最初から見えている内容が変更された要素を除外して比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void excludeVisibleArea() throws Exception {
		assumeFalse("Skip IE8 test (CSS Selector).", isInternetExplorer8());

		openScrollPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var table = document.getElementById('table-scroll');"
					+ "var tr = table.getElementsByTagName('tr')[0];" + "var td = tr.childNodes[0];"
					+ "td.style.background = 'rgb(12, 34, 56)';");
		}

		Rect targetRect = getPixelRectBySelector("#table-scroll > tbody");
		Rect excludeRect = getPixelRectBySelector("tr:nth-of-type(1)");
		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#table-scroll > tbody")
				.addExclude(excludeRect.x - targetRect.x, excludeRect.y - targetRect.y, excludeRect.width,
						excludeRect.height)
				.build();
		assertionView.assertView(arg);
	}

	/**
	 * 要素内スクロールの撮影において、最初は見えていない内容が変更された要素を除外して比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void excludeNotVisibleArea() throws Exception {
		assumeFalse("Skip IE8 test (CSS Selector).", isInternetExplorer8());

		openScrollPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var table = document.getElementById('table-scroll');"
					+ "var tr = table.getElementsByTagName('tr');" + "var td = tr[tr.length - 1].childNodes[0];"
					+ "td.style.background = 'rgb(12, 34, 56)';");
		}

		Rect targetRect = getPixelRectBySelector("#table-scroll > tbody");
		Rect excludeRect = getPixelRectBySelector("tr:nth-last-of-type(1)");
		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#table-scroll > tbody")
				.addExclude(excludeRect.x - targetRect.x, excludeRect.y - targetRect.y, excludeRect.width,
						excludeRect.height)
				.build();
		assertionView.assertView(arg);
	}
}
