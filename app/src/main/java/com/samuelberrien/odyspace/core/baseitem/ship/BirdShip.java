package com.samuelberrien.odyspace.core.baseitem.ship;

import android.content.Context;

import com.samuelberrien.odyspace.controls.GamePad;
import com.samuelberrien.odyspace.core.Bonus;
import com.samuelberrien.odyspace.core.fire.Fire;

public class BirdShip extends Ship {
	public BirdShip(Context context, GamePad gamePad) {
		super(context, "obj/ship_bird.obj", "obj/ship_bird.mtl", 50, gamePad);
	}
}
