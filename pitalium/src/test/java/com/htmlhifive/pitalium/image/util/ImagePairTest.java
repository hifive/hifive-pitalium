package com.htmlhifive.pitalium.image.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.htmlhifive.pitalium.image.model.ComparedRectangleArea;
import com.htmlhifive.pitalium.image.model.DiffCategory;
import com.htmlhifive.pitalium.image.model.Offset;

public class ImagePairTest {
	/**
	 * shiftの差分を検出するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetComparedRectanglesShift() throws Exception {
		BufferedImage expectedImage = ImageIO.read(new File("src/test/resources/images/imagePair/shift_expected.png"));
		BufferedImage targetImage = ImageIO.read(new File("src/test/resources/images/imagePair/shift_actual.png"));

		ImagePair imagePair = new ImagePair(expectedImage, targetImage);
		imagePair.compareImagePairAll();
		List<ComparedRectangleArea> comparedRectangles = imagePair.getComparedRectangles();

		assertThat(comparedRectangles.size(), is(1));
		assertThat(comparedRectangles.get(0).getCategory(), is(DiffCategory.SHIFT));
	}

	/**
	 * missingの差分を検出するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetComparedRectanglesMissing() throws Exception {
		BufferedImage expectedImage = ImageIO
				.read(new File("src/test/resources/images/imagePair/missing_expected.png"));
		BufferedImage targetImage = ImageIO.read(new File("src/test/resources/images/imagePair/missing_actual.png"));

		ImagePair imagePair = new ImagePair(expectedImage, targetImage);
		imagePair.compareImagePairAll();
		List<ComparedRectangleArea> comparedRectangles = imagePair.getComparedRectangles();

		assertThat(comparedRectangles.size(), is(1));
		assertThat(comparedRectangles.get(0).getCategory(), is(DiffCategory.MISSING));
	}

	/**
	 * scalingの差分を検出するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetComparedRectanglesScaling() throws Exception {
		BufferedImage expectedImage = ImageIO
				.read(new File("src/test/resources/images/imagePair/scaling_expected.png"));
		BufferedImage targetImage = ImageIO.read(new File("src/test/resources/images/imagePair/scaling_actual.png"));

		ImagePair imagePair = new ImagePair(expectedImage, targetImage);
		imagePair.compareImagePairAll();
		List<ComparedRectangleArea> comparedRectangles = imagePair.getComparedRectangles();

		assertThat(comparedRectangles.size(), is(1));
		assertThat(comparedRectangles.get(0).getCategory(), is(DiffCategory.SCALING));
	}

	/**
	 * textの差分を検出するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetComparedRectanglesText() throws Exception {
		BufferedImage expectedImage = ImageIO.read(new File("src/test/resources/images/imagePair/text_expected.png"));
		BufferedImage targetImage = ImageIO.read(new File("src/test/resources/images/imagePair/text_actual.png"));

		ImagePair imagePair = new ImagePair(expectedImage, targetImage);
		imagePair.compareImagePairAll();
		List<ComparedRectangleArea> comparedRectangles = imagePair.getComparedRectangles();

		assertThat(comparedRectangles.size(), is(1));
		assertThat(comparedRectangles.get(0).getCategory(), is(DiffCategory.TEXT));
	}

	/**
	 * similarの差分を検出するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetComparedRectanglesSimilar() throws Exception {
		BufferedImage expectedImage = ImageIO
				.read(new File("src/test/resources/images/imagePair/similar_expected.png"));
		BufferedImage targetImage = ImageIO.read(new File("src/test/resources/images/imagePair/similar_actual.png"));

		ImagePair imagePair = new ImagePair(expectedImage, targetImage);
		imagePair.compareImagePairAll();
		List<ComparedRectangleArea> comparedRectangles = imagePair.getComparedRectangles();

		assertThat(comparedRectangles.size(), is(1));
		assertThat(comparedRectangles.get(0).getCategory(), is(DiffCategory.SIMILAR));
	}

	/**
	 * 2枚の画像の全体の類似度を取得するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetEntireSimilarity() throws Exception {
		BufferedImage expectedImage = ImageIO
				.read(new File("src/test/resources/images/imagePair/missing_expected.png"));
		BufferedImage targetImage = ImageIO.read(new File("src/test/resources/images/imagePair/missing_actual.png"));

		ImagePair imagePair = new ImagePair(expectedImage, targetImage);
		imagePair.compareImagePairAll();

		// expectedの値は事前に計算したImageUtils.getEntireSimilarityの結果を使用
		double expected = 0.75940975;
		double actual = imagePair.getEntireSimilarity();

		assertThat(actual, is(expected));
	}

	/**
	 * 異なる領域間の最小類似度を取得するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetMinSimilarity() throws Exception {
		BufferedImage expectedImage = ImageIO
				.read(new File("src/test/resources/images/imagePair/missing_expected.png"));
		BufferedImage targetImage = ImageIO.read(new File("src/test/resources/images/imagePair/missing_actual.png"));

		ImagePair imagePair = new ImagePair(expectedImage, targetImage);
		imagePair.compareImagePairAll();

		// expectedの値は事前に計算したImageUtils.getMinSimilarityの結果を使用
		double expected = 0.19;
		double actual = imagePair.getMinSimilarity();

		assertThat(actual, is(expected));
	}

	/**
	 * 2枚の画像のdominantOffsetを取得するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetDominantOffset() throws Exception {
		BufferedImage expectedImage = ImageIO
				.read(new File("src/test/resources/images/imagePair/missing_expected.png"));
		BufferedImage targetImage = ImageIO.read(new File("src/test/resources/images/imagePair/missing_actual.png"));

		ImagePair imagePair = new ImagePair(expectedImage, targetImage);
		imagePair.compareImagePairAll();

		Offset expected = new Offset(0, 0);
		Offset actual = imagePair.getDominantOffset();

		assertThat(actual.getX(), is(expected.getX()));
		assertThat(actual.getY(), is(expected.getY()));
	}

	/**
	 * dominatOffsetを適用する必要があるかどうかを返すテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testIsExpectedMoved() throws Exception {
		BufferedImage expectedImage = ImageIO
				.read(new File("src/test/resources/images/imagePair/missing_expected.png"));
		BufferedImage targetImage = ImageIO.read(new File("src/test/resources/images/imagePair/missing_actual.png"));

		ImagePair imagePair = new ImagePair(expectedImage, targetImage);
		imagePair.compareImagePairAll();

		boolean actual = imagePair.isExpectedMoved();

		assertThat(actual, is(false));
	}
}
