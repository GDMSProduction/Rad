package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by zfile on 2018-02-02.
 */

public class ObjectDestructible extends ObjectBase {
    public enum CellType {
        Wall, SturdyWall, BreakingWall, Space, Void, Border,
        StairUp, StairDown,
        Rock, Clutter, Barrel, Chest,
        Slime, Goblin, Minotaur, Humanoid,
        //everything that can be picked up/interacted with.
        Weapon, MiningTool, LightSource, Wearable,
        Food, Scroll, Potion
    }
    private CellType cellType = CellType.SturdyWall;

    private int hitPoints = 15;
    private int hitPointsMax = 15;

    ObjectDestructible(Point newPoint, Bitmap newBitmap, int HPMax) {
        super(newPoint, newBitmap);
        hitPoints = hitPointsMax = HPMax;
    }
    ObjectDestructible(Point newPoint, Bitmap newBitmap, int HPMax, CellType CELLTYPE) {
        super(newPoint, newBitmap);
        hitPoints = hitPointsMax = HPMax;
        cellType = CELLTYPE;
    }

    //Accessors
    public int getHP() {
        return hitPoints;
    }

    public int getMaxpHP() {
        return hitPointsMax;
    }

    public CellType getCellType(){return cellType;}

    //Mutators
    public void setCellType(CellType newCellType){cellType = newCellType;}

    //Helper Functions
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
        if(hitPoints == hitPointsMax) {
            hitPoints++;
            hitPointsMax++;
        } else if (healing + hitPoints >= hitPointsMax) {
            hitPoints = hitPointsMax;
        } else {
            hitPoints += healing;
        }
    }
}
