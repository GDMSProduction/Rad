package com.example.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by Stephen on 2018-01-18.
 */

public class BaseObject {
    private Point mPoint = new Point(0, 0);
    private Bitmap image;
    private Rect detectCollision;

    BaseObject(Point newPoint, Bitmap newBitmap) {
        mPoint = newPoint;
        image = newBitmap;
        //initializing rect object
        detectCollision =  new Rect(mPoint.x, mPoint.y, image.getWidth(), image.getHeight());
    }

    //Getters
    public Point GetPoint() {
        return mPoint;
    }

    public int GetX() {
        return mPoint.x;
    }

    public int GetY() {
        return mPoint.y;
    }

    public Bitmap GetBitmap() {
        return image;
    }

    public int GetWidth() {
        return image.getWidth();
    }

    public int GetHeight() {
        return image.getHeight();
    }

    public Rect GetCollideRect() {
        return detectCollision;
    }

    //Setters
    public void SetBitMap(Bitmap newBitMap) {
        image = newBitMap;
    }

    public void SetPoint(Point newPoint) {
        mPoint = newPoint;
    }

    public void SetX(int newX) {
        mPoint.x = newX;
    }

    public void SetY(int newY) {
        mPoint.y = newY;
    }

    public void SetDetectCollision(Rect newRect) {
        detectCollision = newRect;
    }
}
