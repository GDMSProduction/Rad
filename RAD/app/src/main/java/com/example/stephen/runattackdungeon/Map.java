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
    //Picture floor1 = new Picture();
    private Bitmap bm;
    private Bitmap[] CellSpace;
    private Bitmap[] CellWall;
    private int WallPercent = 40;

    private int mX;
    private int mY;
    private int mWidth;
    private int mHeight;
    private Random rand = new Random();

    private Bitmap[][] mCellsCurr;
    private Bitmap[][] mCellsNext;
    private Dungeon mTheDungeon;
    private Canvas backGround;
    private Paint painter;

    private boolean FindInArray(Bitmap[] ImageArray, Bitmap pic){
        for (int i = 0; i < ImageArray.length; i++)
        {
            if (pic == ImageArray[i])
            {
                return true;
            }
        }
        return false;
    }
    private void GenerateNewMap() {
        // randomly initialize the map
        RandomizeMap();
        Pause();

        // refine the map for some number of generations
        for (int i = 0; i < 4; i++)
        {
            RefineMap(true);
            Pause();
        }
        for (int i = 0; i < 3; i++)
        {
            RefineMap(false);
            Pause();
        }
    }
    private void RandomizeMap(){
        // innards of map
        for (int row = 1; row < mHeight-1; row++)
        {
            for (int col = 1; col < mWidth-1; col++)
            {
                if (rand.nextLong() % 100 + 1 <= WallPercent)
                    mCellsCurr[row][col] = CellWall[rand.nextInt(CellWall.length)];
                else
                    mCellsCurr[row][col] = CellSpace[rand.nextInt(CellSpace.length)];
            }
        }

        // clear middle row
        int middle = mHeight / 2;
        for (int i = 1; i < mWidth-1; i++)
        {
            mCellsCurr[middle-1][i] = CellSpace[rand.nextInt(CellSpace.length)];
            mCellsCurr[middle][i] = CellSpace[rand.nextInt(CellSpace.length)];
            mCellsCurr[middle+1][i] = CellSpace[rand.nextInt(CellSpace.length)];
        }

        // horizontal borders
        for (int i = 0; i < mWidth; i++)
        {
            mCellsCurr[0][i] = CellWall[rand.nextInt(CellWall.length)];
            mCellsCurr[mHeight-1][i] = CellWall[rand.nextInt(CellWall.length)];
        }

        // vertical borders
        for (int i = 0; i < mHeight; i++)
        {
            mCellsCurr[i][0] = CellWall[rand.nextInt(CellWall.length)];
            mCellsCurr[i][mWidth-1] = CellWall[rand.nextInt(CellWall.length)];
        }
    }
    private void Pause() {
        //Draw();
        //Console.SetCursorPosition(0, 0);
        //Console.ReadLine();
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

        CopyArray(mCellsNext, mCellsCurr, mWidth * mHeight);
    }
    private void CopyArray(Bitmap[][] ImageArray1, Bitmap[][] ImageArray2, long length){
        for (int i = 0; i < length; i++)
        {
            ImageArray2[i] = ImageArray1[i];
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
        mWidth = Width;
        mHeight = Height;
        CellSpace = new Bitmap[5];
        CellWall = new Bitmap[2];
        CellSpace[0] = BitmapFactory.decodeResource(context.getResources(), R.mipmap.floor1);
        CellSpace[1] = BitmapFactory.decodeResource(context.getResources(), R.mipmap.floor2);
        CellSpace[2] = BitmapFactory.decodeResource(context.getResources(), R.mipmap.floor3);
        CellSpace[3] = BitmapFactory.decodeResource(context.getResources(), R.mipmap.floor4);
        CellSpace[4] = BitmapFactory.decodeResource(context.getResources(), R.mipmap.floor5);
        CellWall[0] = BitmapFactory.decodeResource(context.getResources(), R.mipmap.wall1);
        CellWall[1] = BitmapFactory.decodeResource(context.getResources(), R.mipmap.wall2);


        mCellsCurr = new Bitmap[Height][Width];
        mCellsNext = new Bitmap[Height][Width];

        GenerateNewMap();
    }
    public int GetX() { return mX; }
    public int GetY() { return mY; }
    public boolean IsCellOpen(int cellx, int celly) {
        return (FindInArray(CellSpace, mCellsCurr[celly][cellx]));
    }
    public void Draw() {
        for (int row = 0; row < mHeight; row++)
        {
            for (int col = 0; col < mWidth; col++)
            {
                backGround.drawBitmap(mCellsCurr[row][col], col + CellSpace[0].getWidth(), row + CellSpace[0].getHeight(), null);

                //Console.SetCursorPosition(col + mX, row + mY);
                //Console.Write(mCellsCurr[row][col]);
            }
        }
    }

}
