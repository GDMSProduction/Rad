package com.example.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by Stephen on 2018-01-18.
 */

public class BaseObject {
    private Point mPoint = new Point(0,0);
    private Bitmap image;

    BaseObject(Point newPoint, Bitmap newBitmap){
        mPoint = newPoint;
        image = newBitmap;
    }
    //Getters
    public Point GetPoint(){return mPoint;}
    public int GetX(){return mPoint.x;}
    public int GetY(){return mPoint.y;}
    public Bitmap GetBitmap() {return image;}
    public int GetWidth(){return image.getWidth();}
    public int GetHeight(){return image.getHeight();}
    //Setters
    public void SetBitMap(Bitmap newBitMap){image = newBitMap;}
    public void SetPoint(Point newPoint){mPoint = newPoint;}
    public void SetX(int newX){mPoint.x = newX;}
    public void SetY(int newY){mPoint.y = newY;}
}
