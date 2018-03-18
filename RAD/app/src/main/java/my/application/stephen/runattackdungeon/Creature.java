package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import static my.application.stephen.runattackdungeon.Creature.DirectionType.Random;
import static my.application.stephen.runattackdungeon.Creature.MovementLimit.inCamera;
import static my.application.stephen.runattackdungeon.GameView.imageWearables;

/**
 * Created by Stephen on 2018-01-20.
 * Creatures are any animated "living" thing in the system, things that move and have behavior.
 */

public class Creature extends ObjectDestructible {

    private DirectionType directionType = Random;
    private MovementLimit movementLimit = inCamera;
    private Point target = new Point (0,0);
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
    private ArrayList<Point3d> Path = new ArrayList<>(0);

    Creature(Point newPoint, Bitmap newBitmap, int HPMax) {
        super(newPoint, newBitmap, HPMax);
        setCellType(CellType.Slime);
    }

    Creature(Point newPoint,
             Bitmap newBitmap,
             int HPMax,
             CellType Type,
             int DefMax,
             int Attack,
             int Level,
             DirectionType DIRECTIONTYPE) {
        super(newPoint, newBitmap, HPMax, Type);
        defense = defenseMax = DefMax;
        setCellType(Type);
        setAttack(Attack);
        setCurrentDepth(Level);
        directionType = DIRECTIONTYPE;
    }

    int getAttack() {
        if (attack < 0) {
            return 0;
        }
        return attack;
    }

    public void setAttack(int newAttack) {
        attack = attackMax = newAttack;
    }

    public int getAttackMax() {
        return attackMax;
    }

    //getters

    int getMining() {
        return mine;
    }

    public void setMining(int newMiningPower) {
        mine = mineMax = newMiningPower;
    }

    public int getMiningMax() {
        return mineMax;
    }

    int getDefense() {
        return defense;
    }

    public void setDefense(int newDef) {
        if (newDef > defenseMax) {
            defense = defenseMax;
        } else {
            defense = newDef;
        }
    }

    public int getDefenseMax() {
        return defenseMax;
    }

    int getCurrentDepth() {
        return currentDepth;
    }

    void setCurrentDepth(int newDepth) {
        currentDepth = newDepth;
    }

    Weapon getWeapon() {
        return weapon;
    }

    MiningTool getMiningTool() {
        return miningTool;
    }

    LightSource getLightSource() {
        return lightSource;
    }

    Wearable getRing() {
        return ring;
    }

    Wearable getShield() {
        return shield;
    }

    Food getFood() {
        return food;
    }

    Food getPotion() {
        return potion;
    }

    Clutter getScroll() {
        return scroll;
    }

    MovementLimit getMovementLimit() {
        return movementLimit;
    }

    DirectionType getDirectionType() {
        return directionType;
    }

    ArrayList<Point3d> getPath() {
        return Path;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    int getScore() {
        return score;
    }

    int getTotalValue() {
        int totalValue = score;
        if (weapon != null) {
            totalValue += weapon.getValue();
        }
        if (miningTool != null) {
            totalValue += miningTool.getValue();
        }
        if (lightSource != null) {
            totalValue += lightSource.getValue();
        }
        if (ring != null) {
            totalValue += ring.getValue();
        }
        if (shield != null) {
            totalValue += shield.getValue();
        }
        if (food != null) {
            totalValue += food.getValue();
        }
        if (potion != null) {
            totalValue += potion.getValue();
        }
        if (scroll != null) {
            totalValue += scroll.getValue();
        }
        return totalValue;
    }

    public int getLevel() {
        return level;
    }

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

    Food setPotion(@Nullable  Food newPotion) {
        Food ret = potion;
        if (ret != null) {
            ret.setOwner(null);
        }
        potion = newPotion;
        if (potion != null) {
            potion.setOwner(this);
        }
        return ret;
    }

    Clutter setScroll(@Nullable Clutter newScroll) {
        Clutter ret = scroll;
        if (ret != null){
            ret.setOwner(null);
        }
        scroll = newScroll;
        if (scroll != null) {
            scroll.setOwner(this);
        }
        return ret;
    }

    Food setFood(Food newFood) {
        Food ret = food;
        if (ret != null){
            ret.setOwner(null);
        }
        food = newFood;
        if (food != null){
            food.setOwner(this);
        }
        return ret;
    }

    Wearable setWearable(Wearable newWearable) {
        Wearable ret = null;
        if (newWearable.getBitmap() == imageWearables[0] || newWearable.getBitmap() == imageWearables[1]) {
            ret = setRing(newWearable);
        } else if (newWearable.getBitmap() == imageWearables[2]) {
            ret = setShield(newWearable);
        }
        return ret;
    }

    private Wearable setShield(Wearable newShield) {
        Wearable ret = shield;
        if (ret != null) {
            setDefense(defenseMax - shield.getTotalPower());
            ret.setOwner(null);
        }
        shield = newShield;
        if (shield != null) {
            setDefense(defenseMax + shield.getTotalPower());
            shield.setOwner(this);
        }
        return ret;
    }

    private Wearable setRing(Wearable newRing) {
        Wearable ret = ring;
        if (ret != null) {
            switch (ring.getEnchantType()) {
                case Attack:
                    setAttack(attackMax - ring.getTotalPower());
                    break;
                case Health:
                    setMaxHP(getMaxpHP() - ring.getTotalPower());
                    break;
                case Defense:
                    setDefense(defenseMax - ring.getTotalPower());
                    break;
            }
            ret.setOwner(null);
        }
        ring = newRing;
        if (ring != null) {
            switch (ring.getEnchantType()) {
                case Attack:
                    setAttack(attackMax + ring.getTotalPower());
                    break;
                case Health:
                    setMaxHP(getMaxpHP() + ring.getTotalPower());
                    break;
                case Defense:
                    setDefense(defenseMax + ring.getTotalPower());
                    break;
            }
            ring.setOwner(this);
        }
        return ret;
    }

    Weapon setWeapon(Weapon newWeapon) {
        Weapon ret = weapon;
        if (ret != null) {
            int tempAtk = attackMax - (weapon.getAttackPower() + weapon.getEnchantModifier());
            if (tempAtk < 0) {
                tempAtk = 0;
            }
            setAttack(tempAtk);
            ret.setOwner(null);
        }
        weapon = newWeapon;
        if (weapon != null) {
            setAttack(attackMax + weapon.getAttackPower() + weapon.getEnchantModifier());
            weapon.setOwner(this);
        }
        return ret;
    }

    public MiningTool setMiningTool(MiningTool newMiningTool) {
        MiningTool ret = miningTool;
        if (ret != null) {
            int tempMine = mineMax - (miningTool.getMinePower() + miningTool.getEnchantModifier());
            if (tempMine < 0) {
                tempMine = 0;
            }
            setMining(tempMine);
            ret.setOwner(null);
        }
        miningTool = newMiningTool;
        if (miningTool != null) {
            setMining(mineMax + miningTool.getMinePower() + miningTool.getEnchantModifier());
            miningTool.setOwner(this);
        }
        return ret;
    }

    public LightSource setLightSource(LightSource newLightSource) {
        LightSource ret = lightSource;
        if (ret != null) {
            int tempLightRadius = lightRadius - lightSource.getLightRadius();
            ret.setOwner(null);
        }
        lightSource = newLightSource;
        if (lightSource != null) {
            setLight(lightSource.getLightRadius());
            lightSource.setOwner(this);
        } else {
            setLight(0);
        }
        return ret;
    }

    public void incrementScore(int points) {
        score += points;
    }

    public void levelUP() {
        level++;
        setMaxHP((int) (getMaxpHP() * 1.2));
    }

    public void setLight(int newLightRadius) {
        lightRadius = newLightRadius;
    }

    public void increaseAttack(int increaseToAttack) {
        if (increaseToAttack + attack >= attackMax) {
            attack = attackMax;
        } else {
            attack += increaseToAttack;
        }
    }

    public void useFood() {
        heal(getFood().getHealing());
        setFood(null);
    }

    public void useScroll(Dungeon dungeon) {
        Dungeon.DirectionToGo direction;
        if (scroll.getValue() >= currentDepth){
            direction = Dungeon.DirectionToGo.DOWN;
        } else {
            direction = Dungeon.DirectionToGo.UP;
        }
        dungeon.goToLevel(this, scroll.getValue(), direction, false);
        setScroll(null);
    }

    public void usePotion(Level currentLevel) {
        getPotion().PotionEffect(this, currentLevel);
        setPotion(null);
    }

    public Point getTarget() {
        return target;
    }

    public void setTarget(Point Target) {
        target = Target;
    }

    public enum DirectionType {
        Still,
        LeftandRight,
        UpandDown,
        Random,
        TowardsTargetDirectional,
        TowardsTargetDodgeObstacles,
        TowardsTargetEfficient
    }

    public enum MovementLimit {
        inCamera,
        inLevel,
        inDungeon,
        inWorld
    }

    public enum state {Chase, Wander, Dead, Gather}

}
