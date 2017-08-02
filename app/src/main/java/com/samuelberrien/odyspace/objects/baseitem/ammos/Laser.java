package com.samuelberrien.odyspace.objects.baseitem.ammos;

import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.objects.baseitem.BaseItem;

/**
 * Created by samuel on 02/08/17.
 */

public class Laser extends BaseItem {

	private static int Life = 50;
	private float maxSpeed;

	public Laser(ObjModelMtlVBO objModelMtl, float[] mPosition, float[] mSpeed, float[] mRotationMatrix, float maxSpeed, float scale) {
		super(objModelMtl, Life, mPosition, mSpeed, new float[3], scale);
		super.mRotationMatrix = mRotationMatrix;
		this.maxSpeed = maxSpeed * 5f;
	}

	@Override
	public void move() {
		float[] realSpeed = new float[]{super.mSpeed[0], super.mSpeed[1], super.mSpeed[2], 0f};

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
	}
}
