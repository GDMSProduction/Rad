package my.application.stephen.runattackdungeon;

import android.graphics.Point;

import java.util.ArrayList;

import static my.application.stephen.runattackdungeon.GameView.imageClutter;
import static my.application.stephen.runattackdungeon.GameView.imageEnemy;

/**
 * Created by zfile on 2018-03-13.
 * Rooms are maps with exit points, start points, and more specific creature and clutter generation features
 */

class Room extends Map {
    private int maxClutter = 0;
    private int maxEnemies = 0;
    private Point startPoint = new Point (0,0);
    private Point connectorNORTH = null;
    private Point connectorSOUTH = null;
    private Point connectorEAST = null;
    private Point connectorWEST = null;

    Room(int Width, int Height, int spacePercent, boolean natural, int MAXClutter, int MAXEnemies) {
        super(Width, Height, spacePercent, natural);
        maxClutter = MAXClutter;
        maxEnemies = MAXEnemies;

        createConnectorNorth();
        createConnectorSouth();
        createConnectorEast();
        createConnectorWest();
    }

    //Getters
    Point getStartPoint() {return startPoint;}
    int getMaxClutter() {return maxClutter;}
    int getMaxEnemies(){return maxEnemies;}

    //Setters
    void setStartPoint(Point StartPoint) {
        startPoint = StartPoint;
    }
    //Helper Functions
    private void createConnectorNorth(){
        ArrayList<Point> possibleConnectors = new ArrayList<>(getMapWidth());
        for (int i = 0; i < getMapWidth(); i++){
            possibleConnectors.add(new Point(i , 0) );
        }
        connectorNORTH = possibleConnectors.get(rand.nextInt(possibleConnectors.size()));
    }
    private void createConnectorSouth(){
        ArrayList<Point> possibleConnectors = new ArrayList<>(getMapWidth());
        int height = getMapHeight() - 1;
        for (int i = 0; i < getMapWidth(); i++){
            possibleConnectors.add(new Point(i , height) );
        }
        connectorSOUTH = possibleConnectors.get(rand.nextInt(possibleConnectors.size()));
    }
    private void createConnectorEast(){
        ArrayList<Point> possibleConnectors = new ArrayList<>(getMapWidth());
        for (int i = 0; i < getMapHeight(); i++){
            possibleConnectors.add(new Point(0, i) );
        }
        connectorEAST = possibleConnectors.get(rand.nextInt(possibleConnectors.size()));
    }
    private void createConnectorWest(){
        ArrayList<Point> possibleConnectors = new ArrayList<>(getMapWidth());
        int width = getMapWidth() - 1;
        for (int i = 0; i < getMapWidth(); i++){
            possibleConnectors.add(new Point(width, i) );
        }
        connectorWEST = possibleConnectors.get(rand.nextInt(possibleConnectors.size()));
    }
}
