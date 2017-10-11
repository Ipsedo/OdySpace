package com.samuelberrien.odyspace.controls;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.utils.graphics.Color;
import com.samuelberrien.odyspace.utils.graphics.ShaderLoader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.samuelberrien.odyspace.controls.GamePad.limitScreen;

/**
 * Created by samuel on 17/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

class Joystick extends Control {

	private boolean isVisible;

	private int nbPoint = 64;
	private double circleLength = 0.6d;
	private float[] mCirclePoint = new float[nbPoint * 3];
	private double stickLength = 0.2d;
	private float[] mStickPoint = new float[nbPoint * 3];

	private FloatBuffer circleVertexBuffer;
	private FloatBuffer stickVertexBuffer;

	private float[] mPosition = new float[3];
	private float[] mStickPosition = new float[3];

	private int mPositionHandle;
	private int mColorHandle;
	private int mMVPMatrixHandle;
	private int mProgram;

	private float color[] = Color.ControlsColor;

	Joystick() {
		isVisible = false;
	}

	@Override
	void initGraphics(Context context) {
		int vertexShader = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShaderLoader.openShader(context, R.raw.simple_vs));
		int fragmentShader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderLoader.openShader(context, R.raw.simple_fs));

		mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(mProgram);

		makeCricle();
		makeStick();
		bind();
	}

	private void makeCricle() {
		for (int i = 0; i < nbPoint; i++) {
			double mTmpAngle = (double) i * Math.PI * 2d / (double) nbPoint;
			mCirclePoint[i * 3 + 0] = (float) (circleLength * Math.cos(mTmpAngle));
			mCirclePoint[i * 3 + 1] = (float) (circleLength * Math.sin(mTmpAngle));
			mCirclePoint[i * 3 + 2] = 0f;
		}
		ByteBuffer bb = ByteBuffer.allocateDirect(mCirclePoint.length * 4);
		bb.order(ByteOrder.nativeOrder());
		circleVertexBuffer = bb.asFloatBuffer();
		circleVertexBuffer.put(mCirclePoint);
		circleVertexBuffer.position(0);
	}

	private void makeStick() {
		for (int i = 0; i < nbPoint; i++) {
			double mTmpAngle = (double) (i - 1) * Math.PI * 2d / (double) nbPoint;
			mStickPoint[i * 3 + 0] = (float) (stickLength * Math.cos(mTmpAngle));
			mStickPoint[i * 3 + 1] = (float) (stickLength * Math.sin(mTmpAngle));
			mStickPoint[i * 3 + 2] = 0f;
		}
		ByteBuffer bb = ByteBuffer.allocateDirect(mStickPoint.length * 4);
		bb.order(ByteOrder.nativeOrder());
		stickVertexBuffer = bb.asFloatBuffer();
		stickVertexBuffer.put(mStickPoint);
		stickVertexBuffer.position(0);
	}

	private void bind() {
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
	}

	@Override
	void updatePosition(float x, float y, float ratio) {
		x = x * ratio;
		mPosition[0] = x;
		mPosition[1] = y;
		mPosition[2] = 0f;

		mStickPosition[0] = x;
		mStickPosition[1] = y;
		mStickPosition[2] = 0f;
	}

	@Override
	void updateStick(float x, float y, float ratio) {
		x = x * ratio;
		double length = Math.sqrt(Math.pow(mPosition[0] - x, 2d) + Math.pow(mPosition[1] - y, 2d));
		if (length > circleLength - stickLength) {
			double xDist = x - mPosition[0];
			double yDist = y - mPosition[1];
			mStickPosition[0] = mPosition[0] + (float) ((circleLength - stickLength) * xDist / length);
			mStickPosition[1] = mPosition[1] + (float) ((circleLength - stickLength) * yDist / length);
			mStickPosition[2] = 0f;
		} else {
			mStickPosition[0] = x;
			mStickPosition[1] = y;
			mStickPosition[2] = 0f;
		}
	}

	float[] getStickPosition() {
		if (isVisible) {
			return new float[]{-(mStickPosition[0] - mPosition[0]) / (float) (circleLength - stickLength), (mStickPosition[1] - mPosition[1]) / (float) (circleLength - stickLength)};
		} else {
			return new float[]{0f, 0f};
		}
	}

	@Override
	void setPointerID(int pointerID) {
		setVisible(true);
		super.setPointerID(pointerID);
	}

	private void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	@Override
	void clear() {
		setVisible(false);
		super.clear();
	}

	@Override
	boolean canCatchID(float x, float y, float ratio) {
		return x > limitScreen;
	}

	@Override
	public void draw(float ratio) {
		if (isVisible) {
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
			Matrix.translateM(mMMatrix, 0, mPosition[0], mPosition[1], mPosition[2]);
			Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);

			GLES20.glEnableVertexAttribArray(mPositionHandle);
			GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, circleVertexBuffer);
			GLES20.glUniform4fv(mColorHandle, 1, color, 0);
			GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
			GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, mCirclePoint.length / 3);

			//Stick
			GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, stickVertexBuffer);
			Matrix.setIdentityM(mMMatrix, 0);
			Matrix.translateM(mMMatrix, 0, mStickPosition[0], mStickPosition[1], mStickPosition[2]);
			Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);
			GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
			GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, mStickPoint.length / 3);
			GLES20.glDisableVertexAttribArray(mPositionHandle);

			GLES20.glLineWidth(1f);
		}
	}
}
