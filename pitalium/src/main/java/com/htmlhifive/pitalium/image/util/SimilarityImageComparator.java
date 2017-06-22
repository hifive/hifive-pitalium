/*
 * Copyright (C) 2015-2017 NS Solutions Corporation
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
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import com.htmlhifive.pitalium.image.model.ComparedRectangleArea;
import com.htmlhifive.pitalium.image.model.ImageComparedResult;
import com.htmlhifive.pitalium.image.model.Offset;
import com.htmlhifive.pitalium.image.model.SimilarityComparisonParameters;
import com.htmlhifive.pitalium.image.model.SimilarityImageComparedResult;
import com.htmlhifive.pitalium.image.model.SimilarityUnit;

/**
 * 類似度に基づき、比較の合否判定を行うComparator
 */
class SimilarityImageComparator extends ImageComparator<SimilarityComparisonParameters> {

	/**
	 * デフォルトコンストラクタ
	 */
	SimilarityImageComparator() {
	}

	/**
	 * 引数ありのコンストラクタ
	 *
	 * @param parameters 比較用パラメータ
	 */
	SimilarityImageComparator(SimilarityComparisonParameters parameters) {
		this.parameters = parameters;
	}

	@Override
	protected List<Point> compare(BufferedImage image1, BufferedImage image2, int offsetX, int offsetY) {
		return null;
	}

	@Override
	public ImageComparedResult compare(BufferedImage img1, Rectangle img1Area, BufferedImage img2, Rectangle img2Area) {
		Offset offset = new Offset(img2Area.x - img1Area.x, img2Area.y - img1Area.y);
		SimilarityUnit unit = SimilarityUtils.calcSimilarity(img1, img2, img1Area, new ComparedRectangleArea(img2Area),
				offset);

		boolean isSucceed = true;
		if (parameters.getPixleByPixelThreshold() > unit.getSimilarityPixelByPixel()) {
			isSucceed = false;
		} else if (parameters.getFeatherMatrixThreshold() > unit.getSimilarityFeatureMatrix()) {
			isSucceed = false;
		} else if (parameters.getThresDiffThreshold() > unit.getSimilarityThresDiff()) {
			isSucceed = false;
		} else if (parameters.getTotalDiffThreshold() > unit.getSimilarityTotalDiff()) {
			isSucceed = false;
		}
		return new SimilarityImageComparedResult(isSucceed, unit);
	}
}
