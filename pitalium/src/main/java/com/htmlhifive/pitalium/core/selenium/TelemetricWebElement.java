package com.htmlhifive.pitalium.core.selenium;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.remote.FileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.google.common.base.Supplier;

/**
 * 性能測定機能を持つ{@link PtlWebElement}の実装クラス。 click、submit時に性能測定を行う。
 */
public class TelemetricWebElement extends PtlWebElement implements WebElement {
	private TelemetricWebDriver driver;
	private PtlWebElement element;

	/**
	 * コンストラクタ
	 * 
	 * @param element 動作を委譲するPtlWebElement
	 * @param driver 性能測定を行うTelemetricWebDriver
	 */
	TelemetricWebElement(WebElement element, TelemetricWebDriver driver) {
		this.element = (PtlWebElement) element;
		this.driver = driver;
	}

	@Override
	public void clear() {
		element.clear();
	}

	@Override
	public void click() {
		driver.measurePerformance(null);
		element.click();
	}

	@Override
	public WebElement findElement(By arg0) {
		return element.findElement(arg0);
	}

	@Override
	public List<WebElement> findElements(By arg0) {
		return element.findElements(arg0);
	}

	@Override
	public String getAttribute(String arg0) {
		return element.getAttribute(arg0);
	}

	@Override
	public String getCssValue(String arg0) {
		return element.getCssValue(arg0);
	}

	@Override
	public Point getLocation() {
		return element.getLocation();
	}

	@Override
	public Rectangle getRect() {
		return element.getRect();
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> arg0) throws WebDriverException {
		return element.getScreenshotAs(arg0);
	}

	@Override
	public Dimension getSize() {
		return element.getSize();
	}

	@Override
	public String getTagName() {
		return element.getTagName();
	}

	@Override
	public String getText() {
		return element.getText();
	}

	@Override
	public boolean isDisplayed() {
		return element.isDisplayed();
	}

	@Override
	public boolean isEnabled() {
		return element.isEnabled();
	}

	@Override
	public boolean isSelected() {
		return element.isSelected();
	}

	@Override
	public void sendKeys(CharSequence... arg0) {
		element.sendKeys(arg0);
	}

	@Override
	public void submit() {
		driver.measurePerformance(null);
		element.submit();
	}

	@Override
	public boolean equals(Object obj) {
		return element.equals(obj);
	}

	@Override
	public void executeInFrame(Runnable doInFrame) {
		element.executeInFrame(doInFrame);
	}

	@Override
	public <T> T executeInFrame(Supplier<T> doInFrame) {
		return element.executeInFrame(doInFrame);
	}

	@Override
	public void executeInFrame(WebElement frameElement, Runnable doInFrame) {
		element.executeInFrame(frameElement, doInFrame);
	}

	@Override
	public <T> T executeInFrame(WebElement frameElement, Supplier<T> doInFrame) {
		return element.executeInFrame(frameElement, doInFrame);
	}

	@Override
	public WebElement findElementByClassName(String using) {
		return element.findElementByClassName(using);
	}

	@Override
	public WebElement findElementByCssSelector(String using) {
		return element.findElementByCssSelector(using);
	}

	@Override
	public WebElement findElementById(String using) {
		return element.findElementById(using);
	}

	@Override
	public WebElement findElementByLinkText(String using) {
		return element.findElementByLinkText(using);
	}

	@Override
	public WebElement findElementByName(String using) {
		return element.findElementByName(using);
	}

	@Override
	public WebElement findElementByPartialLinkText(String using) {
		return element.findElementByPartialLinkText(using);
	}

	@Override
	public WebElement findElementByTagName(String using) {
		return element.findElementByTagName(using);
	}

	@Override
	public WebElement findElementByXPath(String using) {
		return element.findElementByXPath(using);
	}

	@Override
	public List<WebElement> findElementsByClassName(String using) {
		return element.findElementsByClassName(using);
	}

	@Override
	public List<WebElement> findElementsByCssSelector(String using) {
		return element.findElementsByCssSelector(using);
	}

	@Override
	public List<WebElement> findElementsById(String using) {
		return element.findElementsById(using);
	}

	@Override
	public List<WebElement> findElementsByLinkText(String using) {
		return element.findElementsByLinkText(using);
	}

	@Override
	public List<WebElement> findElementsByName(String using) {
		return element.findElementsByName(using);
	}

	@Override
	public List<WebElement> findElementsByPartialLinkText(String using) {
		return element.findElementsByPartialLinkText(using);
	}

	@Override
	public List<WebElement> findElementsByTagName(String using) {
		return element.findElementsByTagName(using);
	}

	@Override
	public List<WebElement> findElementsByXPath(String using) {
		return element.findElementsByXPath(using);
	}

	@Override
	public PtlWebElement getFrameParent() {
		return element.getFrameParent();
	}

	@Override
	public WebElementBorderWidth getBorderWidth() {
		return element.getBorderWidth();
	}

	@Override
	public long getClientHeight() {
		return element.getClientHeight();
	}

	@Override
	public long getClientWidth() {
		return element.getClientWidth();
	}

	@Override
	public Coordinates getCoordinates() {
		return element.getCoordinates();
	}

	@Override
	public DoubleValueRect getDoubleValueRect() {
		return element.getDoubleValueRect();
	}

	@Override
	public String getId() {
		return element.getId();
	}

	@Override
	public void setFrameParent(PtlWebElement frameParent) {
		element.setFrameParent(frameParent);
	}

	@Override
	public void setParent(RemoteWebDriver parent) {
		element.setParent(parent);
	}

	@Override
	public PtlWebDriver getWrappedDriver() {
		return element.getWrappedDriver();
	}

	@Override
	public WebElementMargin getMargin() {
		return element.getMargin();
	}

	@Override
	public WebElementPadding getPadding() {
		return element.getPadding();
	}

	@Override
	public void hide() {
		element.hide();
	}

	@Override
	public void show() {
		element.show();
	}

	@Override
	public boolean isVisibilityHidden() {
		return element.isVisibilityHidden();
	}

	@Override
	public int getScrollNum() {
		return element.getScrollNum();
	}

	@Override
	public long getScrollHeight() {
		return element.getScrollHeight();
	}

	@Override
	public long getScrollWidth() {
		return element.getScrollWidth();
	}

	@Override
	public int scrollNext() throws InterruptedException {
		return element.scrollNext();
	}

	@Override
	public void scrollTo(double x, double y) throws InterruptedException {
		element.scrollTo(x, y);
	}

	@Override
	public void hideScrollBar() {
		element.hideScrollBar();
	}

	@Override
	public String[] getOverflowStatus() {
		return element.getOverflowStatus();
	}

	@Override
	public String getResizeStatus() {
		return element.getResizeStatus();
	}

	@Override
	public int hashCode() {
		return element.hashCode();
	}

	@Override
	public void setOverflowStatus(String xStatus, String yStatus) {
		element.setOverflowStatus(xStatus, yStatus);
	}

	@Override
	public boolean isBody() {
		return element.isBody();
	}

	@Override
	public boolean isFrame() {
		return element.isFrame();
	}

	@Override
	public void setFileDetector(FileDetector detector) {
		element.setFileDetector(detector);
	}

	@Override
	public void setId(String id) {
		element.setId(id);
	}

	@Override
	public void setNoResizable() {
		element.setNoResizable();
	}

	@Override
	public void setResizeStatus(String status) {
		element.setResizeStatus(status);
	}

	@Override
	public Map<String, Object> toJson() {
		return element.toJson();
	}

	@Override
	public String toString() {
		return element.toString();
	}

}
