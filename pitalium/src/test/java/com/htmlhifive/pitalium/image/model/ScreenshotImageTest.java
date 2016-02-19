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
package com.htmlhifive.pitalium.image.model;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import com.htmlhifive.pitalium.image.util.ImageUtils;

public class ScreenshotImageTest {

	public BufferedImage image;

	@Before
	public void loadImage() throws Exception {
		image = ImageIO.read(getClass().getResource("ScreenshotImageTest_image.png"));
	}

	/**
	 * 画像がキャッシュされているかどうかのテスト。常にTRUE。
	 */
	@Test
	public void testIsImageCached() throws Exception {
		ScreenshotImage si = new ScreenshotImage(image);
		assertThat(si.isImageCached(), is(true));
	}

	/**
	 * 画像を取得するテスト。コンストラクタで渡した画像と同じ。
	 */
	@Test
	public void testGet() throws Exception {
		ScreenshotImage si = new ScreenshotImage(image);
		assertThat(si.get(), is(image));
	}

	/**
	 * 画像をストリームとして取得するテスト。コンストラクタで渡した画像と同じ。
	 */
	@Test
	public void testGetAsStream() throws Exception {
		ScreenshotImage si = new ScreenshotImage(image);
		BufferedImage actual = ImageIO.read(si.getAsStream());
		assertThat(ImageUtils.imageEquals(actual, image), is(true));
	}

}
