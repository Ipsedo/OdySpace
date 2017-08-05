package com.samuelberrien.odyspace.drawable.explosion;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.GLItemDrawable;
import com.samuelberrien.odyspace.drawable.obj.ObjModel;
import com.samuelberrien.odyspace.utils.maths.Vector;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by samuel on 24/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Explosion implements GLItemDrawable {

	public static class ExplosionBuilder {
		private int nbParticules = 3;
		private float limitSpeedAlife = 0.16f;
		private float limitScale = 0.8f;
		private float maxScale = 1.2f;
		private float limitSpeed = 0.4f;
		private float maxSpeed = 0.6f;

		public ExplosionBuilder() {
		}

		public ExplosionBuilder setNbParticules(int nbParticules) {
			this.nbParticules = nbParticules;
			return this;
		}

		public ExplosionBuilder setLimitSpeedAlife(float limitSpeedAlife) {
			this.limitSpeedAlife = limitSpeedAlife;
			return this;
		}

		public ExplosionBuilder setLimitScale(float limitScale) {
			this.limitScale = limitScale;
			return this;
		}

		public ExplosionBuilder setMaxScale(float maxScale) {
			this.maxScale = maxScale;
			return this;
		}

		public ExplosionBuilder setLimitSpeed(float limitSpeed) {
			this.limitSpeed = limitSpeed;
			return this;
		}

		public ExplosionBuilder setMaxSpeed(float maxSpeed) {
			this.maxSpeed = maxSpeed;
			return this;
		}

		public Explosion makeExplosion(Context context, FloatBuffer mDiffColor) {
			return new Explosion(context, mDiffColor, this);
		}

		public Explosion makeExplosion(ObjModel particule, FloatBuffer mDiffColor) {
			return new Explosion(particule, mDiffColor, this);
		}
	}

	private ArrayList<Particule> particules;
	private ObjModel particule;
	private final float limitSpeedAlife;
	private float[] initialPos;

	private Explosion(Context context, FloatBuffer mDiffColor, /*int nbParticule, float limitSpeedAlife, float limitScale, float maxScale, float limitSpeed, float maxSpeed*/ExplosionBuilder explosionBuilder) {
		this.limitSpeedAlife = explosionBuilder.limitSpeedAlife;
		this.particules = new ArrayList<>();
		Random rand = new Random(System.currentTimeMillis());
		this.particule = new ObjModel(context, "triangle.obj", 1f, 1f, 1f, 1f, 0f, 1f);
		this.particule.setColor(mDiffColor);
		for (int i = 0; i < explosionBuilder.nbParticules; i++) {
			this.particules.add(new Particule(rand, explosionBuilder.limitScale, explosionBuilder.maxScale, explosionBuilder.limitSpeed, explosionBuilder.maxSpeed));
		}
	}

	private Explosion(ObjModel particule, FloatBuffer mDiffColor, /*int nbParticule, float limitSpeedAlife, float limitScale, float maxScale, float limitSpeed, float maxSpeed*/ExplosionBuilder explosionBuilder) {
		this.limitSpeedAlife = explosionBuilder.limitSpeedAlife;
		this.particules = new ArrayList<>();
		Random rand = new Random(System.currentTimeMillis());
		this.particule = particule;
		this.particule.setColor(mDiffColor);
		for (int i = 0; i < explosionBuilder.nbParticules; i++) {
			this.particules.add(new Particule(rand, explosionBuilder.limitScale, explosionBuilder.maxScale, explosionBuilder.limitSpeed, explosionBuilder.maxSpeed));
		}
	}

	public void move() {
		for (Particule p : this.particules) {
			p.move();
		}
	}

	public void setPosition(float[] mPosition) {
		this.initialPos = mPosition;
	}

	@Override
	public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		GLES20.glDisable(GLES20.GL_CULL_FACE);
		float[] mVPMatrix = new float[16];
		Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
		for (Particule p : this.particules) {
			p.draw(mVPMatrix, mViewMatrix, mLightPosInEyeSpace, this.particule);
		}
		GLES20.glEnable(GLES20.GL_CULL_FACE);
	}

	public boolean isAlive() {
		boolean res = false;
		for (Particule p : this.particules) {
			res |= p.isAlive();
		}
		return res;
	}

	private class Particule {

		private float[] mPosition;
		private float[] mSpeed;
		private float[] mModelMatrix;

		private float[] mRotMatrix;

		private float scale;

		private boolean fstMove;

		private Particule(Random rand, float limitScale, float maxScale, float limitSpeed, float maxSpeed) {
			this.mPosition = new float[3];
			this.mSpeed = new float[3];
			this.fstMove = true;
			double phi = rand.nextDouble() * Math.PI * 2d;
			double theta = rand.nextDouble() * Math.PI * 2d;
			this.mSpeed[0] = (limitSpeed + (maxSpeed - limitSpeed) * rand.nextFloat()) * (float) (Math.cos(phi) * Math.sin(theta));
			this.mSpeed[1] = (limitSpeed + (maxSpeed - limitSpeed) * rand.nextFloat()) * (float) Math.sin(phi);
			this.mSpeed[2] = (limitSpeed + (maxSpeed - limitSpeed) * rand.nextFloat()) * (float) (Math.cos(phi) * Math.cos(theta));
			this.mModelMatrix = new float[16];
			float mAngle = rand.nextFloat() * 360f;
			float[] mRotAxis = new float[3];
			mRotAxis[0] = rand.nextFloat() * 2f - 1f;
			mRotAxis[1] = rand.nextFloat() * 2f - 1f;
			mRotAxis[2] = rand.nextFloat() * 2f - 1f;
			this.mRotMatrix = new float[16];
			Matrix.setRotateM(this.mRotMatrix, 0, mAngle, mRotAxis[0], mRotAxis[1], mRotAxis[2]);
			this.scale = limitScale + (maxScale - limitScale) * rand.nextFloat();
		}

		private void move() {
			if (this.fstMove) {
				this.mPosition = Explosion.this.initialPos.clone();
				this.fstMove = false;
			}
			this.mPosition[0] += this.mSpeed[0];
			this.mPosition[1] += this.mSpeed[1];
			this.mPosition[2] += this.mSpeed[2];

			this.mSpeed[0] *= 0.9f;
			this.mSpeed[1] *= 0.9f;
			this.mSpeed[2] *= 0.9f;

			float[] mModelMatrix = new float[16];
			Matrix.setIdentityM(mModelMatrix, 0);
			Matrix.translateM(mModelMatrix, 0, this.mPosition[0], this.mPosition[1], this.mPosition[2]);

			float[] tmpMat = mModelMatrix.clone();
			Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, this.mRotMatrix, 0);

			Matrix.scaleM(mModelMatrix, 0, this.scale, this.scale, this.scale);

			this.mModelMatrix = mModelMatrix.clone();
		}

		private boolean isAlive() {
			return Vector.length3f(this.mSpeed) > Explosion.this.limitSpeedAlife;
		}

		private void draw(float[] mVPMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, ObjModel object) {
			float[] mMVMatrix = new float[16];
			Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, this.mModelMatrix, 0);
			float[] mMVPMatrix = new float[16];
			Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, this.mModelMatrix, 0);
			object.draw(mMVPMatrix, mMVMatrix, mLightPosInEyeSpace, new float[0]);
		}
	}
}
