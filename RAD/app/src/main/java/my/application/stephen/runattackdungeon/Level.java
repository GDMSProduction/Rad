package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by Stephen on 2018-01-25.
 */

public class Level extends Map {

    private int mBitMapHeight;
    private int mBitMapWidth;

    private Bitmap[] Bitmaps;
    //The Clutter
    private int maxClutter = 5;
    private ArrayList<Clutter> clutter = new ArrayList<Clutter>(maxClutter);
    //The Stairs
    private BaseObject stairsUp;
    private BaseObject stairsDown;
    //the enemies
    private int maxEnemies = 5;
    private ArrayList<Creature> enemies = new ArrayList<Creature>(maxEnemies);

    public int getNewEmptyPoint() {
        return rand.nextInt(GetNumEmptyPoints());
    }

    private void GetNewClutter() {
        clutter.clear();
        int newSize = rand.nextInt(maxClutter) + 1;
        clutter = new ArrayList<Clutter>(newSize);
        for (int i = 0; i < newSize; i++) {
            int clutterVal = 10;
            Bitmap clutterBitmap = Bitmaps[0];
            Point clutterPoint = new Point(0, 0);

            Clutter temp = new Clutter(clutterVal, clutterPoint, clutterBitmap);
            switch (rand.nextInt(3)) {
                case 0:
                    //barrel
                    temp.SetBitMap(Bitmaps[0]);
                    temp.SetValue(10);
                    break;
                case 1:
                    //chest
                    temp.SetBitMap(Bitmaps[1]);
                    temp.SetValue(30);
                    break;
                default:
                case 2:
                    //rock
                    temp.SetBitMap(Bitmaps[2]);
                    temp.SetValue(0);
                    break;
            }
            int newPoint = getNewEmptyPoint();
            temp.SetPoint(GetFloorPoints().get(newPoint));
            TakeAwayEmptyFloorTiles(newPoint);
            clutter.add(temp);
        }
    }

    private void createEnemies() {
        //Create Enemies
        enemies.clear();
        int newSize = rand.nextInt(maxEnemies) + 1;
        enemies = new ArrayList<Creature>(newSize);
        for (int i = 0; i < newSize; i++) {
            int hPMax = 3;
            float defMax = 0.3f;
            Bitmap enemyBitmap = Bitmaps[5];
            Point enemyPoint = new Point(0, 0);

            Creature temp;
            switch (rand.nextInt(4)) {
                default:
                case 0:
                    temp = new Creature(enemyPoint, enemyBitmap, hPMax, defMax);
                    break;
                case 1:
                    hPMax = 5;
                    defMax = 0.5f;
                    enemyBitmap = Bitmaps[6];
                    temp = new Creature(enemyPoint, enemyBitmap, hPMax, defMax);
                    break;
            }

            int newPoint = getNewEmptyPoint();
            temp.SetPoint(GetFloorPoints().get(newPoint));
            TakeAwayEmptyFloorTiles(newPoint);

            enemies.add(temp);
        }
    }

    private void createStairs() {
        int newPoint = getNewEmptyPoint();
        stairsDown = new BaseObject(GetFloorPoints().get(newPoint),
                Bitmaps[3]);
        TakeAwayEmptyFloorTiles(newPoint);
        newPoint = getNewEmptyPoint();
        stairsUp = new BaseObject(GetFloorPoints().get(newPoint),
                Bitmaps[4]);
        TakeAwayEmptyFloorTiles(newPoint);
    }

    private void createImages(Bitmap[] images) {
        //Get Images
        Bitmaps = new Bitmap[7];
        //Clutter
        Bitmaps[0] = images[0];
        Bitmaps[1] = images[1];
        Bitmaps[2] = images[2];
        //STAIRS
        Bitmaps[3] = images[3];
        Bitmaps[4] = images[4];
        //ENEMIES
        Bitmaps[5] = images[5];
        Bitmaps[6] = images[6];
    }

    Level(Bitmap[] images, Bitmap[] spaces, Bitmap[] walls, int startX, int startY, int Width, int Height) {
        super(spaces, walls, startX, startY, Width, Height);

        GenerateNewMap();
        //Height and Width of one cell
        mBitMapHeight = spaces[0].getHeight();
        mBitMapWidth = spaces[0].getWidth();
        createImages(images);
        createStairs();
        createEnemies();
        GetNewClutter();
    }

    //Getters
    public BaseObject getStairsUp() {return stairsUp;}
    public BaseObject getStairsDown() {return stairsDown;}
    public ArrayList<Creature> getEnemies() {return enemies;}
    public ArrayList<Clutter> getClutter() {return clutter;}
    //Setters
    //Helper Functions
    public Level GenerateNewLevel(){
        GenerateNewMap();
        createStairs();
        createEnemies();
        GetNewClutter();
        return this;
    }
}
