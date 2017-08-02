package com.samuelberrien.odyspace.game;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.StrictMode;
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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

	private static Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getApplicationContext();

		this.mSurfaceView = new MyGLSurfaceView(this.getApplicationContext(), this, Integer.parseInt(super.getIntent().getStringExtra(MainActivity.LEVEL_ID)));
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		this.gamePreferences = this.getSharedPreferences(getString(R.string.game_preferences), Context.MODE_PRIVATE);
		this.mSurfaceView.inversePitchJoystick(this.gamePreferences.getBoolean(getString(R.string.saved_joystick_inversed), getResources().getBoolean(R.bool.saved_joystick_inversed_default)));
		this.mSurfaceView.inverseYawRoll(this.gamePreferences.getBoolean(getString(R.string.saved_yaw_roll_switched), getResources().getBoolean(R.bool.saved_yaw_roll_switched_default)));

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
						LevelActivity.this.mSurfaceView.inversePitchJoystick(inverseJoystickCheckBox.isChecked());
					}
				});

				final CheckBox switchYawRollCheckBox = (CheckBox) layout.findViewById(R.id.switch_yaw_roll_checkbox);
				switchYawRollCheckBox.setChecked(LevelActivity.this.gamePreferences.getBoolean(getString(R.string.saved_yaw_roll_switched), getResources().getBoolean(R.bool.saved_yaw_roll_switched_default)));
				switchYawRollCheckBox.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						LevelActivity.this.gamePreferences.edit()
								.putBoolean(getString(R.string.saved_yaw_roll_switched), switchYawRollCheckBox.isChecked())
								.apply();
						LevelActivity.this.mSurfaceView.inverseYawRoll(switchYawRollCheckBox.isChecked());
					}
				});

				SharedPreferences savedShop = LevelActivity.this.getSharedPreferences(getString(R.string.shop_preferences), Context.MODE_PRIVATE);
				final SharedPreferences savedShip = LevelActivity.this.getSharedPreferences(getString(R.string.ship_info_preferences), Context.MODE_PRIVATE);

				RadioGroup radioGroup = (RadioGroup) layout.findViewById(R.id.select_weapon_radio_group);
				String[] fireType = LevelActivity.this.getResources().getStringArray(R.array.fire_shop_list_item);
				for (final String fire : fireType) {
					RadioButton tmpRadioButton = new RadioButton(LevelActivity.this);
					tmpRadioButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
					radioGroup.addView(tmpRadioButton);

					tmpRadioButton.setText(fire);

					int rBool = R.bool.faux;
					final FireType fireTypeEnum;
					if (fire.equals(getString(R.string.fire_bonus_4))) {
						fireTypeEnum = FireType.TRIPLE_FIRE;
					} else if (fire.equals(getString(R.string.fire_bonus_2))) {
						fireTypeEnum = FireType.QUINT_FIRE;
					} else if (fire.equals(getString(R.string.fire_bonus_3))) {
						fireTypeEnum = FireType.SIMPLE_BOMB;
					} else if(fire.equals(getString(R.string.fire_bonus_5))) {
						fireTypeEnum = FireType.LASER;
					} else {
						rBool = R.bool.vrai;
						fireTypeEnum = FireType.SIMPLE_FIRE;
					}

					if (savedShop.getBoolean(fire, getResources().getBoolean(rBool))) {
						tmpRadioButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								LevelActivity.this.mSurfaceView.setShipFireType(fireTypeEnum);
								savedShip.edit()
										.putString(getString(R.string.current_fire_type), fire)
										.apply();
							}
						});
					} else {
						tmpRadioButton.setClickable(false);
					}
					if (savedShip.getString(getString(R.string.current_fire_type), getString(R.string.saved_fire_type_default)).equals(fire)) {
						tmpRadioButton.setChecked(true);
					}
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

	public static Context getAppContext() {
		while (context == null)
			try {
				Thread.sleep(10L);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		return context;
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
		if (this.pauseButton != null && this.mSurfaceView.isInit()) {
			this.pauseButton.performClick();
		} else {
			super.onBackPressed();
		}
	}
}
