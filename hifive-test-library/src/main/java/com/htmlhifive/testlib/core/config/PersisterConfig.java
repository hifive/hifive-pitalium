/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.config;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.htmlhifive.testlib.common.util.JSONUtils;

/**
 * 画像やテスト結果の入出力に関する設定
 */
@MrtConfiguration
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersisterConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 画像やテスト結果の情報をファイルから入出力する際の設定
	 */
	private FilePersisterConfig file = new FilePersisterConfig();

	/**
	 * コンストラクタ
	 */
	public PersisterConfig() {
	}

	/**
	 * 画像やテスト結果の情報をファイルから入出力する際の設定を取得します。
	 * 
	 * @return 画像やテスト結果の情報をファイルから入出力する際の設定
	 */
	public FilePersisterConfig getFile() {
		return file;
	}

	/**
	 * 入出力設定を設定します。
	 * 
	 * @param file 入出力設定
	 */
	void setFile(FilePersisterConfig file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return JSONUtils.toString(this);
	}

}
