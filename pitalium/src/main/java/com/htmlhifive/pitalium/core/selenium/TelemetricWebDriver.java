package com.htmlhifive.pitalium.core.selenium;

import com.htmlhifive.pitalium.core.rules.PerformanceTelemetry;

/**
 * 性能測定機能を持つWebDriverのインターフェイス。
 */
public interface TelemetricWebDriver {
	//@formatter:off
	// CHECKSTYLE:OFF
	/**
	 * ブラウザのPerformance API等を実行し
	 * {@link com.htmlhifive.pitalium.core.model.Performance}のJSON文字列表現を取得するJavaScript
	 */
	String SCRIPT =
			"if(typeof JSON === 'undefined') {" +
			// JSON.stringifyが実装されていない場合のための実装
	        "    function stringify(obj) {" +
			"        if (typeof obj === 'number') {" +
			"            return String(obj);" +
			"        } else if (typeof obj === 'string') {" +
			"            return '\"' + jsonEscape(obj) + '\"';" +
			"        } else if (obj instanceof Array) {" +
			"            return stringifyArray(obj);" +
			"        }" +
			"        return stringifyObject(obj);" +
			"    }" +
			"    function jsonEscape(str) {" +
			"        return str.replace(/\\\\/g, '\\\\\\\\')" +
			"            .replace(/\"/g, '\\\\\"')" +
			"            .replace(/\\//g, '\\\\/');" +
			"    }" +
			"    function stringifyObject(obj) {" +
			"        var ret = [];" +
			"        for (var i in obj) {" +
			"            ret.push('\"' + i + '\":' + stringify(obj[i]));" +
			"        }" +
			"        return '{' + ret.join(',') + '}';" +
			"    }" +
			"    function stringifyArray(arr) {" +
			"        var ret = [];" +
			"        for (var i in arr) {" +
			"            ret.push(stringify(arr[i]));" +
			"        }" +
			"        return '[' + ret.join(',') + ']';" +
			"    }" +
			"    JSON = {" +
			"        stringify: stringify" +
			"    };" +
			"}" +
			"return JSON.stringify({" +
			"    navigationTiming: performance && performance.timing || {}," +
			"    resourceTimings: (" +
			"                        performance" +
			"                        && performance.getEntriesByType" +
			"                        && performance.getEntriesByType('resource')" +
			"                     ) || []," +
			"    marks: (" +
			"              performance" +
			"              && performance.getEntriesByType" +
			"              && performance.getEntriesByType('mark')" +
			"           ) || []," +
			"    measures: (" +
			"                 performance " +
			"                 && performance.getEntriesByType" +
			"                 && performance.getEntriesByType('measure')" +
			"              ) || []," +
			"    url: window.location.toString()" +
			"});";
	// CHECKSTYLE:ON
	//@formatter:on

	/**
	 * 性能を測定します。
	 * 
	 * @param label 性能測定結果のラベル
	 */
	void measurePerformance(String label);

	/**
	 * 性能測定結果の記録先となる{@link com.htmlhifive.pitalium.core.rules.PerformanceTelemetry}を設定します。
	 * 
	 * @param telemetry 性能測定結果の記録先
	 */
	void setPerformanceMeasure(PerformanceTelemetry telemetry);
}
