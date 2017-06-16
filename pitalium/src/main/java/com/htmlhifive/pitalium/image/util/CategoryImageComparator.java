package com.htmlhifive.pitalium.image.util;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import com.htmlhifive.pitalium.image.model.CategoryComparisonParameters;
import com.htmlhifive.pitalium.image.model.ComparedRectangleArea;
import com.htmlhifive.pitalium.image.model.DiffCategory;
import com.htmlhifive.pitalium.image.model.ImageComparedResult;

class CategoryImageComparator extends ImageComparator<CategoryComparisonParameters> {

	/**
	 * デフォルトコンストラクタ
	 */
	CategoryImageComparator() {
	}

	/**
	 * 引数ありのコンストラクタ
	 *
	 * @param parameters 比較用パラメータ
	 */
	CategoryImageComparator(CategoryComparisonParameters parameters) {
		this.parameters = parameters;
	}

	@Override
	protected List<Point> compare(BufferedImage image1, BufferedImage image2, int offsetX, int offsetY) {
		return null;
	}

	@Override
	public ImageComparedResult compare(BufferedImage img1, Rectangle img1Area, BufferedImage img2, Rectangle img2Area) {
		ImagePair pair = new ImagePair(cropSubImage(img1, img1Area), cropSubImage(img2, img2Area));
		pair.prepare();
		pair.doCategorize();

		List<ComparedRectangleArea> comparedRectangles = pair.getComparedRectangles();

		boolean isSucceed = true;
		for (ComparedRectangleArea rect : comparedRectangles) {
			DiffCategory type = rect.getCategory();
			if (!parameters.isAccept(type)) {
				isSucceed = false;
				break;
			}
		}

		return new CategoryImageComparedResult(isSucceed, comparedRectangles);
	}

	private BufferedImage cropSubImage(BufferedImage img, Rectangle area) {
		return img.getSubimage(area.x, area.y, area.width, area.height);
	}
}
