package com.example.stephen.runattackdungeon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Stephen Brasel on 2018-01-18.
 */

public class GameView extends SurfaceView implements Runnable {
    //boolean variable to track if the game is playing or not
    volatile boolean playing;
    //the game thread
    private Thread gameThread = null;
    //the player
    private Creature player;
    //These objects will be used for drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private Random rand = new Random();

    private Map map;
    private int mBitMapHeight;
    private int mBitMapWidth;
    private int mHeight;
    private int mWidth;

    private int maxItems = 5;
    private ArrayList<Item> items = new ArrayList<Item>(maxItems);
    private Bitmap[] Bitmaps;

//    private LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    private View view = inflater.inflate(R.layout.dpad, null);

    //Create dPad
    private Rect DPAD;
    private int dpadX = 0;
    private int dpadY = 0;
    private int dpadWidth = 0;
    private int dpadHeight = 0;
    private int dPadOpacity = 100;
    private int DPADbuffer = 8;
    private BaseObject dPadUp;
    private BaseObject dPadDown;
    private BaseObject dPadLeft;
    private BaseObject dPadRight;

    //Class constructor
    public GameView(Context context, int screenX, int screenY) {
        super(context);

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
        Bitmaps = new Bitmap[5];
        Bitmaps[0] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.barrel), (int) (mBitMapWidth * 0.75), (int) (mBitMapHeight * 0.75));
        Bitmaps[1] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.chest), (int) (mBitMapWidth * 0.75), (int) (mBitMapHeight * 0.75));
        Bitmaps[2] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.rock), (int) (mBitMapWidth * 0.75), (int) (mBitMapHeight * 0.75));
        Bitmaps[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.hero);
        Bitmaps[4] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.directional_button), (int) (mBitMapWidth * 1.05), (int) (mBitMapHeight * 1.05));

        //Create player object
        Bitmap heroLeft = Bitmap.createBitmap(Bitmaps[3], 0, 0, 48, 64);
        int newPoint = getNewEmptyPoint();
        player = new Creature(map.GetFloorPoints().get(newPoint),
                getResizedBitmap(heroLeft, (int) (mBitMapWidth * 0.75), (int) (mBitMapHeight * 0.75)));
        map.TakeAwayEmptyFloorTiles(newPoint);

        //Create DPAD
        createDPAD(screenY);
        //Create Items
        GetNewItems();
    }

    private void createDPAD(int screenY) {
        dpadHeight = Bitmaps[4].getHeight() * 2 + Bitmaps[4].getWidth();
        dpadY = screenY - dpadHeight;
        DPAD = new Rect(dpadX + DPADbuffer, dpadY - DPADbuffer, dpadHeight, dpadHeight);

        Point dpadUpPoint = new Point(DPAD.left + (int)(DPAD.width()/2) - (int)(Bitmaps[4].getWidth()/2), DPAD.top);
        dPadUp = new BaseObject(dpadUpPoint, Bitmaps[4]);

        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        Bitmap rotatedBitmap = Bitmap.createBitmap(Bitmaps[4], 0, 0, Bitmaps[4].getWidth(), Bitmaps[4].getHeight(), matrix, true);
        Point dpadLeftPoint = new Point(DPAD.left, DPAD.top + (int)(DPAD.bottom/2) - (int)(rotatedBitmap.getHeight()/2));
        dPadLeft = new BaseObject(dpadLeftPoint, rotatedBitmap);

        //matrix.postRotate(180);
        rotatedBitmap = Bitmap.createBitmap(rotatedBitmap, 0, 0, rotatedBitmap.getWidth(), rotatedBitmap.getHeight(), matrix, true);
        Point dpadDownPoint = new Point(DPAD.left + (int)(DPAD.width()/2) - (int)(Bitmaps[4].getWidth()/2), DPAD.top + DPAD.bottom - rotatedBitmap.getHeight());
        dPadDown = new BaseObject(dpadDownPoint, rotatedBitmap);

        //matrix.postRotate(90);
        rotatedBitmap = Bitmap.createBitmap(rotatedBitmap, 0, 0, rotatedBitmap.getWidth(), rotatedBitmap.getHeight(), matrix, true);
        Point dpadRightPoint = new Point(DPAD.right - rotatedBitmap.getWidth(), DPAD.top + (int)(DPAD.bottom/2) - (int)(rotatedBitmap.getHeight()/2));
        dPadRight = new BaseObject(dpadRightPoint, rotatedBitmap);
    }

    private void SetNewPlayerPoint() {
        int newPoint = getNewEmptyPoint();
        player.SetPoint(map.GetFloorPoints().get(newPoint));
        map.TakeAwayEmptyFloorTiles(newPoint);
    }

    private int getNewEmptyPoint() {
        return rand.nextInt(map.GetNumEmptyPoints());
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private void GetNewItems() {
        items.clear();
        int newSize = rand.nextInt(maxItems) + 1;
        items = new ArrayList<Item>(newSize);
        for (int i = 0; i < newSize; i++) {
            int itemVal = 10;
            Bitmap itemBitmap = Bitmaps[0];
            Point itemPoint = new Point(0, 0);

            Item temp = new Item(itemVal, itemPoint, itemBitmap);
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
            temp.SetPoint(map.GetFloorPoints().get(newPoint));
            map.TakeAwayEmptyFloorTiles(newPoint);
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
            //drawing the player
            canvas.drawBitmap(
                    player.GetBitmap(),
                    player.GetX() * mBitMapWidth + (int) (mBitMapWidth / 2) - (player.GetBitmap().getWidth() / 2),
                    player.GetY() * mBitMapHeight + (int) (mBitMapHeight / 2) - (player.GetBitmap().getHeight() / 2),
                    paint);
            drawingTheDPAD();

            //Unlocking the canvas
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawingTheDPAD() {
        //DRAW THIS LAST.
        //set Paint opacity for DPAD.
        paint.setAlpha(dPadOpacity);

        canvas.drawBitmap(
                dPadUp.GetBitmap(),
                dPadUp.GetX(),
                dPadUp.GetY(),
                paint
        );
        canvas.drawBitmap(
                dPadLeft.GetBitmap(),
                dPadLeft.GetX(),
                dPadLeft.GetY(),
                paint
        );
        canvas.drawBitmap(
                dPadDown.GetBitmap(),
                dPadDown.GetX(),
                dPadDown.GetY(),
                paint
        );
        canvas.drawBitmap(
                dPadRight.GetBitmap(),
                dPadRight.GetX(),
                dPadRight.GetY(),
                paint
        );
        //Reset Paint opacity.
        paint.setAlpha(255);
    }

    private void drawingTheItems() {
        for (int j = 0; j < items.size(); ++j) {
            canvas.drawBitmap(
                    items.get(j).GetBitmap(),
                    items.get(j).GetX() * mBitMapWidth + (int) (mBitMapWidth / 2) - (items.get(j).GetBitmap().getWidth() / 2),
                    items.get(j).GetY() * mBitMapHeight,
                    paint);
        }
    }

    private void drawingTheMap() {
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

    private boolean DetectCollision(float x, float y, Rect rect){
        if (x <= rect.left + rect.right &&
                x >= rect.left &&
                y <= rect.top + rect.bottom &&
                y >= rect.top){
            return true;
        }
        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                //When the user presses on the screen
                //we will do something here
                if (DetectCollision(motionEvent.getX(), motionEvent.getY(), dPadUp.GetDetectCollision()) &&
                        map.IsCellOpen(player.GetX(), player.GetY() - 1)){
                    player.SetY(player.GetY() - 1);
                }
                else if (DetectCollision(motionEvent.getX(), motionEvent.getY(), dPadDown.GetDetectCollision()) &&
                        map.IsCellOpen(player.GetX(), player.GetY() + 1)){
                    player.SetY(player.GetY() + 1);
                }
                if (DetectCollision(motionEvent.getX(), motionEvent.getY(), dPadLeft.GetDetectCollision()) &&
                        map.IsCellOpen(player.GetX() - 1, player.GetY())){
                    player.SetX(player.GetX() - 1);
                }
                else if (DetectCollision(motionEvent.getX(), motionEvent.getY(), dPadRight.GetDetectCollision()) &&
                        map.IsCellOpen(player.GetX() + 1, player.GetY())){
                    player.SetX(player.GetX() + 1);
                }
                //GetNewLevel();
                break;
            case MotionEvent.ACTION_DOWN:
                //When the user releases the screen
                //do something here
                break;
        }
        return true;
    }

    private void GetNewLevel() {
        map.GenerateNewMap();
        SetNewPlayerPoint();
        GetNewItems();
    }
}
