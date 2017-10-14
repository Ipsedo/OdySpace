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
 * Created by samuel on 10/07/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

class Remote extends Control {

	private boolean isVisible;
	private float width = 1f;
	private float height = 0.3f;
	private float[] mRemotePoints = new float[3 * 4];
	private FloatBuffer remoteVertexBuffer;
	private float[] mRemotePosition = new float[3];
	private float[] mRemoteStickPoints = new float[3 * 4];
	private FloatBuffer remoteStickVertexBuffer;
	private float[] mRemoteStickPosition = new float[3];

	private int mPositionHandle;
	private int mColorHandle;
	private int mMVPMatrixHandle;
	private int mProgram;

	private float color[] = Color.ControlsColor;

	Remote() {
		isVisible = false;
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

		makeRemote();
		makeRemoteStick();
		bind();
	}

	private void bind() {
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
	}

	private void makeRemote() {
		mRemotePoints[0] = width * 0.5f;
		mRemotePoints[1] = height * 0.5f;
		mRemotePoints[2] = 0f;

		mRemotePoints[3] = width * 0.5f;
		mRemotePoints[4] = -height * 0.5f;
		mRemotePoints[5] = 0f;

		mRemotePoints[6] = -width * 0.5f;
		mRemotePoints[7] = -height * 0.5f;
		mRemotePoints[8] = 0f;

		mRemotePoints[9] = -width * 0.5f;
		mRemotePoints[10] = height * 0.5f;
		mRemotePoints[11] = 0f;

		ByteBuffer bb = ByteBuffer.allocateDirect(mRemotePoints.length * 4);
		bb.order(ByteOrder.nativeOrder());
		remoteVertexBuffer = (FloatBuffer) bb.asFloatBuffer()
				.put(mRemotePoints)
				.position(0);
	}

	private void makeRemoteStick() {
		mRemoteStickPoints[0] = height * 0.5f;
		mRemoteStickPoints[1] = height * 0.5f;
		mRemoteStickPoints[2] = 0f;

		mRemoteStickPoints[3] = height * 0.5f;
		mRemoteStickPoints[4] = -height * 0.5f;
		mRemoteStickPoints[5] = 0f;

		mRemoteStickPoints[6] = -height * 0.5f;
		mRemoteStickPoints[7] = -height * 0.5f;
		mRemoteStickPoints[8] = 0f;

		mRemoteStickPoints[9] = -height * 0.5f;
		mRemoteStickPoints[10] = height * 0.5f;
		mRemoteStickPoints[11] = 0f;

		ByteBuffer bb = ByteBuffer.allocateDirect(mRemoteStickPoints.length * 4);
		bb.order(ByteOrder.nativeOrder());
		remoteStickVertexBuffer = (FloatBuffer) bb.asFloatBuffer()
				.put(mRemoteStickPoints)
				.position(0);
	}

	@Override
	void setPointerID(int pointerID) {
		setVisible(true);
		super.setPointerID(pointerID);
	}

	@Override
	void clear() {
		setVisible(false);
		super.clear();
	}

	@Override
	boolean canCatchID(float x, float y, float ratio) {
		return x < limitScreen;
	}

	private void setVisible(boolean isRemoteVisible) {
		isVisible = isRemoteVisible;
	}

	@Override
	void updatePosition(float x, float y, float ratio) {
		mRemotePosition[0] = x * ratio;
		mRemotePosition[1] = y;
		mRemotePosition[2] = 0f;
		mRemoteStickPosition = mRemotePosition.clone();
	}

	@Override
	void updateStick(float x, float y, float ratio) {
		x *= ratio;
		if (x - mRemotePosition[0] > width * 0.5f - height * 0.5f) {
			mRemoteStickPosition[0] = mRemotePosition[0] + width * 0.5f - height * 0.5f;
		} else if (x - mRemotePosition[0] < -width * 0.5f + height * 0.5f) {
			mRemoteStickPosition[0] = mRemotePosition[0] - width * 0.5f + height * 0.5f;
		} else {
			mRemoteStickPosition[0] = x;
		}

	}

	float getRemoteLevel() {
		return isVisible ?
				(mRemoteStickPosition[0] - mRemotePosition[0]) / (0.5f * (width - height))
				: 0f;
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
			Matrix.translateM(mMMatrix, 0,
					mRemotePosition[0],
					mRemotePosition[1],
					mRemotePosition[2]);
			Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);

			GLES20.glEnableVertexAttribArray(mPositionHandle);
			GLES20.glVertexAttribPointer(mPositionHandle,
					3, GLES20.GL_FLOAT, false, 3 * 4, remoteVertexBuffer);
			GLES20.glUniform4fv(mColorHandle, 1, color, 0);
			GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
			GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, mRemotePoints.length / 3);

			Matrix.setIdentityM(mMMatrix, 0);
			Matrix.translateM(mMMatrix, 0,
					mRemoteStickPosition[0],
					mRemoteStickPosition[1],
					mRemoteStickPosition[2]);
			Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);

			GLES20.glEnableVertexAttribArray(mPositionHandle);
			GLES20.glVertexAttribPointer(mPositionHandle,
					3, GLES20.GL_FLOAT, false, 3 * 4, remoteStickVertexBuffer);
			GLES20.glUniform4fv(mColorHandle, 1, color, 0);
			GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
			GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, mRemoteStickPoints.length / 3);

			GLES20.glDisableVertexAttribArray(mPositionHandle);

			GLES20.glLineWidth(1f);
		}
	}
}
