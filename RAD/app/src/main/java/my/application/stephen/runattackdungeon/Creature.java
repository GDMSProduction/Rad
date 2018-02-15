package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by Stephen on 2018-01-20.
 */

public class Creature extends ObjectDestructable {

    private float defense = 0.0f;
    private float defenseMax = 0.8f;
    private int attack = 0;
    private int attackMax = 10;
    private int dig = 0;
    private int digMax = 10;
    private boolean following = false;
    private ObjectDestructable weapon;

    Creature(Point newPoint, Bitmap newBitmap, int HPMax) {
        super(newPoint, newBitmap, HPMax);
    }

    Creature(Point newPoint, Bitmap newBitmap, int HPMax, float DefMax) {
        super(newPoint, newBitmap, HPMax);
        defense = defenseMax = DefMax;
    }

    //getters

    public int GetAttack() {
        return attack;
    }

    public int GetAttackMax() {
        return attackMax;
    }

    public int GetDig() {
        return dig;
    }

    public int GetDigMax() {
        return digMax;
    }

    public float GetDef() {
        return defense;
    }

    public float GetDefenseMax() {
        return defenseMax;
    }

    public boolean isFollowing() {
        return following;
    }

    /*
    * Hurt: parameters:
    * int damage = how much to hurt the creature.
     */
    public void setFollowing(boolean follow) {
        following = follow;
    }

    public void SetAttack(int newAttack) {
        attack = attackMax = newAttack;
    }

    public void SetDig(int newMiningPower){
        dig = digMax = newMiningPower;
    }

    public void increaseAttack(int increaseToAttack) {
        if (increaseToAttack + attack >= attackMax) {
            attack = attackMax;
        } else {
            attack += increaseToAttack;
        }
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
