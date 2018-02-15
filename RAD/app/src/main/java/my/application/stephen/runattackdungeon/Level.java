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
    private int diamondPercent = 1;
    //The Stairs
    private ObjectBase stairsUp;
    private ObjectBase stairsDown;
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
            Bitmap enemyBitmap = Bitmaps[8];
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
                    enemyBitmap = Bitmaps[9];
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
        stairsDown = new ObjectBase(GetFloorPoints().get(newPoint),
                Bitmaps[6]);
        TakeAwayEmptyFloorTiles(newPoint);
        newPoint = getNewEmptyPoint();
        stairsUp = new ObjectBase(GetFloorPoints().get(newPoint),
                Bitmaps[7]);
        TakeAwayEmptyFloorTiles(newPoint);
    }

    private void CreateRandomDrop(int i){
        switch(0){
            case 0:
                Clutter coins = new Clutter(clutter.get(i).GetValue(), clutter.get(i).GetPoint(), Bitmaps[3], 0);
                clutter.add(coins);
                break;
            case 1:
                //Clutter dagger = new Clutter(3, clutter.get(i).GetPoint(), Bitmaps[0], 3);
                break;
            case 2:
                break;
        }
    }
    public enum CellType {Wall, Space, Clutter, Weapon, Shield, Potion, Enemy}

    Level(Bitmap[] images, Bitmap[] spaces, Bitmap[] walls, int Width, int Height) {
        super(spaces, walls, Width, Height);

        //Height and Width of one cell
        mBitMapHeight = spaces[0].getHeight();
        mBitMapWidth = spaces[0].getWidth();
        Bitmaps = images;
        createStairs();
        createEnemies();
        GetNewClutter();
    }

    //Getters
    public ObjectBase getStairsUp() {
        return stairsUp;
    }

    public ObjectBase getStairsDown() {
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

    public void UpdateEnemies(Player player){
        for (int i = 0; i < enemies.size(); i++){
            if (enemies.get(i).GetBitmap() == Bitmaps[7]){

            }
        }
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
                if (cellx >= GetMapWidth() || cellx < 0 || celly >= GetMapHeight() || celly < 0) {
                    break;
                }
                harmWall(cellx, celly, mining);
                if (GetCurrentMap()[celly][cellx].GetHP() <= 0 && rand.nextInt(100) <= diamondPercent){
                    Point tempPoint = new Point(cellx, celly);
                    switch(rand.nextInt(2)){
                        default:
                        case 0:
                            Clutter diamond = new Clutter(200, tempPoint, Bitmaps[4], 0);
                            clutter.add(diamond);
                            break;
                        case 1:
                            Clutter diamondRed = new Clutter(200, tempPoint, Bitmaps[5], 0);
                            clutter.add(diamondRed);
                            break;
                    }
                }
                break;
            case Space:
                break;
            case Clutter:
                for (int i = 0; i < clutter.size(); i++) {
                    if (clutter.get(i).GetPoint().x == cellx && clutter.get(i).GetPoint().y == celly) {
                        clutter.get(i).Hurt(damage);
                        if (clutter.get(i).GetBitmap() == Bitmaps[3] ||
                                clutter.get(i).GetBitmap() == Bitmaps[4] ||
                                clutter.get(i).GetBitmap() == Bitmaps[5] )
                        {
                            player.SetPoint(cellx, celly);
                            if (clutter.get(i).GetBitmap() != Bitmaps[5]) {
                                player.incrementScore(clutter.get(i).GetValue());
                            } else {
                                player.SetMaxHP(player.GetMaxpHP() + 1);
                            }
                            clutter.remove(i);
                        }
                        else if (clutter.get(i).GetHP() <= 0)
                        {
                            if (clutter.get(i).GetBitmap() != Bitmaps[2]) {
                                CreateRandomDrop(i);
                            }
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
