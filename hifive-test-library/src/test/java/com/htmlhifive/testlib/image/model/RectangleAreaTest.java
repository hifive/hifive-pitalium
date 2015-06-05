/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.image.model;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.awt.Rectangle;

import org.junit.Test;

public class RectangleAreaTest {

	/**
	 * 小数点切捨てテスト
	 */
	@Test
	public void testFloor() throws Exception {
		RectangleArea rectangle = new RectangleArea(10.5d, 20.5d, 30.5d, 40.5d);
		RectangleArea result = rectangle.floor();

		assertThat(result, is(new RectangleArea(10d, 20d, 30d, 40d)));
	}

	/**
	 * 矩形領域の移動テスト
	 */
	@Test
	public void testMove() throws Exception {
		RectangleArea rectangle = new RectangleArea(10.5d, 20.5d, 30.5d, 40.5d);
		RectangleArea result = rectangle.move(0.5d, 0d);

		assertThat(result, is(new RectangleArea(11d, 20.5d, 30.5d, 40.5d)));
	}

	/**
	 * スケール適応テスト
	 */
	@Test
	public void testApplyScale() throws Exception {
		RectangleArea rectangle = new RectangleArea(10.5d, 20.5d, 30.5d, 40.5d);
		RectangleArea result = rectangle.applyScale(10d);

		assertThat(result, is(new RectangleArea(105d, 205d, 305d, 405d)));
	}

	/**
	 * {@link Rectangle}に変換するテスト
	 */
	@Test
	public void testToRectangle() throws Exception {
		RectangleArea rectangle = new RectangleArea(10.5d, 20.5d, 30.5d, 40.5d);
		Rectangle result = rectangle.toRectangle();

		assertThat(result.x, is(11));
		assertThat(result.y, is(21));
		assertThat(result.width, is(31));
		assertThat(result.height, is(41));
	}

}