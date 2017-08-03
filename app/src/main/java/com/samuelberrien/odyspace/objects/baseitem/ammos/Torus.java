package com.samuelberrien.odyspace.objects.baseitem.ammos;

import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.objects.baseitem.BaseItem;

/**
 * Created by samuel on 02/08/17.
 */

public class Torus extends BaseItem {
	private static int Life = 20;
	private float maxSpeed;

	public Torus(ObjModelMtlVBO objModelMtl, float[] mPosition, float[] mSpeed, float[] mRotationMatrix, float maxSpeed) {
		super(objModelMtl, Life, mPosition, mSpeed, new float[]{0f, 0f, 1e-2f}, 1f);
		this.maxSpeed = maxSpeed;
		super.mRotationMatrix = mRotationMatrix;
	}

	@Override
	public void move() {
		float[] realSpeed = new float[]{super.mSpeed[0] += super.mAcceleration[0], super.mSpeed[1] += super.mAcceleration[1], super.mSpeed[2] += super.mAcceleration[2], 0f};

		Matrix.multiplyMV(realSpeed, 0, super.mRotationMatrix, 0, realSpeed.clone(), 0);

		super.mPosition[0] += this.maxSpeed * realSpeed[0];
		super.mPosition[1] += this.maxSpeed * realSpeed[1];
		super.mPosition[2] += this.maxSpeed * realSpeed[2];

		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, super.mPosition[0], super.mPosition[1], super.mPosition[2]);
		float[] tmpMat = mModelMatrix.clone();
		Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, super.mRotationMatrix, 0);
		Matrix.scaleM(mModelMatrix, 0, super.scale, super.scale, super.scale);

		super.mModelMatrix = mModelMatrix;
		super.scale += 3e-3f;
	}
}
