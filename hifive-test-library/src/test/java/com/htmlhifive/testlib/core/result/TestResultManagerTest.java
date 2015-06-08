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
package com.htmlhifive.testlib.core.result;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.htmlhifive.testlib.common.exception.TestRuntimeException;
import com.htmlhifive.testlib.common.util.JSONUtils;
import com.htmlhifive.testlib.core.config.EnvironmentConfig;
import com.htmlhifive.testlib.core.config.ExecMode;
import com.htmlhifive.testlib.core.io.PersistMetadata;
import com.htmlhifive.testlib.core.io.Persister;
import com.htmlhifive.testlib.core.io.ResourceUnavailableException;
import com.htmlhifive.testlib.core.model.ExecResult;
import com.htmlhifive.testlib.core.model.ScreenshotResult;
import com.htmlhifive.testlib.core.model.TargetResult;
import com.htmlhifive.testlib.core.model.TestResult;

public class TestResultManagerTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private EnvironmentConfig createEnvironmentConfigWithPersister(Class clss) {
		return createEnvironmentConfigWithPersister(clss.getName());
	}

	private EnvironmentConfig createEnvironmentConfigWithPersister(String className) {
		String json = String.format("{\"persister\": \"%s\"}", className);
		return JSONUtils.readValue(new ByteArrayInputStream(json.getBytes()), EnvironmentConfig.class);
	}

	private EnvironmentConfig createEnvironmentConfigWithExecModeAndPersister(ExecMode mode, Class persister) {
		String json = String.format("{\"execMode\": \"%s\", \"persister\": \"%s\"}", mode.name(), persister.getName());
		return JSONUtils.readValue(new ByteArrayInputStream(json.getBytes()), EnvironmentConfig.class);
	}

	//<editor-fold desc="Initialize persister">

	/**
	 * 初期化テスト。Persisterに指定したクラスが存在しない。
	 */
	@Test
	public void testConstruct_persisterNotFound() throws Exception {
		EnvironmentConfig env = createEnvironmentConfigWithPersister("hoge");

		expectedException.expect(TestRuntimeException.class);
		expectedException.expectMessage(containsString("cannot be instantiated"));
		new TestResultManager(env);
	}

	/**
	 * 初期化テスト。Persisterに指定したクラスがPersisterではない。
	 */
	@Test
	public void testConstruct_persister_classCastError() throws Exception {
		EnvironmentConfig env = createEnvironmentConfigWithPersister(getClass());

		expectedException.expect(TestRuntimeException.class);
		expectedException.expectMessage(containsString("cannot be instantiated"));
		new TestResultManager(env);
	}

	//</editor-fold>

	//<editor-fold desc="getExpectedId">

	/**
	 * 初期化テスト。ExpectedIdsが見つからない場合。
	 */
	@Test
	public void testExpectedIds_notFound() throws Exception {
		EnvironmentConfig env = createEnvironmentConfigWithPersister(EmptyPersisterImpl.class);
		TestResultManager manager = new TestResultManager(env);

		assertThat(manager.getPersister(), is(instanceOf(EmptyPersisterImpl.class)));
		assertThat(manager.getExpectedIds().size(), is(0));
	}

	/**
	 * ExpectedId取得テスト。対応するクラス名が存在しない場合TestRuntimeException。
	 */
	@Test
	public void testGetExpectedId_classNotFound() throws Exception {
		EnvironmentConfig env = createEnvironmentConfigWithPersister(ExpectedPersisterImpl.class);
		TestResultManager manager = new TestResultManager(env);

		expectedException.expect(TestRuntimeException.class);
		for (Map.Entry<String, Map<String, String>> classEntry : manager.getExpectedIds().entrySet()) {
			for (Map.Entry<String, String> methodEntry : classEntry.getValue().entrySet()) {
				manager.getExpectedId("hoge", methodEntry.getKey());
				fail();
			}
		}
	}

	/**
	 * ExpectedId取得テスト。対応するメソッド名が存在しない場合TestRuntimeException。
	 */
	@Test
	public void testGetExpectedId_methodNotFound() throws Exception {
		EnvironmentConfig env = createEnvironmentConfigWithPersister(ExpectedPersisterImpl.class);
		TestResultManager manager = new TestResultManager(env);

		expectedException.expect(TestRuntimeException.class);
		for (Map.Entry<String, Map<String, String>> classEntry : manager.getExpectedIds().entrySet()) {
			manager.getExpectedId(classEntry.getKey(), "hoge");
			fail();
		}
	}

	/**
	 * ExpectedId取得テスト。
	 */
	@Test
	public void testGetExpectedId() throws Exception {
		EnvironmentConfig env = createEnvironmentConfigWithPersister(ExpectedPersisterImpl.class);
		TestResultManager manager = new TestResultManager(env);

		for (Map.Entry<String, Map<String, String>> classEntry : manager.getExpectedIds().entrySet()) {
			for (Map.Entry<String, String> methodEntry : classEntry.getValue().entrySet()) {
				String expectedId = manager.getExpectedId(classEntry.getKey(), methodEntry.getKey());
				assertThat(expectedId, is(classEntry.getKey() + "_" + methodEntry.getKey()));
			}
		}
	}

	//</editor-fold>

	//<editor-fold desc="results add/export">

	/**
	 * スクリーンショット撮影結果を追加するテスト。初期化前に追加するとTestRuntimeException。
	 */
	@Test
	public void testAddScreenshotResult_beforeInitialize() throws Exception {
		EnvironmentConfig env = createEnvironmentConfigWithPersister(TestResultPersisterImpl.class);
		TestResultManager manager = new TestResultManager(env);

		expectedException.expect(TestRuntimeException.class);
		manager.addScreenshotResult("testClass", new ScreenshotResult());
	}

	/**
	 * スクリーンショット撮影結果を追加するテスト。初期化を複数回行うとTestRuntimeException。
	 */
	@Test
	public void testInitializeTestResult_multipleTimes() throws Exception {
		EnvironmentConfig env = createEnvironmentConfigWithPersister(TestResultPersisterImpl.class);
		TestResultManager manager = new TestResultManager(env);

		String className = "testClass";
		manager.initializeTestResult(className);

		expectedException.expect(TestRuntimeException.class);
		manager.initializeTestResult(className);
	}

	/**
	 * スクリーンショット撮影結果を追加するテスト。比較結果無しの場合。
	 */
	@Test
	public void testAddScreenshotResult_noResult() throws Exception {
		EnvironmentConfig env = createEnvironmentConfigWithPersister(TestResultPersisterImpl.class);
		TestResultManager manager = new TestResultManager(env);

		String className = "testClass";
		manager.initializeTestResult(className);
		manager.addScreenshotResult(className, new ScreenshotResult("scId", new ArrayList<TargetResult>(), null));
		manager.exportTestResult(className);

		TestResult testResult = ((TestResultPersisterImpl) manager.getPersister()).getResult();
		assertThat(testResult.getResult(), is(nullValue()));

		PersistMetadata metadata = ((TestResultPersisterImpl) manager.getPersister()).getMetadata();
		assertThat(metadata, is(new PersistMetadata(manager.getCurrentId(), className)));
	}

	/**
	 * スクリーンショット撮影結果を追加するテスト。比較結果が成功の場合。
	 */
	@Test
	public void testAddScreenshotResult_success() throws Exception {
		EnvironmentConfig env = createEnvironmentConfigWithPersister(TestResultPersisterImpl.class);
		TestResultManager manager = new TestResultManager(env);

		String className = "testClass";
		manager.initializeTestResult(className);
		manager.addScreenshotResult(className, new ScreenshotResult("scId_0", ExecResult.SUCCESS, null,
				new ArrayList<TargetResult>(), null, null, null, null));
		manager.addScreenshotResult(className, new ScreenshotResult("scId_1", ExecResult.SUCCESS, null,
				new ArrayList<TargetResult>(), null, null, null, null));
		manager.exportTestResult(className);

		TestResult testResult = ((TestResultPersisterImpl) manager.getPersister()).getResult();
		assertThat(testResult.getResult(), is(ExecResult.SUCCESS));
	}

	/**
	 * スクリーンショット撮影結果を追加するテスト。比較結果に失敗が含まれる場合。
	 */
	@Test
	public void testAddScreenshotResult_failure() throws Exception {
		EnvironmentConfig env = createEnvironmentConfigWithPersister(TestResultPersisterImpl.class);
		TestResultManager manager = new TestResultManager(env);

		String className = "testClass";
		manager.initializeTestResult(className);
		manager.addScreenshotResult(className, new ScreenshotResult("scId_0", ExecResult.SUCCESS, null,
				new ArrayList<TargetResult>(), null, null, null, null));
		manager.addScreenshotResult(className, new ScreenshotResult("scId_1", ExecResult.FAILURE, null,
				new ArrayList<TargetResult>(), null, null, null, null));
		manager.exportTestResult(className);

		TestResult testResult = ((TestResultPersisterImpl) manager.getPersister()).getResult();
		assertThat(testResult.getResult(), is(ExecResult.FAILURE));
	}

	//</editor-fold>

	//<editor-fold desc="expectedId update/export/cancelUpdate">

	/**
	 * ExpectedIdの更新テスト。既存メソッドの上書き。
	 */
	@Test
	public void testUpdateExpectedId_SET_EXPECTED_overrideMethod() throws Exception {
		EnvironmentConfig env = createEnvironmentConfigWithPersister(ExpectedPersisterImpl.class);
		assertThat(env.getExecMode(), is(ExecMode.SET_EXPECTED));

		String className = "testClass_0";
		String methodName = "testMethod_0";
		TestResultManager manager = new TestResultManager(env);
		assertThat(manager.getExpectedId(className, methodName), is(not(manager.getCurrentId())));

		manager.updateExpectedId(className, methodName);
		manager.exportExpectedIds(className);

		Map<String, Map<String, String>> expectedIds = ((ExpectedPersisterImpl) manager.getPersister())
				.getExpectedIds();
		String newId = expectedIds.get(className).get(methodName);
		assertThat(newId, is(manager.getCurrentId()));
	}

	/**
	 * ExpectedIdの更新テスト。新規メソッド追加。
	 */
	@Test
	public void testUpdateExpectedId_SET_EXPECTED_addMethod() throws Exception {
		EnvironmentConfig env = createEnvironmentConfigWithPersister(ExpectedPersisterImpl.class);
		assertThat(env.getExecMode(), is(ExecMode.SET_EXPECTED));

		String className = "testClass_0";
		String methodName = "testMethod_1";
		TestResultManager manager = new TestResultManager(env);
		try {
			manager.getExpectedId(className, methodName);
			fail();
		} catch (TestRuntimeException e) {
			assertTrue(true);
		}

		manager.updateExpectedId(className, methodName);
		manager.exportExpectedIds(className);

		Map<String, Map<String, String>> expectedIds = ((ExpectedPersisterImpl) manager.getPersister())
				.getExpectedIds();
		String newId = expectedIds.get(className).get(methodName);
		assertThat(newId, is(manager.getCurrentId()));
	}

	/**
	 * ExpectedIdの更新テスト。新規クラス追加。
	 */
	@Test
	public void testUpdateExpectedId_SET_EXPECTED_addClass() throws Exception {
		EnvironmentConfig env = createEnvironmentConfigWithPersister(ExpectedPersisterImpl.class);
		assertThat(env.getExecMode(), is(ExecMode.SET_EXPECTED));

		String className = "testClass_2";
		String methodName = "testMethod_2";
		TestResultManager manager = new TestResultManager(env);
		try {
			manager.getExpectedId(className, methodName);
			fail();
		} catch (TestRuntimeException e) {
			assertTrue(true);
		}

		manager.updateExpectedId(className, methodName);
		manager.exportExpectedIds(className);

		Map<String, Map<String, String>> expectedIds = ((ExpectedPersisterImpl) manager.getPersister())
				.getExpectedIds();
		String newId = expectedIds.get(className).get(methodName);
		assertThat(newId, is(manager.getCurrentId()));
	}

	/**
	 * ExpectedIdの更新テスト。キャンセルしたテストクラスは更新されない。
	 */
	@Test
	public void testCancelUpdateExpectedId_SET_EXPECTED() throws Exception {
		EnvironmentConfig env = createEnvironmentConfigWithPersister(ExpectedPersisterImpl.class);
		assertThat(env.getExecMode(), is(ExecMode.SET_EXPECTED));

		TestResultManager manager = new TestResultManager(env);
		manager.updateExpectedId("testClass_0", "testMethod_1");
		manager.updateExpectedId("testClass_2", "testMethod_2");

		// Update of testClass_2 is not cancelled
		manager.exportExpectedIds("testClass_2");
		Map<String, Map<String, String>> expectedIds = ((ExpectedPersisterImpl) manager.getPersister())
				.getExpectedIds();
		assertThat(expectedIds.get("testClass_2").get("testMethod_2"), is(manager.getCurrentId()));

		// Update of testClass_0 is cancelled
		manager.cancelUpdateExpectedId("testClass_0");
		manager.exportExpectedIds("testClass_0");
		Map<String, Map<String, String>> cancelledExpectedIds = ((ExpectedPersisterImpl) manager.getPersister())
				.getExpectedIds();
		assertThat(cancelledExpectedIds.get("testClass_0").containsKey("testMethod_1"), is(false));
		assertThat(expectedIds.get("testClass_2").get("testMethod_2"), is(manager.getCurrentId()));
	}

	/**
	 * ExpectedIdの更新テスト。TAKE_SCREENSHOTモードでは更新されない。
	 */
	@Test
	public void testUpdateExpectedId_TAKE_SCREENSHOT() throws Exception {
		EnvironmentConfig env = createEnvironmentConfigWithExecModeAndPersister(ExecMode.TAKE_SCREENSHOT,
				ExpectedPersisterImpl.class);

		String className = "testClass_0";
		String methodName = "testMethod_1";
		TestResultManager manager = new TestResultManager(env);
		manager.updateExpectedId(className, methodName);
		manager.exportExpectedIds(className);

		Map<String, Map<String, String>> expectedIds = ((ExpectedPersisterImpl) manager.getPersister())
				.getExpectedIds();
		assertThat(expectedIds, is(nullValue()));
	}

	/**
	 * ExpectedIdの更新テスト。RUN_TESTモードでは更新されない。
	 */
	@Test
	public void testUpdateExpectedId_RUN_TEST() throws Exception {
		EnvironmentConfig env = createEnvironmentConfigWithExecModeAndPersister(ExecMode.RUN_TEST,
				ExpectedPersisterImpl.class);

		String className = "testClass_0";
		String methodName = "testMethod_1";
		TestResultManager manager = new TestResultManager(env);
		manager.updateExpectedId(className, methodName);
		manager.exportExpectedIds(className);

		Map<String, Map<String, String>> expectedIds = ((ExpectedPersisterImpl) manager.getPersister())
				.getExpectedIds();
		assertThat(expectedIds, is(nullValue()));
	}

	//</editor-fold>

	//<editor-fold desc="PersisterImpl">

	public static class EmptyPersisterImpl implements Persister {

		@Override
		public void saveDiffImage(PersistMetadata metadata, BufferedImage image) {
		}

		@Override
		public BufferedImage loadDiffImage(PersistMetadata metadata) throws ResourceUnavailableException {
			throw new ResourceUnavailableException();
		}

		@Override
		public void saveScreenshot(PersistMetadata metadata, BufferedImage image) {
		}

		@Override
		public InputStream getImageStream(PersistMetadata metadata) throws ResourceUnavailableException {
			throw new ResourceUnavailableException();
		}

		@Override
		public BufferedImage loadScreenshot(PersistMetadata metadata) throws ResourceUnavailableException {
			throw new ResourceUnavailableException();
		}

		@Override
		public void saveTargetResults(PersistMetadata metadata, List<TargetResult> results) {
		}

		@Override
		public List<TargetResult> loadTargetResults(PersistMetadata metadata) throws ResourceUnavailableException {
			throw new ResourceUnavailableException();
		}

		@Override
		public void saveTestResult(PersistMetadata metadata, TestResult result) {
		}

		@Override
		public TestResult loadTestResult(PersistMetadata metadata) throws ResourceUnavailableException {
			throw new ResourceUnavailableException();
		}

		@Override
		public void saveExpectedIds(Map<String, Map<String, String>> expectedIds) {
		}

		@Override
		public Map<String, Map<String, String>> loadExpectedIds() throws ResourceUnavailableException {
			throw new ResourceUnavailableException();
		}
	}

	public static class ExpectedPersisterImpl extends EmptyPersisterImpl {
		@Override
		public Map<String, Map<String, String>> loadExpectedIds() throws ResourceUnavailableException {
			Map<String, Map<String, String>> ids = new HashMap<String, Map<String, String>>();
			ids.put("testClass_0", new HashMap<String, String>() {
				{
					put("testMethod_0", "testClass_0_testMethod_0");
				}
			});
			ids.put("testClass_1", new HashMap<String, String>() {
				{
					put("testMethod_1", "testClass_1_testMethod_1");
				}
			});

			return ids;
		}

		private Map<String, Map<String, String>> expectedIds;

		@Override
		public void saveExpectedIds(Map<String, Map<String, String>> expectedIds) {
			this.expectedIds = expectedIds;
		}

		public Map<String, Map<String, String>> getExpectedIds() {
			return expectedIds;
		}
	}

	public static class TestResultPersisterImpl extends EmptyPersisterImpl {
		private PersistMetadata metadata;
		private TestResult result;

		@Override
		public void saveTestResult(PersistMetadata metadata, TestResult result) {
			this.metadata = metadata;
			this.result = result;
		}

		public PersistMetadata getMetadata() {
			return metadata;
		}

		public TestResult getResult() {
			return result;
		}
	}

	//</editor-fold>

}