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
    //boolean variable to track if the game is playing or not
    volatile boolean playing;
    private boolean win;
    //the game thread
    private Thread gameThread = null;

    final double TICKS_RATE = 1516.667;

    //These objects will be used for drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    //Screensize
    private static int screenWidth;
    private static int screenHeight;
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
    public static Bitmap[] imageNPC;

    //User Interface.
    //dPad
    private Rect DPAD;
    private int dpadX = 0;
    private int dpadY = 0;
    private int dpadHeight = 0;
    private int dPadOpacity = 100;
    private int DPADbuffer = 80;
    private ObjectBase dPadUp;
    private ObjectBase dPadDown;
    private ObjectBase dPadLeft;
    private ObjectBase dPadRight;
    //Level info
    private int depthTextSize = 64;

    //the Levels
    private boolean friendlyFire = false;
    private int numLevels = 2;
    private ArrayList<Level> Levels = new ArrayList<Level>(numLevels);
    private Level currentLevel;
    private int currentLevelIndex = 0;

    private int mBitMapHeight;
    private int mBitMapWidth;
    private int camOffsetX = 0;
    private int camOffsetY = 0;
    public static int camHeight;
    public static int camWidth;

    //the player
    private Bitmap heroLeft;
    private Bitmap heroRight;
    private Bitmap heroUp;
    private Bitmap heroDown;
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

        //friendlyFire = FriendlyFire;

        //Create currentLevel
        AddNewLevel();
//        currentLevel = new Level(screenWidth / spaces[0].getHeight(), screenHeight / spaces[0].getHeight(), true);
//        Levels.add(currentLevel);
        currentLevel = Levels.get(0);
        //currentLevel.GenerateNewMap();


        createDPAD(screenY);

        //Create Core GamePlay Elements
        createPlayer();
        centerTheCamera();
        offsetTheCamera();
    }

    private void centerTheCamera() {
        camOffsetY = player.getY() - camHeight / 2;
        camOffsetX = player.getX() - camWidth / 2;
    }

    private void createPlayer() {
        int newPoint = currentLevel.getNewEmptyPointIndex();
        player = new Creature(
                currentLevel.GetFloorPoints().get(newPoint),
                heroDown,
                startingHealth);
        currentLevel.TakeAwayEmptyFloorTiles(newPoint);
        player.setCreatureType(Creature.CreatureType.Humanoid);
        player.setAttack(5);
        player.setWeapon(new Weapon(0, 0, 0, player.getPoint(), imageWeapon[0], 10));
        player.setMiningTool(new MiningTool(3, 0, player.getPoint(), imageMining[0], 10));
        player.setLightSource(new LightSource(5, 2, 0, player.getPoint(), imageLight[0], 500));
        currentLevel.getCreatures().add(player);
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
        heroLeft = Bitmap.createBitmap(UserInterface[0], 0, 0, UserInterface[0].getWidth() / 3, UserInterface[0].getHeight() / 4);
        heroLeft = getResizedBitmap(heroLeft, (int) (mBitMapWidth * 0.75), (int) (mBitMapHeight * 0.75));
        Matrix flip = new Matrix();
        flip.postScale(-1, 1, heroLeft.getWidth() / 2f, heroLeft.getHeight() / 2f);
        heroRight = Bitmap.createBitmap(heroLeft, 0, 0, heroLeft.getWidth(), heroLeft.getHeight(), flip, true);
        heroRight = getResizedBitmap(heroRight, (int) (mBitMapWidth * 0.75), (int) (mBitMapHeight * 0.75));
        heroUp = Bitmap.createBitmap(UserInterface[0], UserInterface[0].getWidth() / 3, UserInterface[0].getHeight() / 4, UserInterface[0].getWidth() / 3, UserInterface[0].getHeight() / 4);
        heroUp = getResizedBitmap(heroUp, (int) (mBitMapWidth * 0.75), (int) (mBitMapHeight * 0.75));
        heroDown = Bitmap.createBitmap(UserInterface[0], UserInterface[0].getWidth() / 3, UserInterface[0].getHeight() / 2, UserInterface[0].getWidth() / 3, UserInterface[0].getHeight() / 4);
        heroDown = getResizedBitmap(heroDown, (int) (mBitMapWidth * 0.75), (int) (mBitMapHeight * 0.75));

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
        imageWeapon[0] = Bitmap.createBitmap(imageWeapon[0], imageWeapon[0].getWidth() / 2, 0, imageWeapon[0].getWidth() * 1 / 4, imageWeapon[0].getHeight());
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

        Point dpadUpPoint = new Point(DPAD.left + (int) (DPAD.width() / 2) - (int) (UserInterface[1].getWidth() / 2), DPAD.top);
        dPadUp = new ObjectBase(dpadUpPoint, UserInterface[1]);

        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        Bitmap rotatedBitmap = Bitmap.createBitmap(UserInterface[1], 0, 0, UserInterface[1].getWidth(), UserInterface[1].getHeight(), matrix, true);
        Point dpadLeftPoint = new Point(DPAD.left, DPAD.top + (int) (DPAD.bottom / 2) - (int) (rotatedBitmap.getHeight() / 2));
        dPadLeft = new ObjectBase(dpadLeftPoint, rotatedBitmap);

        //matrix.postRotate(180);
        rotatedBitmap = Bitmap.createBitmap(rotatedBitmap, 0, 0, rotatedBitmap.getWidth(), rotatedBitmap.getHeight(), matrix, true);
        Point dpadDownPoint = new Point(DPAD.left + (int) (DPAD.width() / 2) - (int) (UserInterface[1].getWidth() / 2), DPAD.top + DPAD.bottom - rotatedBitmap.getHeight());
        dPadDown = new ObjectBase(dpadDownPoint, rotatedBitmap);

        //matrix.postRotate(90);
        rotatedBitmap = Bitmap.createBitmap(rotatedBitmap, 0, 0, rotatedBitmap.getWidth(), rotatedBitmap.getHeight(), matrix, true);
        Point dpadRightPoint = new Point(DPAD.right - rotatedBitmap.getWidth(), DPAD.top + (int) (DPAD.bottom / 2) - (int) (rotatedBitmap.getHeight() / 2));
        dPadRight = new ObjectBase(dpadRightPoint, rotatedBitmap);
    }

    private void SetNewLevelPoint(Creature creature) {
        int newPoint = currentLevel.getNewEmptyPointIndex();
        creature.setPoint(currentLevel.GetFloorPoints().get(newPoint));
        currentLevel.TakeAwayEmptyFloorTiles(newPoint);
    }

    @Override
    public void run() {
        double lag = 0;
        double prev_game_tick = System.nanoTime() / 1000000;
        double current_game_tick = 0;

        while (playing) {
            current_game_tick = System.nanoTime() / 1000000;
            lag += current_game_tick - prev_game_tick;
            prev_game_tick = current_game_tick;

            while (lag >= TICKS_RATE) {
                //to update the frame
                update();
                lag -= TICKS_RATE;
            }

            //to draw the frame
            draw();

            //to control
            control();
        }
        Intent intent = new Intent(getContext(), HighScoresActivity.class);
        startActivity(getContext(), intent, null);
    }

    private void update() {

        if (player.getHP() <= 0){
            win = false;
            playing = false;
            return;
        }

        currentLevel.UpdateEnemies(player.getPoint(),
                camWidth, camHeight, camOffsetX, camOffsetY,
                currentLevelIndex, friendlyFire);
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
            drawingTheHealth();
            drawingTheEquippedItems();
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

    private void drawLevelObject(ObjectBase object) {
        if (object.getX() < camOffsetX + camWidth &&
                object.getY() < camOffsetY + camHeight) {
            canvas.drawBitmap(
                    object.getBitmap(),
                    ((object.getX() - camOffsetX) * mBitMapWidth) + ((mBitMapWidth / 2) - (object.getBitmap().getWidth() / 2)),
                    ((object.getY() - camOffsetY) * mBitMapHeight) + ((mBitMapHeight / 2) - (object.getBitmap().getHeight() / 2)),
                    paint
            );
        }
    }

    private void drawingTheStairs() {
        drawLevelObject(currentLevel.getStairsUp());
        drawLevelObject(currentLevel.getStairsDown());
    }

    private void drawingTheHealth() {
        int tempWidth = (camWidth - 1) * mBitMapWidth;
        for (int i = 0; i < player.getHP(); i++) {
            canvas.drawBitmap(UserInterface[2],
                    tempWidth - (UserInterface[2].getWidth() * i),
                    0,
                    paint);
        }
    }

    private void drawingTheEquippedItems() {
        int tempWidth = (camWidth - 1) * mBitMapWidth;
        int counter = 0;
        if (player.getWeapon() != null) {
            canvas.drawBitmap(
                    player.getWeapon().getBitmap(),
                    tempWidth,
                    UserInterface[2].getHeight(),
                    paint
            );
            tempWidth -= mBitMapWidth;
        }
        if (player.getMiningTool() != null) {
            canvas.drawBitmap(
                    player.getMiningTool().getBitmap(),
                    tempWidth,
                    UserInterface[2].getHeight(),
                    paint
            );
            tempWidth -= mBitMapWidth;
        }
        if (player.getLightSource() != null) {
            canvas.drawBitmap(
                    player.getLightSource().getBitmap(),
                    tempWidth,
                    UserInterface[2].getHeight(),
                    paint
            );
            tempWidth -= mBitMapWidth;
        }
        if (player.getRing() != null) {
            canvas.drawBitmap(
                    player.getRing().getBitmap(),
                    tempWidth,
                    UserInterface[2].getHeight(),
                    paint
            );
            tempWidth -= mBitMapWidth;
        }
        if (player.getShield() != null) {
            canvas.drawBitmap(
                    player.getShield().getBitmap(),
                    tempWidth,
                    UserInterface[2].getHeight(),
                    paint
            );
        }
        tempWidth = (camWidth - 1) * mBitMapWidth;
        drawingTheConsumables(tempWidth);
//        scroll
    }

    private void drawingTheConsumables(int tempWidth) {
        int tempHeight = (camHeight - 1) * mBitMapHeight;
        if (player.getFood() != null) {
            Rect posRect = new Rect(
                    tempWidth,
                    tempHeight,
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
                    tempWidth,
                    tempHeight,
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
                    tempWidth,
                    tempHeight,
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

    private void drawingTheDPAD() {
        //DRAW THIS LAST.
        //set Paint opacity for DPAD.
        paint.setAlpha(dPadOpacity);

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
        //Reset Paint opacity.
        paint.setAlpha(255);
    }

    private void drawingTheClutter() {
        for (int j = 0; j < currentLevel.getClutter().size(); ++j) {
            drawLevelObject(currentLevel.getClutter().get(j));
        }
        for (int j = 0; j < currentLevel.getFood().size(); ++j) {
            drawLevelObject(currentLevel.getFood().get(j));
        }
        for (int j = 0; j < currentLevel.getPotions().size(); ++j) {
            drawLevelObject(currentLevel.getPotions().get(j));
        }
        for (int j = 0; j < currentLevel.getLights().size(); ++j) {
            drawLevelObject(currentLevel.getLights().get(j));
        }
        for (int j = 0; j < currentLevel.getMiningTools().size(); ++j) {
            drawLevelObject(currentLevel.getMiningTools().get(j));
        }
        for (int j = 0; j < currentLevel.getScrolls().size(); ++j) {
            drawLevelObject(currentLevel.getScrolls().get(j));
        }
        for (int j = 0; j < currentLevel.getWearables().size(); ++j) {
            drawLevelObject(currentLevel.getWearables().get(j));
        }
        for (int j = 0; j < currentLevel.getWeapons().size(); ++j) {
            drawLevelObject(currentLevel.getWeapons().get(j));
        }
    }

    private void drawingTheEnemies() {
        for (int i = 0; i < currentLevel.getCreatures().size(); i++) {
            drawLevelObject(currentLevel.getCreatures().get(i));
        }
    }

    private void drawingTheMap() {
        offsetTheCamera();
        for (int row = 0; row < camHeight; row++) {
            for (int col = 0; col < camWidth; col++) {
                canvas.drawBitmap(currentLevel.GetCurrentMap()
                                [row + camOffsetY]
                                [col + camOffsetX].getBitmap(),
                        col * mBitMapWidth,
                        row * mBitMapHeight,
                        paint);
            }
        }
    }

    private void offsetTheCamera() {
        if (camOffsetY < 0) {
            camOffsetY = 0;
        } else if (camOffsetY > currentLevel.GetMapHeight() - camHeight) {
            camOffsetY = currentLevel.GetMapHeight() - camHeight;
        }
        if (camOffsetX < 0) {
            camOffsetX = 0;
        } else if (camOffsetX > currentLevel.GetMapWidth() - camWidth) {
            camOffsetX = currentLevel.GetMapWidth() - camWidth;
        }
    }

    private boolean DetectButtonPress(PointF pressPoint, Rect rect) {
        if (pressPoint.x <= rect.left + rect.right &&
                pressPoint.x >= rect.left &&
                pressPoint.y <= rect.top + rect.bottom &&
                pressPoint.y >= rect.top) {

            this.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
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
                //if current weapon type is melee:
                if (player.getFood() != null) {
                    if (DetectButtonPress(pressPoint, player.getFood().getCollideRect() ) ){
                        player.heal(player.getFood().getHealing());
                        player.setFood(null);
                    }
                }
                if (player.getScroll() != null){
                    if (DetectButtonPress(pressPoint, player.getScroll().getCollideRect() ) ){
                        goToLevel(player, 0, DirectionToGo.UP);
                        RemoveCreatureFromCurrentLevel(player);
                        currentLevel = Levels.get(0);
                        currentLevelIndex = 0;
                        currentLevel.getCreatures().add(player);
                        player.setX(currentLevel.getStairsUp().getX());
                        player.setY(currentLevel.getStairsUp().getY());
                        centerTheCamera();
                        offsetTheCamera();
                        player.setScroll(null);
                    }
                }
                if (player.getPotion() != null){
                    if (DetectButtonPress(pressPoint, player.getPotion().getCollideRect() ) ){
                        player.getPotion().PotionEffect(player, currentLevel);
                        player.setPotion(null);
                    }
                }
                if (DetectButtonPress(pressPoint, dPadUp.getCollideRect())) {
                    if (currentLevel.getCellType(player.getX(), player.getY() - 1) == Level.CellType.Space ||
                            currentLevel.harmObject(player.getX(), player.getY() - 1, player, currentLevelIndex, friendlyFire)) {
                        player.setY(player.getY() - 1);
                        if (player.getY() < camOffsetY + camHeight / 4) {
                            camOffsetY--;
                            offsetTheCamera();
                        }
                        CheckStairs(player);
                    }
                    checkPlayerImage(heroUp);
                } else if (DetectButtonPress(pressPoint, dPadDown.getCollideRect())) {
                    if (currentLevel.getCellType(player.getX(), player.getY() + 1) == Level.CellType.Space ||
                            currentLevel.harmObject(player.getX(), player.getY() + 1, player, currentLevelIndex, friendlyFire)) {
                        player.setY(player.getY() + 1);
                        if (player.getY() > camOffsetY + (camHeight * 3 / 4)) {
                            camOffsetY++;
                            offsetTheCamera();
                        }
                        CheckStairs(player);
                    }
                    checkPlayerImage(heroDown);
                }
                if (DetectButtonPress(pressPoint, dPadLeft.getCollideRect())) {
                    if (currentLevel.getCellType(player.getX() - 1, player.getY()) == Level.CellType.Space ||
                            currentLevel.harmObject(player.getX() - 1, player.getY(), player, currentLevelIndex, friendlyFire)) {
                        player.setX(player.getX() - 1);
                        if (player.getX() < camOffsetX + camWidth / 4) {
                            camOffsetX--;
                            offsetTheCamera();
                        }
                        CheckStairs(player);
                    }
                    checkPlayerImage(heroLeft);

                } else if (DetectButtonPress(pressPoint, dPadRight.getCollideRect())) {
                    if (currentLevel.getCellType(player.getX() + 1, player.getY()) == Level.CellType.Space ||
                            currentLevel.harmObject(player.getX() + 1, player.getY(), player, currentLevelIndex, friendlyFire)) {
                        player.setX(player.getX() + 1);
                        if (player.getX() > camOffsetX + (camWidth * 3 / 4)) {
                            camOffsetX++;
                            offsetTheCamera();
                        }
                        CheckStairs(player);
                    }
                    checkPlayerImage(heroRight);
                }
                break;
            case MotionEvent.ACTION_DOWN:
                //When the user releases the screen
                //do something here
                break;
        }
        return true;
    }

    private void RemoveCreatureFromCurrentLevel(Creature creature){
        for (int i = 0; i < currentLevel.getCreatures().size(); i++){
            Creature temp = currentLevel.getCreatures().get(i);
            if (temp == creature){
                currentLevel.getCreatures().remove(i);
            }
        }
    }

    private void CheckStairs(Creature creature) {
        if (currentLevel.getStairsDown().getPoint().x == creature.getPoint().x &&
                currentLevel.getStairsDown().getPoint().y == creature.getPoint().y ) {
            goToLevel(creature, creature.getCurrentDepth() + 1, DirectionToGo.DOWN);
        } else if (currentLevel.getStairsUp().getPoint().x == creature.getPoint().x &&
                currentLevel.getStairsUp().getPoint().y == creature.getPoint().y) {
            goToLevel(creature, creature.getCurrentDepth() - 1, DirectionToGo.UP);
        }
    }
    public enum DirectionToGo {DOWN, UP}

    private void goToLevel(Creature creature, int levelToGoTo, DirectionToGo direction){
        switch  (direction){
            case DOWN:
                if (currentLevel == Levels.get(Levels.size() - 1)) {
                    AddNewLevel();
                    //SetNewLevelPoint(creature);
                }
                RemoveCreatureFromCurrentLevel(creature);
                if (creature == player) {
                    currentLevel = Levels.get(levelToGoTo);
                }
                creature.setCurrentDepth(levelToGoTo);
                currentLevel.getCreatures().add(creature);
                creature.setX(currentLevel.getStairsUp().getX());
                creature.setY(currentLevel.getStairsUp().getY());
                if (creature == player) {
                    centerTheCamera();
                    offsetTheCamera();
                }
                break;
            case UP:
                if (currentLevelIndex == 0) {
                } else {

                    RemoveCreatureFromCurrentLevel(creature);
                    if (creature == player) {
                        currentLevel = Levels.get(levelToGoTo);
                    }
                    creature.setCurrentDepth(levelToGoTo);
                    currentLevel.getCreatures().add(creature);
                    creature.setX(currentLevel.getStairsDown().getX());
                    creature.setY(currentLevel.getStairsDown().getY());
                    if (creature == player) {
                        centerTheCamera();
                        offsetTheCamera();
                    }
                }
                break;
        }
        if (creature == player){
            currentLevelIndex = creature.getCurrentDepth();
        }
    }
    private void AddNewLevel() {
        Level temp;
        int Width;
        int Height;
        if (currentLevel != null) {
            Width = currentLevel.GetMapWidth() + currentLevelIndex;
            if (Width > (screenWidth / spaces[0].getHeight()) * 3) {
                Width = (screenWidth / spaces[0].getHeight()) * 3;
            }
            Height = currentLevel.GetMapHeight() + currentLevelIndex;
            if (Height > (screenHeight / spaces[0].getHeight()) * 3) {
                Height = (screenHeight / spaces[0].getHeight()) * 3;
            }
        } else  {
            Width = screenWidth / spaces[0].getHeight();
            Height =screenHeight / spaces[0].getHeight();
        }

        if (currentLevelIndex % 3 == 0 && currentLevelIndex != 0) {
            temp = new Level(Width, Height, false);
        } else {
            temp = new Level(Width, Height, true);
        }
        Levels.add(temp);
    }

    private void checkPlayerImage(Bitmap image) {
        if (player.getBitmap() != image) {
            player.setBitMap(image);
        }
    }

}
