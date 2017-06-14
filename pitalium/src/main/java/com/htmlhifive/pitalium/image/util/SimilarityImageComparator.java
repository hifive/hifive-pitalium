package com.htmlhifive.pitalium.image.util;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import com.htmlhifive.pitalium.image.model.ComparedRectangleArea;
import com.htmlhifive.pitalium.image.model.ImageComparedResult;
import com.htmlhifive.pitalium.image.model.Offset;
import com.htmlhifive.pitalium.image.model.SimilarityComparisonParameters;
import com.htmlhifive.pitalium.image.model.SimilarityImageComparedResult;
import com.htmlhifive.pitalium.image.model.SimilarityUnit;

public class SimilarityImageComparator extends ImageComparator<SimilarityComparisonParameters> {

	@Override
	protected List<Point> compare(BufferedImage image1, BufferedImage image2, int offsetX, int offsetY) {
		return null;
	}

	@Override
	public ImageComparedResult compare(BufferedImage img1, Rectangle img1Area, BufferedImage img2, Rectangle img2Area) {
		Offset offset = new Offset(img2Area.x - img1Area.x, img2Area.y - img1Area.y);
		SimilarityUnit unit = SimilarityUtils.calcSimilarity(img1, img2, img1Area, new ComparedRectangleArea(img2Area),
				offset);

		boolean isSucceed = true;
		if (parameters.getPixleByPixelThreshold() > unit.getSimilarityPixelByPixel()) {
			isSucceed = false;
		} else if (parameters.getFeatherMatrixThreshold() > unit.getSimilarityFeatureMatrix()) {
			isSucceed = false;
		} else if (parameters.getThresDiffThreshold() > unit.getSimilarityThresDiff()) {
			isSucceed = false;
		} else if (parameters.getTotalDiffThreshold() > unit.getSimilarityTotalDiff()) {
			isSucceed = false;
		}
		return new SimilarityImageComparedResult(isSucceed, unit);
	}
}
