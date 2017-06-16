package com.htmlhifive.pitalium.image.util;

import java.util.List;

import com.htmlhifive.pitalium.image.model.ComparedRectangleArea;
import com.htmlhifive.pitalium.image.model.ImageComparedResult;

public class CategoryImageComparedResult extends ImageComparedResult {

	private boolean isSucceed;
	private List<ComparedRectangleArea> comparedRectangles;

	public CategoryImageComparedResult(boolean isSucceed, List<ComparedRectangleArea> comparedRectangles) {
		this.isSucceed = isSucceed;
		this.comparedRectangles = comparedRectangles;
	}

	public List<ComparedRectangleArea> getComparedRectangles() {
		return comparedRectangles;
	}

	@Override
	public boolean isSucceeded() {
		return isSucceed;
	}
}
