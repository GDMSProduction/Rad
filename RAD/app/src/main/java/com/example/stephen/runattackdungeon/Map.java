package com.example.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.content.Context;

import java.util.Random;
/**
 * Created by Stephen on 2018-01-16.
 */

public class Map
{
    //array of available images for Spaces
    private Bitmap[] CellSpace;
    //array of available images for walls
    private Bitmap[] CellWall;
    //the percent of tiles that we want to be walls in the finalized map.
    private int WallPercent = 20;
    private int mX;
    private int mY;
    //the amount of tiles in each row
    private int mWidth;
    //the amount of tiles in each column
    private int mHeight;
    //the amount of desired rooms
    private int roomNums = 3;
    //random number, used for random number generation
    private Random rand = new Random();
    //the current map tileset
    private Bitmap[][] mCellsCurr;
    //the next generation of the map tileset.
    private Bitmap[][] mCellsNext;
    private Canvas canvas;
    private Paint paint;
    private GameView gameView;
    //Given a group of tiles,
        //if a pic is in the group, return true.
        //if not, return false.
    private boolean FindInArray(Bitmap[] ImageArray, Bitmap pic){
        int length = ImageArray.length;
        for (int i = 0; i < length; i++)
        {
            if (pic == ImageArray[i])
            {
                return true;
            }
        }
        return false;
    }
    //Creates a new map through random generation and two refinement process.
    //Refinement one is to prevent large open areas.
    //refinement two is to create paths between areas.
    public void GenerateNewMap() {
        // randomly initialize the map
        RandomizeMap();
        Pause();

        // refine the map for some number of generations
        for (int i = 0; i < 1; i++)
        {
            RefineMap(true);
            Pause();
        }
        for (int i = 0; i < 1; i++)
        {
            RefineMap(false);
            Pause();
        }
    }
    //fills the current map with randomized tiles
    private void RandomizeMap(){
        // innards of map
        for (int row = 0; row < mHeight; row++)
        {
            for (int col = 0; col < mWidth; col++)
            {
                if (rand.nextLong() % 100 + 1 <= WallPercent)
                    mCellsCurr[row][col] = CellWall[rand.nextInt(CellWall.length)];
                else
                    mCellsCurr[row][col] = CellSpace[rand.nextInt(CellSpace.length)];
            }
        }
        ClearMiddleRow();
        //ClearRooms();


//        // horizontal borders
//        for (int i = 0; i < mWidth; i++)
//        {
//            mCellsCurr[0][i] = CellWall[rand.nextInt(CellWall.length)];
//            mCellsCurr[mHeight-1][i] = CellWall[rand.nextInt(CellWall.length)];
//        }
//
//        // vertical borders
//        for (int i = 0; i < mHeight; i++)
//        {
//            mCellsCurr[i][0] = CellWall[rand.nextInt(CellWall.length)];
//            mCellsCurr[i][mWidth-1] = CellWall[rand.nextInt(CellWall.length)];
//        }
    }

    private void ClearMiddleRow() {
        // clear middle row
        int middle = mHeight / 2;
        for (int i = 1; i < mWidth-1; i++)
        {
            mCellsCurr[middle-1][i] = CellSpace[rand.nextInt(CellSpace.length)];
            mCellsCurr[middle][i] = CellSpace[rand.nextInt(CellSpace.length)];
            mCellsCurr[middle+1][i] = CellSpace[rand.nextInt(CellSpace.length)];
        }
    }
//    private void ClearRooms(){
//        for(int i = 0; i < roomNums; i++){
//            int[] Point = GetRandCell();
//        }
//    }
//    private Point GetRandCell(){
//        int pointX = rand.nextInt();
//        int pointY = rand.nextInt();
//        return
//    }

    private void Pause() {
        //Draw();
    }
    private void RefineMap(boolean preventLargeOpenAreas){
        for (int row = 0; row < mHeight; row++)
        {
            for (int col = 0; col < mWidth; col++)
            {
                int A1Walls = NeighboringWallCount(col, row, 1);
                int A2Walls = NeighboringWallCount(col, row, 2);

                if (FindInArray(CellWall,mCellsCurr[row][col]))
                {
                    if (A1Walls >= 4)
                        mCellsNext[row][col] = CellWall[rand.nextInt(CellWall.length)];
                    else
                        mCellsNext[row][col] = CellSpace[rand.nextInt(CellSpace.length)];
                }
                else if (FindInArray(CellSpace, mCellsCurr[row][col]))
                {
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
    private void CopyArray(Bitmap[][] ImageArray1, Bitmap[][] ImageArray2, int width, int height){
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                ImageArray2[j][i] = ImageArray1[j][i];
            }
        }
    }
    private int NeighboringWallCount(int cellx, int celly, int dist) {
        int walls = 0;

        for (int row = celly - dist; row <= celly + dist; row++)
        {
            for (int col = cellx - dist; col <= cellx + dist; col++)
            {
                if (row == celly && col == cellx)
                    continue;
                else if (row < 0 || col < 0 || row >= mHeight || col >= mWidth)
                    walls++;
                else if (FindInArray(CellWall,mCellsCurr[row][col]))
                    walls++;
            }
        }

        return walls;
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

        mCellsCurr = new Bitmap[mHeight][mWidth];
        mCellsNext = new Bitmap[mHeight][mWidth];

        GenerateNewMap();
    }
    public int GetX() { return mX; }
    public int GetY() { return mY; }
    public boolean IsCellOpen(int cellx, int celly) {
        return (FindInArray(CellSpace, mCellsCurr[celly][cellx]));
    }
    public Bitmap[][] GetCurrentMap(){return mCellsCurr;}
    public int GetHeight() {return mHeight;}
    public int GetWidth() {return mWidth;}
    public int GetBitMapWidth() {return CellSpace[0].getWidth();}
    public int GetBitMapHeight() {return CellSpace[0].getHeight();}

    public void Draw() {
        for (int row = 0; row < mHeight; row++)
        {
            for (int col = 0; col < mWidth; col++)
            {
                canvas.drawBitmap(mCellsCurr[row][col],
                        col * CellSpace[0].getWidth(),
                        row * CellSpace[0].getHeight(),
                        paint);
            }
        }
    }
}
//class Point{
//    public int x;
//    public int y;
//
//    public Point(){}
//    public Point(int newX, int newY){
//        x = newX;
//        y = newY;
//    }
//    public int GetX(){return x;}
//    public int GetY()
//}
