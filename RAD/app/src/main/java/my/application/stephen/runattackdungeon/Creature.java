package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by Stephen on 2018-01-20.
 */

public class Creature extends BaseObject {

    private int hitPoints = 10;
    private int hitPointsMax = 10;
    private float defense = 0.0f;
    private float defenseMax = 0.8f;
    private int attack = 0;
    private int attackMax = 10;
    private int mine = 0;
    private int mineMax = 10;
    private boolean following = false;

    Creature(Point newPoint, Bitmap newBitmap) {
        super(newPoint, newBitmap);
    }

    Creature(Point newPoint, Bitmap newBitmap, int HPMax) {
        super(newPoint, newBitmap);
        hitPoints = hitPointsMax = HPMax;
    }

    Creature(Point newPoint, Bitmap newBitmap, int HPMax, float DefMax) {
        super(newPoint, newBitmap);
        hitPoints = hitPointsMax = HPMax;
        defense = defenseMax = DefMax;
    }

    //getters
    public int GetHP() {
        return hitPoints;
    }

    public int GetMaxpHP() {
        return hitPointsMax;
    }

    /*
    *Returns current defense
     */
    public float GetDef() {
        return defense;
    }

    public float GetDefenseMax() {
        return defenseMax;
    }

    public boolean isFollowing(){return following;}
    /*
    * Hurt: parameters:
    * int damage = how much to hurt the creature.
     */
    public void setFollowing(boolean follow){following = follow;}

    public void SetMaxHP(int newMax) {
        hitPointsMax = newMax;
    }

    public void Hurt(int damage) {
        hitPoints -= damage;
    }

    public void SetDefense(float newDef) {
        if (newDef > defenseMax) {
            defense = defenseMax;
            return;
        } else {
            defense = newDef;
        }
    }
}
