package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by zfile on 2018-02-28.
 */

public class Wearable extends Clutter {
    public enum EnchantType{Defense, Attack, Health, FeatherFall}
    private EnchantType type = EnchantType.Health;
    private int power = 0;

    Wearable(EnchantType enchantType, int newValue, int Power, int enchantPower, Point3d mPoint, Bitmap newImage, int HPMax) {
        super(newValue, enchantPower, mPoint, newImage, HPMax);
        type = enchantType;
        power = Power;
        setCellType(CellType.Wearable);
    }
    public EnchantType getEnchantType() {return type;}
    public int getTotalPower(){return power + getEnchantModifier();}
}
