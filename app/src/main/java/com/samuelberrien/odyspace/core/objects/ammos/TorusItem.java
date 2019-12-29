package com.samuelberrien.odyspace.core.objects.ammos;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.core.collision.CollisionMesh;

/**
 * Created by samuel on 02/08/17.
 */

public class TorusItem extends Ammos {
	private static int Life = 20;
	private static double BaseLog = 1.3d;
	private double accuScale;

	public TorusItem(Context context,
					 ObjModelMtlVBO objModelMtl, CollisionMesh collisionMeshes,
					 float[] mPosition, float[] mSpeed, float[] mRotationMatrix,
					 float maxSpeed, double accuScale) {
		super(context,
				objModelMtl, collisionMeshes,
				mPosition, mSpeed, new float[]{0f, 0f, 3e-2f}, mRotationMatrix, maxSpeed, 1f, Life);
		this.accuScale = BaseLog + accuScale;
	}

	@Override
	public void update() {
		super.update();
		udpadeScale();
	}

	private void udpadeScale() {
		accuScale += 1e-2d;
		scale = (float) (Math.log(accuScale) / Math.log(BaseLog));
	}
}
