package com.htmlhifive.pitalium.core.selenium;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class PtlAbsAndroidDriver extends SplitScreenshotWebDriver {

	private static final Logger LOG = LoggerFactory.getLogger(PtlAbsAndroidDriver.class);

	/**
	 * コンストラクタ
	 *
	 * @param remoteAddress RemoteWebDriverServerのアドレス
	 * @param capabilities Capability
	 */
	PtlAbsAndroidDriver(URL remoteAddress, PtlCapabilities capabilities) {
		super(remoteAddress, capabilities);
	}

	@Override
	protected double calcScale(double windowWidth, double imageWidth) {
		return imageWidth / windowWidth;
	}

}
