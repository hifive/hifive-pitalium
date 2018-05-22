package com.htmlhifive.pitalium.core.model;

public class ClientRect {
    private long top;
    private long left;
    private long bottom;
    private long right;
    private long x;
    private long y;
    private long height;
    private long width;

    public ClientRect(long top, long left, long bottom, long right, long x, long y, long height, long width) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    public long getTop() {
        return top;
    }

    public long getLeft() {
        return left;
    }

    public long getBottom() {
        return bottom;
    }

    public long getRight() {
        return right;
    }

    public long getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    public long getHeight() {
        return height;
    }

    public long getWidth() {
        return width;
    }
}

