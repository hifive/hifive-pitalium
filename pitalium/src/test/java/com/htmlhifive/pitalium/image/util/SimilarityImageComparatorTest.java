package com.htmlhifive.pitalium.image.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.htmlhifive.pitalium.image.model.ImageComparedResult;
import com.htmlhifive.pitalium.image.model.SimilarityComparisonParameters;

public class SimilarityImageComparatorTest {
	/**
	 * 比較結果がパラメータの閾値よりも小さければ不一致になるテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCompareLessThanParam() throws Exception {
		BufferedImage expectedImage = ImageIO.read(new File("src/test/resources/images/hifive_logo.png"));
		BufferedImage targetImage = ImageIO.read(new File("src/test/resources/images/hifive_logo_similar.png"));

		// 各Thresholdの値はhifive_logo.pngとhifive_logo_similar.pngの類似度を事前に計算したSimilarityUtils.calcSimilarityの結果を使用
		double pixleByPixelThreshold = 1.00;
		double featherMatrixThreshold = 0.98;
		double thresDiffThreshold = 0.98;
		double totalDiffThreshold = 0.97;
		SimilarityComparisonParameters param = new SimilarityComparisonParameters(pixleByPixelThreshold,
				featherMatrixThreshold, thresDiffThreshold, totalDiffThreshold);
		SimilarityImageComparator similarityImageComparator = new SimilarityImageComparator(param);

		ImageComparedResult result = similarityImageComparator.compare(expectedImage, targetImage);

		assertThat(result.isSucceeded(), is(false));
	}

	/**
	 * 比較結果がパラメータの閾値と同じならば一致になるテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCompareEqualParam() throws Exception {
		BufferedImage expectedImage = ImageIO.read(new File("src/test/resources/images/hifive_logo.png"));
		BufferedImage targetImage = ImageIO.read(new File("src/test/resources/images/hifive_logo_similar.png"));

		// 各Thresholdの値はhifive_logo.pngとhifive_logo_similar.pngの類似度を事前に計算したSimilarityUtils.calcSimilarityの結果を使用
		double pixleByPixelThreshold = 0.99;
		double featherMatrixThreshold = 0.97;
		double thresDiffThreshold = 0.97;
		double totalDiffThreshold = 0.96;
		SimilarityComparisonParameters param = new SimilarityComparisonParameters(pixleByPixelThreshold,
				featherMatrixThreshold, thresDiffThreshold, totalDiffThreshold);
		SimilarityImageComparator similarityImageComparator = new SimilarityImageComparator(param);

		ImageComparedResult result = similarityImageComparator.compare(expectedImage, targetImage);

		assertThat(result.isSucceeded(), is(true));
	}

	/**
	 * 比較結果がパラメータの閾値よりも大きければ一致になるテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCompareGreaterThanParam() throws Exception {
		BufferedImage expectedImage = ImageIO.read(new File("src/test/resources/images/hifive_logo.png"));
		BufferedImage targetImage = ImageIO.read(new File("src/test/resources/images/hifive_logo_similar.png"));

		// 各Thresholdの値はhifive_logo.pngとhifive_logo_similar.pngの類似度を事前に計算したSimilarityUtils.calcSimilarityの結果を使用
		double pixleByPixelThreshold = 0.98;
		double featherMatrixThreshold = 0.96;
		double thresDiffThreshold = 0.96;
		double totalDiffThreshold = 0.95;
		SimilarityComparisonParameters param = new SimilarityComparisonParameters(pixleByPixelThreshold,
				featherMatrixThreshold, thresDiffThreshold, totalDiffThreshold);
		SimilarityImageComparator similarityImageComparator = new SimilarityImageComparator(param);

		ImageComparedResult result = similarityImageComparator.compare(expectedImage, targetImage);

		assertThat(result.isSucceeded(), is(true));
	}

}
