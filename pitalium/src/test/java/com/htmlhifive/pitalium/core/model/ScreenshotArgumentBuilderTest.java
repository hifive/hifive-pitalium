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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

@SuppressWarnings("all")
public class ScreenshotArgumentBuilderTest {

	private static final List<Pair<String, SelectorType>> TYPE_MAPPINGS = Arrays.asList(Pair.of("Id", SelectorType.ID),
			Pair.of("ClassName", SelectorType.CLASS_NAME), Pair.of("CssSelector", SelectorType.CSS_SELECTOR),
			Pair.of("LinkText", SelectorType.LINK_TEXT), Pair.of("Name", SelectorType.NAME),
			Pair.of("PartialLinkText", SelectorType.PARTIAL_LINK), Pair.of("TagName", SelectorType.TAG_NAME),
			Pair.of("XPath", SelectorType.XPATH));

	@Rule
	public ExpectedException expected = ExpectedException.none();

	/**
	 * デフォルトコンストラクタのままbuildするとエラー
	 */
	@Test
	public void constructor() throws Exception {
		ScreenshotArgumentBuilder builder = new ScreenshotArgumentBuilder();

		expected.expect(IllegalStateException.class);
		expected.expect(containsString("screenshotId"));

		builder.build();
	}

	/**
	 * コンストラクタでSSIDにnullを指定してbuildするとエラー
	 */
	@Test
	public void constructor_null() throws Exception {
		ScreenshotArgumentBuilder builder = new ScreenshotArgumentBuilder(null);

		expected.expect(IllegalStateException.class);
		expected.expect(containsString("screenshotId"));

		builder.build();
	}

	/**
	 * コンストラクタでSSIDに空文字を指定してbuildするとエラー
	 */
	@Test
	public void constructor_empty() throws Exception {
		ScreenshotArgumentBuilder builder = new ScreenshotArgumentBuilder("");

		expected.expect(IllegalStateException.class);
		expected.expect(containsString("screenshotId"));

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
		expected.expect(containsString("screenshotId"));

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
		expected.expect(containsString("screenshotId"));

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
	 * {@link ScreenshotArgumentBuilder#addExclude(SelectorType, String)}
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
                .addExcludeByName("name")
                .build();
// @formatter:on

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().size(), is(3));
		assertThat(arg.getHiddenElementSelectors().isEmpty(), is(true));

		assertThat(arg.getTargets().get(0), is(new CompareTarget(ScreenArea.of(SelectorType.ID, "id"))));
		assertThat(arg.getTargets().get(1), is(new CompareTarget(ScreenArea.of(SelectorType.CLASS_NAME, "class"))));
		assertThat(arg.getTargets().get(1), is(new CompareTarget(ScreenArea.of(SelectorType.NAME, "name"))));
	}

	/**
	 * addExcludeをaddNewTargetより前にコールすると例外
	 */
	@Test
	public void addExclude_beforeCall_addNewTarget() throws Exception {
		ScreenshotArgumentBuilder builder = new ScreenshotArgumentBuilder("ssid");

		expected.expect(IllegalStateException.class);
		expected.expect(containsString("addExclude should be called after addNewTarget"));

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

		assertThat(arg.getTargets().get(0),
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
                    .addNewTargetByXPath("ex_xpath")
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
		assertThat(arg.getTargets().get(2),
				is(new CompareTarget(ScreenArea.of(SelectorType.TAG_NAME, "tag"),
						new ScreenArea[] { ScreenArea.of(SelectorType.CLASS_NAME, "ex_class"),
								ScreenArea.of(SelectorType.LINK_TEXT, "ex_link") },
						true)));
	}

	/**
	 * moveTargetをaddNewTargetより前にコールすると例外
	 */
	@Test
	public void moveTarget_beforeCall_addNewTarget() throws Exception {
		ScreenshotArgumentBuilder builder = new ScreenshotArgumentBuilder("ssid");

		expected.expect(IllegalStateException.class);
		expected.expect(containsString("moveTarget should be called after addNewTarget"));

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
		assertThat(arg.getTargets().get(0), is(new CompareTarget(ScreenArea.of(1d, 2d, 3d, 4d), null, true)));
		assertThat(arg.getTargets().get(0),
				is(new CompareTarget(ScreenArea.of(SelectorType.TAG_NAME, "body"), null, false)));
	}

	/**
	 * addHiddenElementSelectorを追加
	 */
	@Test
	public void addHiddenElementSelector() throws Exception {
		ScreenshotArgument arg = new ScreenshotArgumentBuilder("ssid").addHiddenElementSelector(SelectorType.ID, "id")
				.build();

		assertThat(arg.getScreenshotId(), is("ssid"));
		assertThat(arg.getTargets().isEmpty(), is(true));
		assertThat(arg.getHiddenElementSelectors().size(), is(1));

		assertThat(arg.getHiddenElementSelectors().get(0), is(new DomSelector(SelectorType.ID, "id")));
	}

	/**
	 * AddHiddenElementsByXxx...
	 */
	@Test
	public void addHiddenElementsByXxx() throws Exception {
		for (Pair<String, SelectorType> mapping : TYPE_MAPPINGS) {
			ScreenshotArgumentBuilder builder = new ScreenshotArgumentBuilder("ssid");

			ScreenshotArgumentBuilder.class.getMethod("addHiddenElementsBy" + mapping.getKey(), String.class)
					.invoke(builder, "value");
			ScreenshotArgument arg = builder.build();

			assertThat(arg.getScreenshotId(), is("ssid"));
			assertThat(arg.getTargets().isEmpty(), is(true));
			assertThat(arg.getHiddenElementSelectors().size(), is(1));

			assertThat(arg.getHiddenElementSelectors().get(0), is(new DomSelector(mapping.getValue(), "value")));
		}
	}

}