package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by Stephen on 2018-01-23.
 */

public class Player extends Creature {
    private String name = "";
    private int score = 0;
    private int level = 0;
    Player(Point newPoint, Bitmap newBitmap, int HPMax){
        super(newPoint, newBitmap, HPMax);
    }
    //Getters
    public String getName(){return name;}
    public int getScore(){return score;}
    public int getLevel(){return level;}
    //Setters
    public void setName(String newName){name = newName;}
    public void incrementScore(int points){ score+=points;}
    public void levelUP(){
        level++;
        SetMaxHP((int)(GetMaxpHP()*1.2));
    }
}
