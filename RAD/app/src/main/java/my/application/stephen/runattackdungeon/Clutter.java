package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by Stephen on 2018-01-18.
 */

public class Clutter extends ObjectDestructible {
    private int value = 0;
    private int enchantModifier = 0;

    Clutter(int newValue, Point mPoint, Bitmap newImage, int HPMax) {
        super(mPoint, newImage, HPMax);
        value = newValue;
    }
    Clutter(int newValue, int enchantPower, Point mPoint, Bitmap newImage, int HPMax) {
        super(mPoint, newImage, HPMax);
        value = newValue;
        enchantModifier = enchantPower;
    }
    //setters
    public void setValue(int newValue){ value = newValue; }
    //getters
    public int getValue(){return value;}
    public int getEnchantModifier(){return enchantModifier;}

}
