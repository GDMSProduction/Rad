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
import static my.application.stephen.runattackdungeon.GameView.walls;

/**
 * Created by Stephen on 2018-01-25.
 */

public class Level extends Map {

    //the amount of desired rooms
    private int roomNums = 2;
    //The Array of available roomCenters
    private ArrayList<Map> AvailableRooms = new ArrayList<>(roomNums);
    //whether or not to make "rooms"
    private boolean makeRooms;
    private int roomHeightMax = 8;
    private int roomHeightMin = 4;
    private int roomWidthMax = 8;
    private int roomWidthMin = 4;
    private int roomSpacePercent = 100;
    private boolean roomNatural = false;

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
    private ObjectDestructible stairsUp;
    private ObjectDestructible stairsDown;
    //the creatures
    private int maxEnemies = 5;
    private ArrayList<Creature> creatures = new ArrayList<Creature>(maxEnemies);

    Level(int Width, int Height, int SpacesPercent, boolean natural) {
        super(Width, Height, SpacesPercent, natural);
        makeLevelPoints();
//        if (Width < Height) {
//            roomRadiusMax = Width / 8;
//            if (roomRadiusMax*2 > camWidth){
//                roomRadiusMax = camWidth/2;
//            }
//        } else {
//            roomRadiusMax = Height / 8;
//            if (roomRadiusMax*2 > camHeight){
//                roomRadiusMax = camHeight/2;
//            }
//        }
//        roomRadiusMin = roomRadiusMax / 4;
//        if (roomRadiusMin < 2) {
//            roomRadiusMin = 2;
//        }
//
//        makeRooms = !natural;
//        if (makeRooms) {
//            roomNums = rand.nextInt((
//                    (Width/(roomRadiusMax * 2 + 3)) *
//                            (Height / (roomRadiusMax * 2 + 3)))
//                    - 2)
//                    + 2;
//            if (roomNums <= 2){
//                roomNums = 2;
//            }
//        }
//
//        RoomCenters = new Point[roomNums];
//        AvailableRoomCenters = new ArrayList<Point>(Height * Width);
//        MakeAvailableRoomCenters();

//        MakeRooms();
//        MakeCorridors();
        int TotalSpaces = getNumEmptyPoints() - 2;
        createStairs();
        if (getStairsUp() != null && getStairsDown() != null) {
            MakeCorridor(getStairsUp().getPoint(), getStairsDown().getPoint());
        }
        maxEnemies = (int) (TotalSpaces * 2.0f / 18.0f);
        if (maxEnemies < 1) {
            maxEnemies = 1;
        }
        createEnemies();
        maxClutter = (int) (TotalSpaces * 1.0f / 18.0f);
        if (maxClutter < 1) {
            maxClutter = 1;
        }
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

//    private void MakeRooms() {
//        for (int i = 0; i < roomNums && AvailableRooms.size() > 0; i++) {
//            RoomCenters[i] = GetRandRoomCell();
//            int distributionX = rand.nextInt(roomRadiusMax) + roomRadiusMin;
//            int distributionY = rand.nextInt(roomRadiusMax) + roomRadiusMin;
//            //hollows out room
////            switch  (rand.nextInt(2)){
////                case 0:
//            for (int col = RoomCenters[i].x - distributionX; col < RoomCenters[i].x + distributionX; col++) {
//                for (int row = RoomCenters[i].y - distributionY; row < RoomCenters[i].y + distributionY; row++) {
//                    super.getCurrentMap()[row][col].setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
//                    Point temp = new Point(col, row);
//                    int pointIndex = FindAvailablePoint(temp);
//                    if (pointIndex >= 0) {
//                        AvailableRooms.remove(pointIndex);
//                    }
//                }
//            }
////                    break;
////                case 1:
////                    for (int col = RoomCenters[i].x - distributionX; col <= RoomCenters[i].x + distributionX; col++) {
////                        for (int row = RoomCenters[i].y - distributionY; row <= RoomCenters[i].y + distributionY; row++) {
////                            mCellsCurr[row][col].setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
////                            Point temp = new Point(col, row);
////                            int pointIndex = FindAvailablePoint(temp);
////                            if (pointIndex >= 0) {
////                                AvailableRoomCenters.remove(pointIndex);
////                            }
////                        }
////                    }
////                    break;
////            }
//            //makes room borders.
//            if (distributionX > 10 || distributionY > 10) {
//                //Make Room Stablizing Columns.
//            }
////            MakeBorders(RoomCenters[i].x - distributionX, RoomCenters[i].y - distributionY, distributionX, distributionY);
//        }
//    }

//    private int FindAvailablePoint(Point temp) {
//        int ret = -1;
//        for (int index = 0; index < AvailableRooms.size(); index++) {
//            if (AvailableRooms.get(index).x == temp.x &&
//                    AvailableRooms.get(index).y == temp.y) {
//                return index;
//            }
//        }
//        return ret;
//    }

    private Map GetRandRoom() {
        return AvailableRooms.get(rand.nextInt(AvailableRooms.size()));
    }

    private void MakeCorridor(Point start, Point end) {
        int lesserX = start.x;
        int greaterX = end.x;
        if (start.x > end.x) {
            lesserX = end.x;
            greaterX = start.x;
        }
        int lesserY = start.y;
        int greaterY = end.y;
        if (start.y > end.y) {
            lesserY = end.y;
            greaterY = start.y;
        }

        Point thingy = start;
        Point thingy2 = end;
        int corridorWidth = Math.abs(start.x - end.x);
        int corridorHeight = Math.abs(start.y - end.y);

        //Diagonals Quadrant 1 and 3, Equals
        if (start.x >= end.x && start.y <= end.y ||
                start.x <= end.x && start.y >= end.y) {

            switch (rand.nextInt(2)) {
                default:
                case 0:
                    //Top Left - Right
                    for (int j = 0; j <= corridorWidth; j++) {
                        super.getCurrentMap()[lesserY][lesserX + j].get(0).setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
                    }
                    //Top Left - Bottom
                    for (int k = 0; k <= corridorHeight; k++) {
                        super.getCurrentMap()[lesserY + k][lesserX].get(0).setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
                    }
                    break;
                case 1:
                    //Bottom Left - Right
                    for (int j = 0; j <= corridorWidth; j++) {
                        super.getCurrentMap()[greaterY][lesserX + j].get(0).setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
                    }
                    //Bottom Right - Top
                    for (int k = 0; k <= corridorHeight; k++) {
                        super.getCurrentMap()[lesserY + k][greaterX].get(0).setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
                    }
                    break;
            }
        } else if (start.x > end.x && start.y > end.y ||
                start.x < end.x && start.y < end.y) {
            switch (rand.nextInt(2)) {
                default:
                case 0:
                    for (int j = 0; j <= corridorWidth; j++) {
                        super.getCurrentMap()[lesserY][lesserX + j].get(0).setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
                    }
                    for (int k = 0; k <= corridorHeight; k++) {
                        super.getCurrentMap()[lesserY + k][greaterX].get(0).setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
                    }
                    break;
                case 1:
                    for (int j = 0; j <= corridorWidth; j++) {
                        super.getCurrentMap()[greaterY][lesserX + j].get(0).setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
                    }
                    for (int k = 0; k <= corridorHeight; k++) {
                        super.getCurrentMap()[lesserY + k][lesserX].get(0).setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
                    }
                    break;
            }
        }
    }

//    private void MakeAvailableRooms() {
//        for (int row = 3 + roomRadiusMax; row < super.getMapHeight() - 3 - roomRadiusMax; row++) {
//            for (int col = 3 + roomRadiusMax; col < super.getMapWidth() - 3 - roomRadiusMax; col++) {
//                Point temp = new Point(col, row);
//                AvailableRoomCenters.add(temp);
//            }
//        }
//    }

    public void addObjectToMap(Point point, ObjectDestructible object){
        getCurrentMap()[point.y][point.x].add(object);
    }
    public void removeObjectFromMap(Point point, ObjectDestructible object){
        for (int i = 0; i < getCurrentMap()[point.y][point.x].size(); i++){
            if (object == getCurrentMap()[point.y][point.x].get(i)){
                getCurrentMap()[point.y][point.x].remove(i);
                break;
            }
        }
    }

    private void createClutter() {
        clutter.clear();
        int newSize = rand.nextInt(maxClutter) + 1;
        clutter = new ArrayList<Clutter>(newSize);
        for (int i = 0; i < newSize; i++) {
            if (getNumEmptyPoints() > 0) {
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
                        temp.setCellType(ObjectDestructible.CellType.Clutter);
                        break;
                    case 1:
                        //barrel
                        temp.setBitMap(imageClutter[1]);
                        temp.setValue(10);
                        temp.setCellType(ObjectDestructible.CellType.Barrel);
                        break;
                    case 2:
                        //chest
                        temp.setBitMap(imageClutter[2]);
                        temp.setValue(30);
                        temp.setCellType(ObjectDestructible.CellType.Chest);
                        break;
                }
                giveNewPointToObject(temp);
                clutter.add(temp);
            }
        }
    }

    private void createEnemies() {
        //Create Enemies
        creatures.clear();
        int newSize = rand.nextInt(maxEnemies) + 1;
        creatures = new ArrayList<Creature>(newSize);
        for (int i = 0; i < newSize; i++) {
            if (getNumEmptyPoints() > 0) {
                int hPMax = 3;
                int defMax = 0;
                Bitmap enemyBitmap = imageEnemy[0];
                Point enemyPoint = new Point(0, 0);

                Creature temp;
                switch (rand.nextInt(4)) {
                    default:
                    case 0:
                        temp = new Creature(enemyPoint, enemyBitmap, hPMax, ObjectDestructible.CellType.Slime, defMax);
                        temp.setAttack(1000);
                        break;
                    case 1:
                        hPMax = 5;
                        defMax = 50;
                        enemyBitmap = imageEnemy[1];
                        temp = new Creature(enemyPoint, enemyBitmap, hPMax, ObjectDestructible.CellType.Goblin, defMax);
                        temp.setAttack(1);
                        break;
                }

                giveNewPointToObject(temp);
                creatures.add(temp);
            }
        }
    }

    private void createStairs() {
        //DOWN STAIRS
        Point tempPoint;
        if (getNumEmptyPoints() > 0) {
            tempPoint = new Point(0, 0);
            stairsDown = new ObjectDestructible(tempPoint,
                    imageStairs[0],
                    1000,
                    ObjectDestructible.CellType.StairDown);
            giveNewPointToObject(stairsDown);
        }
        //UP STAIRS
        if (getNumEmptyPoints() > 0) {
            tempPoint = new Point(0, 0);
            stairsUp = new ObjectDestructible(tempPoint,
                    imageStairs[1],
                    1000,
                    ObjectDestructible.CellType.StairUp);
            giveNewPointToObject(stairsUp);
        }
    }


    private void CreateCoins(int value, Point point) {
        Clutter coins = new Clutter(value, point, imageClutter[3], 0);
        coins.setCellType(ObjectDestructible.CellType.Clutter);
        clutter.add(coins);
        addObjectToMap(point, coins);
    }

    private void CreateHeartDiamond(Point point) {
        Clutter diamondRed = new Clutter(200, point, imageClutter[5], 0);
        diamondRed.setCellType(ObjectDestructible.CellType.Clutter);
        clutter.add(diamondRed);
        addObjectToMap(point, diamondRed);
    }

    private void CreateWhiteDiamond(Point point) {
        Clutter diamond = new Clutter(200, point, imageClutter[4], 0);
        diamond.setCellType(ObjectDestructible.CellType.Clutter);
        clutter.add(diamond);
        addObjectToMap(point, diamond);
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
        fud.setCellType(ObjectDestructible.CellType.Food);
        food.add(fud);
        addObjectToMap(point, fud);
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
                potion.setHealing(currentLevel / 2);
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
                potion.setHealing(currentLevel / 5);
                break;
        }
        potion.setCellType(ObjectDestructible.CellType.Potion);
        potions.add(potion);
        addObjectToMap(point, potion);
    }

    private void CreateScroll(Point point, int currentLevel) {
        Clutter scroll = new Clutter(currentLevel, point, imageScroll[0], 2);
        scroll.setCellType(ObjectDestructible.CellType.Scroll);
        scrolls.add(scroll);
        addObjectToMap(point, scroll);
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
                CreateScroll(point, currentLevel);
        }
    }

    private void CreateWeapon(Point point, int currentLevel) {
        int Attack;
        switch (rand.nextInt(4)) {
            default:
            case 0:
                Attack = 2;
                Weapon dagger = new Weapon(Attack, currentLevel, Attack * currentLevel, point, imageWeapon[1], 10);
                dagger.setCellType(ObjectDestructible.CellType.Weapon);
                weapons.add(dagger);
                addObjectToMap(point, dagger);
                break;
            case 1:
                Attack = 4;
                Weapon sword = new Weapon(Attack, (int) (currentLevel / 2.0f), Attack * currentLevel, point, imageWeapon[2], 15);
                sword.setCellType(ObjectDestructible.CellType.Weapon);
                weapons.add(sword);
                addObjectToMap(point, sword);
                break;
            case 2:
                Attack = 6;
                Weapon axe = new Weapon(Attack, (int) (currentLevel / 4.0f), Attack * currentLevel, point, imageWeapon[3], 10);
                axe.setCellType(ObjectDestructible.CellType.Weapon);
                weapons.add(axe);
                addObjectToMap(point, axe);
                break;
            case 3:
                Attack = 12;
                Weapon bow = new Weapon(Attack, 0, (int) (50 - currentLevel / 2.0f), point, imageWeapon[4], 8);
                bow.setCellType(ObjectDestructible.CellType.Weapon);
                weapons.add(bow);
                addObjectToMap(point, bow);
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
                shield.setCellType(ObjectDestructible.CellType.Wearable);
                wearables.add(shield);
                addObjectToMap(point, shield);
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
                silverRing.setCellType(ObjectDestructible.CellType.Wearable);
                wearables.add(silverRing);
                addObjectToMap(point,silverRing);
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
                goldRing.setCellType(ObjectDestructible.CellType.Wearable);
                wearables.add(goldRing);
                addObjectToMap(point, goldRing);
                break;
        }
    }

    private void CreateMiningTool(Point point, int currentLevel) {
        switch (rand.nextInt(2)) {
            default:
            case 0:
                MiningTool shovel = new MiningTool(1, currentLevel, point, imageMining[0], 10);
                shovel.setCellType(ObjectDestructible.CellType.MiningTool);
                miningTools.add(shovel);
                addObjectToMap(point, shovel);
                break;
            case 1:
                MiningTool pickaxe = new MiningTool(3, 2 * (currentLevel + 1), point, imageMining[1], 100);
                pickaxe.setCellType(ObjectDestructible.CellType.MiningTool);
                miningTools.add(pickaxe);
                addObjectToMap(point, pickaxe);
                break;
        }
    }

    private void CreateLightSource(Point point, int currentLevel) {
        switch (rand.nextInt(2)) {
            default:
            case 0:
                LightSource torch = new LightSource(5, 2, 0, point, imageLight[0], 500 + currentLevel);
                torch.setCellType(ObjectDestructible.CellType.LightSource);
                lights.add(torch);
                addObjectToMap(point, torch);
                break;
            case 1:
                LightSource lantern = new LightSource(5, 4, currentLevel + 10, point, imageLight[1], 50 * currentLevel);
                lantern.setCellType(ObjectDestructible.CellType.LightSource);
                lights.add(lantern);
                addObjectToMap(point, lantern);
                break;
        }
    }

    private void CreateRandomDrop(int i, ObjectDestructible.CellType type, int currentLevel) {
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

    public void UpdateEnemies(Dungeon dungeon, Point target, int camWidth, int camHeight, int camOffsetX, int camOffsetY, int currentLevel, boolean friendlyFire) {
        for (int i = 0; i < creatures.size(); i++) {
            if (creatures.get(i).getX() > camOffsetX &&
                    creatures.get(i).getY() > camOffsetY &&
                    creatures.get(i).getX() < camOffsetX + camWidth &&
                    creatures.get(i).getY() < camOffsetY + camHeight) {
                Creature temp = creatures.get(i);
                switch (temp.getCellType()) {
                    default:
                    case Slime:
                        break;
                    case Goblin:
                        //moveCreature(dungeon, target, temp, currentLevel, friendlyFire);
                        break;
                    case Minotaur:
                        break;
                    case Humanoid:
                        break;
                }
            }
        }
    }

    private void moveCreature(Dungeon dungeon, Point target, Creature temp, int currentLevel, boolean friendlyFire) {
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
                MoveRandomly(dungeon, temp);
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
                //int debug = 0;
                //Iteratively find all points that can be walked on that are
                //  next to previous points,
                //  not already in Path,
                //  until getPoint() is reached.
                break;
        }
    }

    private void MoveRandomly(Dungeon dungeon, Creature temp) {
        switch (rand.nextInt(5)) {
            //South
            case 0:
                MoveCreatureVertical(dungeon, temp, temp.getCurrentDepth(),temp.getY() + 1);
                break;
                //North
            case 1:
                MoveCreatureVertical(dungeon, temp, temp.getCurrentDepth(),temp.getY() - 1);
                break;
                //West
            case 2:
                MoveCreatureHorizontal(dungeon, temp, temp.getCurrentDepth(),temp.getX() + 1);
                break;
                //East
            case 3:
                MoveCreatureHorizontal(dungeon, temp, temp.getCurrentDepth(),temp.getX() - 1);
                break;
                //Stay
            default:
            case 4:
                break;
        }
    }
    public void MoveCreatureHorizontal(Dungeon dungeon, Creature creature, int currentLevel, int X) {
        if (dungeon.getDungeonLevels().get(currentLevel).interactWithObject(
                dungeon,
                X,
                creature.getY(),
                creature,
                creature.getCurrentDepth(),
                dungeon.getFriendlyFire() ) ) {
            removeObjectFromMap(creature.getPoint(), creature);
            creature.setX(X);
            addObjectToMap(creature.getPoint(), creature);
        }
    }

    public void MoveCreatureVertical(Dungeon dungeon, Creature creature, int currentLevel, int Y) {
        if (dungeon.getDungeonLevels().get(currentLevel).interactWithObject(
                dungeon,
                creature.getX(),
                Y,
                creature,
                creature.getCurrentDepth(),
                dungeon.getFriendlyFire() ) ) {
            removeObjectFromMap(creature.getPoint(), creature);
            creature.setY(Y);
            addObjectToMap(creature.getPoint(), creature);
        }
    }

    private boolean AddPossiblePathPoints(Point point, Creature temp, int distance, Point3d targetDest) {
        if (super.isCellOpen(point.x, point.y) && !findInPath(temp.getPath(), point)) {
            Point3d possiblePathLeft = new Point3d(point.x, point.y, distance);
            temp.getPath().add(possiblePathLeft);
            if (possiblePathLeft == targetDest) {
                return true;
            }
        }
        return false;
    }

    public ObjectDestructible.CellType getOtherCellType(int cellx, int celly){
        if (cellx >= getMapWidth() || cellx < 0 || celly >= getMapHeight() || celly < 0) {
            return ObjectDestructible.CellType.Wall;
        }
        return super.getCurrentMap()[celly][cellx].get(
                super.getCurrentMap()[celly][cellx].size() - 1
        ).getCellType();
    }

//    public ObjectDestructible.CellType getCellType(int cellx, int celly) {
//        ObjectDestructible.CellType returnType = ObjectDestructible.CellType.Wall;
//        if (super.isCellWall(cellx, celly)) {
//            return ObjectDestructible.CellType.Wall;
//        }
//        if (super.isCellOpen(cellx, celly)) {
//            returnType = ObjectDestructible.CellType.Space;
//        }
//        if (getStairsDown() != null) {
//            if (getStairsDown().getPoint().x == cellx &&
//                    getStairsDown().getPoint().y == celly) {
//                return ObjectDestructible.CellType.StairDown;
//            }
//        }
//        if (getStairsUp() != null) {
//            if (getStairsUp().getPoint().x == cellx &&
//                    getStairsUp().getPoint().y == celly) {
//                return ObjectDestructible.CellType.StairUp;
//            }
//        }
//        if (clutter != null) {
//            for (int i = 0; i < clutter.size(); i++) {
//                if (clutter.get(i).getPoint().x == cellx && clutter.get(i).getPoint().y == celly) {
//                    return ObjectDestructible.CellType.Clutter;
//                }
//            }
//        }
//        if (creatures != null) {
//            for (int i = 0; i < creatures.size(); i++) {
//                if (creatures.get(i).getPoint().x == cellx && creatures.get(i).getPoint().y == celly) {
//                    return creatures.get(i).getCellType();
//                }
//            }
//        }
//        if (food != null) {
//            for (int i = 0; i < food.size(); i++) {
//                if (food.get(i).getPoint().x == cellx && food.get(i).getPoint().y == celly) {
//                    return ObjectDestructible.CellType.Food;
//                }
//            }
//        }
//        if (potions != null) {
//            for (int i = 0; i < potions.size(); i++) {
//                if (potions.get(i).getPoint().x == cellx && potions.get(i).getPoint().y == celly) {
//                    return ObjectDestructible.CellType.Potion;
//                }
//            }
//        }
//        if (lights != null) {
//            for (int i = 0; i < lights.size(); i++) {
//                if (lights.get(i).getPoint().x == cellx && lights.get(i).getPoint().y == celly) {
//                    return ObjectDestructible.CellType.LightSource;
//                }
//            }
//        }
//        if (miningTools != null) {
//            for (int i = 0; i < miningTools.size(); i++) {
//                if (miningTools.get(i).getPoint().x == cellx && miningTools.get(i).getPoint().y == celly) {
//                    return ObjectDestructible.CellType.MiningTool;
//                }
//            }
//        }
//        if (scrolls != null) {
//            for (int i = 0; i < scrolls.size(); i++) {
//                if (scrolls.get(i).getPoint().x == cellx && scrolls.get(i).getPoint().y == celly) {
//                    return ObjectDestructible.CellType.Scroll;
//                }
//            }
//        }
//        if (wearables != null) {
//            for (int i = 0; i < wearables.size(); i++) {
//                if (wearables.get(i).getPoint().x == cellx && wearables.get(i).getPoint().y == celly) {
//                    return ObjectDestructible.CellType.Wearable;
//                }
//            }
//        }
//        if (weapons != null) {
//            for (int i = 0; i < weapons.size(); i++) {
//                if (weapons.get(i).getPoint().x == cellx && weapons.get(i).getPoint().y == celly) {
//                    return ObjectDestructible.CellType.Weapon;
//                }
//            }
//        }
//        return returnType;
//    }

    public boolean interactWithObject(Dungeon dungeon, int cellx, int celly, Creature harmer, int currentLevel, boolean friendlyFire) {
        boolean ifCreatureGetsMoved = false;
        ObjectDestructible.CellType harmeeType = getOtherCellType(cellx, celly);
        switch (harmeeType) {
            default:
            case Wall:
                if (cellx >= getMapWidth() || cellx < 0 || celly >= getMapHeight() || celly < 0) {
                    break;
                }
                harmWall(cellx, celly, harmer.getMining());
                if (getCurrentMap()[celly][cellx].get(0).getHP() <= 0 && rand.nextInt(getMapHeight() * getMapWidth()) <= diamondPercent) {
                    switch (rand.nextInt(2)) {
//                        case 0:
//                            super.setEmptySpace(celly, cellx);
//                            break;
                        default:
                        case 1:
                            Point tempPoint = new Point(cellx, celly);
                            CreateRandomDiamond(tempPoint);
                            break;
                    }
                }
                break;
            case Space:
                ifCreatureGetsMoved = true;
                break;
            case StairUp:
                dungeon.goToLevel(harmer, harmer.getCurrentDepth() - 1, Dungeon.DirectionToGo.UP);
                break;
            case StairDown:
                dungeon.goToLevel(harmer, harmer.getCurrentDepth() + 1, Dungeon.DirectionToGo.DOWN);
                break;
            case Clutter:
            case Barrel:
            case Chest:
                for (int i = 0; i < clutter.size(); i++) {
                    Clutter temp = clutter.get(i);
                    Bitmap tempImage = temp.getBitmap();
                    if (temp.getPoint().x == cellx && temp.getPoint().y == celly) {
                        temp.hurt(harmer.getAttack());
                        if (tempImage == imageClutter[3] || // coins
                                tempImage == imageClutter[4] || // white diamond
                                tempImage == imageClutter[5]) { // red diamond
                            ifCreatureGetsMoved = true;
                            if (tempImage != imageClutter[5]) {
                                harmer.incrementScore(temp.getValue());
                            } else {
                                harmer.setMaxHP(harmer.getMaxpHP() + 1);
                            }
                            removeObjectFromMap(temp.getPoint(), temp);
                            clutter.remove(i);
                        } else if (temp.getHP() <= 0) {
                            if (tempImage != imageClutter[0]) {
                                if (tempImage == imageClutter[1]) {
                                    CreateRandomDrop(i, ObjectDestructible.CellType.Barrel, currentLevel);
                                } else if (tempImage == imageClutter[2]) {
                                    CreateRandomDrop(i, ObjectDestructible.CellType.Chest, currentLevel);
                                }
                            }
                            removeObjectFromMap(temp.getPoint(), temp);
                            clutter.remove(i);
                            break;
                        }
                    }
                }
                break;
            case Slime:
                if (harmer.getCellType() == ObjectDestructible.CellType.Slime && !friendlyFire) {
                    break;
                }
                HarmCreature(cellx, celly, harmer, currentLevel, ObjectDestructible.CellType.Slime);
                break;
            case Goblin:
                if (harmer.getCellType() == ObjectDestructible.CellType.Goblin && !friendlyFire) {
                    break;
                }
                HarmCreature(cellx, celly, harmer, currentLevel, ObjectDestructible.CellType.Goblin);
                break;
            case Minotaur:
                if (harmer.getCellType() == ObjectDestructible.CellType.Minotaur && !friendlyFire) {
                    break;
                }
                HarmCreature(cellx, celly, harmer, currentLevel, harmeeType);
                break;
            case Humanoid:
                if (harmer.getCellType() == ObjectDestructible.CellType.Humanoid && !friendlyFire) {
                    break;
                }
                HarmCreature(cellx, celly, harmer, currentLevel, harmeeType);
                break;

            case Weapon:
                for (int i = 0; i < weapons.size(); i++) {
                    if (weapons.get(i).getPoint().x == cellx && weapons.get(i).getPoint().y == celly) {
                        Weapon possibleDrop = harmer.setWeapon(weapons.get(i));
                        removeObjectFromMap(weapons.get(i).getPoint(), weapons.get(i));
                        weapons.remove(i);
                        if (possibleDrop != null && possibleDrop.getBitmap() != imageWeapon[0]) {
                            //if it exists, we want to
                            //  give the weapon on the ground to the harmer (should have happened in setWeapon)
                            //  delete the weapon on the ground from weapons.
                            //  drop the swapped harmer weapon (possibleDrop)
                            possibleDrop.setPoint(cellx, celly);
                            weapons.add(possibleDrop);
                            addObjectToMap(possibleDrop.getPoint(), possibleDrop);
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
                        removeObjectFromMap(miningTools.get(i).getPoint(), miningTools.get(i));
                        miningTools.remove(i);
                        if (possibleDrop != null) {
                            possibleDrop.setPoint(cellx, celly);
                            miningTools.add(possibleDrop);
                            addObjectToMap(possibleDrop.getPoint(), possibleDrop);
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
                        removeObjectFromMap(lights.get(i).getPoint(), lights.get(i));
                        lights.remove(i);
                        if (possibleDrop != null) {
                            possibleDrop.setPoint(cellx, celly);
                            lights.add(possibleDrop);
                            addObjectToMap(possibleDrop.getPoint(), possibleDrop);
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
                        removeObjectFromMap(wearables.get(i).getPoint(), wearables.get(i));
                        wearables.remove(i);
                        if (possibleDrop != null) {
                            possibleDrop.setPoint(cellx, celly);
                            wearables.add(possibleDrop);
                            addObjectToMap(possibleDrop.getPoint(), possibleDrop);
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
                        removeObjectFromMap(food.get(i).getPoint(), food.get(i));
                        food.remove(i);
                        if (possibleDrop != null) {
                            possibleDrop.setPoint(cellx, celly);
                            food.add(possibleDrop);
                            addObjectToMap(possibleDrop.getPoint(), possibleDrop);
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
                        removeObjectFromMap(scrolls.get(i).getPoint(), scrolls.get(i));
                        scrolls.remove(i);
                        if (possibleDrop != null) {
                            possibleDrop.setPoint(cellx, celly);
                            scrolls.add(possibleDrop);
                            addObjectToMap(possibleDrop.getPoint(), possibleDrop);
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
                        removeObjectFromMap(potions.get(i).getPoint(), potions.get(i));
                        potions.remove(i);
                        if (possibleDrop != null) {
                            possibleDrop.setPoint(cellx, celly);
                            potions.add(possibleDrop);
                            addObjectToMap(possibleDrop.getPoint(), possibleDrop);
                        }
                        ifCreatureGetsMoved = true;
                        break;
                    }
                }
                break;
        }
        return ifCreatureGetsMoved;
    }

    public ArrayList<Point> makeLevelPoints(){
        numEmptyCells = 0;
        FloorTiles = new ArrayList<Point>();
        for (int row = 0; row < getMapHeight(); row++) {
            for (int col = 0; col < getMapWidth(); col++) {
                if (getOtherCellType(col, row) == ObjectDestructible.CellType.Space){
                    Point temp = new Point(col, row);
                    FloorTiles.add(temp);
                    numEmptyCells++;
                }
            }
        }
        return FloorTiles;
    }

    public ArrayList<Point> getLevelPoints(){return FloorTiles;}

    private void HarmCreature(int cellx, int celly, Creature harmer, int currentLevel, ObjectDestructible.CellType harmeeType) {
        for (int i = 0; i < creatures.size(); i++) {
            if (creatures.get(i).getPoint().x == cellx && creatures.get(i).getPoint().y == celly) {
                int damagetotal = (int) (harmer.getAttack() * ((100 - creatures.get(i).getDefense()) / 100.0f));
                creatures.get(i).hurt(damagetotal);
                if (creatures.get(i).getHP() <= 0) {
                    CreateRandomDrop(i, harmeeType, currentLevel);
                    removeObjectFromMap(creatures.get(i).getPoint(), creatures.get(i));
                    creatures.remove(i);
                    break;
                }
            }
        }
    }

    public void harmWall(int cellx, int celly, int mining) {
        super.getCurrentMap()[celly][cellx].get(0).hurt(mining);
        if (super.getCurrentMap()[celly][cellx].get(0).getBitmap() != walls[0]) {
            super.getCurrentMap()[celly][cellx].get(0).setBitMap(walls[0]);
        }
        if (super.getCurrentMap()[celly][cellx].get(0).getHP() <= 0) {
            super.setSpace(super.getCurrentMap(), celly, cellx, spaces.length - 2);
        }
    }

    public void giveNewPointToObject(ObjectDestructible object) {
        int floorTilesIndex = rand.nextInt(FloorTiles.size());
        Point newPoint = FloorTiles.get(floorTilesIndex);

        removeObjectFromMap(object.getPoint(), object);
        object.setPoint(newPoint);
        addObjectToMap(object.getPoint(), object);

        FloorTiles.remove(floorTilesIndex);
    }
}
