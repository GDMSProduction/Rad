package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import static my.application.stephen.runattackdungeon.GameView.camHeight;
import static my.application.stephen.runattackdungeon.GameView.camWidth;
import static my.application.stephen.runattackdungeon.GameView.imageClutter;
import static my.application.stephen.runattackdungeon.GameView.imageEnemy;
import static my.application.stephen.runattackdungeon.GameView.imageFood;
import static my.application.stephen.runattackdungeon.GameView.imageLight;
import static my.application.stephen.runattackdungeon.GameView.imageMining;
import static my.application.stephen.runattackdungeon.GameView.imageNPCDown;
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

    private int roomNums = 2;
    private ArrayList<Room> LevelRooms = new ArrayList<>(roomNums);
    private ArrayList<Point> RoomStartPoints = new ArrayList<>(0);
    private boolean makeRooms = false;
    private int roomHeightMax = 8;
    public static final int roomHeightMin = 4;
    private int roomWidthMax = 8;
    public static final int roomWidthMin = 4;
    private int roomSpacePercent = 100;
    private boolean roomNatural = false;
    //the minimum number of empty cells
    // 1 UP stair,
    // 1 DOWN stair,
    // 1 Enemy,
    // 1 Clutter,
    // 1 Player.
    private int minimumEmptyCells = 5;

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
    private ObjectDestructible stairsUp = null;
    private ObjectDestructible stairsDown = null;
    //the Creatures
    private int maxEnemies = 5;
    private ArrayList<Creature> levelCreatures = new ArrayList<Creature>(maxEnemies);

    Level(int Width, int Height, int SpacesPercent, boolean natural, boolean MakeRooms, int currentLevel) {
        super(Width, Height, SpacesPercent, natural);
        makeRooms = MakeRooms;
        PutRoomsInMap(Width, Height, currentLevel);
//            ConnectRooms();
        makeAvailablePoints(getMapHeight(), getMapWidth());
        if (numEmptyCells < minimumEmptyCells) {
            for (int i = 0; i < minimumEmptyCells; i++) {
                setSpace(getCurrentMap(), rand.nextInt(getMapHeight() - 3) + 1, rand.nextInt(getMapWidth() - 3) + 1, spaces.length - 1);
            }
            makeAvailablePoints(getMapHeight(), getMapWidth());
        }

        createStairs(currentLevel);
        if (getStairsUp() != null && getStairsDown() != null) {
            MakeCorridor(getStairsUp().getPoint(), getStairsDown().getPoint());
        }

        int TotalSpaces = getNumEmptyCells() - 2;
        maxEnemies = (int) (TotalSpaces * 0.2f);
        if (maxEnemies < 1) {
            maxEnemies = 1;
        }
        createEnemies(null);
        if (natural) {
            maxClutter = (int) (TotalSpaces * 0.2f);
            if (maxClutter < 1) {
                maxClutter = 1;
            }
            createClutter();
        }

    }

    //Getters
    public ObjectBase getStairsUp() {
        return stairsUp;
    }

    public ObjectBase getStairsDown() {
        return stairsDown;
    }

    public ArrayList<Creature> getLevelCreatures() {
        return levelCreatures;
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

    private void setRoomAmount(int Width, int Height) {
        if (makeRooms) {
            roomNums = rand.nextInt((Width / roomWidthMax) * (Height / roomHeightMax));
            if (roomNums < 2) {
                roomNums = 2;
            }
        }
    }

    private void setRoomDimensionsMax(int Width, int Height) {
        if (Width < Height) {
            roomWidthMax = roomHeightMax = Width / 2;
            if (roomWidthMax * 2 > camWidth) {
                roomWidthMax = roomHeightMax = camWidth;
            }
            if (roomWidthMax <= roomWidthMin) {
                roomWidthMax = roomHeightMax = roomWidthMin + 1;
            }
        } else {
            roomHeightMax = roomWidthMax = Height / 2;
            if (roomHeightMax * 2 > camHeight) {
                roomHeightMax = roomWidthMax = camHeight;
            }
            if (roomHeightMax <= roomHeightMin) {
                roomHeightMax = roomWidthMax = roomHeightMin + 1;
            }
        }
    }

    private void PutRoomsInMap(int Width, int Height, int currentLevel) {
        setRoomDimensionsMax(Width, Height);
        setRoomAmount(Width, Height);

        LevelRooms = new ArrayList<Room>(roomNums);
        makeRoomStartPoints();

        for (int i = 0; i < roomNums; i++) {
            int roomWidth = rand.nextInt(roomWidthMax - roomWidthMin) + roomWidthMin;
            int roomHeight = rand.nextInt(roomHeightMax - roomHeightMin) + roomHeightMin;

            Room newRoom = new Room(roomWidth, roomHeight, 100, false, maxClutter, maxEnemies);
            LevelRooms.add(newRoom);

            if (RoomStartPoints.size() <= 0) {
                break;
            }
            int startPointIndex = rand.nextInt(RoomStartPoints.size());
            newRoom.setStartPoint(RoomStartPoints.get(startPointIndex));
            int startX = RoomStartPoints.get(startPointIndex).x;
            int startY = RoomStartPoints.get(startPointIndex).y;

            for (int col = startX; col < startX + roomWidth; col++) {
                for (int row = startY; row < startY + roomHeight; row++) {
                    getCurrentMap()[row][col] = newRoom.getCurrentMap()[row - startY][col - startX];
                }
            }
            int debug = RoomStartPoints.size();
            for (int col = startX - roomWidthMax; col < startX + roomWidth; col++) {
                for (int row = startY - roomHeightMax; row < startY + roomHeight; row++) {
                    for (int roomStartPointIndex = 0; roomStartPointIndex < RoomStartPoints.size(); roomStartPointIndex++) {
                        if (RoomStartPoints.get(roomStartPointIndex).x == col && RoomStartPoints.get(roomStartPointIndex).y == row) {
                            RoomStartPoints.remove(roomStartPointIndex);
                        }
                    }
                }
            }
            debug = RoomStartPoints.size();
        }
//        for (int i = 0; i < LevelRooms.size(); i++){
//            createClutter(LevelRooms.get(i));
//            createEnemies(LevelRooms.get(i));
////        }
//        if (currentLevel % 25 == 0 && currentLevel != 0){
//            createMinotaur(LevelRooms.get(0));
//        }
        //this is where the for loop ends.
    }

    private void makeRoomStartPoints() {
        int maxRoomStartHeight = super.getMapHeight() - roomHeightMax;
        int maxRoomStartWidth = super.getMapWidth() - roomWidthMax;
        for (int row = 0; row < maxRoomStartHeight; row++) {
            for (int col = 0; col < maxRoomStartWidth; col++) {
                Point temp = new Point(col, row);
                RoomStartPoints.add(temp);
            }
        }
    }

    private void MakeCorridor(Point start, Point end) {
        Point start1 = start;
        Point end1 = end;
        int corridorWidth = (start.x - end.x);
        int corridorHeight = (start.y - end.y);

        while (corridorHeight != 0 || corridorWidth != 0) {
            int direction = rand.nextInt(2);
            if (corridorWidth == 0) {
                direction = 1;
            } else if (corridorHeight == 0) {
                direction = 0;
            }
            switch (direction) {
                case 0: //Horizontal
                    if (corridorWidth > 0) {
                        end1.x++;
                        corridorWidth--;
                        super.setSpace(super.getCurrentMap(), end1.y, end1.x, 4);
                    } else if (corridorWidth < 0) {
                        end1.x--;
                        corridorWidth++;
                        super.setSpace(super.getCurrentMap(), end1.y, end1.x, 4);
                    }
                    break;
                case 1: //Vertical
                    if (corridorHeight > 0) {
                        end1.y++;
                        corridorHeight--;
                        super.setSpace(super.getCurrentMap(), end1.y, end1.x, 4);
                    } else if (corridorHeight < 0) {
                        end1.y--;
                        corridorHeight++;
                        super.setSpace(super.getCurrentMap(), end1.y, end1.x, 4);
                    }
                    break;
            }
        }

//        int lesserX = start.x;
//        int greaterX = end.x;
//        if (start.x > end.x) {
//            lesserX = end.x;
//            greaterX = start.x;
//        }
//        int lesserY = start.y;
//        int greaterY = end.y;
//        if (start.y > end.y) {
//            lesserY = end.y;
//            greaterY = start.y;
//        }
//        int corridorWidth = Math.abs(start.x - end.x);
//        int corridorHeight = Math.abs(start.y - end.y);
//        //Diagonals Quadrants 1 and 3, Horizontal/Vertical
//        if (start.x >= end.x && start.y <= end.y ||
//                start.x <= end.x && start.y >= end.y) {
//            switch (rand.nextInt(2)) {
//                default:
//                case 0:
//                    //Top Left - Right
//                    for (int j = 0; j <= corridorWidth; j++) {
//                        super.setSpace(getCurrentMap(), lesserY, lesserX + j, 4);
////                        super.getCurrentMap()[lesserY][lesserX + j].get(0).setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
//                    }
//                    //Top Left - Bottom
//                    for (int k = 0; k <= corridorHeight; k++) {
//                        setSpace(getCurrentMap(), lesserY + k, lesserX, 4);
////                        super.getCurrentMap()[lesserY + k][lesserX].get(0).setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
//                    }
//                    break;
//                case 1:
//                    //Bottom Left - Right
//                    for (int j = 0; j <= corridorWidth; j++) {
//                        setSpace(getCurrentMap(), greaterY, lesserX + j, 4);
////                        super.getCurrentMap()[greaterY][lesserX + j].get(0).setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
//                    }
//                    //Bottom Right - Top
//                    for (int k = 0; k <= corridorHeight; k++) {
//                        setSpace(getCurrentMap(), lesserY + k, greaterX, 4);
////                        super.getCurrentMap()[lesserY + k][greaterX].get(0).setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
//                    }
//                    break;
//            }
//        }
//        //Diagonals Quadrants 2 and 4
//        else if (start.x > end.x && start.y > end.y ||
//                start.x < end.x && start.y < end.y) {
//            switch (rand.nextInt(2)) {
//                default:
//                case 0:
//                    for (int j = 0; j <= corridorWidth; j++) {
//                        setSpace(getCurrentMap(), lesserY, lesserX + j, 4);
////                        super.getCurrentMap()[lesserY][lesserX + j].get(0).setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
//                    }
//                    for (int k = 0; k <= corridorHeight; k++) {
//                        setSpace(getCurrentMap(), lesserY + k, greaterX, 4);
////                        super.getCurrentMap()[lesserY + k][greaterX].get(0).setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
//                    }
//                    break;
//                case 1:
//                    for (int j = 0; j <= corridorWidth; j++) {
//                        setSpace(getCurrentMap(), greaterY, lesserX + j, 4);
////                        super.getCurrentMap()[greaterY][lesserX + j].get(0).setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
//                    }
//                    for (int k = 0; k <= corridorHeight; k++) {
//                        setSpace(getCurrentMap(), lesserY + k, lesserX, 4);
////                        super.getCurrentMap()[lesserY + k][lesserX].get(0).setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
//                    }
//                    break;
//            }
//        }

    }

    protected void createClutter() {
//        clutter.clear();
        int newSize = rand.nextInt(maxClutter) + 1;
//        clutter = new ArrayList<Clutter>(newSize);
        for (int i = 0; i < newSize; i++) {
            if (getNumEmptyCells() > 0) {

                switch (rand.nextInt(5)) {
                    default:
                    case 0:
                        //rock
                        Clutter rock = new Clutter(0, 0, new Point(0, 0), imageClutter[0], 1, ObjectDestructible.CellType.Clutter);
                        giveNewPointToObject(rock);
                        clutter.add(rock);
                        break;
                    case 1:
                        //barrel
                        Clutter barrel = new Clutter(10, 0, new Point(0, 0), imageClutter[1], 1, ObjectDestructible.CellType.Barrel);
                        giveNewPointToObject(barrel);
                        clutter.add(barrel);
                        break;
                    case 2:
                        //chest
                        Clutter chest = new Clutter(30, 0, new Point(0, 0), imageClutter[2], 1, ObjectDestructible.CellType.Chest);
                        giveNewPointToObject(chest);
                        clutter.add(chest);
                        break;
                }
            }
        }
    }

    protected void createClutter(Room room) {
        int newSize = rand.nextInt(room.getMaxClutter());
        for (int i = 0; i < newSize; i++) {
            if (getNumEmptyCells() > 0) {
                switch (rand.nextInt(5)) {
                    default:
                    case 0:
                        //rock
                        Clutter rock = new Clutter(0, 0, new Point(0, 0), imageClutter[0], 1, ObjectDestructible.CellType.Clutter);
                        giveNewPointToObjectInRoom(rock, room.getStartPoint(), room.getMapWidth(), room.getMapHeight());
                        clutter.add(rock);
                        break;
                    case 1:
                        //barrel
                        Clutter barrel = new Clutter(10, 0, new Point(0, 0), imageClutter[1], 1, ObjectDestructible.CellType.Barrel);
                        giveNewPointToObjectInRoom(barrel, room.getStartPoint(), room.getMapWidth(), room.getMapHeight());
                        clutter.add(barrel);
                        break;
                    case 2:
                        //chest
                        Clutter chest = new Clutter(30, 0, new Point(0, 0), imageClutter[2], 1, ObjectDestructible.CellType.Chest);
                        giveNewPointToObjectInRoom(chest, room.getStartPoint(), room.getMapWidth(), room.getMapHeight());
                        clutter.add(chest);
                        break;
                }
            }
        }
    }

//    protected void createEnemies() {
////        creatures.clear();
//        int newSize = rand.nextInt(maxEnemies) + 1;
////        creatures = new ArrayList<Creature>(newSize);
//        for (int i = 0; i < newSize; i++) {
//            if (getNumEmptyCells() > 0) {
//                switch (rand.nextInt(4)) {
//                    default:
//                    case 0:
//                        Creature slime = new Creature(new Point(0, 0), imageEnemy[0], 3, ObjectDestructible.CellType.Slime, 0, 1000);
//                        giveNewPointToObject(slime);
//                        levelCreatures.add(slime);
//                        break;
//                    case 1:
//                        Creature goblin = new Creature(new Point(0, 0), imageEnemy[1], 5, ObjectDestructible.CellType.Goblin, 50, 1);
//                        giveNewPointToObject(goblin);
//                        levelCreatures.add(goblin);
//                        break;
//                }
//            }
//        }
//    }

    protected void createEnemies(Room room) {
        int newSize = 0;
        if (room != null){
            newSize = rand.nextInt(room.getMaxEnemies());
        } else  {
            newSize = rand.nextInt(maxEnemies);
        }
        for (int i = 0; i < newSize; i++) {
            if (getNumEmptyCells() > 0) {
                switch (rand.nextInt(4)) {
                    case 0:
                        createSlime(room);
                        break;
                    default:
                    case 1:
                        createGoblin(room);
                        break;
                    case 2:
                        createMinotaur(room);
                        break;
                }
            }
        }
    }
    private void createSlime(@Nullable Room room) {
        Creature slime = new Creature(new Point(0, 0), imageEnemy[0], 3, ObjectDestructible.CellType.Slime, 0, 1000);
        if (room != null) {
            giveNewPointToObjectInRoom(slime, room.getStartPoint(), room.getMapWidth(), room.getMapHeight());
        } else {
            giveNewPointToObject(slime);
        }
        levelCreatures.add(slime);
    }
    private void createGoblin(@Nullable Room room) {
        Creature goblin = new Creature(new Point(0, 0), imageEnemy[1], 5, ObjectDestructible.CellType.Goblin, 50, 1);
        if (room != null) {
            giveNewPointToObjectInRoom(goblin, room.getStartPoint(), room.getMapWidth(), room.getMapHeight());
        } else {
            giveNewPointToObject(goblin);
        }
        levelCreatures.add(goblin);
    }

    private void createMinotaur(@Nullable Room room) {
        Creature minotaur = new Creature(new Point(0, 0), imageEnemy[2], 5, ObjectDestructible.CellType.Minotaur, 50, 5);
        if (room != null) {
            giveNewPointToObjectInRoom(minotaur, room.getStartPoint(), room.getMapWidth(), room.getMapHeight());
        } else {
            giveNewPointToObject(minotaur);
        }
        levelCreatures.add(minotaur);
    }

    private void createHumanoid(@Nullable Room room) {
        Creature humanoid = new Creature(new Point(0, 0), imageNPCDown[0], 5, ObjectDestructible.CellType.Humanoid, 50, 5);
        if (room != null) {
            giveNewPointToObjectInRoom(humanoid, room.getStartPoint(), room.getMapWidth(), room.getMapHeight());
        } else {
            giveNewPointToObject(humanoid);
        }
        levelCreatures.add(humanoid);
    }

    private void createStairs(int currentLevel) {
        if (getNumEmptyCells() > 0 && currentLevel <= 100) {
            stairsDown = new ObjectDestructible(new Point(0, 0), imageStairs[0], 1000, ObjectDestructible.CellType.StairDown);
            giveNewPointToObject(stairsDown);
        }
        if (getNumEmptyCells() > 0 && currentLevel != 0) {
            stairsUp = new ObjectDestructible(new Point(0, 0), imageStairs[1], 1000, ObjectDestructible.CellType.StairUp);
            giveNewPointToObject(stairsUp);
        }
    }

    private void CreateCoins(Point point, int value) {
        Clutter coins = new Clutter(value, point, imageClutter[3], 0);
        coins.setCellType(ObjectDestructible.CellType.Clutter);
        clutter.add(coins);
        addObjectToMap(point, coins, true);
    }

    private void CreateHeartDiamond(Point point, int value) {
        Clutter diamondRed = new Clutter(200 + value, point, imageClutter[5], 0);
        diamondRed.setCellType(ObjectDestructible.CellType.Clutter);
        clutter.add(diamondRed);
        addObjectToMap(point, diamondRed, true);
    }

    private void CreateWhiteDiamond(Point point, int value) {
        Clutter diamond = new Clutter(200 + value, point, imageClutter[4], 0);
        diamond.setCellType(ObjectDestructible.CellType.Clutter);
        clutter.add(diamond);
        addObjectToMap(point, diamond, true);
    }

    private void CreateRandomDiamond(Point point, int value) {
        switch (rand.nextInt(2)) {
            default:
            case 0:
                CreateWhiteDiamond(point, value);
                break;
            case 1:
                CreateHeartDiamond(point, value);
                break;
        }
    }

    private void CreateRandomTreasure(Point point, int value) {
        switch (rand.nextInt(3)) {
            default:
            case 0:
                CreateCoins(point, value);
                break;
            case 1:
                CreateRandomDiamond(point, value);
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
        addObjectToMap(point, fud, true);
    }

    private void CreatePotion(Point point, int currentLevel) {
        //if it's a green potion, it restores your health.
        Food potion = new Food(Food.PotionColor.Green, currentLevel, point, imagePotion[1], 0);
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
        addObjectToMap(point, potion, true);
    }

    private void CreateScroll(Point point, int currentLevel) {
        Clutter scroll = new Clutter(currentLevel, point, imageScroll[0], 2);
        scroll.setCellType(ObjectDestructible.CellType.Scroll);
        scrolls.add(scroll);
        addObjectToMap(point, scroll, true);
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
                addObjectToMap(point, dagger, true);
                break;
            case 1:
                Attack = 4;
                Weapon sword = new Weapon(Attack, (int) (currentLevel / 2.0f), Attack * currentLevel, point, imageWeapon[2], 15);
                sword.setCellType(ObjectDestructible.CellType.Weapon);
                weapons.add(sword);
                addObjectToMap(point, sword, true);
                break;
            case 2:
                Attack = 6;
                Weapon axe = new Weapon(Attack, (int) (currentLevel / 4.0f), Attack * currentLevel, point, imageWeapon[3], 10);
                axe.setCellType(ObjectDestructible.CellType.Weapon);
                weapons.add(axe);
                addObjectToMap(point, axe, true);
                break;
            case 3:
                Attack = 12;
                Weapon bow = new Weapon(Attack, 0, (int) (50 - currentLevel / 2.0f), point, imageWeapon[4], 8);
                bow.setCellType(ObjectDestructible.CellType.Weapon);
                weapons.add(bow);
                addObjectToMap(point, bow, true);
                break;
        }
    }

    private void CreateWearable(Point point, int currentLevel) {
        switch (rand.nextInt(3)) {
            default:
            case 0:
                Wearable shield = new Wearable(
                        Wearable.EnchantType.Defense,
                        5 * (currentLevel + 1),
                        5,
                        currentLevel,
                        point,
                        imageWearables[2],
                        12 + currentLevel);
                shield.setCellType(ObjectDestructible.CellType.Wearable);
                wearables.add(shield);
                addObjectToMap(point, shield, true);
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
                addObjectToMap(point, silverRing, true);
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
                addObjectToMap(point, goldRing, true);
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
                addObjectToMap(point, shovel, true);
                break;
            case 1:
                MiningTool pickaxe = new MiningTool(3, 2 * (currentLevel + 1), point, imageMining[1], 100);
                pickaxe.setCellType(ObjectDestructible.CellType.MiningTool);
                miningTools.add(pickaxe);
                addObjectToMap(point, pickaxe, true);
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
                addObjectToMap(point, torch, true);
                break;
            case 1:
                LightSource lantern = new LightSource(5, 4, currentLevel + 10, point, imageLight[1], 50 * currentLevel);
                lantern.setCellType(ObjectDestructible.CellType.LightSource);
                lights.add(lantern);
                addObjectToMap(point, lantern, true);
                break;
        }
    }

    private void CreateRandomDrop(int i, ObjectDestructible.CellType type, int currentLevel) {
        switch (type) {
            case Barrel:
                switch (rand.nextInt(4)) {
                    default:
                    case 0:
                        CreateCoins(clutter.get(i).getPoint(), clutter.get(i).getValue());
                        break;
                    case 1:
                        CreateFood(clutter.get(i).getPoint());
                        break;
                    case 2:
                        CreatePotion(clutter.get(i).getPoint(), currentLevel);
                        break;
                    case 3:
                        CreateLightSource(clutter.get(i).getPoint(), currentLevel);
                }
                break;
            case Chest:
                switch (rand.nextInt(4)) {
                    default:
                    case 0:
                        CreateCoins(clutter.get(i).getPoint(), 30 + currentLevel);
                        break;
                    case 1:
                        CreateRandomDiamond(clutter.get(i).getPoint(), currentLevel);
                        break;
                    case 2:
                        CreateWearable(clutter.get(i).getPoint(), currentLevel);
                        break;
                    case 3:
                        CreateRandomConsumable(clutter.get(i).getPoint(), currentLevel);
                        break;
                }
                break;
            case Slime:
                Creature temp = levelCreatures.get(i);
                int value = rand.nextInt(temp.getMaxpHP() * 3) + 1;
                value += temp.getTotalValue();
                switch (rand.nextInt(3)) {
                    default:
                    case 0:
                        CreateCoins(levelCreatures.get(i).getPoint(), value);
                        break;
                    case 1:
                        CreateFood(levelCreatures.get(i).getPoint());
                        break;
                    case 2:
                        CreatePotion(levelCreatures.get(i).getPoint(), currentLevel);
                        break;
                }
                break;
            case Goblin:
                temp = levelCreatures.get(i);
                value = rand.nextInt(temp.getMaxpHP() * 3) + 1;
                value += temp.getTotalValue();
                switch (rand.nextInt(6)) {
                    default:
                    case 0:
                        CreateCoins(temp.getPoint(), value);
                        break;
                    case 1:
                        CreateWeapon(levelCreatures.get(i).getPoint(), currentLevel);
                        break;
                    case 2:
                        CreateWearable(levelCreatures.get(i).getPoint(), currentLevel);
                        break;
                    case 3:
                        CreateMiningTool(levelCreatures.get(i).getPoint(), currentLevel);
                        break;
                    case 4:
                        CreateRandomConsumable(levelCreatures.get(i).getPoint(), currentLevel);
                        break;
                    case 5:
                        CreateLightSource(levelCreatures.get(i).getPoint(), currentLevel);
//                            lights
                }
                break;
            case Minotaur:
                temp = levelCreatures.get(i);
                value = rand.nextInt(temp.getMaxpHP() * 3) + 1;
                value += temp.getTotalValue();
                switch (rand.nextInt(7)) {
                    default:
                    case 0:
                        CreateRandomTreasure(levelCreatures.get(i).getPoint(), 100 + (currentLevel * 2) + value);
                        break;
                    case 1:
                        CreateWeapon(levelCreatures.get(i).getPoint(), currentLevel + 5);
                        break;
                    case 2:
                        CreateWearable(levelCreatures.get(i).getPoint(), currentLevel + 5);
                        break;
                    case 3:
                        CreateMiningTool(levelCreatures.get(i).getPoint(), currentLevel + 5);
                        break;
                    case 4:
                        CreateRandomConsumable(levelCreatures.get(i).getPoint(), currentLevel);
                        break;
                    case 5:
                        CreateLightSource(levelCreatures.get(i).getPoint(), currentLevel + 5);
                        break;
                }
                break;
            case Humanoid:
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
        for (int i = 0; i < levelCreatures.size(); i++) {
            if (levelCreatures.get(i).getX() > camOffsetX &&
                    levelCreatures.get(i).getY() > camOffsetY &&
                    levelCreatures.get(i).getX() < camOffsetX + camWidth &&
                    levelCreatures.get(i).getY() < camOffsetY + camHeight) {
                Creature temp = levelCreatures.get(i);
                switch (temp.getCellType()) {
                    default:
                    case Slime:
                        break;
                    case Goblin:
                        moveCreature(dungeon, target, temp, currentLevel, friendlyFire);
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
                    if (AddPossiblePathPoints(pointLeft, temp, distance, targetDest)) {
                        break;
                    }
                    Point pointRight = new Point(target.x + 1, target.y);
                    if (AddPossiblePathPoints(pointRight, temp, distance, targetDest)) {
                        break;
                    }
                    Point pointUp = new Point(target.x, target.y - 1);
                    if (AddPossiblePathPoints(pointUp, temp, distance, targetDest)) {
                        break;
                    }
                    Point pointDown = new Point(target.x, target.y + 1);
                    if (AddPossiblePathPoints(pointDown, temp, distance, targetDest)) {
                        break;
                    }
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
        switch (rand.nextInt(4)) {
            //South
            case 0:
                if (MoveCreatureVertical(dungeon, temp, temp.getCurrentDepth(), temp.getY() + 1)){
                break;}
            //North
            case 1:
                if (MoveCreatureVertical(dungeon, temp, temp.getCurrentDepth(), temp.getY() - 1)){
                break;}
            //West
            case 2:
                if (MoveCreatureHorizontal(dungeon, temp, temp.getCurrentDepth(), temp.getX() + 1)){
                break;}
            //East
            case 3:
                if (MoveCreatureHorizontal(dungeon, temp, temp.getCurrentDepth(), temp.getX() - 1)){
                break;}
            //Stay
            default:
            case 4:
                break;
        }
    }

    public boolean MoveCreatureHorizontal(Dungeon dungeon, Creature creature, int currentLevel, int X) {
        if (dungeon.getDungeonLevels().get(currentLevel).interactWithObject(
                dungeon,
                X,
                creature.getPoint().y,
                creature,
                creature.getCurrentDepth(),
                dungeon.getFriendlyFire())) {
            removeObjectFromMap(creature.getPoint(), creature);
            creature.setX(X);
            addObjectToMap(creature.getPoint(), creature, false);
            return true;
        }
        return false;
    }

    public boolean MoveCreatureVertical(Dungeon dungeon, Creature creature, int currentLevel, int Y) {
        if (dungeon.getDungeonLevels().get(currentLevel).interactWithObject(
                dungeon,
                creature.getPoint().x,
                Y,
                creature,
                creature.getCurrentDepth(),
                dungeon.getFriendlyFire())) {
            removeObjectFromMap(creature.getPoint(), creature);
            creature.setY(Y);
            addObjectToMap(creature.getPoint(), creature, false);
            return true;
        }
        return false;
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
//        if (levelCreatures != null) {
//            for (int i = 0; i < levelCreatures.size(); i++) {
//                if (levelCreatures.get(i).getPoint().x == cellx && levelCreatures.get(i).getPoint().y == celly) {
//                    return levelCreatures.get(i).getCellType();
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
//                            super.setVoidSpace(celly, cellx);
//                            break;
                        default:
                        case 1:
                            Point tempPoint = new Point(cellx, celly);
                            CreateRandomDiamond(tempPoint, currentLevel);
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
                            addObjectToMap(possibleDrop.getPoint(), possibleDrop, true);
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
                            addObjectToMap(possibleDrop.getPoint(), possibleDrop, true);
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
                            addObjectToMap(possibleDrop.getPoint(), possibleDrop, true);
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
                            addObjectToMap(possibleDrop.getPoint(), possibleDrop, true);
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
                            addObjectToMap(possibleDrop.getPoint(), possibleDrop, true);
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
                            addObjectToMap(possibleDrop.getPoint(), possibleDrop, true);
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
                            addObjectToMap(possibleDrop.getPoint(), possibleDrop, true);
                        }
                        ifCreatureGetsMoved = true;
                        break;
                    }
                }
                break;
        }
        return ifCreatureGetsMoved;
    }

    public ArrayList<Point> getLevelPoints() {
        return FloorTiles;
    }

    private void HarmCreature(int cellx, int celly, Creature harmer, int currentLevel, ObjectDestructible.CellType harmeeType) {
        for (int i = 0; i < levelCreatures.size(); i++) {
            Creature tempCreature = levelCreatures.get(i);
            Point tempPoint = tempCreature.getPoint();

            if (tempPoint.x == cellx && tempPoint.y == celly) {
                int damagetotal = (int) (harmer.getAttack() * ((100 - tempCreature.getDefense()) / 100.0f));
                tempCreature.hurt(damagetotal);
                if (tempCreature.getHP() <= 0) {
                    CreateRandomDrop(i, harmeeType, currentLevel);
                    removeObjectFromMap(tempPoint, tempCreature);
                    levelCreatures.remove(i);
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
            FloorTiles.add(new Point(cellx, celly));
            numEmptyCells++;
        }
    }
}
