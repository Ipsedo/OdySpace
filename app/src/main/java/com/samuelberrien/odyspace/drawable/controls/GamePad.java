package com.samuelberrien.odyspace.drawable.controls;

import android.content.Context;
import android.view.MotionEvent;

import com.samuelberrien.odyspace.drawable.GLInfoDrawable;

/**
 * Created by samuel on 09/07/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class GamePad implements GLInfoDrawable {

	private Joystick joystick;
	private Controls controls;
	private Remote remote;
	private int remotePointerID;
	private int joystickPointerID;

	private float limitScreen = 0.8f;

	private boolean isPitchInversed;

	private boolean isRollAndYawInversed;

	public GamePad() {
		this.joystick = new Joystick();
		this.controls = new Controls();
		this.remote = new Remote();
		this.isPitchInversed = true;
		this.isRollAndYawInversed = false;
		this.remotePointerID = -1;
		this.joystickPointerID = -1;
	}

	public void initGraphics(Context context) {
		this.joystick.initGraphics(context);
		this.controls.initGraphics(context);
		this.remote.initGraphics(context);
	}

	public void inversePitch(boolean isInversed) {
		this.isPitchInversed = isInversed;
	}

	public void inverseRollAndYaw(boolean isInversed) {
		this.isRollAndYawInversed = isInversed;
	}

	public float getPitch() {
		return this.joystick.getStickPosition()[1] * (this.isPitchInversed ? 1f : -1f);
	}

	public float getRoll() {
		if (!this.isRollAndYawInversed) {
			return this.joystick.getStickPosition()[0];
		} else {
			return -this.remote.getRemoteLevel();
		}
	}

	public float getYaw() {
		if (!this.isRollAndYawInversed) {
			return this.remote.getRemoteLevel();
		} else {
			return -this.joystick.getStickPosition()[0];
		}
	}

	public float getBoost() {
		return this.controls.getBoost();
	}

	public boolean fire() {
		if (this.controls.isFire()) {
			this.controls.turnOffFire();
			return true;
		}
		return false;
	}

	/**
	 * Update all the game pad items
	 * @param e A Motion Event to handle
	 * @param screenWidth Px value in float precision
	 * @param screenHeight Px value in float precision
	 */
	public void update(MotionEvent e, float screenWidth, float screenHeight) {
		int pointerIndex = e.getActionIndex();
		float x = -(2f * e.getX(pointerIndex) / screenWidth - 1f);
		float y = -(2f * e.getY(pointerIndex) / screenHeight - 1f);
		float ratio = screenWidth / screenHeight;
		switch (e.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				if (e.getX(pointerIndex) / screenHeight > this.limitScreen && !this.controls.isTouchFireButton(x, y, ratio) && !this.controls.isTouchBoost(x, y, ratio)) {
					this.remotePointerID = pointerIndex;
					this.remote.setVisible(true);
					this.remote.updatePosition(x, y, ratio);
				} else if (e.getX(pointerIndex) / screenHeight < this.limitScreen) {
					this.joystickPointerID = pointerIndex;
					this.joystick.setVisible(true);
					this.joystick.updatePosition(x, y, ratio);
				}
				break;
			case MotionEvent.ACTION_UP:
				if (this.remotePointerID == pointerIndex) {
					this.remote.setVisible(false);
					this.remotePointerID = -1;
				} else if (this.joystickPointerID == pointerIndex) {
					this.joystick.setVisible(false);
					this.joystickPointerID = -1;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (e.getPointerCount() > 1) {
					if (!this.controls.isTouchBoost(-(2f * e.getX(1) / screenWidth - 1f), -(2f * e.getY(1) / screenHeight - 1f), ratio) && this.remotePointerID == 1) {
						this.remote.updateStickPosition(-(2f * e.getX(1) / screenWidth - 1f), -(2f * e.getY(1) / screenHeight - 1f), ratio);
					} else if (this.joystickPointerID == 1) {
						this.joystick.updateStickPosition(-(2f * e.getX(1) / screenWidth - 1f), -(2f * e.getY(1) / screenHeight - 1f), ratio);
					}
				}
				if (!this.controls.isTouchBoost(-(2f * e.getX(0) / screenWidth - 1f), -(2f * e.getY(0) / screenHeight - 1f), ratio) && this.remotePointerID == 0) {
					this.remote.updateStickPosition(-(2f * e.getX(0) / screenWidth - 1f), -(2f * e.getY(0) / screenHeight - 1f), ratio);
				} else if (this.joystickPointerID == 0) {
					this.joystick.updateStickPosition(-(2f * e.getX(0) / screenWidth - 1f), -(2f * e.getY(0) / screenHeight - 1f), ratio);
				}
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				if (e.getX(pointerIndex) / screenHeight > this.limitScreen && !this.controls.isTouchFireButton(x, y, ratio) && !this.controls.isTouchBoost(x, y, ratio)) {
					this.remotePointerID = pointerIndex;
					this.joystickPointerID = pointerIndex == 1 ? 0 : 1;
					this.remote.setVisible(true);
					this.remote.updatePosition(x, y, ratio);
				} else if (e.getX(pointerIndex) / screenHeight < this.limitScreen) {
					this.joystickPointerID = pointerIndex;
					this.remotePointerID = pointerIndex == 1 ? 0 : 1;
					this.joystick.setVisible(true);
					this.joystick.updatePosition(x, y, ratio);
				}
				break;
			case MotionEvent.ACTION_POINTER_UP:
				if (this.remotePointerID == pointerIndex) {
					this.remote.setVisible(false);
					this.joystickPointerID = 0;
					this.remotePointerID = -1;
				} else if (this.joystickPointerID == pointerIndex) {
					this.joystick.setVisible(false);
					this.remotePointerID = 0;
					this.joystickPointerID = -1;
				}
				break;
		}
	}

	@Override
	public void draw(float ratio) {
		this.joystick.draw(ratio);
		this.controls.draw(ratio);
		this.remote.draw(ratio);
	}
}
