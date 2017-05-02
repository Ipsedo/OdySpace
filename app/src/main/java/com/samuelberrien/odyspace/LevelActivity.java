package com.samuelberrien.odyspace;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LevelActivity extends AppCompatActivity {

    public static final String RESULT = "RESULT";

    private MyGLSurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mSurfaceView = new MyGLSurfaceView(this.getApplicationContext(), this, Integer.parseInt(super.getIntent().getStringExtra(MainActivity.LEVEL_ID)));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(this.mSurfaceView);
    }

    @Override
    protected void onPause(){
        this.mSurfaceView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mSurfaceView.onResume();
    }
}
