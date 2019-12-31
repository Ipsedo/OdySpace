package com.samuelberrien.odyspace.core.baseitem.ship;

import android.content.Context;

import com.samuelberrien.odyspace.controls.GamePad;

class SimpleShip extends Ship {
	SimpleShip(Context context, GamePad gamePad) {
		super(context, "obj/ship_3.obj", "obj/ship_3.mtl", 20, gamePad);
	}
}
