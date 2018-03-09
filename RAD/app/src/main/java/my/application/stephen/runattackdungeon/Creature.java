package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.ArrayList;

import static my.application.stephen.runattackdungeon.Creature.DirectionType.Random;
import static my.application.stephen.runattackdungeon.Creature.MovementLimit.inCamera;
import static my.application.stephen.runattackdungeon.Creature.MovementType.None;
import static my.application.stephen.runattackdungeon.GameView.imageWearables;

/**
 * Created by Stephen on 2018-01-20.
 */

public class Creature extends ObjectDestructible {

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
    private DirectionType directionType = Random;
    private MovementType movementType = None;
    private MovementLimit movementLimit = inCamera;
    private int defense = 0;
    private int defenseMax = 80;//out of 100
    private int attack = 0;
    private int attackMax = 0;
    private int mine = 0;
    private int mineMax = 2;
    private int lightRadius = 0;
    private int currentDepth = 0;
    private boolean following = false;
    private String name = "";
    private int score = 0;
    private int level = 0;
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
        setCellType(CellType.Slime);
    }

    Creature(Point newPoint, Bitmap newBitmap, int HPMax, int DefMax) {
        super(newPoint, newBitmap, HPMax);
        defense = defenseMax = DefMax;
        setCellType(CellType.Slime);
    }
    Creature(Point newPoint, Bitmap newBitmap, int HPMax, CellType Type, int DefMax) {
        super(newPoint, newBitmap, HPMax, Type);
        defense = defenseMax = DefMax;
        setCellType(CellType.Slime);
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

    public int getMining() {
        return mine;
    }

    public int getMiningMax() {
        return mineMax;
    }

    public int getDefense() {
        return defense;
    }

    public int getDefenseMax() {
        return defenseMax;
    }

    public int getCurrentDepth() {return currentDepth;}

    public Weapon getWeapon(){return weapon;}
    public MiningTool getMiningTool(){return miningTool;}
    public LightSource getLightSource(){return lightSource;}
    public Wearable getRing() {return ring;}
    public Wearable getShield() {return shield;}
    public Food getFood() {return food;}
    public Food getPotion() {return potion;}
    public Clutter getScroll() {return scroll;}
    public MovementType getMovementType() {return movementType;}
    public DirectionType getDirectionType() {return directionType;}
    public ArrayList<Point3d> getPath() {return Path;}
    public String getName(){return name;}
    public int getScore(){return score;}
    public int getLevel(){return level;}

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

    public void setCurrentDepth(int newDepth){currentDepth = newDepth;}

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
        if (shield != null) {
            setDefense(defenseMax + shield.getPower());
        }
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
        if (ring != null) {
            switch (ring.getEnchantType()) {
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
        if (weapon != null) {
            setAttack(attackMax + weapon.getAttackPower() + weapon.getEnchantModifier());
        }
        return ret;
    }
    public MiningTool setMiningTool(MiningTool newMiningTool){
        MiningTool ret = miningTool;
        if (miningTool != null) {
            int tempMine = mineMax - (miningTool.getMinePower() + miningTool.getEnchantModifier());
            if (tempMine < 0) {
                tempMine = 0;
            }
            setMining(tempMine);
        }
        miningTool = newMiningTool;
        if (miningTool != null) {
            setMining(mineMax + miningTool.getMinePower() + miningTool.getEnchantModifier());
        }
        return ret;
    }
    public LightSource setLightSource(LightSource newLightSource){
        LightSource ret = lightSource;
        if (lightSource != null) {
            int tempLightRadius = lightRadius - lightSource.getLightRadius();
        }
        lightSource = newLightSource;
        if (lightSource != null) {
            setLight(lightSource.getLightRadius());
        } else {
            setLight(0);
        }
        return ret;
    }

    public void setAttack(int newAttack) {
        attack = attackMax = newAttack;
    }
    public void setDefense(int newDef) {
        if (newDef > defenseMax) {
            defense = defenseMax;
            return;
        } else {
            defense = newDef;
        }
    }

    public void setMining(int newMiningPower){
        mine = mineMax = newMiningPower;
    }

    public void setName(String newName){name = newName;}
    public void incrementScore(int points){ score+=points;}
    public void levelUP(){
        level++;
        setMaxHP((int)(getMaxpHP()*1.2));
    }

    public void setLight(int newLightRadius) {lightRadius = newLightRadius;}

    public void increaseAttack(int increaseToAttack) {
        if (increaseToAttack + attack >= attackMax) {
            attack = attackMax;
        } else {
            attack += increaseToAttack;
        }
    }

    public void useFood(){
        heal(getFood().getHealing());
        setFood(null);
    }
    public void useScroll(Dungeon dungeon){
        dungeon.goToLevel(this, scroll.getValue(), Dungeon.DirectionToGo.UP);
        setScroll(null);
    }
    public void usePotion(Level currentLevel){
        getPotion().PotionEffect(this, currentLevel);
        setPotion(null);
    }

    public boolean Update(Point target){
        boolean isAtTarget = false;
        switch (super.getCellType()){
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
