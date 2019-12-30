package com.samuelberrien.odyspace.core.baseitem.ammos;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.core.baseitem.BaseItem;
import com.samuelberrien.odyspace.core.collision.CollisionMesh;
import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.drawable.ObjModelMtlVBO;

/**
 * Created by samuel on 03/08/17.
 */

public class Ammos extends BaseItem {

	protected float maxSpeed;

	Ammos(Context context,
		  ObjModelMtlVBO objModelMtl, CollisionMesh collisionMesh,
		  float[] mPosition, float[] mSpeed, float[] mAcceleration,
		  float[] mRotationMatrix, float maxSpeed, float scale, int life) {
		super(context, objModelMtl, collisionMesh, life, mPosition, mSpeed, mAcceleration, scale);
		super.mRotationMatrix = mRotationMatrix;
		this.maxSpeed = maxSpeed;
	}

	@Override
	public void update() {
		float[] realSpeed = new float[]{
				super.mSpeed[0] += super.mAcceleration[0],
				super.mSpeed[1] += super.mAcceleration[1],
				super.mSpeed[2] += super.mAcceleration[2], 0f};

		Matrix.multiplyMV(realSpeed, 0, super.mRotationMatrix, 0, realSpeed.clone(), 0);

		super.mPosition[0] += maxSpeed * realSpeed[0];
		super.mPosition[1] += maxSpeed * realSpeed[1];
		super.mPosition[2] += maxSpeed * realSpeed[2];

		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0,
				super.mPosition[0],
				super.mPosition[1],
				super.mPosition[2]);
		Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix.clone(), 0, super.mRotationMatrix, 0);
		Matrix.scaleM(mModelMatrix, 0, super.scale, super.scale, super.scale);

		super.mModelMatrix = mModelMatrix;
	}

	@Override
	protected Explosion getExplosion() {
		return new Explosion.ExplosionBuilder()
				.setNbParticules(3)
				.setLimitScale(0.3f)
				.setRangeScale(0.5f)
				.setLimitSpeed(0.4f)
				.setRangeSpeed(0.4f)
				.makeExplosion(glContext, objModelMtlVBO.getRandomMtlDiffRGB());
	}
}
