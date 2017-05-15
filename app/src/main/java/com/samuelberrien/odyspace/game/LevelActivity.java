package com.samuelberrien.odyspace.game;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.samuelberrien.odyspace.MainActivity;

public class LevelActivity extends AppCompatActivity {

    public static final String LEVEL_RESULT = "LEVEL_RESULT";
    public static final String LEVEL_SCORE = "LEVEL_SCORE";

    private MyGLSurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mSurfaceView = new MyGLSurfaceView(this.getApplicationContext(), this, Integer.parseInt(super.getIntent().getStringExtra(MainActivity.LEVEL_ID)));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(this.mSurfaceView);
    }

    @Override
    protected void onPause() {
        this.mSurfaceView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mSurfaceView.onResume();
    }
}
