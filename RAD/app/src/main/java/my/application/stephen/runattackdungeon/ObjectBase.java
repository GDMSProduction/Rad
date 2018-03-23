package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by Stephen on 2018-01-18.
 */

public class ObjectBase {
    public enum AlignmentHorizontal {Left, Center, Right}
    public enum AlignmentVertical {Top, Middle, Bottom}
    private AlignmentHorizontal alignmentHorizontal = AlignmentHorizontal.Center;
    private AlignmentVertical alignmentVertical = AlignmentVertical.Middle;
    private Point3d mPoint = new Point3d(0, 0, 0);
    private Bitmap image;
    private Rect detectCollision;
    private Paint paint;

    ObjectBase(Point3d newPoint, Bitmap newBitmap) {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAlpha(255);
        mPoint = newPoint;
        image = newBitmap;
        //initializing rect object
        detectCollision =  new Rect(mPoint.x, mPoint.y, image.getWidth(), image.getHeight());
    }

    //Getters
    public Point3d getPoint() {
        return mPoint;
    }
    public Point get2dPoint(){return new Point(mPoint.x, mPoint.y);}
    public Paint getPaint() {return paint;}
    public int getPaintAlpha() {return paint.getAlpha();}
    public AlignmentVertical getAlignmentVertical() {return alignmentVertical;}
    public AlignmentHorizontal getAlignmentHorizontal() {return alignmentHorizontal;}

    public int getX() {
        return mPoint.x;
    }
    public int getY() {
        return mPoint.y;
    }
    public int getZ() {return mPoint.z;}
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
    public void setPaintAlpha(int newAlpha){paint.setAlpha(newAlpha);}
    public void setDetectCollision(Rect newRect) {
        detectCollision = newRect;
    }

    //Helper Functions
    public void checkImage(Bitmap image) {
        if (getBitmap() != image) {
            setBitMap(image);
        }
    }
    public void setPoint(Point3d newPoint) {
        mPoint = newPoint;
        detectCollision.left = mPoint.x;
        detectCollision.top = mPoint.y;
    }
    public void setPoint(int x, int y, int z) {
        mPoint = new Point3d (x, y, z);
        detectCollision.left = mPoint.x;
        detectCollision.top = mPoint.y;
    }

    public void setX(int newX) {
        mPoint.x = newX;
        detectCollision.left = mPoint.x;
    }

    public void setY(int newY) {
        mPoint.y = newY;
        detectCollision.top = mPoint.y;
    }
    public void setZ(int newZ){
        mPoint.z = newZ;
    }

}
