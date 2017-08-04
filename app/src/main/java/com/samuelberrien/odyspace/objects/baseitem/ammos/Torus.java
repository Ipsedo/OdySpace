package com.samuelberrien.odyspace.objects.baseitem.ammos;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;

/**
 * Created by samuel on 02/08/17.
 */

public class Torus extends Ammos {
	private static int Life = 20;
	private static double BaseLog = 1.3d;
	private double accuScale;

	public Torus(ObjModelMtlVBO objModelMtl, float[] mPosition, float[] mSpeed, float[] mRotationMatrix, float maxSpeed, double accuScale) {
		super(objModelMtl, mPosition, mSpeed, new float[]{0f, 0f, 3e-2f}, mRotationMatrix, maxSpeed, 1f, Life);
		this.accuScale = BaseLog + accuScale;
	}

	@Override
	public void update() {
		super.update();
		this.udpadeScale();
	}

	private void udpadeScale() {
		this.accuScale += 1e-2d;
		super.scale = (float) (Math.log(this.accuScale) / Math.log(BaseLog));
	}
}
