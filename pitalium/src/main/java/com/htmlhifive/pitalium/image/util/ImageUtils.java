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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
import com.htmlhifive.pitalium.image.model.CompareOption;
import com.htmlhifive.pitalium.image.model.ComparisonParameters;
import com.htmlhifive.pitalium.image.model.DiffPoints;
import com.htmlhifive.pitalium.image.model.ObjectGroup;
import com.htmlhifive.pitalium.image.model.Offset;

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
	public static double[][] calcIntegralImage(BufferedImage source) {
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
		return Arrays.equals(getRGB(image1, width, height), getRGB(image2, width, height));
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
			markedImg = new BufferedImage(markerMaxX - markerMinX, markerMaxY - markerMinY,
					BufferedImage.TYPE_INT_ARGB);
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
			marker.drawRect(area.x - markerPadding - markerMinX, area.y - markerPadding - markerMinY,
					area.width + markerPadding * 2, area.height + markerPadding * 2);
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
			areas.add(new Rectangle(-rectMargin, start.y - rectMargin, end.x + rectMargin * 2,
					end.y - start.y + rectMargin * 2));
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
		LOG.trace(
				"(Trim) image(top: {}, left: {}, bottom: {}, right; {}). [w: {}; h: {}] -> [x: {}; y: {}; w: {}; h: {}].",
				trimTop, trimLeft, trimBottom, trimRight, width, height, trimLeft, trimTop,
				width - trimLeft - trimRight, height - trimTop - trimBottom);
		return image.getSubimage(trimLeft, trimTop, width - trimLeft - trimRight, height - trimTop - trimBottom);
	}

	/**
	 * 画像を縦に結合し、1枚の画像にします。
	 *
	 * @param images 結合前の画像群
	 * @return 結合後の画像
	 */
	public static BufferedImage verticalMerge(List<BufferedImage> images) {
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
		LOG.trace("(VerticalMerge) new image[{}, {}]", totalWidth, totalHeight);
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
		LOG.trace("(Merge) new image[{}, {}]", totalWidth, totalHeight);
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

	/**
	 * 指定した画像のRGBベースのピクセル配列を取得します。
	 *
	 * @param image 対象の画像
	 * @param width 読み込む幅
	 * @param height 読み込む高さ
	 * @return ピクセル配列
	 */
	public static int[] getRGB(BufferedImage image, int width, int height) {
		return image.getRGB(0, 0, width, height, null, 0, width);
	}

	/**
	 * integer pixel value to aRGB array
	 *
	 * @param pixel
	 * @return aRGB value array
	 */
	public static int[] toARGB(int pixel) {
		int alpha = (pixel >> 24) & 0xff;
		int red = (pixel >> 16) & 0xff;
		int green = (pixel >> 8) & 0xff;
		int blue = (pixel) & 0xff;
		return new int[] { alpha, red, green, blue };
	}

	/**
	 * RGB array to integer pixel value
	 *
	 * @param argb
	 * @return integer pixel value
	 */
	public static int toPixel(int[] argb) {
		int ret = 0;
		ret |= (argb[0] << 24);
		ret |= (argb[1] << 16);
		ret |= (argb[2] << 8);
		ret |= (argb[3]);
		return ret;
	}

	/**
	 * RGB to integer pixel value (alpha is set as 255)
	 *
	 * @param r
	 * @param g
	 * @param b
	 * @return integer pixel value
	 */
	public static int toPixel(int r, int g, int b) {
		return toPixel(new int[] { 255, r, g, b });
	}

	/**
	 * aRGB to integer pixel value
	 *
	 * @param a
	 * @param r
	 * @param g
	 * @param b
	 * @return integer pixel value
	 */
	public static int toPixel(int a, int r, int g, int b) {
		return toPixel(new int[] { a, r, g, b });
	}

	/**
	 * get color - count map of image
	 *
	 * @param source
	 * @return map whose key is color and value is count
	 */
	private static Map<Integer, Integer> colorCountMap(BufferedImage source) {
		Map<Integer, Integer> m = new HashMap<Integer, Integer>();
		for (int y = 0; y < source.getHeight(); y++) {
			for (int x = 0; x < source.getWidth(); x++) {
				if (m.containsKey(source.getRGB(x, y))) {
					m.put(source.getRGB(x, y), m.get(source.getRGB(x, y)) + 1);
				} else {
					m.put(source.getRGB(x, y), 1);
				}
			}
		}
		return m;
	}

	/**
	 * get estimated background color
	 *
	 * @param b
	 * @return most frequent color.
	 */
	public static int detectBackgroundColor(BufferedImage b) {
		Map<Integer, Integer> map = colorCountMap(b);
		int max = 0;
		int max_color = toPixel(255, 255, 255);
		for (Integer color : map.keySet()) {
			if (map.get(color) > max) {
				max = map.get(color);
				max_color = color;
			}
		}
		return max_color;
	}

	/**
	 * find object rectangle in given rectangle area if find, replace given rectangle object with object rectangle
	 *
	 * @param image
	 * @param rectangle
	 * @return true if finding object rectangle succeeds
	 */
	public static boolean getObjectRectangle(BufferedImage image, Rectangle rectangle) {

		int maxMargin = ComparisonParameters.getDefaultGroupDistance();
		int x = (int) rectangle.getX(), y = (int) rectangle.getY(), w = (int) rectangle.getWidth(),
				h = (int) rectangle.getHeight();
		BufferedImage subImage = image.getSubimage(x, y, w, h);

		// if rectangle is not big enough, do not find object rectangle
		// use 3*maxMargin here, in order to assume that object is bigger than margin by margin
		int width = w - 2, height = h - 2; //	width and height of edge map
		if (width <= 3 * maxMargin || height <= 3 * maxMargin) {
			return false;
		}

		// initialize edge map using 8-directional independent edge detection
		boolean[][] edgeMap = new boolean[height][width];
		for (int i = 0; i < maxMargin; i++) {
			// on the corner areas using diagonal filter.
			for (int j = 0; j < maxMargin; j++) {
				// set true if different
				edgeMap[i][j] = subImage.getRGB(j + 1, i + 1) != subImage.getRGB(j, i); // NW corner
				edgeMap[i][width - 1 - j] = subImage.getRGB(width - j, i + 1) != subImage.getRGB(width - j + 1, i); // NE corner
				edgeMap[height - 1 - i][j] = subImage.getRGB(j + 1, height - i) != subImage.getRGB(j, height - i + 1); // SW corner
				edgeMap[height - 1 - i][width - 1 - j] = subImage.getRGB(width - j, height - i) != subImage
						.getRGB(width - j + 1, height - i + 1); // SE corner
			}

			// on the top and bottom bridge areas using vertical filter.
			for (int j = maxMargin; j < width - maxMargin; j++) {
				edgeMap[i][j] = subImage.getRGB(j + 1, i + 1) != subImage.getRGB(j + 1, i); // top bridge area
				edgeMap[height - 1 - i][j] = subImage.getRGB(j + 1, height - i) != subImage.getRGB(j + 1,
						height - i + 1); // bottom bridge area
			}

			// on the left and right bridge areas using horizontal filter.
			for (int j = maxMargin; j < height - maxMargin; j++) {
				edgeMap[j][i] = subImage.getRGB(i + 1, j + 1) != subImage.getRGB(i, j + 1); // left bridge area
				edgeMap[j][width - 1 - i] = subImage.getRGB(width - i, j + 1) != subImage.getRGB(width - i + 1, j + 1); // right bridge area
			}
		}

		// shrink edgeMap down to (2*maxMargin+1) by (2*maxMargin+1) by using these bridges.
		int shrinkLength = 2 * maxMargin + 1;
		boolean[][] shrinkMap = new boolean[shrinkLength][shrinkLength],
				verticalMap = new boolean[shrinkLength][shrinkLength], // to check vertical direction
				horizontalMap = new boolean[shrinkLength][shrinkLength]; // to check horizontal direction

		// initialize shrink map
		for (int i = 0; i < maxMargin; i++) {
			for (int j = 0; j < maxMargin; j++) {
				shrinkMap[i][j] = edgeMap[i][j];
				shrinkMap[i][shrinkLength - 1 - j] = edgeMap[i][width - 1 - j];
				shrinkMap[shrinkLength - 1 - i][j] = edgeMap[height - 1 - i][j];
				shrinkMap[shrinkLength - 1 - i][shrinkLength - 1 - j] = edgeMap[height - 1 - i][width - 1 - j];
			}
		}
		shrinkMap[maxMargin][maxMargin] = false; // center value never used.

		// check if bridge exists
		for (int i = 0; i < maxMargin; i++) {
			int leftIdx = maxMargin, rightIdx = maxMargin, topIdx = maxMargin, bottomIdx = maxMargin;

			// check left & right bridges
			while (leftIdx < height - maxMargin && edgeMap[leftIdx][i]) {
				leftIdx++;
			}
			shrinkMap[maxMargin][i] = (leftIdx == height - maxMargin);
			while (rightIdx < height - maxMargin && edgeMap[rightIdx][width - 1 - i]) {
				rightIdx++;
			}
			shrinkMap[maxMargin][shrinkLength - 1 - i] = (rightIdx == height - maxMargin);

			// check top & bottom bridges
			while (topIdx < width - maxMargin && edgeMap[i][topIdx]) {
				topIdx++;
			}
			shrinkMap[i][maxMargin] = (topIdx == width - maxMargin);
			while (bottomIdx < width - maxMargin && edgeMap[height - 1 - i][bottomIdx]) {
				bottomIdx++;
			}
			shrinkMap[shrinkLength - 1 - i][maxMargin] = (bottomIdx == width - maxMargin);
		}

		// initialize expansion map
		for (int i = 0; i < shrinkLength; i++) {
			for (int j = 0; j < shrinkLength; j++) {
				verticalMap[i][j] = shrinkMap[i][j];
				horizontalMap[i][j] = shrinkMap[i][j];
			}
		}

		// expand continuous points from bridge
		for (int i = 0; i < maxMargin; i++) {
			for (int j = 1; j <= maxMargin; j++) {
				// expand left
				verticalMap[maxMargin - j][i] &= verticalMap[maxMargin - j + 1][i];
				verticalMap[maxMargin + j][i] &= verticalMap[maxMargin + j - 1][i];
				// expand right
				verticalMap[maxMargin - j][shrinkLength - 1 - i] &= verticalMap[maxMargin - j + 1][shrinkLength - 1
						- i];
				verticalMap[maxMargin + j][shrinkLength - 1 - i] &= verticalMap[maxMargin + j - 1][shrinkLength - 1
						- i];
				// expand top
				horizontalMap[i][maxMargin - j] &= horizontalMap[i][maxMargin - j + 1];
				horizontalMap[i][maxMargin + j] &= horizontalMap[i][maxMargin + j - 1];
				// expand bottom
				horizontalMap[shrinkLength - 1 - i][maxMargin - j] &= horizontalMap[shrinkLength - 1 - i][maxMargin - j
						+ 1];
				horizontalMap[shrinkLength - 1 - i][maxMargin + j] &= horizontalMap[shrinkLength - 1 - i][maxMargin + j
						- 1];
			}
		}

		// find points continuous to bridge along both vertical and horizontal directions
		for (int i = 0; i < shrinkLength; i++) {
			for (int j = 0; j < shrinkLength; j++) {
				shrinkMap[i][j] = verticalMap[i][j] & horizontalMap[i][j];
			}
		}

		// find any rectangle
		int top, bottom, left, right; // how may pixels apart from top, bottom, left, right
		for (top = 0; top < maxMargin; top++) {
			if (!shrinkMap[top][maxMargin])
				continue;
			for (left = 0; left < maxMargin; left++) {
				if (!shrinkMap[top][left])
					continue;
				for (bottom = 0; bottom < maxMargin; bottom++) {
					if (!shrinkMap[shrinkLength - 1 - bottom][left])
						continue;
					for (right = 0; right < maxMargin; right++) {
						if (shrinkMap[top][shrinkLength - 1 - right]
								&& shrinkMap[shrinkLength - 1 - bottom][shrinkLength - 1 - right]) {
							width -= left + right;
							height -= top + bottom;
							x += left;
							y += top;

							// replace the given rectangle with object rectangle
							rectangle.setBounds(x, y, width, height);
							return true;
						}

					}
				}
			}
		}

		return false;
	}

	/**
	 * L2 norm between two RGB pixel values
	 *
	 * @param pixel1
	 * @param pixel2
	 * @return d(R)^2 + d(G)^2 + d(B)^2
	 */
	public static double norm(int pixel1, int pixel2) {
		int red1 = (pixel1 >> 16) & 0xff;
		int green1 = (pixel1 >> 8) & 0xff;
		int blue1 = (pixel1) & 0xff;
		int red2 = (pixel2 >> 16) & 0xff;
		int green2 = (pixel2 >> 8) & 0xff;
		int blue2 = (pixel2) & 0xff;
		int redDist = (red1 - red2) * (red1 - red2);
		int greenDist = (green1 - green2) * (green1 - green2);
		int blueDist = (blue1 - blue2) * (blue1 - blue2);
		return redDist + greenDist + blueDist;
	}

	/**
	 * calculate the features of sub-pixel rendered text image
	 *
	 * @param bimage
	 * @return smoothly rendered edges rate over all colored chunk and number of chunks per line.
	 */
	public static double[] countSubpixel(BufferedImage bimage) {
		int width = bimage.getWidth();
		int height = bimage.getHeight();

		int bgColor = detectBackgroundColor(bimage);

		int count = 0;
		int total = 0;

		boolean wasBackground = true;
		boolean inside = false;

		int[] previous = null;
		int[] countPerLine = new int[height];

		for (int y = 0; y < height; y++) {
			int numberOfUpdown = 0;
			for (int x = 0; x < width; x++) {
				int pixel = bimage.getRGB(x, y);
				int[] argb = toARGB(pixel);
				if (norm(pixel, bgColor) < 25) {
					if (!wasBackground) {
						if (previous[1] > previous[2] || previous[2] > previous[3] || previous[1] == previous[3]) { // in-correct
						} else { // correct
							if (inside) {
								count++;
							}
						}
						inside = false;
						total++;
					}
					numberOfUpdown++;
					wasBackground = true;
				} else {
					if (wasBackground) {
						if (argb[1] < argb[2] || argb[2] < argb[3] || argb[1] == argb[3]) { // in-correct
						} else { // correct
							inside = true;
						}
					}
					wasBackground = false;
				}
				previous = argb;
			}
			countPerLine[y] = numberOfUpdown;
		}
		double rate = (double) count / total;

		double avg = 0;
		int countValid = 0;
		for (int i = 0; i < countPerLine.length; i++) {
			avg += (double) countPerLine[i];
			if (countPerLine[i] != 0) {
				countValid++;
			}
		}
		avg /= (double) countValid;
		return new double[] { rate, avg };
	}

	/**
	 * remove redundant rectangles. Each of them may occur raster error, or has smaller length than minLength.
	 *
	 * @param rectangles list of rectangles
	 * @param xLimit limit of x+width of given rectangle
	 * @param yLimit limit of y+height of given rectangle
	 */
	public static void removeRedundantRectangles(List<Rectangle> rectangles, int xLimit, int yLimit) {
		int minLength = 1;
		List<Rectangle> removeList = new ArrayList<Rectangle>();
		for (Rectangle rectangle : rectangles) {
			reshapeRect(rectangle, xLimit, yLimit);
			if (rectangle.getX() >= (xLimit - minLength) || rectangle.getY() >= (yLimit - minLength)
					|| rectangle.getWidth() < minLength || rectangle.getHeight() < minLength) {
				removeList.add(rectangle);
			}
		}

		// remove recorded rectangles
		for (Rectangle removeRect : removeList) {
			rectangles.remove(removeRect);
		}
	}

	/**
	 * remove overlapping rectangles for better UI
	 *
	 * @param rectangles the list of rectangles which will be checker overlapping
	 */
	public static void removeOverlappingRectangles(List<Rectangle> rectangles) {
		// ignore small difference
		int smallDiff = 2;
		List<Rectangle> removeList = new ArrayList<Rectangle>();

		// check containing relation and record what to remove
		for (int i = 0; i < rectangles.size(); i++) {
			Rectangle rect1 = rectangles.get(i);
			int xLeft1 = (int) rect1.getX(), xRight1 = (int) (rect1.getX() + rect1.getWidth());
			int yTop1 = (int) rect1.getY(), yBottom1 = (int) (rect1.getY() + rect1.getHeight());

			for (int j = i + 1; j < rectangles.size(); j++) {
				Rectangle rect2 = rectangles.get(j);
				int xLeft2 = (int) rect2.getX(), xRight2 = (int) (rect2.getX() + rect2.getWidth());
				int yTop2 = (int) rect2.getY(), yBottom2 = (int) (rect2.getY() + rect2.getHeight());

				// check rect1 contains rect2
				if (xLeft1 - smallDiff <= xLeft2 && yTop1 - smallDiff <= yTop2 && xRight1 + smallDiff >= xRight2
						&& yBottom1 + smallDiff >= yBottom2) {
					removeList.add(rect2);
				}

				// check rect2 contains rect1
				else if (xLeft2 - smallDiff <= xLeft1 && yTop2 - smallDiff <= yTop1 && xRight2 + smallDiff >= xRight1
						&& yBottom2 + smallDiff >= yBottom1) {
					removeList.add(rect1);
				}
			}
		}

		// remove recorded rectangles
		for (Rectangle removeRect : removeList) {
			rectangles.remove(removeRect);
		}
	}

	/**
	 * if the given rectangle may occur raster error, reshape it
	 *
	 * @param rectangle Rectangle which will be reshaped
	 * @param xLimit limit of x+width of given rectangle
	 * @param yLimit limit of y+height of given rectangle
	 */
	public static void reshapeRect(Rectangle rectangle, int xLimit, int yLimit) {
		double width = rectangle.getWidth(), height = rectangle.getHeight();
		double x = rectangle.getX(), y = rectangle.getY();

		if (x < 0) {
			width += x;
			x = 0;
		}
		if (y < 0) {
			height += y;
			y = 0;
		}

		if (x + width >= xLimit)
			width = xLimit - x;
		if (y + height >= yLimit)
			height = yLimit - y;

		rectangle.setRect(x, y, Math.max(width, 1), Math.max(height, 1));
	}

	public static Rectangle getTightDiffArea(Rectangle rectangle, int xLimit, int yLimit) {
		int minMargin = Math.min(ComparisonParameters.getDefaultGroupDistance() / 2,
				ComparisonParameters.getSplitGroupDistance() / 2);
		int x = (int) rectangle.getX(), y = (int) rectangle.getY(), width = (int) rectangle.getWidth(),
				height = (int) rectangle.getHeight();
		// check if the rectangle meets the boundary
		if (x > 0) {
			x += minMargin;
			width -= minMargin;
		}
		if (y > 0) {
			y += minMargin;
			height -= minMargin;
		}
		if (x + width < xLimit) {
			width -= minMargin;
		}
		if (y + height < yLimit) {
			height -= minMargin;
		}

		return new Rectangle(x, y, width, height);
	}

	public static List<Rectangle> convertObjectGroupsToAreas(List<ObjectGroup> objectGroups) {
		// Create a list of the Rectangle from diffGroups
		List<Rectangle> rectangles = new ArrayList<Rectangle>();

		for (ObjectGroup objectGroup : objectGroups) {
			rectangles.add(objectGroup.getRectangle());
		}

		return rectangles;
	}

	/**
	 * convert different points to the list of object groups which are completely merged
	 *
	 * @param DP DiffPoints
	 * @param group_distance distance for grouping
	 * @return list of object groups which are completely merged
	 */
	public static List<ObjectGroup> convertDiffPointsToObjectGroups(DiffPoints DP, int group_distance) {
		List<Point> diffPoints = DP.getDiffPoints();
		if (diffPoints == null || diffPoints.isEmpty()) {
			return new ArrayList<ObjectGroup>();
		}

		int mergeFlag = 0;
		List<ObjectGroup> diffGroups = new ArrayList<ObjectGroup>();

		// Merge diffPoints belongs to the same object into one objectGroup.
		for (Point point : diffPoints) {
			ObjectGroup objectGroup = new ObjectGroup(new Point(point.x, point.y), group_distance);
			for (ObjectGroup diffGroup : diffGroups) {
				if (diffGroup.canMerge(objectGroup)) {
					diffGroup.union(objectGroup);
					mergeFlag = 1;
					break;
				}
			}
			if (mergeFlag != 1) {
				diffGroups.add(objectGroup);
			}
			mergeFlag = 0;
		}

		// merge all possible object groups
		return ObjectGroup.mergeAllPossibleObjects(diffGroups);
	}

	/**
	 * get sub-image from given image and rectangle
	 *
	 * @param image
	 * @param rectangle
	 * @return sub-image of given area
	 */
	public static BufferedImage getSubImage(BufferedImage image, Rectangle rectangle) {

		// before getting subImage, reshape rectangle to avoid raster error
		reshapeRect(rectangle, image.getWidth(), image.getHeight());

		// Initialize variables
		int width = (int) rectangle.getWidth(), height = (int) rectangle.getHeight();
		int x = (int) rectangle.getX(), y = (int) rectangle.getY();

		return image.getSubimage(x, y, width, height);
	}

	/**
	 * find dominant offset between two images
	 *
	 * @param expectedImage
	 * @param actualImage
	 * @param diffThreshold threshold to ignore small difference
	 * @return Offset contains offsetX and offsetY
	 */
	public static Offset findDominantOffset(BufferedImage expectedImage, BufferedImage actualImage,
			double diffThreshold) {

		// we don't need to check all elements, only check one element in every STEP*STEP elements
		int STEP = 5;

		// we need to restrict the maximum offset to avoid redundant checking
		int maxOffset = 10;

		// initialize size
		int expectedWidth = expectedImage.getWidth(), expectedHeight = expectedImage.getHeight();
		int actualWidth = actualImage.getWidth(), actualHeight = actualImage.getHeight();
		int subWidth = Math.min(expectedWidth, actualWidth), subHeight = Math.min(expectedHeight, actualHeight);
		int xMax = Math.abs(expectedWidth - actualWidth), yMax = Math.abs(expectedHeight - actualHeight);
		xMax = Math.min(xMax, maxOffset);
		yMax = Math.min(yMax, maxOffset);

		// check the type of relationship between two image sizes
		// if one of two is contained in the other, it has type 1 or 2.
		// else, it has type 3 or 4.
		int sizeRelationType = getSizeRelationType(expectedWidth, expectedHeight, actualWidth, actualHeight);
		int expectedXOffset = 0, expectedYOffset = 0, actualXOffset = 0, actualYOffset = 0;

		// calculation method to find dominant offset depends on the type of this relation.
		switch (sizeRelationType) {
			case 1:
				// for type 1, actualImage is bigger than expectedImage
				// so we need to move a sub-rectangle only in actualImage
				actualXOffset = 1;
				actualYOffset = 1;
				break;

			case 2:
				// for type 2, expectedImage is bigger than actualImage
				// so we need to move a sub-rectangle only in expectedImage
				expectedXOffset = 1;
				expectedYOffset = 1;
				break;

			case 3:
				// for type 3, the width of expectedImage is larger,
				// and the height of actualImage is larger.
				// so we need to move a sub-rectangle rightward in expectedImage,
				// and downward in actualImage
				expectedXOffset = 1;
				actualYOffset = 1;
				break;

			case 4:
				// for type 4, the width of actualImage is larger,
				// and the height of expectedImage is larger.
				// so we need to move a sub-rectangle rightward in actualImage,
				// and downward in expectedImage
				expectedYOffset = 1;
				actualXOffset = 1;
				break;
		}

		// initialize the color array.
		int[] expectedColors = new int[expectedWidth * expectedHeight];
		int[] actualColors = new int[actualWidth * actualHeight];
		expectedImage.getRGB(0, 0, expectedWidth, expectedHeight, expectedColors, 0, expectedWidth);
		actualImage.getRGB(0, 0, actualWidth, actualHeight, actualColors, 0, actualWidth);
		int[] expectedRed = new int[expectedColors.length];
		int[] expectedGreen = new int[expectedColors.length];
		int[] expectedBlue = new int[expectedColors.length];
		int[] actualRed = new int[actualColors.length];
		int[] actualGreen = new int[actualColors.length];
		int[] actualBlue = new int[actualColors.length];

		for (int i = 0; i < expectedColors.length; i++) {
			Color expectedColor = new Color(expectedColors[i]);
			expectedRed[i] = expectedColor.getRed();
			expectedGreen[i] = expectedColor.getGreen();
			expectedBlue[i] = expectedColor.getBlue();
		}

		for (int i = 0; i < actualColors.length; i++) {
			Color actualColor = new Color(actualColors[i]);
			actualRed[i] = actualColor.getRed();
			actualGreen[i] = actualColor.getGreen();
			actualBlue[i] = actualColor.getBlue();
		}

		// the difference of Red, Green, and Blue, respectively.
		int r, g, b, bestX = 0, bestY = 0;

		// count the number of different points using threshold
		int thresDiffCount, thresDiffMin = -1;

		// Find the dominant offset moving the subimage in the bigger image.
		if (sizeRelationType == 1 || sizeRelationType == 2) {
			// containing case

			for (int y = 0; y <= yMax; y++) {
				for (int x = 0; x <= xMax; x++) {

					// find dominant offset
					thresDiffCount = 0;
					for (int i = 0; i < subHeight; i = i + STEP) {
						for (int j = 0; j < subWidth; j = j + STEP) {
							r = expectedRed[expectedWidth * (i + y * expectedYOffset) + (j + x * expectedXOffset)]
									- actualRed[actualWidth * (i + y * actualYOffset) + (j + x * actualXOffset)];
							g = expectedGreen[expectedWidth * (i + y * expectedYOffset) + (j + x * expectedXOffset)]
									- actualGreen[actualWidth * (i + y * actualYOffset) + (j + x * actualXOffset)];
							b = expectedBlue[expectedWidth * (i + y * expectedYOffset) + (j + x * expectedXOffset)]
									- actualBlue[actualWidth * (i + y * actualYOffset) + (j + x * actualXOffset)];
							if (r * r + g * g + b * b > 3 * 255 * 255 * diffThreshold * diffThreshold)
								thresDiffCount++;
						}
					}
					// Find the minimal number of threshold different pixels.
					if (thresDiffCount < thresDiffMin || thresDiffMin == -1) {
						thresDiffMin = thresDiffCount;
						bestX = x;
						bestY = y;
					}
				}
			}
		} else {
			// not containing case

			// move sub-rectangle downward
			int x = 0;
			for (int y = 0; y <= yMax; y++) {

				// find dominant offset
				thresDiffCount = 0;
				for (int i = 0; i < subHeight; i = i + STEP) {
					for (int j = 0; j < subWidth; j = j + STEP) {
						r = expectedRed[expectedWidth * (i + y * expectedYOffset) + (j + x * expectedXOffset)]
								- actualRed[actualWidth * (i + y * actualYOffset) + (j + x * actualXOffset)];
						g = expectedGreen[expectedWidth * (i + y * expectedYOffset) + (j + x * expectedXOffset)]
								- actualGreen[actualWidth * (i + y * actualYOffset) + (j + x * actualXOffset)];
						b = expectedBlue[expectedWidth * (i + y * expectedYOffset) + (j + x * expectedXOffset)]
								- actualBlue[actualWidth * (i + y * actualYOffset) + (j + x * actualXOffset)];
						if (r * r + g * g + b * b > 3 * 255 * 255 * diffThreshold * diffThreshold)
							thresDiffCount++;
					}
				}
				// Find the minimal number of threshold different pixels.
				if (thresDiffCount < thresDiffMin || thresDiffMin == -1) {
					thresDiffMin = thresDiffCount;
					bestX = x;
					bestY = y;
				}
			}

			// move sub-rectangle rightward
			int y = 0;
			for (x = 1; x <= xMax; x++) {

				// find dominant offset
				thresDiffCount = 0;
				for (int i = 0; i < subHeight; i = i + STEP) {
					for (int j = 0; j < subWidth; j = j + STEP) {
						r = expectedRed[expectedWidth * (i + y * expectedYOffset) + (j + x * expectedXOffset)]
								- actualRed[actualWidth * (i + y * actualYOffset) + (j + x * actualXOffset)];
						g = expectedGreen[expectedWidth * (i + y * expectedYOffset) + (j + x * expectedXOffset)]
								- actualGreen[actualWidth * (i + y * actualYOffset) + (j + x * actualXOffset)];
						b = expectedBlue[expectedWidth * (i + y * expectedYOffset) + (j + x * expectedXOffset)]
								- actualBlue[actualWidth * (i + y * actualYOffset) + (j + x * actualXOffset)];
						if (r * r + g * g + b * b > 3 * 255 * 255 * diffThreshold * diffThreshold)
							thresDiffCount++;
					}
				}

				// Find the minimal number of threshold different pixels.
				if (thresDiffCount < thresDiffMin || thresDiffMin == -1) {
					thresDiffMin = thresDiffCount;
					bestX = x;
					bestY = y;
				}
			}
		}

		return new Offset(bestX, bestY);
	}

	/**
	 * using dominant offset, extract subImage from given expectedImage the size of subImage will be the same as the
	 * size of intersection of two images.
	 *
	 * @param expectedImage the image which we want to extract sub-image from
	 * @param actualImage the other image
	 * @param offset dominant offset between two images
	 * @return subImage of expectedImage at dominant offset
	 */
	public static BufferedImage getDominantImage(BufferedImage expectedImage, BufferedImage actualImage,
			Offset offset) {

		// initialize size
		int expectedWidth = expectedImage.getWidth(), expectedHeight = expectedImage.getHeight();
		int actualWidth = actualImage.getWidth(), actualHeight = actualImage.getHeight();

		// dominant frame of expectedImage depends on size-relation type and offset
		int sizeRelationType = getSizeRelationType(expectedWidth, expectedHeight, actualWidth, actualHeight);
		switch (sizeRelationType) {
			case 1:
				return expectedImage;
			case 2:
				return expectedImage.getSubimage(offset.getX(), offset.getY(), actualWidth, actualHeight);
			case 3:
				if (offset.getX() > 0) {
					return expectedImage.getSubimage(offset.getX(), 0, actualWidth, expectedHeight);
				} else {
					return expectedImage.getSubimage(0, 0, actualWidth, expectedHeight);
				}
			case 4:
				if (offset.getY() > 0) {
					return expectedImage.getSubimage(0, offset.getY(), expectedWidth, actualHeight);
				} else {
					return expectedImage.getSubimage(0, 0, expectedWidth, actualHeight);
				}

				// never reach to default case
			default:
				return expectedImage;
		}
	}

	/**
	 * In order to find dominant offset, we have to consider the relationship between two image sizes.
	 *
	 * @param expectedWidth width of expectedImage
	 * @param expectedHeight height of expectedImage
	 * @param actualWidth width of actualImage
	 * @param actualHeight height of actualImage
	 * @return the number of type of size-relationship 1 when actualImage is bigger than expectedImage 2 when
	 *         expectedImage is bigger than actualImage 3 when the width of expectedImage is larger and the height of
	 *         actualImage is larger. 4 when the width of actualImage is larger and the height of expectedImage is
	 *         larger.
	 */
	public static int getSizeRelationType(int expectedWidth, int expectedHeight, int actualWidth, int actualHeight) {
		if (expectedWidth <= actualWidth && expectedHeight <= actualHeight) {
			return 1;
		} else if (expectedWidth >= actualWidth && expectedHeight >= actualHeight) {
			return 2;
		} else if (expectedWidth >= actualWidth && expectedHeight < actualHeight) {
			return 3;
		} else {
			return 4;
		}
	}

}
