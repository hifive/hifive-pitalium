package com.htmlhifive.pitalium.image.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.htmlhifive.pitalium.image.model.ComparedRectangleArea;
import com.htmlhifive.pitalium.image.model.Offset;
import com.htmlhifive.pitalium.image.model.SimilarityUnit;

public class SimilarityUtilsTest {
	/**
	 * 類似度計算、全く同じ画像なら類似度1.0になるテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCalcSimilarity() throws Exception {
		BufferedImage image1 = ImageIO.read(new File("src/test/resources/images/hifive_logo.png"));
		BufferedImage image2 = ImageIO.read(new File("src/test/resources/images/hifive_logo.png"));

		Rectangle rectangle = new Rectangle(0, 0, image1.getWidth(), image1.getHeight());
		ComparedRectangleArea similarRectangle = new ComparedRectangleArea(rectangle);
		Offset offset = new Offset(0, 0);

		SimilarityUnit unit;
		double actual;

		unit = SimilarityUtils.calcSimilarity(image1, image2, rectangle, similarRectangle, offset);
		actual = unit.getSimilarityThresDiff();
		assertThat(actual, is(1.0));

		unit = SimilarityUtils.calcSimilarity(image1, image2, rectangle, similarRectangle, offset, 1.0);
		actual = unit.getSimilarityThresDiff();
		assertThat(actual, is(1.0));
	}

	/**
	 * 類似度計算、異なる画像なら類似度1.0にならないテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCalcSimilarityWithDiff() throws Exception {
		BufferedImage image1 = ImageIO.read(new File("src/test/resources/images/hifive_logo.png"));
		BufferedImage image2 = ImageIO.read(new File("src/test/resources/images/hifive_logo_similar.png"));

		Rectangle rectangle = new Rectangle(0, 0, image1.getWidth(), image1.getHeight());
		ComparedRectangleArea similarRectangle = new ComparedRectangleArea(rectangle);
		Offset offset = new Offset(0, 0);

		// expectedの値はhifive_logo.pngとhifive_logo_similar.pngの類似度を事前に計算したSimilarityUtils.calcSimilarityの結果を使用
		SimilarityUnit unit;
		double actual;

		unit = SimilarityUtils.calcSimilarity(image1, image2, rectangle, similarRectangle, offset);
		actual = unit.getSimilarityThresDiff();
		assertThat(actual, is(0.97));

		unit = SimilarityUtils.calcSimilarity(image1, image2, rectangle, similarRectangle, offset, 1.0);
		actual = unit.getSimilarityThresDiff();
		assertThat(actual, is(0.97));
	}

	/**
	 * 全く同じFeatureなら距離が0.0になるテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCalcFeatureDistance() throws Exception {
		Color[][] feature1 = new Color[5][5];
		Color[][] feature2 = new Color[5][5];
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				feature1[i][j] = new Color(0xff, 0xff, 0xff);
				feature2[i][j] = new Color(0xff, 0xff, 0xff);
			}
		}

		double actual = SimilarityUtils.calcFeatureDistance(feature1, feature2);
		assertThat(actual, is(0.0));
	}

	/**
	 * 異なるFeatureなら距離が0.0にならないテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCalcFeatureDistanceWithDiff() throws Exception {
		Color[][] feature1 = new Color[5][5];
		Color[][] feature2 = new Color[5][5];
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				feature1[i][j] = new Color(0xff, 0xff, 0xff);
				feature2[i][j] = new Color(0x00, 0x00, 0x00);
			}
		}

		double actual = SimilarityUtils.calcFeatureDistance(feature1, feature2);
		// expectedの値は事前に計算したSimilarityUtils.calcFeatureDistanceの結果を使用
		assertThat(actual, is(1.0));
	}

	/**
	 * featureSizeがfeature matrix以上の場合trueになるテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCheckFeatureSize() throws Exception {
		boolean actual = SimilarityUtils.checkFeatureSize(5, 5);

		assertThat(actual, is(true));
	}

	/**
	 * featureSizeがfeature matrix未満の場合falseになるテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCheckFeatureSizeLess() throws Exception {
		boolean actual = SimilarityUtils.checkFeatureSize(4, 4);

		assertThat(actual, is(false));
	}

	/**
	 * featureMatrixによる類似度計算、全く同じ画像なら類似度1.0になるテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCalcSimilarityByFeatureMatrix() throws Exception {
		BufferedImage image1 = ImageIO.read(new File("src/test/resources/images/hifive_logo.png"));
		BufferedImage image2 = ImageIO.read(new File("src/test/resources/images/hifive_logo.png"));

		Rectangle rectangle = new Rectangle(0, 0, image1.getWidth(), image1.getHeight());
		Offset offset = new Offset(0, 0);

		double actual;

		actual = SimilarityUtils.calcSimilarityByFeatureMatrix(image1, image2, rectangle, offset);
		assertThat(actual, is(1.0));

		Rectangle frame1 = new Rectangle(0, 0, image1.getWidth(), image1.getHeight());
		Rectangle frame2 = new Rectangle(0, 0, image2.getWidth(), image2.getHeight());

		actual = SimilarityUtils.calcSimilarityByFeatureMatrix(image1, image2, frame1, frame2);
		assertThat(actual, is(1.0));
	}

	/**
	 * featureMatrixによる類似度計算、異なる画像なら類似度1.0にならないテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCalcSimilarityByFeatureMatrixWithDiff() throws Exception {
		BufferedImage image1 = ImageIO.read(new File("src/test/resources/images/hifive_logo.png"));
		BufferedImage image2 = ImageIO.read(new File("src/test/resources/images/hifive_logo_similar.png"));

		Rectangle rectangle = new Rectangle(0, 0, image1.getWidth(), image1.getHeight());
		Offset offset = new Offset(0, 0);

		// expectedの値はhifive_logo.pngとhifive_logo_similar.pngの類似度を事前に計算したSimilarityUtils.calcSimilarityByFeatureMatrixの結果を使用
		double actual;

		actual = SimilarityUtils.calcSimilarityByFeatureMatrix(image1, image2, rectangle, offset);
		assertThat(actual, is(0.97));

		Rectangle frame1 = new Rectangle(0, 0, image1.getWidth(), image1.getHeight());
		Rectangle frame2 = new Rectangle(0, 0, image2.getWidth(), image2.getHeight());

		actual = SimilarityUtils.calcSimilarityByFeatureMatrix(image1, image2, frame1, frame2);
		assertThat(actual, is(0.97));
	}

	/**
	 * pixel by pixelによる類似度計算、全く同じ画像なら類似度1.0になるテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCalcSimilarityPixelByPixel() throws Exception {
		BufferedImage image1 = ImageIO.read(new File("src/test/resources/images/hifive_logo.png"));
		BufferedImage image2 = ImageIO.read(new File("src/test/resources/images/hifive_logo.png"));

		Rectangle rectangle = new Rectangle(0, 0, image1.getWidth(), image1.getHeight());
		Offset offset = new Offset(0, 0);

		SimilarityUnit calcSimilarityPixelByPixel = SimilarityUtils.calcSimilarityPixelByPixel(image1, image2,
				rectangle, offset);
		double actual = calcSimilarityPixelByPixel.getSimilarityPixelByPixel();
		assertThat(actual, is(1.0));
	}

	/**
	 * pixel by pixelによる類似度計算、異なる画像なら類似度1.0にならないテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCalcSimilarityPixelByPixelWithDiff() throws Exception {
		BufferedImage image1 = ImageIO.read(new File("src/test/resources/images/hifive_logo.png"));
		BufferedImage image2 = ImageIO.read(new File("src/test/resources/images/hifive_logo_similar.png"));
		Rectangle rectangle = new Rectangle(0, 0, image1.getWidth(), image1.getHeight());
		Offset offset = new Offset(0, 0);

		SimilarityUnit calcSimilarityPixelByPixel = SimilarityUtils.calcSimilarityPixelByPixel(image1, image2,
				rectangle, offset);
		double actual = calcSimilarityPixelByPixel.getSimilarityPixelByPixel();
		// expectedの値はhifive_logo.pngとhifive_logo_similar.pngの類似度を事前に計算したSimilarityUtils.getSimilarityPixelByPixelの結果を使用
		assertThat(actual, is(0.99));
	}
}
