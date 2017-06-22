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
package com.htmlhifive.pitalium.image.model;

import java.io.Serializable;

/**
 * 比較時の設定情報を保持するクラス
 */
public class CompareOption implements Serializable {

	/**
	 * 比較種別
	 */
	private CompareOptionType type;

	/**
	 * 比較時の設定
	 */
	private ComparisonParameters parameters;

	/**
	 * CompareOptionクラスを生成する
	 */
	public CompareOption() {
	}

	/**
	 * CompareOptionクラスを生成する
	 *
	 * @param type
	 * @param parameters
	 */
	public CompareOption(CompareOptionType type) {
		this.type = type;
	}

	/**
	 * CompareOptionクラスを生成する
	 *
	 * @param type
	 * @param parameters
	 */
	public CompareOption(CompareOptionType type, ComparisonParameters parameters) {
		this.type = type;
		this.parameters = parameters;
	}

	/**
	 * @return type
	 */
	public CompareOptionType getType() {
		return type;
	}

	/**
	 * @return parameters
	 */
	public ComparisonParameters getParameters() {
		return parameters;
	}
}