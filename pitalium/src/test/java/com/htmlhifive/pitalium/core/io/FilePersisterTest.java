/*
 * Copyright (C) 2015-2016 NS Solutions Corporation
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
package com.htmlhifive.pitalium.core.io;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
import com.htmlhifive.pitalium.common.util.JSONUtils;
import com.htmlhifive.pitalium.core.config.FilePersisterConfig;
import com.htmlhifive.pitalium.core.model.IndexDomSelector;
import com.htmlhifive.pitalium.core.model.ScreenshotResult;
import com.htmlhifive.pitalium.core.model.SelectorType;
import com.htmlhifive.pitalium.core.model.TargetResult;
import com.htmlhifive.pitalium.core.model.TestResult;
import com.htmlhifive.pitalium.core.selenium.PtlCapabilities;
import com.htmlhifive.pitalium.image.model.PersistedScreenshotImage;
import com.htmlhifive.pitalium.image.model.RectangleArea;
import com.htmlhifive.pitalium.image.model.ScreenshotImage;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FilePersisterTest {

	private static final String BASE_DIRECTORY = "file_persister_test";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private Persister persister;

	/**
	 * Persisterを初期化します。
	 */
	@Before
	public void initializePersister() throws Exception {
		InputStream in = null;
		FilePersisterConfig config;
		try {
			in = getClass().getResourceAsStream("FilePersister_Config.json");
			config = JSONUtils.readValue(in, FilePersisterConfig.class);
		} finally {
			if (in != null) {
				in.close();
			}
		}
		persister = new FilePersister(config);
	}

	/**
	 * 保存先ディレクトリを削除します。
	 */
	@Before
	@After
	public void deleteDirectory() throws Exception {
		FileUtils.deleteDirectory(new File(BASE_DIRECTORY));
	}

	//<editor-fold desc="illegalConfig">

	/**
	 * 設定ファイルNG 全てNULL
	 */
	@Test
	public void testConstruct_illegalConfig_allProps_null() throws Exception {
		FilePersisterConfig config = FilePersisterConfig.builder().diffFileName(null).resultDirectory(null)
				.screenshotFileName(null).targetResultFileName(null).build();

		expectedException.expect(TestRuntimeException.class);
		new FilePersister(config);
	}

	/**
	 * 設定ファイルNG diff画像のファイル名がNULL
	 */
	@Test
	public void testConstruct_illegalConfig_diffFileName_null() throws Exception {
		FilePersisterConfig config = FilePersisterConfig.builder().diffFileName(null).build();

		expectedException.expect(TestRuntimeException.class);
		new FilePersister(config);
	}

	/**
	 * 設定ファイルNG 結果格納ディレクトリ名がNULL
	 */
	@Test
	public void testConstruct_illegalConfig_resultDirectory_null() throws Exception {
		FilePersisterConfig config = FilePersisterConfig.builder().resultDirectory(null).build();

		expectedException.expect(TestRuntimeException.class);
		new FilePersister(config);
	}

	/**
	 * 設定ファイルNG スクリーンショットのファイル名がNULL
	 */
	@Test
	public void testConstruct_illegalConfig_screenshotFileName_null() throws Exception {
		FilePersisterConfig config = FilePersisterConfig.builder().screenshotFileName(null).build();

		expectedException.expect(TestRuntimeException.class);
		new FilePersister(config);
	}

	/**
	 * 設定ファイルNG targetResults.jsonのフォーマットがNULL
	 */
	@Test
	public void testConstruct_illegalConfig_targetResultFileName_null() throws Exception {
		FilePersisterConfig config = FilePersisterConfig.builder().targetResultFileName(null).build();

		expectedException.expect(TestRuntimeException.class);
		new FilePersister(config);
	}

	/**
	 * 設定ファイルNG スクリーンショットファイル名とtargetResults.jsonの名前が同じ
	 */
	@Test
	public void testConstruct_illegalConfig_screenshotAndTargetResultIsSame() throws Exception {
		FilePersisterConfig config = FilePersisterConfig.builder().screenshotFileName("ssFileName")
				.targetResultFileName("ssFileName").build();

		expectedException.expect(TestRuntimeException.class);
		expectedException
				.expectMessage("TargetResultFileName, ScreenshotFileName and DiffFileName must be different value.");
		new FilePersister(config);
	}

	/**
	 * 設定ファイルNG スクリーンショットファイル名と差分画像ファイルの名前が同じ
	 */
	@Test
	public void testConstruct_illegalConfig_screenshotAndDiffIsSame() throws Exception {
		FilePersisterConfig config = FilePersisterConfig.builder().screenshotFileName("ssFileName")
				.diffFileName("ssFileName").build();

		expectedException.expect(TestRuntimeException.class);
		expectedException
				.expectMessage("TargetResultFileName, ScreenshotFileName and DiffFileName must be different value.");
		new FilePersister(config);
	}

	//</editor-fold>

	//<editor-fold desc="ExpectedIds">

	/**
	 * ExpectedIdsの書き込みテスト
	 */
	@Test
	public void testSaveExpectedIds() throws Exception {
		Map<String, Map<String, String>> expectedIds = JSONUtils.readValue(
				getClass().getResourceAsStream("FilePersister_ExpectedIds.json"),
				new TypeReference<Map<String, Map<String, String>>>() {
				});
		persister.saveExpectedIds(expectedIds);

		File file = new File(BASE_DIRECTORY, "currentExpectedIds.json");
		assertThat(file.exists(), is(true));

		Map<String, Map<String, String>> savedIds = JSONUtils.readValue(file,
				new TypeReference<Map<String, Map<String, String>>>() {
				});
		assertThat(savedIds, is(expectedIds));
	}

	/**
	 * ExpectedIdsの読み込みテスト
	 */
	@Test
	public void testLoadExpectedIds() throws Exception {
		Map<String, Map<String, String>> expectedIds = JSONUtils.readValue(
				getClass().getResourceAsStream("FilePersister_ExpectedIds.json"),
				new TypeReference<Map<String, Map<String, String>>>() {
				});
		File file = new File(BASE_DIRECTORY, "currentExpectedIds.json");
		file.getParentFile().mkdirs();
		JSONUtils.writeValue(file, expectedIds);

		Map<String, Map<String, String>> loadedIds = persister.loadExpectedIds();
		assertThat(loadedIds, is(expectedIds));
	}

	/**
	 * ExpectedIdsが読み込めないエラー
	 */
	@Test
	public void testLoadExpectedIds_not_found() throws Exception {
		expectedException.expect(ResourceUnavailableException.class);
		persister.loadExpectedIds();
	}

	//</editor-fold>

	//<editor-fold desc="TestResult">

	/**
	 * result.jsonの出力テスト
	 */
	@Test
	public void testSaveTestResult() throws Exception {
		TestResult expected = JSONUtils.readValue(getClass().getResourceAsStream("FilePersister_TestResult.json"),
				TestResult.class);
		PersistMetadata metadata = new PersistMetadata("test1", "testClass");
		persister.saveTestResult(metadata, expected);

		File file = new File(BASE_DIRECTORY + "/test1/testClass/result.json");
		TestResult result = JSONUtils.readValue(file, TestResult.class);
		assertThat(result, is(expected));
	}

	/**
	 * result.jsonの読み込みテスト
	 */
	@Test
	public void testLoadTestResult() throws Exception {
		File file = new File(BASE_DIRECTORY + "/test1/testClass/result.json");
		file.getParentFile().mkdirs();
		FileUtils.copyURLToFile(getClass().getResource("FilePersister_TestResult.json"), file);

		PersistMetadata metadata = new PersistMetadata("test1", "testClass");
		TestResult actual = persister.loadTestResult(metadata);

		TestResult expected = JSONUtils.readValue(getClass().getResourceAsStream("FilePersister_TestResult.json"),
				TestResult.class);
		Field imageField = TargetResult.class.getDeclaredField("image");
		imageField.setAccessible(true);
		for (ScreenshotResult sr : expected.getScreenshotResults()) {
			ScreenshotImage image = new PersistedScreenshotImage(persister, new PersistMetadata(expected.getResultId(),
					sr.getTestClass(), sr.getTestMethod(), sr.getScreenshotId(), new IndexDomSelector(
							SelectorType.TAG_NAME, "body", 0), null, new PtlCapabilities(sr.getCapabilities())));
			imageField.set(sr.getTargetResults().get(0), image);
		}

		assertThat(actual, is(expected));
	}

	/**
	 * result.jsonが読み込めないエラー
	 */
	@Test
	public void testLoadTestResult_not_found() throws Exception {
		expectedException.expect(ResourceUnavailableException.class);

		PersistMetadata metadata = new PersistMetadata("test1", "testClass");
		persister.loadTestResult(metadata);
	}

	//</editor-fold>

	//<editor-fold desc="TargetResults">

	/**
	 * targets.jsonの出力テスト
	 */
	@Test
	public void testSaveTargetResults() throws Exception {
		List<TargetResult> expected = JSONUtils.readValue(
				getClass().getResourceAsStream("FilePersister_TargetResults.json"),
				new TypeReference<List<TargetResult>>() {
				});

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("platform", "WINDOWS");
		map.put("browserName", "firefox");
		map.put("version", "38");

		PtlCapabilities capabilities = new PtlCapabilities(map);
		PersistMetadata metadata = new PersistMetadata("testId", "testClass", "testMethod", "scId", capabilities);
		persister.saveTargetResults(metadata, expected);

		File file = new File(BASE_DIRECTORY + "/testId/testClass/testMethod_scId_WINDOWS_firefox_38.json");
		List<TargetResult> result = JSONUtils.readValue(file, new TypeReference<List<TargetResult>>() {
		});
		assertThat(result, is(expected));
	}

	/**
	 * targets.jsonの読み込みテスト
	 */
	@Test
	public void testLoadTargetResults() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("platform", "WINDOWS");
		map.put("browserName", "firefox");
		map.put("version", "38");

		final PtlCapabilities capabilities = new PtlCapabilities(map);
		final PersistMetadata metadata = new PersistMetadata("testId", "testClass", "testMethod", "scId", capabilities);

		List<TargetResult> jsonValue = JSONUtils.readValue(
				getClass().getResourceAsStream("FilePersister_TargetResults.json"),
				new TypeReference<List<TargetResult>>() {
				});
		List<TargetResult> expected = new ArrayList<TargetResult>(Lists.transform(jsonValue,
				new Function<TargetResult, TargetResult>() {
					@Override
					public TargetResult apply(TargetResult r) {
						// MetadataはTargetResultとScreenshotImageで異なることに注意
						// JSONから読み込んだTargetResultはScreenshotImageが設定されていないので自分で設定する
						return new TargetResult(r.getResult(), r.getTarget(), r.getExcludes(), r.isMoveTarget(), r
								.getHiddenElementSelectors(), new PersistedScreenshotImage(persister,
								new PersistMetadata("testId", "testClass", "testMethod", "scId", new IndexDomSelector(
										SelectorType.TAG_NAME, "body", 0), null, capabilities)), r.getOptions());
					}
				}));

		File file = new File(BASE_DIRECTORY + "/testId/testClass/testMethod_scId_WINDOWS_firefox_38.json");
		file.getParentFile().mkdirs();
		JSONUtils.writeValue(file, expected);

		List<TargetResult> results = persister.loadTargetResults(metadata);
		assertThat(results, is(expected));
	}

	/**
	 * targets.json読み込みエラー
	 */
	@Test
	public void testLoadTargetResults_not_found() throws Exception {
		expectedException.expect(ResourceUnavailableException.class);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("platform", "WINDOWS");
		map.put("browserName", "firefox");
		map.put("version", "38");

		PtlCapabilities capabilities = new PtlCapabilities(map);
		PersistMetadata metadata = new PersistMetadata("testId", "testClass", "testMethod", "scId", capabilities);
		persister.loadTargetResults(metadata);
	}

	//</editor-fold>

	//<editor-fold desc="Screenshot">

	/**
	 * スクリーンショット保存テスト（セレクタ）
	 */
	@Test
	public void testSaveScreenshot_selector() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("platform", "WINDOWS");
		map.put("browserName", "firefox");
		map.put("version", "38");

		PtlCapabilities capabilities = new PtlCapabilities(map);
		IndexDomSelector selector = new IndexDomSelector(SelectorType.CSS_SELECTOR, "main", 0);
		PersistMetadata metadata = new PersistMetadata("testId", "testClass", "testMethod", "scId", selector, null,
				capabilities);
		BufferedImage expected = ImageIO.read(getClass().getResource("FilePersister_Image.png"));
		persister.saveScreenshot(metadata, expected);

		File file = new File(BASE_DIRECTORY
				+ "/testId/testClass/testMethod_scId_WINDOWS_firefox_38_CSS_SELECTOR_main_[0].png");
		BufferedImage actual = ImageIO.read(file);

		assertArrayEquals(toRGB(expected), toRGB(actual));
	}

	/**
	 * スクリーンショット保存テスト（矩形）
	 */
	@Test
	public void testSaveScreenshot_rectangle() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("platform", "WINDOWS");
		map.put("browserName", "firefox");
		map.put("version", "38");

		PtlCapabilities capabilities = new PtlCapabilities(map);
		RectangleArea rectangle = new RectangleArea(0d, 10d, 100d, 110d);
		PersistMetadata metadata = new PersistMetadata("testId", "testClass", "testMethod", "scId", null, rectangle,
				capabilities);
		BufferedImage expected = ImageIO.read(getClass().getResource("FilePersister_Image.png"));
		persister.saveScreenshot(metadata, expected);

		File file = new File(BASE_DIRECTORY
				+ "/testId/testClass/testMethod_scId_WINDOWS_firefox_38_rect_0_10_100_110.png");
		BufferedImage actual = ImageIO.read(file);

		assertArrayEquals(toRGB(expected), toRGB(actual));
	}

	/**
	 * スクリーンショットの読み込みテスト（セレクタ）
	 */
	@Test
	public void testLoadScreenshot_selector() throws Exception {
		File file = new File(BASE_DIRECTORY
				+ "/testId/testClass/testMethod_scId_WINDOWS_firefox_38_CSS_SELECTOR_main_[0].png");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("platform", "WINDOWS");
		map.put("browserName", "firefox");
		map.put("version", "38");

		PtlCapabilities capabilities = new PtlCapabilities(map);
		IndexDomSelector selector = new IndexDomSelector(SelectorType.CSS_SELECTOR, "main", 0);
		PersistMetadata metadata = new PersistMetadata("testId", "testClass", "testMethod", "scId", selector, null,
				capabilities);

		FileUtils.copyURLToFile(getClass().getResource("FilePersister_Image.png"), file);

		BufferedImage expected = ImageIO.read(getClass().getResource("FilePersister_Image.png"));
		BufferedImage actual = persister.loadScreenshot(metadata);

		assertArrayEquals(toRGB(expected), toRGB(actual));
	}

	/**
	 * スクリーンショットの読み込みテスト（矩形）
	 */
	@Test
	public void testLoadScreenshot_rectangle() throws Exception {
		File file = new File(BASE_DIRECTORY
				+ "/testId/testClass/testMethod_scId_WINDOWS_firefox_38_rect_0_10_100_110.png");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("platform", "WINDOWS");
		map.put("browserName", "firefox");
		map.put("version", "38");

		PtlCapabilities capabilities = new PtlCapabilities(map);
		RectangleArea rectangle = new RectangleArea(0d, 10d, 100d, 110d);
		PersistMetadata metadata = new PersistMetadata("testId", "testClass", "testMethod", "scId", null, rectangle,
				capabilities);

		FileUtils.copyURLToFile(getClass().getResource("FilePersister_Image.png"), file);

		BufferedImage expected = ImageIO.read(getClass().getResource("FilePersister_Image.png"));
		BufferedImage actual = persister.loadScreenshot(metadata);

		assertArrayEquals(toRGB(expected), toRGB(actual));
	}

	/**
	 * スクリーンショット保存時のエスケープ処理のテスト
	 */
	@Test
	public void testSaveScreenshot_escape() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("platform", "WINDOWS");
		map.put("browserName", "firefox");
		map.put("version", "38");

		PtlCapabilities capabilities = new PtlCapabilities(map);
		IndexDomSelector selector = new IndexDomSelector(SelectorType.CSS_SELECTOR, "1\\\\2/3:4*5?6\"7<8>9|0", 0);
		PersistMetadata metadata = new PersistMetadata("testId", "testClass", "testMethod", "scId", selector, null,
				capabilities);
		BufferedImage expected = ImageIO.read(getClass().getResource("FilePersister_Image.png"));
		persister.saveScreenshot(metadata, expected);

		File file = new File(BASE_DIRECTORY
				+ "/testId/testClass/testMethod_scId_WINDOWS_firefox_38_CSS_SELECTOR_1-2-3-4-5-6-7-8-9-0_[0].png");
		BufferedImage actual = ImageIO.read(file);

		assertArrayEquals(toRGB(expected), toRGB(actual));
	}

	static int[] toRGB(BufferedImage image) {
		return image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
	}

	//</editor-fold>

}