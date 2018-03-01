package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by zfile on 2018-02-28.
 */

public class Wearable extends Clutter {
    public enum EnchantType{Defense, Attack, Health}
    private EnchantType type = EnchantType.Defense;
    private int power = 0;

    Wearable(EnchantType enchantType, int newValue, int Power, int enchantPower, Point mPoint, Bitmap newImage, int HPMax) {
        super(newValue, enchantPower, mPoint, newImage, HPMax);
        type = enchantType;
        power = Power;
    }
    public EnchantType getEnchantType() {return type;}
    public int getPower(){return power;}
}
