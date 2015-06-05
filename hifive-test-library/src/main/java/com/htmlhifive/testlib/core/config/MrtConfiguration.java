/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * テストツールで利用する設定ファイルの属性
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MrtConfiguration {

	/**
	 * JVM起動引数のキーを取得します。デフォルトはクラス名のlowerCamelCaseです。
	 */
	String argumentName() default "";

	/**
	 * 設定ファイルのデフォルトファイル名を取得します。デフォルトはクラス名のlowerCamelCase + &quot;.json&quot;です。
	 */
	String defaultFileName() default "";

}
