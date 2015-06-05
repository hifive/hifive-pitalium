/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.image.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.htmlhifive.testlib.image.model.DiffPoints;

public class DiffImageMakerTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * デフォルト値で差分ファイル生成
	 */
	@Test
	public void testExecute_default() throws Exception {
		BufferedImage image = ImageIO.read(getClass().getResource("web_page_screenshot.png"));
		List<Point> points = new ArrayList<Point>();
		points.add(new Point(50, 50));
		DiffPoints diffPoints = new DiffPoints(points, new ArrayList<Point>());
		BufferedImage actual = new DiffImageMaker(image, image, diffPoints).execute();

		BufferedImage expected = ImageIO.read(getClass().getResource("DiffImageMakerTest_defaultResult.png"));
		assertThat(ImageUtils.imageEquals(expected, actual), is(true));
	}

	/**
	 * ラベル名を設定するテスト
	 */
	@Test
	public void testExecute_setLabels() throws Exception {
		BufferedImage image = ImageIO.read(getClass().getResource("web_page_screenshot.png"));
		List<Point> points = new ArrayList<Point>();
		points.add(new Point(50, 50));
		DiffPoints diffPoints = new DiffPoints(points, new ArrayList<Point>());
		BufferedImage actual = new DiffImageMaker(image, image, diffPoints, "left", "right").execute();

		BufferedImage expected = ImageIO.read(getClass().getResource("DiffImageMakerTest_setLabelsResult.png"));
		assertThat(ImageUtils.imageEquals(expected, actual), is(true));
	}

}