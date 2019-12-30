package com.samuelberrien.odyspace.core.baseitem.ship;

import android.content.Context;

import com.samuelberrien.odyspace.controls.GamePad;
import com.samuelberrien.odyspace.core.Bonus;
import com.samuelberrien.odyspace.core.fire.Fire;

public class SimpleShip extends Ship {
	public SimpleShip(Context context, GamePad gamePad) {
		super(context, "obj/ship_3.obj", "obj/ship_3.mtl", 20, gamePad);
	}
}
