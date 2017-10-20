package com.samuelberrien.odyspace.drawable.explosion;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.GLDrawable;
import com.samuelberrien.odyspace.utils.graphics.ShaderLoader;
import com.samuelberrien.odyspace.utils.maths.Vector;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by samuel on 16/10/17.
 */

public class Explosion_bis implements GLDrawable {

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

	private final float minSpeed;
	private final float rangeSpeed;

	private ArrayList<Particule> particules;

	public Explosion_bis(Context context, float minSpeed, float rangeSpeed, float[] rgb) {
		this.minSpeed = minSpeed;
		this.rangeSpeed = rangeSpeed;

		Random rand = new Random(System.currentTimeMillis());

		particules = new ArrayList<>();
		int nbParticule = 20 + rand.nextInt(40);
		for (int i = 0; i < nbParticule; i++) {
			particules.add(new Particule(rand));
		}

		color = rgb;

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
		for(int i = particules.size() - 1; i >= 0; i--) {
			if(!particules.get(i).isAlive()) {
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
		for (Particule p : particules) {
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

		public Particule(Random rand) {
			mPosition = initialPos.clone();

			mSpeed = new float[3];
			mSpeed[0] = minSpeed + rand.nextFloat() * rangeSpeed;
			mSpeed[1] = minSpeed + rand.nextFloat() * rangeSpeed;
			mSpeed[2] = minSpeed + rand.nextFloat() * rangeSpeed;

			mModelMatrix = new float[16];
			Matrix.setIdentityM(mModelMatrix, 0);

			mRotMatrix = new float[16];
			Matrix.setRotateM(mRotMatrix, 0,
					rand.nextFloat() * 360f,
					rand.nextFloat() * 2 - 1,
					rand.nextFloat() * 2 - 1,
					rand.nextFloat() * 2 - 1);

			scale = rand.nextFloat();
			ttl = rand.nextInt(1000);

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
}
