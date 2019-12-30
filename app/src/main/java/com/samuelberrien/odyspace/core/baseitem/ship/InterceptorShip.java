package com.samuelberrien.odyspace.core.baseitem.ship;

import android.content.Context;

import com.samuelberrien.odyspace.controls.GamePad;
import com.samuelberrien.odyspace.core.Bonus;
import com.samuelberrien.odyspace.core.fire.Fire;

public class InterceptorShip extends Ship {
	public InterceptorShip(Context context, GamePad gamePad) {
		super(context, "obj/interceptor.obj", "obj/interceptor.mtl", 500, gamePad);
	}
}
