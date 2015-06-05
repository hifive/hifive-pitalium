/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.image.model;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import com.htmlhifive.testlib.image.util.ImageUtils;

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
