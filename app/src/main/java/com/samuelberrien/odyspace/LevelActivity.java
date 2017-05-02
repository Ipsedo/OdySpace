package com.samuelberrien.odyspace;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.samuelberrien.odyspace.utils.game.Level;

public class LevelActivity extends AppCompatActivity {

    private MyGLSurfaceView mSurfaceView;

    private Button nextLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mSurfaceView = new MyGLSurfaceView(this.getApplicationContext(), this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(this.mSurfaceView);
    }
}
