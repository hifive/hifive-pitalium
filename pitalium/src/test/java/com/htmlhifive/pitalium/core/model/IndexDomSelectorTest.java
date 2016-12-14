package com.htmlhifive.pitalium.core.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class IndexDomSelectorTest {

	@Mock
	private WebDriver driver;

	/**
	 * body
	 */
	@Mock
	private WebElement defaultContentBody;

	/**
	 * body > .parent
	 */
	@Mock
	private WebElement parentElement1;

	/**
	 * body > .parent
	 */
	@Mock
	private WebElement parentElement2;

	/**
	 * body > .parent:first > .child
	 */
	@Mock
	private WebElement childElement;

	@Before
	public void initializeMock() throws Exception {
		MockitoAnnotations.initMocks(this);

		final By byTagNameBody = By.tagName("body");
		final By byClassNameParent = By.className("parent");
		final By byClassNameChild = By.className("child");

		when(driver.findElements((By) any())).thenAnswer(new Answer<List<WebElement>>() {
			@Override
			public List<WebElement> answer(InvocationOnMock invocationOnMock) throws Throwable {
				By by = (By) invocationOnMock.getArguments()[0];
				if (by.equals(byTagNameBody)) {
					return newArrayList(defaultContentBody);
				} else if (by.equals(byClassNameParent)) {
					return newArrayList(parentElement1, parentElement2);
				} else if (by.equals(byClassNameChild)) {
					return newArrayList(childElement);
				} else {
					return newArrayList();
				}
			}
		});
		when(driver.findElement((By) any())).thenAnswer(new Answer<WebElement>() {
			@Override
			public WebElement answer(InvocationOnMock invocationOnMock) throws Throwable {
				List<WebElement> elements = driver.findElements((By) invocationOnMock.getArguments()[0]);
				if (elements.isEmpty()) {
					throw new NoSuchElementException("");
				}
				return elements.get(0);
			}
		});

		when(defaultContentBody.findElements((By) any())).thenAnswer(new Answer<List<WebElement>>() {
			@Override
			public List<WebElement> answer(InvocationOnMock invocationOnMock) throws Throwable {
				By by = (By) invocationOnMock.getArguments()[0];
				if (by.equals(byClassNameParent)) {
					return newArrayList(parentElement1, parentElement2);
				} else if (by.equals(byClassNameChild)) {
					return newArrayList(childElement);
				} else {
					return newArrayList();
				}
			}
		});
		when(defaultContentBody.findElement((By) any())).thenAnswer(new Answer<WebElement>() {
			@Override
			public WebElement answer(InvocationOnMock invocationOnMock) throws Throwable {
				List<WebElement> elements = defaultContentBody.findElements((By) invocationOnMock.getArguments()[0]);
				if (elements.isEmpty()) {
					throw new NoSuchElementException("");
				}
				return elements.get(0);
			}
		});

		when(parentElement1.findElements((By) any())).thenAnswer(new Answer<List<WebElement>>() {
			@Override
			public List<WebElement> answer(InvocationOnMock invocationOnMock) throws Throwable {
				By by = (By) invocationOnMock.getArguments()[0];
				if (by.equals(byClassNameChild)) {
					return newArrayList(childElement);
				} else {
					return newArrayList();
				}
			}
		});
		when(parentElement1.findElement((By) any())).thenAnswer(new Answer<WebElement>() {
			@Override
			public WebElement answer(InvocationOnMock invocationOnMock) throws Throwable {
				By by = (By) invocationOnMock.getArguments()[0];
				if (by.equals(byClassNameChild)) {
					return childElement;
				} else {
					throw new NoSuchElementException("");
				}
			}
		});

		when(parentElement2.findElements((By) any())).thenThrow(new NoSuchElementException(""));
		when(parentElement2.findElement((By) any())).thenThrow(new NoSuchElementException(""));
		when(childElement.findElements((By) any())).thenThrow(new NoSuchElementException(""));
		when(childElement.findElement((By) any())).thenThrow(new NoSuchElementException(""));
	}

	@Test
	public void findElement_driver_noIndex() throws Exception {
		IndexDomSelector selector = new IndexDomSelector(SelectorType.CLASS_NAME, "parent", null);
		assertThat(selector.findElement(driver), is(parentElement1));
	}

	@Test
	public void findElements_driver_noIndex() throws Exception {
		IndexDomSelector selector = new IndexDomSelector(SelectorType.CLASS_NAME, "parent", null);
		assertThat(selector.findElements(driver), is(asList(parentElement1, parentElement2)));
	}

	@Test
	public void findElement_driver_index() throws Exception {
		IndexDomSelector selector = new IndexDomSelector(SelectorType.CLASS_NAME, "parent", 1);
		assertThat(selector.findElement(driver), is(parentElement2));
	}

	@Test
	public void findElements_driver_index() throws Exception {
		IndexDomSelector selector = new IndexDomSelector(SelectorType.CLASS_NAME, "parent", 1);
		assertThat(selector.findElements(driver), is(singletonList(parentElement2)));
	}

	@Test
	public void findElement_element_noIndex() throws Exception {
		IndexDomSelector selector = new IndexDomSelector(SelectorType.CLASS_NAME, "parent", null);
		assertThat(selector.findElement(defaultContentBody), is(parentElement1));
	}

	@Test
	public void findElements_element_noIndex() throws Exception {
		IndexDomSelector selector = new IndexDomSelector(SelectorType.CLASS_NAME, "parent", null);
		assertThat(selector.findElements(defaultContentBody), is(asList(parentElement1, parentElement2)));
	}

	@Test
	public void findElement_element_index() throws Exception {
		IndexDomSelector selector = new IndexDomSelector(SelectorType.CLASS_NAME, "parent", 1);
		assertThat(selector.findElement(defaultContentBody), is(parentElement2));
	}

	@Test
	public void findElements_element_index() throws Exception {
		IndexDomSelector selector = new IndexDomSelector(SelectorType.CLASS_NAME, "parent", 1);
		assertThat(selector.findElements(defaultContentBody), is(singletonList(parentElement2)));
	}

}
