package com.samuelberrien.odyspace.controls;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.GLInfoDrawable;

/**
 * Created by samuel on 04/08/17.
 */

abstract class Control implements GLInfoDrawable {

	private final int INVALID_ID = -1;

	private int pointerID = INVALID_ID;

	void setPointerID(int pointerID) {
		this.pointerID = pointerID;
	}

	boolean isCurrentTouched(int pointerID) {
		return this.pointerID == pointerID;
	}

	boolean isActive() {
		return pointerID != INVALID_ID;
	}

	void clear() {
		this.pointerID = INVALID_ID;
	}

	abstract boolean canCatchID(float x, float y, float ratio);

	abstract void updatePosition(float x, float y, float ratio);

	abstract void updateStick(float x, float y, float ratio);

	abstract void initGraphics(Context context);
}
