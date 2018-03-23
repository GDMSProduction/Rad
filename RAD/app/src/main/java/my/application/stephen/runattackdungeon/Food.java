package my.application.stephen.runattackdungeon;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.nfc.NfcAdapter;

import static my.application.stephen.runattackdungeon.GameView.changeLighting;
import static my.application.stephen.runattackdungeon.GameView.imageWeapon;

/**
 * Created by zfile on 2018-02-27.
 */

public class Food extends Clutter {
    public enum PotionColor {Green, LightBlue, Black, Red, Purple, DarkBlue}

    private int healing = 0;
    private PotionColor color = PotionColor.Green;

    //if it's a light blue potion, it randomly teleports you to an open space within 10 feet.
    //if it's a dark blue potion, it increases your defense.
    //if it's a red potion, it increases your attack + maxAttack.
    //if it's a green potion, it restores your health.
    //if it's a black potion, it kills your light source.
    //if it's a purple potion, it poisons you.
    Food(int health, int newValue, Point3d mPoint, Bitmap newImage, int HPMax) {
        super(newValue, mPoint, newImage, HPMax);
        healing = health;
        setCellType(CellType.Food);
    }

    Food(PotionColor Color, int newValue, Point3d mPoint, Bitmap newImage, int HPMax) {
        super(newValue, mPoint, newImage, HPMax);
        color = Color;
        setCellType(CellType.Potion);
    }

    public int getHealing() {
        return healing;
    }

    public void setHealing(int newHealing) {
        healing = newHealing;
    }

    public void setPotionColor(PotionColor newColor) {
        color = newColor;
    }

    public void PotionEffect(int dungeonSize, Creature creature, Level currentLevel) {
        switch (color) {
            default:
            case LightBlue:
                //if it's a light blue potion, it randomly teleports you to an open space on the currentLevel.
                currentLevel.giveNewPointToObject(null, creature, creature.getZ());
//                if (creature.getLightSource() != null){
//                    creature.getLightSource().setPoint(creature.getPoint());
//                }
//                changeLighting = true;
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
                //if it's a black potion, it acidifies and destroys your belongings.
                boolean destroyed = false;
                int i = 0;
                while (i < 8 && !destroyed) {
                    switch (healing) {
                        default:
                        case 0:
                            if (creature.getFood() != null) {
                                creature.setFood(null);
                                destroyed = true;
                                break;
                            }
                            i++;
                        case 1:
                            if (creature.getScroll() != null) {
                                creature.setScroll(null);
                                destroyed = true;
                                break;
                            }
                            i++;
                        case 2:
                            if (creature.getShield() != null) {
                                creature.setShield(null);
                                destroyed = true;
                                break;
                            }
                            i++;
                        case 3:
                            if (creature.getRing() != null) {
                                creature.setRing(null);
                                destroyed = true;
                                break;
                            }
                            i++;
                        case 4:
                            if (creature.getLightSource() != null) {
                                creature.setLightSource(null);
                                destroyed = true;
                                changeLighting = true;
                                break;
                            }
                            i++;
                        case 5:
                            if (creature.getWeapon() != null) {
                                creature.setWeapon(null);
                                destroyed = true;
                                break;
                            }
                            i++;
                        case 6:
                            if (creature.getMiningTool() != null) {
                                creature.setMiningTool(new MiningTool(1, 0, creature.getPoint(), imageWeapon[0], 0));
                                destroyed = true;
                                break;
                            }
                            i++;
                            break;
                    }
                }
                if (destroyed == false){
                    creature.hurt(healing);
                }
                break;
            case Green:
                //if it's a green potion, it restores your health.
                creature.heal(dungeonSize, healing);
                break;
            case Purple:
                //if it's a purple potion, it poisons you.
                creature.hurt(healing);
                break;
        }
    }
}
