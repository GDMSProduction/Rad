package my.application.stephen.runattackdungeon;

import android.graphics.Point;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import static my.application.stephen.runattackdungeon.Creature.DirectionType.Horizontal;
import static my.application.stephen.runattackdungeon.Creature.DirectionType.Still;
import static my.application.stephen.runattackdungeon.Creature.DirectionType.Vertical;
import static my.application.stephen.runattackdungeon.GameView.Noises;
import static my.application.stephen.runattackdungeon.GameView.camHeight;
import static my.application.stephen.runattackdungeon.GameView.camWidth;
import static my.application.stephen.runattackdungeon.GameView.idBottleBreak;
import static my.application.stephen.runattackdungeon.GameView.idMiningFail;
import static my.application.stephen.runattackdungeon.GameView.idMiningSucceed;
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

    public enum ROOMType {EMPTY, LOOT, ENEMY, LOOTandENEMY, BOSS}

    public static final int roomHeightMin = 4;
    public static final int roomWidthMin = 4;
    private int roomNums = 2;
    private ArrayList<Room> LevelRooms = new ArrayList<>(roomNums);
    private ArrayList<Point> RoomStartPoints = new ArrayList<>(0);
    private boolean makeRooms = false;
    private int roomHeightMax = 8;
    private int roomWidthMax = 8;
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
    private ArrayList<Clutter> clutter = new ArrayList<>(maxClutter);
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
    private ArrayList<Creature> levelCreatures = new ArrayList<>(maxEnemies);

    Level(int Width, int Height, int currentLevel, int SpacesPercent, boolean natural, boolean MakeRooms, int borderThickness) {
        super(Width, Height, currentLevel, SpacesPercent, natural, borderThickness, ObjectDestructible.CellType.Border);
        makeRooms = MakeRooms;

        createRooms(Width, Height, currentLevel, borderThickness);
//            ConnectRooms();
        makeAvailablePoints(getMapHeight(), getMapWidth());
        if (numEmptyCells < minimumEmptyCells) {
            for (int i = 0; i < minimumEmptyCells; i++) {
                setSpace(getCurrentMap(), rand.nextInt(getMapHeight() - 6) + 3, rand.nextInt(getMapWidth() - 6) + 3, spaces.length - 1);
            }
            makeAvailablePoints(getMapHeight(), getMapWidth());
        }

        int TotalSpaces = getNumEmptyCells() - 2;
        maxEnemies = (int) (TotalSpaces * 0.2f);
        if (maxEnemies < 1) {
            maxEnemies = 1;
        }
        maxClutter = (int) (TotalSpaces * 0.2f);
        if (maxClutter < 1) {
            maxClutter = 1;
        }

        createStairsDown(null, currentLevel);
        createStairsUp(null, currentLevel);
        if (getStairsUp() != null && getStairsDown() != null) {
            MakeCorridor(getStairsUp().get2dPoint(), getStairsDown().get2dPoint());
        }

        createEnemies(null, currentLevel);
        if (natural) {
            createClutter(null, currentLevel);
        }
        if (currentLevel < 1) {
//            CreateHeartDiamond();
        }
        if (currentLevel > 5) {
            createMinotaur(LevelRooms.get(0), currentLevel);
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

    private void makeAvailablePoints(int Height, int Width) {
        super.numEmptyCells = 0;
        super.FloorTiles = new ArrayList<>();
        for (int row = 0; row < Height; row++) {
            for (int col = 0; col < Width; col++) {
                if (getOtherCellType(col, row) == ObjectDestructible.CellType.Space) {
                    super.addEmptyFloorTile(col, row);
                }
            }
        }
    }

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

    private ObjectDestructible.CellType createBorderType() {
        ObjectDestructible.CellType type;
        switch (rand.nextInt(4) + 1) {
            case 0: //Treasure Islands.
                type = ObjectDestructible.CellType.Border;
                break;
            default:
            case 1:
                type = ObjectDestructible.CellType.Wall;
                break;
            case 2:
                type = ObjectDestructible.CellType.SturdyWall;
                break;
            case 3:
                type = ObjectDestructible.CellType.BreakingWall;
                break;
            case 4:
                type = ObjectDestructible.CellType.Space;
                break;
            case 5: //Treasure Islands.
                type = ObjectDestructible.CellType.Void;
                break;
        }
        return type;
    }

    private ROOMType createRoomType() {
        ROOMType type;
        switch (rand.nextInt(4)) {
            case 0:
                type = ROOMType.EMPTY;
                break;
            case 1:
                type = ROOMType.LOOT;
                break;
            case 2:
                type = ROOMType.ENEMY;
                break;
            default:
            case 3:
                type = ROOMType.LOOTandENEMY;
                break;
        }
        return type;
    }

    private void createRooms(int Width, int Height, int Depth, int borderThickness) {
        setRoomDimensionsMax(Width - (borderThickness * 2), Height - (borderThickness * 2));
        setRoomAmount(Width - (borderThickness * 2), Height - (borderThickness * 2));
        LevelRooms = new ArrayList<>(roomNums);
        makeRoomStartPoints(borderThickness);

        for (int i = 0; i < roomNums; i++) {
            int roomWidth = rand.nextInt(roomWidthMax - roomWidthMin) + roomWidthMin;
            int roomHeight = rand.nextInt(roomHeightMax - roomHeightMin) + roomHeightMin;
            int roomBorderThickness = rand.nextInt(2) + 1;
            boolean natural = rand.nextBoolean();
            if (createRoom(
                    roomWidth,
                    roomHeight,
                    Depth,
                    100,
                    natural,
                    roomBorderThickness,
                    createBorderType(),
                    createRoomType()
            )) break;
        }
        //this is where the for loop ends.
    }

    private boolean createRoom(int Width, int Height, int Depth, int spacePercent, boolean natural, int borderThickness, ObjectDestructible.CellType borderType, ROOMType roomType) {
        if (RoomStartPoints.size() <= 0) {
            return true;
        }
        Room newRoom = new Room(
                Width,
                Height,
                Depth,
                spacePercent,
                natural,
                borderThickness,
                borderType,
                roomType
        );
        LevelRooms.add(newRoom);

        int startPointIndex = rand.nextInt(RoomStartPoints.size());
        newRoom.setStartPoint(RoomStartPoints.get(startPointIndex));
        int startX = RoomStartPoints.get(startPointIndex).x;
        int startY = RoomStartPoints.get(startPointIndex).y;

        //setRoom in the currentMap.
        for (int col = startX; col < startX + Width; col++) {
            for (int row = startY; row < startY + Height; row++) {
                super.getCurrentMap()[row][col].clear();
                for (int index = 0; index < newRoom.getCurrentMap()[row - startY][col - startX].size(); index++) {
                    super.getCurrentMap()[row][col].add(newRoom.getCurrentMap()[row - startY][col - startX].get(index));
                }
            }
        }
        //Remove possible Room tiles
        for (int col = startX - roomWidthMax; col < startX + Width; col++) {
            for (int row = startY - roomHeightMax; row < startY + Height; row++) {
                for (int roomStartPointIndex = 0; roomStartPointIndex < RoomStartPoints.size(); roomStartPointIndex++) {
                    if (RoomStartPoints.get(roomStartPointIndex).x == col && RoomStartPoints.get(roomStartPointIndex).y == row) {
                        RoomStartPoints.remove(roomStartPointIndex);
                    }
                }
            }
        }
        return false;
    }

    private void makeRoomStartPoints(int borderThickness) {
        int maxRoomStartHeight = super.getMapHeight() - roomHeightMax - borderThickness;
        int maxRoomStartWidth = super.getMapWidth() - roomWidthMax - borderThickness;
        for (int row = borderThickness; row < maxRoomStartHeight; row++) {
            for (int col = borderThickness; col < maxRoomStartWidth; col++) {
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

    protected void createClutter(@Nullable Room room, int currentLevel) {
        int newSize;
        if (room != null) {
            newSize = rand.nextInt(room.getMaxClutter());
        } else {
            newSize = rand.nextInt(maxClutter) + 1;
        }
        for (int i = 0; i < newSize; i++) {
            if (getNumEmptyCells() > 0) {
                switch (rand.nextInt(5)) {
                    default:
                    case 0:
                        //rock
                        createRock(room, null, currentLevel);
                        break;
                    case 1:
                        //barrel
                        createBarrel(room, currentLevel);
                        break;
                    case 2:
                        //chest
                        createChest(room, currentLevel);
                        break;
                }
            }
        }
    }
    private void setLighting(ObjectDestructible object){
        object.setPaintAlpha(getCurrentMap()[object.getPoint().y][object.getPoint().x].get(0).getPaintAlpha());
    }

    void createRock(@Nullable Room room, @Nullable Point3d newPoint, int currentLevel) {
        Clutter rock;
        if (newPoint == null) {
            rock = new Clutter(0, 0, new Point3d(0, 0, 0), imageClutter[0], 1, ObjectDestructible.CellType.Rock);
            giveNewPointToObject(room, rock, currentLevel);
        } else {
            rock = new Clutter(0, 0, newPoint, imageClutter[0], 1, ObjectDestructible.CellType.Rock);
            addObjectToMap(new Point(newPoint.x, newPoint.y), rock, false);
        }
        setLighting(rock);
        clutter.add(rock);
    }

    private void createBarrel(@Nullable Room room, int currentLevel) {
        Clutter barrel = new Clutter(10, 0, new Point3d(0, 0, 0), imageClutter[1], 1, ObjectDestructible.CellType.Barrel);
        giveNewPointToObject(room, barrel, currentLevel);
        setLighting(barrel);
        clutter.add(barrel);
    }

    private void createChest(@Nullable Room room, int currentLevel) {
        Clutter chest = new Clutter(30, 0, new Point3d(0, 0, 0), imageClutter[2], 1, ObjectDestructible.CellType.Chest);
        giveNewPointToObject(room, chest, currentLevel);
        setLighting(chest);
        clutter.add(chest);
    }

    protected void createEnemies(Room room, int currentLevel) {
        int newSize;
        if (room != null) {
            newSize = rand.nextInt(room.getMaxEnemies());
        } else {
            newSize = rand.nextInt(maxEnemies);
        }
        for (int i = 0; i < newSize; i++) {
            if (getNumEmptyCells() > 0) {
                switch (rand.nextInt(4)) {
                    default:
                    case 0:
                        createSlime(room, currentLevel);
                        break;
                    case 1:
                        createGoblin(room, currentLevel);
                        break;
                }
            }
        }
    }

    private Creature.PatrolType createPatrolType() {
        Creature.PatrolType patrolType;
        switch (rand.nextInt(2)) {
            case 0:
                patrolType = Creature.PatrolType.Points;
                break;
            default:
            case 1:
                patrolType = Creature.PatrolType.Line;
                break;
        }
        return patrolType;
    }

    private void giveSlimePatrolType(Creature slime, Creature.PatrolType patrolType) {
        slime.setPatrolType(patrolType);
        if (patrolType == Creature.PatrolType.Points) {
            slime.setMiningTool(new MiningTool(15, 0, slime.getPoint(), imageWeapon[0], 10));
            switch (slime.getDirectionType()) {
                case Still:
                    break;
                case Vertical:
                    Point3d possibleTargetVertical = new Point3d(slime.getX(), slime.getY(), slime.getZ());
                    ObjectDestructible.CellType cellTypeVertical = getOtherCellType(slime.getX(), slime.getY() + 1);
                    if (cellTypeVertical != ObjectDestructible.CellType.Border &&
                            cellTypeVertical != ObjectDestructible.CellType.Void) {
                        possibleTargetVertical = new Point3d(slime.getX(), slime.getY() + 1, slime.getZ());
                    }
                    super.setSpace(getCurrentMap(), possibleTargetVertical.y, possibleTargetVertical.x, 5);
                    super.setSpace(getCurrentMap(), possibleTargetVertical.y - 1, possibleTargetVertical.x, 5);
                    slime.getPatrolPoints().add(possibleTargetVertical);
                    slime.getPatrolPoints().add(new Point3d(possibleTargetVertical.x, possibleTargetVertical.y - 1, slime.getZ()));
                    break;
                case Horizontal:
                    Point3d possibleTargetHorizontal = new Point3d(slime.getX(), slime.getY(), slime.getZ());
                    ObjectDestructible.CellType cellTypeHorizontal = getOtherCellType(slime.getX() + 1, slime.getY());
                    if (cellTypeHorizontal != ObjectDestructible.CellType.Border &&
                            cellTypeHorizontal != ObjectDestructible.CellType.Void) {
                        possibleTargetHorizontal = new Point3d(slime.getX() + 1, slime.getY(), slime.getZ());
                    }
                    super.setSpace(getCurrentMap(), possibleTargetHorizontal.y, possibleTargetHorizontal.x, 5);
                    super.setSpace(getCurrentMap(), possibleTargetHorizontal.y, possibleTargetHorizontal.x - 1, 5);
                    slime.getPatrolPoints().add(possibleTargetHorizontal);
                    slime.getPatrolPoints().add(new Point3d(possibleTargetHorizontal.x - 1, possibleTargetHorizontal.y, slime.getZ()));
                    break;
                case HorizontalAndVertical:
                    Point3d possibleTarget = new Point3d(slime.getX(), slime.getY(), slime.getZ());
                    ObjectDestructible.CellType cellType = getOtherCellType(slime.getX() + 1, slime.getY());
                    if (cellType != ObjectDestructible.CellType.Border &&
                            cellType != ObjectDestructible.CellType.Void) {
                        possibleTarget = new Point3d(slime.getX() + 1, slime.getY(), slime.getZ());
                    }
                    cellType = getOtherCellType(possibleTarget.x, possibleTarget.y + 1);
                    if (cellType != ObjectDestructible.CellType.Border &&
                            cellType != ObjectDestructible.CellType.Void) {
                        possibleTarget = new Point3d(possibleTarget.x, possibleTarget.y + 1, slime.getZ());
                    }
                    super.setSpace(getCurrentMap(), possibleTarget.y, possibleTarget.x, 5);
                    super.setSpace(getCurrentMap(), possibleTarget.y, possibleTarget.x - 1, 5);
                    super.setSpace(getCurrentMap(), possibleTarget.y + 1, possibleTarget.x - 1, 5);
                    super.setSpace(getCurrentMap(), possibleTarget.y + 1, possibleTarget.x, 5);
                    slime.getPatrolPoints().add(possibleTarget);
                    slime.getPatrolPoints().add(new Point3d(possibleTarget.x - 1, possibleTarget.y, slime.getZ()));
                    slime.getPatrolPoints().add(new Point3d(possibleTarget.x - 1, possibleTarget.y + 1, slime.getZ()));
                    slime.getPatrolPoints().add(new Point3d(possibleTarget.x, possibleTarget.y + 1, slime.getZ()));
                    break;
            }
        }
    }

    /*
    If override is negative, it is not used.
    If override is positive, the function will not be random.
    override values:
    0: OneWaitTwo
    1: OneWaitOne
    2: TwoWaitTwo
    3: TwoWaitOne
    4: Full
     */
    private Creature.Handicap createCreatureHandicap(int creatureCapability, int override) {
        int switcher = rand.nextInt(creatureCapability);
        if (override > (-1)) {
            switcher = override;
        }
        Creature.Handicap handicap;
        switch (switcher) {
            case 0:
                handicap = Creature.Handicap.OneWaitTwo;
                break;
            case 1:
                handicap = Creature.Handicap.OneWaitOne;
                break;
            case 2:
                handicap = Creature.Handicap.TwoWaitTwo;
                break;
            case 3:
                handicap = Creature.Handicap.TwoWaitOne;
                break;
            default:
            case 4:
                handicap = Creature.Handicap.Full;
                break;
        }
        return handicap;
    }

    private void createSlime(@Nullable Room room, int currentLevel) {
        int attack = 1000;
        Creature.DirectionType directionType;
        Creature.PatrolType patrolType = Creature.PatrolType.Line;
        switch (rand.nextInt(5)) {
            default:
            case 0:
                directionType = Still;
                break;
            case 1:
                directionType = Vertical;
                patrolType = createPatrolType();
                attack = 1;
                break;
            case 2:
                directionType = Horizontal;
                patrolType = createPatrolType();
                attack = 1;
                break;
            case 3:
                directionType = Creature.DirectionType.HorizontalAndVertical;
                patrolType = createPatrolType();
                attack = 2;
                break;
        }
        Creature slime = new Creature(
                new Point3d(0, 0, 0),
                imageEnemy[0],
                3,
                ObjectDestructible.CellType.Slime,
                0,
                attack,
                currentLevel,
                directionType);
        giveNewPointToObject(room, slime, currentLevel);
        giveSlimePatrolType(slime, patrolType);
        slime.setHandicap(createCreatureHandicap(5, -1));
        setLighting(slime);
        levelCreatures.add(slime);
    }

    private void createGoblin(@Nullable Room room, int currentLevel) {
        Creature goblin = new Creature(
                new Point3d(0, 0, 0),
                imageEnemy[1],
                5,
                ObjectDestructible.CellType.Goblin,
                50,
                1,
                currentLevel,
                Creature.DirectionType.HorizontalAndVertical);
        giveNewPointToObject(room, goblin, currentLevel);
        Creature.PatrolType patrolType = createPatrolType();
        if (patrolType == Creature.PatrolType.Points) {
            if (room != null) {
                goblin.getPatrolPoints().add(
                        new Point3d(
                                room.getStartPoint().x + room.getBorderThickness(),
                                room.getStartPoint().y + room.getBorderThickness(),
                                currentLevel
                        )
                );
                goblin.getPatrolPoints().add(
                        new Point3d(
                                room.getStartPoint().x + room.getMapWidth() - room.getBorderThickness(),
                                room.getStartPoint().y + room.getMapHeight() - room.getBorderThickness(),
                                currentLevel
                        )
                );
            } else {
                if (stairsDown != null) {
                    goblin.getPatrolPoints().add(
                            new Point3d(
                                    getStairsDown().getX() - 1,
                                    getStairsDown().getY(),
                                    currentLevel
                            )
                    );
                }
                if (stairsUp != null) {
                    goblin.getPatrolPoints().add(
                            new Point3d(
                                    getStairsUp().getX() - 1,
                                    getStairsUp().getY(),
                                    currentLevel
                            )
                    );
                }
                if (FloorTiles.size() > 0) {
                    Point temp = FloorTiles.get(rand.nextInt(FloorTiles.size()));
                    goblin.getPatrolPoints().add(
                            new Point3d(temp.x, temp.y, currentLevel)
                    );
                }
                if (goblin.getPatrolPoints().size() < 2) {
                    goblin.setDirectionType(Creature.DirectionType.Still);
                }
            }
        }
        setLighting(goblin);
        levelCreatures.add(goblin);
    }

    private void createMinotaur(@Nullable Room room, int currentLevel) {
        Creature minotaur = new Creature(
                new Point3d(0, 0, 0),
                imageEnemy[2],
                2 * currentLevel, //HPMmax
                ObjectDestructible.CellType.Minotaur,
                50,
                5,
                currentLevel,
                Creature.DirectionType.HorizontalAndVertical);
        minotaur.setMovementType(Creature.MovementType.TowardsTargetDirectional);
        giveNewPointToObject(room, minotaur, currentLevel);
        setLighting(minotaur);
        levelCreatures.add(minotaur);
    }

    private void createHumanoid(@Nullable Room room, int currentLevel) {
        Creature humanoid = new Creature(
                new Point3d(0, 0, 0),
                imageNPCDown[0],
                5,
                ObjectDestructible.CellType.Humanoid,
                50,
                5,
                currentLevel,
                Still);
        giveNewPointToObject(room, humanoid, currentLevel);
        setLighting(humanoid);
        levelCreatures.add(humanoid);
    }

    private void createStairsDown(@Nullable Room room, int currentLevel) {
        if (getNumEmptyCells() > 0 && currentLevel <= 100) {
            stairsDown = new ObjectDestructible(new Point3d(0, 0, 0), imageStairs[0], 1000, ObjectDestructible.CellType.StairDown);
            giveNewPointToObject(room, stairsDown, currentLevel);
            setLighting(stairsDown);
        }
    }

    private void createStairsUp(@Nullable Room room, int currentLevel) {
        if (getNumEmptyCells() > 0 && currentLevel > 0) {
            stairsUp = new ObjectDestructible(new Point3d(0, 0, 0), imageStairs[1], 1000, ObjectDestructible.CellType.StairUp);
            giveNewPointToObject(room, stairsUp, currentLevel);
            setLighting(stairsUp);
        }
    }

    private void CreateCoins(Point3d point, int value) {
        Clutter coins = new Clutter(value, point, imageClutter[3], 0);
        coins.setCellType(ObjectDestructible.CellType.Clutter);
        setLighting(coins);
        clutter.add(coins);
        addObjectToMap(new Point(point.x, point.y), coins, true);
    }

    private void CreateHeartDiamond(Point3d point, int value) {
        Clutter diamondRed = new Clutter(200 + value, point, imageClutter[5], 0);
        diamondRed.setCellType(ObjectDestructible.CellType.Clutter);
        setLighting(diamondRed);
        clutter.add(diamondRed);
        addObjectToMap(new Point(point.x, point.y), diamondRed, true);
    }

    private void CreateWhiteDiamond(Point3d point, int value) {
        Clutter diamond = new Clutter(200 + value, point, imageClutter[4], 0);
        diamond.setCellType(ObjectDestructible.CellType.Clutter);
        setLighting(diamond);
        clutter.add(diamond);
        addObjectToMap(new Point(point.x, point.y), diamond, true);
    }

    private void CreateRandomDiamond(Point3d point, int value) {
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

    private void CreateRandomTreasure(Point3d point, int value) {
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

    private void CreateFood(Point3d point) {
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
        setLighting(fud);
        food.add(fud);
        addObjectToMap(new Point(point.x, point.y), fud, true);
    }

    private void CreatePotion(Point3d point, int depth) {
        //if it's a green potion, it restores your health.
        Food potion = new Food(Food.PotionColor.Green, depth, point, imagePotion[1], 0);
        switch (rand.nextInt(6)) {
            default:
                potion.setHealing(depth);
                break;
            case 1:
                //if it's a light blue potion, it randomly teleports you to an open space within 10 feet.
                potion.setPotionColor(Food.PotionColor.LightBlue);
                potion.setBitMap(imagePotion[2]);
                break;
            case 2:
                //if it's a black potion, it acidifies and destroys your belongings.
                potion.setPotionColor(Food.PotionColor.Black);
                potion.setBitMap(imagePotion[3]);
                potion.setHealing(depth / 2);
                break;
            case 3:
                //if it's a red potion, it increases your attack + maxAttack.
                potion.setBitMap(imagePotion[4]);
                potion.setPotionColor(Food.PotionColor.Red);
                potion.setHealing(depth / 2);
                break;
            case 4:
                //if it's a purple potion, it poisons you.
                potion.setPotionColor(Food.PotionColor.Purple);
                potion.setBitMap(imagePotion[5]);
                potion.setHealing(depth);
                break;
            case 5:
                //if it's a dark blue potion, it increases your defense.
                potion.setPotionColor(Food.PotionColor.DarkBlue);
                potion.setBitMap(imagePotion[6]);
                potion.setHealing(depth / 5);
                break;
        }
        potion.setCellType(ObjectDestructible.CellType.Potion);
        setLighting(potion);
        potions.add(potion);
        addObjectToMap(new Point(point.x, point.y), potion, true);
    }

    private void CreateScroll(Point3d point, int depth) {
        Clutter scroll = new Clutter(depth, point, imageScroll[0], 2);
        scroll.setCellType(ObjectDestructible.CellType.Scroll);
        setLighting(scroll);
        scrolls.add(scroll);
        addObjectToMap(new Point(point.x, point.y), scroll, true);
    }

    private void CreateRandomConsumable(Point3d point, int depth) {
        switch (rand.nextInt(3)) {
            case 0:
                CreatePotion(point, depth);
                break;
            default:
            case 1:
                CreateFood(point);
                break;
            case 2:
                CreateScroll(point, depth);
        }
    }

    private void CreateWeapon(Point3d point, int depth) {
        int Attack;
        switch (rand.nextInt(4)) {
            default:
            case 0:
                Attack = 2;
                Weapon dagger = new Weapon(Attack, depth, Attack * depth, point, imageWeapon[1], 10);
                dagger.setCellType(ObjectDestructible.CellType.Weapon);
                setLighting(dagger);
                weapons.add(dagger);
                addObjectToMap(new Point(point.x, point.y), dagger, true);
                break;
            case 1:
                Attack = 4;
                Weapon sword = new Weapon(Attack, (int) (depth / 2.0f), Attack * depth, point, imageWeapon[2], 15);
                sword.setCellType(ObjectDestructible.CellType.Weapon);
                setLighting(sword);
                weapons.add(sword);
                addObjectToMap(new Point(point.x, point.y), sword, true);
                break;
            case 2:
                Attack = 6;
                Weapon axe = new Weapon(Attack, (int) (depth / 4.0f), Attack * depth, point, imageWeapon[3], 10);
                axe.setCellType(ObjectDestructible.CellType.Weapon);
                setLighting(axe);
                weapons.add(axe);
                addObjectToMap(new Point(point.x, point.y), axe, true);
                break;
            case 3:
                Attack = 12;
                Weapon bow = new Weapon(Attack, 0, (int) (50 - depth / 2.0f), point, imageWeapon[4], 8);
                bow.setCellType(ObjectDestructible.CellType.Weapon);
                setLighting(bow);
                weapons.add(bow);
                addObjectToMap(new Point(point.x, point.y), bow, true);
                break;
        }
    }

    private void CreateWearable(Point3d point, int depth) {
        switch (rand.nextInt(3)) {
            default:
            case 0:
                Wearable shield = new Wearable(
                        Wearable.EnchantType.Defense,
                        5 * (depth + 1),
                        5,
                        depth,
                        point,
                        imageWearables[2],
                        12 + depth);
                shield.setCellType(ObjectDestructible.CellType.Wearable);
                setLighting(shield);
                wearables.add(shield);
                addObjectToMap(new Point(point.x, point.y), shield, true);
                break;
            case 1:
                Wearable silverRing;
                silverRing = new Wearable(
                        createWearableEnchantType(),
                        50 * (depth / 2 + 1),
                        2,
                        depth,
                        point,
                        imageWearables[1],
                        500);
                silverRing.setCellType(ObjectDestructible.CellType.Wearable);
                setLighting(silverRing);
                wearables.add(silverRing);
                addObjectToMap(new Point(point.x, point.y), silverRing, true);
                break;
            case 2:
                Wearable goldRing;
                goldRing = new Wearable(
                        createWearableEnchantType(),
                        50 * (depth + 1),
                        5,
                        depth,
                        point,
                        imageWearables[0],
                        1000);
                goldRing.setCellType(ObjectDestructible.CellType.Wearable);
                setLighting(goldRing);
                wearables.add(goldRing);
                addObjectToMap(new Point(point.x, point.y), goldRing, true);
                break;
        }
    }

    private Wearable.EnchantType createWearableEnchantType() {
        Wearable.EnchantType type;
        switch (rand.nextInt(4)) {
            case 0:
                type = Wearable.EnchantType.Defense;
                break;
            default:
            case 1:
                type = Wearable.EnchantType.Health;
                break;
            case 2:
                type = Wearable.EnchantType.Attack;
                break;
            case 3:
                type = Wearable.EnchantType.FeatherFall;
                break;
        }
        return type;
    }

    private void CreateMiningTool(Point3d point, int depth) {
        switch (rand.nextInt(2)) {
            default:
            case 0:
                MiningTool shovel = new MiningTool(1, depth, point, imageMining[0], 10);
                shovel.setCellType(ObjectDestructible.CellType.MiningTool);
                setLighting(shovel);
                miningTools.add(shovel);
                addObjectToMap(new Point(point.x, point.y), shovel, true);
                break;
            case 1:
                MiningTool pickaxe = new MiningTool(3, 2 * (depth + 1), point, imageMining[1], 100);
                pickaxe.setCellType(ObjectDestructible.CellType.MiningTool);
                setLighting(pickaxe);
                miningTools.add(pickaxe);
                addObjectToMap(new Point(point.x, point.y), pickaxe, true);
                break;
        }
    }

    private void CreateLightSource(Point3d point, int depth) {
        switch (rand.nextInt(2)) {
            default:
            case 0:
                LightSource torch = new LightSource(5, 2, 0, point, imageLight[0], 500 + depth);
                torch.setCellType(ObjectDestructible.CellType.LightSource);
                setLighting(torch);
                lights.add(torch);
                addObjectToMap(new Point(point.x, point.y), torch, true);
                break;
            case 1:
                LightSource lantern = new LightSource(5, 4, depth + 10, point, imageLight[1], 50 * depth);
                lantern.setCellType(ObjectDestructible.CellType.LightSource);
                setLighting(lantern);
                lights.add(lantern);
                addObjectToMap(new Point(point.x, point.y), lantern, true);
                break;
        }
    }

    public void CreateRandomDrop(int i, ObjectDestructible.CellType type, int depth) {
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
                        CreatePotion(clutter.get(i).getPoint(), depth);
                        break;
                    case 3:
                        CreateLightSource(clutter.get(i).getPoint(), depth);
                }
                break;
            case Chest:
                switch (rand.nextInt(4)) {
                    default:
                    case 0:
                        CreateCoins(clutter.get(i).getPoint(), 30 + depth);
                        break;
                    case 1:
                        CreateRandomDiamond(clutter.get(i).getPoint(), depth);
                        break;
                    case 2:
                        CreateWearable(clutter.get(i).getPoint(), depth);
                        break;
                    case 3:
                        CreateRandomConsumable(clutter.get(i).getPoint(), depth);
                        break;
                }
                break;
            case Slime:
                Creature temp = levelCreatures.get(i);
                int value = rand.nextInt(temp.getMaxHP() * 3) + 1;
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
                        CreatePotion(levelCreatures.get(i).getPoint(), depth);
                        break;
                }
                break;
            case Goblin:
                temp = levelCreatures.get(i);
                value = rand.nextInt(temp.getMaxHP() * 3) + 1;
                value += temp.getTotalValue();
                switch (rand.nextInt(6)) {
                    default:
                    case 0:
                        CreateCoins(temp.getPoint(), value);
                        break;
                    case 1:
                        CreateWeapon(levelCreatures.get(i).getPoint(), depth);
                        break;
                    case 2:
                        CreateWearable(levelCreatures.get(i).getPoint(), depth);
                        break;
                    case 3:
                        CreateMiningTool(levelCreatures.get(i).getPoint(), depth);
                        break;
                    case 4:
                        CreateRandomConsumable(levelCreatures.get(i).getPoint(), depth);
                        break;
                    case 5:
                        CreateLightSource(levelCreatures.get(i).getPoint(), depth);
//                            lights
                }
                break;
            case Minotaur:
                temp = levelCreatures.get(i);
                value = rand.nextInt(temp.getMaxHP() * 3) + 1;
                value += temp.getTotalValue();
                switch (rand.nextInt(7)) {
                    default:
                    case 0:
                        CreateRandomTreasure(levelCreatures.get(i).getPoint(), 100 + (depth * 2) + value);
                        break;
                    case 1:
                        CreateWeapon(levelCreatures.get(i).getPoint(), depth + 5);
                        break;
                    case 2:
                        CreateWearable(levelCreatures.get(i).getPoint(), depth + 5);
                        break;
                    case 3:
                        CreateMiningTool(levelCreatures.get(i).getPoint(), depth + 5);
                        break;
                    case 4:
                        CreateRandomConsumable(levelCreatures.get(i).getPoint(), depth);
                        break;
                    case 5:
                        CreateLightSource(levelCreatures.get(i).getPoint(), depth + 5);
                        break;
                }
                break;
            case Humanoid:
                break;
        }
    }

    //Creature updates.

    //returns true only if harmee is killed.
    public boolean HarmCreature(int cellx, int celly, Creature harmer, int currentLevel, int dungeonSize, ObjectDestructible.CellType harmeeType) {
//        for (int j = 0; j < getCurrentMap()[celly][cellx].size(); j++){
//            ObjectDestructible object = (getCurrentMap()[celly][cellx].get(j));
//            if (object.getCellType() == harmeeType){
//                Creature creature = (Creature)object;
//                int damagetotal = (int) (harmer.getAttack() * ((100 - creature.getDefense()) / 100.0f));
//                creature.hurt(damagetotal);
//                if (creature.getHP() <= 0) {
//        LightSource possibleDrop = creature.setLightSource(null);
//        if (possibleDrop != null) {
//            possibleDrop.setPoint(cellx, celly, creature.getZ());
//            lights.add(possibleDrop);
//            addObjectToMap(possibleDrop.get2dPoint(), possibleDrop, true);
//        } else {
//            CreateRandomDrop(i, harmeeType, currentLevel);
//        }
//                    removeObjectFromMap(new Point(celly, cellx), creature);
//                    levelCreatures.remove(creature);
//                    return true;
//                }
//            }
//        }
        for (int i = 0; i < levelCreatures.size(); i++) {
            Creature tempCreature = levelCreatures.get(i);
            Point tempPoint = tempCreature.get2dPoint();

            if (tempPoint.x == cellx && tempPoint.y == celly) {
                if (harmer.getPotion() != null){
                    tempCreature.usePotion(harmer.getPotion(), dungeonSize, this);
                    Noises.play(idBottleBreak, 1, 1, 0, 0, 1);
                    harmer.setPotion(null);
                }else {
                    tempCreature.hurt(
                            (int) (harmer.getAttack() * ((100 - tempCreature.getDefense()) / 100.0f))
                    );
                }
                tempCreature.setCreatureState(Creature.state.Chase);
                if (tempCreature.getHP() <= 0) {
                    LightSource possibleDrop = tempCreature.setLightSource(null);
                    if (possibleDrop != null) {
                        possibleDrop.setPoint(cellx, celly, tempCreature.getZ());
                        lights.add(possibleDrop);
                        addObjectToMap(possibleDrop.get2dPoint(), possibleDrop, true);
                    } else {
                        CreateRandomDrop(i, harmeeType, currentLevel);
                    }
                    removeObjectFromMap(tempPoint, tempCreature);
                    levelCreatures.remove(i);

                    return true;
                }
            }
        }
        return false;
    }

    public void harmWall(int cellx, int celly, int currentDepth, int mining, MiningTool miningTool, ObjectDestructible.CellType wallType) {
        super.getCurrentMap()[celly][cellx].get(0).hurt(mining);
        if (mining > 0) {
            Noises.play(idMiningSucceed, 1, 1, 0, 0, 1);
//            miningNoises[1].start();
            if (wallType == ObjectDestructible.CellType.SturdyWall) {
                super.getCurrentMap()[celly][cellx].get(0).setBitMap(walls[0]);
            }
        } else {
            Noises.play(idMiningFail, 1, 1, 0, 0, 1);
//            miningNoises[0].start();
        }
        if (super.getCurrentMap()[celly][cellx].get(0).getHP() <= 0) {
            int wallHealth = super.getCurrentMap()[celly][cellx].get(0).getMaxHP();
            super.setSpace(super.getCurrentMap(), celly, cellx, spaces.length - 2);
            super.addEmptyFloorTile(cellx, celly);
            switch (rand.nextInt(getMapHeight() * getMapWidth())) {
                default:
                case 0:
                    if (miningTool.getBitmap() != imageMining[1]) {
                        createRock(null, new Point3d(cellx, celly, currentDepth), currentDepth);
                        break;
                    }
                case 1:
                    if (miningTool.getBitmap() == imageMining[1]) {
                        if (wallHealth > 10 && rand.nextInt(getMapWidth()) < 5) {
                            CreateRandomDiamond(new Point3d(cellx, celly, currentDepth), currentDepth);
                            break;
                        } else {
                            createRock(null, new Point3d(cellx, celly, currentDepth), currentDepth);
                            break;
                        }
                    }
                case 2:case 3:case 4:
                    if (wallHealth < 10) {
                        super.setVoid(getCurrentMap(), celly, cellx);
                        break;
                    }
            }
        }
    }
}
