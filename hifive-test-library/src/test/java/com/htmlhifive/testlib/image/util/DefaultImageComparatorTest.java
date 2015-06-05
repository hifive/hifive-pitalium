/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.image.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.htmlhifive.testlib.common.exception.TestRuntimeException;
import com.htmlhifive.testlib.image.model.DiffPoints;

public class DefaultImageComparatorTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * メソッド引数チェック。image1がnullの場合、TestRuntimeException。
	 */
	@Test
	public void testCompare_illegalArgument_image1_null() throws Exception {
		BufferedImage image = ImageIO.read(getClass().getResource("hifive_logo.png"));
		Rectangle rectangle = new Rectangle(0, 0, image.getWidth(), image.getHeight());

		expectedException.expect(TestRuntimeException.class);
		new DefaultImageComparator().compare(null, null, image, rectangle);
	}

	/**
	 * メソッド引数チェック。image2がnullの場合、TestRuntimeException。
	 */
	@Test
	public void testCompare_illegalArgument_image2_null() throws Exception {
		BufferedImage image = ImageIO.read(getClass().getResource("hifive_logo.png"));
		Rectangle rectangle = new Rectangle(0, 0, image.getWidth(), image.getHeight());

		expectedException.expect(TestRuntimeException.class);
		new DefaultImageComparator().compare(image, rectangle, null, null);
	}

	/**
	 * 同じ画像と同じ領域を比較 => 成功
	 */
	@Test
	public void testCompare() throws Exception {
		BufferedImage image = ImageIO.read(getClass().getResource("hifive_logo.png"));
		Rectangle rectangle = new Rectangle(0, 0, image.getWidth(), image.getHeight());
		DiffPoints result = new DefaultImageComparator().compare(image, rectangle, image, rectangle);

		assertThat(result.isSucceeded(), is(true));
	}

	/**
	 * 違う画像を比較する => 失敗
	 */
	@Test
	public void testCompare_different_image() throws Exception {
		BufferedImage image1 = ImageIO.read(getClass().getResource("hifive_logo.png"));
		Rectangle rectangle1 = new Rectangle(0, 0, image1.getWidth(), image1.getHeight());
		BufferedImage image2 = ImageIO.read(getClass().getResource("hifive_logo_part.png"));
		Rectangle rectangle2 = new Rectangle(0, 0, image2.getWidth(), image2.getHeight());
		DiffPoints result = new DefaultImageComparator().compare(image1, rectangle1, image2, rectangle2);

		assertThat(result.isFailed(), is(true));
	}

	/**
	 * 1pxだけ違う画像を比較する => 失敗
	 */
	@Test
	public void testCompare_different_1px() throws Exception {
		BufferedImage image1 = ImageIO.read(getClass().getResource("hifive_logo.png"));
		BufferedImage image2 = ImageIO.read(getClass().getResource("hifive_logo.png"));

		// 1pxだけ色を変える
		Random random = new Random();
		int x = random.nextInt(image2.getWidth());
		int y = random.nextInt(image2.getHeight());
		image2.setRGB(x, y, image2.getRGB(x, y) - 1);

		Rectangle rectangle = new Rectangle(0, 0, image1.getWidth(), image2.getHeight());

		DiffPoints result = new DefaultImageComparator().compare(image1, rectangle, image2, rectangle);

		assertThat(result.isFailed(), is(true));
		assertThat(result.getDiffPoints().size(), is(1));
		assertThat(result.getSizeDiffPoints().isEmpty(), is(true));
		assertThat(result.getDiffPoints().get(0), is(new Point(x, y)));
	}

	/**
	 * 端に差分がある場合 => 失敗
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCompare_different_area() throws Exception {
		BufferedImage image = ImageIO.read(getClass().getResource("hifive_logo.png"));
		Rectangle rectangle1 = new Rectangle(0, 0, image.getWidth(), image.getHeight());
		Rectangle rectangle2 = new Rectangle(0, 0, image.getWidth() - 1, image.getHeight() - 1);
		DiffPoints result = new DefaultImageComparator().compare(image, rectangle1, image, rectangle2);

		assertThat(result.isFailed(), is(true));
		assertThat(result.getDiffPoints().isEmpty(), is(true));
		assertThat(result.getSizeDiffPoints().isEmpty(), is(false));

		Comparator<Point> comparator = new Comparator<Point>() {
			@Override
			public int compare(Point o1, Point o2) {
				if (o1.x != o2.x)
					return Integer.compare(o1.x, o2.x);
				if (o1.y != o2.y)
					return Integer.compare(o1.y, o2.y);
				return 0;
			}
		};

		Set<Point> expectedDiffPoints = new TreeSet<Point>(comparator);
		for (int x = 0; x < image.getWidth(); x++) {
			expectedDiffPoints.add(new Point(x, image.getHeight()));
		}
		for (int y = 0; y < image.getHeight(); y++) {
			expectedDiffPoints.add(new Point(image.getWidth(), y));
		}

		Set<Point> diffPoints = new TreeSet<Point>(comparator);
		diffPoints.addAll(result.getSizeDiffPoints());
		assertThat(diffPoints, is(expectedDiffPoints));
	}

}