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
package com.htmlhifive.pitalium.core.selenium;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.htmlhifive.pitalium.common.exception.JSONException;
import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
import com.htmlhifive.pitalium.common.util.JSONUtils;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;

/**
 * テスト実行時に指定したCapabilityを保持するクラス。
 */
public class PtlCapabilities extends DesiredCapabilities {

	private static final Logger LOG = LoggerFactory.getLogger(PtlCapabilities.class);

	private static List<PtlCapabilities[]> capabilities;

	/**
	 * コンストラクタ
	 *
	 * @param rawMap Capabilityを保持するマップ
	 */
	public PtlCapabilities(Map<String, ?> rawMap) {
		super(rawMap);
	}

	/**
	 * コンストラクタ
	 *
	 * @param other Capability
	 */
	public PtlCapabilities(Capabilities other) {
		super(other);
	}

	/**
	 * Capabilityを取得します。
	 *
	 * @return Capabilityのリスト
	 */
	public static List<PtlCapabilities[]> readCapabilities() {
		synchronized (PtlCapabilities.class) {
			if (capabilities != null) {
				return capabilities;
			}

			String filePath = PtlTestConfig.getInstance().getEnvironment().getCapabilitiesFilePath();
			List<Map<String, Object>> maps = readCapabilitiesFromFileOrResources(filePath);

			LOG.debug("Capabilities loaded. (size: {})", maps.size());
			LOG.trace("Capabilities: {}", maps);

			List<PtlCapabilities[]> result = new ArrayList<PtlCapabilities[]>(maps.size());
			for (Map<String, Object> map : maps) {
				PtlCapabilities[] array = { new PtlCapabilities(map) };
				result.add(array);
			}

			capabilities = Collections.unmodifiableList(result);
			return capabilities;
		}
	}

	/**
	 * Capabilitiesをファイルまたはリソースファイルから読み込みます。
	 *
	 * @param filePath ファイルパス
	 * @return 読み込んだCapabilities
	 */
	static List<Map<String, Object>> readCapabilitiesFromFileOrResources(String filePath) {
		LOG.trace("Load capabilities. ({})", filePath);

		TypeReference<List<Map<String, Object>>> reference = new TypeReference<List<Map<String, Object>>>() {
		};
		try {
			// Read from file
			return JSONUtils.readValue(new File(filePath), reference);
		} catch (JSONException e) {
			// Read from resources
			InputStream in = null;
			try {
				in = PtlCapabilities.class.getClassLoader().getResourceAsStream(filePath);
				return JSONUtils.readValue(in, reference);
			} catch (Exception e1) {
				throw new TestRuntimeException("Failed to load capabilities", e1);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException ioe) {
						LOG.error("", ioe);
					}
				}
			}
		}
	}

	/**
	 * プラットフォーム名を取得します。
	 *
	 * @return プラットフォーム名
	 */
	public String getPlatformName() {
		Platform platform = getPlatform();
		if (platform != null) {
			return platform.name();
		} else {
			return toString(getCapability("platformName"));
		}
	}

	/**
	 * プラットフォームのバージョンを取得します。
	 *
	 * @return プラットフォームのバージョン
	 */
	public String getPlatformVersion() {
		return toString(getCapability("platformVersion"));
	}

	/**
	 * デバイス名を取得します。
	 *
	 * @return デバイス名
	 */
	public String getDeviceName() {
		return toString(getCapability("deviceName"));
	}

	/**
	 * 指定したオブジェクトの文字列表現を取得します。
	 * 
	 * @param object 対象のオブジェクト
	 * @return 指定したオブジェクトの文字列表現
	 */
	private static String toString(Object object) {
		return object == null ? null : object.toString();
	}

}
