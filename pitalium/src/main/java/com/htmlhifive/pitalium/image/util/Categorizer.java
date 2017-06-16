/*
 * Copyright (C) 2015-2017 NS Solutions Corporation
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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.List;

import com.htmlhifive.pitalium.image.model.ComparedRectangleArea;
import com.htmlhifive.pitalium.image.model.ComparisonParameterDefaults;

/**
 * For given two images which are expected and actual, methods in this class check each category of difference.
 */
public class Categorizer {

	/**
	 * Constructor
	 */
	public Categorizer() {
	};

	public static boolean CheckSubpixel(BufferedImage expectedImage, BufferedImage actualImage, Rectangle rectangle) {
		int x = rectangle.x;
		int y = rectangle.y;
		int w = rectangle.width;
		int h = rectangle.height;
		BufferedImage subExpectedImage = expectedImage.getSubimage(x, y, w, h);
		BufferedImage subActualImage = actualImage.getSubimage(x, y, w, h);
		double[] expectedIndicator = ImageUtils.countSubpixel(subExpectedImage);
		double[] actualIndicator = ImageUtils.countSubpixel(subActualImage);
		if ((expectedIndicator[0] > ComparisonParameterDefaults.getSubpixelRateThreshold()
				&& expectedIndicator[1] > ComparisonParameterDefaults.getSubpixelPerLineThreshold())
				|| (actualIndicator[0] > ComparisonParameterDefaults.getSubpixelRateThreshold()
						&& actualIndicator[1] > ComparisonParameterDefaults.getSubpixelPerLineThreshold())) {
			return true;
		}
		return false;
	}

	/**
	 * Check the sub-image of actualImage in the given rectangle area is contained in expectedImage at the same or
	 * nearby location if then, create ComparedRectangle with shift information and insert it into ComparedRectangles
	 * list.
	 * 
	 * @param expectedImage
	 * @param actualImage
	 * @param ComparedRectangles list of ComparedRectangle
	 * @param rectangle sub-image area of actual image
	 * @return true if this rectangle is shifted
	 */
	public static boolean CheckShift(BufferedImage expectedImage, BufferedImage actualImage,
			List<ComparedRectangleArea> ComparedRectangles, Rectangle rectangle) {
		int minWidth = Math.min(expectedImage.getWidth(), actualImage.getWidth()),
				minHeight = Math.min(expectedImage.getHeight(), actualImage.getHeight());

		// set range to be checked
		int x = (int) rectangle.getX(), y = (int) rectangle.getY(), w = (int) rectangle.getWidth(),
				h = (int) rectangle.getHeight();
		int maxShift = ComparisonParameterDefaults.getMaxShift();
		int leftMove = Math.min(maxShift, x - 1), rightMove = Math.min(maxShift, minWidth - (x + w)),
				topMove = Math.min(maxShift, y - 1), downMove = Math.min(maxShift, minHeight - (y + h));
		Rectangle entireFrame = new Rectangle(x - leftMove, y - topMove, w + leftMove + rightMove,
				h + topMove + downMove);
		BufferedImage entireImage = ImageUtils.getSubImage(expectedImage, entireFrame);
		BufferedImage templateImage = ImageUtils.getSubImage(actualImage, rectangle);

		double[][] integralImage = ImageUtils.calcIntegralImage(entireImage);

		double sumTemplate = 0;
		Raster r = templateImage.getRaster();

		int[] dArray = new int[r.getNumDataElements()];
		for (int i = 0; i < r.getWidth(); i++) {
			for (int j = 0; j < r.getHeight(); j++) {
				sumTemplate += r.getPixel(i, j, dArray)[0];
			}
		}

		int templateWidth = templateImage.getWidth();
		int templateHeight = templateImage.getHeight();
		double topLeft, topRight, bottomLeft, bottomRight;
		double sumEntire;

		for (int i = 0; i <= topMove + downMove; i++) {
			for (int j = 0; j <= leftMove + rightMove; j++) {
				bottomRight = integralImage[i + templateHeight - 1][j + templateWidth - 1];
				bottomLeft = (j == 0) ? 0 : integralImage[i + templateHeight - 1][j - 1];
				topRight = (i == 0) ? 0 : integralImage[i - 1][j + templateWidth - 1];
				topLeft = (j == 0 || i == 0) ? 0 : integralImage[i - 1][j - 1];
				sumEntire = bottomRight - bottomLeft - topRight + topLeft;

				if (Double.compare(sumEntire, sumTemplate) == 0) {
					BufferedImage cropEntire = entireImage.getSubimage(j, i, templateWidth, templateHeight);

					// If the template matches at this position, create new ComparedRectangle and add it in the list
					if (ImageUtils.imageEquals(cropEntire, templateImage)) {
						ComparedRectangleArea newMatch = new ComparedRectangleArea(rectangle, leftMove - j,
								topMove - i);
						ComparedRectangles.add(newMatch);
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Check whether the given area is different due to object missing
	 * 
	 * @param expectedImage
	 * @param actualImage
	 * @param rectangle
	 * @return true if object missing occurs
	 */
	public static boolean checkMissing(BufferedImage expectedImage, BufferedImage actualImage, Rectangle rectangle) {

		// initialize sub-images
		BufferedImage expectedSubImage = ImageUtils.getSubImage(expectedImage, rectangle);
		BufferedImage actualSubImage = ImageUtils.getSubImage(actualImage, rectangle);
		int width = (int) rectangle.getWidth(), height = (int) rectangle.getHeight();

		// initialize the color array.
		int[] expectedColors = new int[width * height];
		int[] actualColors = new int[width * height];
		expectedSubImage.getRGB(0, 0, width, height, expectedColors, 0, width);
		actualSubImage.getRGB(0, 0, width, height, actualColors, 0, width);

		int expectedColor = expectedColors[0];
		int actualColor = actualColors[0];

		// check expected sub-image
		int i = 1, j = 1, numPixels = expectedColors.length;
		while (i < numPixels && expectedColor == expectedColors[i]) {
			i = i + 1;
		}

		// check actual sub-image
		while (j < numPixels && actualColor == actualColors[j]) {
			j = j + 1;
		}

		// the case that one has the same colors for every pixel, and the other doesn't
		if ((i == numPixels && j < numPixels) || (j == numPixels && i < numPixels)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * check scaling using object detection
	 * 
	 * @param expectedImage
	 * @param actualImage
	 * @param rectangle
	 * @return true if two objects are same (or similar enough) and have different size
	 */
	public static boolean checkScaling(BufferedImage expectedImage, BufferedImage actualImage, Rectangle expectedObject,
			Rectangle actualObject) {

		// check scale factor
		double maximumScaleFactor = ComparisonParameterDefaults.getMaximumScaleFactor();
		double expectedWidth = expectedObject.getWidth(), expectedHeight = expectedObject.getHeight(),
				actualWidth = actualObject.getWidth(), actualHeight = actualObject.getHeight();
		if (expectedWidth > actualWidth * maximumScaleFactor || actualWidth > expectedWidth * maximumScaleFactor
				|| expectedHeight > actualHeight * maximumScaleFactor
				|| actualHeight > expectedHeight * maximumScaleFactor)
			return false;

		// check feature matrix similarity
		double similarity = SimilarityUtils.calcSimilarityByFeatureMatrix(expectedImage, actualImage, expectedObject,
				actualObject);

		if (similarity > ComparisonParameterDefaults.getScalingFeatureCriterion())
			return true;
		else
			return false;
	}

}
