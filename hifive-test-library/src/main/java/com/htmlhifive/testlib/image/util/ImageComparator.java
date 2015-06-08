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
package com.htmlhifive.testlib.image.util;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htmlhifive.testlib.common.exception.TestRuntimeException;
import com.htmlhifive.testlib.image.model.DiffPoints;

/**
 * 画像の比較処理を行うComparatorの抽象クラス。このクラスを拡張して、比較方法毎にComparatorを実装します。
 */
public abstract class ImageComparator {

	private static final Logger LOG = LoggerFactory.getLogger(ImageComparator.class);

	/**
	 * コンストラクタ
	 */
	protected ImageComparator() {
	}

	/**
	 * 2枚の画像を比較し、差分の一覧を取得します。
	 * 
	 * @param img1 画像1
	 * @param img1Area 画像1で比較の対象とする範囲
	 * @param img2 画像2
	 * @param img2Area 画像2で比較の対象とする範囲
	 * @return 比較結果の差分データ
	 */
	public DiffPoints compare(BufferedImage img1, Rectangle img1Area, BufferedImage img2, Rectangle img2Area) {
		if (img1 == null || img2 == null) {
			throw new TestRuntimeException("Both img1 and img2 is required.");
		}

		int offsetX = 0;
		int offsetY = 0;
		BufferedImage image1 = null;
		BufferedImage image2 = null;
		if (img1Area != null) {
			image1 = getSubImage(img1, img1Area);
			offsetX = (int) img1Area.getX();
			offsetY = (int) img1Area.getY();
		} else {
			image1 = img1;
		}
		if (img2Area != null) {
			image2 = getSubImage(img2, img2Area);
		} else {
			image2 = img2;
		}

		LOG.debug("offsetX: {}; offsetY: {};", offsetX, offsetY);

		List<Point> sizeDiffPoints = createSizeDiffPoints(image1, image2, offsetX, offsetY);
		List<Point> diffPoints = compare(image1, image2, offsetX, offsetY);
		return new DiffPoints(diffPoints, sizeDiffPoints);
	}

	/**
	 * 画像サイズを比較し、差分を検出した座標の一覧を取得します。
	 * 
	 * @param img1 画像１
	 * @param img2 画像２
	 * @param offsetX 画像１の元画像からのX方向オフセット値
	 * @param offsetY 画像１の元画像からのY方向オフセット値
	 * @return 検出された差分座標一覧
	 */
	protected List<Point> createSizeDiffPoints(BufferedImage img1, BufferedImage img2, int offsetX, int offsetY) {
		int width1 = img1.getWidth();
		int height1 = img1.getHeight();
		int width2 = img2.getWidth();
		int height2 = img2.getHeight();

		if (width1 == width2 && height1 == height2) {
			LOG.debug("No size diff points.");
			return new ArrayList<Point>();
		}

		int startX;
		int endX;
		int startY;
		int endY;

		if (width1 > width2) {
			startX = offsetX + width2 + 1;
			endX = offsetX + width1;
		} else if (width1 < width2) {
			startX = offsetX + width1 + 1;
			endX = offsetX + width2;
		} else {
			startX = offsetX + width1;
			endX = offsetX + width1;
		}

		if (height1 > height2) {
			startY = offsetY + height2 + 1;
			endY = offsetY + height1;
		} else if (height1 < height2) {
			startY = offsetY + height1 + 1;
			endY = offsetY + height2;
		} else {
			startY = offsetY + height1;
			endY = offsetY + height1;
		}

		List<Point> diffPoints = new ArrayList<Point>();
		if (width1 != width2) {
			for (int i = startX; i <= endX; i++) {
				for (int j = 0; j < endY; j++) {
					Point diffPoint = new Point(i, j);
					diffPoints.add(diffPoint);
				}
			}
		}

		if (height1 != height2) {
			for (int i = startY; i <= endY; i++) {
				for (int j = 0; j < startX; j++) {
					Point diffPoint = new Point(j, i);
					diffPoints.add(diffPoint);
				}
			}
		}

		LOG.debug("Size diff points: {}; startX: {}; endX: {}; startY: {}; endY: {};", diffPoints.size(), startX, endX,
				startY, endY);
		return diffPoints;
	}

	/**
	 * 画像を比較し、差分を検出した座標の一覧を取得します。
	 * 
	 * @param image1 画像１
	 * @param image2 画像２
	 * @param offsetX 画像１の元画像からのX方向オフセット値
	 * @param offsetY 画像１の元画像からのY方向オフセット値
	 * @return 検出された差分座標一覧
	 */
	protected abstract List<Point> compare(BufferedImage image1, BufferedImage image2, int offsetX, int offsetY);

	/**
	 * 指定エリアで切り出した画像を取得します。
	 * 
	 * @param image 元画像
	 * @param area 切り出すエリア
	 * @return 切り出した画像
	 */
	protected final BufferedImage getSubImage(BufferedImage image, Rectangle area) {
		return image.getSubimage((int) area.getX(), (int) area.getY(), (int) area.getWidth(), (int) area.getHeight());
	}
}
