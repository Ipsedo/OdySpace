package com.samuelberrien.odyspace.objects;

import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtl;

/**
 * Created by samuel on 26/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class BaseItemWithoutModel {

	protected float radius;

	protected int life;

	protected float[] mPosition;
	protected float[] mSpeed;
	protected float[] mAcceleration;

	protected float[] mRotationMatrix;

	protected float[] mModelMatrix;

	public BaseItemWithoutModel(int life, float[] mPosition, float[] mSpeed, float[] mAcceleration) {
		this.life = life;
		this.mPosition = mPosition;
		this.mSpeed = mSpeed;
		this.mAcceleration = mAcceleration;
		this.mRotationMatrix = new float[16];
		Matrix.setIdentityM(this.mRotationMatrix, 0);
		this.mModelMatrix = new float[16];
		Matrix.setIdentityM(this.mModelMatrix, 0);
		this.radius = 1f;
	}

	public boolean isAlive() {
		return this.life > 0;
	}

	public boolean isCollided(BaseItem other) {
		return false;
	}

	public void decrementsBothLife(BaseItem other) {
		int otherLife = other.life;
		other.life -= this.life;
		this.life -= otherLife;
	}

	public void move() {
		this.mSpeed[0] += this.mAcceleration[0];
		this.mSpeed[1] += this.mAcceleration[1];
		this.mSpeed[2] += this.mAcceleration[2];

		this.mPosition[0] += this.mSpeed[0];
		this.mPosition[1] += this.mSpeed[1];
		this.mPosition[2] += this.mSpeed[2];

		float[] tmp = new float[16];
		Matrix.setIdentityM(tmp, 0);
		Matrix.translateM(tmp, 0, this.mPosition[0], this.mPosition[1], this.mPosition[2]);
		this.mModelMatrix = tmp.clone();
	}

	public void draw(ObjModelMtl objModelMtl, float[] pMatrix, float[] vMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		float[] mvMatrix = new float[16];
		Matrix.multiplyMM(mvMatrix, 0, vMatrix, 0, this.mModelMatrix, 0);
		float[] mvpMatrix = new float[16];
		Matrix.multiplyMM(mvpMatrix, 0, pMatrix, 0, mvMatrix, 0);
		objModelMtl.draw(mvpMatrix, mvMatrix, mLightPosInEyeSpace, mCameraPosition);
	}
}
