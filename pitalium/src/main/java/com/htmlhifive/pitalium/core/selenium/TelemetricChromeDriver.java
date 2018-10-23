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
package com.htmlhifive.pitalium.core.selenium;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.htmlhifive.pitalium.core.model.Performance;
import com.htmlhifive.pitalium.core.rules.PerformanceTelemetry;

/**
 * Google Chromeで利用する{@link org.openqa.selenium.WebDriver}
 */
class TelemetricChromeDriver extends PtlChromeDriver implements TelemetricWebDriver {
	private PerformanceTelemetry measure;

	/**
	 * コンストラクタ
	 * 
	 * @param remoteAddress RemoteWebDriverServerのアドレス
	 * @param capabilities Capability
	 */
	TelemetricChromeDriver(URL remoteAddress, PtlCapabilities capabilities) {
		super(remoteAddress, capabilities);
	}

	@Override
	public void get(String url) {
		measurePerformance(null);
		super.get(url);
	}

	@Override
	protected PtlWebElement newPtlWebElement() {
		return new PtlChromeWebElement();
	}

	@Override
	public WebElement findElement(By by) {
		return new TelemetricWebElement(super.findElement(by), this);
	}

	@Override
	public WebElement findElementByClassName(String using) {
		return new TelemetricWebElement(super.findElementByClassName(using), this);
	}

	@Override
	public WebElement findElementByCssSelector(String using) {
		return new TelemetricWebElement(super.findElementByCssSelector(using), this);
	}

	@Override
	public WebElement findElementById(String using) {
		return new TelemetricWebElement(super.findElementById(using), this);
	}

	@Override
	public WebElement findElementByLinkText(String using) {
		return new TelemetricWebElement(super.findElementByLinkText(using), this);
	}

	@Override
	public WebElement findElementByName(String using) {
		return new TelemetricWebElement(super.findElementByName(using), this);
	}

	@Override
	public WebElement findElementByPartialLinkText(String using) {
		return new TelemetricWebElement(super.findElementByPartialLinkText(using), this);
	}

	@Override
	public WebElement findElementByTagName(String using) {
		return new TelemetricWebElement(super.findElementByTagName(using), this);
	}

	@Override
	public WebElement findElementByXPath(String using) {
		return new TelemetricWebElement(super.findElementByXPath(using), this);
	}

	@Override
	public List<WebElement> findElements(By by) {
		List<WebElement> elements = new ArrayList<WebElement>();
		for (WebElement element : super.findElements(by)) {
			elements.add(new TelemetricWebElement(element, this));
		}
		return elements;
	}

	@Override
	public List<WebElement> findElementsByClassName(String using) {
		List<WebElement> elements = new ArrayList<WebElement>();
		for (WebElement element : super.findElementsByClassName(using)) {
			elements.add(new TelemetricWebElement(element, this));
		}
		return elements;
	}

	@Override
	public List<WebElement> findElementsByCssSelector(String using) {
		List<WebElement> elements = new ArrayList<WebElement>();
		for (WebElement element : super.findElementsByCssSelector(using)) {
			elements.add(new TelemetricWebElement(element, this));
		}
		return elements;
	}

	@Override
	public List<WebElement> findElementsById(String using) {
		List<WebElement> elements = new ArrayList<WebElement>();
		for (WebElement element : super.findElementsById(using)) {
			elements.add(new TelemetricWebElement(element, this));
		}
		return elements;
	}

	@Override
	public List<WebElement> findElementsByLinkText(String using) {
		List<WebElement> elements = new ArrayList<WebElement>();
		for (WebElement element : super.findElementsByLinkText(using)) {
			elements.add(new TelemetricWebElement(element, this));
		}
		return elements;
	}

	@Override
	public List<WebElement> findElementsByName(String using) {
		List<WebElement> elements = new ArrayList<WebElement>();
		for (WebElement element : super.findElementsByName(using)) {
			elements.add(new TelemetricWebElement(element, this));
		}
		return elements;
	}

	@Override
	public List<WebElement> findElementsByPartialLinkText(String using) {
		List<WebElement> elements = new ArrayList<WebElement>();
		for (WebElement element : super.findElementsByPartialLinkText(using)) {
			elements.add(new TelemetricWebElement(element, this));
		}
		return elements;
	}

	@Override
	public List<WebElement> findElementsByTagName(String using) {
		List<WebElement> elements = new ArrayList<WebElement>();
		for (WebElement element : super.findElementsByTagName(using)) {
			elements.add(new TelemetricWebElement(element, this));
		}
		return elements;
	}

	@Override
	public List<WebElement> findElementsByXPath(String using) {
		List<WebElement> elements = new ArrayList<WebElement>();
		for (WebElement element : super.findElementsByXPath(using)) {
			elements.add(new TelemetricWebElement(element, this));
		}
		return elements;
	}

	@Override
	public void measurePerformance(String label) {
		Performance performance = Performance.parseJson(executeJavaScript(SCRIPT));
		performance.setBrowser(getCapabilities().getBrowserName());
		performance.setLabel(label);
		measure.addPerformance(performance);
	}

	@Override
	public void setPerformanceMeasure(PerformanceTelemetry m) {
		this.measure = m;
	}
}
