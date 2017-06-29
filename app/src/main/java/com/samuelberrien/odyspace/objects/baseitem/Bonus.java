package com.samuelberrien.odyspace.objects.baseitem;

import android.content.Context;

/**
 * Created by samuel on 03/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Bonus extends BaseItem {
	public Bonus(Context context, String objFileName, String mtlFileName, float[] mPosition, float[] mSpeed, float[] mAcceleration) {
		super(context, objFileName, mtlFileName, 1f, 0f, false, 1, mPosition, mSpeed, mAcceleration, 1f);
	}

	@Override
	public int getDamage() {
		return 0;
	}
}
