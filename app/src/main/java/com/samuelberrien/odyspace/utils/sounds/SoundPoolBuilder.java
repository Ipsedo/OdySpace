package com.samuelberrien.odyspace.utils.sounds;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.samuelberrien.odyspace.R;

/**
 * Created by samuel on 05/08/17.
 */

public class SoundPoolBuilder {

	private SoundPool soundPool;

	private int simpleBoomId;
	private int bigBoomId;

	private SharedPreferences gamePreferences;
	private Context context;

	public SoundPoolBuilder(Context context) {
		if (Build.VERSION.SDK_INT >= 21) {
			this.soundPool = new SoundPool.Builder().setMaxStreams(20)
					.setAudioAttributes(new AudioAttributes.Builder()
							.setUsage(AudioAttributes.USAGE_GAME)
							.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
							.build())
					.build();
		} else {
			this.soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 1);
		}

		this.simpleBoomId = this.soundPool.load(context, R.raw.simple_boom, 1);
		this.bigBoomId = this.soundPool.load(context, R.raw.big_boom, 1);
		this.gamePreferences = context.getSharedPreferences(context.getString(R.string.game_preferences), Context.MODE_PRIVATE);
		this.context = context;
	}

	private float getVolume() {
		return (float) this.gamePreferences.getInt(context.getString(R.string.saved_sound_effect_volume), 50) / 100f;
	}

	public void playSimpleBoom(float leftLevel, float rightLevel) {
		float volume = this.getVolume();
		this.soundPool.play(simpleBoomId, leftLevel * volume, rightLevel * volume, 1, 0, 1f);
	}

	public void playBigBoom(float leftLevel, float rightLevel) {
		float volume = this.getVolume();
		this.soundPool.play(bigBoomId, leftLevel * volume, rightLevel * volume, 1, 0, 1f);
	}

}
