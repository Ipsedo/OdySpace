package com.samuelberrien.odyspace.core.baseitem.ship;

import android.content.Context;

import com.samuelberrien.odyspace.controls.GamePad;

class BirdShip extends Ship {
	BirdShip(Context context, GamePad gamePad) {
		super(context, "obj/ship_bird.obj", "obj/ship_bird.mtl", 50, gamePad);
	}
}
