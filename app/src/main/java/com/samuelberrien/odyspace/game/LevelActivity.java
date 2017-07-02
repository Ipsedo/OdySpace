package com.samuelberrien.odyspace.game;

import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.samuelberrien.odyspace.main.MainActivity;
import com.samuelberrien.odyspace.R;

public class LevelActivity extends AppCompatActivity {

	public static final String LEVEL_RESULT = "LEVEL_RESULT";
	public static final String LEVEL_SCORE = "LEVEL_SCORE";

	private MyGLSurfaceView mSurfaceView;

	private ProgressBar progressBar;
	private Button pauseButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.mSurfaceView = new MyGLSurfaceView(this.getApplicationContext(), this, Integer.parseInt(super.getIntent().getStringExtra(MainActivity.LEVEL_ID)));
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		this.progressBar = new ProgressBar(this);
		this.progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.pumpkin), PorterDuff.Mode.SRC_IN);
		//this.progressBar.setIndeterminateDrawable(ContextCompat.getDrawable(this, R.drawable.progress_bar));
		//this.progressBar.setIndeterminateTintList(ColorStateList.valueOf(getColor(R.color.pumpkin)));
		this.progressBar.setIndeterminate(true);
		this.progressBar.setVisibility(View.VISIBLE);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

		this.pauseButton = new Button(this);
		this.pauseButton.setVisibility(View.GONE);
		this.pauseButton.setBackground(ContextCompat.getDrawable(this, R.drawable.button_pause_game));
		RelativeLayout.LayoutParams tmp = new RelativeLayout.LayoutParams(this.getScreenHeight() / 15, this.getScreenHeight() / 15);
		tmp.setMargins(0, this.getScreenHeight() / 50, 0, 0);
		this.pauseButton.setLayoutParams(tmp);

		this.pauseButton.setOnClickListener(new View.OnClickListener() {
			//private boolean paused = false;

			@Override
			public void onClick(View view) {
				LevelActivity.this.mSurfaceView.resumeOrPauseGame();
				/*if(!this.paused) {
					LevelActivity.this.pauseButton.setBackground(ContextCompat.getDrawable(LevelActivity.this, R.drawable.button_resume_game));
					this.paused = true;
				} else {
					LevelActivity.this.pauseButton.setBackground(ContextCompat.getDrawable(LevelActivity.this, R.drawable.button_pause_game));
					this.paused = false;
				}*/
				AlertDialog.Builder builder = new AlertDialog.Builder(LevelActivity.this);
				builder.setTitle("Pause menu");
				builder.setNegativeButton("Quit level", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						LevelActivity.this.finish();
					}
				});
				builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						LevelActivity.this.mSurfaceView.resumeOrPauseGame();
					}
				});
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialogInterface) {
						LevelActivity.this.mSurfaceView.resumeOrPauseGame();
					}
				});
				AlertDialog pauseDialog = builder.create();
				pauseDialog.getWindow().setBackgroundDrawableResource(R.drawable.button_main);
				pauseDialog.setCanceledOnTouchOutside(false);
				pauseDialog.show();
			}
		});

		RelativeLayout relativeLayout = new RelativeLayout(this);
		relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
		relativeLayout.addView(this.pauseButton);
		relativeLayout.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);

		setContentView(this.mSurfaceView);

		this.addContentView(this.progressBar, params);
		this.addContentView(relativeLayout, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
	}

	private int getScreenHeight() {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size.y;
	}

	public void loadingLevelFinished() {
		this.progressBar.setVisibility(View.GONE);
		this.pauseButton.setVisibility(View.VISIBLE);
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
