package com.example.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by Stephen on 2018-01-18.
 */

public class BaseObject {
    private Point mPoint;
    private Bitmap image;

    //Getters
    public Point GetPoint(){return mPoint;}
    public int GetX(){return mPoint.x;}
    public int GetY(){return mPoint.y;}
    public int GetWidth(){return image.getWidth();}
    public int GetHeight(){return image.getHeight();}
}
