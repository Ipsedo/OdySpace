package com.samuelberrien.odyspace.drawable.explosion;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.GLDrawable;
import com.samuelberrien.odyspace.utils.graphics.ShaderLoader;
import com.samuelberrien.odyspace.utils.maths.Vector;

import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by samuel on 24/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Explosion implements GLDrawable {

	public static class ExplosionBuilder {
		private int nbParticules = 3;
		private float limitScale = 0.8f;
		private float rangeScale = 0.4f;
		private float limitSpeed = 0.4f;
		private float rangeSpeed = 0.2f;

		public ExplosionBuilder() {
		}

		public ExplosionBuilder setNbParticules(int nbParticules) {
			this.nbParticules = nbParticules;
			return this;
		}

		public ExplosionBuilder setLimitScale(float limitScale) {
			this.limitScale = limitScale;
			return this;
		}

		public ExplosionBuilder setRangeScale(float rangeScale) {
			this.rangeScale = rangeScale;
			return this;
		}

		public ExplosionBuilder setLimitSpeed(float limitSpeed) {
			this.limitSpeed = limitSpeed;
			return this;
		}

		public ExplosionBuilder setRangeSpeed(float rangeSpeed) {
			this.rangeSpeed = rangeSpeed;
			return this;
		}

		public Explosion makeExplosion(Context context, float[] rgba) {
			return new Explosion(context, rgba, this);
		}
	}

	private List<Particule> particules;
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
		particules = Collections.synchronizedList(new ArrayList<Particule>());
		Random rand = new Random(System.currentTimeMillis());
		color = rgba;
		for (int i = 0; i < explosionBuilder.nbParticules; i++) {
			particules.add(new Particule(rand,
					explosionBuilder.limitScale,
					explosionBuilder.rangeScale,
					explosionBuilder.limitSpeed,
					explosionBuilder.rangeSpeed));
		}
		makeProgram(context);

		vertexBuffer = (FloatBuffer) ByteBuffer.allocateDirect(vertices.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer()
				.put(vertices)
				.position(0);
	}

	private void makeProgram(Context context) {
		int vertexShader = ShaderLoader.loadShader(
				GLES20.GL_VERTEX_SHADER,
				ShaderLoader.openShader(context, R.raw.exlosion_vs));
		int fragmentShader = ShaderLoader.loadShader(
				GLES20.GL_FRAGMENT_SHADER,
				ShaderLoader.openShader(context, R.raw.explosion_fs));

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
		for (int i = particules.size() - 1; i >= 0; i--) {
			if (!particules.get(i).isAlive()) {
				particules.remove(i);
			}
		}
	}

	public void setPosition(float[] mPosition) {
		initialPos = mPosition;
	}

	@Override
	public void draw(float[] mProjectionMatrix,
					 float[] mViewMatrix,
					 float[] mLightPosInEyeSpace,
					 float[] mCameraPosition) {
		GLES20.glDisable(GLES20.GL_CULL_FACE);
		float[] mVPMatrix = new float[16];
		Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
		ArrayList<Particule> tmp = new ArrayList<>(particules);
		for (Particule p : tmp) {
			p.draw(mVPMatrix, mViewMatrix);
		}
		GLES20.glEnable(GLES20.GL_CULL_FACE);
	}

	public boolean isAlive() {
		return !particules.isEmpty();
	}

	private class Particule {

		private float[] mPosition;
		private float[] mSpeed;
		private float[] mModelMatrix;

		private float[] mRotMatrix;

		private float scale;

		private boolean fstMove;

		private int ttl;

		private float speedDecreaseCoeff;

		private Particule(Random rand,
						  float limitScale,
						  float rangeScale,
						  float limitSpeed,
						  float rangeSpeed) {
			mPosition = new float[3];
			mSpeed = new float[3];
			fstMove = true;
			double phi = rand.nextDouble() * Math.PI * 2d;
			double theta = rand.nextDouble() * Math.PI * 2d;
			speedDecreaseCoeff = 0.89f + rand.nextFloat() * 0.02f;
			mSpeed[0] = (limitSpeed + rangeSpeed * rand.nextFloat())
					* (float) (Math.cos(phi) * Math.sin(theta));
			mSpeed[1] = (limitSpeed + rangeSpeed * rand.nextFloat())
					* (float) Math.sin(phi);
			mSpeed[2] = (limitSpeed + rangeSpeed * rand.nextFloat())
					* (float) (Math.cos(phi) * Math.cos(theta));
			mModelMatrix = new float[16];
			float mAngle = rand.nextFloat() * 360f;
			float[] mRotAxis = new float[3];
			mRotAxis[0] = rand.nextFloat() * 2f - 1f;
			mRotAxis[1] = rand.nextFloat() * 2f - 1f;
			mRotAxis[2] = rand.nextFloat() * 2f - 1f;
			mRotMatrix = new float[16];
			Matrix.setRotateM(mRotMatrix, 0, mAngle, mRotAxis[0], mRotAxis[1], mRotAxis[2]);
			scale = limitScale + rangeScale * rand.nextFloat();
			ttl = 10 + rand.nextInt(30);
		}

		private void move() {
			if (fstMove) {
				mPosition = initialPos.clone();
				fstMove = false;
			}
			mPosition[0] += mSpeed[0];
			mPosition[1] += mSpeed[1];
			mPosition[2] += mSpeed[2];

			/*float speedLength = Vector.length3f(mSpeed);
			float[] norm = Vector.normalize3f(mSpeed);

			mSpeed[0] = norm[0] * (speedLength - speedDrecrease);
			mSpeed[1] = norm[1] * (speedLength - speedDrecrease);
			mSpeed[2] = norm[2] * (speedLength - speedDrecrease);*/

			mSpeed = Vector.mul3f(mSpeed, speedDecreaseCoeff);

			float[] mModelMatrix = new float[16];
			Matrix.setIdentityM(mModelMatrix, 0);
			Matrix.translateM(mModelMatrix, 0, mPosition[0], mPosition[1], mPosition[2]);

			float[] tmpMat = mModelMatrix.clone();
			Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, mRotMatrix, 0);

			Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);

			this.mModelMatrix = mModelMatrix.clone();

			ttl--;
		}

		private boolean isAlive() {
			return ttl > 0;
		}

		private void draw(float[] mVPMatrix, float[] mViewMatrix) {
			float[] mMVMatrix = new float[16];
			Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
			float[] mMVPMatrix = new float[16];
			Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mModelMatrix, 0);

			GLES20.glEnable(GLES20.GL_BLEND);
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

			GLES20.glUseProgram(mProgram);

			vertexBuffer.position(0);
			GLES20.glEnableVertexAttribArray(vPositionHandle);
			GLES20.glVertexAttribPointer(vPositionHandle,
					3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);

			GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, mMVPMatrix, 0);

			GLES20.glUniform3fv(uColorHandle, 1, color, 0);

			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.length / 3);

			GLES20.glDisableVertexAttribArray(vPositionHandle);

			GLES20.glDisable(GLES20.GL_BLEND);
		}
	}

	/*private float[] initialPos;

	private float[] vertices = new float[]{
			-1.0f, -1.0f, 0.0f,
			1.0f, -1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
	};
	private FloatBuffer vertexBuffer;
	private float[] color;

	private Random rand;

	private int uMVPMatrixHandle;
	private int uColorHandle;
	private int vPositionHandle;
	private int mProgram;

	private final float rangeSpeed;

	private List<Particule> particules;

	public Explosion(Context glContext, float[] rgb) {
		rand = new Random(System.currentTimeMillis());

		rangeSpeed = rand.nextFloat() * 3f;

		particules = Collections.synchronizedList(new ArrayList<Particule>());
		int nbParticule = 50 + rand.nextInt(50);
		for (int i = 0; i < nbParticule; i++) {
			particules.add(new Particule());
		}

		color = rgb;

		makeProgram(glContext);

		vertexBuffer = (FloatBuffer) ByteBuffer.allocateDirect(vertices.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer()
				.put(vertices)
				.position(0);
	}

	private void makeProgram(Context glContext) {
		int vertexShader = ShaderLoader.loadShader(
				GLES20.GL_VERTEX_SHADER,
				ShaderLoader.openShader(glContext, R.raw.exlosion_vs));
		int fragmentShader = ShaderLoader.loadShader(
				GLES20.GL_FRAGMENT_SHADER,
				ShaderLoader.openShader(glContext, R.raw.explosion_fs));

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
		for(int i = particules.size() - 1; i >= 0; i--) {
			if(!particules.get(i).isAlive()) {
				particules.remove(i);
			}
		}
	}

	public void setPosition(float[] mPosition) {
		initialPos = mPosition;
	}

	public boolean isAlive() {
		return !particules.isEmpty();
	}

	@Override
	public void draw(float[] mProjectionMatrix,
					 float[] mViewMatrix,
					 float[] mLightPosInEyeSpace,
					 float[] mCameraPosition) {
		GLES20.glDisable(GLES20.GL_CULL_FACE);
		float[] mVPMatrix = new float[16];
		Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
		ArrayList<Particule> tmp = new ArrayList<>(particules);
		for (Particule p : tmp) {
			p.draw(mVPMatrix, mViewMatrix);
		}
		GLES20.glEnable(GLES20.GL_CULL_FACE);
	}

	private class Particule {

		private float[] mPosition;
		private float[] mSpeed;
		private float[] mModelMatrix;

		private float[] mRotMatrix;

		private float scale;

		private boolean fstMove;

		private int ttl;

		public Particule() {
			mPosition = new float[3];

			mSpeed = new float[3];
			double phi = rand.nextDouble() * Math.PI * 2d;
			double theta = rand.nextDouble() * Math.PI * 2d;
			mSpeed[0] = rangeSpeed
					* rand.nextFloat() * (float) (Math.cos(phi) * Math.sin(theta));
			mSpeed[1] = rangeSpeed
					* rand.nextFloat() * (float) Math.sin(phi);
			mSpeed[2] = rangeSpeed
					* rand.nextFloat() * (float) (Math.cos(phi) * Math.cos(theta));

			mModelMatrix = new float[16];
			Matrix.setIdentityM(mModelMatrix, 0);

			mRotMatrix = new float[16];
			Matrix.setRotateM(mRotMatrix, 0,
					rand.nextFloat() * 360f,
					rand.nextFloat() * 2 - 1,
					rand.nextFloat() * 2 - 1,
					rand.nextFloat() * 2 - 1);

			scale = rand.nextFloat();
			ttl = rand.nextInt(100);

			fstMove = true;
		}

		private void move() {
			if (fstMove) {
				mPosition = initialPos.clone();
				fstMove = false;
			}
			mPosition[0] += mSpeed[0];
			mPosition[1] += mSpeed[1];
			mPosition[2] += mSpeed[2];

			mSpeed[0] *= (0.9f + rand.nextFloat() * 0.04f);
			mSpeed[1] *= (0.9f + rand.nextFloat() * 0.04f);
			mSpeed[2] *= (0.9f + rand.nextFloat() * 0.04f);

			float[] mModelMatrix = new float[16];
			Matrix.setIdentityM(mModelMatrix, 0);
			Matrix.translateM(mModelMatrix, 0, mPosition[0], mPosition[1], mPosition[2]);

			float[] tmpMat = mModelMatrix.clone();
			Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, mRotMatrix, 0);

			Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);

			this.mModelMatrix = mModelMatrix.clone();

			ttl--;
		}

		private boolean isAlive() {
			return ttl > 0;
		}

		private void draw(float[] mVPMatrix, float[] mViewMatrix) {
			float[] mMVMatrix = new float[16];
			Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
			float[] mMVPMatrix = new float[16];
			Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mModelMatrix, 0);

			GLES20.glEnable(GLES20.GL_BLEND);
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

			GLES20.glUseProgram(mProgram);

			vertexBuffer.position(0);
			GLES20.glEnableVertexAttribArray(vPositionHandle);
			GLES20.glVertexAttribPointer(vPositionHandle,
					3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);

			GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, mMVPMatrix, 0);

			GLES20.glUniform3fv(uColorHandle, 1, color, 0);

			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.length / 3);

			GLES20.glDisableVertexAttribArray(vPositionHandle);

			GLES20.glDisable(GLES20.GL_BLEND);
		}
	}*/
}
