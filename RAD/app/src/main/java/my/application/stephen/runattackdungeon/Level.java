package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.lang.reflect.Type;
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
            int clutterHP = 1;

            Clutter temp = new Clutter(clutterVal, clutterPoint, clutterBitmap, clutterHP);
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
            Bitmap enemyBitmap = Bitmaps[6];
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
                    enemyBitmap = Bitmaps[7];
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
                Bitmaps[4]);
        TakeAwayEmptyFloorTiles(newPoint);
        newPoint = getNewEmptyPoint();
        stairsUp = new BaseObject(GetFloorPoints().get(newPoint),
                Bitmaps[5]);
        TakeAwayEmptyFloorTiles(newPoint);
    }

    private void createImages(Bitmap[] images) {
        //Get Images
        Bitmaps = new Bitmap[23];
        //CLUTTER
        Bitmaps[0] = images[0];
        Bitmaps[1] = images[1];
        Bitmaps[2] = images[2];
        Bitmaps[3] = images[3];
        //STAIRS
        Bitmaps[4] = images[4];
        Bitmaps[5] = images[5];
        //ENEMIES
        Bitmaps[6] = images[6];
        Bitmaps[7] = images[7];
        //CONSUMABLES
        Bitmaps[8] = images[8];
        Bitmaps[9] = images[9];
        Bitmaps[10] = images[10];
        Bitmaps[11] = images[11];
        //WEAPONS
        Bitmaps[12] = images[12];
        Bitmaps[13] = images[13];
        Bitmaps[14] = images[14];
        Bitmaps[15] = images[15];
        //LIGHT
        Bitmaps[16] = images[16];
        Bitmaps[17] = images[17];
        //MINING
        Bitmaps[18] = images[18];
        Bitmaps[19] = images[19];
        //WEARABLES
        Bitmaps[20] = images[20];
        Bitmaps[21] = images[21];
        Bitmaps[22] = images[22];
    }

    public enum CellType {Wall, Space, Clutter, Weapon, Shield, Potion, Enemy}

    Level(Bitmap[] images, Bitmap[] spaces, Bitmap[] walls, int Width, int Height) {
        super(spaces, walls, Width, Height);

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
    public BaseObject getStairsUp() {
        return stairsUp;
    }

    public BaseObject getStairsDown() {
        return stairsDown;
    }

    public ArrayList<Creature> getEnemies() {
        return enemies;
    }

    public ArrayList<Clutter> getClutter() {
        return clutter;
    }

    //Setters
    //Helper Functions
    public Level GenerateNewLevel() {
        GenerateNewMap();
        createStairs();
        createEnemies();
        GetNewClutter();
        return this;
    }

//    @Override
//    public boolean IsCellOpen(int cellx, int celly) {
//        boolean ret = super.IsCellOpen(cellx, celly);
//        for (int i = 0; i < enemies.size(); i++){
//            if (enemies.get(i).GetPoint().x == cellx && enemies.get(i).GetPoint().y == celly){
//                ret = false;
//            }
//        }
//        for (int i = 0; i < clutter.size(); i++){
//            if (clutter.get(i).GetPoint().x == cellx && clutter.get(i).GetPoint().y == celly){
//                ret = false;
//            }
//        }
//        return ret;
//    }

    public CellType getCellType(int cellx, int celly) {
        CellType returnType = CellType.Wall;
        if (super.IsCellOpen(cellx, celly)) {
            returnType = CellType.Space;
        }
        for (int i = 0; i < clutter.size(); i++) {
            if (clutter.get(i).GetPoint().x == cellx && clutter.get(i).GetPoint().y == celly) {
                returnType = CellType.Clutter;
            }
        }
        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).GetPoint().x == cellx && enemies.get(i).GetPoint().y == celly) {
                returnType = CellType.Enemy;
            }
        }
        return returnType;
    }

    public void harmObject(int cellx, int celly, int damage, int mining, Player player) {
        switch (getCellType(cellx, celly)) {
            default:
            case Wall:
                harmWall(cellx, celly, mining);
                break;
            case Space:
                break;
            case Clutter:
                for (int i = 0; i < clutter.size(); i++) {
                    if (clutter.get(i).GetPoint().x == cellx && clutter.get(i).GetPoint().y == celly) {
                        clutter.get(i).Hurt(damage);
                        if (clutter.get(i).GetBitmap() == Bitmaps[3])
                        {
                            player.SetPoint(cellx, celly);
                            player.incrementScore(clutter.get(i).GetValue());
                            clutter.remove(i);
                        }
                        else if (clutter.get(i).GetHP() <= 0)
                        {
                            Clutter coins = new Clutter(clutter.get(i).GetMaxpHP(), clutter.get(i).GetPoint(), Bitmaps[3], 0);
                            clutter.add(coins);
                            clutter.remove(i);
                            break;
                        }
                    }
                }
                break;
            case Weapon:
                break;
            case Shield:
                break;
            case Potion:
                break;
            case Enemy:
                for (int i = 0; i < enemies.size(); i++) {
                    if (enemies.get(i).GetPoint().x == cellx && enemies.get(i).GetPoint().y == celly) {
                        enemies.get(i).Hurt(damage);
                        if (enemies.get(i).GetHP() <= 0) {
                            Clutter coins = new Clutter(enemies.get(i).GetMaxpHP(), enemies.get(i).GetPoint(), Bitmaps[3], 0);
                            clutter.add(coins);
                            enemies.remove(i);
                            break;
                        }
                    }
                }
                break;
        }
    }
}
