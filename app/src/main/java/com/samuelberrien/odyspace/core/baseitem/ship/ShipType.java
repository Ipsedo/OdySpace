package com.samuelberrien.odyspace.core.baseitem.ship;

import android.content.Context;

import com.samuelberrien.odyspace.controls.GamePad;

import java.util.function.BiFunction;

public enum ShipType {
	SIMPLE_SHIP(SimpleShip::new),
	BIRD_SHIP(BirdShip::new),
	SUPREME_SHIP(SupremeShip::new),
	INTERCEPTOR(InterceptorShip::new);

	private BiFunction<Context, GamePad, Ship> getShipFun;

	ShipType(BiFunction<Context, GamePad, Ship> getShipFun) {
		this.getShipFun = getShipFun;
	}

	public Ship getShip(Context context, GamePad gamePad) {
		return getShipFun.apply(context, gamePad);
	}
}
