package com.samuelberrien.odyspace.drawable.controls;

import android.content.Context;
import android.view.MotionEvent;

import com.samuelberrien.odyspace.drawable.GLInfoDrawable;

import java.util.ArrayList;

/**
 * Created by samuel on 09/07/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class GamePad implements GLInfoDrawable {

	private Joystick joystick;
	private Fire fire;
	private Remote remote;
	private Boost boost;

	private ArrayList<Control> controls;

	static float limitScreen = -0.2f;

	private boolean isPitchInversed;

	private boolean isRollAndYawInversed;

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
	}

	/**
	 * Inverse pitch
	 *
	 * @param isInversed A boolean (true if inversed, false otherwise)
	 */
	public void inversePitch(boolean isInversed) {
		this.isPitchInversed = isInversed;
	}

	/**
	 * Inverse Roll and Yaw from joystick and remote
	 *
	 * @param isInversed A boolean (true if inversed, false otherwise)
	 */
	public void inverseRollAndYaw(boolean isInversed) {
		this.isRollAndYawInversed = isInversed;
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
		int pointerIndex = e.getActionIndex();
		float x = -(2f * e.getX(pointerIndex) / screenWidth - 1f);
		float y = -(2f * e.getY(pointerIndex) / screenHeight - 1f);
		float ratio = screenWidth / screenHeight;
		switch (e.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				for (Control c : controls)
					if (c.isTouching(x, y, ratio)) {
						c.setPointerID(pointerIndex);
						c.updatePosition(x, y, ratio);
						break;
					}
				break;
			case MotionEvent.ACTION_UP:
				for (Control c : controls)
					if (c.isCurrentTouched(pointerIndex)) {
						c.clear();
						break;
					}
				break;
			case MotionEvent.ACTION_MOVE:
				if (e.getPointerCount() > 1) {
					for (Control c : controls)
						if (c.isCurrentTouched(1)) {
							c.updateStick(-(2f * e.getX(1) / screenWidth - 1f), -(2f * e.getY(1) / screenHeight - 1f), ratio);
							break;
						}
				}
				for (Control c : controls)
					if (c.isCurrentTouched(0)) {
						c.updateStick(-(2f * e.getX(0) / screenWidth - 1f), -(2f * e.getY(0) / screenHeight - 1f), ratio);
						break;
					}
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				if (e.getPointerCount() == 2)
					for (Control c1 : controls)
						if (c1.isTouching(x, y, ratio)) {
							for (Control c2 : controls)
								if (!c2.equals(c1))
									c2.switchPointerID(pointerIndex);
							c1.setPointerID(pointerIndex);
							c1.updatePosition(x, y, ratio);
							break;
						}
				break;
			case MotionEvent.ACTION_POINTER_UP:
				for (Control c1 : controls)
					if (c1.isCurrentTouched(pointerIndex)) {
						c1.clear();
						for (Control c2 : controls)
							if (!c2.equals(c1))
								c2.redoPointerID();
						break;
					}
				break;
		}
	}

	@Override
	public void draw(float ratio) {
		this.joystick.draw(ratio);
		this.fire.draw(ratio);
		this.remote.draw(ratio);
		this.boost.draw(ratio);
	}
}
