package com.samuelberrien.odyspace.objects.baseitem.ammos;

import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.objects.baseitem.BaseItem;

/**
 * Created by samuel on 02/08/17.
 */

public class Torus extends Ammos {
	private static int Life = 20;
	private float maxSpeed;
	private double accuScale;

	public Torus(ObjModelMtlVBO objModelMtl, float[] mPosition, float[] mSpeed, float[] mRotationMatrix, float maxSpeed) {
		super(objModelMtl, mPosition, mSpeed, new float[]{0f, 0f, 1e-2f}, mRotationMatrix, maxSpeed, 1f, Life);
	}

	@Override
	public void move() {
		super.move();
		this.udpadeScale();
	}

	private void udpadeScale() {
		this.accuScale += 1e-3d;
		super.scale =(float) (Math.log(this.accuScale) / Math.log(2d));
	}
}
