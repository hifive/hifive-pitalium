package com.htmlhifive.pitalium.core.selenium;

import java.awt.image.BufferedImage;
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

	@Override
	protected BufferedImage trimOverlap(double captureTop, double captureLeft, long windowHeight, long windowWidth,
			double scale, BufferedImage img) {
		BufferedImage image = img;
		// 下端の推定位置（次スクロール時にトップに来る位置）と、実際のキャプチャに写っている下端の位置を比較
		long calculatedBottomValue = Math.round((captureTop + windowHeight) * scale);
		long actualBottomValue = Math.round(captureTop * scale) + img.getHeight();
		// 余分にキャプチャに写っていたら切り取っておく
		if (calculatedBottomValue < actualBottomValue) {
			image = image.getSubimage(0, 0, image.getWidth(),
					(int) (image.getHeight() - (actualBottomValue - calculatedBottomValue)));
		}

		long calculatedRightValue = Math.round((captureLeft + windowWidth) * scale);
		long actualRightValue = Math.round(captureLeft * scale) + img.getWidth();
		// 余分にキャプチャに写っていたら切り取っておく
		if (calculatedRightValue < actualRightValue) {
			image = image.getSubimage(0, 0, (int) (image.getWidth() - (actualRightValue - calculatedRightValue)),
					image.getHeight());
		}

		return image;
	}

}
