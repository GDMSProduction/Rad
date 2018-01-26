package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by Stephen on 2018-01-18.
 */

public class Clutter extends BaseObject {
    private int value = 0;

    Clutter(int newValue, Point mPoint, Bitmap newImage) {
        super(mPoint, newImage);
        value = newValue;
    }
    //setters
    public void SetValue(int newValue){ value = newValue; }
    //getters
    public int GetValue(){return value;}

}
