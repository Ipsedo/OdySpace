package com.samuelberrien.odyspace.game;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.main.MainActivity;
import com.samuelberrien.odyspace.utils.game.FireType;

public class LevelActivity extends AppCompatActivity {

	public static final String LEVEL_RESULT = "LEVEL_RESULT";
	public static final String LEVEL_SCORE = "LEVEL_SCORE";

	private MyGLSurfaceView mSurfaceView;

	private ProgressBar progressBar;
	private Button pauseButton;

	private SharedPreferences gamePreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.mSurfaceView = new MyGLSurfaceView(this.getApplicationContext(), this, Integer.parseInt(super.getIntent().getStringExtra(MainActivity.LEVEL_ID)));
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		this.gamePreferences = this.getSharedPreferences(getString(R.string.game_preferences), Context.MODE_PRIVATE);
		this.mSurfaceView.setJoystickInversed(this.gamePreferences.getBoolean(getString(R.string.saved_joystick_inversed), getResources().getBoolean(R.bool.saved_joystick_inversed_default)));

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
			@Override
			public void onClick(View view) {
				LevelActivity.this.mSurfaceView.pauseGame();

				LayoutInflater inflater = LevelActivity.this.getLayoutInflater();
				View layout = inflater.inflate(R.layout.parameters_layout, (LinearLayout) findViewById(R.id.parameters_layout_id));

				SeekBar sb = (SeekBar) layout.findViewById(R.id.volume_seek_bar);
				final AudioManager tmp = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				sb.setMax(tmp.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
				sb.setProgress(tmp.getStreamVolume(AudioManager.STREAM_MUSIC));
				sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						tmp.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
					}
				});

				final CheckBox inverseJoystickCheckBox = (CheckBox) layout.findViewById(R.id.inverse_joystick_checkbox);

				inverseJoystickCheckBox.setChecked(LevelActivity.this.gamePreferences.getBoolean(getString(R.string.saved_joystick_inversed), getResources().getBoolean(R.bool.saved_joystick_inversed_default)));
				inverseJoystickCheckBox.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						LevelActivity.this.gamePreferences.edit()
								.putBoolean(getString(R.string.saved_joystick_inversed), inverseJoystickCheckBox.isChecked())
								.apply();
						LevelActivity.this.mSurfaceView.setJoystickInversed(inverseJoystickCheckBox.isChecked());
					}
				});

				SharedPreferences savedShop = LevelActivity.this.getSharedPreferences(getString(R.string.shop_preferences), Context.MODE_PRIVATE);
				final SharedPreferences savedShip = LevelActivity.this.getSharedPreferences(getString(R.string.ship_info_preferences), Context.MODE_PRIVATE);
				final RadioButton fire1RadioButton = (RadioButton) layout.findViewById(R.id.fire_1_radio_button);
				if (!savedShop.getBoolean(getString(R.string.fire_bonus_1), getResources().getBoolean(R.bool.saved_simple_fire_bought_default))) {
					fire1RadioButton.setClickable(false);
				} else {
					fire1RadioButton.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							LevelActivity.this.mSurfaceView.setShipFireType(FireType.SIMPLE_FIRE);
							savedShip.edit()
									.putString(getString(R.string.current_fire_type), getString(R.string.fire_bonus_1))
									.apply();
						}
					});
				}
				final RadioButton fire2RadioButton = (RadioButton) layout.findViewById(R.id.fire_2_radio_button);
				if (!savedShop.getBoolean(getString(R.string.fire_bonus_2), getResources().getBoolean(R.bool.saved_quint_fire_bought_default))) {
					fire2RadioButton.setClickable(false);
				} else {
					fire2RadioButton.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							LevelActivity.this.mSurfaceView.setShipFireType(FireType.QUINT_FIRE);
							savedShip.edit()
									.putString(getString(R.string.current_fire_type), getString(R.string.fire_bonus_2))
									.apply();
						}
					});
				}
				final RadioButton fire3RadioButton = (RadioButton) layout.findViewById(R.id.fire_3_radio_button);
				if (!savedShop.getBoolean(getString(R.string.fire_bonus_3), getResources().getBoolean(R.bool.saved_simple_bomb_bought_default))) {
					fire3RadioButton.setClickable(false);
				} else {
					fire3RadioButton.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							LevelActivity.this.mSurfaceView.setShipFireType(FireType.SIMPLE_BOMB);
							savedShip.edit()
									.putString(getString(R.string.current_fire_type), getString(R.string.fire_bonus_3))
									.apply();
						}
					});
				}
				final RadioButton fire4RadioButton = (RadioButton) layout.findViewById(R.id.fire_4_radio_button);
				if (!savedShop.getBoolean(getString(R.string.fire_bonus_4), getResources().getBoolean(R.bool.saved_triple_sire_bought_default))) {
					fire4RadioButton.setClickable(false);
				} else {
					fire4RadioButton.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							LevelActivity.this.mSurfaceView.setShipFireType(FireType.TRIPLE_FIRE);
							savedShip.edit()
									.putString(getString(R.string.current_fire_type), getString(R.string.fire_bonus_4))
									.apply();
						}
					});
				}
				if (savedShip.getString(getString(R.string.current_fire_type), getString(R.string.saved_fire_type_default)).equals(getString(R.string.fire_bonus_1))) {
					fire1RadioButton.setChecked(true);
				} else if (savedShip.getString(getString(R.string.current_fire_type), getString(R.string.saved_fire_type_default)).equals(getString(R.string.fire_bonus_2))) {
					fire2RadioButton.setChecked(true);
				} else if (savedShip.getString(getString(R.string.current_fire_type), getString(R.string.saved_fire_type_default)).equals(getString(R.string.fire_bonus_3))) {
					fire3RadioButton.setChecked(true);
				} else if (savedShip.getString(getString(R.string.current_fire_type), getString(R.string.saved_fire_type_default)).equals(getString(R.string.fire_bonus_4))) {
					fire4RadioButton.setChecked(true);
				}

				AlertDialog pauseDialog = new AlertDialog.Builder(LevelActivity.this)
						.setTitle("Pause menu")
						.setView(layout)
						.setNegativeButton("Quit level", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								LevelActivity.this.finish();
							}
						})
						.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								LevelActivity.this.mSurfaceView.resumeGame();
							}
						})
						.setOnDismissListener(new DialogInterface.OnDismissListener() {
							@Override
							public void onDismiss(DialogInterface dialogInterface) {
								LevelActivity.this.mSurfaceView.resumeGame();
							}
						})
						.setMessage("Current Score : " + LevelActivity.this.mSurfaceView.getScore())
						.create();
				pauseDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(LevelActivity.this, R.drawable.button_main));
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

	@Override
	public void onBackPressed() {
		if(this.pauseButton != null && this.mSurfaceView.isInit()) {
			this.pauseButton.performClick();
		} else {
			super.onBackPressed();
		}
	}
}
