package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by zfile on 2018-02-27.
 */

public class Weapon extends Clutter {
    private int attackPower = 0;

    Weapon(int atkPower, int newValue, Point mPoint, Bitmap newImage, int HPMax) {
        super(newValue, mPoint, newImage, HPMax);
        attackPower = atkPower;
    }
    Weapon(int atkPower, int enchant, int newValue, Point mPoint, Bitmap newImage, int HPMax) {
        super(newValue, enchant, mPoint, newImage, HPMax);
        attackPower = atkPower;
    }

    public int getAttackPower(){return attackPower;}
}
