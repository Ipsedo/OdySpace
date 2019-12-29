package com.samuelberrien.odyspace.core.baseitem.ammos;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.ObjModelMtlVBO;
import com.samuelberrien.odyspace.core.collision.CollisionMesh;

/**
 * Created by samuel on 03/08/17.
 */

public class BombItem extends Ammos {

	private static int Life = 3;

	public BombItem(Context context,
					ObjModelMtlVBO objModelMtl, CollisionMesh collisionMeshes,
					float[] mPosition, float[] mSpeed, float[] mRotationMatrix, float maxSpeed) {
		super(context,
				objModelMtl, collisionMeshes,
				mPosition, mSpeed, new float[3], mRotationMatrix, 3f * maxSpeed, 2f, Life);
	}
}
