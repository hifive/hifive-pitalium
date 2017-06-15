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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.htmlhifive.pitalium.image.model.CompareOption;
import com.htmlhifive.pitalium.image.model.DiffPoints;
import com.htmlhifive.pitalium.image.model.ImageComparedResult;

public class ImageUtilsTest {

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

}