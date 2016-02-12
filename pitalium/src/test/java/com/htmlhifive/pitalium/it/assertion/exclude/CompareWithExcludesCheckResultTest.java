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
package com.htmlhifive.pitalium.it.assertion.exclude;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.config.ExecMode;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.result.TestResultManager;
import com.htmlhifive.pitalium.core.selenium.PtlCapabilities;
import com.htmlhifive.pitalium.it.util.ItUtils;

/**
 * ページ全体(body)のスクリーンショットの取得のテストの結果を確認するテストクラス
 */
public class CompareWithExcludesCheckResultTest extends PtlTestBase {

	private static final String TEST_CLASS_NAME = "CompareWithExcludesTest";

	private static String expectedId = null;

	private static JsonNode results = null;

	private static String currentId = null;

	private static final ObjectMapper mapper = new ObjectMapper();

	private static String resultFolderPath;

	/** jsonから座標情報の取得 */
	private JsonNode getCoordinateInfo(String methodName) throws JsonProcessingException, IOException {
		return mapper.readTree(new File(getFileName(methodName) + ".json"));
	}

	/** 結果JSONからこのメソッドのスクリーンショットの結果を取得 */
	private JsonNode getCurrentScreenshotResultJson(String methodName) {
		for (JsonNode jn : results.get("screenshotResults")) {
			if (methodName.equals(jn.get("testMethod").asText())
					&& capabilities.getBrowserName().equals(jn.get("capabilities").get("browserName").asText())) {
				return jn;
			}
		}
		return null;
	}

	/** 座標情報のassert */
	private void assertCoordinateInfo(JsonNode bodyJson, String selectorType, boolean withMargin) {
		JsonNode targetNode = bodyJson.get("target");
		JsonNode selectorNode = targetNode.get("selector");
		assertThat(selectorNode.get("type").asText(), is(selectorType));
		assertThat(selectorNode.get("value").asText(), is("body"));
		assertThat(selectorNode.get("index").asInt(), is(0));
		JsonNode rectangleNode = targetNode.get("rectangle");
		// TODO: モバイルはscaleのためにマージンが変わるが、テスト側で検知できないので目視確認
		if (!"iOS".equals(capabilities.getPlatformName()) && !"ANDROID".equals(capabilities.getPlatformName())) {
			int margin = withMargin ? 100 : 0;
			assertThat(rectangleNode.get("x").asInt(), is(margin));
			assertThat(rectangleNode.get("y").asInt(), is(margin));
		}
		JsonNode screenAreaConditionNode = targetNode.get("screenArea").get("selector");
		assertThat(screenAreaConditionNode.get("type").asText(), is(selectorType));
		assertThat(screenAreaConditionNode.get("value").asText(), is("body"));

		JsonNode exclude = bodyJson.get("excludes").get(0);
		JsonNode excludeSelector = exclude.get("selector");
		assertThat(excludeSelector.get("type").asText(), is("CLASS_NAME"));
		assertThat(excludeSelector.get("value").asText(), is("fb-like-box"));
		assertThat(excludeSelector.get("index").asText(), is("null"));

		JsonNode excludeScreenAreaSelector = exclude.get("screenArea").get("selector");
		assertThat(excludeScreenAreaSelector.get("type").asText(), is("CLASS_NAME"));
		assertThat(excludeScreenAreaSelector.get("value").asText(), is("fb-like-box"));
	}

	/**
	 * スクリーンショットの結果のassert
	 * 
	 * @param selectorType
	 * @param withMargin
	 */
	private void assertScreenshotResult(JsonNode screenshotResult, String selectorType, boolean withMargin)
			throws JsonProcessingException {
		assertThat(screenshotResult.get("screenshotId").asText(), is("topPage"));
		if (PtlTestConfig.getInstance().getEnvironment().getExecMode() == ExecMode.RUN_TEST) {
			assertThat(screenshotResult.get("result").asText(), is("SUCCESS"));
		} else {
			assertNull(screenshotResult.get("result"));
		}
		assertThat(screenshotResult.get("expectedId").asText(), is(expectedId));
		assertThat(screenshotResult.get("testClass").asText(), is(TEST_CLASS_NAME));

		// targetResult
		JsonNode targetResult = screenshotResult.get("targetResults").get(0);
		assertCoordinateInfo(targetResult, selectorType, withMargin);
		assertThat(targetResult.get("result").asText(), is("SUCCESS"));

		// capabilities
		assertThat(new PtlCapabilities(mapper.convertValue(screenshotResult.get("capabilities"), Map.class)),
				is(capabilities));
	}

	private static String readExpectedId() throws IOException {
		File file = new File(PtlTestConfig.getInstance().getPersisterConfig().getFile().getResultDirectory()
				+ File.separator + TEST_CLASS_NAME + ".json");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String str = br.readLine();
		br.close();
		return str;
	}

	@BeforeClass
	public static void beforeClass() throws JsonProcessingException, IOException {
		expectedId = readExpectedId();
		currentId = TestResultManager.getInstance().getCurrentId();
		resultFolderPath = PtlTestConfig.getInstance().getPersisterConfig().getFile().getResultDirectory()
				+ File.separator + currentId + File.separator + TEST_CLASS_NAME;
		results = mapper.readTree(new File(resultFolderPath + File.separator + "result.json"));
	}

	/**
	 * bodyをtargetにexcludeを指定するテスト.<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4, 5.1/iOS 8.1, 8.3<br>
	 * 期待結果：テストに成功する。jsonファイルにexcludeの情報が出力される
	 */
	@Test
	public void excludeForBody() throws JsonProcessingException, IOException {
		assertResult("excludeForBody");
	}

	/**
	 * bodyにmarginがある場合にexcludeを指定するテスト<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4, 5.1/iOS 8.1, 8.3<br>
	 * 期待結果：テストに成功する
	 */
	@Test
	public void excludeForBodyWithMargin() throws JsonProcessingException, IOException {
		assertResult("excludeForBodyWithMargin");
	}

	/**
	 * 十分な高さにしてスクロールが出ない状態でexcludeを指定するテスト<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4, 5.1/iOS 8.1, 8.3<br>
	 * 期待結果：テストに成功する
	 */
	@Test
	public void excludeForBodyWithoutScroll() throws JsonProcessingException, IOException {
		assertResult("excludeForBodyWithoutScroll");
	}

	private void assertResult(String methodName) throws JsonProcessingException, IOException {
		assertResult(methodName, "TAG_NAME");
	}

	private void assertResult(String methodName, String selectorType) throws JsonProcessingException, IOException {
		String fileName = getFileName(methodName) + ".png";
		assertTrue(fileName + "が存在しません", new File(fileName).exists());

		String fileNameOfSelector = getFileName(methodName, selectorType) + ".png";
		assertTrue(fileNameOfSelector + "が存在しません", new File(fileNameOfSelector).exists());

		JsonNode bodyJson = getCoordinateInfo(methodName).get(0);
		assertCoordinateInfo(bodyJson, selectorType, "excludeForBodyWithMargin".equals(methodName));

		JsonNode screenshotResultJson = ItUtils.getCurrentScreenshotResultJson(methodName, results, capabilities);
		assertScreenshotResult(screenshotResultJson, selectorType, "excludeForBodyWithMargin".equals(methodName));
	}

	private String getFileName(String methodName) {
		List<String> strs = new ArrayList<String>();
		strs.add(resultFolderPath + File.separator + methodName);
		strs.add("topPage");
		if (capabilities.getPlatform() != null) {
			strs.add(capabilities.getPlatform().name());
		} else {
			strs.add(capabilities.getPlatformName());
		}
		if (!StringUtils.isEmpty(capabilities.getPlatformVersion())) {
			strs.add(capabilities.getPlatformVersion());
		}
		strs.add(capabilities.getBrowserName());
		if (!StringUtils.isEmpty(capabilities.getVersion())) {
			strs.add(capabilities.getVersion());
		}

		return StringUtils.join(strs, "_");
	}

	private String getFileName(String methodName, String selectorType) {
		return getFileName(methodName) + "_" + selectorType + "_body_[0]";
	}
}
