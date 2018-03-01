package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.Random;

import static my.application.stephen.runattackdungeon.GameView.camHeight;
import static my.application.stephen.runattackdungeon.GameView.camWidth;
import static my.application.stephen.runattackdungeon.GameView.spaces;
import static my.application.stephen.runattackdungeon.GameView.walls;

/**
 * Created by Stephen on 2018-01-16.
 */

public class Map {
    //array of available images for Spaces
//    private Bitmap[] spaces;
    //array of available images for walls
//    private Bitmap[] walls;
    private int sturdywallHealth = 15;
    private int breakingwallHealth = 5;
    //the Array of the center points of Rooms
    private Point[] RoomCenters;
    //The Array of available roomCenters
    private ArrayList<Point> AvailableRoomCenters;
    //the percent of tiles that we want to be walls in the finalized map.
    private int WallPercent = 40;
    //whether or not to make "rooms"
    private boolean makeRooms;
    //the amount of desired rooms
    private int roomNums = 2;
    //the maximum radius of rooms
    private int roomRadiusMax = 4;
    //the maximum radius of rooms
    private int roomRadiusMin = 1;
    //the number of floor tiles
    private int numEmptyCells = 0;
    //the amount of tiles in each row
    private int mWidth;
    //the amount of tiles in each column
    private int mHeight;
    //random number, used for random number generation
    protected Random rand = new Random();
    //the current map tileset
    private ObjectDestructible[][] mCellsCurr;
    //the next generation of the map tileset.
    private ObjectDestructible[][] mCellsNext;
    //The points of every floor tile.
    private ArrayList<Point> FloorTiles;

    //Given a group of tiles,
    //if a pic is in the group, return true.
    //if not, return false.
    private boolean FindInArray(Bitmap[] ImageArray, Bitmap pic) {
        int length = ImageArray.length;
        for (int i = 0; i < length; i++) {
            if (pic == ImageArray[i]) {
                return true;
            }
        }
        return false;
    }

    //fills the current map with randomized tiles
    private void InitializeRandomMap() {
        // innards of map
        for (int row = 0; row < mHeight; row++) {
            for (int col = 0; col < mWidth; col++) {
                Point tempPoint = new Point(0, 0);
                ObjectDestructible temp = new ObjectDestructible(tempPoint, walls[0], 5);
                temp.setPoint(col, row);
                mCellsCurr[row][col] = temp;
                if (rand.nextLong() % 100 + 1 <= WallPercent) {
                    SetWall(row, col);
                } else
                    mCellsCurr[row][col].setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
            }
        }
    }

    private void SetWall(int row, int col) {
        mCellsCurr[row][col].setBitMap(walls[rand.nextInt(walls.length)]);
    }

    private void SetWallHealth(ObjectDestructible objectDestructible) {
        if (objectDestructible.getBitmap() == walls[1])
            objectDestructible.setMaxHP(sturdywallHealth);
        else if (objectDestructible.getBitmap() == walls[0])
            objectDestructible.setMaxHP(breakingwallHealth);
    }

    private void MakeRooms() {
        for (int i = 0; i < roomNums && AvailableRoomCenters.size() > 0; i++) {
            RoomCenters[i] = GetRandRoomCell();
            int distributionX = rand.nextInt(roomRadiusMax) + roomRadiusMin;
            int distributionY = rand.nextInt(roomRadiusMax) + roomRadiusMin;
            //hollows out room
//            switch  (rand.nextInt(2)){
//                case 0:
            for (int col = RoomCenters[i].x - distributionX; col < RoomCenters[i].x + distributionX; col++) {
                for (int row = RoomCenters[i].y - distributionY; row < RoomCenters[i].y + distributionY; row++) {
                    mCellsCurr[row][col].setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
                    Point temp = new Point(col, row);
                    int pointIndex = FindAvailablePoint(temp);
                    if (pointIndex >= 0) {
                        AvailableRoomCenters.remove(pointIndex);
                    }
                }
            }
//                    break;
//                case 1:
//                    for (int col = RoomCenters[i].x - distributionX; col <= RoomCenters[i].x + distributionX; col++) {
//                        for (int row = RoomCenters[i].y - distributionY; row <= RoomCenters[i].y + distributionY; row++) {
//                            mCellsCurr[row][col].setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
//                            Point temp = new Point(col, row);
//                            int pointIndex = FindAvailablePoint(temp);
//                            if (pointIndex >= 0) {
//                                AvailableRoomCenters.remove(pointIndex);
//                            }
//                        }
//                    }
//                    break;
//            }
            //makes room borders.
            if (distributionX > 10 || distributionY > 10) {

            }
            MakeBorders(RoomCenters[i].x - distributionX, RoomCenters[i].y - distributionY, distributionX, distributionY);
        }
    }


    private int FindAvailablePoint(Point temp) {
        int ret = -1;
        for (int index = 0; index < AvailableRoomCenters.size(); index++) {
            if (AvailableRoomCenters.get(index).x == temp.x &&
                    AvailableRoomCenters.get(index).y == temp.y) {
                return index;
            }
        }
        return ret;
    }

    private Point GetRandRoomCell() {
        int debug = 0;
        return AvailableRoomCenters.get(rand.nextInt(AvailableRoomCenters.size()));
    }

    private void MakeCorridors() {
        double high = 0;
        //find the two points farthest apart.
        for (int i = 0; i < RoomCenters.length - 1 && RoomCenters[i + 1] != null; i++) {
            int lesserX = RoomCenters[i].x;
            int greaterX = RoomCenters[i + 1].x;
            if (RoomCenters[i].x > RoomCenters[i + 1].x) {
                lesserX = RoomCenters[i + 1].x;
                greaterX = RoomCenters[i].x;
            }
            int lesserY = RoomCenters[i].y;
            int greaterY = RoomCenters[i + 1].y;
            if (RoomCenters[i].y > RoomCenters[i + 1].y) {
                lesserY = RoomCenters[i + 1].y;
                greaterY = RoomCenters[i].y;
            }

            Point thingy = RoomCenters[i];
            Point thingy2 = RoomCenters[i + 1];
            int corridorWidth = Math.abs(RoomCenters[i].x - RoomCenters[i + 1].x);
            int corridorHeight = Math.abs(RoomCenters[i].y - RoomCenters[i + 1].y);

            if (RoomCenters[i].x >= RoomCenters[i + 1].x && RoomCenters[i].y <= RoomCenters[i + 1].y ||
                    RoomCenters[i].x <= RoomCenters[i + 1].x && RoomCenters[i].y >= RoomCenters[i + 1].y) {

                switch (rand.nextInt(2)) {
                    default:
                    case 0:
                        for (int j = 0; j <= corridorWidth; j++) {
                            mCellsCurr[lesserY][lesserX + j].setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
                        }
                        for (int k = 0; k <= corridorHeight; k++) {
                            mCellsCurr[lesserY + k][lesserX].setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
                        }
                        break;
                    case 1:
                        for (int j = 0; j <= corridorWidth; j++) {
                            mCellsCurr[greaterY][lesserX + j].setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
                        }
                        for (int k = 0; k <= corridorHeight; k++) {
                            mCellsCurr[lesserY + k][greaterX].setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
                        }
                        break;
                }
            } else if (RoomCenters[i].x > RoomCenters[i + 1].x && RoomCenters[i].y > RoomCenters[i + 1].y ||
                    RoomCenters[i].x < RoomCenters[i + 1].x && RoomCenters[i].y < RoomCenters[i + 1].y) {
                switch (rand.nextInt(2)) {
                    default:
                    case 0:
                        for (int j = 0; j <= corridorWidth; j++) {
                            mCellsCurr[lesserY][lesserX + j].setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
                        }
                        for (int k = 0; k <= corridorHeight; k++) {
                            mCellsCurr[lesserY + k][greaterX].setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
                        }
                        break;
                    case 1:
                        for (int j = 0; j <= corridorWidth; j++) {
                            mCellsCurr[greaterY][lesserX + j].setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
                        }
                        for (int k = 0; k <= corridorHeight; k++) {
                            mCellsCurr[lesserY + k][lesserX].setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
                        }
                        break;
                }
            }
        }
    }

    private void MakeBorders(int startX, int startY, int length, int height) {
        // horizontal  borders
        for (int i = startX; i < startX + length; i++) {
            //topLeft to bottomLeft
            SetWall(startY, i);
            //topRight to bottomRight
            SetWall(startY + height, i);
        }

        // vertical borders
        for (int i = startY; i < startY + height; i++) {
            //topLeft to topRight
            SetWall(i, startX);
            //bottomLeft to bottomRight
            SetWall(i, startX + length);
        }
    }

    private double distance(Point start, Point end) {
        return Math.sqrt(Math.pow((start.x - end.x), 2) + Math.pow((start.y - end.y), 2));
    }

    private void RefineMap(boolean preventLargeOpenAreas) {
        for (int row = 0; row < mHeight; row++) {
            for (int col = 0; col < mWidth; col++) {
                mCellsNext[row][col] = mCellsCurr[row][col];
                int A1Walls = NeighboringWallCount(col, row, 1);
                int A2Walls = NeighboringWallCount(col, row, 2);

                if (FindInArray(walls, mCellsCurr[row][col].getBitmap())) {
                    if (A1Walls >= 4)
                        SetWall(row, col);
                    else
                        mCellsNext[row][col].setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
                } else if (FindInArray(spaces, mCellsCurr[row][col].getBitmap())) {
                    if (A1Walls >= 5)
                        SetWall(row, col);
                    else if (preventLargeOpenAreas && A2Walls <= 1)
                        SetWall(row, col);
                    else
                        mCellsNext[row][col].setBitMap(spaces[rand.nextInt(spaces.length - 1)]);
                }
            }
        }
        //MakeCorridors();

        CopyArray(mCellsNext, mCellsCurr, mWidth, mHeight);
    }

    private void CopyArray(ObjectDestructible[][] ImageArray1, ObjectDestructible[][] ImageArray2, int width, int height) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                ImageArray2[j][i] = ImageArray1[j][i];
            }
        }
    }

    private int NeighboringWallCount(int x, int y, int wallDistribution) {
        int wallCount = 0;

        for (int row = y - wallDistribution; row <= y + wallDistribution; row++) {
            for (int col = x - wallDistribution; col <= x + wallDistribution; col++) {
                if (row == y && col == x)
                    continue;
                else if (row < 0 || col < 0 || row >= mHeight || col >= mWidth)
                    wallCount++;
                else if (FindInArray(walls, mCellsCurr[row][col].getBitmap()))
                    wallCount++;
            }
        }

        return wallCount;
    }

    private void GetEmptyFloorPoints() {
        numEmptyCells = 0;
        FloorTiles = new ArrayList<Point>();
        for (int row = 0; row < mHeight; row++) {
            for (int col = 0; col < mWidth; col++) {
                if (FindInArray(spaces, mCellsCurr[row][col].getBitmap())) {
                    Point temp = new Point(col, row);
                    FloorTiles.add(temp);
                    numEmptyCells++;
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //
    //////////////////////////////////////////////////////////////////////////////////////////
    public Map(int Width, int Height, boolean natural) {

//        spaces = new Bitmap[6];
//        spaces = spaces;
//        walls = new Bitmap[2];
//        walls = walls;
        //40 x 40 = properties of all map tiles.
        mWidth = Width;
        mHeight = Height;

        if (mWidth < mHeight) {
            roomRadiusMax = mWidth / 8;
            if (roomRadiusMax*2 > camWidth){
                roomRadiusMax = camWidth/2;
            }
        } else {
            roomRadiusMax = mHeight / 8;
            if (roomRadiusMax*2 > camHeight){
                roomRadiusMax = camHeight/2;
            }
        }
        roomRadiusMin = roomRadiusMax / 4;
        if (roomRadiusMin < 2) {
            roomRadiusMin = 2;
        }

        makeRooms = !natural;
        if (makeRooms) {
            roomNums = rand.nextInt((
                    (mWidth/(roomRadiusMax * 2 + 3)) *
                            (mHeight / (roomRadiusMax * 2 + 3)))
                    - 2)
                    + 2;
            if (roomNums <= 2){
                roomNums = 2;
            }
        }

        RoomCenters = new Point[roomNums];

        mCellsCurr = new ObjectDestructible[mHeight][mWidth];
        mCellsNext = new ObjectDestructible[mHeight][mWidth];
        AvailableRoomCenters = new ArrayList<Point>(mHeight * mWidth);
        MakeAvailableRoomCenters();
        GenerateNewMap();
    }

    private void MakeAvailableRoomCenters() {
        for (int row = 3 + roomRadiusMax; row < mHeight - 3 - roomRadiusMax; row++) {
            for (int col = 3 + roomRadiusMax; col < mWidth - 3 - roomRadiusMax; col++) {
                Point temp = new Point(col, row);
                AvailableRoomCenters.add(temp);
            }
        }
    }

    public ObjectDestructible[][] GetCurrentMap() {
        return mCellsCurr;
    }

    public ObjectDestructible[][] GetSubMap(int offsetX, int offsetY, int width, int height) {
        ObjectDestructible[][] temp = new ObjectDestructible[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                temp[row][col] = mCellsCurr[row + offsetY][col + offsetX];
            }
        }
        return temp;
    }

    public int GetMapHeight() {
        return mHeight;
    }

    public int GetMapWidth() {
        return mWidth;
    }

    public ArrayList<Point> GetFloorPoints() {
        return FloorTiles;
    }

    public int GetNumEmptyPoints() {
        return numEmptyCells;
    }

    //Creates a new map through random generation and two refinement process.
    //Refinement one is to prevent large open areas.
    //refinement two is to create paths between areas.
    public Map GenerateNewMap() {
        // randomly initialize the map
        InitializeRandomMap();
        MakeBorders(0, 0, mWidth - 1, mHeight - 1);

        int refine = rand.nextInt(3) + 1;

        // refine the map for some number of generations
        for (int i = 0; i < refine; i++) {
            RefineMap(false);
        }
        for (int i = 0; i < refine; i++) {
            RefineMap(true);
        }

        MakeRooms();
        MakeCorridors();

        for (int row = 0; row < mHeight; row++) {
            for (int col = 0; col < mWidth; col++) {
                SetWallHealth(mCellsCurr[row][col]);
            }
        }
        GetEmptyFloorPoints();
        return this;
    }


    public boolean IsCellOpen(int cellx, int celly) {
        if (cellx >= mWidth || cellx < 0 || celly >= mHeight || celly < 0) {
            return false;
        }
        return (FindInArray(spaces, mCellsCurr[celly][cellx].getBitmap()));
    }

    public boolean IsCellWall(int cellx, int celly) {
        if (cellx >= mWidth || cellx < 0 || celly >= mHeight || celly < 0) {
            return true;
        }
        return (FindInArray(walls, mCellsCurr[celly][cellx].getBitmap()));
    }

    public void TakeAwayEmptyFloorTiles(int floorTile) {
        FloorTiles.remove(floorTile);
        numEmptyCells--;
    }

    public void harmWall(int cellx, int celly, int mining) {
        mCellsCurr[celly][cellx].hurt(mining);
        if (mCellsCurr[celly][cellx].getBitmap() != walls[0]) {
            mCellsCurr[celly][cellx].setBitMap(walls[0]);
        }
        if (mCellsCurr[celly][cellx].getHP() <= 0) {
            mCellsCurr[celly][cellx].setBitMap(spaces[rand.nextInt(spaces.length - 2)]);
            numEmptyCells++;
        }
    }
}
