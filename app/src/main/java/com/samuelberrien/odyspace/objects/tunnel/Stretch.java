package com.samuelberrien.odyspace.objects.tunnel;

import android.content.Context;
import android.media.MediaActionSound;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.GLDrawable;
import com.samuelberrien.odyspace.utils.collision.Box;
import com.samuelberrien.odyspace.utils.game.Item;
import com.samuelberrien.odyspace.utils.graphics.ShaderLoader;
import com.samuelberrien.odyspace.utils.maths.Vector;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by samuel on 27/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Stretch implements Item, GLDrawable {

	/**
	 * Size of the position data in elements.
	 */
	private static final int POSITION_DATA_SIZE = 3;

	/**
	 * Size of the normal data in elements.
	 */
	private static final int NORMAL_DATA_SIZE = 3;

	private static final int COLOR_DATA_SIZE = 4;
	/**
	 * How many bytes per float.
	 */
	private static final int BYTES_PER_FLOAT = 4;

	private float[] mCircle1ModelMatrix;
	private float[] mCircle2ModelMatrix;

	private float[] circle1;
	private float[] circle2;

	private int nbPointsCircle;

	private float[] vertex;
	private float[] normals;
	private float[] color;

	private float distanceCoef;
	private float lightCoef;
	private float colorCoef;

	private FloatBuffer vertexBuffer;
	private FloatBuffer normalsBuffer;
	private FloatBuffer colorBuffer;

	private int vertexBufferId;
	private int normalsBufferId;
	private int ambBufferId;

	private final int mProgram;
	private int mPositionHandle;
	private int mNormalHandle;
	private int mColorHandle;
	private int mMVPMatrixHandle;
	private int mLightPosHandle;
	private int mMVMatrixHandle;
	private int mDistanceCoefHandle;
	private int mLightCoefHandle;
	private int mAmbColorCoefHandle;

	static final int COORDS_PER_VERTEX = 3;
	private final int vertexStride = COORDS_PER_VERTEX * 4;

	public Stretch(Context context, float[] mCircle1ModelMatrix, float[] mCircle2ModelMatrix, int nbPointsCircle, float[] color, float distanceCoef, float lightCoef, float colorCoef) {
		this.mCircle1ModelMatrix = mCircle1ModelMatrix.clone();
		this.mCircle2ModelMatrix = mCircle2ModelMatrix.clone();

		this.nbPointsCircle = nbPointsCircle;

		this.color = color.clone();

		this.distanceCoef = distanceCoef;
		this.lightCoef = lightCoef;
		this.colorCoef = colorCoef;

		this.makeCircleOriented();
		this.makeTriangleStretch();

		int vertexShader = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShaderLoader.openShader(context, R.raw.diffuse_vs));
		int fragmentShader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderLoader.openShader(context, R.raw.diffuse_fs));

		this.mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
		GLES20.glAttachShader(this.mProgram, vertexShader);   // add the vertex shader to program
		GLES20.glAttachShader(this.mProgram, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(this.mProgram);

		this.bind();
		this.bindBuffer();
	}

	private void bind() {
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
		mMVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVMatrix");
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
		mColorHandle = GLES20.glGetAttribLocation(mProgram, "a_Color");
		mLightPosHandle = GLES20.glGetUniformLocation(mProgram, "u_LightPos");
		mDistanceCoefHandle = GLES20.glGetUniformLocation(mProgram, "u_distance_coef");
		mLightCoefHandle = GLES20.glGetUniformLocation(mProgram, "u_light_coef");
		mNormalHandle = GLES20.glGetAttribLocation(mProgram, "a_Normal");
		mAmbColorCoefHandle = GLES20.glGetUniformLocation(mProgram, "u_amb_Color_coef");
	}

	private void makeCircleOriented() {
		this.circle1 = new float[3 * this.nbPointsCircle];
		this.circle2 = new float[3 * this.nbPointsCircle];

		double pas = Math.PI * 2 / (double) this.nbPointsCircle;

		for (int i = 0; i < this.nbPointsCircle; i++) {
			float[] tmp = new float[]{(float) Math.cos(pas * (double) i), (float) Math.sin(pas * (double) i), 0f, 1f};
			Matrix.multiplyMV(tmp, 0, this.mCircle1ModelMatrix, 0, tmp.clone(), 0);
			this.circle1[i * 3 + 0] = tmp[0];
			this.circle1[i * 3 + 1] = tmp[1];
			this.circle1[i * 3 + 2] = tmp[2];

			tmp = new float[]{(float) Math.cos(pas * (double) i), (float) Math.sin(pas * (double) i), 0f, 1f};
			Matrix.multiplyMV(tmp, 0, this.mCircle2ModelMatrix, 0, tmp.clone(), 0);
			this.circle2[i * 3 + 0] = tmp[0];
			this.circle2[i * 3 + 1] = tmp[1];
			this.circle2[i * 3 + 2] = tmp[2];
		}
	}

	private void makeTriangleStretch() {
		ArrayList<Float> vertex = new ArrayList<>();
		ArrayList<Float> normals = new ArrayList<>();
		for (int i = 0; i < this.circle1.length - 3; i += 3) {
			vertex.add(this.circle1[i]);
			vertex.add(this.circle1[i + 1]);
			vertex.add(this.circle1[i + 2]);

			vertex.add(this.circle2[i]);
			vertex.add(this.circle2[i + 1]);
			vertex.add(this.circle2[i + 2]);

			vertex.add(this.circle1[i + 3]);
			vertex.add(this.circle1[i + 4]);
			vertex.add(this.circle1[i + 5]);

			float[] tmpN = Vector.cross3f(
					Vector.normalize3f(new float[]{this.circle2[i] - this.circle1[i], this.circle2[i + 1] - this.circle1[i + 1], this.circle2[i + 2] - this.circle1[i + 2]}),
					Vector.normalize3f(new float[]{this.circle1[i + 3] - this.circle1[i], this.circle1[i + 4] - this.circle1[i + 1], this.circle1[i + 5] - this.circle1[i + 2]}));

			normals.add(tmpN[0]);
			normals.add(tmpN[1]);
			normals.add(tmpN[2]);

			normals.add(tmpN[0]);
			normals.add(tmpN[1]);
			normals.add(tmpN[2]);

			normals.add(tmpN[0]);
			normals.add(tmpN[1]);
			normals.add(tmpN[2]);


			vertex.add(this.circle1[i + 3]);
			vertex.add(this.circle1[i + 4]);
			vertex.add(this.circle1[i + 5]);

			vertex.add(this.circle2[i]);
			vertex.add(this.circle2[i + 1]);
			vertex.add(this.circle2[i + 2]);

			vertex.add(this.circle2[i + 3]);
			vertex.add(this.circle2[i + 4]);
			vertex.add(this.circle2[i + 5]);

			tmpN = Vector.cross3f(
					Vector.normalize3f(new float[]{this.circle2[i] - this.circle1[i + 3], this.circle2[i + 1] - this.circle1[i + 4], this.circle2[i + 2] - this.circle1[i + 5]}),
					Vector.normalize3f(new float[]{this.circle2[i + 3] - this.circle1[i + 3], this.circle2[i + 4] - this.circle1[i + 4], this.circle2[i + 5] - this.circle1[i + 5]}));

			normals.add(tmpN[0]);
			normals.add(tmpN[1]);
			normals.add(tmpN[2]);

			normals.add(tmpN[0]);
			normals.add(tmpN[1]);
			normals.add(tmpN[2]);

			normals.add(tmpN[0]);
			normals.add(tmpN[1]);
			normals.add(tmpN[2]);

		}

		vertex.add(this.circle1[this.circle1.length - 3]);
		vertex.add(this.circle1[this.circle1.length - 2]);
		vertex.add(this.circle1[this.circle1.length - 1]);

		vertex.add(this.circle2[this.circle1.length - 3]);
		vertex.add(this.circle2[this.circle1.length - 2]);
		vertex.add(this.circle2[this.circle1.length - 1]);

		vertex.add(this.circle1[0]);
		vertex.add(this.circle1[1]);
		vertex.add(this.circle1[2]);

		float[] tmpN = Vector.cross3f(
				Vector.normalize3f(new float[]{this.circle2[this.circle1.length - 3] - this.circle1[this.circle1.length - 3], this.circle2[this.circle1.length - 2] - this.circle1[this.circle1.length - 2], this.circle2[this.circle1.length - 1] - this.circle1[this.circle1.length - 1]}),
				Vector.normalize3f(new float[]{this.circle1[0] - this.circle1[this.circle1.length - 3], this.circle1[1] - this.circle1[this.circle1.length - 1], this.circle1[2] - this.circle1[this.circle1.length - 1]}));

		normals.add(tmpN[0]);
		normals.add(tmpN[1]);
		normals.add(tmpN[2]);

		normals.add(tmpN[0]);
		normals.add(tmpN[1]);
		normals.add(tmpN[2]);

		normals.add(tmpN[0]);
		normals.add(tmpN[1]);
		normals.add(tmpN[2]);


		vertex.add(this.circle1[0]);
		vertex.add(this.circle1[1]);
		vertex.add(this.circle1[2]);

		vertex.add(this.circle2[this.circle1.length - 3]);
		vertex.add(this.circle2[this.circle1.length - 2]);
		vertex.add(this.circle2[this.circle1.length - 1]);

		vertex.add(this.circle2[0]);
		vertex.add(this.circle2[1]);
		vertex.add(this.circle2[2]);

		tmpN = Vector.cross3f(
				Vector.normalize3f(new float[]{this.circle2[this.circle1.length - 3] - this.circle1[0], this.circle2[this.circle1.length - 2] - this.circle1[1], this.circle2[this.circle1.length - 1] - this.circle1[2]}),
				Vector.normalize3f(new float[]{this.circle2[0] - this.circle1[0], this.circle2[1] - this.circle1[1], this.circle2[2] - this.circle1[2]}));

		normals.add(tmpN[0]);
		normals.add(tmpN[1]);
		normals.add(tmpN[2]);

		normals.add(tmpN[0]);
		normals.add(tmpN[1]);
		normals.add(tmpN[2]);

		normals.add(tmpN[0]);
		normals.add(tmpN[1]);
		normals.add(tmpN[2]);

		this.vertex = new float[vertex.size()];
		this.normals = new float[normals.size()];
		float[] color = new float[vertex.size() * 4 / 3];
		for (int i = 0; i < color.length; i += 4) {
			color[i] = this.color[0];
			color[i + 1] = this.color[1];
			color[i + 2] = this.color[2];
			color[i + 3] = this.color[3];
		}
		for (int i = 0; i < this.vertex.length; i++) {
			this.vertex[i] = vertex.get(i);
			this.normals[i] = normals.get(i);
		}

		this.vertexBuffer = ByteBuffer.allocateDirect(this.vertex.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		this.vertexBuffer.put(this.vertex)
				.position(0);

		this.normalsBuffer = ByteBuffer.allocateDirect(this.normals.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		this.normalsBuffer.put(this.normals)
				.position(0);

		this.colorBuffer = ByteBuffer.allocateDirect(color.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		this.colorBuffer.put(color)
				.position(0);
	}

	private void bindBuffer() {
		final int buffers[] = new int[3];
		GLES20.glGenBuffers(3, buffers, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, this.vertexBuffer.capacity() * BYTES_PER_FLOAT, this.vertexBuffer, GLES20.GL_STATIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, this.normalsBuffer.capacity() * BYTES_PER_FLOAT, this.normalsBuffer, GLES20.GL_STATIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, this.colorBuffer.capacity() * BYTES_PER_FLOAT, this.colorBuffer, GLES20.GL_STATIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		this.vertexBufferId = buffers[0];
		this.normalsBufferId = buffers[1];
		this.ambBufferId = buffers[2];

		this.vertexBuffer.limit(0);
		this.vertexBuffer = null;
		this.normalsBuffer.limit(0);
		this.normalsBuffer = null;
		this.colorBuffer.limit(0);
		this.colorBuffer = null;
	}


	@Override
	public boolean collideTest(float[] triangleArray, float[] modelMatrix) {
		return false;
	}

	@Override
	public boolean isCollided(Item other) {
		return false;
	}

	@Override
	public boolean isInside(Box Box) {
		return false;
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
		return new float[0];
	}

	@Override
	public void draw(float[] mvpMatrix, float[] mvMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		float[] identity = new float[16];
		Matrix.setIdentityM(identity, 0);
		Matrix.multiplyMM(mvMatrix, 0, mvMatrix.clone(), 0, identity, 0);
		Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix.clone(), 0, mvMatrix, 0);



		GLES20.glUseProgram(mProgram);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.vertexBufferId);
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);

		// Pass in the normal information
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.normalsBufferId);
		GLES20.glEnableVertexAttribArray(mNormalHandle);
		GLES20.glVertexAttribPointer(mNormalHandle, NORMAL_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);

		// Pass in the texture information
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.ambBufferId);
		GLES20.glEnableVertexAttribArray(this.mColorHandle);
		GLES20.glVertexAttribPointer(this.mColorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

		// get handle to shape's transformation matrix
		GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);

		GLES20.glUniform3fv(mLightPosHandle, 1, mLightPosInEyeSpace, 0);

		GLES20.glUniform1f(mDistanceCoefHandle, this.distanceCoef);

		GLES20.glUniform1f(mLightCoefHandle, this.lightCoef);

		GLES20.glUniform1f(mAmbColorCoefHandle, this.colorCoef);

		// Draw the polygon
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, this.vertex.length / 3);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
		GLES20.glDisableVertexAttribArray(mColorHandle);
		GLES20.glDisableVertexAttribArray(mNormalHandle);
	}
}
