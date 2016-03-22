package com.htmlhifive.pitalium.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link org.openqa.selenium.Capabilities}に対して複数のフィルターを設定します。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD})
public @interface CapabilityFilters {

	/**
	 * 複数の{@link CapabilityFilter}でテスト実行対象を指定する際に使用します。<br>
	 * 指定されたCapabilityFilterはOR条件として扱われます。
	 *
	 * @return テスト実行対象
	 */
	CapabilityFilter[] value();

}
