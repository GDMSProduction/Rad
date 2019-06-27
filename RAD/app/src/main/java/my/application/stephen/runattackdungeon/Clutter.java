package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

/**
 * Created by Stephen on 2018-01-18.
 * Clutter are objects on the "floor" of the level.
 */

public class Clutter extends ObjectDestructible {
    public enum magicType{Fire, Cold, Air, Earth, Water, Electric, Create, Destroy}
    private int value = 0;
    private int enchantModifier = 0;
    private Creature owner = null;

    Clutter(int newValue, Point3d mPoint, Bitmap newImage, int HPMax) {
        super(mPoint, newImage, HPMax);
        value = newValue;
        setCellType(CellType.Clutter);
    }
    Clutter(int newValue, int enchantPower, Point3d mPoint, Bitmap newImage, int HPMax) {
        super(mPoint, newImage, HPMax);
        value = newValue;
        enchantModifier = enchantPower;
        setCellType(CellType.Clutter);
    }
    Clutter(int newValue, int enchantPower, Point3d mPoint, Bitmap newImage, int HPMax, CellType Type) {
        super(mPoint, newImage, HPMax);
        value = newValue;
        enchantModifier = enchantPower;
        setCellType(Type);
    }
    //setters
    public void setOwner(@Nullable Creature newOwner){owner = newOwner;}
    //getters
    int getValue(){return value;}
    int getEnchantModifier(){return enchantModifier;}

}
