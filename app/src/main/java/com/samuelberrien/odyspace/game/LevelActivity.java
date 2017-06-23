package com.samuelberrien.odyspace.game;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.samuelberrien.odyspace.MainActivity;
import com.samuelberrien.odyspace.R;

public class LevelActivity extends AppCompatActivity {

    public static final String LEVEL_RESULT = "LEVEL_RESULT";
    public static final String LEVEL_SCORE = "LEVEL_SCORE";

    private MyGLSurfaceView mSurfaceView;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mSurfaceView = new MyGLSurfaceView(this.getApplicationContext(), this, Integer.parseInt(super.getIntent().getStringExtra(MainActivity.LEVEL_ID)));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.progressBar = new ProgressBar(this);
        this.progressBar.setIndeterminateTintList(ColorStateList.valueOf(getColor(R.color.pumpkin)));
        this.progressBar.setIndeterminate(true);
        this.progressBar.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);

        setContentView(this.mSurfaceView);

        this.addContentView(this.progressBar, params);
    }

    public void loadingLevelFinished() {
        this.progressBar.setVisibility(View.GONE);
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

    @Override
    public void finish() {
        this.mSurfaceView.onPause();
        super.finish();
    }
}
