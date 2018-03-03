package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.ArrayList;

import static my.application.stephen.runattackdungeon.GameView.imageClutter;
import static my.application.stephen.runattackdungeon.GameView.imageEnemy;
import static my.application.stephen.runattackdungeon.GameView.imageFood;
import static my.application.stephen.runattackdungeon.GameView.imageLight;
import static my.application.stephen.runattackdungeon.GameView.imageMining;
import static my.application.stephen.runattackdungeon.GameView.imagePotion;
import static my.application.stephen.runattackdungeon.GameView.imageScroll;
import static my.application.stephen.runattackdungeon.GameView.imageStairs;
import static my.application.stephen.runattackdungeon.GameView.imageWeapon;
import static my.application.stephen.runattackdungeon.GameView.imageWearables;
import static my.application.stephen.runattackdungeon.GameView.spaces;

/**
 * Created by Stephen on 2018-01-25.
 */

public class Level extends Map {

    public enum CellType {
        Wall, Space,
        Clutter, Barrel, Chest,
        Slime, Goblin, Minotaur, Humanoid,
        //everything that can be picked up/interacted with.
        Weapon, MiningTool, LightSource, Wearable,
        Food, Scroll, Potion
    }

    private int mBitMapHeight;
    private int mBitMapWidth;
    //The Clutter
    private int maxClutter = 5;
    private ArrayList<Clutter> clutter = new ArrayList<Clutter>(maxClutter);
    private int diamondPercent = 1;
    private ArrayList<Food> food = new ArrayList<>(0);
    private ArrayList<Food> potions = new ArrayList<>(0);
    private ArrayList<LightSource> lights = new ArrayList<>(0);
    private ArrayList<MiningTool> miningTools = new ArrayList<>(0);
    private ArrayList<Clutter> scrolls = new ArrayList<>(0);
    private ArrayList<Wearable> wearables = new ArrayList<>(0);
    private ArrayList<Weapon> weapons = new ArrayList<>(0);
    //The Stairs
    private ObjectBase stairsUp;
    private ObjectBase stairsDown;
    //the creatures
    private int maxEnemies = 5;
    private ArrayList<Creature> creatures = new ArrayList<Creature>(maxEnemies);

    Level(int Width, int Height, boolean natural) {
        super(Width, Height, natural);

        //Height and Width of one cell
        mBitMapHeight = spaces[0].getHeight();
        mBitMapWidth = spaces[0].getWidth();
        int TotalSpaces = GetNumEmptyPoints() - 2;
        createStairs();
        maxClutter = TotalSpaces * 1 / 18;
        if (maxClutter < 1) {
            maxClutter = 1;
        }
        maxEnemies = TotalSpaces * 2 / 18;
        if (maxEnemies < 1) {
            maxEnemies = 1;
        }
        createEnemies();
        createClutter();
    }

    //Getters
    public ObjectBase getStairsUp() {
        return stairsUp;
    }

    public ObjectBase getStairsDown() {
        return stairsDown;
    }

    public ArrayList<Creature> getCreatures() {
        return creatures;
    }

    public ArrayList<Clutter> getClutter() {
        return clutter;
    }

    public ArrayList<Food> getFood() {
        return food;
    }

    public ArrayList<Food> getPotions() {
        return potions;
    }

    public ArrayList<LightSource> getLights() {
        return lights;
    }

    public ArrayList<MiningTool> getMiningTools() {
        return miningTools;
    }

    public ArrayList<Clutter> getScrolls() {
        return scrolls;
    }

    public ArrayList<Wearable> getWearables() {
        return wearables;
    }

    public ArrayList<Weapon> getWeapons() {
        return weapons;
    }

    //Setters
    //Helper Functions
    public int getNewEmptyPointIndex() {
        return rand.nextInt(GetNumEmptyPoints());
    }

    private void createClutter() {
        clutter.clear();
        int newSize = rand.nextInt(maxClutter) + 1;
        clutter = new ArrayList<Clutter>(newSize);
        for (int i = 0; i < newSize; i++) {
            int clutterVal = 10;
            Bitmap clutterBitmap = imageClutter[1];
            Point clutterPoint = new Point(0, 0);
            int clutterHP = 1;

            Clutter temp = new Clutter(clutterVal, clutterPoint, clutterBitmap, clutterHP);
            switch (rand.nextInt(5)) {
                default:
                case 0:
                    //rock
                    temp.setBitMap(imageClutter[0]);
                    temp.setValue(0);
                    break;
                case 1:
                    //barrel
                    temp.setBitMap(imageClutter[1]);
                    temp.setValue(10);
                    break;
                case 2:
                    //chest
                    temp.setBitMap(imageClutter[2]);
                    temp.setValue(30);
                    break;
            }
            int newPoint = getNewEmptyPointIndex();
            temp.setPoint(GetFloorPoints().get(newPoint));
            TakeAwayEmptyFloorTiles(newPoint);
            clutter.add(temp);
        }
    }

    private void createEnemies() {
        //Create Enemies
        creatures.clear();
        int newSize = rand.nextInt(maxEnemies) + 1;
        creatures = new ArrayList<Creature>(newSize);
        for (int i = 0; i < newSize; i++) {
            int hPMax = 3;
            int defMax = 0;
            Bitmap enemyBitmap = imageEnemy[0];
            int newPoint = getNewEmptyPointIndex();
            Point enemyPoint = GetFloorPoints().get(newPoint);

            Creature temp;
            switch (rand.nextInt(4)) {
                default:
                case 0:
                    temp = new Creature(Creature.CreatureType.Slime, enemyPoint, enemyBitmap, hPMax, defMax);
                    temp.setAttack(1000);
                    break;
                case 1:
                    hPMax = 5;
                    defMax = 50;
                    enemyBitmap = imageEnemy[1];
                    temp = new Creature(Creature.CreatureType.Goblin, enemyPoint, enemyBitmap, hPMax, defMax);
                    temp.setAttack(1);
                    break;
            }

            TakeAwayEmptyFloorTiles(newPoint);

            creatures.add(temp);
        }
    }

    private void createStairs() {
        int newPoint = getNewEmptyPointIndex();
        stairsDown = new ObjectBase(GetFloorPoints().get(newPoint),
                imageStairs[0]);
        TakeAwayEmptyFloorTiles(newPoint);
        newPoint = getNewEmptyPointIndex();
        stairsUp = new ObjectBase(GetFloorPoints().get(newPoint),
                imageStairs[1]);
        TakeAwayEmptyFloorTiles(newPoint);
    }

    private void CreateCoins(int value, Point point) {
        Clutter coins = new Clutter(value, point, imageClutter[3], 0);
        clutter.add(coins);
    }

    private void CreateHeartDiamond(Point point) {
        Clutter diamondRed = new Clutter(200, point, imageClutter[5], 0);
        clutter.add(diamondRed);
    }

    private void CreateWhiteDiamond(Point point) {
        Clutter diamond = new Clutter(200, point, imageClutter[4], 0);
        clutter.add(diamond);
    }

    private void CreateRandomDiamond(Point point) {
        switch (rand.nextInt(2)) {
            default:
            case 0:
                CreateWhiteDiamond(point);
                break;
            case 1:
                CreateHeartDiamond(point);
                break;
        }
    }

    private void CreateRandomTreasure(int value, Point point) {
        switch (rand.nextInt(3)) {
            default:
            case 0:
                CreateCoins(value, point);
                break;
            case 1:
                CreateRandomDiamond(point);
                break;
        }
    }

    private void CreateFood(Point point) {
        //Food
        //apple
        Food fud = new Food(1, 2, point, imageFood[0], 2);
        switch (rand.nextInt(2)) {
            case 1:
                //Turkey Leg
                fud.setHealing(3);
                fud.setBitMap(imageFood[1]);
                break;
            default:
                break;
        }
        food.add(fud);
    }

    private void CreatePotion(Point point, int currentLevel) {
        //if it's a green potion, it restores your health.
        Food potion = new Food(Food.PotionColor.Green, 0, point, imagePotion[1], 0);
        switch (rand.nextInt(6)) {
            default:
                potion.setHealing(currentLevel);
                break;
            case 1:
                //if it's a light blue potion, it randomly teleports you to an open space within 10 feet.
                potion.setPotionColor(Food.PotionColor.LightBlue);
                potion.setBitMap(imagePotion[2]);
                break;
            case 2:
                //if it's a black potion, it kills your light source.
                potion.setPotionColor(Food.PotionColor.Black);
                potion.setBitMap(imagePotion[3]);
                break;
            case 3:
                //if it's a red potion, it increases your attack + maxAttack.
                potion.setBitMap(imagePotion[4]);
                potion.setPotionColor(Food.PotionColor.Red);
                potion.setHealing(currentLevel/2);
                break;
            case 4:
                //if it's a purple potion, it poisons you.
                potion.setPotionColor(Food.PotionColor.Purple);
                potion.setBitMap(imagePotion[5]);
                potion.setHealing(currentLevel);
                break;
            case 5:
                //if it's a dark blue potion, it increases your defense.
                potion.setPotionColor(Food.PotionColor.DarkBlue);
                potion.setBitMap(imagePotion[6]);
                potion.setHealing(currentLevel/5);
                break;
        }
        potions.add(potion);
    }

    private void CreateScroll(Point point) {
        Clutter scroll = new Clutter(4, point, imageScroll[0], 2);
        scrolls.add(scroll);
    }

    private void CreateRandomConsumable(Point point, int currentLevel) {
        switch (rand.nextInt(3)) {
            case 0:
                CreatePotion(point, currentLevel);
                break;
            default:
            case 1:
                CreateFood(point);
                break;
            case 2:
                CreateScroll(point);
        }
    }

    private void CreateWeapon(Point point, int currentLevel) {
        int Attack;
        switch (rand.nextInt(4)) {
            default:
            case 0:
                Attack = 2;
                Weapon dagger = new Weapon(Attack, currentLevel, Attack * currentLevel, point, imageWeapon[1], 10);
                weapons.add(dagger);
                break;
            case 1:
                Attack = 4;
                Weapon sword = new Weapon(Attack, (int) (currentLevel / 2), Attack * currentLevel, point, imageWeapon[2], 15);
                weapons.add(sword);
                break;
            case 2:
                Attack = 6;
                Weapon axe = new Weapon(Attack, (int) (currentLevel / 4), Attack * currentLevel, point, imageWeapon[3], 10);
                weapons.add(axe);
                break;
            case 3:
                Attack = 12;
                Weapon bow = new Weapon(Attack, 0, (50 - currentLevel / 2), point, imageWeapon[4], 8);
                weapons.add(bow);
                break;
        }
    }

    private void CreateWearable(Point point, int currentLevel) {
        switch (rand.nextInt(3)) {
            default:
            case 0:
                Wearable shield = new Wearable(
                        Wearable.EnchantType.Defense,
                        5 * currentLevel,
                        5,
                        currentLevel,
                        point,
                        imageWearables[2],
                        12 + currentLevel);
                wearables.add(shield);
                break;
            case 1:
                Wearable silverRing = new Wearable(
                        Wearable.EnchantType.Attack,
                        50 * (currentLevel / 2 + 1),
                        5,
                        currentLevel,
                        point,
                        imageWearables[1],
                        1000);
                wearables.add(silverRing);
                break;
            case 2:
                Wearable goldRing = new Wearable(
                        Wearable.EnchantType.Health,
                        50 * (currentLevel / 2 + 1),
                        5,
                        currentLevel,
                        point,
                        imageWearables[0],
                        1000);
                wearables.add(goldRing);
                break;
        }
    }

    private void CreateMiningTool(Point point, int currentLevel) {
        switch (rand.nextInt(2)) {
            default:
            case 0:
                MiningTool shovel = new MiningTool(1, currentLevel, point, imageMining[0], 10);
                miningTools.add(shovel);
                break;
            case 1:
                MiningTool pickaxe = new MiningTool(3, 2 * (currentLevel + 1), point, imageMining[1], 100);
                miningTools.add(pickaxe);
                break;
        }
    }

    private void CreateLightSource(Point point, int currentLevel) {
        switch (rand.nextInt(2)) {
            default:
            case 0:
                LightSource torch = new LightSource(5, 2, 0, point, imageLight[0], 500 + currentLevel);
                lights.add(torch);
                break;
            case 1:
                LightSource lantern = new LightSource(5, 4, currentLevel + 10, point, imageLight[1], 50 * currentLevel);
                lights.add(lantern);
                break;
        }
    }

    private void CreateRandomDrop(int i, CellType type, int currentLevel) {
        switch (type) {
            case Barrel:
                switch (rand.nextInt(3)) {
                    default:
                    case 0:
                        CreateCoins(clutter.get(i).getValue(), clutter.get(i).getPoint());
                        break;
                    case 1:
                        CreateFood(clutter.get(i).getPoint());
                        break;
                    case 2:
                        CreatePotion(clutter.get(i).getPoint(), currentLevel);
                        break;
                }
                break;
            case Chest:
                switch (rand.nextInt(4)) {
                    default:
                    case 0:
                        CreateCoins(30, clutter.get(i).getPoint());
                        break;
                    case 1:
                        CreateRandomDiamond(clutter.get(i).getPoint());
                        break;
                    case 2:
                        CreateWearable(clutter.get(i).getPoint(), currentLevel);
                        break;
                    case 3:
                        //consumables
                        CreateRandomConsumable(clutter.get(i).getPoint(), currentLevel);
                        break;
                }
                //Clutter dagger = new Clutter(3, clutter.get(i).getPoint(), imageWeapon[0], 3);
                break;
            case Slime:
                switch (rand.nextInt(3)) {
                    default:
                    case 0:
                        int value = rand.nextInt(creatures.get(i).getMaxpHP() * 3) + 1;
                        CreateCoins(value, creatures.get(i).getPoint());
                        break;
                    case 1:
                        CreateFood(creatures.get(i).getPoint());
                        break;
                    case 2:
                        CreatePotion(creatures.get(i).getPoint(), currentLevel);
                        break;
                }
                break;
            case Goblin:
                switch (rand.nextInt(5)) {
                    default:
                    case 0:
                        int value = rand.nextInt(creatures.get(i).getMaxpHP() * 3) + 1;
                        CreateCoins(value, creatures.get(i).getPoint());
                        break;
                    case 1:
                        CreateWeapon(creatures.get(i).getPoint(), currentLevel);
                        break;
                    case 2:
                        CreateWearable(creatures.get(i).getPoint(), currentLevel);
                        break;
                    case 3:
                        CreateMiningTool(creatures.get(i).getPoint(), currentLevel);
//                            mining tool,
                        break;
                    case 4:
                        CreateRandomConsumable(creatures.get(i).getPoint(), currentLevel);
                        break;
                    case 5:
                        CreateLightSource(creatures.get(i).getPoint(), currentLevel);
//                            lights
                }
                break;
            case Minotaur:
                switch (rand.nextInt(7)) {
                    default:
                    case 0:
                        CreateRandomTreasure(100 + (currentLevel * 2), creatures.get(i).getPoint());
                        break;
                    case 1:
                        CreateWeapon(creatures.get(i).getPoint(), currentLevel + 5);
                        break;
                    case 2:
                        CreateWearable(creatures.get(i).getPoint(), currentLevel + 5);
                        break;
                    case 3:
                        CreateMiningTool(creatures.get(i).getPoint(), currentLevel + 5);
                        break;
                    case 4:
                        CreateRandomConsumable(creatures.get(i).getPoint(), currentLevel);
                        break;
                    case 5:
                        CreateLightSource(creatures.get(i).getPoint(), currentLevel + 5);
                        break;
                }
                break;
        }
    }

    public boolean findInPath(ArrayList<Point3d> path, Point target) {
        boolean isFound = false;
        for (int i = 0; i < path.size(); i++) {
            if (target.x == path.get(i).x && target.y == path.get(i).y) {
                isFound = true;
                break;
            }
        }
        return isFound;
    }
    public void UpdateEnemies(Point target, int camWidth, int camHeight, int camOffsetX, int camOffsetY, int currentLevel, boolean friendlyFire) {
        for (int i = 0; i < creatures.size(); i++) {
            if (creatures.get(i).getX() > camOffsetX &&
                    creatures.get(i).getY() > camOffsetY &&
                    creatures.get(i).getX() < camOffsetX + camWidth &&
                    creatures.get(i).getY() < camOffsetY + camHeight) {
                Creature temp = creatures.get(i);
                switch (temp.getCreatureType()) {
                    default:
                    case Slime:
                        break;
                    case Goblin:
                        moveGoblin(target, temp, currentLevel, friendlyFire);
                        break;
                    case Minotaur:
                        break;
                    case Humanoid:
                        break;
                }
            }
        }
    }

    private void moveGoblin(Point target, Creature temp, int currentLevel, boolean friendlyFire) {
        switch (temp.getDirectionType()) {
            case Still:
                break;
            case UpandDown:
                //get points above OR below current point.
                //if (point.y > otherpoint.y) {point.y = otherpoint.y} else if
                // (point.y = otherpoint.y) {point.y = point.y}
                break;
            case LeftandRight:
                //get points left OR right of current point.
                //if (point.y > otherpoint.y) {point.y = otherpoint.y} else if
                // (point.y = otherpoint.y) {point.y = point.y}
                break;
            case Random:
                MoveRandomly(temp, currentLevel, friendlyFire);
                break;
            case TowardsTargetDirectional:
                break;
            case TowardsTargetEfficient:
                int distance = 0;
                Point3d targetDest = new Point3d(target.x, target.y, distance);
                temp.getPath().add(targetDest);
                //Find all points next to target that can be walked on.
                //  append distance to point on creation of target.
                //  append this 3d point to Path.
                while (!findInPath(temp.getPath(), temp.getPoint())) {
                    distance++;
                    Point pointLeft = new Point(target.x - 1, target.y);
                    if (AddPossiblePathPoints(pointLeft, temp, distance, targetDest))
                        break;

                    Point pointRight = new Point(target.x + 1, target.y);
                    if (AddPossiblePathPoints(pointRight, temp, distance, targetDest))
                        break;

                    Point pointUp = new Point(target.x, target.y - 1);
                    if (AddPossiblePathPoints(pointUp, temp, distance, targetDest))
                        break;

                    Point pointDown = new Point(target.x, target.y + 1);
                    if (AddPossiblePathPoints(pointDown, temp, distance, targetDest))
                        break;
                }
                int debug = 0;
                //Iteratively find all points that can be walked on that are
                //  next to previous points,
                //  not already in Path,
                //  until getPoint() is reached.
                break;
        }
    }

    private void MoveRandomly(Creature temp, int currentLevel, boolean friendlyFire) {
        switch (rand.nextInt(5)) {
            default:
            case 0:
                if (getCellType(temp.getX(), temp.getY() + 1) == CellType.Space ||
                        harmObject(temp.getX(), temp.getY() + 1, temp, currentLevel, friendlyFire)) {
                    temp.setPoint(temp.getX(), temp.getY() + 1);
                    break;
                }
            case 1:
                if (getCellType(temp.getX(), temp.getY() - 1) == CellType.Space ||
                        harmObject(temp.getX(), temp.getY() - 1, temp, currentLevel, friendlyFire)) {
                    temp.setPoint(temp.getX(), temp.getY() - 1);
                    break;
                }
            case 2:
                if (getCellType(temp.getX() + 1, temp.getY()) == CellType.Space ||
                        harmObject(temp.getX() + 1, temp.getY(), temp, currentLevel, friendlyFire)) {
                    temp.setPoint(temp.getX() + 1, temp.getY());
                    break;
                }
            case 3:
                if (getCellType(temp.getX() - 1, temp.getY()) == CellType.Space ||
                        harmObject(temp.getX() - 1, temp.getY(), temp, currentLevel, friendlyFire)) {
                    temp.setPoint(temp.getX() - 1, temp.getY());
                    //CheckStairs
                    break;
                }
            case 4:
                break;
        }
    }

    private boolean AddPossiblePathPoints(Point point, Creature temp, int distance, Point3d targetDest) {
        if (super.IsCellOpen(point.x, point.y) && !findInPath(temp.getPath(), point)) {
            Point3d possiblePathLeft = new Point3d(point.x, point.y, distance);
            temp.getPath().add(possiblePathLeft);
            if (possiblePathLeft == targetDest) {
                return true;
            }
        }
        return false;
    }

    public CellType getCellType(int cellx, int celly) {
        CellType returnType = CellType.Wall;
        if (super.IsCellWall(cellx, celly)) {
            return CellType.Wall;
        }
        if (super.IsCellOpen(cellx, celly)) {
            returnType = CellType.Space;
        }
        for (int i = 0; i < clutter.size(); i++) {
            if (clutter.get(i).getPoint().x == cellx && clutter.get(i).getPoint().y == celly) {
                return CellType.Clutter;
            }
        }
        for (int i = 0; i < creatures.size(); i++) {
            Bitmap tempImage = creatures.get(i).getBitmap();
            if (creatures.get(i).getPoint().x == cellx && creatures.get(i).getPoint().y == celly) {
                if (tempImage == imageEnemy[0]) {
                    return CellType.Slime;
                } else if (tempImage == imageEnemy[1]) {
                    return CellType.Goblin;
                } else if (tempImage == imageEnemy[2]) {
                    return CellType.Minotaur;
                } else if (creatures.get(i).getCreatureType() == Creature.CreatureType.Humanoid){
                    return CellType.Humanoid;
                }
            }
        }
        for (int i = 0; i < food.size(); i++) {
            if (food.get(i).getPoint().x == cellx && food.get(i).getPoint().y == celly) {
                return CellType.Food;
            }
        }
        for (int i = 0; i < potions.size(); i++) {
            if (potions.get(i).getPoint().x == cellx && potions.get(i).getPoint().y == celly) {
                return CellType.Potion;
            }
        }
        for (int i = 0; i < lights.size(); i++) {
            if (lights.get(i).getPoint().x == cellx && lights.get(i).getPoint().y == celly) {
                return CellType.LightSource;
            }
        }
        for (int i = 0; i < miningTools.size(); i++) {
            if (miningTools.get(i).getPoint().x == cellx && miningTools.get(i).getPoint().y == celly) {
                return CellType.MiningTool;
            }
        }
        for (int i = 0; i < scrolls.size(); i++) {
            if (scrolls.get(i).getPoint().x == cellx && scrolls.get(i).getPoint().y == celly) {
                return CellType.Scroll;
            }
        }
        for (int i = 0; i < wearables.size(); i++) {
            if (wearables.get(i).getPoint().x == cellx && wearables.get(i).getPoint().y == celly) {
                return CellType.Wearable;
            }
        }
        for (int i = 0; i < weapons.size(); i++) {
            if (weapons.get(i).getPoint().x == cellx && weapons.get(i).getPoint().y == celly) {
                return CellType.Weapon;
            }
        }
        return returnType;
    }

    public boolean harmObject(int cellx, int celly, Creature harmer, int currentLevel, boolean friendlyFire) {
        boolean ifCreatureGetsMoved = false;
        CellType harmeeType = getCellType(cellx, celly);
        switch (harmeeType) {
            default:
            case Wall:
                if (cellx >= GetMapWidth() || cellx < 0 || celly >= GetMapHeight() || celly < 0) {
                    break;
                }
                harmWall(cellx, celly, harmer.getMining());
                if (GetCurrentMap()[celly][cellx].getHP() <= 0 && rand.nextInt(GetMapHeight() * GetMapWidth()) <= diamondPercent) {
                    Point tempPoint = new Point(cellx, celly);
                    CreateRandomDiamond(tempPoint);
                }
                break;
            case Space:
                break;
            case Clutter:
            case Barrel:
            case Chest:
                for (int i = 0; i < clutter.size(); i++) {
                    Clutter temp = clutter.get(i);
                    Bitmap tempImage = temp.getBitmap();
                    if (temp.getPoint().x == cellx && temp.getPoint().y == celly) {
                        temp.hurt(harmer.getAttack());
                        if (tempImage == imageClutter[3] ||
                                tempImage == imageClutter[4] ||
                                tempImage == imageClutter[5]) {
                            ifCreatureGetsMoved = true;
                            if (tempImage != imageClutter[5]) {
                                harmer.incrementScore(temp.getValue());
                            } else {
                                harmer.setMaxHP(harmer.getMaxpHP() + 1);
                            }
                            clutter.remove(i);
                        } else if (temp.getHP() <= 0) {
                            if (tempImage != imageClutter[0]) {
                                if (tempImage == imageClutter[1]) {
                                    CreateRandomDrop(i, CellType.Barrel, currentLevel);
                                } else if (tempImage == imageClutter[2]) {
                                    CreateRandomDrop(i, CellType.Chest, currentLevel);
                                }
                            }
                            clutter.remove(i);
                            break;
                        }
                    }
                }
                break;
            case Slime:
                if (harmer.getCreatureType() == Creature.CreatureType.Slime && !friendlyFire){
                    break;
                }
                HarmCreature(cellx, celly, harmer, currentLevel, CellType.Slime);
                break;
            case Goblin:
                if (harmer.getCreatureType() == Creature.CreatureType.Goblin && !friendlyFire){
                    break;
                }
                HarmCreature(cellx, celly, harmer, currentLevel, CellType.Goblin);
                break;
            case Minotaur:
                if (harmer.getCreatureType() == Creature.CreatureType.Minotaur && !friendlyFire){
                    break;
                }
                HarmCreature(cellx, celly, harmer, currentLevel, harmeeType);
                break;
            case Humanoid:
                if (harmer.getCreatureType() == Creature.CreatureType.Humanoid && !friendlyFire){
                    break;
                }
                HarmCreature(cellx, celly, harmer, currentLevel, harmeeType);
                break;

            case Weapon:
                for (int i = 0; i < weapons.size(); i++) {
                    if (weapons.get(i).getPoint().x == cellx && weapons.get(i).getPoint().y == celly) {
                        Weapon possibleDrop = harmer.setWeapon(weapons.get(i));
                        weapons.remove(i);
                        if (possibleDrop != null && possibleDrop.getBitmap() != imageWeapon[0]) {
                            //if it exists, we want to
                            //  give the weapon on the ground to the harmer (should have happened in setWeapon)
                            //  delete the weapon on the ground from weapons.
                            //  drop the swapped harmer weapon (possibleDrop)
                            possibleDrop.setPoint(cellx, celly);
                            weapons.add(possibleDrop);
                        }
                        //either way, we want to move the harmer to the new point.
                        ifCreatureGetsMoved = true;
                        break;
                    }
                }
                break;
            case MiningTool:
                for (int i = 0; i < miningTools.size(); i++) {
                    if (miningTools.get(i).getPoint().x == cellx && miningTools.get(i).getPoint().y == celly) {
                        MiningTool possibleDrop = harmer.setMiningTool(miningTools.get(i));
                        miningTools.remove(i);
                        if (possibleDrop != null) {
                            possibleDrop.setPoint(cellx, celly);
                            miningTools.add(possibleDrop);
                        }
                        ifCreatureGetsMoved = true;
                        break;
                    }
                }
                break;
            case LightSource:
                for (int i = 0; i < lights.size(); i++) {
                    if (lights.get(i).getPoint().x == cellx && lights.get(i).getPoint().y == celly) {
                        LightSource possibleDrop = harmer.setLightSource(lights.get(i));
                        lights.remove(i);
                        if (possibleDrop != null) {
                            possibleDrop.setPoint(cellx, celly);
                            lights.add(possibleDrop);
                        }
                        ifCreatureGetsMoved = true;
                        break;
                    }
                }
                break;
            case Wearable:
                for (int i = 0; i < wearables.size(); i++) {
                    if (wearables.get(i).getPoint().x == cellx &&
                            wearables.get(i).getPoint().y == celly) {
                        Wearable possibleDrop = harmer.setWearable(wearables.get(i));
                        wearables.remove(i);
                        if (possibleDrop != null) {
                            possibleDrop.setPoint(cellx, celly);
                            wearables.add(possibleDrop);
                        }
                        ifCreatureGetsMoved = true;
                        break;
                    }
                }
                break;

            case Food:
                for (int i = 0; i < food.size(); i++) {
                    if (food.get(i).getPoint().x == cellx && food.get(i).getPoint().y == celly) {
                        Food possibleDrop = harmer.setFood(food.get(i));
                        food.remove(i);
                        if (possibleDrop != null) {
                            possibleDrop.setPoint(cellx, celly);
                            food.add(possibleDrop);
                        }
                        ifCreatureGetsMoved = true;
                        break;
                    }
                }
                break;
            case Scroll:
                for (int i = 0; i < scrolls.size(); i++) {
                    if (scrolls.get(i).getPoint().x == cellx && scrolls.get(i).getPoint().y == celly) {
                        Clutter possibleDrop = harmer.setScroll(scrolls.get(i));
                        scrolls.remove(i);
                        if (possibleDrop != null) {
                            possibleDrop.setPoint(cellx, celly);
                            scrolls.add(possibleDrop);
                        }
                        ifCreatureGetsMoved = true;
                        break;
                    }
                }
                break;
            case Potion:
                for (int i = 0; i < potions.size(); i++) {
                    if (potions.get(i).getPoint().x == cellx && potions.get(i).getPoint().y == celly) {
                        Food possibleDrop = harmer.setPotion(potions.get(i));
                        potions.remove(i);
                        if (possibleDrop != null) {
                            possibleDrop.setPoint(cellx, celly);
                            potions.add(possibleDrop);
                        }
                        ifCreatureGetsMoved = true;
                        break;
                    }
                }
                break;
        }
        return ifCreatureGetsMoved;
    }

    private void HarmCreature(int cellx, int celly, Creature harmer, int currentLevel, CellType harmeeType) {
        for (int i = 0; i < creatures.size(); i++){
            if (creatures.get(i).getPoint().x == cellx && creatures.get(i).getPoint().y == celly) {
                int damagetotal = (int)(harmer.getAttack()*((100 - creatures.get(i).getDefense())/100.0f));
                creatures.get(i).hurt(damagetotal);
                if (creatures.get(i).getHP() <= 0) {
                    CreateRandomDrop(i, harmeeType, currentLevel);
                    creatures.remove(i);
                    break;
                }
            }
        }
    }
}
