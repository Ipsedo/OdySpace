package com.samuelberrien.odyspace.controls;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.MotionEvent;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.GLInfoDrawable;

import java.util.ArrayList;

/**
 * Created by samuel on 09/07/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class GamePad implements GLInfoDrawable, SharedPreferences.OnSharedPreferenceChangeListener {

	private Joystick joystick;
	private Fire fire;
	private Remote remote;
	private Boost boost;

	private ArrayList<Control> controls;

	static float limitScreen = -0.2f;

	private boolean isPitchInversed;

	private boolean isRollAndYawInversed;

	private Context context;

	/**
	 * Create a new GamePad instance
	 */
	public GamePad() {
		joystick = new Joystick();
		fire = new Fire();
		remote = new Remote();
		boost = new Boost();
		controls = new ArrayList<>();

		controls.add(fire);
		controls.add(boost);
		controls.add(joystick);
		controls.add(remote);
		isPitchInversed = true;
		isRollAndYawInversed = false;
	}

	/**
	 * Init the graphics, must be called an OpenGL Thread
	 *
	 * @param context The Application context
	 */
	public void initGraphics(Context context) {
		joystick.initGraphics(context);
		fire.initGraphics(context);
		remote.initGraphics(context);
		boost.initGraphics(context);
		context = context;
		SharedPreferences gamePreference = context.getSharedPreferences(context.getString(R.string.game_preferences), Context.MODE_PRIVATE);
		isPitchInversed = gamePreference.getBoolean(context.getString(R.string.saved_joystick_inversed), context.getResources().getBoolean(R.bool.vrai));
		isRollAndYawInversed = gamePreference.getBoolean(context.getString(R.string.saved_yaw_roll_switched), context.getResources().getBoolean(R.bool.faux));
		gamePreference.registerOnSharedPreferenceChangeListener(this);
	}

	/**
	 * Get the gamepad pitch
	 *
	 * @return pitch value between -1 and 1
	 */
	public float getPitch() {
		return joystick.getStickPosition()[1] * (isPitchInversed ? 1f : -1f);
	}

	/**
	 * Get the gamepad roll
	 *
	 * @return roll value between -1 and 1
	 */
	public float getRoll() {
		if (!isRollAndYawInversed) {
			return joystick.getStickPosition()[0];
		} else {
			return -remote.getRemoteLevel();
		}
	}

	/**
	 * Get the gamepad yaw
	 *
	 * @return yaw value between -1 and 1
	 */
	public float getYaw() {
		if (!isRollAndYawInversed) {
			return remote.getRemoteLevel();
		} else {
			return -joystick.getStickPosition()[0];
		}
	}

	/**
	 * Get the gamepad boost
	 *
	 * @return boost value between -1 and 1
	 */
	public float getBoost() {
		return boost.getBoost();
	}

	/**
	 * Gamepad fire
	 *
	 * @return true if there is a fire
	 */
	public boolean fire() {
		if (fire.isFire()) {
			fire.turnOffFire();
			return true;
		}
		return false;
	}

	/**
	 * Update all the game pad items
	 *
	 * @param e            A Motion Event to handle
	 * @param screenWidth  Px value in float precision
	 * @param screenHeight Px value in float precision
	 */
	public void update(MotionEvent e, float screenWidth, float screenHeight) {
		int pointerID = e.getPointerId(e.getActionIndex());
		float x = -(2f * e.getX(e.findPointerIndex(pointerID)) / screenWidth - 1f);
		float y = -(2f * e.getY(e.findPointerIndex(pointerID)) / screenHeight - 1f);
		float ratio = screenWidth / screenHeight;
		switch (e.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				for (Control c : controls)
					if (c.canCatchID(x, y, ratio)) {
						c.setPointerID(pointerID);
						c.updatePosition(x, y, ratio);
						break;
					}
				break;
			case MotionEvent.ACTION_UP:
				for (Control c : controls)
					if (c.isCurrentTouched(pointerID)) {
						c.clear();
						break;
					}
				break;
			case MotionEvent.ACTION_MOVE:
				for (int i = 0; i < e.getPointerCount(); i++)
					for (Control c : controls)
						if (c.isCurrentTouched(e.getPointerId(i)))
							c.updateStick(-(2f * e.getX(i) / screenWidth - 1f), -(2f * e.getY(i) / screenHeight - 1f), ratio);
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				for (Control c : controls)
					if (c.canCatchID(x, y, ratio) && !c.isActive()) {
						c.setPointerID(pointerID);
						c.updatePosition(x, y, ratio);
						break;
					}

				break;
			case MotionEvent.ACTION_POINTER_UP:
				for (Control c : controls)
					if (c.isCurrentTouched(pointerID)) {
						c.clear();
						break;
					}
				break;
		}
	}

	@Override
	public void draw(float ratio) {
		for (Control c : controls)
			c.draw(ratio);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(context.getString(R.string.saved_joystick_inversed))) {
			isPitchInversed = sharedPreferences.getBoolean(key, context.getResources().getBoolean(R.bool.vrai));
		} else if (key.equals(context.getString(R.string.saved_yaw_roll_switched))) {
			isRollAndYawInversed = sharedPreferences.getBoolean(context.getString(R.string.saved_yaw_roll_switched), context.getResources().getBoolean(R.bool.faux));
		}
	}
}
