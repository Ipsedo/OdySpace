package com.samuelberrien.odyspace.objects.tunnel;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.core.collision.TriangleCollision;
import com.samuelberrien.odyspace.drawable.GLDrawable;
import com.samuelberrien.odyspace.core.collision.Box;
import com.samuelberrien.odyspace.core.Item;
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

	/*private native boolean areCollided(float[] mPointItem1, float[] mModelMatrix1, float[] mPointItem2, float[] mModelMatrix2);

	static {
		System.loadLibrary("collision");
	}*/

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

	private float[] identityMatrix;

	private static final int COORDS_PER_VERTEX = 3;
	private final int vertexStride = COORDS_PER_VERTEX * 4;

	private Box box;

	public Stretch(Context context, float[] mCircle1ModelMatrix, float[] mCircle2ModelMatrix, int nbPointsCircle, float[] color, float distanceCoef, float lightCoef, float colorCoef) {
		this.mCircle1ModelMatrix = mCircle1ModelMatrix.clone();
		this.mCircle2ModelMatrix = mCircle2ModelMatrix.clone();

		this.nbPointsCircle = nbPointsCircle;

		this.color = color.clone();

		this.distanceCoef = distanceCoef;
		this.lightCoef = lightCoef;
		this.colorCoef = colorCoef;

		makeCircleOriented();
		makeTriangleStretch();
		makeBoundingBox();
		identityMatrix = new float[16];
		Matrix.setIdentityM(identityMatrix, 0);

		int vertexShader = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShaderLoader.openShader(context, R.raw.diffuse_vs));
		int fragmentShader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderLoader.openShader(context, R.raw.diffuse_fs));

		mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(mProgram);

		bind();
		bindBuffer();
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
		circle1 = new float[3 * nbPointsCircle];
		circle2 = new float[3 * nbPointsCircle];

		double pas = Math.PI * 2 / (double) nbPointsCircle;

		for (int i = 0; i < nbPointsCircle; i++) {
			float[] tmp = new float[]{(float) Math.cos(pas * (double) i), (float) Math.sin(pas * (double) i), 0f, 1f};
			Matrix.multiplyMV(tmp, 0, mCircle1ModelMatrix, 0, tmp.clone(), 0);
			circle1[i * 3 + 0] = tmp[0];
			circle1[i * 3 + 1] = tmp[1];
			circle1[i * 3 + 2] = tmp[2];

			tmp = new float[]{(float) Math.cos(pas * (double) i), (float) Math.sin(pas * (double) i), 0f, 1f};
			Matrix.multiplyMV(tmp, 0, mCircle2ModelMatrix, 0, tmp.clone(), 0);
			circle2[i * 3 + 0] = tmp[0];
			circle2[i * 3 + 1] = tmp[1];
			circle2[i * 3 + 2] = tmp[2];
		}
	}

	private void makeTriangleStretch() {
		ArrayList<Float> vertex = new ArrayList<>();
		ArrayList<Float> normals = new ArrayList<>();
		for (int i = 0; i < circle1.length - 3; i += 3) {
			vertex.add(circle1[i]);
			vertex.add(circle1[i + 1]);
			vertex.add(circle1[i + 2]);

			vertex.add(circle2[i]);
			vertex.add(circle2[i + 1]);
			vertex.add(circle2[i + 2]);

			vertex.add(circle1[i + 3]);
			vertex.add(circle1[i + 4]);
			vertex.add(circle1[i + 5]);

			float[] tmpN = Vector.cross3f(
					Vector.normalize3f(new float[]{circle2[i] - circle1[i], circle2[i + 1] - circle1[i + 1], circle2[i + 2] - circle1[i + 2]}),
					Vector.normalize3f(new float[]{circle1[i + 3] - circle1[i], circle1[i + 4] - circle1[i + 1], circle1[i + 5] - circle1[i + 2]}));

			normals.add(tmpN[0]);
			normals.add(tmpN[1]);
			normals.add(tmpN[2]);

			normals.add(tmpN[0]);
			normals.add(tmpN[1]);
			normals.add(tmpN[2]);

			normals.add(tmpN[0]);
			normals.add(tmpN[1]);
			normals.add(tmpN[2]);


			vertex.add(circle1[i + 3]);
			vertex.add(circle1[i + 4]);
			vertex.add(circle1[i + 5]);

			vertex.add(circle2[i]);
			vertex.add(circle2[i + 1]);
			vertex.add(circle2[i + 2]);

			vertex.add(circle2[i + 3]);
			vertex.add(circle2[i + 4]);
			vertex.add(circle2[i + 5]);

			tmpN = Vector.cross3f(
					Vector.normalize3f(new float[]{circle1[i + 3] - circle2[i + 3], circle1[i + 4] - circle2[i + 4], circle1[i + 5] - circle2[i + 5]}),
					Vector.normalize3f(new float[]{circle2[i] - circle2[i + 3], circle2[i + 1] - circle2[i + 4], circle2[i + 2] - circle2[i + 5]}));

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

		vertex.add(circle1[circle1.length - 3]);
		vertex.add(circle1[circle1.length - 2]);
		vertex.add(circle1[circle1.length - 1]);

		vertex.add(circle2[circle1.length - 3]);
		vertex.add(circle2[circle1.length - 2]);
		vertex.add(circle2[circle1.length - 1]);

		vertex.add(circle1[0]);
		vertex.add(circle1[1]);
		vertex.add(circle1[2]);

		float[] tmpN = Vector.cross3f(
				Vector.normalize3f(new float[]{circle2[circle1.length - 3] - circle1[circle1.length - 3], circle2[circle1.length - 2] - circle1[circle1.length - 2], circle2[circle1.length - 1] - circle1[circle1.length - 1]}),
				Vector.normalize3f(new float[]{circle1[0] - circle1[circle1.length - 3], circle1[1] - circle1[circle1.length - 1], circle1[2] - circle1[circle1.length - 1]}));

		normals.add(tmpN[0]);
		normals.add(tmpN[1]);
		normals.add(tmpN[2]);

		normals.add(tmpN[0]);
		normals.add(tmpN[1]);
		normals.add(tmpN[2]);

		normals.add(tmpN[0]);
		normals.add(tmpN[1]);
		normals.add(tmpN[2]);


		vertex.add(circle1[0]);
		vertex.add(circle1[1]);
		vertex.add(circle1[2]);

		vertex.add(circle2[circle1.length - 3]);
		vertex.add(circle2[circle1.length - 2]);
		vertex.add(circle2[circle1.length - 1]);

		vertex.add(circle2[0]);
		vertex.add(circle2[1]);
		vertex.add(circle2[2]);

		tmpN = Vector.cross3f(
				Vector.normalize3f(new float[]{circle1[0] - circle2[0], circle1[1] - circle2[1], circle1[2] - circle2[2]}),
				Vector.normalize3f(new float[]{circle2[circle1.length - 3] - circle2[0], circle2[circle1.length - 2] - circle2[1], circle2[circle1.length - 1] - circle2[2]}));

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
		for (int i = 0; i < this.vertex.length; i++)
			this.vertex[i] = vertex.get(i);

		for (int i = 0; i < this.normals.length; i++)
			this.normals[i] = normals.get(i);


		vertexBuffer = ByteBuffer.allocateDirect(this.vertex.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		vertexBuffer.put(this.vertex)
				.position(0);

		normalsBuffer = ByteBuffer.allocateDirect(this.normals.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		normalsBuffer.put(this.normals)
				.position(0);

		colorBuffer = ByteBuffer.allocateDirect(color.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		colorBuffer.put(color)
				.position(0);
	}

	private void bindBuffer() {
		final int buffers[] = new int[3];
		GLES20.glGenBuffers(3, buffers, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * BYTES_PER_FLOAT, vertexBuffer, GLES20.GL_STATIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, normalsBuffer.capacity() * BYTES_PER_FLOAT, normalsBuffer, GLES20.GL_STATIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, colorBuffer.capacity() * BYTES_PER_FLOAT, colorBuffer, GLES20.GL_STATIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		vertexBufferId = buffers[0];
		normalsBufferId = buffers[1];
		ambBufferId = buffers[2];

		vertexBuffer.limit(0);
		vertexBuffer = null;
		normalsBuffer.limit(0);
		normalsBuffer = null;
		colorBuffer.limit(0);
		colorBuffer = null;
	}

	private void makeBoundingBox() {
		float maxX = Float.MIN_VALUE;
		float maxY = Float.MIN_VALUE;
		float maxZ = Float.MIN_VALUE;

		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float minZ = Float.MAX_VALUE;

		for (int i = 0; i < circle1.length; i += 3) {
			if (maxX < circle1[i]) {
				maxX = circle1[i];
			}
			if (maxX < circle2[i]) {
				maxX = circle2[i];
			}
			if (minX > circle1[i]) {
				minX = circle1[i];
			}
			if (minX > circle2[i]) {
				minX = circle2[i];
			}

			if (maxY < circle1[i + 1]) {
				maxY = circle1[i + 1];
			}
			if (maxY < circle2[i + 1]) {
				maxY = circle2[i + 1];
			}
			if (minY > circle1[i + 1]) {
				minY = circle1[i + 1];
			}
			if (minY > circle2[i + 1]) {
				minY = circle2[i + 1];
			}

			if (maxZ < circle1[i + 2]) {
				maxZ = circle1[i + 2];
			}
			if (maxZ < circle2[i + 2]) {
				maxZ = circle2[i + 2];
			}
			if (minZ > circle1[i + 2]) {
				minZ = circle1[i + 2];
			}
			if (minZ > circle2[i + 2]) {
				minZ = circle2[i + 2];
			}
		}
		box = new Box(minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ);
	}

	public Box getBox() {
		return box;
	}

	@Override
	public boolean collideTest(float[] triangleArray, float[] modelMatrix, Box unused) {
		return TriangleCollision.AreCollided(vertex.clone(), identityMatrix.clone(), triangleArray, modelMatrix);
	}

	@Override
	public boolean isCollided(Item other) {
		return other.collideTest(vertex.clone(), identityMatrix.clone(), getBox());
	}

	@Override
	public boolean isInside(Box box) {
		return box.isInside(box);
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
		return box.getPos();
	}

	@Override
	public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		float[] mMVPMatrix = new float[16];
		float[] mMVMatrix = new float[16];
		Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix.clone(), 0, identityMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix.clone(), 0, mMVMatrix, 0);


		GLES20.glUseProgram(mProgram);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferId);
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);

		// Pass in the normal information
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, normalsBufferId);
		GLES20.glEnableVertexAttribArray(mNormalHandle);
		GLES20.glVertexAttribPointer(mNormalHandle, NORMAL_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);

		// Pass in the texture information
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, ambBufferId);
		GLES20.glEnableVertexAttribArray(mColorHandle);
		GLES20.glVertexAttribPointer(mColorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

		// get handle to shape's transformation matrix
		GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVMatrix, 0);

		GLES20.glUniform3fv(mLightPosHandle, 1, mLightPosInEyeSpace, 0);

		GLES20.glUniform1f(mDistanceCoefHandle, distanceCoef);

		GLES20.glUniform1f(mLightCoefHandle, lightCoef);

		GLES20.glUniform1f(mAmbColorCoefHandle, colorCoef);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertex.length / 3);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
		GLES20.glDisableVertexAttribArray(mColorHandle);
		GLES20.glDisableVertexAttribArray(mNormalHandle);
	}
}
