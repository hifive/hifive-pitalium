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
package com.htmlhifive.pitalium.core.io;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.htmlhifive.pitalium.core.model.IndexDomSelector;

/**
 * {@link FilePersister}で保存するファイル名のフォーマットを行うクラス
 */
public class FileNameFormatter {

	/**
	 * プレースホルダー用正規表現パターン
	 */
	private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([a-zA-Z0-9]+)\\}");
	/**
	 * 重複アンダースコア用正規表現パターン
	 */
	private static final Pattern UNDERSCORE_PATTERN = Pattern.compile("__");
	/**
	 * ファイル名不可文字用正規表現パターン
	 */
	private static final Pattern FILE_NAME_ESCAPE_PATTERN = Pattern.compile("[\\\\/:\\*\\?\"<>|]");

	/**
	 * プレースホルダを特殊な方法で置換する関数コンテナ
	 */
	private static final Map<String, Function<PersistMetadata, String>> PLACEHOLDER_FUNCTIONS;

	static {
		PLACEHOLDER_FUNCTIONS = new HashMap<String, Function<PersistMetadata, String>>();

		PLACEHOLDER_FUNCTIONS.put("browserName", new Function<PersistMetadata, String>() {
			@Override
			public String apply(PersistMetadata metadata) {
				String browserName = metadata.getCapabilities().getBrowserName();
				if (Strings.isNullOrEmpty(browserName)
						&& metadata.getCapabilities().getCapability("automationName") != null) {
					browserName = "Selendroid";
				}
				return browserName;
			}
		});
		PLACEHOLDER_FUNCTIONS.put("platformName", new Function<PersistMetadata, String>() {
			@Override
			public String apply(PersistMetadata metadata) {
				return metadata.getCapabilities().getPlatformName();
			}
		});

		PLACEHOLDER_FUNCTIONS.put("screenArea", new Function<PersistMetadata, String>() {
			@Override
			public String apply(PersistMetadata metadata) {
				StringBuilder sb = new StringBuilder();
				if (metadata.getSelector() != null) {
					IndexDomSelector selector = metadata.getSelector();
					sb.append(selector.getType().name()).append("_").append(selector.getValue());
					if (selector.getIndex() != null) {
						sb.append("_[").append(selector.getIndex()).append("]");
					}
				} else if (metadata.getRectangle() != null) {
					Rectangle r = metadata.getRectangle().toRectangle();
					sb.append("rect_").append(r.x).append("_").append(r.y).append("_").append(r.width).append("_")
							.append(r.height);
				}
				return sb.toString();
			}
		});
	}

	private final String format;

	/**
	 * 初期化します。
	 * 
	 * @param format 整形フォーマット
	 */
	public FileNameFormatter(String format) {
		this.format = format;
	}

	/**
	 * Capabilitiesやセレクタ等を指定のフォーマットで整形した文字列を取得します。
	 * 
	 * @param metadata フォーマットのプレースホルダを置換するメタデータ
	 * @return 整形した文字列
	 */
	public String format(PersistMetadata metadata) {
		StringBuffer sb = new StringBuffer();
		sb.append(metadata.getMethodName()).append("_").append(metadata.getScreenshotId()).append("_");

		// Replace placeholders with capabilities values
		Matcher matcher = PLACEHOLDER_PATTERN.matcher(format);
		while (matcher.find()) {
			String key = matcher.group(1);
			String value;
			Function<PersistMetadata, String> func = PLACEHOLDER_FUNCTIONS.get(key);
			if (func == null) {
				Object object = metadata.getCapabilities().getCapability(key);
				value = object == null ? null : object.toString();
			} else {
				value = func.apply(metadata);
			}

			matcher.appendReplacement(sb, Strings.nullToEmpty(value));
		}
		matcher.appendTail(sb);

		// Remove tail underscores
		int index = sb.lastIndexOf(".") - 1;
		while (sb.charAt(index) == '_') {
			sb.deleteCharAt(index--);
		}

		String s = UNDERSCORE_PATTERN.matcher(sb).replaceAll("_");
		return FILE_NAME_ESCAPE_PATTERN.matcher(s).replaceAll("-");
	}

	/**
	 * プレースホルダの置換方法を追加します。
	 * 
	 * @param key プレースホルダ名
	 * @param func 置換方法を示す関数
	 */
	protected final void putPlaceholderFunc(String key, Function<PersistMetadata, String> func) {
		PLACEHOLDER_FUNCTIONS.put(key, func);
	}

	/**
	 * プレースホルダを置換する関数を削除します。
	 * 
	 * @param key プレースホルダ名
	 */
	protected final void removePlaceholderFunc(String key) {
		PLACEHOLDER_FUNCTIONS.remove(key);
	}

}
