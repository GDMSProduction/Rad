package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by zfile on 2018-02-27.
 */

public class MiningTool extends Clutter {
    private int digPower = 0;

    MiningTool(int newDigPower, int newValue, Point mPoint, Bitmap newImage, int HPMax) {
        super(newValue, mPoint, newImage, HPMax);
        digPower = newDigPower;
    }
    public int getDigPower(){return digPower;}
}
