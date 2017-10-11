package com.samuelberrien.odyspace.drawable.explosion;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.GLItemDrawable;
import com.samuelberrien.odyspace.utils.graphics.ShaderLoader;
import com.samuelberrien.odyspace.utils.maths.Vector;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

		public Explosion makeExplosion(Context context, float[] rgba) {
			return new Explosion(context, rgba, this);
		}
	}

	private ArrayList<Particule> particules;
	private final float limitSpeedAlife;
	private float[] initialPos;

	private float[] vertices = new float[]{
			-1.0f, -1.0f, 0.0f,
			1.0f, -1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
	};
	private FloatBuffer vertexBuffer;
	private float[] color;

	private int uMVPMatrixHandle;
	private int uColorHandle;
	private int vPositionHandle;
	private int mProgram;

	private Explosion(Context context, float[] rgba, ExplosionBuilder explosionBuilder) {
		limitSpeedAlife = explosionBuilder.limitSpeedAlife;
		particules = new ArrayList<>();
		Random rand = new Random(System.currentTimeMillis());
		color = rgba;
		for (int i = 0; i < explosionBuilder.nbParticules; i++) {
			particules.add(new Particule(rand, explosionBuilder.limitScale, explosionBuilder.maxScale, explosionBuilder.limitSpeed, explosionBuilder.maxSpeed));
		}
		makeProgram(context);

		vertexBuffer = (FloatBuffer) ByteBuffer.allocateDirect(vertices.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer()
				.put(vertices)
				.position(0);
	}

	private void makeProgram(Context context) {
		int vertexShader = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShaderLoader.openShader(context, R.raw.exlosion_vs));
		int fragmentShader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderLoader.openShader(context, R.raw.explosion_fs));

		mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(mProgram);

		bind();
	}

	private void bind() {
		uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
		uColorHandle = GLES20.glGetUniformLocation(mProgram, "u_Color");
		vPositionHandle = GLES20.glGetAttribLocation(mProgram, "v_Position");
	}

	public void move() {
		for (Particule p : particules) {
			p.move();
		}
	}

	public void setPosition(float[] mPosition) {
		initialPos = mPosition;
	}

	@Override
	public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		GLES20.glDisable(GLES20.GL_CULL_FACE);
		float[] mVPMatrix = new float[16];
		Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
		for (Particule p : particules) {
			p.draw(mVPMatrix, mViewMatrix);
		}
		GLES20.glEnable(GLES20.GL_CULL_FACE);
	}

	public boolean isAlive() {
		boolean res = false;
		for (Particule p : particules) {
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
			mPosition = new float[3];
			mSpeed = new float[3];
			fstMove = true;
			double phi = rand.nextDouble() * Math.PI * 2d;
			double theta = rand.nextDouble() * Math.PI * 2d;
			mSpeed[0] = (limitSpeed + (maxSpeed - limitSpeed) * rand.nextFloat()) * (float) (Math.cos(phi) * Math.sin(theta));
			mSpeed[1] = (limitSpeed + (maxSpeed - limitSpeed) * rand.nextFloat()) * (float) Math.sin(phi);
			mSpeed[2] = (limitSpeed + (maxSpeed - limitSpeed) * rand.nextFloat()) * (float) (Math.cos(phi) * Math.cos(theta));
			mModelMatrix = new float[16];
			float mAngle = rand.nextFloat() * 360f;
			float[] mRotAxis = new float[3];
			mRotAxis[0] = rand.nextFloat() * 2f - 1f;
			mRotAxis[1] = rand.nextFloat() * 2f - 1f;
			mRotAxis[2] = rand.nextFloat() * 2f - 1f;
			mRotMatrix = new float[16];
			Matrix.setRotateM(mRotMatrix, 0, mAngle, mRotAxis[0], mRotAxis[1], mRotAxis[2]);
			scale = limitScale + (maxScale - limitScale) * rand.nextFloat();
		}

		private void move() {
			if (fstMove) {
				mPosition = initialPos.clone();
				fstMove = false;
			}
			mPosition[0] += mSpeed[0];
			mPosition[1] += mSpeed[1];
			mPosition[2] += mSpeed[2];

			mSpeed[0] *= 0.9f;
			mSpeed[1] *= 0.9f;
			mSpeed[2] *= 0.9f;

			float[] mModelMatrix = new float[16];
			Matrix.setIdentityM(mModelMatrix, 0);
			Matrix.translateM(mModelMatrix, 0, mPosition[0], mPosition[1], mPosition[2]);

			float[] tmpMat = mModelMatrix.clone();
			Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, mRotMatrix, 0);

			Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);

			this.mModelMatrix = mModelMatrix.clone();
		}

		private boolean isAlive() {
			return Vector.length3f(mSpeed) > limitSpeedAlife;
		}

		private void draw(float[] mVPMatrix, float[] mViewMatrix) {
			float[] mMVMatrix = new float[16];
			Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
			float[] mMVPMatrix = new float[16];
			Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mModelMatrix, 0);

			ShaderLoader.checkGlError("0");
			GLES20.glUseProgram(mProgram);
			ShaderLoader.checkGlError("1");

			vertexBuffer.position(0);
			GLES20.glEnableVertexAttribArray(vPositionHandle);
			ShaderLoader.checkGlError("2");
			GLES20.glVertexAttribPointer(vPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
			ShaderLoader.checkGlError("3");

			GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, mMVPMatrix, 0);
			ShaderLoader.checkGlError("4");

			GLES20.glUniform4fv(uColorHandle, 1, color, 0);
			ShaderLoader.checkGlError("5");

			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.length / 3);
			ShaderLoader.checkGlError("6");

			GLES20.glDisableVertexAttribArray(vPositionHandle);
			ShaderLoader.checkGlError("7");
		}
	}
}
