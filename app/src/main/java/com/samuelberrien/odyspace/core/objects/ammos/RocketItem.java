package com.samuelberrien.odyspace.core.objects.ammos;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.core.collision.CollisionMesh;

/**
 * Created by samuel on 20/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class RocketItem extends Ammos {

	private static int Life = 1;

	public RocketItem(Context context,
					  ObjModelMtlVBO objModelMtl, CollisionMesh collisionMesh,
					  float[] mPosition, float[] mSpeed, float[] mRotationMatrix, float maxSpeed) {
		super(context,
				objModelMtl, collisionMesh,
				mPosition, mSpeed, new float[3], mRotationMatrix, 3f * maxSpeed, 1f, Life);
	}
}
