package com.samuelberrien.odyspace.drawable.controls;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.GLInfoDrawable;
import com.samuelberrien.odyspace.drawable.obj.ObjModel;
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

public class Controls implements GLInfoDrawable {

	//private boolean isBoostVisible;

	private float boostWidth = 0.3f;
	private float boostHeight = 1f;
	private float[] mBoostPoint = new float[3 * 4];
	private FloatBuffer boostVertexBuffer;
	private float[] mBoostPosition = new float[3];
	private float[] mBoostStickPoint = new float[3 * 4];
	private FloatBuffer boostStickVertexBuffer;
	private float[] mBoostStickPosition = new float[3];

	private int nbPoint = 64;
	private float fireButtonRay = 0.3f;
	private float[] fireButtonPoints = new float[this.nbPoint * 3];
	private FloatBuffer fireButtonVertexBuffer;
	private float[] mFireButtonPosition = new float[]{-1f + 1e-2f, -1f + this.fireButtonRay + 1e-2f, 0f};
	private ObjModel fireLogo;
	private boolean isFire;

	private int mPositionHandle;
	private int mColorHandle;
	private int mMVPMatrixHandle;
	private int mProgram;

	private float color[] = Color.ControlsColor;

	public Controls() {
		this.mBoostPosition[0] = -1f + 1e-2f;
		this.mBoostPosition[1] = 1.5e-1f;
		this.mBoostPosition[2] = 0f;
		this.mBoostStickPosition = this.mBoostPosition.clone();
		this.isFire = false;
	}

	public void initGraphics(Context context) {
		int vertexShader = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShaderLoader.openShader(context, R.raw.simple_vs));
		int fragmentShader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderLoader.openShader(context, R.raw.simple_fs));

		this.mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
		GLES20.glAttachShader(this.mProgram, vertexShader);   // add the vertex shader to program
		GLES20.glAttachShader(this.mProgram, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(this.mProgram);

		this.bind();
		this.makeBoost();
		this.makeFireButton();
		this.makeBoostStick();
		this.fireLogo = new ObjModel(context, "bullet.obj", this.color[0], this.color[1], this.color[2], 1f, 0f, 0f);
	}

	private void bind() {
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
	}

	private void makeBoost() {
		this.mBoostPoint[0] = this.boostWidth * 0.5f;
		this.mBoostPoint[1] = this.boostHeight * 0.5f;
		this.mBoostPoint[2] = 0f;

		this.mBoostPoint[3] = this.boostWidth * 0.5f;
		this.mBoostPoint[4] = -this.boostHeight * 0.5f;
		this.mBoostPoint[5] = 0f;

		this.mBoostPoint[6] = -this.boostWidth * 0.5f;
		this.mBoostPoint[7] = -this.boostHeight * 0.5f;
		this.mBoostPoint[8] = 0f;

		this.mBoostPoint[9] = -this.boostWidth * 0.5f;
		this.mBoostPoint[10] = this.boostHeight * 0.5f;
		this.mBoostPoint[11] = 0f;

		ByteBuffer bb = ByteBuffer.allocateDirect(this.mBoostPoint.length * 4);
		bb.order(ByteOrder.nativeOrder());
		this.boostVertexBuffer = bb.asFloatBuffer();
		this.boostVertexBuffer.put(this.mBoostPoint);
		this.boostVertexBuffer.position(0);
	}

	private void makeBoostStick() {
		this.mBoostStickPoint[0] = this.boostWidth * 0.5f;
		this.mBoostStickPoint[1] = this.boostWidth * 0.5f;
		this.mBoostPoint[2] = 0f;

		this.mBoostStickPoint[3] = this.boostWidth * 0.5f;
		this.mBoostStickPoint[4] = -this.boostWidth * 0.5f;
		this.mBoostStickPoint[5] = 0f;

		this.mBoostStickPoint[6] = -this.boostWidth * 0.5f;
		this.mBoostStickPoint[7] = -this.boostWidth * 0.5f;
		this.mBoostStickPoint[8] = 0f;

		this.mBoostStickPoint[9] = -this.boostWidth * 0.5f;
		this.mBoostStickPoint[10] = this.boostWidth * 0.5f;
		this.mBoostStickPoint[11] = 0f;

		ByteBuffer bb = ByteBuffer.allocateDirect(this.mBoostStickPoint.length * 4);
		bb.order(ByteOrder.nativeOrder());
		this.boostStickVertexBuffer = bb.asFloatBuffer();
		this.boostStickVertexBuffer.put(this.mBoostStickPoint);
		this.boostStickVertexBuffer.position(0);
	}


	private void makeFireButton() {
		for (int i = 0; i < this.nbPoint; i++) {
			double mTmpAngle = (double) (i - 1) * Math.PI * 2d / (double) this.nbPoint;
			this.fireButtonPoints[i * 3 + 0] = (float) (this.fireButtonRay * Math.cos(mTmpAngle));
			this.fireButtonPoints[i * 3 + 1] = (float) (this.fireButtonRay * Math.sin(mTmpAngle));
			this.fireButtonPoints[i * 3 + 2] = 0f;
		}
		ByteBuffer bb = ByteBuffer.allocateDirect(this.fireButtonPoints.length * 4);
		bb.order(ByteOrder.nativeOrder());
		this.fireButtonVertexBuffer = bb.asFloatBuffer();
		this.fireButtonVertexBuffer.put(this.fireButtonPoints);
		this.fireButtonVertexBuffer.position(0);
	}

	public boolean isTouchBoost(float x, float y, float ratio) {
		if (x * ratio < this.mBoostPosition[0] * ratio - this.boostWidth * 0.5f + this.fireButtonRay) {
			return false;
		} else if (x * ratio > this.mBoostPosition[0] * ratio + this.boostWidth * 0.5f + this.fireButtonRay) {
			return false;
		} else if (y < this.mBoostPosition[1] - this.boostHeight * 0.5f + this.boostWidth * 0.5f) {
			return false;
		} else if (y > this.mBoostPosition[1] + this.boostHeight * 0.5f - this.boostWidth * 0.5f) {
			return false;
		} else {
			this.mBoostStickPosition[1] = y;
			return true;
		}
	}

	public boolean isTouchFireButton(float x, float y, float ratio) {
		float xRef = x * ratio - (this.mFireButtonPosition[0] * ratio + this.fireButtonRay);
		float yRef = y - this.mFireButtonPosition[1];
		if (xRef * xRef + yRef * yRef < this.fireButtonRay * this.fireButtonRay) {
			this.isFire = true;
			return true;
		} else {
			this.isFire = false;
			return false;
		}
	}

	public boolean isFire() {
		return this.isFire;
	}

	public void turnOffFire() {
		this.isFire = false;
	}

	/**
	 * Return current boost
	 *
	 * @return a float between -1 (min) to 1 (max)
	 */
	public float getBoost() {
		return (this.mBoostStickPosition[1] - this.mBoostPosition[1]) / (0.5f * (this.boostHeight - this.boostWidth));
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
		Matrix.translateM(mMMatrix, 0, this.mBoostPosition[0] * ratio + this.fireButtonRay, this.mBoostPosition[1], this.mBoostPosition[2]);
		Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);

		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, this.boostVertexBuffer);
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, this.mBoostPoint.length / 3);

		Matrix.setIdentityM(mMMatrix, 0);
		Matrix.translateM(mMMatrix, 0, this.mBoostStickPosition[0] * ratio + this.fireButtonRay, this.mBoostStickPosition[1], this.mBoostStickPosition[2]);
		Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);

		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, this.boostStickVertexBuffer);
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, this.mBoostStickPoint.length / 3);

		Matrix.setIdentityM(mMMatrix, 0);
		Matrix.translateM(mMMatrix, 0, this.mFireButtonPosition[0] * ratio + this.fireButtonRay, this.mFireButtonPosition[1], this.mFireButtonPosition[2]);
		Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);

		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, this.fireButtonVertexBuffer);
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, this.fireButtonPoints.length / 3);

		GLES20.glDisableVertexAttribArray(mPositionHandle);

		Matrix.setIdentityM(mMMatrix, 0);
		Matrix.translateM(mMMatrix, 0, this.mFireButtonPosition[0] * ratio + this.fireButtonRay, this.mFireButtonPosition[1], this.mFireButtonPosition[2]);
		Matrix.scaleM(mMMatrix, 0, this.fireButtonRay / 1.2f, this.fireButtonRay / 1.2f, this.fireButtonRay / 1.2f);
		Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);

		GLES20.glLineWidth(1f);

		this.fireLogo.draw(mMVPMatrix, mVPMatrix, new float[]{0f, 0f, -1f}, new float[0]);
	}
}
