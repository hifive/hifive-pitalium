package com.htmlhifive.pitalium.core.model;

/**
 * User Timingのmarkを保持するクラス。
 */
public class PerformanceMark {
	private String name;
	private double startTime;

	/**
	 * 空のオブジェクトを生成します。
	 */
	public PerformanceMark() {
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
	 * performance.mark()の呼び出された時間を取得します。
	 * 
	 * @return performance.mark()の呼び出された時間
	 */
	public double getStartTime() {
		return startTime;
	}

	/**
	 * performance.mark()の呼び出された時間を設定します。
	 * 
	 * @param startTime performance.mark()の呼び出された時間
	 */
	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}
}
