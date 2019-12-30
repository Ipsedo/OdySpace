package com.samuelberrien.odyspace.drawable.maps;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.core.Item;
import com.samuelberrien.odyspace.core.collision.Box;
import com.samuelberrien.odyspace.core.collision.TriangleCollision;
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

	private static final int POSITION_DATA_SIZE = 3;

	private static final int NORMAL_DATA_SIZE = 3;

	private static final int BYTES_PER_FLOAT = 4;

	private static final int STRIDE = (POSITION_DATA_SIZE + NORMAL_DATA_SIZE) * BYTES_PER_FLOAT;

	private final int SIZE = 30;

	private float lightCoeff;
	private float distanceCoeff;

	private int coeffNoise;
	private float scale;
	private float limitHeight;
	private float coeffHeight;

	private float[] points;

	private FloatBuffer mDataPackedBuffer;

	private int mDataPackedBufferId;

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

	private float[] color;

	private Box box;

	public NoiseMap(Context context,
					float[] color,
					float lightCoeff,
					float distanceCoeff,
					int coeffNoise,
					float scale,
					float limitHeight,
					float coeffHeight) {
		this.lightCoeff = lightCoeff;
		this.distanceCoeff = distanceCoeff;
		this.coeffNoise = coeffNoise;
		this.scale = scale;
		this.coeffHeight = coeffHeight;
		this.limitHeight = limitHeight;
		this.color = color;
		mModelMatrix = new float[16];

		int vertexShader = ShaderLoader.loadShader(
				GLES20.GL_VERTEX_SHADER,
				ShaderLoader.openShader(context, R.raw.noise_map_vs));
		int fragmentShader = ShaderLoader.loadShader(
				GLES20.GL_FRAGMENT_SHADER,
				ShaderLoader.openShader(context, R.raw.noise_map_fs));

		mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(mProgram);

		bind();

		initPlan();

		bindBuffer();

		makeBox();
	}

	private void initPlan() {
		ArrayList<Float> triangles = new ArrayList<>();
		ArrayList<Float> packedData = new ArrayList<>();

		for (int i = 0; i < SIZE; i++) {
			float[] tmpPoints = new float[(SIZE + 1) * 2 * 3];
			for (int j = 0; j < SIZE + 1; j++) {
				tmpPoints[j * 2 * 3] = 2f * (float) j / (float) SIZE - 1f;
				tmpPoints[j * 2 * 3 + 1] = (float) SimplexNoise.noise(
						(double) i / (double) (SIZE / coeffNoise),
						(double) j / (double) (SIZE / coeffNoise))
						* coeffHeight;
				tmpPoints[j * 2 * 3 + 2] = 2f * (float) i / (float) SIZE - 1f;

				tmpPoints[(j * 2 + 1) * 3] = 2f * (float) j / (float) SIZE - 1f;
				tmpPoints[(j * 2 + 1) * 3 + 1] = (float) SimplexNoise.noise(
						(double) (i + 1) / (double) (SIZE / coeffNoise),
						(double) j / (double) (SIZE / coeffNoise))
						* coeffHeight;
				tmpPoints[(j * 2 + 1) * 3 + 2] = 2f * ((float) i + 1) / (float) SIZE - 1f;
			}
			for (int j = 0; j < tmpPoints.length / 3 - 2; j += 2) {

				//Triangle 1
				//Normal 1
				float[] v1 = new float[]{
						tmpPoints[j * 3 + 6] - tmpPoints[j * 3],
						tmpPoints[j * 3 + 7] - tmpPoints[j * 3 + 1],
						tmpPoints[j * 3 + 8] - tmpPoints[j * 3 + 2]};
				float[] v2 = new float[]{
						tmpPoints[j * 3 + 3] - tmpPoints[j * 3],
						tmpPoints[j * 3 + 4] - tmpPoints[j * 3 + 1],
						tmpPoints[j * 3 + 5] - tmpPoints[j * 3 + 2]};
				float[] normal = Vector.normalize3f(Vector.cross3f(v2, v1));

				//POINT 1
				triangles.add(tmpPoints[j * 3]);
				triangles.add(tmpPoints[j * 3 + 1]);
				triangles.add(tmpPoints[j * 3 + 2]);

				packedData.add(tmpPoints[j * 3]);
				packedData.add(tmpPoints[j * 3 + 1]);
				packedData.add(tmpPoints[j * 3 + 2]);
				packedData.add(normal[0]);
				packedData.add(normal[1]);
				packedData.add(normal[2]);

				//POINT 2
				triangles.add(tmpPoints[j * 3 + 3]);
				triangles.add(tmpPoints[j * 3 + 4]);
				triangles.add(tmpPoints[j * 3 + 5]);

				packedData.add(tmpPoints[j * 3 + 3]);
				packedData.add(tmpPoints[j * 3 + 4]);
				packedData.add(tmpPoints[j * 3 + 5]);
				packedData.add(normal[0]);
				packedData.add(normal[1]);
				packedData.add(normal[2]);

				//POINT 3
				triangles.add(tmpPoints[j * 3 + 6]);
				triangles.add(tmpPoints[j * 3 + 7]);
				triangles.add(tmpPoints[j * 3 + 8]);

				packedData.add(tmpPoints[j * 3 + 6]);
				packedData.add(tmpPoints[j * 3 + 7]);
				packedData.add(tmpPoints[j * 3 + 8]);
				packedData.add(normal[0]);
				packedData.add(normal[1]);
				packedData.add(normal[2]);


				//Triangle 2

				v1 = new float[]{
						tmpPoints[j * 3 + 9] - tmpPoints[j * 3 + 3],
						tmpPoints[j * 3 + 10] - tmpPoints[j * 3 + 4],
						tmpPoints[j * 3 + 11] - tmpPoints[j * 3 + 5]};
				v2 = new float[]{
						tmpPoints[j * 3 + 6] - tmpPoints[j * 3 + 3],
						tmpPoints[j * 3 + 7] - tmpPoints[j * 3 + 4],
						tmpPoints[j * 3 + 8] - tmpPoints[j * 3 + 5]};
				normal = Vector.normalize3f(Vector.cross3f(v1, v2));

				//POINT 1
				triangles.add(tmpPoints[(j + 1) * 3 + 3]);
				triangles.add(tmpPoints[(j + 1) * 3 + 4]);
				triangles.add(tmpPoints[(j + 1) * 3 + 5]);

				packedData.add(tmpPoints[(j + 1) * 3 + 3]);
				packedData.add(tmpPoints[(j + 1) * 3 + 4]);
				packedData.add(tmpPoints[(j + 1) * 3 + 5]);
				packedData.add(normal[0]);
				packedData.add(normal[1]);
				packedData.add(normal[2]);

				//POINT 2
				triangles.add(tmpPoints[(j + 1) * 3]);
				triangles.add(tmpPoints[(j + 1) * 3 + 1]);
				triangles.add(tmpPoints[(j + 1) * 3 + 2]);

				packedData.add(tmpPoints[(j + 1) * 3]);
				packedData.add(tmpPoints[(j + 1) * 3 + 1]);
				packedData.add(tmpPoints[(j + 1) * 3 + 2]);
				packedData.add(normal[0]);
				packedData.add(normal[1]);
				packedData.add(normal[2]);

				//POINT 3
				triangles.add(tmpPoints[(j + 1) * 3 + 6]);
				triangles.add(tmpPoints[(j + 1) * 3 + 7]);
				triangles.add(tmpPoints[(j + 1) * 3 + 8]);

				packedData.add(tmpPoints[(j + 1) * 3 + 6]);
				packedData.add(tmpPoints[(j + 1) * 3 + 7]);
				packedData.add(tmpPoints[(j + 1) * 3 + 8]);
				packedData.add(normal[0]);
				packedData.add(normal[1]);
				packedData.add(normal[2]);
			}
		}

		points = new float[triangles.size()];
		for (int i = 0; i < points.length; i++)
			points[i] = triangles.get(i);

		float[] packedDataArray = new float[packedData.size()];
		for (int i = 0; i < packedDataArray.length; i++)
			packedDataArray[i] = packedData.get(i);

		mDataPackedBuffer = ByteBuffer.allocateDirect(packedDataArray.length * BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		mDataPackedBuffer.put(packedDataArray)
				.position(0);
	}

	private void bindBuffer() {
		int[] buffers = new int[1];

		GLES20.glGenBuffers(1, buffers, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
				mDataPackedBuffer.capacity() * 4, mDataPackedBuffer, GLES20.GL_STATIC_DRAW);

		mDataPackedBufferId = buffers[0];
		mDataPackedBuffer.limit(0);
		mDataPackedBuffer = null;
	}

	@Override
	public float[] passToModelMatrix(float[] triangles) {
		float[] tmp;
		float[] res = new float[triangles.length];
		for (int i = 0; i < triangles.length; i += 3) {
			tmp = new float[]{triangles[i], triangles[i + 1], triangles[i + 2], 1f};
			Matrix.multiplyMV(tmp, 0, mModelMatrix, 0, tmp.clone(), 0);
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
		if (position[0] >= -1f * scale
				&& position[0] <= 1f * scale
				&& position[2] >= -1f * scale
				&& position[2] <= 1f * scale) {
			float xNorm = (position[0] / scale + 1f) * 0.5f;
			float zNorm = (position[2] / scale + 1f) * 0.5f;

			int i = (int) (xNorm * SIZE);
			int j = (int) (zNorm * SIZE);

			float[] res = new float[18];
			int indRes = 0;
			for (int a = j * 2 * SIZE; a <= j * 2 * SIZE; a++) {
				for (int b = i * 2; b <= i * 2 + 1; b++) {
					int tmp = (a + b) * 3 * 3;
					res[indRes] = points[tmp];
					res[indRes + 1] = points[tmp + 1];
					res[indRes + 2] = points[tmp + 2];

					res[indRes + 3] = points[tmp + 3];
					res[indRes + 4] = points[tmp + 4];
					res[indRes + 5] = points[tmp + 5];

					res[indRes + 6] = points[tmp + 6];
					res[indRes + 7] = points[tmp + 7];
					res[indRes + 8] = points[tmp + 8];
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
		Matrix.translateM(mModelMatrix, 0, 0f, limitHeight, 0f);
		Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);

		this.mModelMatrix = mModelMatrix;
	}

	@Override
	public void draw(float[] mProjectionMatrix,
					 float[] mViewMatrix,
					 float[] mLightPosInEyeSpace,
					 float[] unused) {
		float[] mvpMatrix = new float[16];
		float[] mvMatrix = new float[16];
		Matrix.multiplyMM(mvMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		Matrix.multiplyMM(mvpMatrix, 0, mProjectionMatrix, 0, mvMatrix, 0);

		GLES20.glUseProgram(mProgram);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mDataPackedBufferId);
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false,
				STRIDE, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mDataPackedBufferId);
		GLES20.glEnableVertexAttribArray(mNormalHandle);
		GLES20.glVertexAttribPointer(mNormalHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false,
				STRIDE, POSITION_DATA_SIZE * BYTES_PER_FLOAT);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		GLES20.glUniform4fv(mColorHandle, 1, color, 0);

		GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);

		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

		GLES20.glUniform3fv(mLightPosHandle, 1, mLightPosInEyeSpace, 0);

		GLES20.glUniform1f(mDistanceCoefHandle, distanceCoeff);

		GLES20.glUniform1f(mLightCoefHandle, lightCoeff);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, points.length / 3);

		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}

	@Override
	public boolean collideTest(float[] triangleArray, float[] modelMatrix, Box container) {
		//TODO use container to get restreint area ?
		float[] position = new float[]{0f, 0f, 0f, 1f};
		Matrix.multiplyMV(position, 0, modelMatrix, 0, position.clone(), 0);
		return TriangleCollision.AreCollided(getRestreintArea(
				new float[]{position[0], position[1], position[2]}),
				mModelMatrix.clone(),
				triangleArray,
				modelMatrix);
	}

	@Override
	public boolean isCollided(Item other) {
		return other.collideTest(getRestreintArea(other.clonePosition()), mModelMatrix.clone(), box);
	}

	private void makeBox() {
		box = new Box(-scale,
				limitHeight - coeffHeight * scale,
				-scale,
				scale * 2f,
				2f * coeffHeight * scale,
				scale * 2f);
	}

	@Override
	public boolean isInside(Box otherBox) {
		return box.isInside(otherBox);
	}

	@Override
	public boolean isAlive() {
		return true;
	}

	@Override
	public int getDamage() {
		return Integer.MAX_VALUE - 1;
	}

	@Override
	public void decrementLife(int minus) {

	}

	@Override
	public float[] clonePosition() {
		return new float[]{-0.5f * scale, limitHeight, -0.5f * scale};
	}
}
