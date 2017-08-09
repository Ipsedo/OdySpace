package com.samuelberrien.odyspace.drawable.controls;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.GLInfoDrawable;

/**
 * Created by samuel on 04/08/17.
 */

abstract class Control implements GLInfoDrawable {

	private int pointerID = -1;

	void setPointerID(int pointerID) {
		this.pointerID = pointerID;
	}

	boolean isCurrentTouched(int pointerID) {
		return this.pointerID == pointerID;
	}

	void switchPointerID(int pointerID) {
		if (this.pointerID >= 0)
			this.pointerID = pointerID == 1 ? 0 : 1;
	}

	void redoPointerID() {
		if (this.pointerID == 1)
			this.pointerID = 0;
	}

	void clear() {
		this.pointerID = -1;
	}

	abstract boolean isTouching(float x, float y, float ratio);

	abstract void updatePosition(float x, float y, float ratio);

	abstract void updateStick(float x, float y, float ratio);

	abstract void initGraphics(Context context);
}
