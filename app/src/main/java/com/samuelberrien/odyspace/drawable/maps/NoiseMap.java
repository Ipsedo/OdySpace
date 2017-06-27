package com.samuelberrien.odyspace.drawable.maps;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.utils.collision.Box;
import com.samuelberrien.odyspace.utils.game.Item;
import com.samuelberrien.odyspace.utils.graphics.ShaderLoader;
import com.samuelberrien.odyspace.utils.maths.SimplexNoise;
import com.samuelberrien.odyspace.utils.maths.Vector;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by samuel on 11/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class NoiseMap implements Item, Map {

	private native boolean areCollided(float[] mPointItem1, float[] mModelMatrix1, float[] mPointItem2, float[] mModelMatrix2);

	static {
		System.loadLibrary("collision");
	}

	private Context context;

	private final int SIZE = 30;

	private float lightCoeff;
	private float distanceCoeff;

	private int coeffNoise;
	private float scale;
	private float limitHeight;
	private float coeffHeight;

	private float[] points;
	private float[] normals;
	private FloatBuffer mPositions;
	private FloatBuffer mNormals;

	private int mPositionsBufferId;
	private int mNormalsBufferId;

	private float[] mModelMatrix;

	private final int mProgram;
	private int mPositionHandle;
	private int mNormalHandle;
	private int mColorHandle;
	private int mMVPMatrixHandle;
	private int mLightPosHandle;
	private int mMVMatrixHandle;
	private int mDistanceCoefHandle;
	private int mLightCoefHandle;

	static final int COORDS_PER_VERTEX = 3;
	private final int vertexStride = COORDS_PER_VERTEX * 4;

	private float[] color;

	public NoiseMap(Context context, float[] color, float lightCoeff, float distanceCoeff, int coeffNoise, float scale, float limitHeight, float coeffHeight) {
		this.context = context;
		this.lightCoeff = lightCoeff;
		this.distanceCoeff = distanceCoeff;
		this.coeffNoise = coeffNoise;
		this.scale = scale;
		this.coeffHeight = coeffHeight;
		this.limitHeight = limitHeight;
		this.color = color;
		this.mModelMatrix = new float[16];

		int vertexShader = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShaderLoader.openShader(context, R.raw.noise_map_vs));
		int fragmentShader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderLoader.openShader(context, R.raw.noise_map_fs));

		this.mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
		GLES20.glAttachShader(this.mProgram, vertexShader);   // add the vertex shader to program
		GLES20.glAttachShader(this.mProgram, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(this.mProgram);

		this.bind();

		this.initPlan();

		this.bindBuffer();
	}

	private void initPlan() {
		ArrayList<Float> triangles = new ArrayList<>();
		ArrayList<Float> normales = new ArrayList<>();

		for (int i = 0; i < SIZE; i++) {
			float[] tmpPoints = new float[(SIZE + 1) * 2 * 3];
			for (int j = 0; j < SIZE + 1; j++) {
				tmpPoints[j * 2 * 3] = 2f * (float) j / (float) SIZE - 1f;
				tmpPoints[j * 2 * 3 + 1] = (float) SimplexNoise.noise((double) i / (double) (SIZE / this.coeffNoise), (double) j / (double) (SIZE / this.coeffNoise)) * this.coeffHeight;
				tmpPoints[j * 2 * 3 + 2] = 2f * (float) i / (float) SIZE - 1f;

				tmpPoints[(j * 2 + 1) * 3] = 2f * (float) j / (float) SIZE - 1f;
				tmpPoints[(j * 2 + 1) * 3 + 1] = (float) SimplexNoise.noise((double) (i + 1) / (double) (SIZE / this.coeffNoise), (double) j / (double) (SIZE / this.coeffNoise)) * this.coeffHeight;
				tmpPoints[(j * 2 + 1) * 3 + 2] = 2f * ((float) i + 1) / (float) SIZE - 1f;
			}
			for (int j = 0; j < tmpPoints.length / 3 - 2; j += 2) {

				//Triangle 1
				triangles.add(tmpPoints[j * 3 + 0]);
				triangles.add(tmpPoints[j * 3 + 1]);
				triangles.add(tmpPoints[j * 3 + 2]);
				triangles.add(tmpPoints[j * 3 + 3]);
				triangles.add(tmpPoints[j * 3 + 4]);
				triangles.add(tmpPoints[j * 3 + 5]);
				triangles.add(tmpPoints[j * 3 + 6]);
				triangles.add(tmpPoints[j * 3 + 7]);
				triangles.add(tmpPoints[j * 3 + 8]);

				//Normal 1
				float[] v1 = new float[]{tmpPoints[j * 3 + 6] - tmpPoints[j * 3 + 0], tmpPoints[j * 3 + 7] - tmpPoints[j * 3 + 1], tmpPoints[j * 3 + 8] - tmpPoints[j * 3 + 2]};
				float[] v2 = new float[]{tmpPoints[j * 3 + 3] - tmpPoints[j * 3 + 0], tmpPoints[j * 3 + 4] - tmpPoints[j * 3 + 1], tmpPoints[j * 3 + 5] - tmpPoints[j * 3 + 2]};
				float[] normal = Vector.normalize3f(Vector.cross3f(v2, v1));
				normales.add(normal[0]);
				normales.add(normal[1]);
				normales.add(normal[2]);
				normales.add(normal[0]);
				normales.add(normal[1]);
				normales.add(normal[2]);
				normales.add(normal[0]);
				normales.add(normal[1]);
				normales.add(normal[2]);

				//Triangle 2

				triangles.add(tmpPoints[(j + 1) * 3 + 3]);
				triangles.add(tmpPoints[(j + 1) * 3 + 4]);
				triangles.add(tmpPoints[(j + 1) * 3 + 5]);
				triangles.add(tmpPoints[(j + 1) * 3 + 0]);
				triangles.add(tmpPoints[(j + 1) * 3 + 1]);
				triangles.add(tmpPoints[(j + 1) * 3 + 2]);
				triangles.add(tmpPoints[(j + 1) * 3 + 6]);
				triangles.add(tmpPoints[(j + 1) * 3 + 7]);
				triangles.add(tmpPoints[(j + 1) * 3 + 8]);

				//Normal 2
				v1 = new float[]{tmpPoints[j * 3 + 9] - tmpPoints[j * 3 + 3], tmpPoints[j * 3 + 10] - tmpPoints[j * 3 + 4], tmpPoints[j * 3 + 11] - tmpPoints[j * 3 + 5]};
				v2 = new float[]{tmpPoints[j * 3 + 6] - tmpPoints[j * 3 + 3], tmpPoints[j * 3 + 7] - tmpPoints[j * 3 + 4], tmpPoints[j * 3 + 8] - tmpPoints[j * 3 + 5]};
				normal = Vector.normalize3f(Vector.cross3f(v1, v2));
				normales.add(normal[0]);
				normales.add(normal[1]);
				normales.add(normal[2]);
				normales.add(normal[0]);
				normales.add(normal[1]);
				normales.add(normal[2]);
				normales.add(normal[0]);
				normales.add(normal[1]);
				normales.add(normal[2]);
			}
		}

		this.points = new float[triangles.size()];
		this.normals = new float[normales.size()];
		for (int i = 0; i < this.points.length; i++) {
			this.points[i] = triangles.get(i);
			this.normals[i] = normales.get(i);
		}

		this.mPositions = ByteBuffer.allocateDirect(this.points.length * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.mPositions.put(this.points).position(0);

		this.mNormals = ByteBuffer.allocateDirect(this.normals.length * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.mNormals.put(this.normals).position(0);
	}

	private void bindBuffer() {
		int[] buffers = new int[2];

		GLES20.glGenBuffers(2, buffers, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, this.mPositions.capacity() * 4, this.mPositions, GLES20.GL_STATIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, this.mNormals.capacity() * 4, this.mNormals, GLES20.GL_STATIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		this.mPositionsBufferId = buffers[0];
		this.mNormalsBufferId = buffers[1];

		this.mPositions.limit(0);
		this.mPositions = null;
		this.mNormals.limit(0);
		this.mNormals = null;
	}

	@Override
	public float[] passToModelMatrix(float[] triangles) {
		float[] tmp;
		float[] res = new float[triangles.length];
		for (int i = 0; i < triangles.length; i += 3) {
			tmp = new float[]{triangles[i], triangles[i + 1], triangles[i + 2], 1f};
			Matrix.multiplyMV(tmp, 0, this.mModelMatrix, 0, tmp.clone(), 0);
			res[i] = tmp[0];
			res[i + 1] = tmp[1];
			res[i + 2] = tmp[2];
		}
		return res;
	}

	private void bind() {
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
		mMVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVMatrix");
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "u_Amb_Color");
		mLightPosHandle = GLES20.glGetUniformLocation(mProgram, "u_LightPos");
		mDistanceCoefHandle = GLES20.glGetUniformLocation(mProgram, "u_distance_coef");
		mLightCoefHandle = GLES20.glGetUniformLocation(mProgram, "u_light_coef");
		mNormalHandle = GLES20.glGetAttribLocation(mProgram, "a_Normal");
	}

	@Override
	public float[] getRestreintArea(float[] position) {
		if (position[0] >= -1f * this.scale && position[0] <= 1f * this.scale && position[2] >= -1f * this.scale && position[2] <= 1f * this.scale) {
			float xNorm = (position[0] / this.scale + 1f) / 2f;
			float zNorm = (position[2] / this.scale + 1f) / 2f;

			float pas = 1f / (float) SIZE;

			int i = (int) (xNorm / pas);
			int j = (int) (zNorm / pas);

			float[] res = new float[18];
			int indRes = 0;
			for (int a = j * 2 * SIZE; a <= j * 2 * SIZE; a++) {
				for (int b = i * 2; b <= i * 2 + 1; b++) {
					res[indRes + 0] = (this.points[(a + b) * 3 * 3 + 0]);
					res[indRes + 1] = (this.points[(a + b) * 3 * 3 + 1]);
					res[indRes + 2] = (this.points[(a + b) * 3 * 3 + 2]);

					res[indRes + 3] = (this.points[(a + b) * 3 * 3 + 3]);
					res[indRes + 4] = (this.points[(a + b) * 3 * 3 + 4]);
					res[indRes + 5] = (this.points[(a + b) * 3 * 3 + 5]);

					res[indRes + 6] = (this.points[(a + b) * 3 * 3 + 6]);
					res[indRes + 7] = (this.points[(a + b) * 3 * 3 + 7]);
					res[indRes + 8] = (this.points[(a + b) * 3 * 3 + 8]);
					indRes += 9;
				}
			}

			return res;
		} else {
			return new float[18];
		}
	}

	@Override
	public void update() {
		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, 0f, this.limitHeight, 0f);
		Matrix.scaleM(mModelMatrix, 0, this.scale, this.scale, this.scale);

		this.mModelMatrix = mModelMatrix;
	}

	@Override
	public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] unused) {
		float[] mvpMatrix = new float[16];
		float[] mvMatrix = new float[16];
		Matrix.multiplyMM(mvMatrix, 0, mViewMatrix, 0, this.mModelMatrix, 0);
		Matrix.multiplyMM(mvpMatrix, 0, mProjectionMatrix, 0, mvMatrix, 0);

		GLES20.glUseProgram(mProgram);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.mPositionsBufferId);
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.mNormalsBufferId);
		GLES20.glEnableVertexAttribArray(mNormalHandle);
		GLES20.glVertexAttribPointer(mNormalHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		GLES20.glUniform4fv(this.mColorHandle, 1, this.color, 0);

		GLES20.glUniformMatrix4fv(this.mMVMatrixHandle, 1, false, mvMatrix, 0);

		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

		GLES20.glUniform3fv(mLightPosHandle, 1, mLightPosInEyeSpace, 0);

		GLES20.glUniform1f(mDistanceCoefHandle, this.distanceCoeff);

		GLES20.glUniform1f(mLightCoefHandle, this.lightCoeff);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, this.points.length / 3);

		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}

	@Override
	public boolean collideTest(float[] triangleArray, float[] modelMatrix) {
		float[] position = new float[]{0f, 0f, 0f, 1f};
		Matrix.multiplyMV(position, 0, modelMatrix, 0, position.clone(), 0);
		return this.areCollided(this.getRestreintArea(new float[]{position[0], position[1], position[2]}), this.mModelMatrix.clone(), triangleArray, modelMatrix);
		//return this.areCollided(this.points.clone(), this.mModelMatrix.clone(), triangleArray, modelMatrix);
	}

	@Override
	public boolean isCollided(Item other) {
		return other.collideTest(this.getRestreintArea(other.getPosition()), this.mModelMatrix.clone());
	}

	@Override
	public boolean isInside(Box otherBox) {
		Box box = new Box(-this.scale, this.limitHeight - this.coeffHeight * this.scale, -this.scale, this.scale * 2f, 2f * this.coeffHeight * this.scale, this.scale * 2f);
		return box.isInside(otherBox);
	}

	@Override
	public int getDamage() {
		return Integer.MAX_VALUE - 1;
	}

	@Override
	public void decrementLife(int minus) {

	}

	@Override
	public float[] getPosition() {
		return new float[]{-0.5f * this.scale, this.limitHeight, -0.5f * this.scale};
	}
}
