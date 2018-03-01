package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by zfile on 2018-02-02.
 */

public class ObjectDestructible extends ObjectBase {
    private int hitPoints = 10;
    private int hitPointsMax = 10;

    ObjectDestructible(Point newPoint, Bitmap newBitmap, int HPMax) {
        super(newPoint, newBitmap);
        hitPoints = hitPointsMax = HPMax;
    }

    public int getHP() {
        return hitPoints;
    }

    public int getMaxpHP() {
        return hitPointsMax;
    }

    public void setMaxHP(int newMax) {
        hitPoints += newMax - hitPointsMax;
        hitPointsMax = newMax;
    }

    public void hurt(int damage) {
        if (hitPoints - damage <= 0) {
            hitPoints = 0;
        } else {
            hitPoints -= damage;
        }
    }

    public void heal(int healing) {
        if (healing + hitPoints >= hitPointsMax) {
            hitPoints = hitPointsMax;
        } else {
            hitPoints += healing;
        }
    }
}
