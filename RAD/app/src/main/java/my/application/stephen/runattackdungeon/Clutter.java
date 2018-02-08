package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by Stephen on 2018-01-18.
 */

public class Clutter extends DestructableObject {
    private int value = 0;

    Clutter(int newValue, Point mPoint, Bitmap newImage, int HPMax) {
        super(mPoint, newImage, HPMax);
        value = newValue;
    }
    //setters
    public void SetValue(int newValue){ value = newValue; }
    //getters
    public int GetValue(){return value;}

}
