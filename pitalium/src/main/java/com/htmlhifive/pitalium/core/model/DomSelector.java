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
package com.htmlhifive.pitalium.core.model;

import java.io.Serializable;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.htmlhifive.pitalium.common.util.JSONUtils;

/**
 * DOM要素を指定するためのセレクタを保持するクラス。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DomSelector implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * セレクタの種別
	 */
	private final SelectorType type;

	/**
	 * セレクタの値
	 */
	private final String value;

	/**
	 * フレームを指定するセレクタ
	 */
	private final DomSelector parentSelector;

	/**
	 * DOM要素をセレクタの種別と値で指定します。
	 * 
	 * @param type セレクタの種別
	 * @param value セレクタの値
	 */
	public DomSelector(SelectorType type, String value) {
		this(type, value, null);
	}

	/**
	 * DOM要素をセレクタの種別と値で指定します。
	 * 
	 * @param type セレクタの種別
	 * @param value セレクタの値
	 * @param parentSelector フレームを指定するセレクタ
	 */
	@JsonCreator
	public DomSelector(@JsonProperty("type") SelectorType type, @JsonProperty("value") String value,
			@JsonProperty("parentSelector") DomSelector parentSelector) {
		this.type = type;
		this.value = value;
		this.parentSelector = parentSelector;
	}

	/**
	 * セレクタの種別を取得します。
	 * 
	 * @return セレクタの種別
	 */
	public SelectorType getType() {
		return type;
	}

	/**
	 * セレクタの値を取得します。
	 * 
	 * @return セレクタの値
	 */
	public String getValue() {
		return value;
	}

	/**
	 * フレームを指定するセレクタを取得します。
	 * 
	 * @return フレームを指定するセレクタ
	 */
	public DomSelector getParentSelector() {
		return parentSelector;
	}

	/**
	 * このDomSelectorが指し示す要素を取得します。
	 * 
	 * @param driver WebDriver
	 * @return このDomSelectorが指し示す要素
	 */
	public WebElement findElement(WebDriver driver) {
		if (parentSelector == null) {
			return type.findElement(driver, value);
		}

		WebElement frameElement = parentSelector.findElement(driver);
		return type.findElement(frameElement, value);
	}

	/**
	 * このDomSelectorが指し示す要素を取得します。
	 * 
	 * @param element 要素を検索するDOMのルート要素
	 * @return このDomSelectorが指し示す要素
	 */
	public WebElement findElement(WebElement element) {
		if (parentSelector == null) {
			return type.findElement(element, value);
		}

		WebElement frameElement = parentSelector.findElement(element);
		return type.findElement(frameElement, value);
	}

	/**
	 * このDomSelectorが指し示す要素を取得します。
	 * 
	 * @param driver WebDriver
	 * @return このDomSelectorが指し示す要素
	 */
	@SuppressWarnings("unchecked")
	public List<WebElement> findElements(WebDriver driver) {
		if (parentSelector == null) {
			return type.findElements(driver, value);
		}

		WebElement frameElement = parentSelector.findElement(driver);
		return type.findElements(frameElement, value);
	}

	/**
	 * このDomSelectorが指し示す要素を取得します。
	 * 
	 * @param element 要素を検索するDOMのルート要素
	 * @return このDomSelectorが指し示す要素
	 */
	@SuppressWarnings("unchecked")
	public List<WebElement> findElements(WebElement element) {
		if (parentSelector == null) {
			return type.findElements(element, value);
		}

		WebElement frameElement = parentSelector.findElement(element);
		return type.findElements(frameElement, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DomSelector other = (DomSelector) obj;
		if (parentSelector == null) {
			if (other.parentSelector != null) {
				return false;
			}
		} else if (!parentSelector.equals(other.parentSelector)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parentSelector == null) ? 0 : parentSelector.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return JSONUtils.toString(this);
	}

}
