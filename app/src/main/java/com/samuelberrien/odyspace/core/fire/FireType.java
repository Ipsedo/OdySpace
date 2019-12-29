package com.samuelberrien.odyspace.core.fire;

import android.arch.core.util.Function;
import android.content.Context;
import android.support.v4.util.Consumer;

import com.samuelberrien.odyspace.R;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by samuel on 03/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public enum FireType {

	SIMPLE_FIRE(R.string.fire_1, SimpleFire::new),
	QUINT_FIRE(R.string.fire_2, QuintFire::new),
	SIMPLE_BOMB(R.string.fire_3, SimpleBomb::new),
	TRIPLE_FIRE(R.string.fire_4, TripleFire::new),
	LASER(R.string.fire_5, Laser::new),
	TORUS(R.string.fire_6, Torus::new),
	GUIDED_MISSILE(R.string.fire_7, GuidedMissile::new);

	private int rIdString;

	private Function<Context, Fire> getFireFun;

	FireType(int rIdString, Function<Context, Fire> getFireFun) {
		this.rIdString = rIdString;
		this.getFireFun = getFireFun;
	}

	public String getName(Context context) {
		return context.getString(rIdString);
	}

	public Fire getFire(Context glContext) {
		return getFireFun.apply(glContext);
	}
}
