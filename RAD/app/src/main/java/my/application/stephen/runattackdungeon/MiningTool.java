package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by zfile on 2018-02-27.
 */

public class MiningTool extends Clutter {
    private int minePower = 0;

    MiningTool(int newDigPower, int newValue, Point mPoint, Bitmap newImage, int HPMax) {
        super(newValue, mPoint, newImage, HPMax);
        minePower = newDigPower;
    }
    public int getMinePower(){return minePower;}
}
