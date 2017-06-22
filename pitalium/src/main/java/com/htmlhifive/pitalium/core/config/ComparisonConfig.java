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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.htmlhifive.pitalium.common.util.JSONUtils;
import com.htmlhifive.pitalium.image.model.CompareOption;
import com.htmlhifive.pitalium.image.model.CompareOptionType;
import com.htmlhifive.pitalium.image.model.DefaultComparisonParameters;
import com.htmlhifive.pitalium.image.model.SimilarityComparisonParameters;

/**
 * 比較方法に関する設定
 */
@PtlConfiguration
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComparisonConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	// TODO: できればクラスで受けたい
	/**
	 * 比較方法
	 */
	private List<Map<String, Object>> options;

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
	@SuppressWarnings("unchecked")
	public List<CompareOption> getOptions() {
		if (options == null) {
			return null;
		}

		List<CompareOption> ret = new ArrayList<>();
		for (Map<String, Object> map : options) {
			CompareOptionType type = CompareOptionType.valueOf(map.get("type").toString());
			Map<String, Object> parameters = (Map<String, Object>) map.get("parameters");
			if (parameters == null) {
				ret.add(new CompareOption(type));
				continue;
			}

			switch (type) {
				case SIMILARITY:
					ret.add(new CompareOption(type, new SimilarityComparisonParameters(parameters)));
					continue;
				default:
					ret.add(new CompareOption(type, new DefaultComparisonParameters(parameters)));
					continue;
			}
		}

		return ret;
	}

	/**
	 * 比較方法を設定します。
	 *
	 * @param options 比較方法
	 */
	void setOptions(List<Map<String, Object>> options) {
		this.options = options;
	}

	@Override
	public String toString() {
		return JSONUtils.toString(this);
	}

}
