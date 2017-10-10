package com.samuelberrien.odyspace.objects.baseitem.ammos;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.objects.crashable.CrashableMesh;

/**
 * Created by samuel on 02/08/17.
 */

public class Laser extends Ammos {

	private static int Life = 50;

	public Laser(Context context, ObjModelMtlVBO objModelMtl, CrashableMesh crashableMesh, float[] mPosition, float[] mSpeed, float[] mRotationMatrix, float maxSpeed, float scale) {
		super(context, objModelMtl, crashableMesh, mPosition, mSpeed, new float[3], mRotationMatrix, maxSpeed * 5f, scale, Life);
	}
}
