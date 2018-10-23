package com.htmlhifive.pitalium.core.model;

/**
 * User Timingのmeasureを保持するクラス。
 */
public class PerformanceMeasure {
	private String name;
	private double startTime;
	private double duration;

	/**
	 * 空のオブジェクトを生成します。
	 */
	public PerformanceMeasure() {
	}

	/**
	 * 名前を取得します。
	 * 
	 * @return 名前
	 */
	public String getName() {
		return name;
	}

	/**
	 * 名前を設定します。
	 * 
	 * @param name 名前
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * measureの開始点となるperformance.mark()の呼び出された時間を取得します。
	 * 
	 * @return measureの開始点となるperformance.mark()の呼び出された時間
	 */
	public double getStartTime() {
		return startTime;
	}

	/**
	 * measureの開始点となるperformance.mark()の呼び出された時間を設定します。
	 * 
	 * @param startTime measureの開始点となるperformance.mark()の呼び出された設定
	 */
	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	/**
	 * performance.measure()で測定した時間を取得します。
	 * 
	 * @return performance.measure()で測定した時間
	 */
	public double getDuration() {
		return duration;
	}

	/**
	 * performance.measure()で測定した時間を設定します。
	 * 
	 * @param duration performance.measure()で測定した時間
	 */
	public void setDuration(double duration) {
		this.duration = duration;
	}
}
