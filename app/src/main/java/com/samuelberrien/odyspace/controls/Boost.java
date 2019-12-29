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

import static com.samuelberrien.odyspace.controls.FireButton.FireButtonRay;

/**
 * Created by samuel on 04/08/17.
 */

class Boost extends Control {


	private float boostWidth = 0.3f;
	private float boostHeight = 1f;
	private float[] mBoostPoint = new float[3 * 4];
	private FloatBuffer boostVertexBuffer;
	private float[] mBoostPosition = new float[3];
	private float[] mBoostStickPoint = new float[3 * 4];
	private FloatBuffer boostStickVertexBuffer;
	private float[] mBoostStickPosition = new float[3];

	private int mPositionHandle;
	private int mColorHandle;
	private int mMVPMatrixHandle;
	private int mProgram;

	private float color[] = Color.ControlsColor;

	Boost() {
		mBoostPosition[0] = -1f + 1e-2f;
		mBoostPosition[1] = 1.5e-1f;
		mBoostPosition[2] = 0f;
		mBoostStickPosition = mBoostPosition.clone();
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
		makeBoost();
		makeBoostStick();
	}

	private void bind() {
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
	}

	private void makeBoost() {
		mBoostPoint[0] = boostWidth * 0.5f;
		mBoostPoint[1] = boostHeight * 0.5f;
		mBoostPoint[2] = 0f;

		mBoostPoint[3] = boostWidth * 0.5f;
		mBoostPoint[4] = -boostHeight * 0.5f;
		mBoostPoint[5] = 0f;

		mBoostPoint[6] = -boostWidth * 0.5f;
		mBoostPoint[7] = -boostHeight * 0.5f;
		mBoostPoint[8] = 0f;

		mBoostPoint[9] = -boostWidth * 0.5f;
		mBoostPoint[10] = boostHeight * 0.5f;
		mBoostPoint[11] = 0f;

		ByteBuffer bb = ByteBuffer.allocateDirect(mBoostPoint.length * 4);
		bb.order(ByteOrder.nativeOrder());
		boostVertexBuffer = bb.asFloatBuffer();
		boostVertexBuffer.put(mBoostPoint);
		boostVertexBuffer.position(0);
	}

	private void makeBoostStick() {
		mBoostStickPoint[0] = boostWidth * 0.5f;
		mBoostStickPoint[1] = boostWidth * 0.5f;
		mBoostPoint[2] = 0f;

		mBoostStickPoint[3] = boostWidth * 0.5f;
		mBoostStickPoint[4] = -boostWidth * 0.5f;
		mBoostStickPoint[5] = 0f;

		mBoostStickPoint[6] = -boostWidth * 0.5f;
		mBoostStickPoint[7] = -boostWidth * 0.5f;
		mBoostStickPoint[8] = 0f;

		mBoostStickPoint[9] = -boostWidth * 0.5f;
		mBoostStickPoint[10] = boostWidth * 0.5f;
		mBoostStickPoint[11] = 0f;

		ByteBuffer bb = ByteBuffer.allocateDirect(mBoostStickPoint.length * 4);
		bb.order(ByteOrder.nativeOrder());
		boostStickVertexBuffer = bb.asFloatBuffer();
		boostStickVertexBuffer.put(mBoostStickPoint);
		boostStickVertexBuffer.position(0);
	}

	@Override
	boolean canCatchID(float x, float y, float ratio) {
		if (x * ratio < mBoostPosition[0] * ratio - boostWidth * 0.5f + FireButtonRay) {
			return false;
		} else if (x * ratio > mBoostPosition[0] * ratio + boostWidth * 0.5f + FireButtonRay) {
			return false;
		} else if (y < mBoostPosition[1] - boostHeight * 0.5f + boostWidth * 0.5f) {
			return false;
		} else if (y > mBoostPosition[1] + boostHeight * 0.5f - boostWidth * 0.5f) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	void updatePosition(float unused1, float unused2, float unused3) {

	}

	@Override
	void updateStick(float unused1, float y, float unused2) {
		if (y >= mBoostPosition[1] - boostHeight * 0.5f + boostWidth * 0.5f
				&& y <= mBoostPosition[1] + boostHeight * 0.5f - boostWidth * 0.5f)
			mBoostStickPosition[1] = y;
	}

	/**
	 * Return current boost
	 *
	 * @return a float between -1 (min) to 1 (max)
	 */
	float getBoost() {
		return (mBoostStickPosition[1] - mBoostPosition[1]) / (0.5f * (boostHeight - boostWidth));
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
				mBoostPosition[0] * ratio + FireButtonRay,
				mBoostPosition[1],
				mBoostPosition[2]);
		Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);

		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle,
				3, GLES20.GL_FLOAT, false, 3 * 4, boostVertexBuffer);
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, mBoostPoint.length / 3);

		Matrix.setIdentityM(mMMatrix, 0);
		Matrix.translateM(mMMatrix, 0,
				mBoostStickPosition[0] * ratio + FireButtonRay,
				mBoostStickPosition[1],
				mBoostStickPosition[2]);
		Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);

		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle,
				3, GLES20.GL_FLOAT, false, 3 * 4, boostStickVertexBuffer);
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, mBoostStickPoint.length / 3);

		GLES20.glLineWidth(1f);
	}
}
