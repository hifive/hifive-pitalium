/*
 * Copyright (C) 2015 NS Solutions Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.htmlhifive.pitalium.core.selenium;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.WebElement;

import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.model.ScreenshotParams;

/**
 * Firefoxで利用する{@link org.openqa.selenium.WebDriver}
 */
class PtlFirefoxDriver extends PtlWebDriver {

	/**
	 * コンストラクタ
	 *
	 * @param remoteAddress RemoteWebDriverServerのアドレス
	 * @param capabilities Capability
	 */
	PtlFirefoxDriver(URL remoteAddress, PtlCapabilities capabilities) {
		super(remoteAddress, capabilities);
	}

	@Override
	protected PtlWebElement newPtlWebElement() {
		return new PtlFirefoxWebElement();
	}

	@Override
	protected void trimNonMovePadding(List<List<BufferedImage>> allTargetScreenshots,
			List<Pair<CompareTarget, ScreenshotParams>> targetParams) {
		// firefoxのtextareaは上下paddingが常に表示されるため、不要なpaddingを切り取る
		for (int i = 0; i < allTargetScreenshots.size(); i++) {
			PtlWebElement targetElement = targetParams.get(i).getRight().getTarget().getElement();
			if ("textarea".equals(targetElement.getTagName()) && targetParams.get(i).getLeft().isScrollTarget()) {
				List<BufferedImage> targetScreenshots = allTargetScreenshots.get(i);
				for (int j = 0; j < targetScreenshots.size(); j++) {
					targetScreenshots.set(j,
							trimTargetPadding(targetElement, targetScreenshots.get(j), j, targetScreenshots.size()));
				}
			}
		}
	}

	@Override
	protected void trimMovePadding(WebElement el, List<BufferedImage> images) {
		// firefoxのtextareaは上下paddingが常に表示されるため、不要なpaddingを切り取る
		if ("textarea".equals(el.getTagName())) {
			for (int i = 0; i < images.size(); i++) {
				images.set(i, trimTargetPadding(el, images.get(i), i, images.size()));
			}
		}
	}

	@Override
	protected int calcTrimTop(int imageHeight, long scrollAmount, PtlWebElement targetElement, double currentScale) {
		int trimTop = super.calcTrimTop(imageHeight, scrollAmount, targetElement, currentScale);
		// firefoxのtextareaは上下paddingが常に表示されるため、上padding分trim量を減らす
		if ("textarea".equals(targetElement.getTagName())) {
			WebElementPadding padding = targetElement.getPadding();
			trimTop -= padding.getTop();
		}
		return trimTop;
	}
}
