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

package com.htmlhifive.pitalium.core.model;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

@SuppressWarnings("all")
public class ScreenshotArgumentBuilderTest {

// @formatter:off
	private static final List<Pair<String, SelectorType>> TYPE_MAPPINGS = Arrays.asList(
			Pair.of("Id", SelectorType.ID),
			Pair.of("ClassName", SelectorType.CLASS_NAME),
			Pair.of("CssSelector", SelectorType.CSS_SELECTOR),
			Pair.of("LinkText", SelectorType.LINK_TEXT),
			Pair.of("Name", SelectorType.NAME),
			Pair.of("PartialLinkText", SelectorType.PARTIAL_LINK),
			Pair.of("TagName", SelectorType.TAG_NAME),
			Pair.of("XPath", SelectorType.XPATH));
// @formatter:on

	@Rule
	public ExpectedException expected = ExpectedException.none();

	/**
	 * デフォルトコンストラクタのままbuildするとエラー
	 */
	@Test
	public void constructor() throws Exception {
		ScreenshotArgumentBuilder builder = new ScreenshotArgumentBuilder();

		expected.expect(IllegalStateException.class);
		expected.expectMessage(containsString("screenshotId"));

		builder.build();
	}

	/**
	 * コンストラクタでSSIDにnullを指定してbuildするとエラー
	 */
	@Test
	public void constructor_null() throws Exception {
		ScreenshotArgumentBuilder builder = new ScreenshotArgumentBuilder(null);

		expected.expect(IllegalStateException.class);
		expected.expectMessage(containsString("screenshotId"));

		builder.build();
	}

	/**
	 * コンストラクタでSSIDに空文字を指定してbuildするとエラー
	 */
	@Test
	public void constructor_empty() throws Exception {
		ScreenshotArgumentBuilder builder = new ScreenshotArgumentBuilder("");

		expected.expect(IllegalStateException.class);
		expected.expectMessage(containsString("screenshotId"));

		builder.build();
	}

	/**
	 * コンストラクタにSSIDを指定してbuild
	 */
	@Test
	public void constructor_ssid() throws Exception {
		ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid").build();

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().isEmpty(), is(true));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));
	}

	/**
	 * SSIDを空から値を指定する
	 */
	@Test
	public void screenshotId_empty_to_notEmpty() throws Exception {
		ScreenshotArgument arg = new ScreenshotArgumentBuilder().screenshotId("ssid").build();

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().isEmpty(), is(true));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));
	}

	/**
	 * SSIDを空から空を指定する。
	 */
	@Test
	public void screenshotId_empty_to_empty() throws Exception {
		ScreenshotArgumentBuilder builder = new ScreenshotArgumentBuilder().screenshotId(null);

		expected.expect(IllegalStateException.class);
		expected.expectMessage(containsString("screenshotId"));

		builder.build();
	}

	/**
	 * SSIDの値を変更する
	 */
	@Test
	public void screenshotId_notEmpty_to_notEmpty() throws Exception {
		ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid").screenshotId("id").build();

		assertThat(arg.getScreenshotId(), is("id"));
		assertThat(arg.getTargets().isEmpty(), is(true));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));
	}

	/**
	 * SSIDを空に変更する。
	 */
	@Test
	public void screenshotId_notEmpty_to_empty() throws Exception {
		ScreenshotArgumentBuilder builder = new ScreenshotArgumentBuilder("ssid").screenshotId(null);

		expected.expect(IllegalStateException.class);
		expected.expectMessage(containsString("screenshotId"));

		builder.build();
	}

	/**
	 * 引数なしのaddNewTargetはデフォルトのCompareTarget
	 */
	@Test
	public void addNewTarget() throws Exception {
		ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid").addNewTarget().build();

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().size(), is(1));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));

		assertThat(arg.getTargets().get(0), is(new CompareTarget()));
	}

	/**
	 * {@link ScreenshotArgumentBuilder#addNewTarget(SelectorType, String)}
	 */
	@Test
	public void addNewTarget_selector() throws Exception {
		for (SelectorType type : SelectorType.values()) {
			ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid").addNewTarget(type, "target").build();

			assertThat(arg.getScreenshotId(), is("ssid"));
			assertThat(arg.getTargets().size(), is(1));
			assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));

			assertThat(arg.getTargets().get(0), is(new CompareTarget(ScreenArea.of(type, "target"))));
		}
	}

	/**
	 * addNewTargetByXxx系。全てのメソッドのテストは大変なのでリフレクションで。
	 */
	@Test
	public void addNewTargetByXxx() throws Exception {
		Class<ScreenshotArgumentBuilder> clss = ScreenshotArgumentBuilder.class;
		for (Pair<String, SelectorType> mapping : TYPE_MAPPINGS) {
			Method method = clss.getMethod("addNewTargetBy" + mapping.getKey(), String.class);

			ScreenshotArgumentBuilder builder = new ScreenshotArgumentBuilder("ssid");
			method.invoke(builder, "target");

			ScreenshotArgument arg = builder.build();

			assertThat(arg.getScreenshotId(), is("ssid"));
			assertThat(arg.getTargets().size(), is(1));
			assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));

			assertThat(arg.getTargets().get(0), is(new CompareTarget(ScreenArea.of(mapping.getValue(), "target"))));
		}
	}

	/**
	 * 座標を元にaddNewTarget
	 */
	@Test
	public void addNewTarget_position() throws Exception {
		ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid").addNewTarget(1d, 2d, 3d, 4d).build();

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().size(), is(1));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));

		assertThat(arg.getTargets().get(0), is(new CompareTarget(ScreenArea.of(1d, 2d, 3d, 4d))));
	}

	/**
	 * 複数回addNewTarget
	 */
	@Test
	public void addNewTarget_multiple() throws Exception {
// @formatter:off
        ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid")
                .addNewTargetById("id")
                .addNewTargetByClassName("class")
                .addNewTargetByName("name")
                .build();
// @formatter:on

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().size(), is(3));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));

		assertThat(arg.getTargets().get(0), is(new CompareTarget(ScreenArea.of(SelectorType.ID, "id"))));
		assertThat(arg.getTargets().get(1), is(new CompareTarget(ScreenArea.of(SelectorType.CLASS_NAME, "class"))));
		assertThat(arg.getTargets().get(2), is(new CompareTarget(ScreenArea.of(SelectorType.NAME, "name"))));
	}

	/**
	 * addNewTargetで既存のCompareTargetを追加
	 */
	@Test
	public void addNewTarget_CompareTarget() throws Exception {
		CompareTarget target = new CompareTarget(ScreenArea.of(SelectorType.ID, "id"), new ScreenArea[] {
				ScreenArea.of(SelectorType.CSS_SELECTOR, "css"), ScreenArea.of(1d, 2d, 3d, 4d) }, false);

// @formatter:off
		ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid")
				.addNewTarget(target)
				.build();
// @formatter:on

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().size(), is(1));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));

		assertThat(arg.getTargets().get(0), is(target));
		assertThat(arg.getTargets().get(0), is(not(sameInstance(target))));
	}

	/**
	 * addNewTargetで既存のCompareTargetを追加
	 */
	@Test
	public void addNewTarget_CompareTarget_edit() throws Exception {
		CompareTarget target = new CompareTarget(ScreenArea.of(SelectorType.ID, "id"), new ScreenArea[] {
				ScreenArea.of(SelectorType.CSS_SELECTOR, "css"), ScreenArea.of(1d, 2d, 3d, 4d) }, false);

// @formatter:off
        ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid")
                .addNewTarget(target)
                    .addExcludeByPartialLinkText("partial")
                    .moveTarget(true)
                .build();
// @formatter:on

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().size(), is(1));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));

		assertThat(arg.getTargets().get(0), is(new CompareTarget(ScreenArea.of(SelectorType.ID, "id"),
				new ScreenArea[] { ScreenArea.of(SelectorType.CSS_SELECTOR, "css"), ScreenArea.of(1d, 2d, 3d, 4d),
						ScreenArea.of(SelectorType.PARTIAL_LINK, "partial") }, true)));
	}

	/**
	 * addMewTargetで既存のScreenAreaを追加
	 */
	@Test
	public void addNewTarget_ScreenArea() throws Exception {
// @formatter:off
        ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid")
                .addNewTarget(ScreenArea.of(SelectorType.ID, "id"))
                .build();
// @formatter:on

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().size(), is(1));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));

		assertThat(arg.getTargets().get(0), is(new CompareTarget(ScreenArea.of(SelectorType.ID, "id"), null, true)));
	}

	/**
	 * addExcludeをaddNewTargetより前にコールすると例外
	 */
	@Test
	public void addExclude_beforeCall_addNewTarget() throws Exception {
		ScreenshotArgumentBuilder builder = new ScreenshotArgumentBuilder("ssid");

		expected.expect(IllegalStateException.class);
		expected.expectMessage(containsString("addNewTarget is not called"));

		builder.addExclude(SelectorType.ID, "target");
	}

	/**
	 * Excludeを追加する
	 */
	@Test
	public void addExclude() throws Exception {
// @formatter:off
		ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid")
				.addNewTargetByName("name")
					.addExclude(SelectorType.ID, "ex")
				.build();
// @formatter:on

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().size(), is(1));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));

		assertThat(arg.getTargets().get(0), is(new CompareTarget(ScreenArea.of(SelectorType.NAME, "name"),
				new ScreenArea[] { ScreenArea.of(SelectorType.ID, "ex") }, true)));
	}

	/**
	 * Excludeを追加する
	 */
	@Test
	public void addExclude_frame() throws Exception {
// @formatter:off
		ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid")
				.addNewTargetByName("name")
					.addExclude(SelectorType.ID, "ex", SelectorType.ID, "frame")
				.build();
// @formatter:on

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().size(), is(1));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));

		assertThat(arg.getTargets().get(0), is(new CompareTarget(ScreenArea.of(SelectorType.NAME, "name"),
				new ScreenArea[] { ScreenArea.of(SelectorType.ID, "ex", SelectorType.ID, "frame") }, true)));
	}

	/**
	 * addExcludeByXxxをリフレクションで
	 */
	@Test
	public void addExcludeByXxx() throws Exception {
		for (Pair<String, SelectorType> mapping : TYPE_MAPPINGS) {
			ScreenshotArgumentBuilder builder = new ScreenshotArgumentBuilder("ssid").addNewTargetByXPath("xpath");

			Method method = ScreenshotArgumentBuilder.class.getMethod("addExcludeBy" + mapping.getKey(), String.class);
			method.invoke(builder, "value");

			ScreenshotArgument arg = builder.build();

			assertThat(arg.getScreenshotId(), is("ssid"));
			assertThat(arg.getTargets().size(), is(1));
			assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));

			assertThat(arg.getTargets().get(0), is(new CompareTarget(ScreenArea.of(SelectorType.XPATH, "xpath"),
					new ScreenArea[] { ScreenArea.of(mapping.getValue(), "value") }, true)));
		}
	}

	/**
	 * 座標のExcludeを追加
	 */
	@Test
	public void addExclude_position() throws Exception {
// @formatter:off
        ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid")
                .addNewTargetById("id")
                    .addExclude(1d, 2d, 3d, 4d)
                .build();
// @formatter:on

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().size(), is(1));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));

		assertThat(arg.getTargets().get(0), is(new CompareTarget(ScreenArea.of(SelectorType.ID, "id"),
				new ScreenArea[] { ScreenArea.of(1d, 2d, 3d, 4d) }, true)));
	}

	/**
	 * Excludeを複数追加する
	 */
	@Test
	public void addExclude_multiple() throws Exception {
// @formatter:off
        ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid")
                .addNewTargetByCssSelector("css")
                    .addExcludeById("ex_id")
                    .addExcludeByCssSelector("ex_css")
                .build();
// @formatter:on

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().size(), is(1));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));

		assertThat(
				arg.getTargets().get(0),
				is(new CompareTarget(ScreenArea.of(SelectorType.CSS_SELECTOR, "css"), new ScreenArea[] {
						ScreenArea.of(SelectorType.ID, "ex_id"), ScreenArea.of(SelectorType.CSS_SELECTOR, "ex_css") },
						true)));
	}

	/**
	 * 複数のターゲットにexclude追加
	 */
	@Test
	public void addExclude_to_multipleTargets() throws Exception {
// @formatter:off
        ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid")
                .addNewTargetByLinkText("link")
                .addNewTargetByPartialLinkText("partial_link")
                    .addExcludeByXPath("ex_xpath")
                .addNewTargetByTagName("tag")
                    .addExcludeByClassName("ex_class")
                    .addExcludeByLinkText("ex_link")
                .build();
// @formatter:on

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().size(), is(3));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));

		assertThat(arg.getTargets().get(0), is(new CompareTarget(ScreenArea.of(SelectorType.LINK_TEXT, "link"))));
		assertThat(arg.getTargets().get(1),
				is(new CompareTarget(ScreenArea.of(SelectorType.PARTIAL_LINK, "partial_link"),
						new ScreenArea[] { ScreenArea.of(SelectorType.XPATH, "ex_xpath") }, true)));
		assertThat(
				arg.getTargets().get(2),
				is(new CompareTarget(ScreenArea.of(SelectorType.TAG_NAME, "tag"), new ScreenArea[] {
						ScreenArea.of(SelectorType.CLASS_NAME, "ex_class"),
						ScreenArea.of(SelectorType.LINK_TEXT, "ex_link") }, true)));
	}

	/**
	 * Excludeをコレクションで追加
	 */
	@Test
	public void addExcludes_collection() throws Exception {
		ScreenArea[] areas = { ScreenArea.of(SelectorType.ID, "id"), ScreenArea.of(1d, 2d, 3d, 4d) };
		Collection<ScreenArea> excludes = Arrays.asList(areas);

		ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid").addNewTarget().addExcludes(excludes).build();

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().size(), is(1));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));

		assertThat(arg.getTargets().get(0), is(new CompareTarget(ScreenArea.of(SelectorType.TAG_NAME, "body"), areas,
				true)));
	}

	/**
	 * Excludeを配列で追加
	 */
	@Test
	public void addExcludes_array() throws Exception {
		ScreenArea[] areas = { ScreenArea.of(SelectorType.ID, "id"), ScreenArea.of(1d, 2d, 3d, 4d) };
		ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid").addNewTarget().addExcludes(areas).build();

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().size(), is(1));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));

		assertThat(arg.getTargets().get(0), is(new CompareTarget(ScreenArea.of(SelectorType.TAG_NAME, "body"), areas,
				true)));
	}

	/**
	 * moveTargetをaddNewTargetより前にコールすると例外
	 */
	@Test
	public void moveTarget_beforeCall_addNewTarget() throws Exception {
		ScreenshotArgumentBuilder builder = new ScreenshotArgumentBuilder("ssid");

		expected.expect(IllegalStateException.class);
		expected.expectMessage(containsString("addNewTarget is not called"));

		builder.moveTarget(false);
	}

	/**
	 * moveTargetにtrueを設定
	 */
	@Test
	public void moveTarget_true() throws Exception {
// @formatter:off
        ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid")
                .addNewTargetById("id")
                    .moveTarget(true)
                .build();
// @formatter:on

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().size(), is(1));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));

		assertThat(arg.getTargets().get(0), is(new CompareTarget(ScreenArea.of(SelectorType.ID, "id"), null, true)));
	}

	/**
	 * moveTargetにfalseを設定
	 */
	@Test
	public void moveTarget_false() throws Exception {
// @formatter:off
        ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid")
                .addNewTargetById("id")
                    .moveTarget(false)
                .build();
// @formatter:on

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().size(), is(1));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));

		assertThat(arg.getTargets().get(0), is(new CompareTarget(ScreenArea.of(SelectorType.ID, "id"), null, false)));
	}

	/**
	 * moveTargetを複数回呼ぶと最後の設定に
	 */
	@Test
	public void moveTarget_multiple_true_to_false() throws Exception {
// @formatter:off
        ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid")
                .addNewTargetById("id")
                    .moveTarget(true)
                    .moveTarget(false)
                .build();
// @formatter:on

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().size(), is(1));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));

		assertThat(arg.getTargets().get(0), is(new CompareTarget(ScreenArea.of(SelectorType.ID, "id"), null, false)));
	}

	/**
	 * moveTargetを複数回呼ぶと最後の設定に
	 */
	@Test
	public void moveTarget_multiple_false_to_true() throws Exception {
// @formatter:off
        ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid")
                .addNewTargetById("id")
                    .moveTarget(false)
                    .moveTarget(true)
                .build();
// @formatter:on

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().size(), is(1));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));

		assertThat(arg.getTargets().get(0), is(new CompareTarget(ScreenArea.of(SelectorType.ID, "id"), null, true)));
	}

	/**
	 * moveTargetを複数のTargetに指定
	 */
	@Test
	public void moveTarget_to_multipleTargets() throws Exception {
// @formatter:off
        ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid")
                .addNewTargetById("id")
                    .moveTarget(false)
                .addNewTarget(1d, 2d, 3d, 4d)
                    .moveTarget(true)
                .addNewTarget()
                    .moveTarget(false)
                .build();
// @formatter:on

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().size(), is(3));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));

		assertThat(arg.getTargets().get(0), is(new CompareTarget(ScreenArea.of(SelectorType.ID, "id"), null, false)));
		assertThat(arg.getTargets().get(1), is(new CompareTarget(ScreenArea.of(1d, 2d, 3d, 4d), null, true)));
		assertThat(arg.getTargets().get(2), is(new CompareTarget(ScreenArea.of(SelectorType.TAG_NAME, "body"), null,
				false)));
	}

	/**
	 * addHiddenElementSelectorを追加
	 */
	@Test
	public void addHiddenElementSelector() throws Exception {
// @formatter:off
		ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid")
				.addHiddenElementSelector(SelectorType.ID, "id")
				.build();
// @formatter:on

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().isEmpty(), is(true));
		assertThat(arg.getHiddenElementSelectors().size(), is(1));

		assertThat(arg.getHiddenElementSelectors().get(0), is(new DomSelector(SelectorType.ID, "id")));
	}

	/**
	 * addHiddenElementSelectorを追加
	 */
	@Test
	public void addHiddenElementSelector_frame() throws Exception {
// @formatter:off
		ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid")
				.addHiddenElementSelector(SelectorType.ID, "id", SelectorType.ID, "frame")
				.build();
// @formatter:on

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().isEmpty(), is(true));
		assertThat(arg.getHiddenElementSelectors().size(), is(1));

		assertThat(arg.getHiddenElementSelectors().get(0), is(new DomSelector(SelectorType.ID, "id", new DomSelector(
				SelectorType.ID, "frame"))));
	}

	/**
	 * AddHiddenElementsByXxx...
	 */
	@Test
	public void addHiddenElementsByXxx() throws Exception {
		for (Pair<String, SelectorType> mapping : TYPE_MAPPINGS) {
			ScreenshotArgumentBuilder builder = new ScreenshotArgumentBuilder("ssid");

			ScreenshotArgumentBuilder.class.getMethod("addHiddenElementsBy" + mapping.getKey(), String.class).invoke(
					builder, "value");
			ScreenshotArgument arg = builder.build();

			assertThat(arg.getScreenshotId(), is("ssid"));
			assertThat(arg.getTargets().isEmpty(), is(true));
			assertThat(arg.getHiddenElementSelectors().size(), is(1));

			assertThat(arg.getHiddenElementSelectors().get(0), is(new DomSelector(mapping.getValue(), "value")));
		}
	}

	/**
	 * HiddenElementSelectorをコレクションで追加
	 */
	@Test
	public void addHiddenElementSelectors_collection() throws Exception {
		DomSelector[] selectors = { new DomSelector(SelectorType.ID, "id"),
				new DomSelector(SelectorType.TAG_NAME, "tag") };
		ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid").addHiddenElementSelectors(
				Arrays.asList(selectors)).build();

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().isEmpty(), is(true));
		assertThat(arg.getHiddenElementSelectors().size(), is(2));

		assertThat(arg.getHiddenElementSelectors().get(0), is(selectors[0]));
		assertThat(arg.getHiddenElementSelectors().get(1), is(selectors[1]));
	}

	/**
	 * HiddenElementSelectorを配列で追加
	 */
	@Test
	public void addHiddenElementSelectors_array() throws Exception {
		DomSelector[] selectors = { new DomSelector(SelectorType.ID, "id"),
				new DomSelector(SelectorType.TAG_NAME, "tag") };
		ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid").addHiddenElementSelectors(selectors).build();

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().isEmpty(), is(true));
		assertThat(arg.getHiddenElementSelectors().size(), is(2));

		assertThat(arg.getHiddenElementSelectors().get(0), is(selectors[0]));
		assertThat(arg.getHiddenElementSelectors().get(1), is(selectors[1]));
	}

	//<editor-fold desc="inFrame">

	/**
	 * exclude + frame
	 */
	@Test
	public void inFrame_exclude() throws Exception {
		for (Pair<String, SelectorType> mapping : TYPE_MAPPINGS) {
			ScreenshotArgumentBuilder builder = new ScreenshotArgumentBuilder("ssid").addNewTargetByXPath("xpath")
					.addExcludeById("id");

			Method method = ScreenshotArgumentBuilder.class.getMethod("inFrameBy" + mapping.getKey(), String.class);
			method.invoke(builder, "value");

			ScreenshotArgument arg = builder.build();

			ScreenArea[] excludes = arg.getTargets().get(0).getExcludes();
			assertThat(excludes.length, is(1));
			assertThat(excludes[0], is(ScreenArea.of(SelectorType.ID, "id", mapping.getRight(), "value")));
		}
	}

	/**
	 * exclude (collection) + frame
	 */
	@Test
	public void inFrame_excludes() throws Exception {
		// formatter: off
		ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid")
				.addNewTarget()
				.addExcludes(ScreenArea.of(SelectorType.ID, "id"), ScreenArea.of(SelectorType.TAG_NAME, "body"),
						ScreenArea.of(0.0, 0.0, 1.0, 1.0)).inFrameById("fid").build();
		// formatter: on

		ScreenArea[] excludes = arg.getTargets().get(0).getExcludes();
		assertThat(excludes.length, is(3));
		assertThat(excludes[0], is(ScreenArea.of(SelectorType.ID, "id", SelectorType.ID, "fid")));
		assertThat(excludes[1], is(ScreenArea.of(SelectorType.TAG_NAME, "body", SelectorType.ID, "fid")));
		assertThat(excludes[2], is(ScreenArea.of(0.0, 0.0, 1.0, 1.0)));
	}

	/**
	 * hidden + frame
	 */
	@Test
	public void inFrame_hidden() throws Exception {
		for (Pair<String, SelectorType> mapping : TYPE_MAPPINGS) {
			ScreenshotArgumentBuilder builder = new ScreenshotArgumentBuilder("ssid").addNewTargetByXPath("xpath")
					.addHiddenElementsById("id");

			Method method = ScreenshotArgumentBuilder.class.getMethod("inFrameBy" + mapping.getKey(), String.class);
			method.invoke(builder, "value");

			ScreenshotArgument arg = builder.build();

			List<DomSelector> hidden = arg.getHiddenElementSelectors();
			assertThat(hidden.size(), is(1));
			assertThat(hidden.get(0), is(new DomSelector(SelectorType.ID, "id", new DomSelector(mapping.getRight(),
					"value"))));
		}
	}

	/**
	 * hidden (collection) + frame
	 */
	@Test
	public void inFrame_hiddens() throws Exception {
		// formatter: off
		ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid")
				.addNewTarget()
				.addHiddenElementSelectors(new DomSelector(SelectorType.ID, "id"),
						new DomSelector(SelectorType.TAG_NAME, "body"), new DomSelector(SelectorType.XPATH, "xx"))
				.inFrameById("fid").build();
		// formatter: on

		List<DomSelector> hiddens = arg.getHiddenElementSelectors();
		assertThat(hiddens.size(), is(3));
		assertThat(hiddens.get(0), is(new DomSelector(SelectorType.ID, "id", new DomSelector(SelectorType.ID, "fid"))));
		assertThat(hiddens.get(1), is(new DomSelector(SelectorType.TAG_NAME, "body", new DomSelector(SelectorType.ID,
				"fid"))));
		assertThat(hiddens.get(2),
				is(new DomSelector(SelectorType.XPATH, "xx", new DomSelector(SelectorType.ID, "fid"))));
	}

	/**
	 * NewTargetの後にinFrameを呼ぶとエラー
	 */
	@Test
	public void inFrame_afterNewTarget() throws Exception {
		expected.expect(IllegalStateException.class);
		new ScreenshotArgumentBuilder("ssid").addNewTarget().inFrameById("id");
	}

	/**
	 * MoveTargetの後にinFrameを呼ぶとエラー
	 */
	@Test
	public void inFrame_afterMoveTarget() throws Exception {
		expected.expect(IllegalStateException.class);
		new ScreenshotArgumentBuilder("ssid").addNewTarget().moveTarget(true).inFrameById("id");
	}

	/**
	 * ScrollTargetの後にinFrameを呼ぶとエラー
	 */
	@Test
	public void inFrame_afterScrollTarget() throws Exception {
		expected.expect(IllegalStateException.class);
		new ScreenshotArgumentBuilder("ssid").addNewTarget().scrollTarget(true).inFrameById("id");
	}

	/**
	 * ScreenshotIdの後にinFrameを呼ぶとエラー
	 */
	@Test
	public void inFrame_afterScreenshotId() throws Exception {
		expected.expect(IllegalStateException.class);
		new ScreenshotArgumentBuilder("ssid").addNewTarget().screenshotId("new_id").inFrameById("id");
	}

	//</editor-fold>

}