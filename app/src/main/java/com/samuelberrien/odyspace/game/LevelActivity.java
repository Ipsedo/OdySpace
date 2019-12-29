package com.samuelberrien.odyspace.game;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.main.MainActivity;
import com.samuelberrien.odyspace.main.SettingsView;

public class LevelActivity extends AppCompatActivity {

	public static final String WIN = "WIN";
	public static final String FAIIL = "FAIL";

	public static final String LEVEL_RESULT = "LEVEL_RESULT";
	public static final String LEVEL_SCORE = "LEVEL_SCORE";

	private MyGLSurfaceView mSurfaceView;

	private ProgressBar progressBar;
	private Button pauseButton;

	private SharedPreferences gamePreferences;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSurfaceView = new MyGLSurfaceView(
				getApplicationContext(),
				this,
				Integer.parseInt(super.getIntent().getStringExtra(MainActivity.LEVEL_ID)));
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		gamePreferences = getSharedPreferences(getString(R.string.game_preferences), Context.MODE_PRIVATE);

		progressBar = new ProgressBar(this);
		progressBar.getIndeterminateDrawable()
				.setColorFilter(
						ContextCompat.getColor(this, R.color.pumpkin),
						PorterDuff.Mode.SRC_IN);
		//progressBar.setIndeterminateDrawable(ContextCompat.getDrawable(this, R.drawable.progress_bar));
		//progressBar.setIndeterminateTintList(ColorStateList.valueOf(getColor(R.color.pumpkin)));
		progressBar.setIndeterminate(true);
		progressBar.setVisibility(View.VISIBLE);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);

		pauseButton = (Button) getLayoutInflater().inflate(R.layout.button_pause, null); //new Button(this);
		pauseButton.setVisibility(View.GONE);
		//pauseButton.setBackground(ContextCompat.getDrawable(this, R.drawable.transition_button_main));
		RelativeLayout.LayoutParams tmp = new RelativeLayout.LayoutParams(
				getScreenHeight() / 13,
				getScreenHeight() / 13);
		tmp.setMargins(0, getScreenHeight() / 50, 0, 0);
		pauseButton.setLayoutParams(tmp);
		//pauseButton.setText("❚❚");

		pauseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mSurfaceView.pauseGame();

				View v = getPauseView();

				AlertDialog pauseDialog = new AlertDialog.Builder(LevelActivity.this)
						.setTitle("Pause menu")
						.setView(v)
						.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								finish();
							}
						})
						.setPositiveButton("Resume", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
							}
						})
						.setOnDismissListener(new DialogInterface.OnDismissListener() {
							@Override
							public void onDismiss(DialogInterface dialogInterface) {
								mSurfaceView.resumeGame();
							}
						})
						.setMessage("Current Score : " + mSurfaceView.getScore())
						.create();
				pauseDialog.getWindow()
						.setBackgroundDrawable(
								ContextCompat.getDrawable(
										LevelActivity.this,
										R.drawable.grey_corner));
				pauseDialog.setCanceledOnTouchOutside(false);
				pauseDialog.show();
				pauseDialog.getWindow()
						.setLayout(
								getScreenWidth() * 4 / 5,
								pauseDialog.getWindow().getAttributes().height);
				v.requestLayout();
			}
		});

		RelativeLayout relativeLayout = new RelativeLayout(this);
		relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT));
		relativeLayout.addView(pauseButton);
		relativeLayout.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);

		setContentView(mSurfaceView);

		addContentView(progressBar, params);
		addContentView(relativeLayout, new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT));
	}

	private View getPauseView() {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(
				R.layout.pause_layout,
				(LinearLayout) findViewById(R.id.parameters_layout_id));

		//GameParamsView.buildGameParams(this, layout, gamePreferences);
		((LinearLayout) layout.findViewById(R.id.game_settings_pause))
				.addView(new SettingsView(this));

		SharedPreferences savedShop = getSharedPreferences(
				getString(R.string.shop_preferences),
				Context.MODE_PRIVATE);
		final SharedPreferences savedShip = getSharedPreferences(
				getString(R.string.ship_info_preferences),
				Context.MODE_PRIVATE);

		RadioGroup radioGroup = (RadioGroup) layout.findViewById(R.id.select_weapon_radio_group);
		String[] fireType = getResources().getStringArray(R.array.fire_shop_list_item);
		for (final String fire : fireType) {
			int rBool = fire.equals(getString(R.string.fire_1)) ? R.bool.vrai : R.bool.faux;
			if (savedShop.getBoolean(fire, getResources().getBoolean(rBool))) {
				RadioButton tmpRadioButton = new RadioButton(LevelActivity.this);
				tmpRadioButton.setLayoutParams(new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
				radioGroup.addView(tmpRadioButton);
				tmpRadioButton.setText(fire);

				tmpRadioButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						savedShip.edit()
								.putString(getString(R.string.current_fire_type), fire)
								.apply();
					}
				});
				if (savedShip.getString(
						getString(R.string.current_fire_type),
						getString(R.string.saved_fire_type_default))
						.equals(fire)) {
					tmpRadioButton.setChecked(true);
				}
			}
		}

		radioGroup = (RadioGroup) layout.findViewById(R.id.select_bonus_radio_group);
		final String[] bonus = getResources().getStringArray(R.array.bonus_shop_list_item);
		final int[] duration = getResources().getIntArray(R.array.bonus_duration_shop_list_item);
		for (int i = 0; i < bonus.length; i++) {
			if (!bonus[i].equals(getString(R.string.bought_duration))) {
				int rBool = bonus[i].equals(getString(R.string.bonus_1)) ? R.bool.vrai : R.bool.faux;
				if (savedShop.getBoolean(bonus[i], getResources().getBoolean(rBool))) {
					RadioButton tmpRadioButton = new RadioButton(LevelActivity.this);
					tmpRadioButton.setLayoutParams(new LinearLayout.LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT));
					radioGroup.addView(tmpRadioButton);

					tmpRadioButton.setText(bonus[i]);

					final int index = i;
					tmpRadioButton.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							savedShip.edit()
									.putString(getString(R.string.current_bonus_used),
											bonus[index])
									.putInt(getString(R.string.current_bonus_duration),
											duration[index - 1])
									.apply();
						}
					});
					if (savedShip.getString(getString(R.string.current_bonus_used),
							getString(R.string.bonus_1))
							.equals(bonus[i])) {
						tmpRadioButton.setChecked(true);
					}
				}
			}
		}
		return layout;
	}

	private int getScreenWidth() {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size.x;
	}

	private int getScreenHeight() {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size.y;
	}

	public void loadingLevelFinished() {
		progressBar.setVisibility(View.GONE);
		pauseButton.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onPause() {
		mSurfaceView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSurfaceView.onResume();
	}

	@Override
	public void onBackPressed() {
		if (pauseButton != null && mSurfaceView.isInit()) {
			pauseButton.performClick();
		} else {
			super.onBackPressed();
		}
	}
}
