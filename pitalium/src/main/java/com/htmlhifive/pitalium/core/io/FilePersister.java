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
package com.htmlhifive.pitalium.core.io;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import com.htmlhifive.pitalium.common.exception.JSONException;
import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
import com.htmlhifive.pitalium.common.util.JSONUtils;
import com.htmlhifive.pitalium.core.config.FilePersisterConfig;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.model.PersistedScreenshotImage;
import com.htmlhifive.pitalium.core.model.ScreenshotResult;
import com.htmlhifive.pitalium.core.model.TargetResult;
import com.htmlhifive.pitalium.core.model.TestResult;
import com.htmlhifive.pitalium.core.selenium.PtlCapabilities;

/**
 * データをファイルとして永続化するためのPersister
 */
public class FilePersister implements Persister {
	/**
	 * ExpectedIdを保存するデフォルトのファイル名
	 */
	public static final String DEFAULT_EXPECTED_IDS_FILE_NAME = "currentExpectedIds.json";
	/**
	 * テストクラス毎のテスト結果を保存するデフォルトのファイル名
	 */
	public static final String DEFAULT_TEST_RESULT_FILE_NAME = "result.json";

	private static final Logger LOG = LoggerFactory.getLogger(FilePersister.class);
	private static final TypeReference<List<TargetResult>> TARGET_RESULTS_REFERENCE = new TypeReference<List<TargetResult>>() {
	};
	private static final TypeReference<Map<String, Map<String, String>>> EXPECTED_IDS_REFERENCE = new TypeReference<Map<String, Map<String, String>>>() {
	};

	protected FilePersisterConfig config;
	private final FileNameFormatter targetResultFileNameFormatter;
	private final FileNameFormatter screenshotFileNameFormatter;
	private final FileNameFormatter diffFileNameFormatter;

	/**
	 * コンストラクタ
	 */
	public FilePersister() {
		this(PtlTestConfig.getInstance().getPersisterConfig().getFile());
	}

	/**
	 * コンストラクタ
	 *
	 * @param config データ永続化の設定
	 */
	public FilePersister(FilePersisterConfig config) {
		this.config = config;
		validateConfig();

		targetResultFileNameFormatter = new FileNameFormatter(config.getTargetResultFileName());
		screenshotFileNameFormatter = new FileNameFormatter(config.getScreenshotFileName());
		diffFileNameFormatter = new FileNameFormatter(config.getDiffFileName());
	}

	/**
	 * 設定ファイルの値をチェックします。
	 */
	private void validateConfig() {
		if (Strings.isNullOrEmpty(config.getResultDirectory())) {
			throw new TestRuntimeException("ResultDirectory cannot be empty.");
		}
		if (Strings.isNullOrEmpty(config.getTargetResultFileName())) {
			throw new TestRuntimeException("TargetResultFileName cannot be empty.");
		}
		if (Strings.isNullOrEmpty(config.getScreenshotFileName())) {
			throw new TestRuntimeException("ScreenshotFileName cannot be empty.");
		}
		if (Strings.isNullOrEmpty(config.getDiffFileName())) {
			throw new TestRuntimeException("DiffFileName cannot be empty.");
		}

		if (config.getScreenshotFileName().equals(config.getTargetResultFileName())
				|| config.getScreenshotFileName().equals(config.getDiffFileName())) {
			throw new TestRuntimeException(
					"TargetResultFileName, ScreenshotFileName and DiffFileName must be different value.");
		}
	}

	@Override
	public void saveDiffImage(PersistMetadata metadata, BufferedImage image) {
		File file = checkParentFileAvailable(getDiffImageFile(metadata));
		LOG.debug("[Save diff image] ({})", file);
		LOG.trace("[Save diff image] ({})]", metadata);
		try {
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			LOG.debug("Failed to save diff image.", e);
			throw new TestRuntimeException(e);
		}
	}

	@Override
	public BufferedImage loadDiffImage(PersistMetadata metadata) {
		File file = checkFileAvailable(getDiffImageFile(metadata));
		LOG.debug("[Load diff image] ({})", file);
		LOG.trace("[Load diff image] ({})", metadata);
		try {
			return ImageIO.read(file);
		} catch (IOException e) {
			LOG.debug("Failed to load diff image.", e);
			throw new ResourceUnavailableException(e);
		}
	}

	@Override
	public void saveScreenshot(PersistMetadata metadata, BufferedImage image) {
		File file = checkParentFileAvailable(getScreenshotImageFile(metadata));
		LOG.debug("[Save screenshot] ({})", file);
		LOG.trace("[Save screenshot] ({})", metadata);
		try {
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			LOG.debug("Failed to save screenshot.", e);
			throw new TestRuntimeException(e);
		}
	}

	@Override
	public InputStream getImageStream(PersistMetadata metadata) {
		File file = checkFileAvailable(getScreenshotImageFile(metadata));
		LOG.debug("[Load image stream] ({})", file);
		LOG.trace("[Load image stream] ({})", metadata);
		try {
			return new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			LOG.debug("Failed to load image stream.", e);
			throw new ResourceUnavailableException(String.format(Locale.US, "File %s not found", file));
		}
	}

	@Override
	public BufferedImage loadScreenshot(PersistMetadata metadata) {
		File file = checkFileAvailable(getScreenshotImageFile(metadata));
		LOG.debug("[Load screenshot] ({})", file);
		LOG.trace("[Load screenshot] ({})", metadata);
		try {
			return ImageIO.read(file);
		} catch (IOException e) {
			LOG.debug("Failed to load screenshot.", e);
			throw new ResourceUnavailableException(e);
		}
	}

	@Override
	public void saveTargetResults(PersistMetadata metadata, List<TargetResult> results) {
		File file = checkParentFileAvailable(getTargetResultsFile(metadata));
		LOG.debug("[Save TargetResults] ({})", file);
		LOG.trace("[Save TargetResults] (results: {}; meta: {})", results, metadata);
		try {
			JSONUtils.writeValueWithIndent(file, results);
		} catch (JSONException e) {
			LOG.debug("Failed to save TargetResults.", e);
			throw e;
		}
	}

	@Override
	public List<TargetResult> loadTargetResults(final PersistMetadata metadata) {
		File file = checkFileAvailable(getTargetResultsFile(metadata));
		LOG.debug("[Load TargetResults] ({})", file);
		LOG.trace("[Load TargetResults] ({})", metadata);
		try {
			List<TargetResult> targetResults = JSONUtils.readValue(file, TARGET_RESULTS_REFERENCE);
			LOG.trace("[Load TargetResults] => {}", targetResults);

			// Build with screenshot image
			return Collections.unmodifiableList(fillScreenshotImageProperty(targetResults, metadata));
		} catch (JSONException e) {
			LOG.debug("Failed to load TargetResults.", e);
			throw e;
		} catch (Exception e) {
			LOG.debug("Failed to fill TargetResults object.", e);
			throw e;
		}
	}

	/**
	 * {@list TargetResult}のリストに指定されたメタデータを追加します。
	 *
	 * @param targetResults 対象の{@list TargetResult}
	 * @param metadata 追加するメタデータ
	 * @return メタデータを追加済の{@list TargetResult}のリスト
	 */
	private List<TargetResult> fillScreenshotImageProperty(List<TargetResult> targetResults, PersistMetadata metadata) {
		List<TargetResult> results = new ArrayList<TargetResult>(targetResults.size());
		for (TargetResult targetResult : targetResults) {
			PersistMetadata imageMetadata;
			if (targetResult.getTarget().getSelector() == null) {
				imageMetadata = new PersistMetadata(metadata.getExpectedId(), metadata.getClassName(),
						metadata.getMethodName(), metadata.getScreenshotId(), null,
						targetResult.getTarget().getRectangle(), metadata.getCapabilities());
			} else {
				imageMetadata = new PersistMetadata(metadata.getExpectedId(), metadata.getClassName(),
						metadata.getMethodName(), metadata.getScreenshotId(), targetResult.getTarget().getSelector(),
						null, metadata.getCapabilities());
			}
			results.add(new TargetResult(targetResult.getResult(), targetResult.getTarget(), targetResult.getExcludes(),
					targetResult.isMoveTarget(), targetResult.getHiddenElementSelectors(),
					new PersistedScreenshotImage(this, imageMetadata), targetResult.getOptions()));
		}

		return results;
	}

	@Override
	public void saveTestResult(PersistMetadata metadata, TestResult result) {
		File file = checkParentFileAvailable(getTestResultFile(metadata));
		LOG.debug("[Save TestResult] ({})", file);
		LOG.trace("[Save TestResult] (result: {}; meta: {})", result, metadata);
		try {
			JSONUtils.writeValueWithIndent(file, result);
		} catch (JSONException e) {
			LOG.debug("Failed to save TestResult.", e);
			throw e;
		}
	}

	@Override
	public TestResult loadTestResult(PersistMetadata metadata) {
		File file = checkFileAvailable(getTestResultFile(metadata));
		LOG.debug("[Load TestResult] ({})", file);
		LOG.trace("[Load TestResult] ({})", metadata);
		try {
			TestResult testResult = JSONUtils.readValue(file, TestResult.class);
			LOG.trace("[Load TestResult] => {}", testResult);

			// Build with screenshot image
			List<ScreenshotResult> results = new ArrayList<ScreenshotResult>(testResult.getScreenshotResults().size());
			for (ScreenshotResult r : testResult.getScreenshotResults()) {
				PersistMetadata m = new PersistMetadata(metadata.getExpectedId(), metadata.getClassName(),
						r.getTestMethod(), r.getScreenshotId(), new PtlCapabilities(r.getCapabilities()));
				List<TargetResult> targetResults = fillScreenshotImageProperty(r.getTargetResults(), m);
				results.add(new ScreenshotResult(r.getScreenshotId(), r.getResult(), r.getExpectedId(), targetResults,
						r.getTestClass(), r.getTestMethod(), r.getCapabilities(), null));
			}

			return new TestResult(testResult.getResultId(), testResult.getResult(), results);
		} catch (JSONException e) {
			LOG.debug("Failed to load TestResult.", e);
			throw e;
		} catch (Exception e) {
			LOG.debug("Failed to fill TestResult object.", e);
			throw e;
		}
	}

	@Override
	public void saveExpectedIds(Map<String, Map<String, String>> expectedIds) {
		File file = checkParentFileAvailable(getExpectedIdsFile());
		LOG.debug("[Save ExpectedIds] ({})", file);
		LOG.trace("[Save ExpectedIds] ({})", expectedIds);
		try {
			JSONUtils.writeValueWithIndent(file, expectedIds);
		} catch (JSONException e) {
			LOG.debug("Failed to save ExpectedIds.", e);
			throw e;
		}
	}

	@Override
	public Map<String, Map<String, String>> loadExpectedIds() {
		File file = checkFileAvailable(getExpectedIdsFile());
		LOG.debug("[Load ExpectedIds] ({})", file);
		try {
			Map<String, Map<String, String>> ids = JSONUtils.readValue(file, EXPECTED_IDS_REFERENCE);
			LOG.trace("[Load ExpectedIds] => {}", ids);
			return ids;
		} catch (JSONException e) {
			LOG.debug("Failed to load ExpectedIds.", e);
			throw e;
		}
	}

	/**
	 * 指定したファイルの親フォルダが存在し、アクセス可能か否かをチェックします。<br>
	 * 存在しない場合はフォルダを作成します。
	 *
	 * @param file 対象のファイル
	 * @return 対象のファイル
	 */
	private File checkParentFileAvailable(File file) {
		File parent = file.getParentFile();
		if (!parent.exists() && !parent.mkdirs()) {
			throw new TestRuntimeException(String.format(Locale.US, "mkdir error \"%s\"", parent));
		}
		if (!parent.canWrite()) {
			throw new TestRuntimeException(String.format(Locale.US, "No write permission at \"%s\"", parent));
		}

		return file;
	}

	/**
	 * 指定したファイルが存在し、アクセス可能か否かをチェックします。
	 *
	 * @param file 対象のファイル
	 * @return 対象のファイル
	 */
	private File checkFileAvailable(File file) {
		if (!file.exists()) {
			throw new ResourceUnavailableException(String.format(Locale.UK, "File \"%s\" not found", file));
		}
		if (!file.canRead()) {
			throw new ResourceUnavailableException(String.format(Locale.UK, "File \"%s\" cannot read", file));
		}

		return file;
	}

	/**
	 * 結果を保存する基底ディレクトリを取得します。
	 *
	 * @return 結果を保存する基底ディレクトリのパス
	 */
	public File getResultDirectoryFile() {
		return new File(config.getResultDirectory());
	}

	/**
	 * 期待結果IDが記録されたファイルパスを取得します。
	 *
	 * @return 期待結果IDファイルのパス
	 */
	public File getExpectedIdsFile() {
		return new File(config.getResultDirectory(), getExpectedIdsFileName());
	}

	/**
	 * テストクラス実行結果のファイルパスを取得します。
	 *
	 * @param metadata メタデータ
	 * @return テストクラス実行結果ファイルのパス
	 */
	public File getTestResultFile(PersistMetadata metadata) {
		return new File(getFilePath(metadata, getTestResultFileName()));
	}

	/**
	 * スクリーンショットの比較結果が記録されたファイルパスを取得します。
	 *
	 * @param metadata メタデータ
	 * @return スクリーンショット比較結果ファイルのパス
	 */
	public File getTargetResultsFile(PersistMetadata metadata) {
		return new File(getFilePath(metadata, getTargetResultsFileName(metadata)));
	}

	/**
	 * スクリーンショットの画像ファイルのパスを取得します。
	 *
	 * @param metadata メタデータ
	 * @return スクリーンショット画像ファイルのパス
	 */
	public File getScreenshotImageFile(PersistMetadata metadata) {
		return new File(getFilePath(metadata, getScreenshotImageFileName(metadata)));
	}

	/**
	 * 差分画像のファイルパスを取得します。
	 *
	 * @param metadata メタデータ
	 * @return 差分画像のファイルパス
	 */
	public File getDiffImageFile(PersistMetadata metadata) {
		return new File(getFilePath(metadata, getDiffImageFileName(metadata)));
	}

	/**
	 * 期待結果IDが記録されたファイル名を取得します。
	 *
	 * @return 期待結果ファイルのファイル名
	 */
	public String getExpectedIdsFileName() {
		return DEFAULT_EXPECTED_IDS_FILE_NAME;
	}

	/**
	 * テストクラスの実行結果が記録されたファイル名を取得します。
	 *
	 * @return テストクラスの実行結果ファイル名
	 */
	public String getTestResultFileName() {
		return DEFAULT_TEST_RESULT_FILE_NAME;
	}

	/**
	 * スクリーンショットの比較結果が記録されたファイル名を取得します。
	 *
	 * @param metadata メタデータ
	 * @return スクリーンショットの比較結果ファイル名
	 */
	public String getTargetResultsFileName(PersistMetadata metadata) {
		return targetResultFileNameFormatter.format(metadata);
	}

	/**
	 * スクリーンショットの画像ファイル名を取得します。
	 *
	 * @param metadata メタデータ
	 * @return スクリーンショット画像のファイル名
	 */
	public String getScreenshotImageFileName(PersistMetadata metadata) {
		return screenshotFileNameFormatter.format(metadata);
	}

	/**
	 * 差分画像のファイル名を取得します。
	 *
	 * @param metadata メタデータ
	 * @return 差分画像のファイル名
	 */
	public String getDiffImageFileName(PersistMetadata metadata) {
		return diffFileNameFormatter.format(metadata);
	}

	/**
	 * メタデータとファイル名から実際のファイルパスを取得します。
	 *
	 * @param metadata メタデータ
	 * @param fileName ファイル名
	 * @return 実際のファイルパス
	 */
	protected String getFilePath(PersistMetadata metadata, String fileName) {
		return config.getResultDirectory() + File.separator + metadata.getExpectedId() + File.separator + File.separator
				+ metadata.getClassName() + File.separator + fileName;
	}

}
