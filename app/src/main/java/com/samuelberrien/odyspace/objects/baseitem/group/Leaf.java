package com.samuelberrien.odyspace.objects.baseitem.group;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.objects.baseitem.BaseItem;

/**
 * Created by samuel on 04/08/17.
 */

public abstract class Leaf extends BaseItem {

	private float[] parentModelMatrix;

	public Leaf(Context context, String objFileName, String mtlFileName, float lightAugmentation, float distanceCoef, boolean randomColor, int life, float[] mPosition, float[] mSpeed, float[] mAcceleration, float scale) {
		super(context, objFileName, mtlFileName, lightAugmentation, distanceCoef, randomColor, life, mPosition, mSpeed, mAcceleration, scale);
		this.parentModelMatrix = parentModelMatrix;
	}

	public Leaf(ObjModelMtlVBO objModelMtl, int life, float[] mPosition, float[] mSpeed, float[] mAcceleration, float scale) {
		super(objModelMtl, life, mPosition, mSpeed, mAcceleration, scale);
		this.parentModelMatrix = parentModelMatrix;
	}

	public void setParentModelMatrix(float[] parentModelMatrix) {
		this.parentModelMatrix = parentModelMatrix;
	}

	@Override
	public void update() {
		float[] mModelMatrix = new float[16];
		Matrix.multiplyMM(mModelMatrix, 0, this.parentModelMatrix, 0, this.computeLeafModelMatrix(), 0);
		super.mModelMatrix = mModelMatrix;
	}

	/**
	 * Must modifiy BaseItem.mPosition, BaseItem.mSpeed, BaseItem.mAcceleration and not touch BaseItem.mModelMatrix
	 * @return the leaf modelMatrix
	 */
	protected abstract float[] computeLeafModelMatrix();

	public float[] cloneTriangleArray() {
		return super.allCoords.clone();
	}

	public float[] cloneModelMatrix() {
		return super.mModelMatrix.clone();
	}
}
