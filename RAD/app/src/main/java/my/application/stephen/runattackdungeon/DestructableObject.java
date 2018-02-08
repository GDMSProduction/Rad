package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by zfile on 2018-02-02.
 */

public class DestructableObject extends BaseObject {
    private int hitPoints = 10;
    private int hitPointsMax = 10;

    DestructableObject(Point newPoint, Bitmap newBitmap, int HPMax) {
        super(newPoint, newBitmap);
        hitPoints = hitPointsMax = HPMax;
    }

    public int GetHP() {
        return hitPoints;
    }

    public int GetMaxpHP() {
        return hitPointsMax;
    }

    public void SetMaxHP(int newMax) {
        hitPointsMax = newMax;
        if (hitPoints < hitPointsMax){
            hitPoints = hitPointsMax;
        }
    }

    public void Hurt(int damage) {
        if (hitPoints - damage <= 0) {
            hitPoints = 0;
        } else {
            hitPoints -= damage;
        }
    }

    public void Heal(int healing) {
        if (healing + hitPoints >= hitPointsMax) {
            hitPoints = hitPointsMax;
        } else {
            hitPoints += healing;
        }
    }
}
