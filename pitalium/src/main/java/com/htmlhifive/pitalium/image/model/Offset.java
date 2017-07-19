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
 * offset class which contains x and y-coordinates
 */
public class Offset {

	private int x;
	private int y;

	/**
	 * Constructor
	 *
	 * @param x offsetX
	 * @param y offsetY
	 */
	public Offset(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return offsetX
	 */
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return offsetY
	 */
	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
