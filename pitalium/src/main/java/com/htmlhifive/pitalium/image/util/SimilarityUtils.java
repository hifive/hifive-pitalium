/*
 * Copyright (C) 2015-2017 NS Solutions Corporation
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
package com.htmlhifive.pitalium.image.util;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.htmlhifive.pitalium.image.model.ComparedRectangleArea;
import com.htmlhifive.pitalium.image.model.ComparisonParameterDefaults;
import com.htmlhifive.pitalium.image.model.DiffCategory;
import com.htmlhifive.pitalium.image.model.Offset;
import com.htmlhifive.pitalium.image.model.SimilarityUnit;

/**
 * Utility class to calculate similarity.
 */
public class SimilarityUtils {

	/**
	 * Extract the feature matrix of size FeatureRow by FeatureCol from image.
	 *
	 * @param FeatureRow the row size of feature matrix
	 * @param FeatureCol the column size of feature matrix
	 */
	private static final int FeatureRow = 5;
	private static final int FeatureCol = 5;

	/**
	 * Constructor
	 */
	private SimilarityUtils() {
	}

	/**
	 * calculate similarity of given rectangle area and offset And then, build similarRectangle using similarity values.
	 *
	 * @param expectedImage
	 * @param actualImage
	 * @param rectangle
	 * @param similarRectangle
	 * @param offset
	 * @return TODO
	 * @return similarity using norm calculation pixel by pixel
	 */
	public static SimilarityUnit calcSimilarity(BufferedImage expectedImage, BufferedImage actualImage,
			Rectangle rectangle, ComparedRectangleArea similarRectangle, Offset offset) {
		return calcSimilarity(expectedImage, actualImage, rectangle, similarRectangle, offset, -1);
	}

	/**
	 * calculate similarity of given rectangle area and offset, but similarityFeatureMatrix is given. And then, build
	 * similarRectangle using similarity values.
	 *
	 * @param expectedImage
	 * @param actualImage
	 * @param rectangle
	 * @param similarRectangle
	 * @param offset
	 * @param similarityFeatureMatrix in the case of "SCALING", we use similarity already calculated. in the other
	 *            cases, it has -1.
	 */
	public static SimilarityUnit calcSimilarity(BufferedImage expectedImage, BufferedImage actualImage,
			Rectangle rectangle, ComparedRectangleArea similarRectangle, Offset offset, double similarityFeatureMatrix) {

		Offset featureOffset = offset;
		SimilarityUnit similarityUnit = calcSimilarityPixelByPixel(expectedImage, actualImage, rectangle, offset);

		/* calculate similarity using feature matrix. */
		int comparedRectangleWidth = (int) rectangle.getWidth(), comparedRectangleHeight = (int) rectangle.getHeight();

		// execute this only when comparedRectangleWidth >= FeatureCol && comparedRectangleHeight >= FeatureRow
		if (similarityFeatureMatrix == -1
				&& SimilarityUtils.checkFeatureSize(comparedRectangleWidth, comparedRectangleHeight)) {
			similarityFeatureMatrix = calcSimilarityByFeatureMatrix(expectedImage, actualImage, rectangle,
					featureOffset);
		}
		similarityUnit.setSimilarityFeatureMatrix(similarityFeatureMatrix);

		if (similarRectangle.getCategory() == null) {
			similarRectangle.setCategory(DiffCategory.SIMILAR);
		}
		return similarityUnit;
	}

	/**
	 * Calculate the distance of two feature matrices.
	 *
	 * @param expectedFeature The feature matrix of expected sub image
	 * @param actualFeature The feature matrix of actual sub image
	 * @return the norm of distance between two matrices
	 */
	public static double calcFeatureDistance(Color[][] expectedFeature, Color[][] actualFeature) {
		// the difference of Red, Green, and Blue.
		int r, g, b;
		double dist = 0;
		for (int row = 0; row < FeatureRow; row++) {
			for (int col = 0; col < FeatureCol; col++) {
				r = expectedFeature[row][col].getRed() - actualFeature[row][col].getRed();
				g = expectedFeature[row][col].getGreen() - actualFeature[row][col].getGreen();
				b = expectedFeature[row][col].getBlue() - actualFeature[row][col].getBlue();
				dist += r * r + g * g + b * b;
			}
		}

		// normalize and return.
		return Math.sqrt(dist) / (Math.sqrt(3 * FeatureRow * FeatureCol) * 255);
	}

	/**
	 * Check if the size of rectangle is large enough to use feature method.
	 *
	 * @param width the width of compared rectangle
	 * @param heigth the height of compared rectangle
	 * @return if the rectangle is large enough, return true.
	 */
	public static boolean checkFeatureSize(int width, int height) {
		return width >= FeatureCol && height >= FeatureRow;
	}

	/**
	 * Calculate the similarity using feature matrix and find the best match where it has the highest similarity This
	 * method should be implemented only when the size of actualSubImage is greater than or equal to FeatureCol by
	 * FeatureRow.
	 *
	 * @param expectedImage
	 * @param actualImage
	 * @param rectangle The rectangle area to be compared of actual image.
	 * @return the 'feature' similarity of given area between two images.
	 */
	public static double calcSimilarityByFeatureMatrix(BufferedImage expectedImage, BufferedImage actualImage,
			Rectangle rectangle, Offset offset) {

		// set range to be checked
		int minWidth = Math.min(expectedImage.getWidth(), actualImage.getWidth()), minHeight = Math.min(
				expectedImage.getHeight(), actualImage.getHeight());
		int actualX = (int) rectangle.getX(), actualY = (int) rectangle.getY(), actualWidth = (int) rectangle
				.getWidth(), actualHeight = (int) rectangle.getHeight();
		int maxMove;
		if (offset == null) {
			maxMove = ComparisonParameterDefaults.getMaxMove();
			offset = new Offset(0, 0);
		} else {
			maxMove = 0;
		}
		int leftMove = Math.min(maxMove, actualX - 1), rightMove = Math
				.min(maxMove, minWidth - (actualX + actualWidth)), topMove = Math.min(maxMove, actualY - 1), downMove = Math
				.min(maxMove, minHeight - (actualY + actualHeight));
		int expectedX = actualX - leftMove - offset.getX(), expectedY = actualY - topMove - offset.getY(), expectedWidth = actualWidth
				+ leftMove + rightMove, expectedHeight = actualHeight + topMove + downMove;

		// initialize sub-image.
		Rectangle entireFrame = new Rectangle(expectedX, expectedY, expectedWidth, expectedHeight);
		BufferedImage expectedSubImage = ImageUtils.getSubImage(expectedImage, entireFrame);
		BufferedImage actualSubImage = ImageUtils.getSubImage(actualImage, rectangle);

		// initialize the color array.
		int[] expectedColors = new int[expectedWidth * expectedHeight];
		int[] actualColors = new int[actualWidth * actualHeight];

		expectedSubImage.getRGB(0, 0, expectedWidth, expectedHeight, expectedColors, 0, expectedWidth);
		actualSubImage.getRGB(0, 0, actualWidth, actualHeight, actualColors, 0, actualWidth);

		int[] expectedRed = new int[expectedColors.length];
		int[] expectedGreen = new int[expectedColors.length];
		int[] expectedBlue = new int[expectedColors.length];
		int[] actualRed = new int[actualColors.length];
		int[] actualGreen = new int[actualColors.length];
		int[] actualBlue = new int[actualColors.length];

		for (int i = 0; i < expectedColors.length; i++) {
			Color expectedColor = new Color(expectedColors[i]);
			expectedRed[i] = expectedColor.getRed();
			expectedGreen[i] = expectedColor.getGreen();
			expectedBlue[i] = expectedColor.getBlue();
		}

		for (int i = 0; i < actualColors.length; i++) {
			Color actualColor = new Color(actualColors[i]);
			actualRed[i] = actualColor.getRed();
			actualGreen[i] = actualColor.getGreen();
			actualBlue[i] = actualColor.getBlue();
		}

		/* Calculate the feature matrix of actual sub-image. */

		// the size of grid.
		int GridWidth = actualWidth / FeatureCol, GridHeight = actualHeight / FeatureRow;
		int GridArea = GridWidth * GridHeight;

		Color[][] actualFeature = new Color[FeatureRow][FeatureCol];
		for (int row = 0; row < FeatureRow; row++) {
			for (int col = 0; col < FeatureCol; col++) {

				// Sum of Red, Green, and Blue.
				int rSum = 0, gSum = 0, bSum = 0;

				// Calculate the feature value actualFeature[row][col].
				for (int i = 0; i < GridHeight; i++) {
					for (int j = 0; j < GridWidth; j++) {
						rSum += actualRed[actualWidth * (GridHeight * row + i) + (GridWidth * col + j)];
						gSum += actualGreen[actualWidth * (GridHeight * row + i) + (GridWidth * col + j)];
						bSum += actualBlue[actualWidth * (GridHeight * row + i) + (GridWidth * col + j)];
					}
				}

				actualFeature[row][col] = new Color(rSum / GridArea, gSum / GridArea, bSum / GridArea);
			}
		}

		/* Calculate the feature matrix of expected subimage. */

		Color[][] expectedFeature = new Color[FeatureRow][FeatureCol];

		int bestX = 0, bestY = 0;
		double dist = 0, min = -1;

		// Find the best match moving sub-image.
		for (int y = 0; y <= topMove + downMove; y++) {
			for (int x = 0; x <= leftMove + rightMove; x++) {

				// Calculate the distance between the expected feature matrix and the actual feature matrix shifhted (x, y).
				for (int row = 0; row < FeatureRow; row++) {
					for (int col = 0; col < FeatureCol; col++) {

						// Sum of Red, Green, and Blue.
						int rSum = 0, gSum = 0, bSum = 0;

						// Calculate the feature value expectedFeature[row][col].
						for (int i = 0; i < GridHeight; i++) {
							for (int j = 0; j < GridWidth; j++) {
								rSum += expectedRed[expectedWidth * (GridHeight * row + (y + i))
										+ (GridWidth * col + (x + j))];
								gSum += expectedGreen[expectedWidth * (GridHeight * row + (y + i))
										+ (GridWidth * col + (x + j))];
								bSum += expectedBlue[expectedWidth * (GridHeight * row + (y + i))
										+ (GridWidth * col + (x + j))];
							}
						}
						expectedFeature[row][col] = new Color(rSum / GridArea, gSum / GridArea, bSum / GridArea);
					}
				}

				// Calculate the feature distance at each shift (x, y).
				dist = calcFeatureDistance(expectedFeature, actualFeature);

				// Find the best match.
				if (dist < min || min == -1) {
					min = dist;
					// offset (from expected to actual) of best match
					bestX = leftMove - x;
					bestY = topMove - y;
				}
			}
		}

		if (maxMove != 0) {
			offset.setX(bestX);
			offset.setY(bestY);
		}
		double similarity = 1 - min;

		// round similarity to 2 decimal places.
		similarity = (double) Math.round(similarity * 100) / 100;

		return similarity;
	}

	/**
	 * Calculate the similarity using feature matrix using fixed frames of expected and actual images. This method
	 * should be implemented only when the size of actualSubImage is greater than or equal to FeatureCol by FeatureRow.
	 *
	 * @param expectedImage
	 * @param actualImage
	 * @param expectedFrame the rectangle area to be extracted feature vector in expectedImage
	 * @param actualFrame the rectangle area to be extracted feature vector in actualImage
	 * @return the 'feature' similarity of given area between two images.
	 */
	public static double calcSimilarityByFeatureMatrix(BufferedImage expectedImage, BufferedImage actualImage,
			Rectangle expectedFrame, Rectangle actualFrame) {

		int expectedWidth = (int) expectedFrame.getWidth(), expectedHeight = (int) expectedFrame.getHeight(), actualWidth = (int) actualFrame
				.getWidth(), actualHeight = (int) actualFrame.getHeight();
		BufferedImage expectedSubImage = ImageUtils.getSubImage(expectedImage, expectedFrame);
		BufferedImage actualSubImage = ImageUtils.getSubImage(actualImage, actualFrame);

		// initialize the color array.
		int[] expectedColors = new int[expectedWidth * expectedHeight];
		int[] actualColors = new int[actualWidth * actualHeight];

		expectedSubImage.getRGB(0, 0, expectedWidth, expectedHeight, expectedColors, 0, expectedWidth);
		actualSubImage.getRGB(0, 0, actualWidth, actualHeight, actualColors, 0, actualWidth);

		int[] expectedRed = new int[expectedColors.length];
		int[] expectedGreen = new int[expectedColors.length];
		int[] expectedBlue = new int[expectedColors.length];
		int[] actualRed = new int[actualColors.length];
		int[] actualGreen = new int[actualColors.length];
		int[] actualBlue = new int[actualColors.length];

		for (int i = 0; i < expectedColors.length; i++) {
			Color expectedColor = new Color(expectedColors[i]);
			expectedRed[i] = expectedColor.getRed();
			expectedGreen[i] = expectedColor.getGreen();
			expectedBlue[i] = expectedColor.getBlue();
		}

		for (int i = 0; i < actualColors.length; i++) {
			Color actualColor = new Color(actualColors[i]);
			actualRed[i] = actualColor.getRed();
			actualGreen[i] = actualColor.getGreen();
			actualBlue[i] = actualColor.getBlue();
		}

		/* Calculate the feature matrix. */

		// initialize the size of grid.
		int expectedGridWidth = expectedWidth / FeatureCol, expectedGridHeight = expectedHeight / FeatureRow, expectedGridArea = expectedGridWidth
				* expectedGridHeight, actualGridWidth = actualWidth / FeatureCol, actualGridHeight = actualHeight
				/ FeatureRow, actualGridArea = actualGridWidth * actualGridHeight;
		int rSum, gSum, bSum; // Sum of Red, Green, and Blue.

		Color[][] expectedFeature = new Color[FeatureRow][FeatureCol];
		Color[][] actualFeature = new Color[FeatureRow][FeatureCol];
		for (int row = 0; row < FeatureRow; row++) {
			for (int col = 0; col < FeatureCol; col++) {

				// Calculate the feature value expectedFeature[row][col].
				rSum = 0;
				gSum = 0;
				bSum = 0;
				for (int i = 0; i < expectedGridHeight; i++) {
					for (int j = 0; j < expectedGridWidth; j++) {
						rSum += expectedRed[expectedWidth * (expectedGridHeight * row + i)
								+ (expectedGridWidth * col + j)];
						gSum += expectedGreen[expectedWidth * (expectedGridHeight * row + i)
								+ (expectedGridWidth * col + j)];
						bSum += expectedBlue[expectedWidth * (expectedGridHeight * row + i)
								+ (expectedGridWidth * col + j)];
					}
				}
				expectedFeature[row][col] = new Color(rSum / expectedGridArea, gSum / expectedGridArea, bSum
						/ expectedGridArea);

				// Calculate the feature value actualFeature[row][col].
				rSum = 0;
				gSum = 0;
				bSum = 0;
				for (int i = 0; i < actualGridHeight; i++) {
					for (int j = 0; j < actualGridWidth; j++) {
						rSum += actualRed[actualWidth * (actualGridHeight * row + i) + (actualGridWidth * col + j)];
						gSum += actualGreen[actualWidth * (actualGridHeight * row + i) + (actualGridWidth * col + j)];
						bSum += actualBlue[actualWidth * (actualGridHeight * row + i) + (actualGridWidth * col + j)];
					}
				}

				actualFeature[row][col] = new Color(rSum / actualGridArea, gSum / actualGridArea, bSum / actualGridArea);
			}
		}

		double similarity = 1 - calcFeatureDistance(expectedFeature, actualFeature);
		;

		// round similarity to 2 decimal places.
		similarity = (double) Math.round(similarity * 100) / 100;

		return similarity;
	}

	/**
	 * Calculate the similarity by comparing two images pixel by pixel, and find the best match where it has the highest
	 * similarity (when given offset is null). In this method, we count the number of different pixels as well.
	 *
	 * @param expectedSubImage the sub-image of given rectangle area of expected image
	 * @param actualSubImage the sub-image of given 'template' rectangle area of actual image. it is smaller than
	 *            expectedSubImage.
	 * @param rectangle The rectangle area where to compare.
	 * @param similarityUnit
	 * @param offset best match offset. If default offset is given, don't find the best match.
	 * @return
	 */
	public static SimilarityUnit calcSimilarityPixelByPixel(BufferedImage expectedImage, BufferedImage actualImage,
			Rectangle rectangle, Offset offset) {

		// set range to be checked
		int minWidth = Math.min(expectedImage.getWidth(), actualImage.getWidth()), minHeight = Math.min(
				expectedImage.getHeight(), actualImage.getHeight());
		int actualX = (int) rectangle.getX(), actualY = (int) rectangle.getY(), actualWidth = (int) rectangle
				.getWidth(), actualHeight = (int) rectangle.getHeight();
		int maxMove;
		if (offset == null) {
			maxMove = ComparisonParameterDefaults.getMaxMove();
			offset = new Offset(0, 0);
		} else {
			maxMove = 0;
		}
		int leftMove = Math.min(maxMove, actualX - 1), rightMove = Math
				.min(maxMove, minWidth - (actualX + actualWidth)), topMove = Math.min(maxMove, actualY - 1), downMove = Math
				.min(maxMove, minHeight - (actualY + actualHeight));
		int expectedX = actualX - leftMove - offset.getX(), expectedY = actualY - topMove - offset.getY(), expectedWidth = actualWidth
				+ leftMove + rightMove, expectedHeight = actualHeight + topMove + downMove;

		// initialize sub-image.
		Rectangle entireFrame = new Rectangle(expectedX, expectedY, expectedWidth, expectedHeight);
		BufferedImage expectedSubImage = ImageUtils.getSubImage(expectedImage, entireFrame);
		BufferedImage actualSubImage = ImageUtils.getSubImage(actualImage, rectangle);

		// initialize the color array.
		int[] expectedColors = new int[expectedWidth * expectedHeight];
		int[] actualColors = new int[actualWidth * actualHeight];

		expectedSubImage.getRGB(0, 0, expectedWidth, expectedHeight, expectedColors, 0, expectedWidth);
		actualSubImage.getRGB(0, 0, actualWidth, actualHeight, actualColors, 0, actualWidth);

		int[] expectedRed = new int[expectedColors.length];
		int[] expectedGreen = new int[expectedColors.length];
		int[] expectedBlue = new int[expectedColors.length];
		int[] actualRed = new int[actualColors.length];
		int[] actualGreen = new int[actualColors.length];
		int[] actualBlue = new int[actualColors.length];

		for (int i = 0; i < expectedColors.length; i++) {
			Color expectedColor = new Color(expectedColors[i]);
			expectedRed[i] = expectedColor.getRed();
			expectedGreen[i] = expectedColor.getGreen();
			expectedBlue[i] = expectedColor.getBlue();
		}

		for (int i = 0; i < actualColors.length; i++) {
			Color actualColor = new Color(actualColors[i]);
			actualRed[i] = actualColor.getRed();
			actualGreen[i] = actualColor.getGreen();
			actualBlue[i] = actualColor.getBlue();
		}

		// the difference of Red, Green, and Blue, respectively.
		int r, g, b, bestX = 0, bestY = 0;

		// to count the number of different pixels.
		int thresDiffCount, thresDiffMin = -1; // difference from diffThreshold
		int totalDiffCount, totalDiffMin = -1; // difference from 0
		double similarityThresDiff, similarityTotalDiff;
		double norm = 0, min = -1;
		double diffThreshold = ComparisonParameterDefaults.getDiffThreshold();

		// Find the best match moving sub-image.
		for (int y = 0; y <= topMove + downMove; y++) {
			for (int x = 0; x <= leftMove + rightMove; x++) {

				// Calculate the similarity on the (x, y)-shifted sub-image of expectedImage.
				thresDiffCount = 0;
				totalDiffCount = 0;
				norm = 0;
				for (int i = 0; i < actualHeight; i++) {
					for (int j = 0; j < actualWidth; j++) {
						r = expectedRed[expectedWidth * (i + y) + (j + x)] - actualRed[actualWidth * i + j];
						g = expectedGreen[expectedWidth * (i + y) + (j + x)] - actualGreen[actualWidth * i + j];
						b = expectedBlue[expectedWidth * (i + y) + (j + x)] - actualBlue[actualWidth * i + j];
						norm += Math.sqrt(r * r + g * g + b * b);
						if (r * r + g * g + b * b > 3 * 255 * 255 * diffThreshold * diffThreshold)
							thresDiffCount++;
						if (r * r + g * g + b * b > 0)
							totalDiffCount++;
					}
				}

				// Find the minimal difference.
				if (norm < min || min == -1) {
					min = norm;
					// offset (from expected to actual) of best match
					bestX = leftMove - x;
					bestY = topMove - y;
				}

				// Find the minimal number of total different pixels.
				if (totalDiffCount < totalDiffMin || totalDiffMin == -1) {
					totalDiffMin = totalDiffCount;
				}

				// Find the minimal number of threshold different pixels.
				if (thresDiffCount < thresDiffMin || thresDiffMin == -1) {
					thresDiffMin = thresDiffCount;
				}
			}
		}
		double similarity;

		// normalize and calculate average.
		similarity = 1 - min / (Math.sqrt(3) * 255 * actualWidth * actualHeight);

		// normalize the number of different pixels.
		similarityThresDiff = 1 - (double) thresDiffMin / (actualWidth * actualHeight);
		similarityTotalDiff = 1 - (double) totalDiffMin / (actualWidth * actualHeight);

		// round similarities to 2 decimal place.
		similarity = (double) Math.round(similarity * 100) / 100;
		similarityThresDiff = (double) Math.round(similarityThresDiff * 100) / 100;
		similarityTotalDiff = (double) Math.round(similarityTotalDiff * 100) / 100;

		if (maxMove != 0) {
			offset.setX(bestX);
			offset.setY(bestY);
		}
		return new SimilarityUnit(offset.getX(), offset.getY(), similarity, 0, similarityThresDiff, similarityTotalDiff);
	}
}
