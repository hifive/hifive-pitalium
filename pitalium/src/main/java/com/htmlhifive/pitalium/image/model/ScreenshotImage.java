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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htmlhifive.pitalium.common.exception.TestRuntimeException;

/**
 * スクリーンショット画像を表すクラス
 */
public class ScreenshotImage {

	private static final Logger LOG = LoggerFactory.getLogger(ScreenshotImage.class);

	/**
	 * スクリーンショット画像
	 */
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

	@Override
	public String toString() {
		if (image == null) {
			return "ScreenshotImage: not cached.";
		}

		return "ScreenshotImage[width: " + image.getWidth() + "; height: " + image.getHeight() + "].";
	}

}
