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

import com.htmlhifive.pitalium.image.model.CategoryComparisonParameters;
import com.htmlhifive.pitalium.image.model.ComparedRectangleArea;
import com.htmlhifive.pitalium.image.model.DiffCategory;
import com.htmlhifive.pitalium.image.model.ImageComparedResult;

/**
 * カテゴリ分類に基づく、比較の実行を行うComparator
 */
class CategoryImageComparator extends ImageComparator<CategoryComparisonParameters> {

	/**
	 * デフォルトコンストラクタ
	 */
	CategoryImageComparator() {
	}

	/**
	 * 引数ありのコンストラクタ
	 *
	 * @param parameters 比較用パラメータ
	 */
	CategoryImageComparator(CategoryComparisonParameters parameters) {
		this.parameters = parameters;
	}

	@Override
	protected List<Point> compare(BufferedImage image1, BufferedImage image2, int offsetX, int offsetY) {
		return null;
	}

	@Override
	public ImageComparedResult compare(BufferedImage img1, Rectangle img1Area, BufferedImage img2, Rectangle img2Area) {
		ImagePair pair = new ImagePair(cropSubImage(img1, img1Area), cropSubImage(img2, img2Area));
		pair.prepare();
		pair.doCategorize();

		List<ComparedRectangleArea> comparedRectangles = pair.getComparedRectangles();

		boolean isSucceed = true;
		for (ComparedRectangleArea rect : comparedRectangles) {
			DiffCategory type = rect.getCategory();
			if (!parameters.isAccept(type)) {
				isSucceed = false;
				break;
			}
		}

		return new CategoryImageComparedResult(isSucceed, comparedRectangles);
	}

	private BufferedImage cropSubImage(BufferedImage img, Rectangle area) {
		return img.getSubimage(area.x, area.y, area.width, area.height);
	}
}
