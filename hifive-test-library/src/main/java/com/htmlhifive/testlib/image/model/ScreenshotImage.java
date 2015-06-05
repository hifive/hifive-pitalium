/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.image.model;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htmlhifive.testlib.common.exception.TestRuntimeException;

/**
 * スクリーンショット画像を表すクラス
 */
public class ScreenshotImage {

	private static final Logger LOG = LoggerFactory.getLogger(ScreenshotImage.class);

	protected BufferedImage image;

	/**
	 * 初期化します。
	 */
	public ScreenshotImage() {
		this(null);
	}

	/**
	 * 画像を持ったオブジェクトを初期化します。
	 * 
	 * @param image 画像
	 */
	public ScreenshotImage(BufferedImage image) {
		this.image = image;
	}

	/**
	 * 画像がメモリ上にキャッシュされているかどうか取得します。
	 * 
	 * @return キャッシュされている場合true、されていない場合false
	 */
	public boolean isImageCached() {
		return image != null;
	}

	/**
	 * スクリーンショットの画像を取得します。
	 * 
	 * @return スクリーンショットの画像
	 */
	public BufferedImage get() {
		if (image == null) {
			throw new IllegalStateException("image is null");
		}

		return image;
	}

	/**
	 * スクリーンショット画像を取得できるストリームを取得します。
	 * 
	 * @return 画像の入力ストリーム
	 */
	public InputStream getAsStream() {
		if (image == null) {
			throw new IllegalStateException("image is null");
		}

		return getInputStreamFromImage(image);
	}

	/**
	 * 画像からInputStreamを取得します。
	 * 
	 * @param image 画像
	 * @return InputStream
	 */
	protected static InputStream getInputStreamFromImage(BufferedImage image) {
		final int initialBuffer = 1024 * 1024;
		ByteArrayOutputStream bao = new ByteArrayOutputStream(initialBuffer);
		try {
			ImageIO.write(image, "png", bao);
		} catch (IOException e) {
			throw new TestRuntimeException(e);
		}

		return new ByteArrayInputStream(bao.toByteArray());
	}

}
