/*
 * Copyright (C) 2015 NS Solutions Corporation
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

package com.htmlhifive.pitalium.core.model;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ScreenshotArgumentTest {

	@Rule
	public ExpectedException expected = ExpectedException.none();

	//<editor-fold desc="Constructor">

	@Test
	public void constructor_screenshotId_null() throws Exception {
		expected.expect(NullPointerException.class);
		expected.expectMessage(containsString("screenshotId"));

		new ScreenshotArgument(null, new ArrayList<CompareTarget>(), new ArrayList<DomSelector>());
	}

	@Test
	public void constructor_screenshotId_empty() throws Exception {
		expected.expect(NullPointerException.class);
		expected.expectMessage(containsString("screenshotId"));

		new ScreenshotArgument("", new ArrayList<CompareTarget>(), new ArrayList<DomSelector>());
	}

	@Test
	public void constructor_targets_null() throws Exception {
		ScreenshotArgument arg = new ScreenshotArgument("scid", null, new ArrayList<DomSelector>());

		assertThat(arg.getTargets(), is(notNullValue()));
		assertThat(arg.getTargets().isEmpty(), is(true));
	}

	@Test
	public void constructor_targets_null_unmodifiable() throws Exception {
		ScreenshotArgument arg = new ScreenshotArgument("scid", null, new ArrayList<DomSelector>());

		// Unmodifiable
		expected.expect(UnsupportedOperationException.class);
		arg.getTargets().add(new CompareTarget());
	}

	@Test
	public void constructor_hiddenElementSelectors_null() throws Exception {
		ScreenshotArgument arg = new ScreenshotArgument("scid", new ArrayList<CompareTarget>(), null);

		assertThat(arg.getHiddenElementSelectors(), is(notNullValue()));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));
	}

	@Test
	public void constructor_hiddenElementSelectors_null_unmodifiable() throws Exception {
		ScreenshotArgument arg = new ScreenshotArgument("scid", new ArrayList<CompareTarget>(), null);

		expected.expect(UnsupportedOperationException.class);
		arg.getHiddenElementSelectors().add(new DomSelector(SelectorType.ID, "id"));
	}

	//</editor-fold>

	//<editor-fold desc="Getter">

	@Test
	public void getter_screenshotId() throws Exception {
		ScreenshotArgument arg = new ScreenshotArgument("scid", null, null);

		assertThat(arg.getScreenshotId(), is("scid"));
	}

	@Test
	public void getter_targets() throws Exception {
		CompareTarget[] targets = { new CompareTarget(), new CompareTarget(ScreenArea.of(SelectorType.ID, "id")) };
		ScreenshotArgument arg = new ScreenshotArgument("scid", Arrays.asList(targets), null);

		List<CompareTarget> result = arg.getTargets();
		assertArrayEquals(targets, result.toArray(new CompareTarget[result.size()]));
	}

	@Test
	public void getter_targets_unmodifiable() throws Exception {
		CompareTarget[] targets = { new CompareTarget(), new CompareTarget(ScreenArea.of(SelectorType.ID, "id")) };
		ScreenshotArgument arg = new ScreenshotArgument("scid", Arrays.asList(targets), null);

		expected.expect(UnsupportedOperationException.class);
		arg.getTargets().add(new CompareTarget());
	}

	@Test
	public void getter_hiddenElementSelectors() throws Exception {
		DomSelector[] selectors = { new DomSelector(SelectorType.ID, "id"),
				new DomSelector(SelectorType.CLASS_NAME, "class") };
		ScreenshotArgument arg = new ScreenshotArgument("scid", null, Arrays.asList(selectors));

		List<DomSelector> result = arg.getHiddenElementSelectors();
		assertArrayEquals(selectors, result.toArray(new DomSelector[result.size()]));
	}

	@Test
	public void getter_hiddenElementSelectors_unmodifiable() throws Exception {
		DomSelector[] selectors = { new DomSelector(SelectorType.ID, "id"),
				new DomSelector(SelectorType.CLASS_NAME, "class") };
		ScreenshotArgument arg = new ScreenshotArgument("scid", null, Arrays.asList(selectors));

		expected.expect(UnsupportedOperationException.class);
		arg.getHiddenElementSelectors().add(new DomSelector(SelectorType.ID, "test"));
	}

	//</editor-fold>

	@Test
	public void withScreenshotId() throws Exception {
		CompareTarget[] targets = { new CompareTarget(), new CompareTarget(ScreenArea.of(SelectorType.ID, "id")) };
		DomSelector[] selectors = { new DomSelector(SelectorType.ID, "id"),
				new DomSelector(SelectorType.CLASS_NAME, "class") };

		ScreenshotArgument arg = new ScreenshotArgument("scid", Arrays.asList(targets), Arrays.asList(selectors));
		ScreenshotArgument newObj = arg.withScreenshotId("id");

		// Not same object
		// Only screenshotId is changed

		assertThat(newObj, is(not(sameInstance(arg))));
		assertThat(newObj.getScreenshotId(), is("id"));
		assertArrayEquals(targets, newObj.getTargets().toArray());
		assertArrayEquals(selectors, newObj.getHiddenElementSelectors().toArray());
	}

}