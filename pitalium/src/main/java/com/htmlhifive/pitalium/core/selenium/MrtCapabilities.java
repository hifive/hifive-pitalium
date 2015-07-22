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
import com.htmlhifive.pitalium.core.config.MrtTestConfig;

/**
 * テスト実行時に指定したCapabilityを保持するクラス。
 */
public class MrtCapabilities extends DesiredCapabilities {

	private static final Logger LOG = LoggerFactory.getLogger(MrtCapabilities.class);

	private static List<MrtCapabilities[]> capabilities;

	/**
	 * コンストラクタ
	 * 
	 * @param rawMap Capabilityを保持するマップ
	 */
	public MrtCapabilities(Map<String, ?> rawMap) {
		super(rawMap);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param other Capability
	 */
	public MrtCapabilities(Capabilities other) {
		super(other);
	}

	/**
	 * Capabilityを取得します。
	 * 
	 * @return Capabilityのリスト
	 */
	public static List<MrtCapabilities[]> readCapabilities() {

		synchronized (MrtCapabilities.class) {
			if (capabilities != null) {
				return capabilities;
			}

			String filePath = MrtTestConfig.getInstance().getEnvironment().getCapabilitiesFilePath();
			List<Map<String, Object>> maps = readCapabilitiesFromFileOrResources(filePath);

			LOG.debug("Read capabilities count: {}", maps.size());

			List<MrtCapabilities[]> result = new ArrayList<MrtCapabilities[]>(maps.size());
			for (Map<String, Object> map : maps) {
				MrtCapabilities[] array = { new MrtCapabilities(map) };
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
		TypeReference<List<Map<String, Object>>> reference = new TypeReference<List<Map<String, Object>>>() {
		};
		try {
			// Read from file
			return JSONUtils.readValue(new File(filePath), reference);
		} catch (JSONException e) {
			LOG.debug("Capabilities is not file \"{}\"", filePath);

			// Read from resources
			InputStream in = null;
			try {
				in = MrtCapabilities.class.getClassLoader().getResourceAsStream(filePath);
				return JSONUtils.readValue(in, reference);
			} catch (Exception e1) {
				throw new TestRuntimeException("Load capabilities.json error", e1);
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

	private static String toString(Object object) {
		return object == null ? null : object.toString();
	}

}
