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
package com.htmlhifive.pitalium.image.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
import com.htmlhifive.pitalium.image.model.CompareOption;
import com.htmlhifive.pitalium.image.model.DiffPoints;

/**
 * 画像操作を行うユーティリティクラス
 */
public final class ImageUtils {

	private static final Logger LOG = LoggerFactory.getLogger(ImageUtils.class);

	private static final String DIFF_IMAGE_LEFT_LABEL = "expected";
	private static final String DIFF_IMAGE_RIGHT_LABEL = "actual";

	/**
	 * コンストラクタ
	 */
	private ImageUtils() {
	}

	/**
	 * 2つの画像を比較し、差分を取得します。
	 *
	 * @param image1 画像1
	 * @param imageArea1 画像1の比較範囲
	 * @param image2 画像2
	 * @param imageArea2 画像2の比較範囲
	 * @param options 比較オプション
	 * @return 比較結果
	 */
	public static DiffPoints compare(BufferedImage image1, Rectangle imageArea1, BufferedImage image2,
			Rectangle imageArea2, CompareOption[] options) {
		ImageComparator comparator = ImageComparatorFactory.getInstance().getImageComparator(options);
		return comparator.compare(image1, imageArea1, image2, imageArea2);
	}

	/**
	 * 全体画像の中に指定した部分画像が含まれているかどうかを取得します。
	 *
	 * @param entireImage 全体画像
	 * @param partImage 部分画像
	 * @return 全体画像の中に部分画像が含まれていればtrue、含まれていなければfalse
	 */
	public static boolean isContained(BufferedImage entireImage, BufferedImage partImage) {
		// 元画像の積分画像を作成
		double[][] integralImage = calcIntegralImage(entireImage);

		double sumContent = 0;
		Raster r = partImage.getRaster();
		int[] dArray = new int[r.getNumDataElements()];
		for (int x = 0; x < r.getWidth(); x++) {
			for (int y = 0; y < r.getHeight(); y++) {
				sumContent += r.getPixel(x, y, dArray)[0];
			}
		}

		int contentWidth = partImage.getWidth();
		int contentHeight = partImage.getHeight();
		double p0;
		double p1;
		double p2;
		double p3;
		double sumContainer;
		final int yMax = entireImage.getHeight() - partImage.getHeight() + 1;
		final int xMax = entireImage.getWidth() - partImage.getWidth() + 1;
		for (int y = 0; y < yMax; y++) {
			for (int x = 0; x < xMax; x++) {
				p0 = integralImage[y + contentHeight - 1][x + contentWidth - 1];
				p1 = (x == 0) ? 0 : integralImage[y + contentHeight - 1][x - 1];
				p2 = (y == 0) ? 0 : integralImage[y - 1][x + contentWidth - 1];
				p3 = (x == 0 || y == 0) ? 0 : integralImage[y - 1][x - 1];
				sumContainer = p0 - p1 - p2 + p3;

				if (Double.compare(sumContainer, sumContent) == 0) {
					BufferedImage window = entireImage.getSubimage(x, y, contentWidth, contentHeight);
					if (imageEquals(window, partImage)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * 元画像の積分画像を生成します。
	 *
	 * @param source 元画像
	 * @return 積分結果の配列
	 */
	private static double[][] calcIntegralImage(BufferedImage source) {
		double[][] integralImage = new double[source.getHeight()][source.getWidth()];
		Raster raster = source.getRaster();
		int[] pixel = new int[raster.getNumDataElements()];
		double leftNum;
		double upNum;
		double leftUpNum;
		for (int y = 0; y < source.getHeight(); y++) {
			for (int x = 0; x < source.getWidth(); x++) {
				leftNum = (x == 0) ? 0 : integralImage[y][x - 1];
				upNum = (y == 0) ? 0 : integralImage[y - 1][x];
				leftUpNum = (x == 0 || y == 0) ? 0 : integralImage[y - 1][x - 1];
				integralImage[y][x] = leftNum + upNum + raster.getPixel(x, y, pixel)[0] - leftUpNum;
			}
		}
		return integralImage;
	}

	/**
	 * 画像を比較し、同一であるかどうかを取得します。
	 *
	 * @param image1 一つの画像
	 * @param image2 二つ目のがぞおう
	 * @return 二つの画像が同一である場合true、異なる画像の場合false
	 */
	public static boolean imageEquals(BufferedImage image1, BufferedImage image2) {
		// サイズ不一致
		if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) {
			return false;
		}

		int width = image1.getWidth();
		int height = image1.getHeight();
		return Arrays.equals(image1.getRGB(0, 0, width, height, null, 0, width),
				image2.getRGB(0, 0, width, height, null, 0, width));
	}

	/**
	 * 指定エリアをマスクした画像を生成します。
	 *
	 * @param image マスクする画像
	 * @param maskAreas マスクするエリア
	 * @return 指定エリアをマスクした画像
	 */
	public static BufferedImage getMaskedImage(BufferedImage image, List<Rectangle> maskAreas) {
		BufferedImage img = getDeepCopyImage(image);

		Color color = Color.RED;
		Graphics grf = img.getGraphics();
		grf.setColor(color);

		for (Rectangle rect : maskAreas) {
			Point location = rect.getLocation();
			Dimension size = rect.getSize();
			grf.fillRect(location.x, location.y, size.width, size.height);
		}
		grf.dispose();

		return img;
	}

	/**
	 * 画像をDeepCopyします。
	 *
	 * @param image DeepCopy元の{@link BufferedImage}
	 * @return DeepCopyされた {@link BufferedImage}
	 */
	private static BufferedImage getDeepCopyImage(BufferedImage image) {
		ColorModel cm = image.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = image.copyData(null);
		WritableRaster rasterChild = (WritableRaster) raster.createChild(0, 0, image.getWidth(), image.getHeight(),
				image.getMinX(), image.getMinY(), null);
		return new BufferedImage(cm, rasterChild, isAlphaPremultiplied, null);
	}

	/**
	 * 指定のポイントをマークした画像を作成します。
	 *
	 * @param image 対象の画像
	 * @param diffPoints マークするポイント
	 * @return マークした画像
	 */
	public static BufferedImage getMarkedImage(BufferedImage image, DiffPoints diffPoints) {
		List<Rectangle> diffAreas = convertDiffPointsToAreas(diffPoints);

		// 異なるピクセルの左上にマーカーを置いていく
		BufferedImage mark = getMarkImage();

		// マーカーの方が範囲が大きい場合、その範囲の画像を作成する
		int markerMaxX = image.getWidth();
		int markerMaxY = image.getHeight();
		int markerMinX = 0;
		int markerMinY = 0;

		boolean extend = false;

		// マーカーの最大値を取得する
		final int imageMargin = 4;
		final int markWidth = 30;
		final int markHeight = 25;
		for (Rectangle area : diffAreas) {
			if (markerMaxX < (int) area.getMaxX() + imageMargin) {
				markerMaxX = (int) area.getMaxX() + imageMargin;
				extend = true;
			}
			if (markerMaxY < (int) area.getMaxY() + imageMargin) {
				markerMaxY = (int) area.getMaxY() + imageMargin;
				extend = true;
			}
			if (markerMinX > area.getMinX() - markWidth) {
				markerMinX = (int) area.getMinX() - markWidth;
				extend = true;
			}
			if (markerMinY > area.getMinY() - markHeight) {
				markerMinY = (int) area.getMinY() - markHeight;
				extend = true;
			}
		}

		BufferedImage markedImg;
		Graphics2D marker;
		if (extend) {
			//画像が、マーカーの範囲を超えている場合コピーする
			markedImg = new BufferedImage(markerMaxX - markerMinX, markerMaxY - markerMinY, BufferedImage.TYPE_INT_ARGB);
			marker = (Graphics2D) markedImg.getGraphics();
			marker.setBackground(Color.GRAY);
			marker.clearRect(0, 0, markerMaxX - markerMinX, markerMaxY - markerMinY);
			marker.drawImage(image, -markerMinX, -markerMinY, image.getWidth(), image.getHeight(), null);
		} else {
			markedImg = getDeepCopyImage(image);
			marker = (Graphics2D) markedImg.getGraphics();
		}

		for (Rectangle area : diffAreas) {
			marker.drawImage(mark, area.x - markWidth - markerMinX, area.y - markHeight - markerMinY, null);

			final float markerR = 1.0f;
			final float markerG = 0.0f;
			final float markerB = 0.0f;
			final float markerA = 0.5f;
			final float markeStroke = 4.0f;
			marker.setColor(new Color(markerR, markerG, markerB, markerA));
			marker.setStroke(new BasicStroke(markeStroke));
			final int markerPadding = 2;
			marker.drawRect(area.x - markerPadding - markerMinX, area.y - markerPadding - markerMinY, area.width
					+ markerPadding * 2, area.height + markerPadding * 2);
		}

		return markedImg;
	}

	/**
	 * diff座標から近似の四角形を作成する。
	 *
	 * @param diffPoints 差分データ
	 * @return 近似の四角形のリスト
	 */
	static List<Rectangle> convertDiffPointsToAreas(DiffPoints diffPoints) {
		List<Rectangle> areas = convertDiffPointsToAreas(diffPoints.getDiffPoints());
		areas.addAll(convertSizeDiffPointsToAreas(diffPoints.getSizeDiffPoints()));
		return areas;
	}

	/**
	 * diff座標から近似の四角形を作成する。
	 *
	 * @param diffPoints diff座標のリスト
	 * @return 近似の四角形のリスト
	 */
	static List<Rectangle> convertDiffPointsToAreas(List<Point> diffPoints) {
		if (diffPoints == null || diffPoints.isEmpty()) {
			return new ArrayList<Rectangle>();
		}

		int margeFlag = 0;
		List<MarkerGroup> diffGroups = new ArrayList<MarkerGroup>();

		for (Point point : diffPoints) {
			MarkerGroup markerGroup = new MarkerGroup(new Point(point.x, point.y));
			for (MarkerGroup diffGroup : diffGroups) {
				if (diffGroup.canMarge(markerGroup)) {
					diffGroup.union(markerGroup);
					margeFlag = 1;
					break;
				}
			}
			if (margeFlag != 1) {
				diffGroups.add(markerGroup);
			}
			margeFlag = 0;
		}

		// 結合が無くなるまでループする
		int num = -1;
		while (num != 0) {
			num = 0;
			for (MarkerGroup rectangleGroup : diffGroups) {
				List<MarkerGroup> removeList = new ArrayList<MarkerGroup>();
				for (MarkerGroup rectangleGroup2 : diffGroups) {
					if (!rectangleGroup.equals(rectangleGroup2) && rectangleGroup.canMarge(rectangleGroup2)) {
						rectangleGroup.union(rectangleGroup2);
						// マージが発生した場合はカウントする
						num++;
						// マージしたモデルを削除対象として記録
						removeList.add(rectangleGroup2);
					}
				}
				if (num > 0) {
					// 削除対象がある場合は、リストから取り除く
					for (MarkerGroup removeModel : removeList) {
						diffGroups.remove(removeModel);
					}
					break;
				}
			}
		}

		// diffGroupsからRectangleのリストを作成
		List<Rectangle> rectangles = new ArrayList<Rectangle>();

		for (MarkerGroup markerGroup : diffGroups) {
			rectangles.add(markerGroup.getRectangle());
		}
		return rectangles;
	}

	/**
	 * サイズのdiff座標から近似の四角形を作成する。
	 *
	 * @param sizeDiffPoints サイズのdiff座標のリスト
	 * @return 近似の四角形のリスト
	 */
	static List<Rectangle> convertSizeDiffPointsToAreas(List<Point> sizeDiffPoints) {
		List<Rectangle> areas = new ArrayList<Rectangle>();
		if (sizeDiffPoints == null || sizeDiffPoints.isEmpty()) {
			return areas;
		}

		Point start = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
		Point end = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);

		for (Point point : sizeDiffPoints) {
			if (point.y == 0 && start.x > point.x) {
				start.x = point.x;
			}
			if (point.x == 0 && start.y > point.y) {
				start.y = point.y;
			}
			if (end.x < point.x) {
				end.x = point.x;
			}
			if (end.y < point.y) {
				end.y = point.y;
			}
		}

		if (start.x <= end.x) {
			areas.add(new Rectangle(start.x, 0, end.x - start.x, end.y));
		}

		if (start.y <= end.y) {
			final int rectMargin = 3;
			areas.add(new Rectangle(-rectMargin, start.y - rectMargin, end.x + rectMargin * 2, end.y - start.y
					+ rectMargin * 2));
		}

		return areas;
	}

	/**
	 * マーカー画像を取得します。
	 *
	 * @return マーカー画像
	 */
	private static BufferedImage getMarkImage() {
		URL resource = ImageUtils.class.getClassLoader().getResource("mark.png");
		if (resource == null) {
			LOG.error("mark.png is not exists.");
			throw new TestRuntimeException("mark.png is not exists.");
		}

		try {
			return ImageIO.read(resource);
		} catch (IOException e) {
			throw new TestRuntimeException(e);
		}
	}

	/**
	 * 二つの画像と差分情報から差分確認用画像を取得します。
	 *
	 * @param leftImage 左側の画像
	 * @param rightImage 右側の画像
	 * @param diffPoints 差分データ
	 * @return 差分確認用画像
	 */
	public static BufferedImage getDiffImage(BufferedImage leftImage, BufferedImage rightImage, DiffPoints diffPoints) {
		return new DiffImageMaker(leftImage, rightImage, diffPoints, DIFF_IMAGE_LEFT_LABEL, DIFF_IMAGE_RIGHT_LABEL)
				.execute();
	}

	/**
	 * 画像を指定の値でトリムします。
	 *
	 * @param image 元画像
	 * @param trimTop 上方向のトリム値
	 * @param trimLeft 左方向のトリム値
	 * @param trimBottom 下方向のトリム値
	 * @param trimRight 右方向のトリム値
	 * @return トリム済の画像
	 */
	public static BufferedImage trim(BufferedImage image, int trimTop, int trimLeft, int trimBottom, int trimRight) {
		int width = image.getWidth();
		int height = image.getHeight();
		return image.getSubimage(trimLeft, trimTop, width - trimLeft - trimRight, height - trimTop - trimBottom);
	}

	/**
	 * 画像を縦に結合し、1枚の画像にします。
	 *
	 * @param images 結合前の画像群
	 * @return 結合後の画像
	 */
	public static BufferedImage vertialMerge(List<BufferedImage> images) {
		// 結合後の画像サイズを調べる
		int totalHeight = 0;
		int totalWidth = -1;
		for (BufferedImage image : images) {
			totalHeight += image.getHeight();
			if (totalWidth < 0) {
				totalWidth = image.getWidth();
			}
		}

		// 画像の結合
		BufferedImage screenshot = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = screenshot.getGraphics();
		int nextTop = 0;
		for (BufferedImage image : images) {
			graphics.drawImage(image, 0, nextTop, null);
			nextTop += image.getHeight();
		}

		return screenshot;
	}

	/**
	 * 画像を結合し、1枚の画像にします。
	 *
	 * @param images 結合前の画像群
	 * @return 結合後の画像
	 */
	public static BufferedImage merge(List<List<BufferedImage>> images) {
		// 結合後の画像サイズを調べる
		int totalHeight = 0;
		int totalWidth = -1;
		for (List<BufferedImage> lineImages : images) {
			totalHeight += lineImages.get(0).getHeight();
			if (totalWidth < 0) {
				int width = 0;
				for (BufferedImage image : lineImages) {
					width += image.getWidth();
				}
				totalWidth = width;
			}
		}

		// 画像の結合
		BufferedImage screenshot = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = screenshot.getGraphics();
		int nextTop = 0;
		for (List<BufferedImage> lineImage : images) {
			int imgHeight = -1;
			int nextLeft = 0;
			for (BufferedImage img : lineImage) {
				graphics.drawImage(img, nextLeft, nextTop, null);
				nextLeft += img.getWidth();
				if (imgHeight < 0) {
					imgHeight = img.getHeight();
				}
			}
			nextTop += imgHeight;
		}

		return screenshot;
	}
}
