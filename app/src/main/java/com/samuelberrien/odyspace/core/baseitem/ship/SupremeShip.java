package com.samuelberrien.odyspace.core.baseitem.ship;

import android.content.Context;

import com.samuelberrien.odyspace.controls.GamePad;
import com.samuelberrien.odyspace.core.Bonus;
import com.samuelberrien.odyspace.core.fire.Fire;

public class SupremeShip extends Ship {
	public SupremeShip(Context context, GamePad gamePad) {
		super(context, "obj/ship_supreme.obj", "obj/ship_supreme.mtl", 200, gamePad);
	}
}
