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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

import com.htmlhifive.pitalium.image.model.DiffPoints;

/**
 * 比較結果から、差分を表示する画像を作成するクラス。2枚の画像を左右に並べ、差異がある部分にマークした画像が出力されます。
 */
public class DiffImageMaker {

	private static final String DEFAULT_LEFT_IMAGE_LABEL = "expected";
	private static final String DEFAULT_RIGHT_IMAGE_LABEL = "actual";

	private final BufferedImage leftImage;
	private final BufferedImage rightImage;
	private final DiffPoints diffPoints;
	private String leftImageLabel = DEFAULT_LEFT_IMAGE_LABEL;
	private String rightImageLabel = DEFAULT_RIGHT_IMAGE_LABEL;

	/**
	 * 2枚の画像と画像間の差分を指定してImageMakerを生成します。
	 * 
	 * @param leftImage 左側の画像
	 * @param rightImage 右側の画像
	 * @param diffPoints 差分データ
	 */
	public DiffImageMaker(BufferedImage leftImage, BufferedImage rightImage, DiffPoints diffPoints) {
		this.leftImage = leftImage;
		this.rightImage = rightImage;
		this.diffPoints = diffPoints;
	}

	/**
	 * 2枚の画像と画像間の差分を指定してImageMakerを生成します。
	 * 
	 * @param leftImage 左側の画像
	 * @param rightImage 右側の画像
	 * @param diffPoints 差分データ
	 * @param leftImageLabel 左画像のラベル。左側の画像の上に表示されます。
	 * @param rightImageLabel 右画像のラベル。右側の画像の上に表示されます。
	 */
	public DiffImageMaker(BufferedImage leftImage, BufferedImage rightImage, DiffPoints diffPoints,
			String leftImageLabel, String rightImageLabel) {
		this.leftImage = leftImage;
		this.rightImage = rightImage;
		this.diffPoints = diffPoints;
		this.leftImageLabel = leftImageLabel;
		this.rightImageLabel = rightImageLabel;
	}

	//<editor-fold desc="Getter/Setter">

	/**
	 * 左側の画像を取得します。
	 * 
	 * @return 左側の画像
	 */
	public BufferedImage getLeftImage() {
		return leftImage;
	}

	/**
	 * 右側の画像を取得します。
	 * 
	 * @return 右側の画像
	 */
	public BufferedImage getRightImage() {
		return rightImage;
	}

	/**
	 * 差分データを取得します。
	 * 
	 * @return 差分データ
	 */
	public DiffPoints getDiffPoints() {
		return diffPoints;
	}

	/**
	 * 左画像のラベルを取得します。
	 * 
	 * @return 左画像のラベル
	 */
	public String getLeftImageLabel() {
		return leftImageLabel;
	}

	/**
	 * 左画像のラベルを設定します。
	 * 
	 * @param leftImageLabel 左画像のラベル
	 */
	public void setLeftImageLabel(String leftImageLabel) {
		this.leftImageLabel = leftImageLabel;
	}

	/**
	 * 右画像のラベルを取得します。
	 * 
	 * @return 右画像のラベル
	 */
	public String getRightImageLabel() {
		return rightImageLabel;
	}

	/**
	 * 右画像のラベルを設定します。
	 * 
	 * @param rightImageLabel 右画像のラベル
	 */
	public void setRightImageLabel(String rightImageLabel) {
		this.rightImageLabel = rightImageLabel;
	}

	//</editor-fold>

	/**
	 * 差分を表示する画像を作成し、{@link BufferedImage}として返します。
	 * 
	 * @return 差分画像
	 */
	public BufferedImage execute() {
		List<Point> diffPointList = diffPoints.getDiffPoints();
		List<Point> sizeDiffPointList = diffPoints.getSizeDiffPoints();

		if (sizeDiffPointList == null || (diffPointList != null && diffPointList.isEmpty())) {
			if (sizeDiffPointList == null || sizeDiffPointList.isEmpty()) {
				return null;
			}
		}

		// Diff画像の生成
		BufferedImage expectedBaseImage = ImageUtils.getMarkedImage(leftImage, diffPoints);
		final int border = 1;
		int expectedImageWidth = expectedBaseImage.getWidth() + border * 2;
		BufferedImage actualBaseImage = ImageUtils.getMarkedImage(rightImage, diffPoints);
		int actualImageWidth = actualBaseImage.getWidth() + border * 2;
		int diffImageWidth = expectedImageWidth + actualImageWidth;

		int baseImageHeight = ((expectedBaseImage.getHeight() >= actualBaseImage.getHeight()) ? expectedBaseImage
				.getHeight() : actualBaseImage.getHeight()) + border * 2;
		final int statusHeight = 50; // ラベルを表示する領域の高さ
		int diffImageHeight = baseImageHeight + statusHeight;
		BufferedImage diffImage = new BufferedImage(diffImageWidth, diffImageHeight, 1);
		Graphics2D g = (Graphics2D) diffImage.getGraphics();

		// 左右の差分画像（グレースケール）の描画
		g.drawImage(expectedBaseImage, 1, statusHeight + 1, null);
		g.drawImage(actualBaseImage, expectedImageWidth + border, statusHeight + 1, null);

		// expected（左）のラベル領域と枠の描画
		final float leftR = 0.2f;
		final float leftG = 0.5f;
		final float leftB = 1.0f;
		final float leftA = 1.0f;
		g.setColor(new Color(leftR, leftG, leftB, leftA)); // 青
		g.fillRect(0, 0, expectedImageWidth + 1, statusHeight + 1);
		g.drawRect(0, statusHeight, expectedBaseImage.getWidth() + border, diffImageHeight + 1);

		// actual（右）のラベル領域と枠の描画
		final float rightR = 0.8f;
		final float rightG = 0.2f;
		final float rightB = 0.2f;
		final float rightA = 1.0f;
		g.setColor(new Color(rightR, rightG, rightB, rightA)); // 赤
		g.fillRect(expectedImageWidth, 0, actualImageWidth + 1, statusHeight + 1);
		g.drawRect(expectedImageWidth, statusHeight, actualBaseImage.getWidth() + 1, diffImageHeight + 1);

		// ラベル文字の描画
		final int fontSize = 25;
		final int centeringWidth = 80;
		final int centeringHeight = 35;
		g.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
		g.setFont(new Font("Arial", Font.BOLD, fontSize));
		g.drawString(leftImageLabel, expectedBaseImage.getWidth() / 2 - centeringWidth, centeringHeight);
		g.drawString(rightImageLabel, expectedImageWidth + actualImageWidth / 2 - centeringWidth, centeringHeight);

		return diffImage;
	}

}
