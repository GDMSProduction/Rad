package com.example.stephen.runattackdungeon;

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

    Creature(Point newPoint, Bitmap newBitmap) {
        super(newPoint, newBitmap);
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

    /*
    * Hurt: parameters:
    * int damage = how much to hurt the creature.
     */
    public void Hurt(int damage) {
        hitPoints -= damage;
    }

    public void SetDefense(float newDef) {
        if (newDef > defenseMax) {
            defense = defenseMax;
            return;
        }
        else{
            defense = newDef;
        }
    }
}
