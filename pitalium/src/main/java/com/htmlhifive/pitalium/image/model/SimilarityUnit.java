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
package com.htmlhifive.pitalium.image.model;

/**
 * Similarity unit class contains information of the similarity calculated from each method.
 */
public class SimilarityUnit {

	private int XSimilar;
	private int YSimilar;
	private double similarityPixelByPixel;
	private double similarityFeatureMatrix;
	private double similarityThresDiff;
	private double similarityTotalDiff;

	/**
	 * Default constructor
	 */
	public SimilarityUnit(int XSimilar, int YSimilar, double similarityPixelByPixel, double similarityFeatureMatrix,
			double similarityThresDiff, double similarityTotalDiff) {
		this.XSimilar = XSimilar;
		this.YSimilar = YSimilar;
		this.similarityPixelByPixel = similarityPixelByPixel;
		this.similarityFeatureMatrix = similarityFeatureMatrix;
		this.similarityThresDiff = similarityThresDiff;
		this.similarityTotalDiff = similarityTotalDiff;
	}

	public SimilarityUnit() {
		this(0, 0, 0, 0, 0, 0);
	}

	/**
	 * @return X-direction Similar at the best match with the highest similarity
	 */
	public int getXSimilar() {
		return XSimilar;
	}

	public void setXSimilar(int XSimilar) {
		this.XSimilar = XSimilar;
	}

	/**
	 * @return Y-direction Similar at the best match with the highest similarity
	 */
	public int getYSimilar() {
		return YSimilar;
	}

	public void setYSimilar(int YSimilar) {
		this.YSimilar = YSimilar;
	}

	/**
	 * @return the highest similarity calculated using pixel by pixel method at the position of given X, Y-Similar
	 */
	public double getSimilarityPixelByPixel() {
		return similarityPixelByPixel;
	}

	public void setSimilarityPixelByPixel(double similarityPixelByPixel) {
		this.similarityPixelByPixel = similarityPixelByPixel;
	}

	/**
	 * @return similarity calculated using feature matrix
	 */
	public double getSimilarityFeatureMatrix() {
		return similarityFeatureMatrix;
	}

	public void setSimilarityFeatureMatrix(double similarityFeatureMatrix) {
		this.similarityFeatureMatrix = similarityFeatureMatrix;
	}

	/**
	 * @return similarity counting difference from threshold
	 */
	public double getSimilarityThresDiff() {
		return similarityThresDiff;
	}

	public void setSimilarityThresDiff(double similarityThresDiff) {
		this.similarityThresDiff = similarityThresDiff;
	}

	/**
	 * @return similarity counting difference from zero
	 */
	public double getSimilarityTotalDiff() {
		return similarityTotalDiff;
	}

	public void setSimilarityTotalDiff(double similarityTotalDiff) {
		this.similarityTotalDiff = similarityTotalDiff;
	}
}
