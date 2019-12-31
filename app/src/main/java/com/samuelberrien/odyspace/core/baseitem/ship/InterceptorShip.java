package com.samuelberrien.odyspace.core.baseitem.ship;

import android.content.Context;

import com.samuelberrien.odyspace.controls.GamePad;

class InterceptorShip extends Ship {
	InterceptorShip(Context context, GamePad gamePad) {
		super(context, "obj/interceptor.obj", "obj/interceptor.mtl", 500, gamePad);
	}
}
