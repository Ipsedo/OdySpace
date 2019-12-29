package com.samuelberrien.odyspace.controls;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.obj.ObjModelVBO;
import com.samuelberrien.odyspace.utils.graphics.Color;
import com.samuelberrien.odyspace.utils.graphics.ShaderLoader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by samuel on 23/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

class FireButton extends Control {

	//private boolean isBoostVisible;

	private int nbPoint = 64;
	static float FireButtonRay = 0.3f;
	private float[] fireButtonPoints = new float[nbPoint * 3];
	private FloatBuffer fireButtonVertexBuffer;
	private float[] mFireButtonPosition = new float[]{
			-1f + 1e-2f,
			-1f + FireButtonRay + 1e-2f,
			0f};
	private ObjModelVBO fireLogo;
	private boolean isFire;

	private int mPositionHandle;
	private int mColorHandle;
	private int mMVPMatrixHandle;
	private int mProgram;

	private float color[] = Color.ControlsColor;

	FireButton() {
		isFire = false;
	}

	@Override
	void updatePosition(float unused1, float unused2, float unused3) {

	}

	@Override
	void updateStick(float unused1, float unused2, float unused3) {

	}

	@Override
	void initGraphics(Context context) {
		int vertexShader = ShaderLoader.loadShader(
				GLES20.GL_VERTEX_SHADER,
				ShaderLoader.openShader(context, R.raw.simple_vs));
		int fragmentShader = ShaderLoader.loadShader(
				GLES20.GL_FRAGMENT_SHADER,
				ShaderLoader.openShader(context, R.raw.simple_fs));

		mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(mProgram);

		bind();
		makeFireButton();
		fireLogo = new ObjModelVBO(context, "obj/bullet.obj", color[0], color[1], color[2], 1f, 0f, 0f);
	}

	private void bind() {
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
	}

	private void makeFireButton() {
		for (int i = 0; i < nbPoint; i++) {
			double mTmpAngle = (double) (i - 1) * Math.PI * 2d / (double) nbPoint;
			fireButtonPoints[i * 3 + 0] = (float) (FireButtonRay * Math.cos(mTmpAngle));
			fireButtonPoints[i * 3 + 1] = (float) (FireButtonRay * Math.sin(mTmpAngle));
			fireButtonPoints[i * 3 + 2] = 0f;
		}
		ByteBuffer bb = ByteBuffer.allocateDirect(fireButtonPoints.length * 4);
		bb.order(ByteOrder.nativeOrder());
		fireButtonVertexBuffer = bb.asFloatBuffer();
		fireButtonVertexBuffer.put(fireButtonPoints);
		fireButtonVertexBuffer.position(0);
	}

	@Override
	boolean canCatchID(float x, float y, float ratio) {
		float xRef = x * ratio - (mFireButtonPosition[0] * ratio + FireButtonRay);
		float yRef = y - mFireButtonPosition[1];
		if (xRef * xRef + yRef * yRef < FireButtonRay * FireButtonRay) {
			isFire = true;
			return true;
		} else {
			isFire = false;
			return false;
		}
	}

	public boolean isFire() {
		return isFire;
	}

	void turnOffFire() {
		isFire = false;
	}

	@Override
	public void draw(float ratio) {
		GLES20.glUseProgram(mProgram);

		GLES20.glLineWidth(5f);

		float[] mViewMatrix = new float[16];
		Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -1, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
		float[] mVPMatrix = new float[16];
		float[] mPMatrix = new float[16];
		Matrix.orthoM(mPMatrix, 0, -1f * ratio, 1f * ratio, -1f, 1f, -1f, 1f);
		Matrix.multiplyMM(mVPMatrix, 0, mPMatrix, 0, mViewMatrix, 0);
		float[] mMVPMatrix = new float[16];

		float[] mMMatrix = new float[16];
		Matrix.setIdentityM(mMMatrix, 0);
		Matrix.translateM(mMMatrix, 0,
				mFireButtonPosition[0] * ratio + FireButtonRay,
				mFireButtonPosition[1],
				mFireButtonPosition[2]);
		Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);

		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle,
				3, GLES20.GL_FLOAT, false, 3 * 4, fireButtonVertexBuffer);
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, fireButtonPoints.length / 3);

		GLES20.glDisableVertexAttribArray(mPositionHandle);

		Matrix.setIdentityM(mMMatrix, 0);
		Matrix.translateM(mMMatrix, 0,
				mFireButtonPosition[0] * ratio + FireButtonRay,
				mFireButtonPosition[1],
				mFireButtonPosition[2]);
		Matrix.scaleM(mMMatrix, 0,
				FireButtonRay / 1.2f,
				FireButtonRay / 1.2f,
				FireButtonRay / 1.2f);
		Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);

		GLES20.glLineWidth(1f);

		fireLogo.draw(mMVPMatrix, mVPMatrix, new float[]{0f, 0f, -1f}, new float[0]);
	}
}
