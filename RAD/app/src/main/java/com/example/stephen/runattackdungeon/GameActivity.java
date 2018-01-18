package com.example.stephen.runattackdungeon;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        decorView = this.getWindow().getDecorView();
        hideSystemUI();

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Getting display object
        Display display = getWindowManager().getDefaultDisplay();
        //Getting the screen resolution into point object
        Point size = new Point();
        display.getSize(size);
        //Initializing game view object
        gameView = new GameView(this, size.x, size.y);
        setContentView(gameView);
    }

    //pausing the game when activity is paused
    @Override
    protected void onPause() {
        super.onPause();
        showSystemUI();
        gameView.pause();
    }

    // This snippet hides the system bars.
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    // This snippet shows the system bars. It does this by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
    //running the game when activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        gameView.resume();
    }
}
