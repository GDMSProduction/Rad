package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.Random;

import static my.application.stephen.runattackdungeon.GameView.spaces;
import static my.application.stephen.runattackdungeon.GameView.walls;

/**
 * Created by Stephen on 2018-01-16.
 * Maps are the base layer of a generated dungeon: walls, floors, spaces.
 */

public class Map {
    //array of available images for Spaces
//    private Bitmap[] spaces;
    //array of available images for walls
//    private Bitmap[] walls;
    private int sturdywallHealth = 15;
    private int breakingwallHealth = 5;
    //the percent of tiles that we want to be walls in the finalized map.
    private int SpacePercent = 40;
    //the number of floor tiles
    protected int numEmptyCells = 0;
    //the amount of tiles in each row
    private int mWidth;
    //the amount of tiles in each column
    private int mHeight;
    //random number, used for random number generation
    protected Random rand = new Random();
    //the current map tileset
    //private ObjectDestructible[][] mCellsCurr;
    private ArrayList<ObjectDestructible>[][] mCellsCurr;
    //the next generation of the map tileset.
    //private ObjectDestructible[][] mCellsNext;
    private ArrayList<ObjectDestructible>[][] mCellsNext;
    //The points of every floor tile.
    protected ArrayList<Point> FloorTiles;

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
    private void InitializeMap() {
        // innards of map
        for (int row = 0; row < mHeight; row++) {
            for (int col = 0; col < mWidth; col++) {
                //Set the lists.
                mCellsCurr[row][col] = new ArrayList<ObjectDestructible>(2);
                mCellsNext[row][col] = new ArrayList<ObjectDestructible>(2);

                //set the map
                Point tempPoint = new Point(col, row);
                ObjectDestructible temp = new ObjectDestructible(tempPoint, walls[0], 5);
                mCellsCurr[row][col].add(temp);

                //randomize the map.
                long randomPercent = java.lang.Math.abs(rand.nextLong() % 100);
                if (randomPercent + 1 <= SpacePercent) {
                    setSpace(mCellsCurr, row, col, spaces.length - 1);
                } else {
                    setWall(mCellsCurr, row, col);
                }
            }
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

                if (FindInArray(walls, mCellsCurr[row][col].get(0).getBitmap())) {
                    if (A1Walls >= 4)
                        setWall(mCellsNext, row, col);
                    else
                        setSpace(mCellsNext, row, col, spaces.length - 1);
                } else if (FindInArray(spaces, mCellsCurr[row][col].get(0).getBitmap())) {
                    if (A1Walls >= 5)
                        setWall(mCellsNext, row, col);
                    else if (preventLargeOpenAreas && A2Walls <= 1)
                        setWall(mCellsNext, row, col);
                    else
                        setSpace(mCellsNext, row, col, spaces.length - 1);
                }
            }
        }
        //MakeCorridors();

        CopyArray(mCellsNext, mCellsCurr, mWidth, mHeight);
    }

    private void CopyArray(ArrayList<ObjectDestructible>[][] ImageArray1, ArrayList<ObjectDestructible>[][] ImageArray2, int width, int height) {
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
                else if (FindInArray(walls, mCellsCurr[row][col].get(0).getBitmap()))
                    wallCount++;
            }
        }

        return wallCount;
    }


    //////////////////////////////////////////////////////////////////////////////////////////
    //
    //////////////////////////////////////////////////////////////////////////////////////////
    public Map(int Width, int Height, int spacePercent, boolean natural) {
        mWidth = Width;
        mHeight = Height;
        SpacePercent = spacePercent;
        mCellsCurr = new ArrayList[mHeight][mWidth];
        mCellsNext = new ArrayList[mHeight][mWidth];

        generateNewMap(natural);
    }

    //Accessors
    public ArrayList<ObjectDestructible>[][] getCurrentMap() {
        return mCellsCurr;
    }

    public int getMapHeight() {
        return mHeight;
    }

    public int getMapWidth() {
        return mWidth;
    }

    public ArrayList<Point> getSomeFloorPoints(Point start, int width, int height) {
        ArrayList<Point> someFloorTiles = new ArrayList<Point>();
        for (int row = start.y; row < start.y + height; row++) {
            for (int col = start.x; col < start.x + width; col++) {
                if (FindInArray(spaces, mCellsCurr[row][col].get(0).getBitmap())) {
                    someFloorTiles.add(new Point(col, row));
                }
            }
        }
        return someFloorTiles;
    }

    public ArrayList<Point> makeAvailablePoints(int Height, int Width) {
        numEmptyCells = 0;
        FloorTiles = new ArrayList<Point>();
        for (int row = 0; row < Height; row++) {
            for (int col = 0; col < Width; col++) {
                if (getOtherCellType(col, row) == ObjectDestructible.CellType.Space) {
                    addEmptyFloorTile(col, row);
                }
            }
        }
        return FloorTiles;
    }

    public int getNumEmptyCells() {
        return numEmptyCells;
    }

    //Mutators
    public void setSpace(ArrayList<ObjectDestructible>[][] array, int row, int col, int agedness) {
        switch (rand.nextInt(agedness)) {
            case 0:
                array[row][col].get(0).setBitMap(spaces[0]);
                array[row][col].get(0).setCellType(ObjectDestructible.CellType.Space);
                break;
            case 1:
                array[row][col].get(0).setBitMap(spaces[1]);
                array[row][col].get(0).setCellType(ObjectDestructible.CellType.Space);
                break;
            case 2:
                array[row][col].get(0).setBitMap(spaces[2]);
                array[row][col].get(0).setCellType(ObjectDestructible.CellType.Space);
                break;
            case 3:
                array[row][col].get(0).setBitMap(spaces[3]);
                array[row][col].get(0).setCellType(ObjectDestructible.CellType.Space);
                break;
            case 4:
                setGrassySpace(array, row, col);
                break;
        }
    }
    public void setGrassySpace(ArrayList<ObjectDestructible>[][] array, int row, int col) {
        array[row][col].get(0).setBitMap(spaces[4]);
        array[row][col].get(0).setCellType(ObjectDestructible.CellType.Space);
    }
    public void setVoidSpace(ArrayList<ObjectDestructible>[][] array, int row, int col) {
        array[row][col].get(0).setBitMap(spaces[5]);
        array[row][col].get(0).setCellType(ObjectDestructible.CellType.Void);
    }

    public void setWall(ArrayList<ObjectDestructible>[][] array, int row, int col) {
        switch (rand.nextInt(2)) {
            case 0:
                setBreakingWall(array, row, col);
                break;
            case 1:
                setSturdyWall(array, row, col, -1);
                break;
        }
    }
    public void setBreakingWall(ArrayList<ObjectDestructible>[][] array, int row, int col) {
        array[row][col].get(0).setBitMap(walls[0]);
        array[row][col].get(0).setMaxHP(breakingwallHealth);
        array[row][col].get(0).setCellType(ObjectDestructible.CellType.Wall);
    }
    public void setSturdyWall(ArrayList<ObjectDestructible>[][] array, int row, int col, int Health) {
        if (Health < 0) {
            Health = sturdywallHealth;
        }
        array[row][col].get(0).setBitMap(walls[1]);
        array[row][col].get(0).setMaxHP(sturdywallHealth);
        array[row][col].get(0).setCellType(ObjectDestructible.CellType.Wall);
    }

    //Creates a new map through random generation and two refinement process.
    //Refinement one is to prevent large open areas.
    //refinement two is to create paths between areas.
    public Map generateNewMap(boolean natural) {
        // randomly initialize the map
        InitializeMap();
        MakeBorders(0, 0, mWidth - 1, mHeight - 1);

        if (natural == true) {
            int refine = /*rand.nextInt(3) + 1*/ 3;
            //refine the map for some number of generations
            for (int i = 0; i < refine; i++) {
                RefineMap(false);
            }
            for (int i = 0; i < refine; i++) {
                RefineMap(true);
            }
        }
        return this;
    }


    public boolean isCellOpen(int cellx, int celly) {
        if (cellx >= mWidth || cellx < 0 || celly >= mHeight || celly < 0) {
            return false;
        }
        return (FindInArray(spaces, mCellsCurr[celly][cellx].get(0).getBitmap()));
    }

    public boolean isCellWall(int cellx, int celly) {
        if (cellx >= mWidth || cellx < 0 || celly >= mHeight || celly < 0) {
            return true;
        }
        return (FindInArray(walls, mCellsCurr[celly][cellx].get(0).getBitmap()));
    }

    public ObjectDestructible.CellType getOtherCellType(int cellx, int celly) {
        if (cellx >= getMapWidth() || cellx < 0 || celly >= getMapHeight() || celly < 0) {
            return ObjectDestructible.CellType.Wall;
        }
        return mCellsCurr[celly][cellx].get(
                mCellsCurr[celly][cellx].size() - 1
        ).getCellType();
    }

    public void removeEmptyFloorTiles(int floorTile) {
        FloorTiles.remove(floorTile);
        numEmptyCells--;
    }
    public void findAndRemoveEmptyFloorTiles(Point point) {
        for (int i = 0; i < FloorTiles.size(); i++) {
            Point temp = FloorTiles.get(i);
            if (temp.x == point.x && temp.y == point.y) {
                removeEmptyFloorTiles(i);
            }
        }
    }

    public void addEmptyFloorTile(int X, int Y) {
        FloorTiles.add(new Point(X, Y));
        numEmptyCells++;
    }

    public void addObjectToMap(Point point, ObjectDestructible object, boolean Distribute) {
//        if (Distribute == true){
//            getCurrentMap()[point.y][point.x].add(object);
//        } else {
        getCurrentMap()[point.y][point.x].add(object);
//        }
    }

    public void removeObjectFromMap(Point point, ObjectDestructible object) {
        for (int i = 0; i < getCurrentMap()[point.y][point.x].size(); i++) {
            if (object == getCurrentMap()[point.y][point.x].get(i)) {
                getCurrentMap()[point.y][point.x].remove(i);
                break;
            }
        }
    }

    public void giveNewPointToObject(ObjectDestructible object) {
        int floorTilesIndex = rand.nextInt(FloorTiles.size());
        Point newPoint = FloorTiles.get(floorTilesIndex);

        removeObjectFromMap(object.getPoint(), object);
        object.setPoint(newPoint);
        addObjectToMap(object.getPoint(), object, true);

        removeEmptyFloorTiles(floorTilesIndex);
    }

    public void giveNewPointToObjectInRoom(ObjectDestructible object, Point start, int width, int height) {
        ArrayList<Point> someFloorTiles = getSomeFloorPoints(start, width, height);
        if (someFloorTiles.size() > 0) {
            int floorTilesIndex = rand.nextInt(someFloorTiles.size());
            Point newPoint = someFloorTiles.get(floorTilesIndex);

            removeObjectFromMap(object.getPoint(), object);
            object.setPoint(newPoint);
            addObjectToMap(object.getPoint(), object, true);

            findAndRemoveEmptyFloorTiles(newPoint);
        }
    }

    public void MakeBorders(int startX, int startY, int length, int height) {
        // horizontal  borders
        for (int i = 0; i <= length; i++) {
            //topLeft to bottomLeft
            setWall(mCellsCurr, startY, startX + i);
            //topRight to bottomRight
            setWall(mCellsCurr, startY + height, startX + i);
        }

        // vertical borders
        for (int i = 0; i <= height; i++) {
            //topLeft to topRight
            setWall(mCellsCurr, startY + i, startX);
            //bottomLeft to bottomRight
            setWall(mCellsCurr, startY + i, startX + length);
        }
    }
}
