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
		this.joystick = new Joystick();
		this.fire = new Fire();
		this.remote = new Remote();
		this.boost = new Boost();
		this.controls = new ArrayList<>();

		//Important order of adding !
		this.controls.add(this.fire);
		this.controls.add(this.boost);
		this.controls.add(this.joystick);
		this.controls.add(this.remote);
		this.isPitchInversed = true;
		this.isRollAndYawInversed = false;
	}

	/**
	 * Init the graphics, must be called an OpenGL Thread
	 *
	 * @param context The Application context
	 */
	public void initGraphics(Context context) {
		this.joystick.initGraphics(context);
		this.fire.initGraphics(context);
		this.remote.initGraphics(context);
		this.boost.initGraphics(context);
		this.context = context;
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
		return this.joystick.getStickPosition()[1] * (this.isPitchInversed ? 1f : -1f);
	}

	/**
	 * Get the gamepad roll
	 *
	 * @return roll value between -1 and 1
	 */
	public float getRoll() {
		if (!this.isRollAndYawInversed) {
			return this.joystick.getStickPosition()[0];
		} else {
			return -this.remote.getRemoteLevel();
		}
	}

	/**
	 * Get the gamepad yaw
	 *
	 * @return yaw value between -1 and 1
	 */
	public float getYaw() {
		if (!this.isRollAndYawInversed) {
			return this.remote.getRemoteLevel();
		} else {
			return -this.joystick.getStickPosition()[0];
		}
	}

	/**
	 * Get the gamepad boost
	 *
	 * @return boost value between -1 and 1
	 */
	public float getBoost() {
		return this.boost.getBoost();
	}

	/**
	 * Gamepad fire
	 *
	 * @return true if there is a fire
	 */
	public boolean fire() {
		if (this.fire.isFire()) {
			this.fire.turnOffFire();
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
