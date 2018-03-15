package my.application.stephen.runattackdungeon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by Stephen Brasel on 2018-01-18.
 */

public class GameView extends SurfaceView implements Runnable {
    private static final String TAG = "Array out of bounds.";
    private double lag = 0;
    //boolean variable to track if the game is playing or not
    volatile boolean playing;
    private boolean win;
    private boolean checkForInputUP;
    private PointF pressPoint;
    public static boolean changeMap = false;
    //the game thread
    private Thread gameThread = null;

    final double TICKS_RATE = 516.667;

    //These objects will be used for drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    //Screensize
    public static int screenWidth;
    public static int screenHeight;
    private Random rand = new Random();
    //Holder of images.
    private Bitmap[] UserInterface;
    public static Bitmap[] spaces;
    public static Bitmap[] walls;
    public static Bitmap[] imageStairs;
    public static Bitmap[] imageClutter;
    public static Bitmap[] imageEnemy;
    public static Bitmap[] imageFood;
    public static Bitmap[] imagePotion;
    public static Bitmap[] imageScroll;
    public static Bitmap[] imageWeapon;
    public static Bitmap[] imageLight;
    public static Bitmap[] imageMining;
    public static Bitmap[] imageWearables;
    public static Bitmap[] imageNPCLeft;
    public static Bitmap[] imageNPCRight;
    public static Bitmap[] imageNPCUp;
    public static Bitmap[] imageNPCDown;
    private Bitmap npcLeft;
    private Bitmap npcRight;
    private Bitmap npcUp;
    private Bitmap npcDown;
    private boolean checkForInputDOWN;

    public enum spaceTiles {Smooth, Rocky, Potholes, Bumpy, Grassy, Empty}

    public enum wallTiles {Breaking, Sturdy}

    //User Interface.
    //UI info
    private int largeTextSize = 64;
    private int UIOpacity = 150;
    //dPad
    private Rect DPAD;
    private int dpadX = 0;
    private int dpadY = 0;
    private int dpadHeight = 0;
    private int DPADbuffer = 80;
    private ObjectBase dPadUp;
    private ObjectBase dPadDown;
    private ObjectBase dPadLeft;
    private ObjectBase dPadRight;
    //The Dungeon
    private Dungeon dungeon = null;
    public static boolean friendlyFire = false;

    //The Camera
    private Level levelToDraw = null;
    public static int mBitMapHeight;
    public static int mBitMapWidth;
    public static int camOffsetX = 0;
    public static int camOffsetY = 0;
    public static float mainOffsetX = 0;
    public static float mainOffsetY = 0;
    public static int camHeight;
    public static int camWidth;

    //the player
    private Creature player;
    private int startingHealth = 3;

    //Class constructor
    public GameView(Context context, int screenX, int screenY/*, boolean FriendlyFire*/) {
        super(context);

        screenWidth = screenX;
        screenHeight = screenY;

        //Create drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();
        createImages(context);

        //Playable spaces on the currentLevel, i.e., the number of spaces wide and long that the player can potentially use.
        camWidth = screenX / spaces[0].getWidth();
        camHeight = screenY / spaces[0].getHeight();
        mainOffsetX = (screenX % spaces[0].getHeight()) / 2.0f;
        mainOffsetY = (screenY % spaces[0].getHeight()) / 2.0f;

        //friendlyFire = FriendlyFire;

        //Create dungeon
        dungeon = new Dungeon();
        levelToDraw = dungeon.getCurrentLevel();

        //Create Core GamePlay Elements
        createPlayer();

        createDPAD(screenY);
        CameraCenter();
        CameraOffset();
    }

    public void CameraCenter() {
        camOffsetY = player.getY() - (camHeight / 2);
        camOffsetX = player.getX() - (camWidth / 2);
    }

    public void CameraInterpolate() {
        if (camOffsetY > player.getY() - (int) (camHeight * 0.25f)) {
            camOffsetY = player.getY() - (int) (camHeight * 0.25f);
        } else if (camOffsetY < player.getY() - (int) (camHeight * 0.75f)) {
            camOffsetY = player.getY() - (int) (camHeight * 0.75f);
        }
        if (camOffsetX > player.getX() - (int) (camWidth * 0.25f)) {
            camOffsetX = player.getX() - (int) (camWidth * 0.25f);
        } else if (camOffsetX < player.getX() - (int) (camWidth * 0.75f)) {
            camOffsetX = player.getX() - (int) (camWidth * 0.75f);
        }
    }

    public void CameraOffset() {
        CameraInterpolate();
        if (camOffsetY > levelToDraw.getMapHeight() - camHeight) {
            camOffsetY = levelToDraw.getMapHeight() - camHeight;
        } else if (camOffsetY < 0) {
            camOffsetY = 0;
        }
        if (camOffsetX > levelToDraw.getMapWidth() - camWidth) {
            camOffsetX = levelToDraw.getMapWidth() - camWidth;
        } else if (camOffsetX < 0) {
            camOffsetX = 0;
        }
    }

    private void createPlayer() {
        Point tempPoint = new Point(0, 0);
        player = new Creature(
                tempPoint,
                npcDown,
                startingHealth);
        if (dungeon.getCurrentLevel().getNumEmptyCells() > 0) {
            dungeon.getCurrentLevel().giveNewPointToObject(null, player);
        }
        player.setCellType(ObjectDestructible.CellType.Humanoid);
        player.setAttack(5);
        player.setWeapon(new Weapon(0, 0, 0, player.getPoint(), imageWeapon[0], 10));
        player.setMiningTool(new MiningTool(3, 0, player.getPoint(), imageMining[0], 10));
        player.setLightSource(new LightSource(5, 2, 0, player.getPoint(), imageLight[0], 500));
        dungeon.getCurrentLevel().getLevelCreatures().add(player);
        dungeon.setPlayer(player);
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
        UserInterface = new Bitmap[3];//PLAYER
        UserInterface[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.hero);
        //Directional Button
        UserInterface[1] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.directional_button), (int) (mBitMapWidth * 1.05), (int) (mBitMapHeight * 1.05));
        UserInterface[2] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.heart_3d), (int) (mBitMapWidth * 1.05), (int) (mBitMapHeight * 1.05));

        //Create player images
        npcLeft = Bitmap.createBitmap(UserInterface[0], 0, 0, UserInterface[0].getWidth() / 3, UserInterface[0].getHeight() / 4);
        npcLeft = getResizedBitmap(npcLeft, (int) (mBitMapWidth * 0.75), (int) (mBitMapHeight * 0.75));
        Matrix flip = new Matrix();
        flip.postScale(-1, 1, npcLeft.getWidth() / 2.0f, npcLeft.getHeight() / 2.0f);
        npcRight = Bitmap.createBitmap(npcLeft, 0, 0, npcLeft.getWidth(), npcLeft.getHeight(), flip, true);
        npcRight = getResizedBitmap(npcRight, (int) (mBitMapWidth * 0.75), (int) (mBitMapHeight * 0.75));
        npcUp = Bitmap.createBitmap(UserInterface[0], UserInterface[0].getWidth() / 3, UserInterface[0].getHeight() / 4, UserInterface[0].getWidth() / 3, UserInterface[0].getHeight() / 4);
        npcUp = getResizedBitmap(npcUp, (int) (mBitMapWidth * 0.75), (int) (mBitMapHeight * 0.75));
        npcDown = Bitmap.createBitmap(UserInterface[0], UserInterface[0].getWidth() / 3, UserInterface[0].getHeight() / 2, UserInterface[0].getWidth() / 3, UserInterface[0].getHeight() / 4);
        npcDown = getResizedBitmap(npcDown, (int) (mBitMapWidth * 0.75), (int) (mBitMapHeight * 0.75));

        //Clutter
        imageClutter = new Bitmap[6];
        imageClutter[0] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.rock), (int) (mBitMapWidth * 0.75), (int) (mBitMapHeight * 0.75));
        imageClutter[1] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.barrel), (int) (mBitMapWidth * 0.75), (int) (mBitMapHeight * 0.75));
        imageClutter[2] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.chest), (int) (mBitMapWidth * 0.75), (int) (mBitMapHeight * 0.75));
        imageClutter[3] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.coins), mBitMapWidth, mBitMapHeight);
        imageClutter[4] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.diamond), mBitMapWidth, mBitMapHeight);
        imageClutter[5] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.diamond_red), mBitMapWidth, mBitMapHeight);
        //STAIRS
        imageStairs = new Bitmap[2];
        imageStairs[0] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.stairsdown), mBitMapWidth, mBitMapHeight);
        imageStairs[1] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.stairsup), mBitMapWidth, mBitMapHeight);
        //ENEMIES
        imageEnemy = new Bitmap[3];
        imageEnemy[0] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.blob_green), mBitMapWidth, mBitMapHeight);
        imageEnemy[1] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.goblin_easy), mBitMapWidth, mBitMapHeight);
        imageEnemy[2] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.game_asset_call_bull_drawing_beast_human_greek), mBitMapWidth, mBitMapHeight);

        //Consumable
        imageFood = new Bitmap[2];
        imageFood[0] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.apple), mBitMapWidth, mBitMapHeight);
        imageFood[1] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.drumstick), mBitMapWidth, mBitMapHeight);

        imagePotion = new Bitmap[7];
        imagePotion[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.bottles);
        //green
        imagePotion[1] = Bitmap.createBitmap(imagePotion[0], 0, 0, imagePotion[0].getWidth() / 3, imagePotion[0].getHeight() / 2);
        //light blue
        imagePotion[2] = Bitmap.createBitmap(imagePotion[0], imagePotion[0].getWidth() / 3, 0, imagePotion[0].getWidth() / 3, imagePotion[0].getHeight() / 2);
        //black
        imagePotion[3] = Bitmap.createBitmap(imagePotion[0], imagePotion[0].getWidth() * 2 / 3, 0, imagePotion[0].getWidth() / 3, imagePotion[0].getHeight() / 2);
        //red
        imagePotion[4] = Bitmap.createBitmap(imagePotion[0], 0, imagePotion[0].getHeight() / 2, imagePotion[0].getWidth() / 3, imagePotion[0].getHeight() / 2);
        //purple
        imagePotion[5] = Bitmap.createBitmap(imagePotion[0], imagePotion[0].getWidth() / 3, imagePotion[0].getHeight() / 2, imagePotion[0].getWidth() / 3, imagePotion[0].getHeight() / 2);
        //dark blue
        imagePotion[6] = Bitmap.createBitmap(imagePotion[0], imagePotion[0].getWidth() * 2 / 3, imagePotion[0].getHeight() / 2, imagePotion[0].getWidth() / 3, imagePotion[0].getHeight() / 2);
        for (int i = 1; i < 7; i++) {
            imagePotion[i] = getResizedBitmap(imagePotion[i], mBitMapWidth, mBitMapHeight);
        }

        imageScroll = new Bitmap[1];
        imageScroll[0] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.scroll), mBitMapWidth, mBitMapHeight);

        //Weapons
        imageWeapon = new Bitmap[5];
        imageWeapon[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.human_fist_different_sides);
        imageWeapon[0] = Bitmap.createBitmap(imageWeapon[0], imageWeapon[0].getWidth() / 2, 0, (int)(imageWeapon[0].getWidth() * 0.25f), imageWeapon[0].getHeight());
        imageWeapon[0] = getResizedBitmap(imageWeapon[0], mBitMapWidth, mBitMapHeight);
        imageWeapon[1] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.dagger), mBitMapWidth, mBitMapHeight);
        imageWeapon[2] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.sword), mBitMapWidth, mBitMapHeight);
        imageWeapon[3] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.axe), mBitMapWidth, mBitMapHeight);
        imageWeapon[4] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.bow_and_arrow), mBitMapWidth, mBitMapHeight);
        //Light
        imageLight = new Bitmap[2];
        imageLight[0] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.torch), mBitMapWidth, mBitMapHeight);
        imageLight[1] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.lantern), mBitMapWidth, mBitMapHeight);
        //Mining
        imageMining = new Bitmap[2];
        imageMining[0] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.shovel), mBitMapWidth, mBitMapHeight);
        imageMining[1] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.pickaxe), mBitMapWidth, mBitMapHeight);
        //Wearables
        imageWearables = new Bitmap[3];
        imageWearables[0] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ring_gold), mBitMapWidth, mBitMapHeight);
        imageWearables[1] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ring_silver), mBitMapWidth, mBitMapHeight);
        imageWearables[2] = getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.shield_wooden), mBitMapWidth, mBitMapHeight);
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
        dpadHeight = UserInterface[1].getHeight() * 2 + UserInterface[1].getWidth();
        dpadY = screenY - dpadHeight;
        DPAD = new Rect(dpadX + DPADbuffer, dpadY - DPADbuffer, dpadHeight, dpadHeight);

        Point dpadUpPoint = new Point(DPAD.left + (int) (DPAD.width() / 2.0f) - (int) (UserInterface[1].getWidth() / 2.0f), DPAD.top);
        dPadUp = new ObjectBase(dpadUpPoint, UserInterface[1]);

        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        Bitmap rotatedBitmap = Bitmap.createBitmap(UserInterface[1], 0, 0, UserInterface[1].getWidth(), UserInterface[1].getHeight(), matrix, true);
        Point dpadLeftPoint = new Point(DPAD.left, DPAD.top + (int) (DPAD.bottom / 2.0f) - (int) (rotatedBitmap.getHeight() / 2.0f));
        dPadLeft = new ObjectBase(dpadLeftPoint, rotatedBitmap);

        //matrix.postRotate(180);
        rotatedBitmap = Bitmap.createBitmap(rotatedBitmap, 0, 0, rotatedBitmap.getWidth(), rotatedBitmap.getHeight(), matrix, true);
        Point dpadDownPoint = new Point(DPAD.left + (int) (DPAD.width() / 2.0f) - (int) (UserInterface[1].getWidth() / 2.0f), DPAD.top + DPAD.bottom - rotatedBitmap.getHeight());
        dPadDown = new ObjectBase(dpadDownPoint, rotatedBitmap);

        //matrix.postRotate(90);
        rotatedBitmap = Bitmap.createBitmap(rotatedBitmap, 0, 0, rotatedBitmap.getWidth(), rotatedBitmap.getHeight(), matrix, true);
        Point dpadRightPoint = new Point(DPAD.right - rotatedBitmap.getWidth(), DPAD.top + (int) (DPAD.bottom / 2.0f) - (int) (rotatedBitmap.getHeight() / 2.0f));
        dPadRight = new ObjectBase(dpadRightPoint, rotatedBitmap);
    }

    @Override
    public void run() {
        double prev_game_tick = System.nanoTime() / 1000000;
        double current_game_tick;

        while (playing) {
            current_game_tick = System.nanoTime() / 1000000;
            lag += current_game_tick - prev_game_tick;
            prev_game_tick = current_game_tick;

            //to update the frame
            update();

            //to draw the frame
            draw();

            //to control
            control();
        }
        Intent intent = new Intent(getContext(), HighScoresActivity.class);
        startActivity(getContext(), intent, null);
    }

    private void update() {

        if (player.getHP() <= 0) {
            win = false;
            playing = false;
            return;
        }
        if (changeMap) {
            levelToDraw = dungeon.getCurrentLevel();
            changeMap = false;
        }
        if (checkForInputDOWN) {
            //dungeon.goToLevel(player, player.getCurrentDepth() + 1, Dungeon.DirectionToGo.DOWN);
            checkForInputDOWN = false;
        }
        if (checkForInputUP) {
            if (player.getFood() != null) {
                if (DetectButtonPress(pressPoint, player.getFood().getCollideRect())) {
                    player.useFood();
                }
            }
            if (player.getScroll() != null) {
                if (DetectButtonPress(pressPoint, player.getScroll().getCollideRect())) {
                    player.useScroll(dungeon);
                }
            }
            if (player.getPotion() != null) {
                if (DetectButtonPress(pressPoint, player.getPotion().getCollideRect())) {
                    player.usePotion(dungeon.getCurrentLevel());
                }
            }
            if (DetectButtonPress(pressPoint, dPadUp.getCollideRect())) {
                dPadUpPress();
            } else if (DetectButtonPress(pressPoint, dPadDown.getCollideRect())) {
                dPadDownPress();
            }
            if (DetectButtonPress(pressPoint, dPadLeft.getCollideRect())) {
                dPadLeftPress();
            } else if (DetectButtonPress(pressPoint, dPadRight.getCollideRect())) {
                dPadRightPress();
            }

            checkForInputUP = false;
        }

        while (lag >= TICKS_RATE) {
            dungeon.getCurrentLevel().UpdateEnemies(dungeon, player);
            lag -= TICKS_RATE;
        }
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
            //setting the paint color
            paint.setColor(Color.WHITE);
            //drawing the currentLevel
            drawingTheMap();

            //User Interface
            //THESE NEED TO BE LAST.
            paint.setAlpha(UIOpacity);
            drawingTheDPAD();
            drawingTheEquippedItems();
            drawingTheHealth();
            paint.setAlpha(255);
            //UI text
            drawingCurrentDepth();
            drawingScore();
            //Unlocking the canvas
            surfaceHolder.unlockCanvasAndPost(canvas);
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

    //The User Interface
    private void drawingTheDPAD() {
        canvas.drawBitmap(
                dPadUp.getBitmap(),
                dPadUp.getX(),
                dPadUp.getY(),
                paint
        );
        canvas.drawBitmap(
                dPadLeft.getBitmap(),
                dPadLeft.getX(),
                dPadLeft.getY(),
                paint
        );
        canvas.drawBitmap(
                dPadDown.getBitmap(),
                dPadDown.getX(),
                dPadDown.getY(),
                paint
        );
        canvas.drawBitmap(
                dPadRight.getBitmap(),
                dPadRight.getX(),
                dPadRight.getY(),
                paint
        );
    }

    private void drawingCurrentDepth() {
        paint.setColor(Color.BLACK);
        paint.setTextSize(largeTextSize);
        String depth = "Depth: " + player.getCurrentDepth() * 10 + " feet";
        canvas.drawText(depth, mainOffsetX, largeTextSize + mainOffsetY, paint);
    }

    private void drawingScore() {
        paint.setColor(Color.BLACK);
        paint.setTextSize(largeTextSize);
        String score = "Gold: " + player.getScore();
        canvas.drawText(score, mainOffsetX, largeTextSize * 2 + mainOffsetY, paint);
    }

    private void drawingTheHealth() {
        String health = " x " + player.getHP();
        Rect healthBounds = new Rect(0,0,0,0);
        float healthWidth = paint.measureText(health);
        float tempWidth = (camWidth) * mBitMapWidth + mainOffsetX;
        canvas.drawBitmap(UserInterface[2],
                tempWidth - UserInterface[2].getWidth() - healthWidth,
                mainOffsetY,
                paint);
        paint.setColor(Color.BLACK);
        paint.setTextSize(largeTextSize);
        canvas.drawText(health, tempWidth - healthWidth, largeTextSize + mainOffsetY, paint);
    }

    private void drawingTheEquippedItems() {
        float tempWidth = (camWidth - 1) * mBitMapWidth + mainOffsetX;
        float tempHeight = UserInterface[2].getHeight() + mainOffsetY;
        int counter = 0;
        if (player.getWeapon() != null) {
            Rect posRect = new Rect(
                    (int) tempWidth,
                    (int) tempHeight,
                    player.getWeapon().getBitmap().getWidth(),
                    player.getWeapon().getBitmap().getHeight()
            );
            player.getWeapon().setDetectCollision(posRect);
            canvas.drawBitmap(
                    player.getWeapon().getBitmap(),
                    tempWidth,
                    tempHeight,
                    paint
            );
            tempWidth -= mBitMapWidth;
        }
        if (player.getMiningTool() != null) {
            Rect posRect = new Rect(
                    (int) tempWidth,
                    (int) tempHeight,
                    player.getMiningTool().getBitmap().getWidth(),
                    player.getMiningTool().getBitmap().getHeight()
            );
            player.getMiningTool().setDetectCollision(posRect);
            canvas.drawBitmap(
                    player.getMiningTool().getBitmap(),
                    tempWidth,
                    tempHeight,
                    paint
            );
            tempWidth -= mBitMapWidth;
        }
        if (player.getLightSource() != null) {
            Rect posRect = new Rect(
                    (int) tempWidth,
                    (int) tempHeight,
                    player.getLightSource().getBitmap().getWidth(),
                    player.getLightSource().getBitmap().getHeight()
            );
            player.getLightSource().setDetectCollision(posRect);
            canvas.drawBitmap(
                    player.getLightSource().getBitmap(),
                    tempWidth,
                    tempHeight,
                    paint
            );
            tempWidth -= mBitMapWidth;
        }
        if (player.getRing() != null) {
            Rect posRect = new Rect(
                    (int) tempWidth,
                    (int) tempHeight,
                    player.getRing().getBitmap().getWidth(),
                    player.getRing().getBitmap().getHeight()
            );
            player.getRing().setDetectCollision(posRect);
            canvas.drawBitmap(
                    player.getRing().getBitmap(),
                    tempWidth,
                    tempHeight,
                    paint
            );
            tempWidth -= mBitMapWidth;
        }
        if (player.getShield() != null) {
            Rect posRect = new Rect(
                    (int) tempWidth,
                    (int) tempHeight,
                    player.getShield().getBitmap().getWidth(),
                    player.getShield().getBitmap().getHeight()
            );
            player.getShield().setDetectCollision(posRect);
            canvas.drawBitmap(
                    player.getShield().getBitmap(),
                    tempWidth,
                    tempHeight,
                    paint
            );
        }
        drawingTheConsumables(tempWidth, tempHeight);
//        scroll
    }

    private void drawingTheConsumables(float tempWidth, float tempHeight) {
        tempWidth = (camWidth - 1) * mBitMapWidth + mainOffsetX;
        tempHeight = (camHeight - 1) * mBitMapHeight + mainOffsetY;
        if (player.getFood() != null) {
            Rect posRect = new Rect(
                    (int) tempWidth,
                    (int) tempHeight,
                    player.getFood().getBitmap().getWidth(),
                    player.getFood().getBitmap().getHeight()
            );
            player.getFood().setDetectCollision(posRect);
            canvas.drawBitmap(
                    player.getFood().getBitmap(),
                    tempWidth,
                    tempHeight,
                    paint
            );
        }
        tempHeight -= mBitMapHeight;
        if (player.getPotion() != null) {
            Rect posRect = new Rect(
                    (int) tempWidth,
                    (int) tempHeight,
                    player.getPotion().getBitmap().getWidth(),
                    player.getPotion().getBitmap().getHeight()
            );
            player.getPotion().setDetectCollision(posRect);
            canvas.drawBitmap(
                    player.getPotion().getBitmap(),
                    tempWidth,
                    tempHeight,
                    paint
            );
        }
        tempHeight -= mBitMapHeight;
        if (player.getScroll() != null) {
            Rect posRect = new Rect(
                    (int) tempWidth,
                    (int) tempHeight,
                    player.getScroll().getBitmap().getWidth(),
                    player.getScroll().getBitmap().getHeight()
            );
            player.getScroll().setDetectCollision(posRect);
            canvas.drawBitmap(
                    player.getScroll().getBitmap(),
                    tempWidth,
                    tempHeight,
                    paint
            );
        }
    }

    //The Current Map
    private void drawLevelObject(ObjectBase object) {
        if (object != null) {
            if (object.getX() < camOffsetX + camWidth &&
                    object.getY() < camOffsetY + camHeight &&
                    object.getX() >= mainOffsetX / spaces[0].getWidth() &&
                    object.getY() >= mainOffsetY / spaces[0].getHeight()) {
                drawAlignmentHorizontal(object);
            }
        }
    }

    private void drawAlignmentHorizontal(ObjectBase object) {
        switch (object.getAlignmentHorizontal()) {
            case Left:
                drawAlignmentVertical(object,
                        ((object.getX() - camOffsetX) * mBitMapWidth));
                break;
            default:
            case Center:
                drawAlignmentVertical(object,
                        ((object.getX() - camOffsetX) * mBitMapWidth) + ((mBitMapWidth / 2) - (object.getBitmap().getWidth() / 2)));
                break;
            case Right:
                drawAlignmentVertical(object,
                        ((object.getX() - camOffsetX) * mBitMapWidth) + (mBitMapWidth - (object.getBitmap().getWidth() / 2)));
                break;
        }
    }

    private void drawAlignmentVertical(ObjectBase object, float horizontalOffset) {
        switch (object.getAlignmentVertical()) {
            case Top:
                canvas.drawBitmap(
                        object.getBitmap(),
                        horizontalOffset + mainOffsetX,
                        ((object.getY() - camOffsetY) * mBitMapHeight) + mainOffsetY,
                        paint
                );
                break;
            default:
            case Middle:
                canvas.drawBitmap(
                        object.getBitmap(),
                        horizontalOffset + mainOffsetX,
                        ((object.getY() - camOffsetY) * mBitMapHeight) + ((mBitMapHeight / 2) - (object.getBitmap().getHeight() / 2)) + mainOffsetY,
                        paint
                );
                break;
            case Bottom:
                canvas.drawBitmap(
                        object.getBitmap(),
                        horizontalOffset + mainOffsetX,
                        ((object.getY() - camOffsetY) * mBitMapHeight) + (mBitMapHeight - (object.getBitmap().getHeight() / 2)) + mainOffsetY,
                        paint
                );
                break;
        }
    }

    private void drawingTheStairs() {
        drawLevelObject(levelToDraw.getStairsUp());
        drawLevelObject(levelToDraw.getStairsDown());
    }

    private void drawingTheClutter() {
        for (int j = 0; j < levelToDraw.getClutter().size(); ++j) {
            drawLevelObject(levelToDraw.getClutter().get(j));
        }
        for (int j = 0; j < levelToDraw.getFood().size(); ++j) {
            drawLevelObject(levelToDraw.getFood().get(j));
        }
        for (int j = 0; j < levelToDraw.getPotions().size(); ++j) {
            drawLevelObject(levelToDraw.getPotions().get(j));
        }
        for (int j = 0; j < levelToDraw.getLights().size(); ++j) {
            drawLevelObject(levelToDraw.getLights().get(j));
        }
        for (int j = 0; j < levelToDraw.getMiningTools().size(); ++j) {
            drawLevelObject(levelToDraw.getMiningTools().get(j));
        }
        for (int j = 0; j < levelToDraw.getScrolls().size(); ++j) {
            drawLevelObject(levelToDraw.getScrolls().get(j));
        }
        for (int j = 0; j < levelToDraw.getWearables().size(); ++j) {
            drawLevelObject(levelToDraw.getWearables().get(j));
        }
        for (int j = 0; j < levelToDraw.getWeapons().size(); ++j) {
            drawLevelObject(levelToDraw.getWeapons().get(j));
        }
    }

    private void drawingTheEnemies() {
        for (int j = 0; j < levelToDraw.getLevelCreatures().size(); j++) {
            drawLevelObject(levelToDraw.getLevelCreatures().get(j));
        }
    }

    private void drawingTheMap() {
        CameraOffset();
        ArrayList<ObjectDestructible>[][] temp = levelToDraw.getCurrentMap();
        int mapWidth = levelToDraw.getMapWidth();
        int mapHeight = levelToDraw.getMapHeight();
        try {
            for (int row = 0; row < camHeight; row++) {
                for (int col = 0; col < camWidth; col++) {
                    for (int list = 0; list < levelToDraw.getCurrentMap()[row + camOffsetY][col + camOffsetX].size(); list++) {
                        canvas.drawBitmap(levelToDraw.getCurrentMap()
                                        [(row + camOffsetY)]
                                        [(col + camOffsetX)].get(list).getBitmap(),
                                (col * mBitMapWidth) + mainOffsetX,
                                (row * mBitMapHeight) + mainOffsetY,
                                paint);
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.d(TAG, "\ncamHeight = " + camHeight + " camWidth = " + camWidth +
                    "\ncamOffsetY = " + camOffsetY + " camOffSetX = " + camOffsetX +
                    "\nMapHeight = " + levelToDraw.getMapHeight() + " MapWidth = " + levelToDraw.getMapWidth() + "\n", e);

            Log.d(TAG, (camHeight + camOffsetY) + " was greater than " + levelToDraw.getMapHeight() + " OR\n" +
                    (camWidth + camOffsetX) + " was greater than " + levelToDraw.getMapWidth() + " OR\n" +
                    camOffsetY + " was less than 0. OR\n" +
                    camOffsetX + " was less than 0.", e);
        }
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
            case MotionEvent.ACTION_DOWN:
                //When the user releases the screen
                //do something here
                this.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
                checkForInputDOWN = true;
                break;
            case MotionEvent.ACTION_UP:
                //When the user presses on the screen
                //we will do something here
                pressPoint = new PointF(motionEvent.getX(), motionEvent.getY());
                checkForInputUP = true;
                break;
        }
        return true;
    }

    private void dPadUpPress() {
        dungeon.getCurrentLevel().MoveCreatureVertical(dungeon, player, player.getCurrentDepth(), player.getY() - 1);
        player.checkImage(npcUp);
    }

    private void dPadDownPress() {
        dungeon.getCurrentLevel().MoveCreatureVertical(dungeon, player, player.getCurrentDepth(), player.getY() + 1);
        player.checkImage(npcDown);
    }

    private void dPadLeftPress() {
        dungeon.getCurrentLevel().MoveCreatureHorizontal(dungeon, player, player.getCurrentDepth(), player.getX() - 1);
        player.checkImage(npcLeft);
    }

    private void dPadRightPress() {
        dungeon.getCurrentLevel().MoveCreatureHorizontal(dungeon, player, player.getCurrentDepth(), player.getX() + 1);
        player.checkImage(npcRight);
    }

}
