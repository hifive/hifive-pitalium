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
