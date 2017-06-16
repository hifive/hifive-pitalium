package com.htmlhifive.pitalium.image.model;

import java.io.Serializable;

public class CompareOption implements Serializable {

	CompareOptionType type;

	ComparisonParameters parameters;

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