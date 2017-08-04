package com.samuelberrien.odyspace.drawable.controls;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.utils.graphics.Color;
import com.samuelberrien.odyspace.utils.graphics.ShaderLoader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.samuelberrien.odyspace.drawable.controls.GamePad.limitScreen;

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
		this.isVisible = false;
	}

	@Override
	void initGraphics(Context context) {
		int vertexShader = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShaderLoader.openShader(context, R.raw.simple_vs));
		int fragmentShader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderLoader.openShader(context, R.raw.simple_fs));

		this.mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
		GLES20.glAttachShader(this.mProgram, vertexShader);   // add the vertex shader to program
		GLES20.glAttachShader(this.mProgram, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(this.mProgram);

		this.makeRemote();
		this.makeRemoteStick();
		this.bind();
	}

	private void bind() {
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
	}

	private void makeRemote() {
		this.mRemotePoints[0] = this.width * 0.5f;
		this.mRemotePoints[1] = this.height * 0.5f;
		this.mRemotePoints[2] = 0f;

		this.mRemotePoints[3] = this.width * 0.5f;
		this.mRemotePoints[4] = -this.height * 0.5f;
		this.mRemotePoints[5] = 0f;

		this.mRemotePoints[6] = -this.width * 0.5f;
		this.mRemotePoints[7] = -this.height * 0.5f;
		this.mRemotePoints[8] = 0f;

		this.mRemotePoints[9] = -this.width * 0.5f;
		this.mRemotePoints[10] = this.height * 0.5f;
		this.mRemotePoints[11] = 0f;

		ByteBuffer bb = ByteBuffer.allocateDirect(this.mRemotePoints.length * 4);
		bb.order(ByteOrder.nativeOrder());
		this.remoteVertexBuffer = (FloatBuffer) bb.asFloatBuffer()
				.put(this.mRemotePoints)
				.position(0);
	}

	private void makeRemoteStick() {
		this.mRemoteStickPoints[0] = this.height * 0.5f;
		this.mRemoteStickPoints[1] = this.height * 0.5f;
		this.mRemoteStickPoints[2] = 0f;

		this.mRemoteStickPoints[3] = this.height * 0.5f;
		this.mRemoteStickPoints[4] = -this.height * 0.5f;
		this.mRemoteStickPoints[5] = 0f;

		this.mRemoteStickPoints[6] = -this.height * 0.5f;
		this.mRemoteStickPoints[7] = -this.height * 0.5f;
		this.mRemoteStickPoints[8] = 0f;

		this.mRemoteStickPoints[9] = -this.height * 0.5f;
		this.mRemoteStickPoints[10] = this.height * 0.5f;
		this.mRemoteStickPoints[11] = 0f;

		ByteBuffer bb = ByteBuffer.allocateDirect(this.mRemoteStickPoints.length * 4);
		bb.order(ByteOrder.nativeOrder());
		this.remoteStickVertexBuffer = (FloatBuffer) bb.asFloatBuffer()
				.put(this.mRemoteStickPoints)
				.position(0);
	}

	@Override
	void setPointerID(int pointerID) {
		this.setVisible(true);
		super.setPointerID(pointerID);
	}

	@Override
	void clear() {
		this.setVisible(false);
		super.clear();
	}

	@Override
	boolean isTouching(float x, float y, float ratio) {
		return x < limitScreen;
	}

	private void setVisible(boolean isRemoteVisible) {
		this.isVisible = isRemoteVisible;
	}

	@Override
	void updatePosition(float x, float y, float ratio) {
		this.mRemotePosition[0] = x * ratio;
		this.mRemotePosition[1] = y;
		this.mRemotePosition[2] = 0f;
		this.mRemoteStickPosition = this.mRemotePosition.clone();
	}

	@Override
	void updateStick(float x, float y, float ratio) {
		x *= ratio;
		if (x - this.mRemotePosition[0] > this.width * 0.5f - this.height * 0.5f) {
			this.mRemoteStickPosition[0] = this.mRemotePosition[0] + this.width * 0.5f - this.height * 0.5f;
		} else if (x - this.mRemotePosition[0] < -this.width * 0.5f + this.height * 0.5f) {
			this.mRemoteStickPosition[0] = this.mRemotePosition[0] - this.width * 0.5f + this.height * 0.5f;
		} else {
			this.mRemoteStickPosition[0] = x;
		}

	}

	float getRemoteLevel() {
		return this.isVisible ? (this.mRemoteStickPosition[0] - this.mRemotePosition[0]) / (0.5f * (this.width - this.height)) : 0f;
	}

	@Override
	public void draw(float ratio) {
		if (this.isVisible) {
			GLES20.glUseProgram(this.mProgram);

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
			Matrix.translateM(mMMatrix, 0, this.mRemotePosition[0], this.mRemotePosition[1], this.mRemotePosition[2]);
			Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);

			GLES20.glEnableVertexAttribArray(mPositionHandle);
			GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, this.remoteVertexBuffer);
			GLES20.glUniform4fv(mColorHandle, 1, color, 0);
			GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
			GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, this.mRemotePoints.length / 3);

			Matrix.setIdentityM(mMMatrix, 0);
			Matrix.translateM(mMMatrix, 0, this.mRemoteStickPosition[0], this.mRemoteStickPosition[1], this.mRemoteStickPosition[2]);
			Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);

			GLES20.glEnableVertexAttribArray(mPositionHandle);
			GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, this.remoteStickVertexBuffer);
			GLES20.glUniform4fv(mColorHandle, 1, color, 0);
			GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
			GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, this.mRemoteStickPoints.length / 3);

			GLES20.glDisableVertexAttribArray(mPositionHandle);

			GLES20.glLineWidth(1f);
		}
	}
}
