package com.samuelberrien.odyspace.drawable;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.utils.graphics.Color;
import com.samuelberrien.odyspace.utils.graphics.ShaderLoader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by samuel on 11/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class ProgressBar implements GLInfoDrawable {

	private final int maxProgress;
	private int currProgress;

	private FloatBuffer container;
	private FloatBuffer progressRect;

	private float x;
	private float y;

	private float[] color;
	private float[] containerColor = Color.ControlsColor;

	private int mPositionHandle;
	private int mColorHandle;
	private int mMVPMatrixHandle;
	private int mProgram;

	public ProgressBar(Context context, int maxProgress, float x, float y, float[] color) {
		this.maxProgress = maxProgress;
		currProgress = 0;
		this.x = x;
		this.y = y;
		this.color = color;
		int vertexShader = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShaderLoader.openShader(context, R.raw.simple_vs));
		int fragmentShader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderLoader.openShader(context, R.raw.simple_fs));
		mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(mProgram);
		makeContainer();
		makeProgressRect();
		bind();
	}

	private void makeContainer() {
		float[] mPoints = new float[3 * 4];
		mPoints[0] = -1f;
		mPoints[1] = -1f;
		mPoints[2] = 0f;

		mPoints[3] = -1f;
		mPoints[4] = 1f;
		mPoints[5] = 0f;

		mPoints[6] = 1f;
		mPoints[7] = 1f;
		mPoints[8] = 0f;

		mPoints[9] = 1f;
		mPoints[10] = -1f;
		mPoints[11] = 0f;

		ByteBuffer bb = ByteBuffer.allocateDirect(mPoints.length * 4);
		bb.order(ByteOrder.nativeOrder());
		container = bb.asFloatBuffer();
		container.put(mPoints);
		container.position(0);
	}

	private void makeProgressRect() {
		float[] mPoints = new float[3 * 4];
		mPoints[0] = -1f;
		mPoints[1] = -1f;
		mPoints[2] = 0f;

		mPoints[3] = -1f;
		mPoints[4] = 1f;
		mPoints[5] = 0f;

		mPoints[6] = 1f;
		mPoints[7] = -1f;
		mPoints[8] = 0f;

		mPoints[9] = 1f;
		mPoints[10] = 1f;
		mPoints[11] = 0f;

		ByteBuffer bb = ByteBuffer.allocateDirect(mPoints.length * 4);
		bb.order(ByteOrder.nativeOrder());
		progressRect = bb.asFloatBuffer();
		progressRect.put(mPoints);
		progressRect.position(0);
	}

	private void bind() {
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
	}

	public void updateProgress(int newProgress) {
		currProgress = newProgress;
	}

	@Override
	public void draw(float ratio) {
		GLES20.glUseProgram(mProgram);

		float[] mViewMatrix = new float[16];
		Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -1, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
		float[] mVPMatrix = new float[16];
		float[] mPMatrix = new float[16];
		Matrix.orthoM(mPMatrix, 0, -1f * ratio, 1f * ratio, -1f, 1f, -1f, 1f);
		Matrix.multiplyMM(mVPMatrix, 0, mPMatrix, 0, mViewMatrix, 0);
		float[] mMVPMatrix = new float[16];

		float[] mMMatrix = new float[16];
		Matrix.setIdentityM(mMMatrix, 0);
		Matrix.translateM(mMMatrix, 0, x, y, 0f);
		Matrix.scaleM(mMMatrix, 0, 0.501f, 0.051f, 0.051f);
		Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);

		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, container);
		GLES20.glUniform4fv(mColorHandle, 1, containerColor, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, 4);

		Matrix.setIdentityM(mMMatrix, 0);
		Matrix.translateM(mMMatrix, 0, x + 0.5f * (float) (maxProgress - currProgress) / (float) maxProgress, y, 0f);
		Matrix.scaleM(mMMatrix, 0, 0.5f * (float) currProgress / (float) maxProgress, 0.05f, 0.05f);
		Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);

		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, progressRect);
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
	}
}
