package my.application.stephen.runattackdungeon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
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
    //These objects will be used for drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    //Screensize
    private int screenWidth = 0;
    private int screenHeight = 0;
    private Random rand = new Random();
    //Holder of images.
    private Bitmap[] Bitmaps;
    private Bitmap[] spaces;
    private Bitmap[] walls;
    private Bitmap[] levelImages;

    //User Interface.
    //dPad
    private Rect DPAD;
    private int dpadX = 0;
    private int dpadY = 0;
    private int dpadHeight = 0;
    private int dPadOpacity = 100;
    private int DPADbuffer = 80;
    private BaseObject dPadUp;
    private BaseObject dPadDown;
    private BaseObject dPadLeft;
    private BaseObject dPadRight;
    //Level info
    private int depthTextSize = 64;

    //the Levels
    private int numLevels = 2;
    private ArrayList<Level> Levels = new ArrayList<Level>(numLevels);
    private Level currentLevel;
    private int currentLevelIndex = 0;

    private int mBitMapHeight;
    private int mBitMapWidth;
    private int camOffsetX = 0;
    private int camOffsetY = 0;
    private int camHeight;
    private int camWidth;

    //the player
    private Bitmap heroLeft;
    private Bitmap heroRight;
    private Bitmap heroUp;
    private Bitmap heroDown;
    private Player player;
    private int startingHealth = 10;

    //Class constructor
    public GameView(Context context, int screenX, int screenY) {
        super(context);

        screenWidth = screenX;
        screenHeight = screenY;

        //Create drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();
        createImages(context);

        //Create currentLevel
        currentLevel = new Level(levelImages, spaces, walls, screenWidth, screenHeight);
        Levels.add(currentLevel);
        currentLevelIndex = 0;
        //currentLevel.GenerateNewMap();

        //Playable spaces on the currentLevel, i.e., the number of spaces wide and long that the player can potentially use.
        camHeight = screenY / spaces[0].getHeight();
        camWidth = screenX / spaces[0].getWidth();

        createDPAD(screenY);

        //Create Core GamePlay Elements
        createPlayer();
        //offsetTheCamera();
    }

    private void offsetTheCamera() {
        camOffsetY = player.GetY() - camHeight / 2;
        camOffsetX = player.GetX() - camWidth / 2;
        if (camOffsetY < 0) {
            camOffsetY = 0;
        }
        if (camOffsetY >= currentLevel.GetHeight() - camHeight) {
            camOffsetY = currentLevel.GetHeight() - camHeight;
        }
        if (camOffsetX < 0) {
            camOffsetX = 0;
        }
        if (camOffsetX >= currentLevel.GetWidth() - camWidth) {
            camOffsetX = currentLevel.GetWidth() - camWidth;
        }
    }

    private void createPlayer() {
        //Create player object
        heroLeft = Bitmap.createBitmap(Bitmaps[0], 0, 0, Bitmaps[0].getWidth() / 3, Bitmaps[0].getHeight() / 4);
        heroLeft = getResizedBitmap(heroLeft, (int) (mBitMapWidth * 0.75), (int) (mBitMapHeight * 0.75));
        Matrix flip = new Matrix();
        flip.postScale(-1, 1, heroLeft.getWidth() / 2f, heroLeft.getHeight() / 2f);
        heroRight = Bitmap.createBitmap(heroLeft, 0, 0, heroLeft.getWidth(), heroLeft.getHeight(), flip, true);
        heroRight = getResizedBitmap(heroRight, (int) (mBitMapWidth * 0.75), (int) (mBitMapHeight * 0.75));
        heroUp = Bitmap.createBitmap(Bitmaps[0], Bitmaps[0].getWidth() / 3, Bitmaps[0].getHeight() / 4, Bitmaps[0].getWidth() / 3, Bitmaps[0].getHeight() / 4);
        heroUp = getResizedBitmap(heroUp, (int) (mBitMapWidth * 0.75), (int) (mBitMapHeight * 0.75));
        heroDown = Bitmap.createBitmap(Bitmaps[0], Bitmaps[0].getWidth() / 3, Bitmaps[0].getHeight() / 2, Bitmaps[0].getWidth() / 3, Bitmaps[0].getHeight() / 4);
        heroDown = getResizedBitmap(heroDown, (int) (mBitMapWidth * 0.75), (int) (mBitMapHeight * 0.75));
        int newPoint = currentLevel.getNewEmptyPoint();
        player = new Player(currentLevel.GetFloorPoints().get(newPoint),
                heroDown, startingHealth);
        currentLevel.TakeAwayEmptyFloorTiles(newPoint);
        player.SetDig(5);
        player.SetAttack(5);
    }
    private void createImages(Context context) {

        spaces = new Bitmap[6];
        spaces[0] = BitmapFactory.decodeResource(context.getResources(), my.application.stephen.runattackdungeon.R.drawable.floor1);
        spaces[1] = BitmapFactory.decodeResource(context.getResources(), my.application.stephen.runattackdungeon.R.drawable.floor2);
        spaces[2] = BitmapFactory.decodeResource(context.getResources(), my.application.stephen.runattackdungeon.R.drawable.floor3);
        spaces[3] = BitmapFactory.decodeResource(context.getResources(), my.application.stephen.runattackdungeon.R.drawable.floor4);
        spaces[4] = BitmapFactory.decodeResource(context.getResources(), my.application.stephen.runattackdungeon.R.drawable.floor5);
        spaces[5] = BitmapFactory.decodeResource(context.getResources(), my.application.stephen.runattackdungeon.R.drawable.floor6);

        //Height and Width of one cell
        mBitMapHeight = spaces[0].getHeight();
        mBitMapWidth = spaces[0].getWidth();

        walls = new Bitmap[2];
        walls[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.wall1);
        walls[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.wall2);

        //Get Images
        Bitmaps = new Bitmap[3];//PLAYER
        Bitmaps[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.hero);
        //Directional Button
        Bitmaps[1] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.directional_button), (int) (mBitMapWidth * 1.05), (int) (mBitMapHeight * 1.05));
        Bitmaps[2] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.heart_3d), (int) (mBitMapWidth * 1.05), (int) (mBitMapHeight * 1.05));

        levelImages = new Bitmap[25];
        //Clutter
        levelImages[0] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.barrel), (int) (mBitMapWidth * 0.75), (int) (mBitMapHeight * 0.75));
        levelImages[1] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.chest), (int) (mBitMapWidth * 0.75), (int) (mBitMapHeight * 0.75));
        levelImages[2] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.rock), (int) (mBitMapWidth * 0.75), (int) (mBitMapHeight * 0.75));
        levelImages[3] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.coins), mBitMapWidth, mBitMapHeight);
        levelImages[4] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.diamond), mBitMapWidth, mBitMapHeight);
        levelImages[5] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.diamond_red), mBitMapWidth, mBitMapHeight);
        //STAIRS
        levelImages[6] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.stairsdown), mBitMapWidth, mBitMapHeight);
        levelImages[7] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.stairsup), mBitMapWidth, mBitMapHeight);
        //ENEMIES
        levelImages[8] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.blob_green), mBitMapWidth, mBitMapHeight);
        levelImages[9] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.goblin_easy), mBitMapWidth, mBitMapHeight);
        //Consumable
        levelImages[10] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.apple), mBitMapWidth, mBitMapHeight);
        levelImages[11] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.bottles), mBitMapWidth, mBitMapHeight);
        levelImages[12] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.diploma), mBitMapWidth, mBitMapHeight);
        levelImages[13] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.drumstick), mBitMapWidth, mBitMapHeight);
        //Weapons
        levelImages[14] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.axe), mBitMapWidth, mBitMapHeight);
        levelImages[15] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.bow_and_arrow), mBitMapWidth, mBitMapHeight);
        levelImages[16] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.dagger), mBitMapWidth, mBitMapHeight);
        levelImages[17] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.sword), mBitMapWidth, mBitMapHeight);
        //Light
        levelImages[18] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.torch), mBitMapWidth, mBitMapHeight);
        levelImages[19] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.lantern), mBitMapWidth, mBitMapHeight);
        //Mining
        levelImages[20] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.shovel), mBitMapWidth, mBitMapHeight);
        levelImages[21] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.pickaxe), mBitMapWidth, mBitMapHeight);
        //Wearables
        levelImages[22] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ring_gold), mBitMapWidth, mBitMapHeight);
        levelImages[23] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ring_silver), mBitMapWidth, mBitMapHeight);
        levelImages[24] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.shield_wooden), mBitMapWidth, mBitMapHeight);
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

    private void createDPAD(int screenY) {
        dpadHeight = Bitmaps[1].getHeight() * 2 + Bitmaps[1].getWidth();
        dpadY = screenY - dpadHeight;
        DPAD = new Rect(dpadX + DPADbuffer, dpadY - DPADbuffer, dpadHeight, dpadHeight);

        Point dpadUpPoint = new Point(DPAD.left + (int) (DPAD.width() / 2) - (int) (Bitmaps[1].getWidth() / 2), DPAD.top);
        dPadUp = new BaseObject(dpadUpPoint, Bitmaps[1]);

        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        Bitmap rotatedBitmap = Bitmap.createBitmap(Bitmaps[1], 0, 0, Bitmaps[1].getWidth(), Bitmaps[1].getHeight(), matrix, true);
        Point dpadLeftPoint = new Point(DPAD.left, DPAD.top + (int) (DPAD.bottom / 2) - (int) (rotatedBitmap.getHeight() / 2));
        dPadLeft = new BaseObject(dpadLeftPoint, rotatedBitmap);

        //matrix.postRotate(180);
        rotatedBitmap = Bitmap.createBitmap(rotatedBitmap, 0, 0, rotatedBitmap.getWidth(), rotatedBitmap.getHeight(), matrix, true);
        Point dpadDownPoint = new Point(DPAD.left + (int) (DPAD.width() / 2) - (int) (Bitmaps[1].getWidth() / 2), DPAD.top + DPAD.bottom - rotatedBitmap.getHeight());
        dPadDown = new BaseObject(dpadDownPoint, rotatedBitmap);

        //matrix.postRotate(90);
        rotatedBitmap = Bitmap.createBitmap(rotatedBitmap, 0, 0, rotatedBitmap.getWidth(), rotatedBitmap.getHeight(), matrix, true);
        Point dpadRightPoint = new Point(DPAD.right - rotatedBitmap.getWidth(), DPAD.top + (int) (DPAD.bottom / 2) - (int) (rotatedBitmap.getHeight() / 2));
        dPadRight = new BaseObject(dpadRightPoint, rotatedBitmap);
    }

    private void SetNewPlayerPoint() {
        int newPoint = currentLevel.getNewEmptyPoint();
        player.SetPoint(currentLevel.GetFloorPoints().get(newPoint));
        currentLevel.TakeAwayEmptyFloorTiles(newPoint);
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
        currentLevel.UpdateEnemies(player);
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
            //drawing the currentLevel
            drawingTheMap();
            drawingTheStairs();
            drawingTheClutter();
            drawingTheEnemies();
            //drawing the player
            drawLevelObject(player);

            //User Interface
            //THESE NEED TO BE LAST.
            drawingTheDPAD();
            drawingCurrentDepth();
            drawingScore();
            //Unlocking the canvas
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawingCurrentDepth() {
        paint.setColor(Color.BLACK);
        paint.setTextSize(depthTextSize);
        String depth = "Depth: " + currentLevelIndex * 10 + " feet";
        canvas.drawText(depth, 0, depthTextSize, paint);
    }

    private void drawingScore() {
        paint.setColor(Color.BLACK);
        paint.setTextSize(depthTextSize);
        String score = "Gold: " + player.getScore();
        canvas.drawText(score, 0, depthTextSize * 2, paint);
    }

    private void drawLevelObject(BaseObject object) {
        canvas.drawBitmap(
                object.GetBitmap(),
                object.GetX() * mBitMapWidth + (int) (mBitMapWidth / 2) - (object.GetBitmap().getWidth() / 2),
                object.GetY() * mBitMapHeight + (int) (mBitMapHeight / 2) - (object.GetBitmap().getHeight() / 2),
                paint
        );
    }

    private void animateLevelObject(BaseObject object, int offsetX, int offsetY) {
        canvas.drawBitmap(
                object.GetBitmap(),
                object.GetX() * mBitMapWidth + (int) (mBitMapWidth / 2) - (object.GetBitmap().getWidth() / 2),
                object.GetY() * mBitMapHeight + (int) (mBitMapHeight / 2) - (object.GetBitmap().getHeight() / 2),
                paint
        );
    }

    private void drawingTheStairs() {
        drawLevelObject(currentLevel.getStairsUp());
        drawLevelObject(currentLevel.getStairsDown());
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

    private void drawingTheClutter() {
        for (int j = 0; j < currentLevel.getClutter().size(); ++j) {
            drawLevelObject(currentLevel.getClutter().get(j));
        }
    }

    private void drawingTheEnemies() {
        for (int i = 0; i < currentLevel.getEnemies().size(); i++) {
            drawLevelObject(currentLevel.getEnemies().get(i));
        }
    }

    private void drawingTheMap() {
        DestructableObject[][] tempMap = currentLevel.GetCurrentMap();

        for (int row = 0; row < camHeight; row++) {
            for (int col = 0; col < camWidth; col++) {
                canvas.drawBitmap(tempMap[row][col].GetBitmap(),
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

    private boolean DetectButtonPress(PointF pressPoint, Rect rect) {
        if (pressPoint.x <= rect.left + rect.right &&
                pressPoint.x >= rect.left &&
                pressPoint.y <= rect.top + rect.bottom &&
                pressPoint.y >= rect.top) {
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
                PointF pressPoint = new PointF(motionEvent.getX(), motionEvent.getY());
                if (DetectButtonPress(pressPoint, dPadUp.GetCollideRect())) {
                    if (currentLevel.getCellType(player.GetX(), player.GetY() - 1) == Level.CellType.Space) {
                        player.SetY(player.GetY() - 1);
                        CheckStairs();
                    } else {
                        currentLevel.harmObject(player.GetX(), player.GetY() - 1, player.GetAttack(), player.GetDig(), player);
                    }
                    checkPlayerImage(heroUp);
                } else if (DetectButtonPress(pressPoint, dPadDown.GetCollideRect())) {
                    if (currentLevel.getCellType(player.GetX(), player.GetY() + 1) == Level.CellType.Space) {
                        player.SetY(player.GetY() + 1);
                        CheckStairs();
                    } else {
                        currentLevel.harmObject(player.GetX(), player.GetY() + 1, player.GetAttack(), player.GetDig(), player);
                    }
                    checkPlayerImage(heroDown);
                }
                if (DetectButtonPress(pressPoint, dPadLeft.GetCollideRect())) {
                    if (currentLevel.getCellType(player.GetX() - 1, player.GetY()) == Level.CellType.Space) {
                        player.SetX(player.GetX() - 1);
                        CheckStairs();
                    } else {
                        currentLevel.harmObject(player.GetX() - 1, player.GetY(), player.GetAttack(), player.GetDig(), player);
                    }
                    checkPlayerImage(heroLeft);

                } else if (DetectButtonPress(pressPoint, dPadRight.GetCollideRect())) {
                    if (currentLevel.getCellType(player.GetX() + 1, player.GetY()) == Level.CellType.Space) {
                        player.SetX(player.GetX() + 1);
                        CheckStairs();
                    }else {
                        currentLevel.harmObject(player.GetX() + 1, player.GetY(), player.GetAttack(), player.GetDig(), player);
                    }
                    checkPlayerImage(heroRight);
                }
                //AddNewLevel();
                break;
            case MotionEvent.ACTION_DOWN:
                //When the user releases the screen
                //do something here
                break;
        }
        return true;
    }

    private void CheckStairs() {
        if (player.GetPoint().x == currentLevel.getStairsDown().GetPoint().x &&
                player.GetPoint().y == currentLevel.getStairsDown().GetPoint().y) {
            if (currentLevel == Levels.get(Levels.size() - 1)) {
                AddNewLevel();
            } else {
                currentLevel = Levels.get(currentLevelIndex + 1);
                currentLevelIndex++;
            }
            player.SetX(currentLevel.getStairsUp().GetX());
            player.SetY(currentLevel.getStairsUp().GetY());
        } else if (player.GetPoint().x == currentLevel.getStairsUp().GetPoint().x &&
                player.GetPoint().y == currentLevel.getStairsUp().GetPoint().y) {
            if (currentLevelIndex == 0) {
            } else {
                currentLevel = Levels.get(currentLevelIndex - 1);
                currentLevelIndex--;
                player.SetX(currentLevel.getStairsDown().GetX());
                player.SetY(currentLevel.getStairsDown().GetY());
            }
        }
    }

    private void checkPlayerImage(Bitmap image) {
        if (player.GetBitmap() != image) {
            player.SetBitMap(image);
        }
    }

    private void AddNewLevel() {
        Level temp = new Level(levelImages, spaces, walls, screenWidth, screenHeight);
        Levels.add(temp);
        currentLevel = Levels.get(Levels.size() - 1);
        currentLevelIndex++;
        SetNewPlayerPoint();
    }
}
