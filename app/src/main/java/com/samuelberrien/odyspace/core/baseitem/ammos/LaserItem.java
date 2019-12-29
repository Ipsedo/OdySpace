package com.samuelberrien.odyspace.core.baseitem.ammos;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.ObjModelMtlVBO;
import com.samuelberrien.odyspace.core.collision.CollisionMesh;

/**
 * Created by samuel on 02/08/17.
 */

public class LaserItem extends Ammos {

	private static int Life = 50;

	public LaserItem(Context context,
					 ObjModelMtlVBO objModelMtl, CollisionMesh collisionMesh,
					 float[] mPosition, float[] mSpeed, float[] mRotationMatrix,
					 float maxSpeed, float scale) {
		super(context,
				objModelMtl, collisionMesh,
				mPosition, mSpeed, new float[3], mRotationMatrix,
				maxSpeed * 5f, scale, Life);
	}
}
