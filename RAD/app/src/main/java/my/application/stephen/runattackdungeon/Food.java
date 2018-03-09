package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.nfc.NfcAdapter;

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

    public int getHealing(){return healing;}
    public void setHealing(int newHealing){healing = newHealing;}
    public void setPotionColor(PotionColor newColor){color = newColor;}
    public void PotionEffect(Creature creature, Level currentLevel){
        switch (color){
            default:
            case LightBlue:
                //if it's a light blue potion, it randomly teleports you to an open space on the currentLevel.
                currentLevel.giveNewPointToObject(creature);
                break;
            case DarkBlue:
                //if it's a dark blue potion, it increases your defense.
                creature.setDefense(creature.getDefense() + healing);
                break;
            case Red:
                //if it's a red potion, it increases your attack + maxAttack.
                creature.setAttack(creature.getAttack() + healing);
                break;
            case Black:
                //if it's a black potion, it kills your light source.
                if (creature.getLightSource() != null) {
                    creature.setLightSource(null);
                }
                break;
            case Green:
                //if it's a green potion, it restores your health.
                creature.heal(healing);
                break;
            case Purple:
                //if it's a purple potion, it poisons you.
                creature.hurt(healing);
                break;
        }
    }
}
