package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by Stephen on 2018-01-18.
 */

public class ObjectBase {
    private Point mPoint = new Point(0, 0);
    private int centerOffset = 0;
    private Bitmap image;
    private Rect detectCollision;

    ObjectBase(Point newPoint, Bitmap newBitmap) {
        mPoint = newPoint;
        image = newBitmap;
        //initializing rect object
        detectCollision =  new Rect(mPoint.x, mPoint.y, image.getWidth(), image.getHeight());
    }

    //Getters
    public Point getPoint() {
        return mPoint;
    }

    public int getX() {
        return mPoint.x;
    }

    public int getY() {
        return mPoint.y;
    }

    public int getCenterOffset() {return centerOffset;}

    public Bitmap getBitmap() {
        return image;
    }

    public Rect getCollideRect() {
        return detectCollision;
    }

    //Setters
    public void setBitMap(Bitmap newBitMap) {
        image = newBitMap;
    }

    public void setCenterOffset(int newCenterOffset) {centerOffset = newCenterOffset;}
    public void setPoint(Point newPoint) {
        mPoint = newPoint;
    }
    public void setPoint(int x, int y) {
        Point temp = new Point (x, y);
        mPoint = temp;
    }

    public void setX(int newX) {
        mPoint.x = newX;
    }

    public void setY(int newY) {
        mPoint.y = newY;
    }

    public void setDetectCollision(Rect newRect) {
        detectCollision = newRect;
    }

    //Helper Functions

}
