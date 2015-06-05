/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.config;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.htmlhifive.testlib.common.util.JSONUtils;

/**
 * 画像やテスト結果の情報をファイルから入出力する際の設定
 */
@MrtConfiguration
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilePersisterConfig implements Serializable {

	/**
	 * デフォルトのスクリーンショット撮影結果ファイル名フォーマット
	 * 
	 * @see #targetResultFileName
	 */
	public static final String DEFAULT_TARGET_RESULT_FILE_NAME = "{platformName}_{platformVersion}_{browserName}_{version}.json";
	/**
	 * デフォルトのファイル名フォーマット
	 * 
	 * @see #screenshotFileName
	 */
	public static final String DEFAULT_SCREENSHOT_FILE_NAME = "{platformName}_{platformVersion}_{browserName}_{version}_{screenArea}.png";
	/**
	 * デフォルトの差分ファイル名フォーマット
	 * 
	 * @see #diffFileName
	 */
	public static final String DEFAULT_DIFF_FILE_NAME = "{platformName}_{platformVersion}_{browserName}_{version}_{screenArea}_diff.png";

	private static final long serialVersionUID = 1L;

	/**
	 * テスト結果出力ディレクトリ
	 */
	private String resultDirectory = "results";

	/**
	 * スクリーンショット撮影結果ファイル名フォーマット
	 */
	private String targetResultFileName = DEFAULT_TARGET_RESULT_FILE_NAME;

	/**
	 * ファイル名フォーマット
	 */
	private String screenshotFileName = DEFAULT_SCREENSHOT_FILE_NAME;

	/**
	 * 差分ファイル名フォーマット
	 */
	private String diffFileName = DEFAULT_DIFF_FILE_NAME;

	/**
	 * テスト結果出力ディレクトリを取得します。
	 * 
	 * @return テスト結果出力ディレクトリ
	 */
	public String getResultDirectory() {
		return resultDirectory;
	}

	/**
	 * スクリーンショット撮影結果ファイル名フォーマットを取得します。
	 * 
	 * @return スクリーンショット撮影結果ファイル名フォーマット
	 */
	public String getTargetResultFileName() {
		return targetResultFileName;
	}

	/**
	 * ファイル名フォーマットを取得します。
	 * 
	 * @return ファイル名フォーマット
	 */
	public String getScreenshotFileName() {
		return screenshotFileName;
	}

	/**
	 * 差分ファイル名フォーマットを取得します。
	 * 
	 * @return 差分ファイル名フォーマット
	 */
	public String getDiffFileName() {
		return diffFileName;
	}

	/**
	 * テスト結果出力ディレクトリを設定します。
	 * 
	 * @param resultDirectory テスト結果出力ディレクトリ
	 */
	void setResultDirectory(String resultDirectory) {
		this.resultDirectory = resultDirectory;
	}

	/**
	 * スクリーンショット撮影結果ファイル名フォーマットを設定します。
	 * 
	 * @param targetResultFileName スクリーンショット撮影結果ファイル名フォーマット
	 */
	void setTargetResultFileName(String targetResultFileName) {
		this.targetResultFileName = targetResultFileName;
	}

	/**
	 * スクリーンショット画像のファイル名フォーマットを設定します。
	 * 
	 * @param screenshotFileName スクリーンショット画像のファイル名フォーマット
	 */
	void setScreenshotFileName(String screenshotFileName) {
		this.screenshotFileName = screenshotFileName;
	}

	/**
	 * 差分ファイル名フォーマットを設定します。
	 * 
	 * @param diffFileName 差分ファイル名フォーマット
	 */
	void setDiffFileName(String diffFileName) {
		this.diffFileName = diffFileName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		FilePersisterConfig config = (FilePersisterConfig) o;

		if (resultDirectory != null ? !resultDirectory.equals(config.resultDirectory) : config.resultDirectory != null) {
			return false;
		}
		if (targetResultFileName != null ? !targetResultFileName.equals(config.targetResultFileName)
				: config.targetResultFileName != null) {
			return false;
		}
		if (screenshotFileName != null ? !screenshotFileName.equals(config.screenshotFileName)
				: config.screenshotFileName != null) {
			return false;
		}
		return !(diffFileName != null ? !diffFileName.equals(config.diffFileName) : config.diffFileName != null);

	}

	@Override
	public int hashCode() {
		final int hashPrime = 31;
		int result = resultDirectory != null ? resultDirectory.hashCode() : 0;
		result = hashPrime * result + (targetResultFileName != null ? targetResultFileName.hashCode() : 0);
		result = hashPrime * result + (screenshotFileName != null ? screenshotFileName.hashCode() : 0);
		result = hashPrime * result + (diffFileName != null ? diffFileName.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return JSONUtils.toString(this);
	}

	//<editor-fold desc="Builder>

	/**
	 * {@link FilePersisterConfig}を任意のプロパティで生成するビルダーを取得します。
	 * 
	 * @return FilePersisterConfigのビルダー
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * {@link FilePersisterConfig}を構築するためのビルダークラス
	 */
	public static class Builder {

		private final FilePersisterConfig config = new FilePersisterConfig();

		/**
		 * 設定したプロパティから{@link FilePersisterConfig}を生成し取得します。
		 * 
		 * @return 設定したプロパティから生成した設定オブジェクト
		 */
		public FilePersisterConfig build() {
			FilePersisterConfig conf = new FilePersisterConfig();
			conf.resultDirectory = config.resultDirectory;
			conf.targetResultFileName = config.targetResultFileName;
			conf.screenshotFileName = config.screenshotFileName;
			conf.diffFileName = config.diffFileName;
			return conf;
		}

		/**
		 * テスト結果出力ディレクトリを設定します。
		 * 
		 * @param resultDirectory テスト結果出力ディレクトリ
		 * @return 設定済のビルダー
		 */
		public Builder resultDirectory(String resultDirectory) {
			config.setResultDirectory(resultDirectory);
			return this;
		}

		/**
		 * スクリーンショット撮影結果ファイル名フォーマットを設定します。
		 * 
		 * @param targetResultFileName スクリーンショット撮影結果ファイル名フォーマット
		 * @return 設定済のビルダー
		 */
		public Builder targetResultFileName(String targetResultFileName) {
			config.setTargetResultFileName(targetResultFileName);
			return this;
		}

		/**
		 * スクリーンショット画像のファイル名フォーマットを設定します。
		 * 
		 * @param screenshotFileName スクリーンショット画像のファイル名フォーマット
		 * @return 設定済のビルダー
		 */
		public Builder screenshotFileName(String screenshotFileName) {
			config.setScreenshotFileName(screenshotFileName);
			return this;
		}

		/**
		 * 差分ファイル名フォーマットを設定します。
		 * 
		 * @param diffFileName 差分ファイル名フォーマット
		 * @return 設定済のビルダー
		 */
		public Builder diffFileName(String diffFileName) {
			config.setDiffFileName(diffFileName);
			return this;
		}

	}

	//</editor-fold>

}
