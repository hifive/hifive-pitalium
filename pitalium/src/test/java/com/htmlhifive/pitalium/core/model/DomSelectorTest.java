package com.htmlhifive.pitalium.core.model;

import static com.google.common.collect.Lists.*;
import static java.util.Collections.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class DomSelectorTest {

	// TODO フレームを挟む複雑なテストは、実際のWebDriverを使用する

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private WebDriver driver;

	/**
	 * body
	 */
	@Mock
	private WebElement defaultContentElement;

	/**
	 * body > #parent
	 */
	@Mock
	private WebElement parentElement;

	/**
	 * body > #parent > .child
	 */
	@Mock
	private WebElement childElement;

	@Before
	public void initializeMock() throws Exception {
		MockitoAnnotations.initMocks(this);

		final By byTagNameBody = By.tagName("body");
		final By byIdMain = By.id("main");
		final By byClassNameChild = By.className("child");

		// Driver
		when(driver.findElement((By) any())).thenAnswer(new Answer<WebElement>() {
			@Override
			public WebElement answer(InvocationOnMock invocation) throws Throwable {
				Object by = invocation.getArguments()[0];
				if (by.equals(byTagNameBody)) {
					return defaultContentElement;
				} else if (by.equals(byIdMain)) {
					return parentElement;
				} else if (by.equals(byClassNameChild)) {
					return childElement;
				} else {
					throw new NoSuchElementException("");
				}
			}
		});
		when(driver.findElements((By) any())).thenAnswer(new Answer<List<WebElement>>() {
			@Override
			public List<WebElement> answer(InvocationOnMock invocation) throws Throwable {
				Object by = invocation.getArguments()[0];
				if (by.equals(byTagNameBody)) {
					return newArrayList(defaultContentElement);
				} else if (by.equals(byIdMain)) {
					return newArrayList(parentElement);
				} else if (by.equals(byClassNameChild)) {
					return newArrayList(childElement);
				} else {
					return newArrayList();
				}
			}
		});

		// Body element
		when(defaultContentElement.findElement((By) any())).thenAnswer(new Answer<WebElement>() {
			@Override
			public WebElement answer(InvocationOnMock invocation) throws Throwable {
				Object by = invocation.getArguments()[0];
				if (by.equals(byIdMain)) {
					return parentElement;
				} else if (by.equals(byClassNameChild)) {
					return childElement;
				} else {
					throw new NoSuchElementException("");
				}
			}
		});
		when(defaultContentElement.findElements((By) any())).thenAnswer(new Answer<List<WebElement>>() {
			@Override
			public List<WebElement> answer(InvocationOnMock invocation) throws Throwable {
				Object by = invocation.getArguments()[0];
				if (by.equals(byIdMain)) {
					return newArrayList(parentElement);
				} else if (by.equals(byClassNameChild)) {
					return newArrayList(childElement);
				} else {
					return newArrayList();
				}
			}
		});

		// #main element
		when(parentElement.findElement((By) any())).thenAnswer(new Answer<WebElement>() {
			@Override
			public WebElement answer(InvocationOnMock invocation) throws Throwable {
				Object by = invocation.getArguments()[0];
				if (by.equals(byClassNameChild)) {
					return childElement;
				} else {
					throw new NoSuchElementException("");
				}
			}
		});
		when(parentElement.findElements((By) any())).thenAnswer(new Answer<List<WebElement>>() {
			@Override
			public List<WebElement> answer(InvocationOnMock invocation) throws Throwable {
				Object by = invocation.getArguments()[0];
				if (by.equals(byClassNameChild)) {
					return newArrayList(childElement);
				} else {
					return newArrayList();
				}
			}
		});

		// #main element
		when(childElement.findElement((By) any())).thenThrow(new NoSuchElementException(""));
		when(childElement.findElements((By) any())).thenReturn(Collections.<WebElement> emptyList());
	}

	/**
	 * driver -> body
	 */
	@Test
	public void findElement_driver() throws Exception {
		DomSelector selector = new DomSelector(SelectorType.TAG_NAME, "body");
		assertThat(selector.findElement(driver), is(defaultContentElement));
	}

	/**
	 * driver -> [body]
	 */
	@Test
	public void findElements_driver() throws Exception {
		DomSelector selector = new DomSelector(SelectorType.TAG_NAME, "body");
		assertThat(selector.findElements(driver), is(singletonList(defaultContentElement)));
	}

	/**
	 * driver -> ???
	 */
	@Test
	public void findElement_driver_notFound() throws Exception {
		expectedException.expect(NoSuchElementException.class);

		DomSelector selector = new DomSelector(SelectorType.TAG_NAME, "none");
		selector.findElement(driver);
	}

	/**
	 * driver -> [???]
	 */
	@Test
	public void findElements_driver_notFound() throws Exception {
		DomSelector selector = new DomSelector(SelectorType.TAG_NAME, "none");
		assertThat(selector.findElements(driver), is(Collections.<WebElement> emptyList()));
	}

	/**
	 * body -> #main
	 */
	@Test
	public void findElement_element() throws Exception {
		DomSelector selector = new DomSelector(SelectorType.ID, "main");
		assertThat(selector.findElement(defaultContentElement), is(parentElement));
	}

	/**
	 * body -> [#main]
	 */
	@Test
	public void findElements_element() throws Exception {
		DomSelector selector = new DomSelector(SelectorType.ID, "main");
		assertThat(selector.findElements(defaultContentElement), is(singletonList(parentElement)));
	}

	/**
	 * body -> ???
	 */
	@Test
	public void findElement_element_notFound() throws Exception {
		expectedException.expect(NoSuchElementException.class);

		DomSelector selector = new DomSelector(SelectorType.ID, "none");
		selector.findElement(defaultContentElement);
	}

	/**
	 * body -> [???]
	 */
	@Test
	public void findElements_element_notFound() throws Exception {
		DomSelector selector = new DomSelector(SelectorType.ID, "none");
		assertThat(selector.findElements(defaultContentElement), is(Collections.<WebElement> emptyList()));
	}

	/**
	 * driver -> body -> #main
	 */
	@Test
	public void findElement_driver_nested() throws Exception {
		DomSelector selector = new DomSelector(SelectorType.ID, "main", new DomSelector(SelectorType.TAG_NAME, "body"));
		assertThat(selector.findElement(driver), is(parentElement));
	}

	/**
	 * driver -> body -> [#main]
	 */
	@Test
	public void findElements_driver_nested() throws Exception {
		DomSelector selector = new DomSelector(SelectorType.ID, "main", new DomSelector(SelectorType.TAG_NAME, "body"));
		assertThat(selector.findElements(driver), is(singletonList(parentElement)));
	}

	/**
	 * driver -> body -> ???
	 */
	@Test
	public void findElement_driver_nested_notFound() throws Exception {
		expectedException.expect(NoSuchElementException.class);

		DomSelector selector = new DomSelector(SelectorType.ID, "none", new DomSelector(SelectorType.TAG_NAME, "body"));
		selector.findElement(driver);
	}

	/**
	 * driver -> body -> [???]
	 */
	@Test
	public void findElements_driver_nested_notFound() throws Exception {
		expectedException.expect(NoSuchElementException.class);

		DomSelector selector = new DomSelector(SelectorType.ID, "none", new DomSelector(SelectorType.TAG_NAME, "body"));
		selector.findElements(defaultContentElement);
	}

	/**
	 * body -> #main -> .child
	 */
	@Test
	public void findElement_element_nested() throws Exception {
		DomSelector selector = new DomSelector(SelectorType.CLASS_NAME, "child", new DomSelector(SelectorType.ID,
				"main"));
		assertThat(selector.findElement(defaultContentElement), is(childElement));
	}

	/**
	 * body -> #main -> [.child]
	 */
	@Test
	public void findElements_element_nested() throws Exception {
		DomSelector selector = new DomSelector(SelectorType.CLASS_NAME, "child", new DomSelector(SelectorType.ID,
				"main"));
		assertThat(selector.findElements(defaultContentElement), is(singletonList(childElement)));
	}

	/**
	 * body -> #main -> ???
	 */
	@Test
	public void findElement_element_nested_notFound() throws Exception {
		expectedException.expect(NoSuchElementException.class);

		DomSelector selector = new DomSelector(SelectorType.CLASS_NAME, "none",
				new DomSelector(SelectorType.ID, "main"));
		selector.findElement(defaultContentElement);
	}

	/**
	 * body -> #main -> [???]
	 */
	@Test
	public void findElements_element_nested_notFound() throws Exception {
		DomSelector selector = new DomSelector(SelectorType.CLASS_NAME, "none",
				new DomSelector(SelectorType.ID, "main"));
		assertThat(selector.findElements(defaultContentElement), is(Collections.<WebElement> emptyList()));
	}

	/**
	 * body -> ??? -> .child
	 */
	@Test
	public void findElement_element_nested_notFound_parent() throws Exception {
		expectedException.expect(NoSuchElementException.class);

		DomSelector selector = new DomSelector(SelectorType.CLASS_NAME, "child", new DomSelector(SelectorType.ID,
				"none"));
		selector.findElement(defaultContentElement);
	}

	/**
	 * body -> ??? -> [.child]
	 */
	@Test
	public void findElements_element_nested_notFound_parent() throws Exception {
		expectedException.expect(NoSuchElementException.class);

		DomSelector selector = new DomSelector(SelectorType.CLASS_NAME, "child", new DomSelector(SelectorType.ID,
				"none"));
		selector.findElements(defaultContentElement);
	}

}
