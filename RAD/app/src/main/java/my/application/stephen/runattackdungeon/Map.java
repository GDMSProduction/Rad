package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Integer.MAX_VALUE;
import static my.application.stephen.runattackdungeon.GameView.spaces;
import static my.application.stephen.runattackdungeon.GameView.walls;

/**
 * Created by Stephen on 2018-01-16.
 * Maps are the base layer of a generated dungeon: walls, floors, spaces.
 */

public class Map {
    //the number of floor tiles
    protected int numEmptyCells = 0;
    //random number, used for random number generation
    protected Random rand = new Random();
    //The points of every floor tile.
    protected ArrayList<Point> FloorTiles;
    //array of available images for Spaces
//    private Bitmap[] spaces;
    //array of available images for walls
//    private Bitmap[] walls;
    private int sturdywallHealth = 15;
    private int breakingwallHealth = 5;
    //the percent of tiles that we want to be walls in the finalized map.
    private int SpacePercent = 40;
    //the amount of tiles in each row
    private int mWidth;
    //the amount of tiles in each column
    private int mHeight;
    //the current map tileset
    //private ObjectDestructible[][] mCellsCurr;
    private ArrayList<ObjectDestructible>[][] mCellsCurr;
    //the next generation of the map tileset.
    //private ObjectDestructible[][] mCellsNext;
    private ArrayList<ObjectDestructible>[][] mCellsNext;

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

    //Given a group of tiles,
    //if a pic is in the group, return true.
    //if not, return false.
    private boolean FindInArray(Bitmap[] ImageArray, Bitmap pic) {
        for (Bitmap aImageArray : ImageArray) {
            if (pic == aImageArray) {
                return true;
            }
        }
        return false;
    }

    //fills the current map with randomized tiles
    private void InitializeMap() {
        for (int row = 0; row < mHeight; row++) {
            for (int col = 0; col < mWidth; col++) {
                mCellsCurr[row][col] = new ArrayList<>();
                mCellsCurr[row][col].add(
                        new ObjectDestructible(
                                new Point(col, row),
                                walls[0],
                                5));
                mCellsNext[row][col] = new ArrayList<>();
            }
        }
    }

    private void RandomizeMap() {
        for (int row = 0; row < mHeight; row++) {
            for (int col = 0; col < mWidth; col++) {
                long randomPercent = java.lang.Math.abs(rand.nextLong() % 100);
                if (randomPercent + 1 <= SpacePercent) {
                    setSpace(mCellsCurr, row, col, 5);
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
        setArray(mCellsNext, mCellsCurr, 0,0, mWidth, mHeight);
    }

    private void setArray(ArrayList<ObjectDestructible>[][] ArrayToImport, ArrayList<ObjectDestructible>[][] ArrayToSet, int startX, int startY, int width, int height) {
        for (int i = startX; i < width; i++) {
            for (int j = startY; j < height; j++) {
                ArrayToSet[j][i] = ArrayToImport[j][i];
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
        ArrayList<Point> someFloorTiles = new ArrayList<>();
        for (int row = start.y; row < start.y + height; row++) {
            for (int col = start.x; col < start.x + width; col++) {
                if (FindInArray(spaces, mCellsCurr[row][col].get(0).getBitmap())) {
                    someFloorTiles.add(new Point(col, row));
                }
            }
        }
        return someFloorTiles;
    }

    public int getNumEmptyCells() {
        return numEmptyCells;
    }

    //Mutators
    public void setSpace(ArrayList<ObjectDestructible>[][] array, int row, int col, int agedness) {
        switch (rand.nextInt(agedness)) {
            default:
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

    public void setVoid(ArrayList<ObjectDestructible>[][] array, int row, int col) {
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
        array[row][col].get(0).setCellType(ObjectDestructible.CellType.SturdyWall);
    }

    public void setSturdyWall(ArrayList<ObjectDestructible>[][] array, int row, int col, int Health) {
        if (Health < sturdywallHealth) {
            Health = sturdywallHealth;
        }
        array[row][col].get(0).setBitMap(walls[1]);
        array[row][col].get(0).setMaxHP(Health);
        array[row][col].get(0).setCellType(ObjectDestructible.CellType.SturdyWall);
    }

    //Creates a new map through random generation and two refinement process.
    //Refinement one is to prevent large open areas.
    //refinement two is to create paths between areas.
    public void generateNewMap(boolean natural) {
        // randomly initialize the map
        InitializeMap();
        RandomizeMap();

        if (natural) {
            int refine = /*rand.nextInt(3) + 1*/ 3;
            //refine the map for some number of generations
            for (int i = 0; i < refine; i++) {
                RefineMap(false);
            }
            for (int i = 0; i < refine; i++) {
                RefineMap(true);
            }
        }
        MakeBorders(0, 0, mWidth - 1, mHeight - 1, 1, ObjectDestructible.CellType.SturdyWall);
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

    public void giveNewPointToObject(@Nullable Room room, ObjectDestructible object) {
        if (room != null) {
            giveNewPointToObjectInRoom(object, room.getStartPoint(), room.getMapWidth(), room.getMapHeight());
        } else {
            int floorTilesIndex = rand.nextInt(FloorTiles.size());
            Point newPoint = FloorTiles.get(floorTilesIndex);

            removeObjectFromMap(object.getPoint(), object);
            object.setPoint(newPoint);
            addObjectToMap(object.getPoint(), object, true);

            removeEmptyFloorTiles(floorTilesIndex);
        }
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

    public void MakeBorders(int startX, int startY, int length, int height, int borderThickness, ObjectDestructible.CellType borderType) {
        switch (borderType) {
            case Border:
                int borderHealth = MAX_VALUE;
                for (int thickness = 0; thickness < borderThickness; thickness++) {
                    for (int i = 0; i <= length; i++) {
                        setSturdyWall(mCellsCurr, startY + thickness, startX + i, borderHealth);
                        setSturdyWall(mCellsCurr, startY + height - thickness, startX + i, borderHealth);
                    }
                    for (int i = 0; i <= height; i++) {
                        setSturdyWall(mCellsCurr, startY + i, startX + thickness, borderHealth);
                        setSturdyWall(mCellsCurr, startY + i, startX + length - thickness, borderHealth);
                    }
                }
                break;
            case Wall:
                for (int thickness = 0; thickness < borderThickness; thickness++) {
                    // horizontal  borders
                    for (int i = 0; i <= length; i++) {
                        //topLeft to bottomLeft
                        setWall(mCellsCurr, startY + thickness, startX + i);
                        //topRight to bottomRight
                        setWall(mCellsCurr, startY + height - thickness, startX + i);
                    }
                    // vertical borders
                    for (int i = 0; i <= height; i++) {
                        //topLeft to topRight
                        setWall(mCellsCurr, startY + i, startX + thickness);
                        //bottomLeft to bottomRight
                        setWall(mCellsCurr, startY + i, startX + length - thickness);
                    }
                }
                break;
            case SturdyWall:
                for (int thickness = 0; thickness < borderThickness; thickness++) {
                    for (int i = 0; i <= length; i++) {
                        setSturdyWall(mCellsCurr, startY + thickness, startX + i, sturdywallHealth);
                        setSturdyWall(mCellsCurr, startY + height - thickness, startX + i, sturdywallHealth);
                    }
                    for (int i = 0; i <= height; i++) {
                        setSturdyWall(mCellsCurr, startY + i, startX + thickness, sturdywallHealth);
                        setSturdyWall(mCellsCurr, startY + i, startX + length - thickness, sturdywallHealth);
                    }
                }
                break;
            case BreakingWall:
                for (int thickness = 0; thickness < borderThickness; thickness++) {
                    for (int i = 0; i <= length; i++) {
                        setBreakingWall(mCellsCurr, startY + thickness, startX + i);
                        setBreakingWall(mCellsCurr, startY + height - thickness, startX + i);
                    }
                    for (int i = 0; i <= height; i++) {
                        setBreakingWall(mCellsCurr, startY + i, startX + thickness);
                        setBreakingWall(mCellsCurr, startY + i, startX + length - thickness);
                    }
                }
                break;
            case Space:
                for (int thickness = 0; thickness < borderThickness; thickness++) {
                    for (int i = 0; i <= length; i++) {
                        setSpace(mCellsCurr, startY + thickness, startX + i, 5);
                        setSpace(mCellsCurr, startY + height - thickness, startX + i, 5);
                    }
                    for (int i = 0; i <= height; i++) {
                        setSpace(mCellsCurr, startY + i, startX + thickness, 5);
                        setSpace(mCellsCurr, startY + i, startX + length - thickness, 5);
                    }
                }
                break;
            case Void:
                for (int thickness = 0; thickness < borderThickness; thickness++) {
                    for (int i = 0; i <= length; i++) {
                        setVoid(mCellsCurr, startY + thickness, startX + i);
                        setVoid(mCellsCurr, startY + height - thickness, startX + i);
                    }
                    for (int i = 0; i <= height; i++) {
                        setVoid(mCellsCurr, startY + i, startX + thickness);
                        setVoid(mCellsCurr, startY + i, startX + length - thickness);
                    }
                }
                break;
        }

    }
}
