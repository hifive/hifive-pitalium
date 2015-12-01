package com.htmlhifive.pitalium.it.screenshot.partialapge;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.model.DomSelector;
import com.htmlhifive.pitalium.core.model.ScreenArea;
import com.htmlhifive.pitalium.core.model.SelectorType;
import com.htmlhifive.pitalium.core.selenium.PtlWebDriverWait;

/**
 * 部分スクロールの動作テスト用クラス
 *
 * @author msakai
 */
public class TakeScrollScreenshotTest extends PtlTestBase {
	private static final String BASE_URL = "http://localhost/pitalium-test/overflow.html";

	/**
	 * overflow:scroll/auto/hidden に指定されている要素のスクリーンショットを<br>
	 * moveTarget=true で撮影するテスト。
	 */
	@Test
	public void takeMoveOverflowScreenshotTest() {
		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		DomSelector divSelector = new DomSelector(SelectorType.ID, "about-scroll");
		ScreenArea divScreenArea = ScreenArea.of(divSelector.getType(), divSelector.getValue());

		DomSelector textareaSelector = new DomSelector(SelectorType.ID, "textarea-scroll");
		ScreenArea textScreenArea = ScreenArea.of(textareaSelector.getType(), textareaSelector.getValue());

		DomSelector tbodySelector = new DomSelector(SelectorType.CSS_SELECTOR, "#table-scroll tbody");
		ScreenArea tbodyScreenArea = ScreenArea.of(tbodySelector.getType(), tbodySelector.getValue());

		CompareTarget[] targets = { new CompareTarget(divScreenArea), new CompareTarget(textScreenArea),
				new CompareTarget(tbodyScreenArea) };
		assertionView.assertView("overflowScreenshot", targets);
	}

	/**
	 * スクロール有りのiframeのスクリーンショットをmoveTarget=true で撮影するテスト。
	 */
	@Test
	public void takeMoveIframeScreenshotTest() {
		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		DomSelector selector = new DomSelector(SelectorType.NAME, "fb-scroll");
		ScreenArea iframeScreenArea = ScreenArea.of(selector.getType(), selector.getValue());
		CompareTarget[] targets = { new CompareTarget(iframeScreenArea) };

		assertionView.assertView("iframeScreenshot", targets);
	}

	/**
	 * overflow:scroll/auto/hidden に指定されている要素のスクリーンショットを<br>
	 * moveTarget=false で撮影するテスト。
	 */
	@Test
	public void takeNonMoveOverflowScreenshotTest() {
		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		DomSelector divSelector = new DomSelector(SelectorType.ID, "about-scroll");
		ScreenArea divScreenArea = ScreenArea.of(divSelector.getType(), divSelector.getValue());

		DomSelector textareaSelector = new DomSelector(SelectorType.ID, "textarea-scroll");
		ScreenArea textScreenArea = ScreenArea.of(textareaSelector.getType(), textareaSelector.getValue());

		DomSelector tbodySelector = new DomSelector(SelectorType.CSS_SELECTOR, "#table-scroll tbody");
		ScreenArea tbodyScreenArea = ScreenArea.of(tbodySelector.getType(), tbodySelector.getValue());

		CompareTarget[] targets = { new CompareTarget(divScreenArea, null, false),
				new CompareTarget(textScreenArea, null, false), new CompareTarget(tbodyScreenArea, null, false) };
		assertionView.assertView("overflowScreenshot", targets);
	}

	/**
	 * スクロール有りのiframeのスクリーンショットをmoveTarget=false で撮影するテスト。
	 */
	@Test
	public void takeNonMoveIframeScreenshotTest() {
		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		DomSelector selector = new DomSelector(SelectorType.NAME, "fb-scroll");
		ScreenArea iframeScreenArea = ScreenArea.of(selector.getType(), selector.getValue());
		CompareTarget[] targets = { new CompareTarget(iframeScreenArea, null, false) };

		assertionView.assertView("iframeScreenshot", targets);
	}

	/**
	 * スクロール有りの要素と無しの要素を同時にTargetに指定して撮影するテスト。
	 */
	@Test
	public void takeScrollAndNoScrollScreenshotTest() {
		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		DomSelector noScrollSelector = new DomSelector(SelectorType.CLASS_NAME, "fb-like-box");
		ScreenArea noScrollArea = ScreenArea.of(noScrollSelector.getType(), noScrollSelector.getValue());

		DomSelector scrollSelector = new DomSelector(SelectorType.ID, "about-scroll");
		ScreenArea scrollArea = ScreenArea.of(scrollSelector.getType(), scrollSelector.getValue());

		CompareTarget[] targets = { new CompareTarget(noScrollArea), new CompareTarget(scrollArea) };
		assertionView.assertView("scrollAndNoScrollScreenshot", targets);
	}

	/**
	 * ボーダーの有り/無し、太さを変えた場合に正しく撮影できるか確認するテスト。
	 */
	@Test
	public void takeBorderScreenshotTest() {

		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		// Targetの設定
		DomSelector divSelector = new DomSelector(SelectorType.ID, "about-scroll");
		ScreenArea divScreenArea = ScreenArea.of(divSelector.getType(), divSelector.getValue());

		DomSelector textareaSelector = new DomSelector(SelectorType.ID, "textarea-scroll");
		ScreenArea textScreenArea = ScreenArea.of(textareaSelector.getType(), textareaSelector.getValue());

		DomSelector tbodySelector = new DomSelector(SelectorType.CSS_SELECTOR, "#table-scroll tbody");
		ScreenArea tbodyScreenArea = ScreenArea.of(tbodySelector.getType(), tbodySelector.getValue());

		CompareTarget[] targets = { new CompareTarget(divScreenArea), new CompareTarget(textScreenArea),
				new CompareTarget(tbodyScreenArea) };

		// border: 1px
		driver.executeScript("document.getElementById('about-scroll').style.border = 'solid 1px black';");
		driver.executeScript("document.getElementById('textarea-scroll').style.border = 'solid 1px black';");
		WebElement tbodyElement = driver.findElement(By.cssSelector("#table-scroll tbody"));
		driver.executeScript("arguments[0].style.border = 'solid 1px black';", tbodyElement);

		assertionView.assertView("normalBorderScreenshot", targets);

		// border: 0px
		driver.executeScript("document.getElementById('about-scroll').style.border = 'solid 0px black';");
		driver.executeScript("document.getElementById('textarea-scroll').style.border = 'solid 0px black';");
		driver.executeScript("arguments[0].style.border = 'solid 0px black';", tbodyElement);

		assertionView.assertView("noBorderScreenshot", targets);

		// border: 10px
		driver.executeScript("document.getElementById('about-scroll').style.border = 'solid 10px black';");
		driver.executeScript("document.getElementById('textarea-scroll').style.border = 'solid 10px black';");
		driver.executeScript("arguments[0].style.border = 'solid 10px black';", tbodyElement);

		assertionView.assertView("wideBorderScreenshot", targets);
	}

	/**
	 * マージンの有り/無し、幅を変えた場合に正しく撮影できるか確認するテスト。
	 */
	@Test
	public void takeMarginScreenshotTest() {

		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		// Targetの設定
		DomSelector divSelector = new DomSelector(SelectorType.ID, "about-scroll");
		ScreenArea divScreenArea = ScreenArea.of(divSelector.getType(), divSelector.getValue());

		DomSelector textareaSelector = new DomSelector(SelectorType.ID, "textarea-scroll");
		ScreenArea textScreenArea = ScreenArea.of(textareaSelector.getType(), textareaSelector.getValue());

		DomSelector tbodySelector = new DomSelector(SelectorType.CSS_SELECTOR, "#table-scroll tbody");
		ScreenArea tbodyScreenArea = ScreenArea.of(tbodySelector.getType(), tbodySelector.getValue());

		CompareTarget[] targets = { new CompareTarget(divScreenArea), new CompareTarget(textScreenArea),
				new CompareTarget(tbodyScreenArea) };

		// margin: 100px
		driver.executeScript("document.getElementById('about-scroll').style.margin = '100px';");
		driver.executeScript("document.getElementById('textarea-scroll').style.margin = '100px';");
		WebElement tbodyElement = driver.findElement(By.cssSelector("#table-scroll tbody"));
		driver.executeScript("arguments[0].style.margin = '100px';", tbodyElement);

		assertionView.assertView("normalMarginScreenshot", targets);

		// margin: 0px
		driver.executeScript("document.getElementById('about-scroll').style.margin = '0px';");
		driver.executeScript("document.getElementById('textarea-scroll').style.margin = '0px';");
		driver.executeScript("arguments[0].style.margin = '0px';", tbodyElement);
		assertionView.assertView("noMarginScreenshot", targets);

	}

	/**
	 * paddingの有り/無し、幅を変えた場合に正しく撮影できるか確認するテスト。
	 */
	@Test
	public void takePaddingScreenshotTest() {

		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		// Targetの設定
		DomSelector divSelector = new DomSelector(SelectorType.ID, "about-scroll");
		ScreenArea divScreenArea = ScreenArea.of(divSelector.getType(), divSelector.getValue());

		DomSelector textareaSelector = new DomSelector(SelectorType.ID, "textarea-scroll");
		ScreenArea textScreenArea = ScreenArea.of(textareaSelector.getType(), textareaSelector.getValue());

		DomSelector tbodySelector = new DomSelector(SelectorType.CSS_SELECTOR, "#table-scroll tbody");
		ScreenArea tbodyScreenArea = ScreenArea.of(tbodySelector.getType(), tbodySelector.getValue());

		CompareTarget[] targets = { new CompareTarget(divScreenArea), new CompareTarget(textScreenArea),
				new CompareTarget(tbodyScreenArea) };

		// padding: 8px
		driver.executeScript("document.getElementById('about-scroll').style.padding = '8px';");
		driver.executeScript("document.getElementById('textarea-scroll').style.padding = '8px';");
		WebElement tbodyElement = driver.findElement(By.cssSelector("#table-scroll tbody"));
		driver.executeScript("arguments[0].style.padding = '8px';", tbodyElement);

		assertionView.assertView("normalPaddingScreenshot", targets);

		// padding: 0px
		driver.executeScript("document.getElementById('about-scroll').style.padding = '0px';");
		driver.executeScript("document.getElementById('textarea-scroll').style.padding = '0px';");
		driver.executeScript("arguments[0].style.padding = '0px';", tbodyElement);
		assertionView.assertView("noPaddingScreenshot", targets);

	}

	/**
	 * スクロールなし（overflow: visible）で、子要素のサイズ＞親要素のサイズに<br>
	 * なっているとき、正しく撮影できるか確認するテスト。
	 */
	@Test
	public void takeMoveVisibleOverflowScreenshotTest() {
		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		// Targetの設定
		DomSelector divSelector = new DomSelector(SelectorType.ID, "about-noscroll");
		ScreenArea divScreenArea = ScreenArea.of(divSelector.getType(), divSelector.getValue());
		CompareTarget[] targets = { new CompareTarget(divScreenArea) };
		assertionView.assertView("moveVisibleOverflowScreenshot", targets);

	}

	/**
	 * スクロールなし（overflow: visible）で、子要素のサイズ＞親要素のサイズに<br>
	 * なっているとき、正しく撮影できるか確認するテスト。
	 */
	@Test
	public void takeNonMoveVisibleOverflowScreenshotTest() {
		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		// Targetの設定
		DomSelector divSelector = new DomSelector(SelectorType.ID, "about-noscroll");
		ScreenArea divScreenArea = ScreenArea.of(divSelector.getType(), divSelector.getValue());
		CompareTarget[] targets = { new CompareTarget(divScreenArea, null, false) };
		assertionView.assertView("nonMoveVisibleOverflowScreenshot", targets);

	}

}
