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
		RectangleArea rectangle = new RectangleArea(10.3d, 20.4d, 30.5d, 40.6d);
		RectangleArea result = rectangle.floor();

		assertThat(result, is(new RectangleArea(10d, 20d, 30d, 40d)));
	}

	/**
	 * 四捨五入テスト
	 */
	@Test
	public void testRound() throws Exception {
		RectangleArea rectangle = new RectangleArea(10.3d, 20.4d, 30.5d, 40.6d);
		RectangleArea result = rectangle.round();

		assertThat(result, is(new RectangleArea(10d, 20d, 31d, 41d)));
	}

	/**
	 * 切り上げテスト
	 */
	@Test
	public void testCeil() throws Exception {
		RectangleArea rectangle = new RectangleArea(10.3d, 20.4d, 30.5d, 40.6d);
		RectangleArea result = rectangle.ceil();

		assertThat(result, is(new RectangleArea(11d, 21d, 31d, 41d)));
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