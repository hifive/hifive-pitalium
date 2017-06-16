package com.htmlhifive.pitalium.image.model;

import java.io.Serializable;

public abstract class CompareOption implements Serializable {

	protected CompareOptionType type;

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
	 * @return type
	 */
	public CompareOptionType getType() {
		return type;
	}
}