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
package com.htmlhifive.testlib.it.assertion.execlude;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.htmlhifive.testlib.core.MrtTestBase;
import com.htmlhifive.testlib.core.config.ExecMode;
import com.htmlhifive.testlib.core.config.MrtTestConfig;
import com.htmlhifive.testlib.core.result.TestResultManager;
import com.htmlhifive.testlib.core.selenium.MrtCapabilities;
import com.htmlhifive.testlib.core.selenium.MrtWebElement;

/**
 * ページ全体(body)のスクリーンショットの取得のテストの結果を確認するテストクラス
 */
public class CompareEntirePageCheckResultTest extends MrtTestBase {

	private static final String TEST_CLASS_NAME = "CompareEntirePageTest";

	private static final String EXPECTED_ID = "2015_05_31_21_29_19";

	private static JsonNode results = null;

	private static String currentId = null;

	private static final ObjectMapper mapper = new ObjectMapper();

	private static String resultFolderPath;

	/** jsonから座標情報の取得 */
	private JsonNode getCoordinateInfo(String methodName) throws JsonProcessingException, IOException {
		String jsonFileName = methodName + "_topPage_WINDOWS_" + capabilities.getBrowserName();
		return mapper.readTree(new File(resultFolderPath + File.separator + jsonFileName + ".json"));
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
	private void assertCoordinateInfo(JsonNode bodyJson, String selectorType) {
		JsonNode targetNode = bodyJson.get("target");
		JsonNode selectorNode = targetNode.get("selector");
		assertThat(selectorNode.get("type").asText(), is(selectorType));
		assertThat(selectorNode.get("value").asText(), is("body"));
		assertThat(selectorNode.get("index").asInt(), is(0));
		JsonNode rectangleNode = targetNode.get("rectangle");
		assertThat(rectangleNode.get("x").asInt(), is(0));
		assertThat(rectangleNode.get("y").asInt(), is(0));
		//		assertThat(rectangleNode.get("width").asInt(), is(getBodyWidth()));
		//		assertThat(rectangleNode.get("height").asInt(), is(getBodyHeight()));
		JsonNode screenAreaConditionNode = targetNode.get("screenArea").get("selector");
		assertThat(screenAreaConditionNode.get("type").asText(), is(selectorType));
		assertThat(screenAreaConditionNode.get("value").asText(), is("body"));

		assertThat(bodyJson.get("excludes").size(), is(0));
	}

	private static final int EXPECTED_WINDOW_SIZE = 1280;

	private int getBodyWidth() {
		return (int) ((MrtWebElement) driver.findElement(By.tagName("body"))).getRect().getHeight();
		//		int chromeSize = 16;
		//		if ("chrome".equals(capabilities.getBrowserName())) {
		//			chromeSize = 10;
		//		}
		//		return 1280 - chromeSize;
	}

	private int getBodyHeight() {
		return (int) ((MrtWebElement) driver.findElement(By.tagName("body"))).getRect().getHeight();
	}

	/**
	 * スクリーンショットの結果のassert
	 *
	 * @param selectorType
	 */
	private void assertScreenshotResult(JsonNode screenshotResult, String selectorType) throws JsonProcessingException {
		assertThat(screenshotResult.get("screenshotId").asText(), is("topPage"));
		if (MrtTestConfig.getInstance().getEnvironment().getExecMode() == ExecMode.RUN_TEST) {
			assertThat(screenshotResult.get("result").asText(), is("SUCCESS"));
		} else {
			assertNull(screenshotResult.get("result"));
		}
		assertThat(screenshotResult.get("expectedId").asText(), is(EXPECTED_ID));
		assertThat(screenshotResult.get("testClass").asText(), is(TEST_CLASS_NAME));

		// targetResult
		JsonNode targetResult = screenshotResult.get("targetResults").get(0);
		assertCoordinateInfo(targetResult, selectorType);
		assertThat(targetResult.get("result").asText(), is("SUCCESS"));

		// capabilities
		assertThat(new MrtCapabilities(mapper.convertValue(screenshotResult.get("capabilities"), Map.class)),
				is(capabilities));
	}

	@BeforeClass
	public static void beforeClass() throws JsonProcessingException, IOException {
		currentId = TestResultManager.getInstance().getCurrentId();
		currentId = "2015_05_31_21_36_57";
		resultFolderPath = "results" + File.separator + currentId + File.separator + TEST_CLASS_NAME;
		results = mapper.readTree(new File(resultFolderPath + File.separator + "result.json"));
	}

	/**
	 * targetを指定せずにassertViewを実行するテスト.<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：実行したタイムスタンプのフォルダ内にspecifyNoTarget_topPage_WINDOWS_(browser name).pngが生成される<br>
	 * 　　　　　　　仕様通りのresult.jsonと座標用のjsonファイルが生成される<br>
	 * 　　　　　　　RUN_TESTモードの場合、assertViewの比較で一致し、compareResultがtrueとなる
	 */
	@Test
	public void specifyNoTarget() throws JsonProcessingException, IOException {
		assertResult("specifyNoTarget");
	}

	/**
	 * bodyタグを指定してassertViewを実行するテスト.<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：実行したタイムスタンプのフォルダ内にspecifyTargetTagBody_topPage_WINDOWS_(browser name).pngが生成される<br>
	 * 　　　　　　　仕様通りのresult.jsonと座標用のjsonファイルが生成される<br>
	 * 　　　　　　　RUN_TESTモードの場合、assertViewの比較で一致し、compareResultがtrueとなる
	 */
	@Test
	public void specifyTargetTagBody() throws JsonProcessingException, IOException {
		assertResult("specifyTargetTagBody");
	}

	/**
	 * クラスを指定してbodyのassertViewを実行するテスト.<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：実行したタイムスタンプのフォルダ内にspecifyTargetBodyClass_topPage_WINDOWS_(browser name).pngが生成される<br>
	 * 　　　　　　　仕様通りのresult.jsonと座標用のjsonファイルが生成される<br>
	 * 　　　　　　　RUN_TESTモードの場合、assertViewの比較で一致し、compareResultがtrueとなる
	 */
	@Test
	public void specifyTargetClassBody() throws JsonProcessingException, IOException {
		assertResult("specifyTargetClassBody", "CLASS_NAME");
	}

	/**
	 * bodyにmarginがある場合にassertViewを実行するテスト.<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：実行したタイムスタンプのフォルダ内にspecifyTargetBodyWithMargin_topPage_WINDOWS_(browser name).pngが生成される<br>
	 * 　　　　　　　仕様通りのresult.jsonと座標用のjsonファイルが生成される<br>
	 * 　　　　　　　RUN_TESTモードの場合、assertViewの比較で一致し、compareResultがtrueとなる
	 */
	@Test
	public void specifyTargetBodyWithMargin() throws JsonProcessingException, IOException {
		assertResult("specifyTargetBodyWithMargin");
	}

	/**
	 * 十分な高さにしてスクロールが出ない状態でbodyのassertViewを実行するテスト.<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：実行したタイムスタンプのフォルダ内にspecifyTargetBodyWithoutScroll_topPage_WINDOWS_(browser name).pngが生成される<br>
	 * 　　　　　　　仕様通りのresult.jsonと座標用のjsonファイルが生成される<br>
	 * 　　　　　　　RUN_TESTモードの場合、assertViewの比較で一致し、compareResultがtrueとなる
	 */
	@Test
	public void specifyTargetBodyWithoutScroll() throws JsonProcessingException, IOException {
		assertResult("specifyTargetBodyWithoutScroll");
	}

	private void assertResult(String methodName) throws JsonProcessingException, IOException {
		assertResult(methodName, "TAG_NAME");
	}

	private void assertResult(String methodName, String selectorType) throws JsonProcessingException, IOException {
		assertTrue(new File(resultFolderPath + File.separator + methodName + "_topPage_WINDOWS_"
				+ capabilities.getBrowserName() + ".png").exists());

		JsonNode bodyJson = getCoordinateInfo(methodName).get(0);
		assertCoordinateInfo(bodyJson, selectorType);

		JsonNode screenshotResultJson = getCurrentScreenshotResultJson(methodName);
		assertScreenshotResult(screenshotResultJson, selectorType);
	}
}
