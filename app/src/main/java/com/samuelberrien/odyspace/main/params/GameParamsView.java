package com.samuelberrien.odyspace.main.params;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.samuelberrien.odyspace.R;

/**
 * Created by samuel on 12/10/17.
 */

public class GameParamsView
		extends LinearLayout
		implements SharedPreferences.OnSharedPreferenceChangeListener {

	private SharedPreferences gamePreferences;

	private Context context;

	private View v;

	private final AudioManager tmp;

	private final SeekBar sb1;
	private final SeekBar effectVolumeSeekBar;
	private final CheckBox inverseJoystickCheckBox;
	private final CheckBox switchYawRollCheckBox;

	public GameParamsView(Activity activity) {
		super(activity);

		v = activity.getLayoutInflater().inflate(R.layout.game_params, null);

		context = activity;

		gamePreferences = activity.getSharedPreferences(
				activity.getString(R.string.game_preferences),
				Context.MODE_PRIVATE);

		tmp = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		sb1 = (SeekBar) v.findViewById(R.id.device_volume_seek_bar);
		effectVolumeSeekBar = (SeekBar) v.findViewById(R.id.effect_volume_seek_bar);
		inverseJoystickCheckBox = (CheckBox) v.findViewById(R.id.inverse_joystick_checkbox);
		switchYawRollCheckBox = (CheckBox) v.findViewById(R.id.switch_yaw_roll_checkbox);

		initSettings();

		addView(v, new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

		gamePreferences.registerOnSharedPreferenceChangeListener(this);
	}

	private void initSettings() {
		sb1.setMax(tmp.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		sb1.setProgress(tmp.getStreamVolume(AudioManager.STREAM_MUSIC));
		sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				tmp.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_VIBRATE);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		context.getContentResolver()
				.registerContentObserver(
						android.provider.Settings.System.CONTENT_URI,
						true,
						new ContentObserver(new Handler()) {
							@Override
							public boolean deliverSelfNotifications() {
								return super.deliverSelfNotifications();
							}

							@Override
							public void onChange(boolean selfChange) {
								super.onChange(selfChange);
								sb1.setProgress(tmp.getStreamVolume(AudioManager.STREAM_MUSIC));
							}
						});

		effectVolumeSeekBar.setMax(100);
		effectVolumeSeekBar.setProgress(
				gamePreferences.getInt(context.getString(R.string.saved_sound_effect_volume), 50));
		effectVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				gamePreferences.edit()
						.putInt(context.getString(R.string.saved_sound_effect_volume), progress)
						.apply();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		inverseJoystickCheckBox.setChecked(
				gamePreferences.getBoolean(context.getString(R.string.saved_joystick_inversed),
						context.getResources()
								.getBoolean(R.bool.saved_joystick_inversed_default)));
		inverseJoystickCheckBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				gamePreferences.edit()
						.putBoolean(context.getString(R.string.saved_joystick_inversed),
								inverseJoystickCheckBox.isChecked())
						.apply();
			}
		});

		switchYawRollCheckBox.setChecked(gamePreferences.getBoolean(
				context.getString(R.string.saved_yaw_roll_switched),
				context.getResources()
						.getBoolean(R.bool.saved_yaw_roll_switched_default)));
		switchYawRollCheckBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				gamePreferences.edit()
						.putBoolean(context.getString(R.string.saved_yaw_roll_switched),
								switchYawRollCheckBox.isChecked())
						.apply();
			}
		});
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
		initSettings();
	}
}
