package com.htmlhifive.pitalium.core;

import org.junit.Before;
import org.junit.Rule;

import com.htmlhifive.pitalium.core.rules.PerformanceTelemetry;
import com.htmlhifive.pitalium.core.selenium.TelemetricWebDriver;

/**
 * 性能測定モードでのテスト実行用の基底クラス。テスト実行に必要な&#064;Rule、&#064;ClassRuleが定義されています。<br/>
 * 本テストツールの機能を性能測定モードで利用する場合は、このクラスを拡張してテストクラスを実装してください。
 */
public class TelemetricTestBase extends PtlTestBase {

	/**
	 * 性能測定結果の記録を行うクラス。
	 */
	@Rule
	public PerformanceTelemetry measure = new PerformanceTelemetry();

	/**
	 * テスト実行時のセットアップを行います。
	 */
	@Override
	@Before
	public void setUp() {
		super.setUp();
		LOG.debug("[TelemetricTestBase>setUp start]");
		TelemetricWebDriver telemetricWebDriver = (TelemetricWebDriver) driver;
		telemetricWebDriver.setPerformanceMeasure(measure);
		measure.setTelemetricWebDriver(telemetricWebDriver);
		measure.setCapabilities(capabilities);
		LOG.debug("[TelemetricTestBase>setUp finished]");
	}
}
