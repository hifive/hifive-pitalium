package com.htmlhifive.pitalium.it.exec.frame;

import com.google.common.base.Supplier;
import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
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

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * iframe操作時のフレームスイッチテスト
 */
public class FrameWebElementTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private PtlWebDriver driver;

	@Before
	public void initializeWebDriver() throws Exception {
		// 全ブラウザでチェックする必要は無いため、capabilities.jsonに登録されている最初のブラウザでテストを実行する。
		PtlCapabilities capabilities = PtlCapabilities.readCapabilities().get(0)[0];
		driver = PtlWebDriverFactory.getInstance(capabilities).getDriver();
		driver.manage().timeouts().implicitlyWait(3L, TimeUnit.SECONDS);
	}

	@After
	public void closeWebDriver() throws Exception {
		if (driver != null) {
			driver.quit();
		}
	}

	@Test
	public void executeInFrame_runnable_notFrame() throws Exception {
		driver.get("iframe/iframe-test.html");
		PtlWebElement body = (PtlWebElement) driver.findElementByTagName("body");

		expectedException.expect(TestRuntimeException.class);
		expectedException.expectMessage(containsString("not frame or iframe element"));

		body.executeInFrame(new Runnable() {
			@Override
			public void run() {
				fail();
			}
		});

		fail();
	}

	@Test
	public void executeInFrame_supplier_notFrame() throws Exception {
		driver.get("iframe/iframe-test.html");
		PtlWebElement body = (PtlWebElement) driver.findElementByTagName("body");

		expectedException.expect(TestRuntimeException.class);
		expectedException.expectMessage(containsString("not frame or iframe element"));

		body.executeInFrame(new Supplier<String>() {
			@Override
			public String get() {
				fail();
				return "";
			}
		});

		fail();
	}

	@Test
	public void executeInFrame() throws Exception {
		driver.get("iframe/iframe-test.html");
		// #container is not in default content
		try {
			driver.findElementById("container");
			fail();
		} catch (NoSuchElementException e) {
			//
		}

		// #container is in .content iframe
		PtlWebElement content = (PtlWebElement) driver.findElementByClassName("content");
		PtlWebElement container = content.executeInFrame(new Supplier<PtlWebElement>() {
			@Override
			public PtlWebElement get() {
				return (PtlWebElement) driver.findElementById("container");
			}
		});
		assertThat(container, is(notNullValue()));

		// driver watches default content
		try {
			driver.findElementById("container");
			fail("driver watches frame content");
		} catch (NoSuchElementException e) {
			//
		}
	}

	@Test
	public void findElement() throws Exception {
		driver.get("iframe/iframe-test.html");

		PtlWebElement body = (PtlWebElement) driver.findElementByTagName("body");
		try {
			body.findElementById("container");
			fail();
		} catch (NoSuchElementException e) {
			//
		}

		PtlWebElement content = (PtlWebElement) driver.findElementByClassName("content");
		PtlWebElement container = (PtlWebElement) content.findElementById("container");

		// driver watches default content
		try {
			content.getLocation();
		} catch (Exception e) {
			fail("driver watches frame content");
		}

		try {
			container.getLocation();
		} catch (Exception e) {
			fail("Auto content switcher is not work");
		}
	}

}
