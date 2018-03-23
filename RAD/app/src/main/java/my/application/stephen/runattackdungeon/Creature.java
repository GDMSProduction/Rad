package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import my.application.stephen.runattackdungeon.Dungeon.DirectionToGo;

import static my.application.stephen.runattackdungeon.GameView.changeLighting;
import static my.application.stephen.runattackdungeon.GameView.imageWearables;

/**
 * Created by Stephen on 2018-01-20.
 * Creatures are any animated "living" thing in the system, things that move and have behavior.
 */

public class Creature extends ObjectDestructible {

    public enum DirectionType {
        Still,
        Horizontal,
        Vertical,
        HorizontalAndVertical,
    }
    public enum MovementType{
        Random,
        TowardsTargetDirectional,
        TowardsTargetDodgeObstacles,
        TowardsTargetEfficient
    }
    public enum Handicap{
        OneWaitTwo,
        OneWaitOne,
        TwoWaitTwo,
        TwoWaitOne,
        Full
    }
    public enum MovementLimit {
        inCamera,
        inLevel,
        inDungeon,
        inWorld
    }
    public enum state {
        Chase,
        Patrol,
        Wander,
        Gather,
        Rest
    }
    public enum PatrolType{
        Points,
        Line
    }

    private PatrolType patrolType = PatrolType.Line;
    private state creatureState = state.Patrol;
    private DirectionToGo creatureDirection = DirectionToGo.UP;
    private DirectionType directionType = DirectionType.HorizontalAndVertical;
    private MovementType movementType = MovementType.TowardsTargetDirectional;
    private Handicap handicap = Handicap.Full;
    private MovementLimit movementLimit = MovementLimit.inCamera;
    private Point3d target = new Point3d(0, 0, 0);
    private int patrolState = 0;
    private int handicapWait = 0;
    private int defense = 0;
    private int defenseMax = 80;//out of 100
    private int attack = 0;
    private int attackMax = 0;
    private int mine = 0;
    private int mineMax = 2;
    private int lightRadius = 0;
//    private int currentDepth = 0;
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
    private ArrayList<Point3d> Path = new ArrayList<>();
    private ArrayList<Point3d> PatrolPoints = new ArrayList<>();

    Creature(Point3d newPoint, Bitmap newBitmap, int HPMax) {
        super(newPoint, newBitmap, HPMax);
        setCellType(CellType.Slime);
    }
    Creature(Point3d newPoint, Bitmap newBitmap, int HPMax, CellType Type, int DefMax, int Attack, int Level, DirectionType DIRECTIONTYPE) {
        super(newPoint, newBitmap, HPMax, Type);
        defense = defenseMax = DefMax;
        setCellType(Type);
        setAttack(Attack);
        setZ(Level);
        directionType = DIRECTIONTYPE;
    }

    //Mutators

    void setAttack(int newAttack) {
        attack = attackMax = newAttack;
    }
    private void setMining(int newMiningPower) {
        mine = mineMax = newMiningPower;
    }
    void setDefense(int newDef) {
        if (newDef > defenseMax) {
            defense = defenseMax;
        } else {
            defense = newDef;
        }
    }
    public void setName(String newName) {
        name = newName;
    }
    public void setFollowing(boolean follow) {
        following = follow;
    }
    Food setPotion(@Nullable Food newPotion) {
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
        if (ret != null) {
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
        if (ret != null) {
            ret.setOwner(null);
        }
        food = newFood;
        if (food != null) {
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
    Wearable setShield(Wearable newShield) {
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
    Wearable setRing(Wearable newRing) {
        Wearable ret = ring;
        if (ret != null) {
            switch (ring.getEnchantType()) {
                case Attack:
                    setAttack(attackMax - ring.getTotalPower());
                    break;
                case Health:
                    setMaxHP(getMaxHP() - ring.getTotalPower());
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
                    setMaxHP(getMaxHP() + ring.getTotalPower());
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
    MiningTool setMiningTool(MiningTool newMiningTool) {
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
    LightSource setLightSource(LightSource newLightSource) {
        LightSource ret = lightSource;
        if (ret != null) {
            ret.setOwner(null);
        }
        lightSource = newLightSource;
        if (lightSource != null) {
            lightSource.setOwner(this);
            lightSource.setPoint(getPoint());
        } else {
            setLight(0);
        }
        changeLighting = true;
        return ret;
    }
    void setLight(int newLightRadius) {
        lightRadius = newLightRadius;
    }
    void setTarget(Point3d Target) {
        target = Target;
    }
    void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }
    void setHandicap(Handicap handicap) {
        this.handicap = handicap;
    }
    public void setHandicapWait(int handicapWait) {
        this.handicapWait = handicapWait;
    }
    public void setCreatureState(state creatureState) {
        this.creatureState = creatureState;
    }
    public void setPatrolState(int patrolState) {
        this.patrolState = patrolState;
    }
    public void setPatrolType(PatrolType patrolType) {
        this.patrolType = patrolType;
    }
    public void setPatrolPoints(ArrayList<Point3d> patrolPoints) {
        PatrolPoints = patrolPoints;
    }
    public void setDirectionType(DirectionType newDirectionType){directionType = newDirectionType;}

    //Accessors

    int getAttack() {
        if (attack < 0) {
            return 0;
        }
        return attack;
    }
    int getAttackMax() {
        return attackMax;
    }
    int getMining() {
        return mine;
    }
    int getMiningMax() {
        return mineMax;
    }
    int getDefense() {
        return defense;
    }
    int getDefenseMax() {
        return defenseMax;
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
    int getLevel() {
        return level;
    }
    boolean isFollowing() {
        return following;
    }
    Point3d getTarget() {
        return target;
    }
    Point get2dTarget(){
        return new Point(target.x, target.y);
    }
    public MovementType getMovementType() {
        return movementType;
    }
    public Handicap getHandicap() {
        return handicap;
    }
    public int getHandicapWait() {
        return handicapWait;
    }
    public state getCreatureState() {
        return creatureState;
    }
    public int getPatrolState() {
        return patrolState;
    }
    public PatrolType getPatrolType() {
        return patrolType;
    }
    public ArrayList<Point3d> getPatrolPoints() {
        return PatrolPoints;
    }

    //Helper Functions

    void incrementScore(int points) {
        score += points;
    }
    void levelUP() {
        level++;
        setMaxHP((int) (getMaxHP() * 1.2));
    }
    void increaseAttack(int increaseToAttack) {
        if (increaseToAttack + attack >= attackMax) {
            attack = attackMax;
        } else {
            attack += increaseToAttack;
        }
    }
    void useFood(int dungeonSize) {
        heal(dungeonSize, getFood().getHealing());
        setFood(null);
    }
    void useScroll(Dungeon dungeon) {
        DirectionToGo direction;
        if (scroll.getValue() >= getZ()) {
            direction = DirectionToGo.DOWN;
        } else {
            direction = DirectionToGo.UP;
        }
        dungeon.goToLevel(this, scroll.getValue(), direction, false);
        setScroll(null);
    }
    void usePotion(Food POTION, int dungeonSize, Level currentLevel) {
        POTION.PotionEffect(dungeonSize, this, currentLevel);
        if (potion == POTION) {
            setPotion(null);
        }
    }
}
