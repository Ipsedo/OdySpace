package com.samuelberrien.odyspace.objects.baseitem.ammos;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.objects.crashable.CrashableMesh;

/**
 * Created by samuel on 03/08/17.
 */

public class Bomb extends Ammos {

	private static int Life = 3;

	public Bomb(Context context,
				ObjModelMtlVBO objModelMtl, CrashableMesh crashableMeshes,
				float[] mPosition, float[] mSpeed, float[] mRotationMatrix, float maxSpeed) {
		super(context,
				objModelMtl, crashableMeshes,
				mPosition, mSpeed, new float[3], mRotationMatrix, 3f * maxSpeed, 2f, Life);
	}
}
