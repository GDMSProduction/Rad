package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.ArrayList;

import static my.application.stephen.runattackdungeon.Creature.CreatureType.Slime;
import static my.application.stephen.runattackdungeon.Creature.DirectionType.Random;
import static my.application.stephen.runattackdungeon.Creature.DirectionType.Still;
import static my.application.stephen.runattackdungeon.Creature.MovementLimit.inCamera;
import static my.application.stephen.runattackdungeon.Creature.MovementType.None;
import static my.application.stephen.runattackdungeon.GameView.imageWearables;

/**
 * Created by Stephen on 2018-01-20.
 */

public class Creature extends ObjectDestructible {

    public enum CreatureType{
        Slime,
        Goblin,
        Minotaur,
        Humanoid
    }
    public enum DirectionType{
        Still,
        LeftandRight,
        UpandDown,
        Random,
        TowardsTargetDirectional,
        TowardsTargetEfficient
    }
    public enum MovementType{
        None,
        OneWaitOne,
        TwoWaitOne,
        Full,

    }
    public enum MovementLimit{
        inCamera,
        inLevel,
        inDungeon,
        inWorld
    }
    private CreatureType creatureType = Slime;
    private DirectionType directionType = Random;
    private MovementType movementType = None;
    private MovementLimit movementLimit = inCamera;
    private float defense = 0.0f;
    private float defenseMax = 0.8f;
    private int attack = 0;
    private int attackMax = 0;
    private int dig = 0;
    private int digMax = 2;
    private int lightRadius = 0;
    private boolean following = false;
    private Weapon weapon = null;
    private MiningTool miningTool = null;
    private LightSource lightSource = null;
    private Wearable ring = null;
    private Wearable shield = null;
    private Food food = null;
    private Food potion = null;
    private Clutter scroll = null;
    private ArrayList<Point3d> Path = new ArrayList<Point3d>(0);

    Creature(Point newPoint, Bitmap newBitmap, int HPMax) {
        super(newPoint, newBitmap, HPMax);
    }

    Creature(Point newPoint, Bitmap newBitmap, int HPMax, float DefMax) {
        super(newPoint, newBitmap, HPMax);
        defense = defenseMax = DefMax;
    }
    Creature(CreatureType Type, Point newPoint, Bitmap newBitmap, int HPMax, float DefMax) {
        super(newPoint, newBitmap, HPMax);
        creatureType = Type;
        defense = defenseMax = DefMax;
    }

    //getters

    public int getAttack() {
        if (attack < 0){
            return 0;
        }
        return attack;
    }

    public int getAttackMax() {
        return attackMax;
    }

    public int getDig() {
        return dig;
    }

    public int getDigMax() {
        return digMax;
    }

    public float getDef() {
        return defense;
    }

    public float getDefenseMax() {
        return defenseMax;
    }

    public Weapon getWeapon(){return weapon;}
    public MiningTool getMiningTool(){return miningTool;}
    public LightSource getLightSource(){return lightSource;}
    public Wearable getRing() {return ring;}
    public Wearable getShield() {return shield;}
    public Food getFood() {return food;}
    public Food getPotion() {return potion;}
    public Clutter getScroll() {return scroll;}
    public CreatureType getCreatureType() {return creatureType;}
    public MovementType getMovementType() {return movementType;}
    public DirectionType getDirectionType() {return directionType;}
    public ArrayList<Point3d> getPath() {return Path;}

    public boolean isFollowing() {
        return following;
    }

    /*
    * hurt: parameters:
    * int damage = how much to hurt the creature.
     */
    public void setFollowing(boolean follow) {
        following = follow;
    }

    public void setCreatureType(CreatureType newType){
        creatureType = newType;}

    public Food setPotion(Food newPotion){
        Food ret = potion;
        potion = newPotion;
        return ret;
    }
    public Clutter setScroll(Clutter newScroll){
        Clutter ret = scroll;
        scroll = newScroll;
        return ret;
    }
    public Food setFood(Food newFood){
        Food ret = food;
        food = newFood;
        return ret;
    }
    public Wearable setWearable(Wearable newWearable){
        Wearable ret = null;
        if (newWearable.getBitmap() == imageWearables[0] || newWearable.getBitmap() == imageWearables[1]){
            ret = setRing(newWearable);
        } else if (newWearable.getBitmap() == imageWearables[2]){
            ret = setShield(newWearable);
        }
        return ret;
    }
    public Wearable setShield(Wearable newShield){
        Wearable ret = shield;
        if (shield != null){
            setDefense(defenseMax - shield.getPower());
        }
        shield = newShield;
        setDefense(defenseMax + shield.getPower());
        return ret;
    }
    public Wearable setRing(Wearable newRing){
        Wearable ret = ring;
        if (ring != null){
            switch(ring.getEnchantType()){
                case Attack:
                    setAttack(attackMax - ring.getPower());
                    break;
                case Health:
                    setMaxHP(getMaxpHP() - ring.getPower());
                    break;
                case Defense:
                    setDefense(defenseMax - ring.getPower());
                    break;
            }
        }
        ring = newRing;
        switch(ring.getEnchantType()){
            case Attack:
                setAttack(attackMax + ring.getPower());
                break;
            case Health:
                setMaxHP(getMaxpHP() + ring.getPower());
                break;
            case Defense:
                setDefense(defenseMax + ring.getPower());
                break;
        }
        return ret;
    }
    public Weapon setWeapon(Weapon newWeapon){
        Weapon ret = weapon;
        if (weapon != null) {
            int tempAtk = attackMax - (weapon.getAttackPower() + weapon.getEnchantModifier());
            if (tempAtk < 0) {
                tempAtk = 0;
            }
            setAttack(tempAtk);
        }
        weapon = newWeapon;
        setAttack(attackMax + weapon.getAttackPower() + weapon.getEnchantModifier());
        return ret;
    }
    public MiningTool setMiningTool(MiningTool newMiningTool){
        MiningTool ret = miningTool;
        if (miningTool != null) {
            int tempDig = digMax - (miningTool.getDigPower() + miningTool.getEnchantModifier());
            if (tempDig < 0) {
                tempDig = 0;
            }
            setDig(tempDig);
        }
        miningTool = newMiningTool;
        setDig(digMax + miningTool.getDigPower() + miningTool.getEnchantModifier());
        return ret;
    }
    public LightSource setLightSource(LightSource newLightSource){
        LightSource ret = lightSource;
        if (lightSource != null) {
            int tempLightRadius = lightRadius - lightSource.getLightRadius();
        }
        lightSource = newLightSource;
        setLight(lightSource.getLightRadius());
        return ret;
    }

    public void setAttack(int newAttack) {
        attack = attackMax = newAttack;
    }

    public void setDig(int newMiningPower){
        dig = digMax = newMiningPower;
    }

    public void setDefense(float newDef) {
        if (newDef > defenseMax) {
            defense = defenseMax;
            return;
        } else {
            defense = newDef;
        }
    }

    public void setLight(int newLightRadius) {lightRadius = newLightRadius;}

    public void increaseAttack(int increaseToAttack) {
        if (increaseToAttack + attack >= attackMax) {
            attack = attackMax;
        } else {
            attack += increaseToAttack;
        }
    }

    public boolean Update(Point target){
        boolean isAtTarget = false;
        switch (creatureType){
            case Slime:
                //No movement unless interacted with.
                break;
            case Goblin:

                break;
            case Minotaur:
                //A*, if in line of sight, charge.
                break;
            case Humanoid:
                //A*
                break;
        }
        return isAtTarget;
    }

}
