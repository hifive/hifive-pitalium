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
package com.htmlhifive.pitalium.core.config;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.htmlhifive.pitalium.common.util.JSONUtils;
import com.htmlhifive.pitalium.image.model.CompareOption;

/**
 * 比較方法に関する設定
 */
@PtlConfiguration
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComparisonConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 比較方法
	 */
	private List<CompareOption> options;

	/**
	 * コンストラクタ
	 */
	public ComparisonConfig() {
	}

	/**
	 * 比較方法を取得します。
	 *
	 * @return 比較方法
	 */
	public List<CompareOption> getOptions() {
		return options;
	}

	/**
	 * 比較方法を設定します。
	 *
	 * @param options 比較方法
	 */
	void setOptions(List<CompareOption> options) {
		this.options = options;
	}

	/**
	 * 比較方法を設定します。
	 *
	 * @param options 比較方法
	 */
	//	void setOptions(Map<String, ?>[] optionMaps) {
	//		CompareOption[] options = new CompareOption[optionMaps.length];
	//		int i = 0;
	//		for (Map<String, ?> option : optionMaps) {
	//			CompareOptionType type = CompareOptionType.valueOf((String) option.get("type"));
	//			if (type == null) {
	//				throw new TestRuntimeException("the type is not correct. type=" + type);
	//			}
	//			Map<String, Object> p = (Map<String, Object>) option.get("parameters");
	//			if (p == null) {
	//				options[i] = new CompareOption(type);
	//			} else {
	//				options[i] = new CompareOption(type, createParametersInstance(type).);
	//			}
	//
	//			CompareOption o = new CompareOption();
	//		}
	//
	//		this.options = options;
	//	}
	//
	//	private Class<?> createParametersInstance(CompareOptionType type, Map<String, Object> params) {
	//		switch (type) {
	//			case DEFAULT:
	//				return new DefaultComparisonParameters(params);
	//			case SIMILARITY:
	//				return new SimilarityComparisonParameters(params);
	//			default:
	//				return new DefaultComparisonParameters(params);
	//		}
	//	}

	@Override
	public String toString() {
		return JSONUtils.toString(this);
	}

}
