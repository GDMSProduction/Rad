package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by Stephen on 2018-01-23.
 */

public class Player extends Creature {
    private String name = "";
    private int score = 0;
    Player(Point newPoint, Bitmap newBitmap){
        super(newPoint, newBitmap);
    }
    //Getters
    public String getName(){return name;}
    public int getScore(){return score;}
    //Setters
    public void setName(String newName){name = newName;}
    public void incrementScore(int points){ score+=points;}
}
