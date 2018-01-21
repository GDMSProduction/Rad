package com.example.stephen.runattackdungeon;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Stephen on 2018-01-18.
 */

public class GameView extends SurfaceView implements Runnable {
    //boolean variable to track if the game is playing or not
    volatile boolean playing;
    //the game thread
    private Thread gameThread = null;
    //the player
    //private Player player;
    //These objects will be used for drawing
    private final Paint paint;
    private Canvas canvas;
    private final SurfaceHolder surfaceHolder;
    private Map map;
    private int maxItems = 5;
    private ArrayList<Item> items = new ArrayList<Item>(maxItems);
    private Bitmap[] itemBitmaps;
    private Random rand = new Random();
    private int mBitMapHeight;
    private int mBitMapWidth;
    private int mHeight;
    private int mWidth;

    //Class constructor
    public GameView(Context context, int screenX, int screenY) {
        super(context);

        //Create player object
        //player = new Player(context, screenX, screenY);

        //Create drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();

        //Create map
        map = new Map(context, 0, 0, screenX, screenY);
        map.GenerateNewMap();

        //Height and Width of one cell
        mBitMapHeight = map.GetBitMapHeight();
        mBitMapWidth = map.GetBitMapWidth();
        //Playable spaces on the map, i.e., the number of spaces wide and long that the player can potentially use.
        mHeight = map.GetHeight();
        mWidth = map.GetWidth();

        //Create Bitmaps
        itemBitmaps = new Bitmap[3];
        itemBitmaps[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.barrel);
        itemBitmaps[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.chest);
        itemBitmaps[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.rock);

        //Create Items
        GetNewItems();

    }

    private void GetNewItems() {
        items.clear();
        //items = new ArrayList<Item>(rand.nextInt(maxItems) + 1);
        items = new ArrayList<Item>(maxItems);
        for (int i = 0; i < maxItems; i++) {
            int itemVal = 10;
            Bitmap itemBitmap = itemBitmaps[0];
            Point itemPoint = new Point(0, 0);

            Item temp = new Item(itemVal, itemPoint, itemBitmap);
            switch (rand.nextInt(3)) {
                case 0:
                    //barrel
                    temp.SetBitMap(itemBitmaps[0]);
                    temp.SetValue(10);
                    break;
                case 1:
                    //chest
                    temp.SetBitMap(itemBitmaps[1]);
                    temp.SetValue(30);
                    break;
                default:
                case 2:
                    //rock
                    temp.SetBitMap(itemBitmaps[2]);
                    temp.SetValue(0);
                    break;
            }
            temp.SetPoint(map.GetFloorPoints().get(rand.nextInt(map.GetNumEmptyPoints())));
            items.add(temp);
        }
    }

    @Override
    public void run() {
        while (playing) {
            //to update the frame
            update();
            //to draw the frame
            draw();
            //to control
            control();
        }
    }

    private void update() {
//        //updating player position
//        player.update();
//        //setting boom outside the screen
//        boom.setX(-250);
//        boom.setY(-250);
//        //updating the enemy coordinate with respect to player speed
//        for(int i=0; i<enemyCount; i++){
//            enemies[i].update(player.getSpeed());
//            //if collision occurs with player
//            if (Rect.intersects(player.getDetectCollision(), enemies[i].getDetectCollision())) {
//                //displaying boom at that location
//                boom.setX(enemies[i].getX());
//                boom.setY(enemies[i].getY());
//                //moving enemy outside the left edge
//                enemies[i].setX(-200);
//            }
//        }
    }

    private void draw() {
        //checking if surface is valid
        if (surfaceHolder.getSurface().isValid()) {
            //locking the canvas
            canvas = surfaceHolder.lockCanvas();
            //drawing a background color for canvas
            canvas.drawColor(Color.BLACK);
            //setting the paint color to white to draw the stars
            paint.setColor(Color.WHITE);
            //drawing the map
            drawingTheMap();
            drawingTheItems();

            //Unlocking the canvas
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawingTheItems() {
        for (int j = 0; j < items.size(); ++j) {
            canvas.drawBitmap(
                    items.get(j).GetBitmap(),
                    items.get(j).GetX() * mBitMapWidth,
                    items.get(j).GetY() * mBitMapHeight,
                    paint);
        }
    }

    public void drawingTheMap() {
        Bitmap[][] tempMap = map.GetCurrentMap();

        for (int row = 0; row < mHeight; row++) {
            for (int col = 0; col < mWidth; col++) {
                canvas.drawBitmap(tempMap[row][col],
                        col * mBitMapWidth,
                        row * mBitMapHeight,
                        paint);
            }
        }
    }

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        //when the game is paused
        //setting the variable to false
        playing = false;
        try {
            //stopping the thread
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        //when the game is resumed
        //starting the thread again
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                //When the user presses on the screen
                //we will do something here
                map.GenerateNewMap();
                GetNewItems();
                int x = 9;
//                player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                //When the user releases the screen
                //do something here
//                player.setBoosting();
                break;
        }
        return true;
    }
}
