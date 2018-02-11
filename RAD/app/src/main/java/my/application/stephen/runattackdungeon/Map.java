package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Stephen on 2018-01-16.
 */

public class Map {
    //array of available images for Spaces
    private Bitmap[] CellSpace;
    //array of available images for walls
    private Bitmap[] CellWall;
    private int sturdywallHealth = 15;
    private int breakingwallHealth = 5;
    //the Array of the center points of Rooms
    private Point[] RoomCenters;
    //the percent of tiles that we want to be walls in the finalized map.
    private int WallPercent = 10;
    //the amount of desired rooms
    private int roomNums = 2;
    //the maximum radius of rooms
    private int roomRadiusMax = 4;
    //the number of floor tiles
    private int numEmptyCells = 0;
    //the amount of tiles in each row
    private int mWidth;
    //the amount of tiles in each column
    private int mHeight;
    //random number, used for random number generation
    protected Random rand = new Random();
    //the current map tileset
    private DestructableObject[][] mCellsCurr;
    //the next generation of the map tileset.
    private DestructableObject[][] mCellsNext;
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
    private void RandomizeMap() {
        // innards of map
        for (int row = 0; row < mHeight; row++) {
            for (int col = 0; col < mWidth; col++) {
                Point tempPoint = new Point(0, 0);
                DestructableObject temp = new DestructableObject(tempPoint, CellWall[0], 5);
                temp.SetPoint(col, row);
                mCellsCurr[row][col] = temp;
                if (rand.nextLong() % 100 + 1 <= WallPercent) {
                    SetWall(row, col);
                } else
                    mCellsCurr[row][col].SetBitMap(CellSpace[rand.nextInt(CellSpace.length - 1)]);
            }
        }
        int debugbreakpoint = 0;
    }

    private void SetWall(int row, int col) {
        mCellsCurr[row][col].SetBitMap(CellWall[rand.nextInt(CellWall.length)]);
    }

    private void SetWallHealth(DestructableObject destructableObject) {
        if (destructableObject.GetBitmap() == CellWall[1])
            destructableObject.SetMaxHP(sturdywallHealth);
        else if (destructableObject.GetBitmap() == CellWall[0])
            destructableObject.SetMaxHP(breakingwallHealth);
    }

    private void MakeRooms() {
        for (int i = 0; i < RoomCenters.length; i++) {
            RoomCenters[i] = GetRandRoomCell();
            int distributionX = rand.nextInt(roomRadiusMax) + 1;
            int distributionY = rand.nextInt(roomRadiusMax) + 1;
            for (int j = RoomCenters[i].x - distributionX; j < RoomCenters[i].x + distributionX; j++) {
                for (int k = RoomCenters[i].y - distributionY; k < RoomCenters[i].y + distributionY; k++) {
                    mCellsCurr[k][j].SetBitMap(CellSpace[rand.nextInt(CellSpace.length - 1)]);
                }
            }
        }
    }

    private Point GetRandRoomCell() {
        Point thisPoint = new Point(
                rand.nextInt(mWidth - 2 * roomRadiusMax) + roomRadiusMax,
                rand.nextInt(mHeight - 2 * roomRadiusMax) + roomRadiusMax
        );
        return thisPoint;
    }

    private void MakeCorridors() {
        double high = 0;
        //find the two points farthest apart.
        for (int i = 0; i < RoomCenters.length - 1; i++) {
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
                            mCellsCurr[lesserY][lesserX + j].SetBitMap(CellSpace[rand.nextInt(CellSpace.length - 1)]);
                        }
                        for (int k = 0; k <= corridorHeight; k++) {
                            mCellsCurr[lesserY + k][lesserX].SetBitMap(CellSpace[rand.nextInt(CellSpace.length - 1)]);
                        }
                        break;
                    case 1:
                        for (int j = 0; j <= corridorWidth; j++) {
                            mCellsCurr[greaterY][lesserX + j].SetBitMap(CellSpace[rand.nextInt(CellSpace.length - 1)]);
                        }
                        for (int k = 0; k <= corridorHeight; k++) {
                            mCellsCurr[lesserY + k][greaterX].SetBitMap(CellSpace[rand.nextInt(CellSpace.length - 1)]);
                        }
                        break;
                }
            } else if (RoomCenters[i].x > RoomCenters[i + 1].x && RoomCenters[i].y > RoomCenters[i + 1].y ||
                    RoomCenters[i].x < RoomCenters[i + 1].x && RoomCenters[i].y < RoomCenters[i + 1].y) {
                switch (rand.nextInt(2)) {
                    default:
                    case 0:
                        for (int j = 0; j <= corridorWidth; j++) {
                            mCellsCurr[lesserY][lesserX + j].SetBitMap(CellSpace[rand.nextInt(CellSpace.length - 1)]);
                        }
                        for (int k = 0; k <= corridorHeight; k++) {
                            mCellsCurr[lesserY + k][greaterX].SetBitMap(CellSpace[rand.nextInt(CellSpace.length - 1)]);
                        }
                        break;
                    case 1:
                        for (int j = 0; j <= corridorWidth; j++) {
                            mCellsCurr[greaterY][lesserX + j].SetBitMap(CellSpace[rand.nextInt(CellSpace.length - 1)]);
                        }
                        for (int k = 0; k <= corridorHeight; k++) {
                            mCellsCurr[lesserY + k][lesserX].SetBitMap(CellSpace[rand.nextInt(CellSpace.length - 1)]);
                        }
                        break;
                }
            }
        }
    }

    private void MakeBorders() {
        // horizontal borders
        for (int i = 0; i < mWidth; i++) {
            SetWall(0, i);
            SetWall(mHeight - 1, i);
        }

        // vertical borders
        for (int i = 0; i < mHeight; i++) {
            SetWall(i, 0);
            SetWall(i, mWidth - 1);
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

                if (FindInArray(CellWall, mCellsCurr[row][col].GetBitmap())) {
                    if (A1Walls >= 4)
                        SetWall(row, col);
                    else
                        mCellsNext[row][col].SetBitMap(CellSpace[rand.nextInt(CellSpace.length - 1)]);
                } else if (FindInArray(CellSpace, mCellsCurr[row][col].GetBitmap())) {
                    if (A1Walls >= 5)
                        SetWall(row, col);
                    else if (preventLargeOpenAreas && A2Walls <= 1)
                        SetWall(row, col);
                    else
                        mCellsNext[row][col].SetBitMap(CellSpace[rand.nextInt(CellSpace.length - 1)]);
                }
            }
        }
        MakeCorridors();

        CopyArray(mCellsNext, mCellsCurr, mWidth, mHeight);
    }

    private void CopyArray(DestructableObject[][] ImageArray1, DestructableObject[][] ImageArray2, int width, int height) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                ImageArray2[j][i] = ImageArray1[j][i];
            }
        }
    }

    private int NeighboringWallCount(int x, int y, int wallDistribution) {
        int walls = 0;

        for (int row = y - wallDistribution; row <= y + wallDistribution; row++) {
            for (int col = x - wallDistribution; col <= x + wallDistribution; col++) {
                if (row == y && col == x)
                    continue;
                else if (row < 0 || col < 0 || row >= mHeight || col >= mWidth)
                    walls++;
                else if (FindInArray(CellWall, mCellsCurr[row][col].GetBitmap()))
                    walls++;
            }
        }

        return walls;
    }

    private void GetEmptyFloorPoints() {
        numEmptyCells = 0;
        FloorTiles = new ArrayList<Point>();
        for (int row = 0; row < mHeight; row++) {
            for (int col = 0; col < mWidth; col++) {
                if (FindInArray(CellSpace, mCellsCurr[row][col].GetBitmap())) {
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
    public Map(Bitmap[] spaces, Bitmap[] walls, int Width, int Height) {

        CellSpace = new Bitmap[6];
        CellSpace = spaces;
        CellWall = new Bitmap[2];
        CellWall = walls;
        //40 x 40 = properties of all map tiles.
        mWidth = (Width / CellSpace[0].getWidth());
        mHeight = (Height / CellSpace[0].getHeight());

        RoomCenters = new Point[roomNums];

        mCellsCurr = new DestructableObject[mHeight][mWidth];
        mCellsNext = new DestructableObject[mHeight][mWidth];

        GenerateNewMap();
    }

    public DestructableObject[][] GetCurrentMap() {
        return mCellsCurr;
    }

    public DestructableObject[][] GetSubMap(int offsetX, int offsetY, int width, int height) {
        DestructableObject[][] temp = new DestructableObject[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                temp[row][col] = mCellsCurr[row + offsetY][col + offsetX];
            }
        }
        return temp;
    }

    public int GetHeight() {
        return mHeight;
    }

    public int GetWidth() {
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
        RandomizeMap();
        MakeRooms();
        MakeCorridors();
        MakeBorders();


        int refine = rand.nextInt(3) + 1;

        // refine the map for some number of generations
        for (int i = 0; i < refine; i++) {
            RefineMap(true);
        }
        for (int i = 0; i < refine + 1; i++) {
            RefineMap(false);
        }

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
        return (FindInArray(CellSpace, mCellsCurr[celly][cellx].GetBitmap()));
    }

    public void TakeAwayEmptyFloorTiles(int floorTile) {
        FloorTiles.remove(floorTile);
        numEmptyCells--;
    }

    public void harmWall(int cellx, int celly, int mining) {
        if (cellx >= mWidth || cellx < 0 || celly >= mHeight || celly < 0) {
            return;
        }

        mCellsCurr[celly][cellx].Hurt(mining);
        if (mCellsCurr[celly][cellx].GetBitmap() != CellWall[0]) {
            mCellsCurr[celly][cellx].SetBitMap(CellWall[0]);
        }
        if (mCellsCurr[celly][cellx].GetHP() <= 0) {
            mCellsCurr[celly][cellx].SetBitMap(CellSpace[rand.nextInt(CellSpace.length - 2)]);
            numEmptyCells++;
        }
    }

}
