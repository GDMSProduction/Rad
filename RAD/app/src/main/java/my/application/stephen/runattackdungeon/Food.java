package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by zfile on 2018-02-27.
 */

public class Food extends Clutter {
    public enum PotionColor{Green, LightBlue, Black, Red, Purple, DarkBlue}

    private int healing = 0;
    private PotionColor color = PotionColor.Green;
    //if it's a light blue potion, it randomly teleports you to an open space within 10 feet.
    //if it's a dark blue potion, it increases your defense.
    //if it's a red potion, it increases your attack + maxAttack.
    //if it's a green potion, it restores your health.
    //if it's a black potion, it kills your light source.
    //if it's a purple potion, it poisons you.
    Food(int health, int newValue, Point mPoint, Bitmap newImage, int HPMax) {
        super(newValue, mPoint, newImage, HPMax);
        healing = health;
    }
    Food(PotionColor Color, int newValue, Point mPoint, Bitmap newImage, int HPMax) {
        super(newValue, mPoint, newImage, HPMax);
        color = Color;
    }
    public void setHealing(int newHealing){healing = newHealing;}
    public void setPotionColor(PotionColor newColor){color = newColor;}
}
