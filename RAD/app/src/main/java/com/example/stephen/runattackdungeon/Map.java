package com.example.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.content.Context;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Stephen on 2018-01-16.
 */

public class Map {
    //array of available images for Spaces
    private Bitmap[] CellSpace;
    //Items to be placed on the map.
    private BaseObject[] Items;
    //Number of items in the current level;
    private int itemCount = 3;
    //MAX Number of items per level;
    private int maxItems = 5;
    //array of available images for walls
    private Bitmap[] CellWall;
    //the Array of Rooms
    private Point[] Rooms;
    //the percent of tiles that we want to be walls in the finalized map.
    private int WallPercent = 10;
    //the amount of desired rooms
    private int roomNums = 5;
    //the maximum size of rooms
    private int roomSize = 4;
    //the number of floor tiles
    private int numEmptyCells = 0;
    private int mX;
    private int mY;
    //the amount of tiles in each row
    private int mWidth;
    //the amount of tiles in each column
    private int mHeight;
    //random number, used for random number generation
    private Random rand = new Random();
    //the current map tileset
    private Bitmap[][] mCellsCurr;
    //the next generation of the map tileset.
    private Bitmap[][] mCellsNext;
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
                if (rand.nextLong() % 100 + 1 <= WallPercent)
                    mCellsCurr[row][col] = CellWall[rand.nextInt(CellWall.length)];
                else
                    mCellsCurr[row][col] = CellSpace[rand.nextInt(CellSpace.length)];
            }
        }
        MakeRooms();
    }

    private void MakeRooms() {
        for (int i = 0; i < Rooms.length; i++) {
            Rooms[i] = GetRandCell();
            int distributionX = rand.nextInt(roomSize) + 1;
            int distributionY = rand.nextInt(roomSize) + 1;
            for (int j = Rooms[i].x - distributionX; j < Rooms[i].x + distributionX; j++) {
                for (int k = Rooms[i].y - distributionY; k < Rooms[i].y + distributionY; k++) {
                    mCellsCurr[k][j] = CellSpace[rand.nextInt(CellSpace.length)];
                }
            }
        }
    }

    private Point GetRandCell() {
        Point thisPoint = new Point(
                rand.nextInt(mWidth - 2 * roomSize) + roomSize,
                rand.nextInt(mHeight - 2 * roomSize) + roomSize
        );
        return thisPoint;
    }

    private void MakeCorridors() {

    }

    private void Pause() {
        //Draw();
    }

    private void RefineMap(boolean preventLargeOpenAreas) {
        for (int row = 0; row < mHeight; row++) {
            for (int col = 0; col < mWidth; col++) {
                int A1Walls = NeighboringWallCount(col, row, 1);
                int A2Walls = NeighboringWallCount(col, row, 2);

                if (FindInArray(CellWall, mCellsCurr[row][col])) {
                    if (A1Walls >= 4)
                        mCellsNext[row][col] = CellWall[rand.nextInt(CellWall.length)];
                    else
                        mCellsNext[row][col] = CellSpace[rand.nextInt(CellSpace.length)];
                } else if (FindInArray(CellSpace, mCellsCurr[row][col])) {
                    if (A1Walls >= 5)
                        mCellsNext[row][col] = CellWall[rand.nextInt(CellWall.length)];
                    else if (preventLargeOpenAreas && A2Walls <= 1)
                        mCellsNext[row][col] = CellWall[rand.nextInt(CellWall.length)];
                    else
                        mCellsNext[row][col] = CellSpace[rand.nextInt(CellSpace.length)];
                }
            }
        }

        CopyArray(mCellsNext, mCellsCurr, mWidth, mHeight);
    }

    private void CopyArray(Bitmap[][] ImageArray1, Bitmap[][] ImageArray2, int width, int height) {
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
                else if (FindInArray(CellWall, mCellsCurr[row][col]))
                    walls++;
            }
        }

        return walls;
    }

    private void GetEmptyFloorPoints(){
        numEmptyCells = 0;
        FloorTiles = new ArrayList<Point>();
        for (int row = 0; row < mHeight; row++) {
            for (int col = 0; col < mWidth; col++) {
                if (FindInArray(CellSpace, mCellsCurr[row][col])){
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
    public Map(Context context, int startX, int startY, int Width, int Height) {
        mX = startX;
        mY = startY;

        CellSpace = new Bitmap[5];
        CellWall = new Bitmap[2];
        CellSpace[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.floor1);
        CellSpace[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.floor2);
        CellSpace[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.floor3);
        CellSpace[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.floor4);
        CellSpace[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.floor5);
        CellWall[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.wall1);
        CellWall[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.wall2);
        //40 x 40 = properties of all map tiles.
        mWidth = Width / CellSpace[0].getWidth();
        mHeight = Height / CellSpace[0].getHeight();

        itemCount = rand.nextInt(maxItems) + 1;
        Items = new BaseObject[itemCount];
        Rooms = new Point[roomNums];

        mCellsCurr = new Bitmap[mHeight][mWidth];
        mCellsNext = new Bitmap[mHeight][mWidth];

        GenerateNewMap();
    }

    //Creates a new map through random generation and two refinement process.
    //Refinement one is to prevent large open areas.
    //refinement two is to create paths between areas.
    public void GenerateNewMap() {
        // randomly initialize the map
        RandomizeMap();
        Pause();
        int refine = rand.nextInt(3) + 1;

        // refine the map for some number of generations
        for (int i = 0; i < refine; i++)
        {
            RefineMap(true);
            Pause();
        }
        for (int i = 0; i < refine + 1; i++)
        {
            RefineMap(false);
            Pause();
        }
//        MakeCorridors();
        GetEmptyFloorPoints();
    }

    public int GetX() {return mX;}

    public int GetY() {return mY;}

    public boolean IsCellOpen(int cellx, int celly) {
        if (cellx >= mWidth || cellx < 0 || celly >= mHeight || celly < 0){
            return false;
        }
        return (FindInArray(CellSpace, mCellsCurr[celly][cellx]));
    }

    public Bitmap[][] GetCurrentMap() {return mCellsCurr;}

    public int GetHeight() {return mHeight;}

    public int GetWidth() {return mWidth;}

    public int GetBitMapWidth() {return CellSpace[0].getWidth();}

    public int GetBitMapHeight() {return CellSpace[0].getHeight();}

    public ArrayList<Point> GetFloorPoints(){return FloorTiles;}
    public void TakeAwayEmptyFloorTiles(int floorTile){
        FloorTiles.remove(floorTile);
        numEmptyCells--;
    }
    public int GetNumEmptyPoints() {return numEmptyCells;}

}
