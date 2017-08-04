package com.samuelberrien.odyspace.objects.baseitem.ammos;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;

/**
 * Created by samuel on 02/08/17.
 */

public class Laser extends Ammos {

	private static int Life = 50;

	public Laser(ObjModelMtlVBO objModelMtl, float[] mPosition, float[] mSpeed, float[] mRotationMatrix, float maxSpeed, float scale) {
		super(objModelMtl, mPosition, mSpeed, new float[3], mRotationMatrix, maxSpeed * 5f, scale, Life);
	}
}