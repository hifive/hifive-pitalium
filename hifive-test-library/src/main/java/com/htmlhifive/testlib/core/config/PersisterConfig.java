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
