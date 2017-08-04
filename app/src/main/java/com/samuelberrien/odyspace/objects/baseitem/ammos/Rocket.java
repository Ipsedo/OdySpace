package com.samuelberrien.odyspace.objects.baseitem.ammos;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;

/**
 * Created by samuel on 20/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Rocket extends Ammos {

	private static int Life = 1;

	public Rocket(ObjModelMtlVBO objModelMtl, float[] mPosition, float[] mSpeed, float[] mRotationMatrix, float maxSpeed) {
		super(objModelMtl, mPosition, mSpeed, new float[3], mRotationMatrix, 3f * maxSpeed, 1f, Life);
	}
}
