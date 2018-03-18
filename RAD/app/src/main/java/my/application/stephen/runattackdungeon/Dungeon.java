package my.application.stephen.runattackdungeon;

import java.util.ArrayList;

import static my.application.stephen.runattackdungeon.GameView.changeMap;
import static my.application.stephen.runattackdungeon.GameView.screenHeight;
import static my.application.stephen.runattackdungeon.GameView.screenWidth;
import static my.application.stephen.runattackdungeon.GameView.spaces;
import static my.application.stephen.runattackdungeon.Level.roomHeightMin;
import static my.application.stephen.runattackdungeon.Level.roomWidthMin;

/**
 * Created by zfile on 2018-03-02.
 * Dungeon class used to contain levels and specify the behavior and generation therein.
 */

public class Dungeon {

    public enum DirectionToGo {NORTH, SOUTH, EAST , WEST, DOWN, UP}
    //the Levels
    static boolean minotaurSlain = false;
    private int numLevels = 100;
    private ArrayList<Level> dungeonLevels = new ArrayList<>(numLevels);
    private Level currentLevel;
    private int currentLevelIndex = 0;
    private Creature player = null;

    Dungeon(){
        minotaurSlain = false;
        AddNewLevel();
        currentLevel = dungeonLevels.get(0);
    }

    //Accessors
    public ArrayList<Level> getDungeonLevels() {return dungeonLevels;}
    public Level getCurrentLevel() {return currentLevel;}
    Creature getPlayer() {return player;}
    //Mutators
    public void setPlayer(Creature newPlayer){player = newPlayer;}
    //Helper Functions
    private void RemoveCreatureFromCurrentLevel(Creature creature) {
        for (int i = 0; i < currentLevel.getLevelCreatures().size(); i++) {
            Creature temp = currentLevel.getLevelCreatures().get(i);
            if (temp == creature) {
                currentLevel.getLevelCreatures().remove(i);
            }
        }
    }

    public void goToLevel(Creature creature, int levelToGoTo, DirectionToGo direction, boolean fallen) {
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
                dungeonLevels.get(levelToGoTo).getLevelCreatures().add(creature);
                if (creature != null) {
                    if (creature == player && currentLevel.getStairsUp() != null && !fallen) {
                        creature.setX(currentLevel.getStairsUp().getX());
                        creature.setY(currentLevel.getStairsUp().getY());
                        dungeonLevels.get(levelToGoTo).addObjectToMap(creature.getPoint(), creature, true);
                    }
                    else {
                        dungeonLevels.get(levelToGoTo).giveNewPointToObject(null, creature);
                    }
                }
                if (creature == player){
                    changeMap = true;
                }
                break;
            case UP:
                if (creature.getCurrentDepth() > 0) {
                    dungeonLevels.get(creature.getCurrentDepth()).removeObjectFromMap(creature.getPoint(), creature);
                    RemoveCreatureFromCurrentLevel(creature);
                    if (creature == player) {
                        currentLevel = dungeonLevels.get(levelToGoTo);
                    }
                    creature.setCurrentDepth(levelToGoTo);
                    dungeonLevels.get(levelToGoTo).getLevelCreatures().add(creature);
                    if (creature != null) {
                        if (creature == player && currentLevel.getStairsDown() != null) {
                            creature.setX(currentLevel.getStairsDown().getX());
                            creature.setY(currentLevel.getStairsDown().getY());
                            dungeonLevels.get(levelToGoTo).addObjectToMap(creature.getPoint(), creature, true);
                        } else{
                            dungeonLevels.get(levelToGoTo).giveNewPointToObject(null, creature);
                        }
                    }
                    if (creature == player){
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
        int borderThickness = 3;
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

        if (Width < roomWidthMin){
            Width = roomWidthMin;
        }
        if (Height < roomHeightMin){
            Height = roomHeightMin;
        }
//        if (currentLevelIndex % 5 == 0 && currentLevelIndex != 0) {
//            //ShopLevel
//            temp = new Level(Width, Height, 0, false, currentLevelIndex);
//        } else if (currentLevelIndex % 3 == 0 && currentLevelIndex != 0) {
//            temp = new Level(Width, Height, 0, false, currentLevelIndex);
//        } else {
            temp = new Level(
                    Width + (borderThickness * 2),
                    Height + (borderThickness * 2),
                    50,
                    true,
                    true,
                    dungeonLevels.size(),
                    borderThickness
            );
//        }
        dungeonLevels.add(temp);
    }
}
