package com.samuelberrien.odyspace.core.baseitem.group;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.core.baseitem.BaseItem;
import com.samuelberrien.odyspace.core.collision.CollisionMesh;
import com.samuelberrien.odyspace.drawable.ObjModelMtlVBO;

/**
 * Created by samuel on 04/08/17.
 */

public abstract class Leaf extends BaseItem {

	private float[] parentModelMatrix;

	private float[] allCoords = new float[0];
	//TODO

	public Leaf(Context context, String objFileName, String mtlFileName, float lightAugmentation, float distanceCoef, boolean randomColor, int life, float[] mPosition, float[] mSpeed, float[] mAcceleration, float scale) {
		super(context, objFileName, mtlFileName, objFileName, lightAugmentation, distanceCoef, randomColor, life, mPosition, mSpeed, mAcceleration, scale);
	}

	public Leaf(Context context, ObjModelMtlVBO objModelMtl, CollisionMesh collisionMesh, int life, float[] mPosition, float[] mSpeed, float[] mAcceleration, float scale) {
		super(context, objModelMtl, collisionMesh, life, mPosition, mSpeed, mAcceleration, scale);
	}

	public void setParentModelMatrix(float[] parentModelMatrix) {
		this.parentModelMatrix = parentModelMatrix;
	}

	@Override
	public void update() {
		float[] mModelMatrix = new float[16];
		Matrix.multiplyMM(mModelMatrix, 0, parentModelMatrix, 0, computeLeafModelMatrix(), 0);
		super.mModelMatrix = mModelMatrix;
	}

	/**
	 * Must modifiy BaseItem.mPosition, BaseItem.mSpeed, BaseItem.mAcceleration and not touch BaseItem.mModelMatrix
	 *
	 * @return the leaf modelMatrix
	 */
	protected abstract float[] computeLeafModelMatrix();

	public float[] cloneTriangleArray() {
		return allCoords.clone();
	}

	public float[] cloneModelMatrix() {
		return super.mModelMatrix.clone();
	}
}
