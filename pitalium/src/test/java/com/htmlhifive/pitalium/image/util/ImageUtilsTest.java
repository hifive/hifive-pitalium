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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.htmlhifive.pitalium.image.model.CompareOption;
import com.htmlhifive.pitalium.image.model.ComparisonParameterDefaults;
import com.htmlhifive.pitalium.image.model.DiffPoints;
import com.htmlhifive.pitalium.image.model.ImageComparedResult;
import com.htmlhifive.pitalium.image.model.ObjectGroup;
import com.htmlhifive.pitalium.image.model.Offset;

public class ImageUtilsTest {

	// ObjectGroup内で定義されているグループ化可能な距離のデフォルト値
	private static final int DEFAULT_GROUP_DISTANCE = ObjectGroup.DEFAULT_GROUP_DISTANCE;

	//<editor-fold desc="compare_default">

	/**
	 * デフォルト設定で画像を比較するテスト。同一の場合。
	 */
	@Test
	public void testCompare_default_same() throws Exception {
		BufferedImage image1 = ImageIO.read(getClass().getResource("hifive_logo.png"));
		Rectangle rect1 = new Rectangle(0, 0, image1.getWidth(), image1.getHeight());
		BufferedImage image2 = ImageIO.read(getClass().getResource("hifive_logo.png"));
		Rectangle rect2 = new Rectangle(0, 0, image2.getWidth(), image2.getHeight());

		ImageComparedResult result = ImageUtils.compare(image1, rect1, image2, rect2, new CompareOption[0]);
		assertThat(result.isSucceeded(), is(true));
	}

	/**
	 * デフォルト設定で画像を比較するテスト。同一の場合。
	 */
	@Test
	public void testCompare_default_notSame() throws Exception {
		BufferedImage image1 = ImageIO.read(getClass().getResource("hifive_logo.png"));
		Rectangle rect1 = new Rectangle(0, 0, image1.getWidth(), image1.getHeight());
		BufferedImage image2 = ImageIO.read(getClass().getResource("hifive_logo.png"));
		Rectangle rect2 = new Rectangle(0, 0, image2.getWidth(), image2.getHeight());

		image2.setRGB(10, 10, 0x000000);

		ImageComparedResult result = ImageUtils.compare(image1, rect1, image2, rect2, new CompareOption[0]);
		assertThat(result.isFailed(), is(true));
	}

	//</editor-fold>

	//<editor-fold desc="isContained">

	/**
	 * 画像に画像が含まれるかのテスト。含まれる場合。
	 */
	@Test
	public void testIsContained_contained() throws Exception {
		BufferedImage entireImage = ImageIO.read(getClass().getResource("hifive_logo.png"));
		BufferedImage partImage = ImageIO.read(getClass().getResource("hifive_logo_part.png"));

		assertThat(ImageUtils.isContained(entireImage, partImage), is(true));
	}

	/**
	 * 画像に画像が含まれるかのテスト。含まれない場合。
	 */
	@Test
	public void testIsContained_NOT_contained() throws Exception {
		BufferedImage entireImage = ImageIO.read(getClass().getResource("hifive_logo.png"));
		BufferedImage partImage = ImageIO.read(getClass().getResource("hifive_logo_not_part.png"));

		assertThat(ImageUtils.isContained(entireImage, partImage), is(false));
	}

	//</editor-fold>

	//<editor-fold desc="imageEquals">

	/**
	 * 画像が同一かチェックするテスト。同一の場合。
	 */
	@Test
	public void testImageEquals_true() throws Exception {
		BufferedImage image1 = ImageIO.read(getClass().getResource("hifive_logo.png"));
		BufferedImage image2 = ImageIO.read(getClass().getResource("hifive_logo.png"));

		assertThat(image1, is(not(sameInstance(image2))));
		assertThat(ImageUtils.imageEquals(image1, image2), is(true));
	}

	/**
	 * 画像が同一かチェックするテスト。同一でない場合。
	 */
	@Test
	public void testImageEquals_false() throws Exception {
		BufferedImage image1 = ImageIO.read(getClass().getResource("hifive_logo.png"));
		BufferedImage image2 = ImageIO.read(getClass().getResource("hifive_logo.png"));
		image2.setRGB(10, 10, 0x000000);

		assertThat(ImageUtils.imageEquals(image1, image2), is(false));
	}

	/**
	 * 画像が同一かチェックするテスト。大きさが違うので同一でない場合。
	 */
	@Test
	public void testImageEquals_false_size() throws Exception {
		BufferedImage image1 = ImageIO.read(getClass().getResource("hifive_logo.png"));
		BufferedImage image2 = ImageIO.read(getClass().getResource("hifive_logo_part.png"));

		assertThat(ImageUtils.imageEquals(image1, image2), is(false));
	}

	//</editor-fold>

	//<editor-fold desc="getMaskedImage">

	/**
	 * 画像をマスクするテスト
	 */
	@Test
	public void testGetMaskedImage() throws Exception {
		BufferedImage image = ImageIO.read(getClass().getResource("hifive_logo.png"));
		List<Rectangle> areas = Collections.singletonList(new Rectangle(10, 10, 10, 10));

		BufferedImage expected = ImageIO.read(getClass().getResource("ImageUtilsTest_maskedImage.png"));
		BufferedImage actual = ImageUtils.getMaskedImage(image, areas);

		assertThat(ImageUtils.imageEquals(expected, actual), is(true));
	}

	//</editor-fold>

	/**
	 * 差分画像を取得するテスト。
	 */
	@Test
	public void testGetDiffImage() throws Exception {
		BufferedImage image = ImageIO.read(getClass().getResource("web_page_screenshot.png"));
		List<Point> points = new ArrayList<Point>();
		points.add(new Point(100, 100));
		DiffPoints diffPoints = new DiffPoints(points, new ArrayList<Point>());

		BufferedImage actual = ImageUtils.getDiffImage(image, image, diffPoints);
		BufferedImage expected = ImageIO.read(getClass().getResource("ImageUtilsTest_getDiffImage.png"));
		assertThat(ImageUtils.imageEquals(expected, actual), is(true));
	}

	//<editor-fold desc="trim">

	/**
	 * 画像をトリムするテスト。
	 */
	@Test
	public void testTrim() throws Exception {
		BufferedImage image = ImageIO.read(getClass().getResource("hifive_logo.png"));

		BufferedImage expected = ImageIO.read(getClass().getResource("ImageUtilsTest_trim.png"));
		BufferedImage actual = ImageUtils.trim(image, 2, 2, 2, 2);

		assertThat(ImageUtils.imageEquals(expected, actual), is(true));
	}

	//</editor-fold>

	//<editor-fold desc="trim">

	/**
	 * BufferedImageからRGBのint配列を取得するテスト。
	 */
	@Test
	public void testGetRGB() throws Exception {
		BufferedImage image = ImageIO.read(getClass().getResource("hifive_logo.png"));
		int width = image.getWidth();
		int height = image.getHeight();

		int[] expected = image.getRGB(0, 0, width, height, null, 0, width);
		int[] actual = ImageUtils.getRGB(image, width, height);
		assertArrayEquals(expected, actual);
	}

	//</editor-fold>

	/**
	 * 元画像の積分画像を生成するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCalcIntegralImage() throws Exception {
		BufferedImage source = ImageIO.read(new File("src/test/resources/images/imageUtils/calc_integral_image.png"));

		// expectedの値はcalc_integral_image.pngで事前に計算したImageUtils.calcIntegralImageの結果を使用
		double[][] expected = { { 255.0, 255.0, 510.0, 765.0 }, { 510.0, 765.0, 1275.0, 1530.0 },
				{ 510.0, 1020.0, 1785.0, 2295.0 }, { 765.0, 1530.0, 2295.0, 3060.0 } };
		double[][] actual = ImageUtils.calcIntegralImage(source);

		assertThat(actual, is(expected));
	}

	/**
	 * 指定のポイントをマークした画像を作成するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetMarkedImage() throws Exception {
		BufferedImage image = ImageIO.read(getClass().getResource("web_page_screenshot.png"));

		List<Point> points = new ArrayList<Point>();
		points.add(new Point(100, 100));
		DiffPoints diffPoints = new DiffPoints(points, new ArrayList<Point>());

		// expectedのget_marked_image.pngは事前に出力したImageUtils.getMarkedImageの結果を使用
		BufferedImage expected = ImageIO.read(new File("src/test/resources/images/imageUtils/get_marked_image.png"));
		BufferedImage actual = ImageUtils.getMarkedImage(image, diffPoints);

		assertThat(ImageUtils.imageEquals(expected, actual), is(true));
	}

	/**
	 * diff座標から近似の四角形を作成するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testConvertDiffPointsToAreas() throws Exception {
		BufferedImage image1 = ImageIO
				.read(new File("src/test/resources/images/imageUtils/convert_diff_points_to_areas_expected.png"));
		Rectangle rectangle1 = new Rectangle(0, 0, image1.getWidth(), image1.getHeight());
		BufferedImage image2 = ImageIO
				.read(new File("src/test/resources/images/imageUtils/convert_diff_points_to_areas_actual.png"));
		Rectangle rectangle2 = new Rectangle(0, 0, image2.getWidth(), image2.getHeight());

		ImageComparedResult result = new DefaultImageComparator().compare(image1, rectangle1, image2, rectangle2);
		List<Point> points = ((DiffPoints) result).getDiffPoints();
		DiffPoints diffPoints = new DiffPoints(points, new ArrayList<Point>());

		// ImageUtils.convertDiffPointsToAreas内の計算でObjectGroupを作成するので、expectedの値もObjectGroupのマージンを考慮
		int x = 1;
		int y = 0;
		int width = 3;
		int height = 2;
		int margin = DEFAULT_GROUP_DISTANCE / 2;
		List<Rectangle> expected = new ArrayList<Rectangle>();
		expected.add(new Rectangle(x - margin, y - margin, 2 * margin + width, 2 * margin + height));

		List<Rectangle> actual;
		// 引数がDiffPoints diffPointsの場合
		actual = ImageUtils.convertDiffPointsToAreas(diffPoints);
		assertThat(actual, is(expected));
		// 引数がList<Point> diffPointsの場合
		actual = ImageUtils.convertDiffPointsToAreas(points);
		assertThat(actual, is(expected));
	}

	/**
	 * サイズのdiff座標から近似の四角形を作成するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testConvertSizeDiffPointsToAreas() throws Exception {
		BufferedImage image1 = ImageIO
				.read(new File("src/test/resources/images/imageUtils/convert_size_diff_points_to_areas_expected.png"));
		Rectangle rectangle1 = new Rectangle(0, 0, image1.getWidth(), image1.getHeight());
		BufferedImage image2 = ImageIO
				.read(new File("src/test/resources/images/imageUtils/convert_size_diff_points_to_areas_actual.png"));
		Rectangle rectangle2 = new Rectangle(0, 0, image2.getWidth(), image2.getHeight());

		ImageComparedResult result = new DefaultImageComparator().compare(image1, rectangle1, image2, rectangle2);
		List<Point> sizeDiffPoints = ((DiffPoints) result).getSizeDiffPoints();

		// ImageUtils.convertSizeDiffPointsToAreas内の計算でRectangleを作成するときrectMargin3で計算しているので、expectedの値もrectMarginを考慮
		int startX = 21;
		int startY = 21;
		int endX = 30;
		int endY = 30;
		int rectMargin = 3;
		List<Rectangle> expected = new ArrayList<Rectangle>();
		// 右側の四角形
		expected.add(new Rectangle(startX, 0, endX - startX, endY));
		// 下側の四角形
		expected.add(
				new Rectangle(-rectMargin, startY - rectMargin, endX + rectMargin * 2, endY - startY + rectMargin * 2));
		List<Rectangle> actual = ImageUtils.convertSizeDiffPointsToAreas(sizeDiffPoints);

		assertThat(actual, is(expected));
	}

	/**
	 * 画像を縦に結合し、1枚の画像にするテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testVerticalMerge() throws Exception {
		List<BufferedImage> images = new ArrayList<BufferedImage>();
		images.add(ImageIO.read(new File("src/test/resources/images/hifive_logo.png")));
		images.add(ImageIO.read(new File("src/test/resources/images/hifive_logo.png")));

		// expectedのvertical_merge.pngは事前に出力したImageUtils.verticalMergeの結果を使用
		BufferedImage expected = ImageIO.read(new File("src/test/resources/images/imageUtils/vertical_merge.png"));
		BufferedImage actual = ImageUtils.verticalMerge(images);

		assertThat(ImageUtils.imageEquals(expected, actual), is(true));
	}

	/**
	 * 画像を結合し、1枚の画像にするテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testMerge() throws Exception {
		List<List<BufferedImage>> images = new ArrayList<List<BufferedImage>>();
		images.add(new ArrayList<BufferedImage>());
		images.get(0).add(ImageIO.read(new File("src/test/resources/images/hifive_logo.png")));
		images.get(0).add(ImageIO.read(new File("src/test/resources/images/hifive_logo.png")));

		images.add(new ArrayList<BufferedImage>());
		images.get(1).add(ImageIO.read(new File("src/test/resources/images/hifive_logo.png")));
		images.get(1).add(ImageIO.read(new File("src/test/resources/images/hifive_logo.png")));

		// expectedのmerge.pngは事前に出力したImageUtils.mergeの結果を使用
		BufferedImage expected = ImageIO.read(new File("src/test/resources/images/imageUtils/merge.png"));
		BufferedImage actual = ImageUtils.merge(images);

		assertThat(ImageUtils.imageEquals(expected, actual), is(true));
	}

	/**
	 * pixel値をARGBに変換するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testToARGB() throws Exception {
		int pixel = 0xffffffff;

		int[] expected = { 0xff, 0xff, 0xff, 0xff };
		int[] actual = ImageUtils.toARGB(pixel);

		assertThat(expected, is(actual));
	}

	/**
	 * ARGB, RGBをpixel値に変換するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testToPixel() throws Exception {
		int[] argb = { 0xff, 0xff, 0xff, 0xff };
		int a = 0xff, r = 0xff, g = 0xff, b = 0xff;

		int expected = 0xffffffff;
		int actual;

		// 引数が配列の場合
		actual = ImageUtils.toPixel(argb);
		assertThat(actual, is(expected));
		// 引数がARGBの場合
		actual = ImageUtils.toPixel(a, r, g, b);
		assertThat(actual, is(expected));
		// 引数がRGBの場合
		actual = ImageUtils.toPixel(r, g, b);
		assertThat(actual, is(expected));
	}

	/**
	 * backgroundColorを検出するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testDetectBackgroundColor() throws Exception {
		BufferedImage b = ImageIO.read(new File("src/test/resources/images/hifive_logo.png"));

		// expectedの値はhifive_logo.pngで事前に計算したImageUtils.detectBackgroundColorの結果を使用
		int expected = 0xfff5f4f4;
		int actual = ImageUtils.detectBackgroundColor(b);

		assertThat(actual, is(expected));
	}

	/**
	 * Rectangleを領域内で取得した領域のRectangleで上書きするテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetObjectRectangle() throws Exception {
		BufferedImage image = ImageIO.read(new File("src/test/resources/images/imageUtils/get_object_rectangle.png"));
		// 取得したい領域よりもwidth, heightに+10大きく、x, yに-4移動したRectangleを作成
		Rectangle rectangle = new Rectangle(247, 225, 109, 109);

		// 取得したい領域の大きさと位置
		Rectangle expected = new Rectangle(251, 229, 99, 99);
		// 取得できたかどうかのbooleanを返し、rectangleは取得した領域の値で上書き
		boolean result = ImageUtils.getObjectRectangle(image, rectangle);

		assertThat(result, is(true));
		assertThat(rectangle, is(expected));
	}

	/**
	 * RGBピクセル同士のノルムを計算するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testNorm() throws Exception {
		int pixel1 = 0xffffffff, pixel2 = 0xffffffff;

		double expected = 0.0;
		double actual = ImageUtils.norm(pixel1, pixel2);

		assertThat(actual, is(expected));
	}

	/**
	 * サブピクセルレンダリングされたテキスト画像の特徴を計算するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCountSubpixel() throws Exception {
		BufferedImage bimage = ImageIO.read(new File("src/test/resources/images/imageUtils/count_subpixel.png"));

		// expectedの値はcount_subpixel.pngで事前に計算したImageUtils.countSubpixelの結果を使用
		double[] expected = { 1.0, 124.01960784313725 };
		double[] actual = ImageUtils.countSubpixel(bimage);

		assertThat(actual, is(expected));
	}

	/**
	 * Rectanglesの余分を領域を削除して上限内に収めるテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testRemoveRedundantRectangles() throws Exception {
		List<Rectangle> rectangles = new ArrayList<Rectangle>();
		rectangles.add(new Rectangle(0, 0, 89, 89));
		rectangles.add(new Rectangle(0, 0, 90, 90));
		rectangles.add(new Rectangle(0, 0, 91, 91));

		int xLimit = 90;
		int yLimit = 90;

		double[][] expected = { { 0.0, 0.0, 89.0, 89.0 }, { 0.0, 0.0, 90.0, 90.0 }, { 0.0, 0.0, 90.0, 90.0 } };
		ImageUtils.removeRedundantRectangles(rectangles, xLimit, yLimit);

		assertThat(rectangles.size(), is(3));
		for (int i = 0; i < rectangles.size(); i++) {
			Rectangle rectangle = rectangles.get(i);
			assertThat(rectangle.getX(), is(expected[i][0]));
			assertThat(rectangle.getY(), is(expected[i][1]));
			assertThat(rectangle.getWidth(), is(expected[i][2]));
			assertThat(rectangle.getHeight(), is(expected[i][3]));
		}
	}

	/**
	 * Rectanglesの重複を削除するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testRemoveOverlappingRectangles() throws Exception {
		List<Rectangle> rectangles = new ArrayList<Rectangle>();
		rectangles.add(new Rectangle(0, 0, 80, 80));
		rectangles.add(new Rectangle(10, 10, 100, 100));
		rectangles.add(new Rectangle(0, 0, 90, 90));
		rectangles.add(new Rectangle(0, 0, 90, 90));

		// (0, 0, 80, 80)は(0, 0, 90, 90)に含まれているので削除される
		double[][] expected = { { 10.0, 10.0, 100.0, 100.0 }, { 0.0, 0.0, 90.0, 90.0 } };
		ImageUtils.removeOverlappingRectangles(rectangles);

		assertThat(rectangles.size(), is(2));
		for (int i = 0; i < rectangles.size(); i++) {
			Rectangle rectangle = rectangles.get(i);
			assertThat(rectangle.getX(), is(expected[i][0]));
			assertThat(rectangle.getY(), is(expected[i][1]));
			assertThat(rectangle.getWidth(), is(expected[i][2]));
			assertThat(rectangle.getHeight(), is(expected[i][3]));
		}
	}

	/**
	 * Rectangleの再形成テスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testReshapeRect() throws Exception {
		Rectangle rectangle = new Rectangle(-10, -10, 90, 90);
		int xLimit = 90;
		int yLimit = 90;

		ImageUtils.reshapeRect(rectangle, xLimit, yLimit);

		assertThat(rectangle.getX(), is(0.0));
		assertThat(rectangle.getY(), is(0.0));
		assertThat(rectangle.getWidth(), is(80.0));
		assertThat(rectangle.getHeight(), is(80.0));
	}

	/**
	 * tightDiffAreaを取得するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetTightDiffArea() throws Exception {
		Rectangle rectangle = new Rectangle(0, 0, 90, 90);
		int xLimit = 100;
		int yLimit = 100;

		Rectangle actual = ImageUtils.getTightDiffArea(rectangle, xLimit, yLimit);

		assertThat(actual.getX(), is(0.0));
		assertThat(actual.getY(), is(0.0));
		assertThat(actual.getWidth(), is(87.0));
		assertThat(actual.getHeight(), is(87.0));
	}

	/**
	 * ObjectGroupsをAreasに変換するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testConvertObjectGroupsToAreas() throws Exception {
		BufferedImage image1 = ImageIO
				.read(new File("src/test/resources/images/imageUtils/convert_object_groups_to_areas_expected.png"));
		Rectangle rectangle1 = new Rectangle(0, 0, image1.getWidth(), image1.getHeight());
		BufferedImage image2 = ImageIO
				.read(new File("src/test/resources/images/imageUtils/convert_object_groups_to_areas_actual.png"));
		Rectangle rectangle2 = new Rectangle(0, 0, image2.getWidth(), image2.getHeight());

		ImageComparedResult result = new DefaultImageComparator().compare(image1, rectangle1, image2, rectangle2);
		List<Point> points = ((DiffPoints) result).getDiffPoints();
		List<ObjectGroup> objectGroups = new ArrayList<ObjectGroup>();
		for (int i = 0; i < points.size(); i++) {
			objectGroups.add(new ObjectGroup(points.get(i)));
		}

		// ImageUtils.convertObjectGroupsToAreas内の計算でObjectGroupを作成するので、expectedの値もObjectGroupのマージンを考慮
		int margin = DEFAULT_GROUP_DISTANCE / 2;
		List<Rectangle> expected = new ArrayList<Rectangle>();
		expected.add(new Rectangle(1 - margin, 0 - margin, 1 + margin * 2, 1 + margin * 2));
		expected.add(new Rectangle(2 - margin, 0 - margin, 1 + margin * 2, 1 + margin * 2));
		expected.add(new Rectangle(3 - margin, 0 - margin, 1 + margin * 2, 1 + margin * 2));
		expected.add(new Rectangle(1 - margin, 1 - margin, 1 + margin * 2, 1 + margin * 2));
		expected.add(new Rectangle(2 - margin, 1 - margin, 1 + margin * 2, 1 + margin * 2));
		expected.add(new Rectangle(3 - margin, 1 - margin, 1 + margin * 2, 1 + margin * 2));
		List<Rectangle> actual = ImageUtils.convertObjectGroupsToAreas(objectGroups);

		assertThat(actual, is(expected));
	}

	/**
	 * DiffPointsをObjectGroupsに変換するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testConvertDiffPointsToObjectGroups() throws Exception {
		BufferedImage image1 = ImageIO
				.read(new File("src/test/resources/images/imageUtils/convert_object_groups_to_areas_expected.png"));
		Rectangle rectangle1 = new Rectangle(0, 0, image1.getWidth(), image1.getHeight());
		BufferedImage image2 = ImageIO
				.read(new File("src/test/resources/images/imageUtils/convert_object_groups_to_areas_actual.png"));
		Rectangle rectangle2 = new Rectangle(0, 0, image2.getWidth(), image2.getHeight());

		ImageComparedResult result = new DefaultImageComparator().compare(image1, rectangle1, image2, rectangle2);
		List<Point> points = ((DiffPoints) result).getDiffPoints();
		DiffPoints diffPoints = new DiffPoints(points, new ArrayList<Point>());

		// ImageUtils.convertDiffPointsToObjectGroups内の計算でObjectGroupを作成するので、expectedの値もObjectGroupのマージンを考慮
		int x = 1;
		int y = 0;
		int width = 3;
		int height = 2;
		int margin = DEFAULT_GROUP_DISTANCE / 2;
		Rectangle expected = new Rectangle(x - margin, y - margin, 2 * margin + width, 2 * margin + height);
		List<ObjectGroup> actual = ImageUtils.convertDiffPointsToObjectGroups(diffPoints, DEFAULT_GROUP_DISTANCE);

		assertThat(actual.size(), is(1));
		assertThat(actual.get(0).getRectangle(), is(expected));
	}

	/**
	 * 指定範囲の画像を取得するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetSubImage() throws Exception {
		BufferedImage image = ImageIO.read(new File("src/test/resources/images/hifive_logo.png"));

		Rectangle rectangle = new Rectangle(0, 0, 30, 30);

		BufferedImage expected = ImageIO.read(new File("src/test/resources/images/imageUtils/get_sub_image.png"));
		BufferedImage actual = ImageUtils.getSubImage(image, rectangle);

		assertThat(ImageUtils.imageEquals(expected, actual), is(true));
	}

	/**
	 * サイズの異なる同じ内容の領域を持つ画像2枚について、同じ内容の領域が重なり合うようなOffsetを計算するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testFindDominantOffset() throws Exception {
		BufferedImage image1 = ImageIO.read(new File("src/test/resources/images/imageUtils/dominant_expected.png"));
		BufferedImage image2 = ImageIO.read(new File("src/test/resources/images/imageUtils/dominant_merge.png"));

		// 0に近いほど領域の内容の違いを無視して計算する
		// デフォルトは0.1
		double diffThreshold = ComparisonParameterDefaults.getDiffThreshold();

		Offset expected = new Offset(5, 5);
		Offset actual = ImageUtils.findDominantOffset(image1, image2, diffThreshold);

		assertThat(actual.getX(), is(expected.getX()));
		assertThat(actual.getY(), is(expected.getY()));
	}

	/**
	 * Offsetから同じ内容をの領域を持つ2枚の画像を重ね合わせた画像を作成するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetDominantImage() throws Exception {
		BufferedImage image1 = ImageIO.read(new File("src/test/resources/images/imageUtils/dominant_expected.png"));
		BufferedImage image2 = ImageIO.read(new File("src/test/resources/images/imageUtils/dominant_actual.png"));

		Offset offset = new Offset(5, 5);

		// expectedのdominant_merge.pngは事前に出力したImageUtils.getDominantImageの結果を使用
		BufferedImage expected = ImageIO.read(new File("src/test/resources/images/imageUtils/dominant_merge.png"));
		BufferedImage actual = ImageUtils.getDominantImage(image1, image2, offset);

		assertThat(ImageUtils.imageEquals(expected, actual), is(true));
	}

	/**
	 * size-relationshipのタイプ番号を取得するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetSizeRelationType() throws Exception {
		int actual;

		actual = ImageUtils.getSizeRelationType(100, 100, 101, 101);
		assertThat(actual, is(1));

		actual = ImageUtils.getSizeRelationType(101, 101, 100, 100);
		assertThat(actual, is(2));

		actual = ImageUtils.getSizeRelationType(101, 100, 100, 101);
		assertThat(actual, is(3));

		actual = ImageUtils.getSizeRelationType(100, 101, 101, 100);
		assertThat(actual, is(4));
	}

}