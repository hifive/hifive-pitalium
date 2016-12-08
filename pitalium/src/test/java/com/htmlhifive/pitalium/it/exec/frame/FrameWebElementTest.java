package com.htmlhifive.pitalium.it.exec.frame;

import com.google.common.base.Supplier;
import com.htmlhifive.pitalium.core.selenium.PtlCapabilities;
import com.htmlhifive.pitalium.core.selenium.PtlWebDriver;
import com.htmlhifive.pitalium.core.selenium.PtlWebDriverFactory;
import com.htmlhifive.pitalium.core.selenium.PtlWebElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * iframe操作時のフレームスイッチテスト
 */
public class FrameWebElementTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private PtlWebDriver driver;
	private final AtomicInteger count = new AtomicInteger(0);

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
	 * Frameにスイッチするテスト
	 */
	@Test
	public void executeInFrame_runnable() throws Exception {
		PtlWebElement iframe = (PtlWebElement) driver.findElementByClassName("content");
		iframe.executeInFrame(iframe, new Runnable() {
			@Override
			public void run() {
				// content-leftはiframe内要素
				driver.findElementByClassName("content-left");
				count.incrementAndGet();
			}
		});

		assertThat(count.get(), is(1));
	}

	/**
	 * Frameにスイッチするテスト
	 */
	@Test
	public void executeInFrame_supplier() throws Exception {
		PtlWebElement iframe = (PtlWebElement) driver.findElementByClassName("content");
		WebElement left = iframe.executeInFrame(iframe, new Supplier<WebElement>() {
			@Override
			public WebElement get() {
				return driver.findElementByClassName("content-left");
			}
		});

		expectedException.expect(StaleElementReferenceException.class);
		driver.executeJavaScript("return arguments[0].tagName", left);
	}

	/**
	 * Frameにスイッチできないテスト
	 */
	@Test
	public void executeInFrame_not_inFrameElement() throws Exception {
		PtlWebElement iframe = (PtlWebElement) driver.findElementByClassName("content");
		expectedException.expect(NoSuchElementException.class);
		iframe.executeInFrame(new Supplier<WebElement>() {
			@Override
			public WebElement get() {
				return driver.findElementByClassName("content-left");
			}
		});
	}

	/**
	 * Frameにスイッチするテスト
	 */
	@Test
	public void executeInFrame_inFrameElement() throws Exception {
		PtlWebElement iframe = (PtlWebElement) driver.findElementByClassName("content");
		driver.switchTo().frame(iframe);
		PtlWebElement left = (PtlWebElement) driver.findElementByClassName("content-left");
		driver.switchTo().defaultContent();

		try {
			left.getTagName();
			fail();
		} catch (StaleElementReferenceException e) {
			//
		}

		left.setFrameParent(iframe);
		assertThat(left.getTagName(), is("div"));
	}

}
