package com.example.stephen.runattackdungeon;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class GameActivity extends AppCompatActivity {

    newView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        int width = displayMetrics.widthPixels;
        mView = new newView(this, null);
        setContentView(mView);
    }
}

class newView extends SurfaceView {
    Map mMap;
    public newView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMap = new Map(context, 0, 0, 1920, 1200);
    }
}
