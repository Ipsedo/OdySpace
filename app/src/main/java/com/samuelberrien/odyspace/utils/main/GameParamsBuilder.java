package com.samuelberrien.odyspace.utils.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;

import com.samuelberrien.odyspace.R;

/**
 * Created by samuel on 12/10/17.
 */

public class GameParamsBuilder {

	public static void buildGameParams(final Activity activity, View v, final SharedPreferences gamePreferences) {
		final SeekBar sb1 = (SeekBar) v.findViewById(R.id.device_volume_seek_bar);
		final AudioManager tmp = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
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

		activity.getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, new ContentObserver(new Handler()) {
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

		SeekBar sb2 = (SeekBar) v.findViewById(R.id.effect_volume_seek_bar);
		sb2.setMax(100);
		sb2.setProgress(gamePreferences.getInt(activity.getString(R.string.saved_sound_effect_volume), 100));
		sb2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				gamePreferences.edit()
						.putInt(activity.getString(R.string.saved_sound_effect_volume), progress)
						.apply();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		final CheckBox inverseJoystickCheckBox = (CheckBox) v.findViewById(R.id.inverse_joystick_checkbox);
		inverseJoystickCheckBox.setChecked(gamePreferences.getBoolean(activity.getString(R.string.saved_joystick_inversed), activity.getResources().getBoolean(R.bool.saved_joystick_inversed_default)));
		inverseJoystickCheckBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				gamePreferences.edit()
						.putBoolean(activity.getString(R.string.saved_joystick_inversed), inverseJoystickCheckBox.isChecked())
						.apply();
			}
		});

		final CheckBox switchYawRollCheckBox = (CheckBox) v.findViewById(R.id.switch_yaw_roll_checkbox);
		switchYawRollCheckBox.setChecked(gamePreferences.getBoolean(activity.getString(R.string.saved_yaw_roll_switched), activity.getResources().getBoolean(R.bool.saved_yaw_roll_switched_default)));
		switchYawRollCheckBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				gamePreferences.edit()
						.putBoolean(activity.getString(R.string.saved_yaw_roll_switched), switchYawRollCheckBox.isChecked())
						.apply();
			}
		});
	}
}
