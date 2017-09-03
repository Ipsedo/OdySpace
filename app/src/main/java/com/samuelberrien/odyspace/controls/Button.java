package com.samuelberrien.odyspace.controls;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.obj.ObjModel;
import com.samuelberrien.odyspace.utils.graphics.Color;
import com.samuelberrien.odyspace.utils.graphics.ShaderLoader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Jean-François on 12/08/2017.
 */

public class Button extends Control {

	private int mPositionHandle;
	private int mColorHandle;
	private int mMVPMatrixHandle;
	private int mProgram;

	private static int nbPoint = 64;
	private float[] buttonPoints = new float[this.nbPoint * 3];
	private FloatBuffer buttonVertexBuffer;
	private String objFileName;
	private ObjModel logo;

	private float ray;
	private float[] mPosition;// = new float[]{-1f + 1e-2f, -1f + FireButtonRay + 1e-2f, 0f};

	private static float color[] = Color.ControlsColor;

	private boolean isTouching;

	public Button(String objFileName, float ray, float[] mPosition) {
		this.objFileName = objFileName;
		this.isTouching = false;
		this.ray = ray;
		this.mPosition = mPosition;
	}

	@Override
	public void draw(float ratio) {
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
		Matrix.translateM(mMMatrix, 0, this.mPosition[0] * ratio + ray, this.mPosition[1], this.mPosition[2]);
		Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);

		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, this.buttonVertexBuffer);
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, this.buttonPoints.length / 3);

		GLES20.glDisableVertexAttribArray(mPositionHandle);
		GLES20.glLineWidth(1f);

		Matrix.setIdentityM(mMMatrix, 0);
		Matrix.translateM(mMMatrix, 0, this.mPosition[0] * ratio + ray, this.mPosition[1], this.mPosition[2]);
		Matrix.scaleM(mMMatrix, 0, ray / 1.2f, ray / 1.2f, ray / 1.2f);
		Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);

		this.logo.draw(mMVPMatrix, mVPMatrix, new float[]{0f, 0f, -1f}, new float[0]);
	}

	@Override
	boolean canCatchID(float x, float y, float ratio) {
		float xRef = x * ratio - (this.mPosition[0] * ratio + ray);
		float yRef = y - this.mPosition[1];
		if (xRef * xRef + yRef * yRef < ray * ray) {
			this.isTouching = true;
			return true;
		} else {
			this.isTouching = false;
			return false;
		}
	}

	public boolean isTouching() {
		if (isTouching) {
			isTouching = false;
			return true;
		} else {
			return false;
		}
	}

	@Override
	void updatePosition(float x, float y, float ratio) {

	}

	@Override
	void updateStick(float x, float y, float ratio) {

	}

	@Override
	void initGraphics(Context context) {
		int vertexShader = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShaderLoader.openShader(context, R.raw.simple_vs));
		int fragmentShader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderLoader.openShader(context, R.raw.simple_fs));

		this.mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
		GLES20.glAttachShader(this.mProgram, vertexShader);   // add the vertex shader to program
		GLES20.glAttachShader(this.mProgram, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(this.mProgram);

		this.bind();
		this.makeFireButton();
		this.logo = new ObjModel(context, objFileName, color[0], color[1], color[2], 1f, 0f, 0f);
	}

	private void bind() {
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
	}

	private void makeFireButton() {
		for (int i = 0; i < nbPoint; i++) {
			double mTmpAngle = (double) (i - 1) * Math.PI * 2d / (double) nbPoint;
			this.buttonPoints[i * 3 + 0] = (float) (ray * Math.cos(mTmpAngle));
			this.buttonPoints[i * 3 + 1] = (float) (ray * Math.sin(mTmpAngle));
			this.buttonPoints[i * 3 + 2] = 0f;
		}
		ByteBuffer bb = ByteBuffer.allocateDirect(this.buttonPoints.length * 4);
		bb.order(ByteOrder.nativeOrder());
		this.buttonVertexBuffer = bb.asFloatBuffer();
		this.buttonVertexBuffer.put(this.buttonPoints);
		this.buttonVertexBuffer.position(0);
	}
}
