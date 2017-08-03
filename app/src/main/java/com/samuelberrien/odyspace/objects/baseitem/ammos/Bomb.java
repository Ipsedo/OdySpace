package com.samuelberrien.odyspace.objects.baseitem.ammos;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;

/**
 * Created by samuel on 03/08/17.
 */

public class Bomb extends Ammos {

	private static int Life = 3;

	public Bomb(ObjModelMtlVBO objModelMtl, float[] mPosition, float[] mSpeed, float[] mRotationMatrix, float maxSpeed) {
		super(objModelMtl, mPosition, mSpeed, new float[3], mRotationMatrix, 3f * maxSpeed, 2f, Life);
	}
}
