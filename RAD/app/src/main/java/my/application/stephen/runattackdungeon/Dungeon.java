package my.application.stephen.runattackdungeon;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static my.application.stephen.runattackdungeon.GameView.changeMap;
import static my.application.stephen.runattackdungeon.GameView.screenHeight;
import static my.application.stephen.runattackdungeon.GameView.screenWidth;
import static my.application.stephen.runattackdungeon.GameView.spaces;

/**
 * Created by zfile on 2018-03-02.
 */

public class Dungeon {

    public enum DirectionToGo {NORTH, SOUTH, EAST , WEST, DOWN, UP}
    //the Levels
    private boolean friendlyFire = false;
    private int numLevels = 100;
    private ArrayList<Level> dungeonLevels = new ArrayList<Level>(numLevels);
    private Level currentLevel;
    private int currentLevelIndex = 0;
    private Creature player = null;

    Dungeon(Boolean FriendlyFire){
        friendlyFire = FriendlyFire;
        AddNewLevel();
        currentLevel = dungeonLevels.get(0);
    }

    //Accessors
    public ArrayList<Level> getDungeonLevels() {return dungeonLevels;}
    public Level getCurrentLevel() {return currentLevel;}
    public int getCurrentLevelIndex() {return currentLevelIndex;}
    public Boolean getFriendlyFire() {return friendlyFire;}
    //Mutators
    public void setPlayer(Creature newPlayer){player = newPlayer;}
    //Helper Functions
    private void RemoveCreatureFromCurrentLevel(Creature creature) {
        for (int i = 0; i < currentLevel.getCreatures().size(); i++) {
            Creature temp = currentLevel.getCreatures().get(i);
            if (temp == creature) {
                currentLevel.getCreatures().remove(i);
            }
        }
    }

    public void goToLevel(Creature creature, int levelToGoTo, DirectionToGo direction) {
        switch (direction) {
            case DOWN:
                dungeonLevels.get(creature.getCurrentDepth()).removeObjectFromMap(creature.getPoint(), creature);
                if (currentLevel == dungeonLevels.get(dungeonLevels.size() - 1)) {
                    AddNewLevel();
                }
                RemoveCreatureFromCurrentLevel(creature);
                if (creature == player) {
                    currentLevel = dungeonLevels.get(levelToGoTo);
                }
                creature.setCurrentDepth(levelToGoTo);
                dungeonLevels.get(levelToGoTo).getCreatures().add(creature);
                if (creature != null) {
                    if (creature == player && currentLevel.getStairsUp() != null) {
                        creature.setX(currentLevel.getStairsUp().getX());
                        creature.setY(currentLevel.getStairsUp().getY());
                        dungeonLevels.get(levelToGoTo).addObjectToMap(creature.getPoint(), creature);
                    }
                    else {
                        dungeonLevels.get(levelToGoTo).giveNewPointToObject(creature);
                    }
                }
                if (creature == player) {
                    changeMap = true;
                }
                break;
            case UP:
                if (creature.getCurrentDepth() == 0) {
                } else {
                    dungeonLevels.get(creature.getCurrentDepth()).removeObjectFromMap(creature.getPoint(), creature);
                    RemoveCreatureFromCurrentLevel(creature);
                    if (creature == player) {
                        currentLevel = dungeonLevels.get(levelToGoTo);
                    }
                    creature.setCurrentDepth(levelToGoTo);
                    currentLevel.getCreatures().add(creature);
                    if (creature != null) {
                        creature.setX(currentLevel.getStairsDown().getX());
                        creature.setY(currentLevel.getStairsDown().getY());
                        dungeonLevels.get(levelToGoTo).addObjectToMap(creature.getPoint(), creature);
                    }
                    if (creature == player) {
                        changeMap = true;
                    }
                }
                break;
        }
        if (creature == player) {
            currentLevelIndex = creature.getCurrentDepth();
        }
    }

    private void AddNewLevel() {
        Level temp;
        int Width;
        int Height;
        if (dungeonLevels.size() != 0) {
            Width = dungeonLevels.get(dungeonLevels.size() - 1).getMapWidth() + dungeonLevels.size() - 1;
            if (Width > (screenWidth / spaces[0].getHeight()) * 3) {
                Width = (screenWidth / spaces[0].getHeight()) * 3;
            }
            Height = dungeonLevels.get(dungeonLevels.size() - 1).getMapHeight() + dungeonLevels.size() - 1;
            if (Height > (screenHeight / spaces[0].getHeight()) * 3) {
                Height = (screenHeight / spaces[0].getHeight()) * 3;
            }
        } else {
            Width = screenWidth / spaces[0].getHeight();
            Height = screenHeight / spaces[0].getHeight();
        }

//        if (currentLevelIndex % 5 == 0 && currentLevelIndex != 0){
//            //ShopLevel
//            temp = new Level(Width, Height, 40, false);
//        } else if (currentLevelIndex % 3 == 0 && currentLevelIndex != 0) {
//            temp = new Level(Width, Height, 40, false);
//        } else {
        temp = new Level(Width, Height, 50, true);
//        }
        dungeonLevels.add(temp);
    }
}
