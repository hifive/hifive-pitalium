/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.image.model;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.Platform;

import com.htmlhifive.testlib.common.exception.TestRuntimeException;
import com.htmlhifive.testlib.core.config.FilePersisterConfig;
import com.htmlhifive.testlib.core.io.FilePersister;
import com.htmlhifive.testlib.core.io.PersistMetadata;
import com.htmlhifive.testlib.core.io.Persister;
import com.htmlhifive.testlib.core.io.ResourceUnavailableException;
import com.htmlhifive.testlib.core.model.IndexDomSelector;
import com.htmlhifive.testlib.core.model.SelectorType;
import com.htmlhifive.testlib.core.selenium.MrtCapabilities;
import com.htmlhifive.testlib.image.util.ImageUtils;

public class PersistedScreenshotImageTest {

	private static final String DIRECTORY_NAME = "screenshotImageTest";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private Persister persister;
	private PersistMetadata metadata;

	@Before
	public void setUp() throws Exception {
		persister = new FilePersister(FilePersisterConfig.builder().resultDirectory(DIRECTORY_NAME).build());

		MrtCapabilities capabilities = new MrtCapabilities(new HashMap<String, Object>());
		capabilities.setPlatform(Platform.WINDOWS);
		capabilities.setBrowserName("firefox");
		capabilities.setVersion("38");
		IndexDomSelector selector = new IndexDomSelector(SelectorType.ID, "main", 0);
		metadata = new PersistMetadata("testId", "testClass", "testMethod", "scId", selector, null, capabilities);
	}

	@Before
	@After
	public void deleteDirectory() throws Exception {
		FileUtils.deleteDirectory(new File(DIRECTORY_NAME));
	}

	private BufferedImage getImage() throws Exception {
		return ImageIO.read(getClass().getResource("ScreenshotImageTest_image.png"));
	}

	//<editor-fold desc="construct">

	/**
	 * PersisterをnullでScreenshotImageを初期化するとIllegalArgumentException。
	 */
	@Test
	public void testConstruct_persister_null() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		new PersistedScreenshotImage(null, metadata);
	}

	/**
	 * metadataをnullでScreenshotImageを初期化するとIllegalArgumentException。
	 */
	@Test
	public void testConstruct_metadata_null() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		new PersistedScreenshotImage(persister, null);
	}

	//</editor-fold>

	//<editor-fold desc="isCached">

	/**
	 * 画像がキャッシュされているかどうかのテスト。されていない場合。
	 */
	@Test
	public void testIsCached_not_cached() throws Exception {
		ScreenshotImage image = new PersistedScreenshotImage(persister, metadata);
		assertThat(image.isImageCached(), is(false));
	}

	/**
	 * 画像がキャッシュされているかどうかのテスト。されている場合。
	 */
	@Test
	public void testIsCached_cached() throws Exception {
		ScreenshotImage image = new PersistedScreenshotImage(persister, metadata, getImage());
		assertThat(image.isImageCached(), is(true));
	}

	/**
	 * 画像の取得。キャッシュなし、ファイル無しはResourceUnavailableException。
	 */
	@Test
	public void testGet_image_not_exist() throws Exception {
		ScreenshotImage image = new PersistedScreenshotImage(persister, metadata);

		expectedException.expect(ResourceUnavailableException.class);
		image.get();
	}

	//</editor-fold>

	//<editor-fold desc="get">

	/**
	 * 画像の取得。キャッシュ無し、ファイル有り。
	 */
	@Test
	public void testGet_image_exists() throws Exception {
		BufferedImage expected = getImage();
		persister.saveScreenshot(metadata, expected);

		ScreenshotImage image = new PersistedScreenshotImage(persister, metadata);
		assertThat(image.isImageCached(), is(false));

		BufferedImage actual = image.get();
		assertThat(ImageUtils.imageEquals(expected, actual), is(true));

		assertThat(image.isImageCached(), is(true));

		BufferedImage i = image.get();
		assertThat(i, is(sameInstance(actual)));
	}

	/**
	 * 画像の取得。キャッシュ有り。
	 */
	@Test
	public void testGet() throws Exception {
		BufferedImage i = getImage();
		ScreenshotImage image = new PersistedScreenshotImage(persister, metadata, i);

		assertThat(image.get(), is(sameInstance(i)));
	}

	//</editor-fold>

	//<editor-fold desc="getAsStream">

	/**
	 * 画像をストリームとして取得。キャッシュ無し、ファイル無しはResourceUnavailableException。
	 */
	@Test
	public void testGetAsStream_image_not_exist() throws Exception {
		ScreenshotImage image = new PersistedScreenshotImage(persister, metadata);

		expectedException.expect(ResourceUnavailableException.class);
		image.getAsStream();
	}

	/**
	 * 画像をストリームとして取得。キャッシュ無し、ファイル有り。
	 */
	@Test
	public void testGetAsStream_image_exists() throws Exception {
		BufferedImage expected = getImage();
		persister.saveScreenshot(metadata, expected);

		PersistedScreenshotImage image = new PersistedScreenshotImage(persister, metadata);
		InputStream in = null;
		try {
			in = image.getImageStream();
			BufferedImage actual = ImageIO.read(in);
			assertThat(ImageUtils.imageEquals(expected, actual), is(true));
		} finally {
			if (in != null) {
				in.close();
			}
		}

		assertThat(image.isImageCached(), is(false));
	}

	/**
	 * 画像をストリームとして取得。画像有り。
	 */
	@Test
	public void testGetAsStream() throws Exception {
		BufferedImage expected = getImage();
		ScreenshotImage image = new PersistedScreenshotImage(persister, metadata, expected);
		InputStream in = null;
		try {
			in = image.getAsStream();
			BufferedImage actual = ImageIO.read(in);

			assertThat(ImageUtils.imageEquals(expected, actual), is(true));
		} finally {
			if (in != null) {
				in.close();
			}
		}

		assertThat(image.isImageCached(), is(true));
	}

	//</editor-fold>

}