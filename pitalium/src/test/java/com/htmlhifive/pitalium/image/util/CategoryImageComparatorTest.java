package com.htmlhifive.pitalium.image.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.htmlhifive.pitalium.image.model.CategoryComparisonParameters;
import com.htmlhifive.pitalium.image.model.CategoryImageComparedResult;
import com.htmlhifive.pitalium.image.model.CompareOption;
import com.htmlhifive.pitalium.image.model.CompareOptionType;
import com.htmlhifive.pitalium.image.model.DiffCategory;

public class CategoryImageComparatorTest {
	/**
	 * missingの差分を容認するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCompareMissingSucceeded() throws Exception {
		BufferedImage expectedImage = ImageIO
				.read(new File("src/test/resources/images/categoryImageComparator/missing_expected.png"));
		Rectangle expectedRectangle = new Rectangle(0, 0, expectedImage.getWidth(), expectedImage.getHeight());
		// missingの差分がある画像
		BufferedImage actualImage = ImageIO
				.read(new File("src/test/resources/images/categoryImageComparator/missing_actual.png"));
		Rectangle actualRectangle = new Rectangle(0, 0, actualImage.getWidth(), actualImage.getHeight());

		// missingの差分を容認するカテゴリ比較を行うComparatorを作成
		DiffCategory[] acceptCategories = new DiffCategory[] { DiffCategory.MISSING };
		CategoryComparisonParameters parameters = new CategoryComparisonParameters(acceptCategories);
		CompareOption option = new CompareOption(CompareOptionType.CATEGORY, parameters);
		CategoryImageComparator categoryImageComparator = new CategoryImageComparator(
				(CategoryComparisonParameters) option.getParameters());

		CategoryImageComparedResult result = (CategoryImageComparedResult) categoryImageComparator
				.compare(expectedImage, expectedRectangle, actualImage, actualRectangle);

		assertThat(result.isSucceeded(), is(true));
		assertThat(result.getComparedRectangles().size(), is(1));
		assertThat(result.getComparedRectangles().get(0).getCategory(), is(DiffCategory.MISSING));
	}

	/**
	 * missingの差分を容認するが、missing以外の差分がある場合画像が一致しないテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCompareMissingFailed() throws Exception {
		BufferedImage expectedImage = ImageIO
				.read(new File("src/test/resources/images/categoryImageComparator/missing_expected.png"));
		Rectangle expectedRectangle = new Rectangle(0, 0, expectedImage.getWidth(), expectedImage.getHeight());
		// missingの差分とtextの差分がある画像
		BufferedImage actualImage = ImageIO
				.read(new File("src/test/resources/images/categoryImageComparator/missing_actual_failed.png"));
		Rectangle actualRectangle = new Rectangle(0, 0, actualImage.getWidth(), actualImage.getHeight());

		// missingの差分を容認するカテゴリ比較を行うComparatorを作成
		DiffCategory[] acceptCategories = new DiffCategory[] { DiffCategory.MISSING };
		CategoryComparisonParameters parameters = new CategoryComparisonParameters(acceptCategories);
		CompareOption option = new CompareOption(CompareOptionType.CATEGORY, parameters);
		CategoryImageComparator categoryImageComparator = new CategoryImageComparator(
				(CategoryComparisonParameters) option.getParameters());

		CategoryImageComparedResult result = (CategoryImageComparedResult) categoryImageComparator
				.compare(expectedImage, expectedRectangle, actualImage, actualRectangle);

		assertThat(result.isSucceeded(), is(false));
		assertThat(result.getComparedRectangles().size(), is(2));
		assertThat(result.getComparedRectangles().get(0).getCategory(), is(DiffCategory.TEXT));
		assertThat(result.getComparedRectangles().get(1).getCategory(), is(DiffCategory.MISSING));
	}

	/**
	 * shiftの差分を容認するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCompareShiftSucceeded() throws Exception {
		BufferedImage expectedImage = ImageIO
				.read(new File("src/test/resources/images/categoryImageComparator/shift_expected.png"));
		Rectangle expectedRectangle = new Rectangle(0, 0, expectedImage.getWidth(), expectedImage.getHeight());
		// shiftの差分がある画像
		BufferedImage actualImage = ImageIO
				.read(new File("src/test/resources/images/categoryImageComparator/shift_actual.png"));
		Rectangle actualRectangle = new Rectangle(0, 0, actualImage.getWidth(), actualImage.getHeight());

		// shiftの差分を容認するカテゴリ比較を行うComparatorを作成
		DiffCategory[] acceptCategories = new DiffCategory[] { DiffCategory.SHIFT };
		CategoryComparisonParameters parameters = new CategoryComparisonParameters(acceptCategories);
		CompareOption option = new CompareOption(CompareOptionType.CATEGORY, parameters);
		CategoryImageComparator categoryImageComparator = new CategoryImageComparator(
				(CategoryComparisonParameters) option.getParameters());

		CategoryImageComparedResult result = (CategoryImageComparedResult) categoryImageComparator
				.compare(expectedImage, expectedRectangle, actualImage, actualRectangle);

		assertThat(result.isSucceeded(), is(true));
		assertThat(result.getComparedRectangles().size(), is(1));
		assertThat(result.getComparedRectangles().get(0).getCategory(), is(DiffCategory.SHIFT));
	}

	/**
	 * shiftの差分を容認するが、shift以外の差分がある場合画像が一致しないテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCompareShiftFailed() throws Exception {
		BufferedImage expectedImage = ImageIO
				.read(new File("src/test/resources/images/categoryImageComparator/shift_expected.png"));
		Rectangle expectedRectangle = new Rectangle(0, 0, expectedImage.getWidth(), expectedImage.getHeight());
		// shiftの差分とtextの差分がある画像
		BufferedImage actualImage = ImageIO
				.read(new File("src/test/resources/images/categoryImageComparator/shift_actual_failed.png"));
		Rectangle actualRectangle = new Rectangle(0, 0, actualImage.getWidth(), actualImage.getHeight());

		// shiftの差分を容認するカテゴリ比較を行うComparatorを作成
		DiffCategory[] acceptCategories = new DiffCategory[] { DiffCategory.SHIFT };
		CategoryComparisonParameters parameters = new CategoryComparisonParameters(acceptCategories);
		CompareOption option = new CompareOption(CompareOptionType.CATEGORY, parameters);
		CategoryImageComparator categoryImageComparator = new CategoryImageComparator(
				(CategoryComparisonParameters) option.getParameters());

		CategoryImageComparedResult result = (CategoryImageComparedResult) categoryImageComparator
				.compare(expectedImage, expectedRectangle, actualImage, actualRectangle);

		assertThat(result.isSucceeded(), is(false));
		assertThat(result.getComparedRectangles().size(), is(2));
		assertThat(result.getComparedRectangles().get(0).getCategory(), is(DiffCategory.TEXT));
		assertThat(result.getComparedRectangles().get(1).getCategory(), is(DiffCategory.SHIFT));
	}

	/**
	 * textの差分を容認するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCompareTextSucceeded() throws Exception {
		BufferedImage expectedImage = ImageIO
				.read(new File("src/test/resources/images/categoryImageComparator/text_expected.png"));
		Rectangle expectedRectangle = new Rectangle(0, 0, expectedImage.getWidth(), expectedImage.getHeight());
		// textの差分がある画像
		BufferedImage actualImage = ImageIO
				.read(new File("src/test/resources/images/categoryImageComparator/text_actual.png"));
		Rectangle actualRectangle = new Rectangle(0, 0, actualImage.getWidth(), actualImage.getHeight());

		// textの差分を容認するカテゴリ比較を行うComparatorを作成
		DiffCategory[] acceptCategories = new DiffCategory[] { DiffCategory.TEXT };
		CategoryComparisonParameters parameters = new CategoryComparisonParameters(acceptCategories);
		CompareOption option = new CompareOption(CompareOptionType.CATEGORY, parameters);
		CategoryImageComparator categoryImageComparator = new CategoryImageComparator(
				(CategoryComparisonParameters) option.getParameters());

		CategoryImageComparedResult result = (CategoryImageComparedResult) categoryImageComparator
				.compare(expectedImage, expectedRectangle, actualImage, actualRectangle);

		assertThat(result.isSucceeded(), is(true));
		assertThat(result.getComparedRectangles().size(), is(1));
		assertThat(result.getComparedRectangles().get(0).getCategory(), is(DiffCategory.TEXT));
	}

	/**
	 * textの差分を容認するが、text以外の差分がある場合画像が一致しないテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCompareTextFailed() throws Exception {
		BufferedImage expectedImage = ImageIO
				.read(new File("src/test/resources/images/categoryImageComparator/text_expected.png"));
		Rectangle expectedRectangle = new Rectangle(0, 0, expectedImage.getWidth(), expectedImage.getHeight());
		// textの差分とmissingの差分がある画像
		BufferedImage actualImage = ImageIO
				.read(new File("src/test/resources/images/categoryImageComparator/text_actual_failed.png"));
		Rectangle actualRectangle = new Rectangle(0, 0, actualImage.getWidth(), actualImage.getHeight());

		// textの差分を容認するカテゴリ比較を行うComparatorを作成
		DiffCategory[] acceptCategories = new DiffCategory[] { DiffCategory.TEXT };
		CategoryComparisonParameters parameters = new CategoryComparisonParameters(acceptCategories);
		CompareOption option = new CompareOption(CompareOptionType.CATEGORY, parameters);
		CategoryImageComparator categoryImageComparator = new CategoryImageComparator(
				(CategoryComparisonParameters) option.getParameters());

		CategoryImageComparedResult result = (CategoryImageComparedResult) categoryImageComparator
				.compare(expectedImage, expectedRectangle, actualImage, actualRectangle);

		assertThat(result.isSucceeded(), is(false));
		assertThat(result.getComparedRectangles().size(), is(2));
		assertThat(result.getComparedRectangles().get(0).getCategory(), is(DiffCategory.MISSING));
		assertThat(result.getComparedRectangles().get(1).getCategory(), is(DiffCategory.TEXT));
	}

	/**
	 * scalingの差分を容認するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCompareScalingSucceeded() throws Exception {
		BufferedImage expectedImage = ImageIO
				.read(new File("src/test/resources/images/categoryImageComparator/scaling_expected.png"));
		Rectangle expectedRectangle = new Rectangle(0, 0, expectedImage.getWidth(), expectedImage.getHeight());
		// scalingの差分がある画像
		BufferedImage actualImage = ImageIO
				.read(new File("src/test/resources/images/categoryImageComparator/scaling_actual.png"));
		Rectangle actualRectangle = new Rectangle(0, 0, actualImage.getWidth(), actualImage.getHeight());

		// scalingの差分を容認するカテゴリ比較を行うComparatorを作成
		DiffCategory[] acceptCategories = new DiffCategory[] { DiffCategory.SCALING };
		CategoryComparisonParameters parameters = new CategoryComparisonParameters(acceptCategories);
		CompareOption option = new CompareOption(CompareOptionType.CATEGORY, parameters);
		CategoryImageComparator categoryImageComparator = new CategoryImageComparator(
				(CategoryComparisonParameters) option.getParameters());

		CategoryImageComparedResult result = (CategoryImageComparedResult) categoryImageComparator
				.compare(expectedImage, expectedRectangle, actualImage, actualRectangle);

		assertThat(result.isSucceeded(), is(true));
		assertThat(result.getComparedRectangles().size(), is(1));
		assertThat(result.getComparedRectangles().get(0).getCategory(), is(DiffCategory.SCALING));
	}

	/**
	 * scalingの差分を容認するが、scaling以外の差分がある場合画像が一致しないテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCompareScalingFailed() throws Exception {
		BufferedImage expectedImage = ImageIO
				.read(new File("src/test/resources/images/categoryImageComparator/scaling_expected.png"));
		Rectangle expectedRectangle = new Rectangle(0, 0, expectedImage.getWidth(), expectedImage.getHeight());
		// scalingの差分とtextの差分がある画像
		BufferedImage actualImage = ImageIO
				.read(new File("src/test/resources/images/categoryImageComparator/scaling_actual_failed.png"));
		Rectangle actualRectangle = new Rectangle(0, 0, actualImage.getWidth(), actualImage.getHeight());

		// scalingの差分を容認するカテゴリ比較を行うComparatorを作成
		DiffCategory[] acceptCategories = new DiffCategory[] { DiffCategory.SCALING };
		CategoryComparisonParameters parameters = new CategoryComparisonParameters(acceptCategories);
		CompareOption option = new CompareOption(CompareOptionType.CATEGORY, parameters);
		CategoryImageComparator categoryImageComparator = new CategoryImageComparator(
				(CategoryComparisonParameters) option.getParameters());

		CategoryImageComparedResult result = (CategoryImageComparedResult) categoryImageComparator
				.compare(expectedImage, expectedRectangle, actualImage, actualRectangle);

		assertThat(result.isSucceeded(), is(false));
		assertThat(result.getComparedRectangles().size(), is(2));
		assertThat(result.getComparedRectangles().get(0).getCategory(), is(DiffCategory.TEXT));
		assertThat(result.getComparedRectangles().get(1).getCategory(), is(DiffCategory.SCALING));
	}

	/**
	 * similarの差分を容認するテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCompareSimilarSucceeded() throws Exception {
		BufferedImage expectedImage = ImageIO
				.read(new File("src/test/resources/images/categoryImageComparator/similar_expected.png"));
		Rectangle expectedRectangle = new Rectangle(0, 0, expectedImage.getWidth(), expectedImage.getHeight());
		// similarの差分がある画像
		BufferedImage actualImage = ImageIO
				.read(new File("src/test/resources/images/categoryImageComparator/similar_actual.png"));
		Rectangle actualRectangle = new Rectangle(0, 0, actualImage.getWidth(), actualImage.getHeight());

		// similarの差分を容認するカテゴリ比較を行うComparatorを作成
		DiffCategory[] acceptCategories = new DiffCategory[] { DiffCategory.SIMILAR };
		CategoryComparisonParameters parameters = new CategoryComparisonParameters(acceptCategories);
		CompareOption option = new CompareOption(CompareOptionType.CATEGORY, parameters);
		CategoryImageComparator categoryImageComparator = new CategoryImageComparator(
				(CategoryComparisonParameters) option.getParameters());

		CategoryImageComparedResult result = (CategoryImageComparedResult) categoryImageComparator
				.compare(expectedImage, expectedRectangle, actualImage, actualRectangle);

		assertThat(result.isSucceeded(), is(true));
		assertThat(result.getComparedRectangles().size(), is(1));
		assertThat(result.getComparedRectangles().get(0).getCategory(), is(DiffCategory.SIMILAR));
	}

	/**
	 * similarの差分を容認するが、similar以外の差分がある場合画像が一致しないテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void testCompareSimilarFailed() throws Exception {
		BufferedImage expectedImage = ImageIO
				.read(new File("src/test/resources/images/categoryImageComparator/similar_expected.png"));
		Rectangle expectedRectangle = new Rectangle(0, 0, expectedImage.getWidth(), expectedImage.getHeight());
		// similarの差分とtextの差分がある画像
		BufferedImage actualImage = ImageIO
				.read(new File("src/test/resources/images/categoryImageComparator/similar_actual_failed.png"));
		Rectangle actualRectangle = new Rectangle(0, 0, actualImage.getWidth(), actualImage.getHeight());

		// similarの差分を容認するカテゴリ比較を行うComparatorを作成
		DiffCategory[] acceptCategories = new DiffCategory[] { DiffCategory.SIMILAR };
		CategoryComparisonParameters parameters = new CategoryComparisonParameters(acceptCategories);
		CompareOption option = new CompareOption(CompareOptionType.CATEGORY, parameters);
		CategoryImageComparator categoryImageComparator = new CategoryImageComparator(
				(CategoryComparisonParameters) option.getParameters());

		CategoryImageComparedResult result = (CategoryImageComparedResult) categoryImageComparator
				.compare(expectedImage, expectedRectangle, actualImage, actualRectangle);

		assertThat(result.isSucceeded(), is(false));
		assertThat(result.getComparedRectangles().size(), is(2));
		assertThat(result.getComparedRectangles().get(0).getCategory(), is(DiffCategory.TEXT));
		assertThat(result.getComparedRectangles().get(1).getCategory(), is(DiffCategory.SIMILAR));
	}

}
