package com.htmlhifive.pitalium.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openqa.selenium.Platform;

/**
 * {@link org.openqa.selenium.Capabilities}に対してフィルターを設定します。<br>
 * フィルター項目は全て複数個設定可能で、各フィルター項目内ではOR条件、フィルター項目間ではAND条件として扱われます。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD })
public @interface CapabilityFilter {

	/**
	 * テスト実行対象のバージョンを指定します。正規表現として扱われ、複数個指定することが出来ます。
	 * 
	 * @return テスト実行対象のバージョン
	 */
	String[] version() default {};

	/**
	 * テスト実行対象のプラットフォームを指定します。複数個指定することが出来ます。
	 * 
	 * @return テスト実行対象のプラットフォーム
	 */
	Platform[] platform() default {};

	/**
	 * テスト実行対象のブラウザ名を指定します。複数個指定することが出来ます。
	 * 
	 * @return テスト実行対象のブラウザ名
	 */
	String[] browserName() default {};

	/**
	 * テスト実行対象のデバイス名を指定します。正規表現として扱われ、複数個指定することが出来ます。
	 * 
	 * @return テスト実行対象のデバイス名
	 */
	String[] deviceName() default {};

	/**
	 * テスト実行対象のグループを指定します。複数個指定することが出来ます。<br>
	 * グループはcapabilitiesで{@code filterGroup}を指定します。
	 * 
	 * @return テスト実行対象のグループ
	 */
	String[] filterGroup() default {};

}
