/*
 * Copyright (C) 2015-2016 NS Solutions Corporation
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
package com.htmlhifive.pitalium.image.util;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htmlhifive.pitalium.image.model.ComparisonParameters;

/**
 * 通常の方法で画像比較
 */
class DefaultImageComparator extends ImageComparator<ComparisonParameters> {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultImageComparator.class);

	/**
	 * コンストラクタ
	 */
	DefaultImageComparator() {
	}

	@Override
	protected List<Point> compare(BufferedImage image1, BufferedImage image2, int offsetX, int offsetY) {
		LOG.trace("[Compare] image1[w: {}, h: {}], image2[w: {}, h: {}], offset: ({}, {})", image1.getWidth(),
				image1.getHeight(), image2.getWidth(), image2.getHeight(), offsetX, offsetY);
		int width = Math.min(image1.getWidth(), image2.getWidth());
		int height = Math.min(image1.getHeight(), image2.getHeight());

		int[] rgb1 = ImageUtils.getRGB(image1, width, height);
		int[] rgb2 = ImageUtils.getRGB(image2, width, height);

		List<Point> diffPoints = new ArrayList<Point>();
		for (int i = 0, length = rgb1.length; i < length; i++) {
			if (rgb1[i] != rgb2[i]) {
				int x = (i % width) + offsetX;
				int y = (i / width) + offsetY;

				Point diffPoint = new Point(x, y);
				diffPoints.add(diffPoint);
				LOG.trace("[Compare] Diff found ({}, {}). #{} <=> #{}", diffPoint.x, diffPoint.y,
						Integer.toHexString(rgb1[i]), Integer.toHexString(rgb2[i]));
			}
		}

		if (!diffPoints.isEmpty()) {
			LOG.debug("[Compare] {} diff found.", diffPoints.size());
		}
		return diffPoints;
	}

}
