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
package com.htmlhifive.pitalium.common.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.htmlhifive.pitalium.common.exception.JSONException;

/**
 * {@link ObjectMapper}のラッパークラス
 */
public final class JSONUtils {

	private static ObjectMapper mapper;
	private static ObjectMapper indentMapper;

	static {
		mapper = new ObjectMapper();
		mapper.findAndRegisterModules();

		indentMapper = new ObjectMapper();
		indentMapper.findAndRegisterModules();
		indentMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	/**
	 * コンストラクタ
	 */
	private JSONUtils() {
	}

	/**
	 * ファイルからJSONを読み出し、Beanに変換します。
	 * 
	 * @param file 読み出し元ファイル
	 * @param clss 変換後のクラス
	 * @param <T> 変換後クラスの型
	 * @return 変換後のBeanオブジェクト
	 */
	public static <T> T readValue(File file, Class<T> clss) {
		try {
			return mapper.readValue(file, clss);
		} catch (IOException e) {
			throw new JSONException(e);
		}
	}

	/**
	 * ファイルからJSONを読み出し、Beanに変換します。
	 * 
	 * @param file 読み出し元ファイル
	 * @param reference 変換後の型
	 * @param <T> 変換後クラスの型
	 * @return 変換後のBeanオブジェクト
	 */
	public static <T> T readValue(File file, TypeReference<T> reference) {
		try {
			return mapper.readValue(file, reference);
		} catch (IOException e) {
			throw new JSONException(e);
		}
	}

	/**
	 * InputStreamからJSONを読み出し、Beanに変換します。
	 * 
	 * @param in InputStream
	 * @param reference 変換後の型
	 * @param <T> 変換後クラスの型
	 * @return 変換後のBeanオブジェクト
	 */
	public static <T> T readValue(InputStream in, TypeReference<T> reference) {
		try {
			return mapper.readValue(in, reference);
		} catch (IOException e) {
			throw new JSONException(e);
		}
	}

	/**
	 * InputStreamからJSONを読み出し、Beanに変換します。
	 * 
	 * @param in InputStream
	 * @param clss 変換後のクラス
	 * @param <T> 変換後クラスの型
	 * @return 変換後のBeanオブジェクト
	 */
	public static <T> T readValue(InputStream in, Class<T> clss) {
		try {
			return mapper.readValue(in, clss);
		} catch (IOException e) {
			throw new JSONException(e);
		}
	}

	/**
	 * オブジェクトをJSON文字列として取得します
	 * 
	 * @param object オブジェクト
	 * @return オブジェクトを表すJSON文字列
	 */
	public static String toString(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (IOException e) {
			throw new JSONException(e);
		}
	}

	/**
	 * 値をJSONに変換し、ファイルに書き込みます。
	 * 
	 * @param file 書き込み先ファイル
	 * @param value 書き込む値
	 */
	public static void writeValue(File file, Object value) {
		try {
			mapper.writeValue(file, value);
		} catch (IOException e) {
			throw new JSONException(e);
		}
	}

	/**
	 * オブジェクトをJSON文字列として取得します
	 * 
	 * @param object オブジェクト
	 * @return オブジェクトを表すJSON文字列
	 */
	public static String toStringWithIndent(Object object) {
		try {
			return indentMapper.writeValueAsString(object);
		} catch (IOException e) {
			throw new JSONException(e);
		}
	}

	/**
	 * 値をJSONに変換し、ファイルに書き込みます。
	 * 
	 * @param file 書き込み先ファイル
	 * @param value 書き込む値
	 */
	public static void writeValueWithIndent(File file, Object value) {
		try {
			indentMapper.writeValue(file, value);
		} catch (IOException e) {
			throw new JSONException(e);
		}
	}

}
