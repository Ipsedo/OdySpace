package com.samuelberrien.odyspace.core.baseitem.ship;

import android.content.Context;

import com.samuelberrien.odyspace.controls.GamePad;

class SupremeShip extends Ship {
	SupremeShip(Context context, GamePad gamePad) {
		super(context, "obj/ship_supreme.obj", "obj/ship_supreme.mtl", 200, gamePad);
	}
}
