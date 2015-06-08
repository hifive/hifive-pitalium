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
package com.htmlhifive.testlib.core.io;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.htmlhifive.testlib.core.model.TargetResult;
import com.htmlhifive.testlib.core.model.TestResult;

/**
 * データ永続化方法を提供するインターフェース
 */
public interface Persister {

	/**
	 * 差分画像を保存します。
	 * 
	 * @param metadata データのメタデータ
	 * @param image 差分画像
	 */
	void saveDiffImage(PersistMetadata metadata, BufferedImage image);

	/**
	 * 差分画像を読み込みます。
	 * 
	 * @param metadata データのメタデータ
	 * @return 差分画像
	 * @throws ResourceUnavailableException 対応するリソースが見つからない場合
	 */
	BufferedImage loadDiffImage(PersistMetadata metadata) throws ResourceUnavailableException;

	/**
	 * スクリーンショットの画像を保存します。
	 * 
	 * @param metadata データのメタデータ
	 * @param image スクリーンショットの画像
	 */
	void saveScreenshot(PersistMetadata metadata, BufferedImage image);

	/**
	 * スクリーンショットの画像のストリームを取得します。
	 * 
	 * @param metadata データのメタデータ
	 * @return スクリーンショット画像のストリーム
	 * @throws ResourceUnavailableException 対応するリソースが見つからない場合
	 */
	InputStream getImageStream(PersistMetadata metadata) throws ResourceUnavailableException;

	/**
	 * スクリーンショットの画像を読み込みます。
	 * 
	 * @param metadata データのメタデータ
	 * @return スクリーンショットの画像
	 * @throws ResourceUnavailableException 対応するリソースが見つからない場合
	 */
	BufferedImage loadScreenshot(PersistMetadata metadata) throws ResourceUnavailableException;

	/**
	 * スクリーンショット撮影結果を保存します。
	 * 
	 * @param metadata データのメタデータ
	 * @param results スクリーンショット撮影結果
	 */
	void saveTargetResults(PersistMetadata metadata, List<TargetResult> results);

	/**
	 * スクリーンショット撮影結果を読み込みます。
	 * 
	 * @param metadata データのメタデータ
	 * @return スクリーンショット撮影結果
	 * @throws ResourceUnavailableException 対応するリソースが見つからない場合
	 */
	List<TargetResult> loadTargetResults(PersistMetadata metadata) throws ResourceUnavailableException;

	/**
	 * テスト結果を保存します。
	 * 
	 * @param metadata データのメタデータ
	 * @param result テスト結果
	 */
	void saveTestResult(PersistMetadata metadata, TestResult result);

	/**
	 * テスト結果を読み込みます。
	 * 
	 * @param metadata データのメタデータ
	 * @return テスト結果
	 * @throws ResourceUnavailableException 対応するリソースが見つからない場合
	 */
	TestResult loadTestResult(PersistMetadata metadata) throws ResourceUnavailableException;

	/**
	 * 正解のテスト実行ID一覧を保存します。
	 * 
	 * @param expectedIds 正解のテスト実行ID一覧
	 */
	void saveExpectedIds(Map<String, Map<String, String>> expectedIds);

	/**
	 * 正解のテスト実行ID一覧を読み込みます。
	 * 
	 * @return 正解のテスト実行ID一覧
	 * @throws ResourceUnavailableException 対応するリソースが見つからない場合
	 */
	Map<String, Map<String, String>> loadExpectedIds() throws ResourceUnavailableException;
}
