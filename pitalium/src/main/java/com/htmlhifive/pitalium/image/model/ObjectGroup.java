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

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents ObjectGroup
 */
public class ObjectGroup {

	/**
	 * グループ化可能な距離のデフォルト値 高速化メモリ節約のため、四角形を作るため誤差が出る
	 */
	public static final int DEFAULT_GROUP_DISTANCE = 10;

	/**
	 * rectangle which represent object
	 */
	private Rectangle rectangle;

	/**
	 * Build object rectangle using center point p For grouping, it's assigned groupDistance. If the difference of
	 * x-coordinate or y-coordinate of two points is smaller than groupDistance, two points are merged into the same
	 * group.
	 *
	 * @param p center point
	 * @param groupDistance distance for grouping
	 */
	public ObjectGroup(Point p, int groupDistance) {
		int margin = groupDistance / 2;
		rectangle = new Rectangle(p.x - margin, p.y - margin, 2 * margin + 1, 2 * margin + 1);
	}

	/**
	 * Build object rectangle using center point p for grouping.
	 *
	 * @param p center point
	 */
	public ObjectGroup(Point p) {
		this(p, DEFAULT_GROUP_DISTANCE);
	}

	/**
	 * Specify the coordinates of the center point to generate an object rectangle
	 *
	 * @param x x-coordinate of the center point
	 * @param y y-coordinate of the center point
	 * @param groupDistance distance for grouping
	 */
	public ObjectGroup(int x, int y, int groupDistance) {
		this(new Point(x, y), groupDistance);
	}

	/**
	 * Specify the coordinates of the center point to generate an object rectangle
	 *
	 * @param x x-coordinate of the center point
	 * @param y y-coordinate of the center point
	 */
	public ObjectGroup(int x, int y) {
		this(new Point(x, y));
	}

	/**
	 * To join with the specified objectGroup, you need to make sure at whether it is possible to join in advance
	 *
	 * @param objectGroup ObjectGroup that bind
	 */
	public void union(ObjectGroup objectGroup) {
		// To combine two of the squares, should check canMerge in advance.
		rectangle = rectangle.union(objectGroup.getRectangle());
	}

	/**
	 * Check whether one of the binding conditions is met. The first condition is that one contains the other, and the
	 * second is intersection.
	 *
	 * @param objectGroup target object
	 * @return whether one of the condition is met
	 */
	public boolean canMerge(ObjectGroup objectGroup) {

		// If one contains the other
		if (objectGroup.getRectangle().contains(this.getRectangle())
				|| this.getRectangle().contains(objectGroup.getRectangle())) {
			return true;
		}

		// If two object intersect
		if (objectGroup.getRectangle().intersects(this.getRectangle())) {
			return true;
		}

		// otherwise, can't merge
		return false;
	}

	/**
	 * merge all possible object groups
	 *
	 * @param objectGroups list of object groups
	 * @return list of object groups which are completely merged
	 */
	public static List<ObjectGroup> mergeAllPossibleObjects(List<ObjectGroup> objectGroups) {

		// Count how many times merge occur for each case
		int num = -1;

		// loop until there is no merge
		while (num != 0) {
			num = 0;
			for (ObjectGroup object1 : objectGroups) {
				List<ObjectGroup> removeList = new ArrayList<ObjectGroup>();
				for (ObjectGroup object2 : objectGroups) {

					// Check if two distinct rectangles can be merged.
					if (!object1.equals(object2) && object1.canMerge(object2)) {
						object1.union(object2);
						num++;

						// Record the rectangle which will be removed.
						removeList.add(object2);
					}
				}
				if (num > 0) {
					// Remove the merged rectangle.
					for (ObjectGroup removeModel : removeList) {
						objectGroups.remove(removeModel);
					}
					break;
				}
			}
		}

		return objectGroups;
	}

	/**
	 * Get the rectangle area of object
	 *
	 * @return rectangle area of object
	 */
	public Rectangle getRectangle() {
		return rectangle;
	}

}
