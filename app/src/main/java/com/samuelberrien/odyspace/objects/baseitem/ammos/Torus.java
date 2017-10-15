package com.samuelberrien.odyspace.objects.baseitem.ammos;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.objects.crashable.CrashableMesh;

/**
 * Created by samuel on 02/08/17.
 */

public class Torus extends Ammos {
	private static int Life = 20;
	private static double BaseLog = 1.3d;
	private double accuScale;

	public Torus(Context context,
				 ObjModelMtlVBO objModelMtl, CrashableMesh crashableMeshes,
				 float[] mPosition, float[] mSpeed, float[] mRotationMatrix,
				 float maxSpeed, double accuScale) {
		super(context,
				objModelMtl, crashableMeshes,
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
