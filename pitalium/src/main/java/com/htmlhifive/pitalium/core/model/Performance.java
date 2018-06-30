package com.htmlhifive.pitalium.core.model;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 性能測定結果を保持するクラス。
 */
public class Performance {
	private String url;
	private List<PerformanceResourceTiming> resourceTimings;
	private PerformanceTiming navigationTiming;
	private List<PerformanceMark> marks;
	private List<PerformanceMeasure> measures;
	private String browser;
	private String id;
	private String label;

	/**
	 * 空のオブジェクトを生成します。
	 */
	public Performance() {
	}

	/**
	 * 性能測定時のURLを取得します。
	 * 
	 * @return 性能測定時のURL
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 性能測定時のURLを設定します。
	 * 
	 * @param url 性能測定時のURL
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Resource Timingのリストを取得します。
	 * 
	 * @return Resource Timingのリスト
	 */
	public List<PerformanceResourceTiming> getResourceTimings() {
		return resourceTimings;
	}

	/**
	 * Resource Timingのリストを設定します。
	 * 
	 * @param resourceTimings Resource Timingのリスト
	 */
	public void setResourceTimings(List<PerformanceResourceTiming> resourceTimings) {
		this.resourceTimings = resourceTimings;
	}

	/**
	 * Navigation Timingを取得します。
	 * 
	 * @return Navigation Timing
	 */
	public PerformanceTiming getNavigationTiming() {
		return navigationTiming;
	}

	/**
	 * Navigation Timingを設定します。
	 * 
	 * @param navigationTiming Navigation Timing
	 */
	public void setNavigationTiming(PerformanceTiming navigationTiming) {
		this.navigationTiming = navigationTiming;
	}

	/**
	 * 性能測定に使用したブラウザを取得します。
	 * 
	 * @return 性能測定に使用したブラウザ
	 */
	public String getBrowser() {
		return browser;
	}

	/**
	 * 性能測定に使用したブラウザを設定します。
	 * 
	 * @param browser 性能測定に使用したブラウザ
	 */
	public void setBrowser(String browser) {
		this.browser = browser;
	}

	/**
	 * 性能測定結果のIDを取得します。
	 * 
	 * @return ID 性能測定結果のID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 性能測定結果のIDを設定します。
	 * 
	 * @param id 性能測定結果のID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * User Timingのmarkのリストを取得します。
	 * 
	 * @return User Timingのmarkのリスト
	 */
	public List<PerformanceMark> getMarks() {
		return marks;
	}

	/**
	 * User Timingのmarkのリストを設定します。
	 * 
	 * @param marks User Timingのmarkのリスト
	 */
	public void setMarks(List<PerformanceMark> marks) {
		this.marks = marks;
	}

	/**
	 * User Timingのmeasureのリストを取得します。
	 * 
	 * @return User Timingのmeasureのリスト
	 */
	public List<PerformanceMeasure> getMeasures() {
		return measures;
	}

	/**
	 * User Timingのmeasureのリストを設定します。
	 * 
	 * @param measures User Timingのmeasureのリスト
	 */
	public void setMeasures(List<PerformanceMeasure> measures) {
		this.measures = measures;
	}

	/**
	 * 性能測定結果を更新します。
	 * 
	 * @param performance 性能測定結果の更新内容
	 */
	public void updateWith(Performance performance) {
		setUrl(performance.getUrl());
		setResourceTimings(performance.getResourceTimings());
		setNavigationTiming(performance.getNavigationTiming());
		setMarks(performance.getMarks());
		setMeasures(performance.getMeasures());
		setBrowser(performance.getBrowser());
		setId(performance.getId());
		if (performance.getLabel() != null) {
			setLabel(performance.getLabel());
		}
	}

	/**
	 * 性能測定結果のラベルを取得します。
	 * 
	 * @return 性能測定結果のラベル
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * 性能測定結果のラベルを設定します。
	 * 
	 * @param label 性能測定結果のラベル
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * 性能測定結果のJSON文字列表現をパースします。
	 * 
	 * @param obj 性能測定結果のJSON文字列表現
	 * @return 性能測定結果
	 */
	public static Performance parseJson(Object obj) {
		if (!(obj instanceof String)) {
			throw new IllegalArgumentException("Returned object cannot be parsed to String.");
		}
		String json = (String) obj;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			Performance performanceResult = objectMapper.readValue(json, Performance.class);
			return performanceResult;
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
