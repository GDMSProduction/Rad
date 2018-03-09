package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by zfile on 2018-02-27.
 */

public class LightSource extends Weapon {
    private int lightRadius = 0;
    LightSource(int newLightRadius, int atkPower, int newValue, Point mPoint, Bitmap newImage, int HPMax) {
        super(atkPower, newValue, mPoint, newImage, HPMax);
        lightRadius = newLightRadius;
        setCellType(CellType.LightSource);
    }
    public int getLightRadius(){return lightRadius;}
}
