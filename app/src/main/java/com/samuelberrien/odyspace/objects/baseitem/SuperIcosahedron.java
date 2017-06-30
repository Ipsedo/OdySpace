package com.samuelberrien.odyspace.objects.baseitem;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;

/**
 * Created by samuel on 30/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class SuperIcosahedron extends Icosahedron {
	public SuperIcosahedron(Context context, float[] mPosition, float scale) {
		super(context, mPosition, scale);
	}

	public SuperIcosahedron(ObjModelMtlVBO model, float[] mPosition, float[] mSpeed, float scale) {
		super(model, mPosition, mSpeed, scale);
	}

	@Override
	public int getDamage() {
		return Integer.MAX_VALUE - 1;
	}
}
