/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.selenium;

import java.awt.image.BufferedImage;
import java.net.URL;

import com.htmlhifive.testlib.common.exception.TestRuntimeException;

/**
 * iPadのSafariで利用する{@link org.openqa.selenium.WebDriver}
 */
class MrtIPadDriver extends MrtIPhoneDriver {

	private final int headerHeight;

	/**
	 * コンストラクタ
	 * 
	 * @param remoteAddress RemoteWebDriverServerのアドレス
	 * @param capabilities Capability
	 */
	MrtIPadDriver(URL remoteAddress, MrtCapabilities capabilities) {
		super(remoteAddress, capabilities);

		Object headerHeightCapability = capabilities.getCapability("headerHeight");
		if (headerHeightCapability == null) {
			throw new TestRuntimeException("Capability \"headerHeight\" is required");
		} else {
			if (headerHeightCapability instanceof Number) {
				headerHeight = ((Number) headerHeightCapability).intValue();
			} else {
				headerHeight = Integer.parseInt(headerHeightCapability.toString());
			}
		}
	}

	@Override
	protected int getHeaderHeight(long pageHeight, long scrollTop) {

		int currentHeaderHeight = headerHeight;
		if (getWindowHandles().size() > 1) {
			// 複数タブが開かれている場合はタブバーも含めた高さ
			final long tabHeight = 66;
			currentHeaderHeight += tabHeight;
		} else if (scrollTop > 0) {
			// 最上部以外はヘッダを影ごと切り取る
			currentHeaderHeight += 2;
		}
		return currentHeaderHeight;
	}

	@Override
	protected int getFooterHeight(long pageHeight, long scrollTop, long windowHeight) {
		return 0;
	}

	@Override
	protected BufferedImage trimCaptureTop(double captureTop, long windowHeight, double scale, BufferedImage img) {
		// 下端の推定位置（次スクロール時にトップに来る位置）と、実際のキャプチャに写っている下端の位置を比較
		long calculatedBottomValue = Math.round((captureTop + windowHeight) * scale);
		long actualBottomValue = Math.round(captureTop * scale) + img.getHeight();
		// 余分にキャプチャに写っていたら切り取っておく
		if (calculatedBottomValue < actualBottomValue) {
			return img.getSubimage(0, 0, img.getWidth(),
					(int) (img.getHeight() - (actualBottomValue - calculatedBottomValue)));
		} else {
			return img;
		}
	}

	@Override
	protected MrtWebElement newMrtWebElement() {
		return new MrtIPadWebElement();
	}

	@Override
	protected double calcScrollIncrementWithHeader(int imageHeight, double scale) {
		double scrollIncrement = super.calcScrollIncrementWithHeader(imageHeight, scale);
		// タブがないときはヘッダの影が1px写りこむため、スクロール幅を1px少なくする
		if (getWindowHandles().size() <= 1) {
			scrollIncrement -= 1;
		}
		return scrollIncrement;
	}

	@Override
	protected int calcTrimTop(int imageNum, long windowHeight, long pageHeight, double scale) {
		// スクロール幅をずらした分、切り取り位置を調整する
		if (getWindowHandles().size() <= 1) {
			return (int) Math.round((windowHeight - (pageHeight % windowHeight) - (imageNum - 1)) * scale);
		}
		return super.calcTrimTop(imageNum, windowHeight, pageHeight, scale);
	}

}
