package com.htmlhifive.pitalium.it.exec.frame;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.htmlhifive.pitalium.core.model.DomSelector;
import com.htmlhifive.pitalium.core.model.SelectorType;
import com.htmlhifive.pitalium.core.selenium.PtlCapabilities;
import com.htmlhifive.pitalium.core.selenium.PtlWebDriver;
import com.htmlhifive.pitalium.core.selenium.PtlWebDriverFactory;

public class FrameDomSelectorTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private PtlWebDriver driver;

	@Before
	public void initializeWebDriver() throws Exception {
		// 全ブラウザでチェックする必要は無いため、capabilities.jsonに登録されている最初のブラウザでテストを実行する。
		PtlCapabilities capabilities = PtlCapabilities.readCapabilities().get(0)[0];
		driver = PtlWebDriverFactory.getInstance(capabilities).getDriver();
		driver.manage().timeouts().implicitlyWait(1L, TimeUnit.SECONDS);

		driver.get("iframe/iframe-test.html");
	}

	@After
	public void closeWebDriver() throws Exception {
		if (driver != null) {
			driver.quit();
		}
	}

	/**
	 * Driver -> body
	 */
	@Test
	public void findDefaultContentBody() throws Exception {
		DomSelector selector = new DomSelector(SelectorType.TAG_NAME, "body");
		assertThat(selector.findElement(driver).getTagName(), is("body"));
	}

	/**
	 * Driver - (iframe) -> #container
	 */
	@Test
	public void findInFrame_noParent() throws Exception {
		expectedException.expect(NoSuchElementException.class);

		DomSelector selector = new DomSelector(SelectorType.ID, "container");
		selector.findElement(driver);
	}

	/**
	 * Driver -> iframe.content -> #container
	 */
	@Test
	public void findInFrame() throws Exception {
		DomSelector selector = new DomSelector(SelectorType.ID, "container", new DomSelector(SelectorType.CLASS_NAME,
				"content"));
		assertThat(selector.findElement(driver).getTagName(), is("div"));
	}

	/**
	 * Driver -> body - (iframe) -> #container
	 */
	@Test
	public void findFromDefaultContentBody() throws Exception {
		expectedException.expect(NoSuchElementException.class);

		DomSelector selector = new DomSelector(SelectorType.ID, "container", new DomSelector(SelectorType.TAG_NAME,
				"body"));
		selector.findElement(driver);
	}

	/**
	 * Driver -> body -> iframe.content -> #container
	 */
	@Test
	public void findFromIFrame() throws Exception {
		DomSelector selector = new DomSelector(SelectorType.ID, "container", new DomSelector(SelectorType.CLASS_NAME,
				"content"));
		WebElement defaultContentBody = driver.findElementByTagName("body");
		assertThat(selector.findElement(defaultContentBody).getTagName(), is("div"));
	}

}
