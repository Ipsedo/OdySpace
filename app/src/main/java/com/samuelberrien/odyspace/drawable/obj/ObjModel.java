package com.samuelberrien.odyspace.drawable.obj;

import android.content.Context;
import android.opengl.GLES20;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.GLItemDrawable;
import com.samuelberrien.odyspace.utils.graphics.ShaderLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by samuel on 05/01/17.
 */

public class ObjModel implements GLItemDrawable {

	private FloatBuffer vertexBuffer;
	private FloatBuffer normalsBuffer;
	private FloatBuffer colorBuffer;

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

	private float lightCoef;
	private float distanceCoef;
	private float ambColorCoef;

	// number of coordinates per vertex in this array
	static final int COORDS_PER_VERTEX = 3;
	private float[] coords;
	private float[] normal;
	private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

	private float[] color;

	/**
	 * @param context           the application context
	 * @param resId             the res id of the obj file
	 * @param red               the red color of the object
	 * @param green             the green color of the object
	 * @param blue              the blue color of the object
	 * @param lightAugmentation the light augmentation of the object
	 */
	public ObjModel(Context context,
					int resId,
					float red,
					float green,
					float blue,
					float lightAugmentation, float distanceCoef, float ambColorCoef) {

		lightCoef = lightAugmentation;
		this.distanceCoef = distanceCoef;
		this.ambColorCoef = ambColorCoef;

		InputStream inputStream = context.getResources().openRawResource(resId);
		InputStreamReader inputreader = new InputStreamReader(inputStream);
		parseObj(inputreader, red, green, blue);
		try {
			inputreader.close();
			inputStream.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}


		int vertexShader = ShaderLoader.loadShader(
				GLES20.GL_VERTEX_SHADER,
				ShaderLoader.openShader(context, R.raw.diffuse_vs));
		int fragmentShader = ShaderLoader.loadShader(
				GLES20.GL_FRAGMENT_SHADER,
				ShaderLoader.openShader(context, R.raw.diffuse_fs));

		mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(mProgram);

		bind();
	}

	public ObjModel(Context context,
					String fileName,
					float red,
					float green,
					float blue,
					float lightAugmentation, float distanceCoef, float ambColorCoef) {

		lightCoef = lightAugmentation;
		this.distanceCoef = distanceCoef;
		this.ambColorCoef = ambColorCoef;

		try {
			InputStream inputStream = context.getAssets().open(fileName);
			InputStreamReader inputreader = new InputStreamReader(inputStream);
			parseObj(inputreader, red, green, blue);
			inputreader.close();
			inputStream.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		int vertexShader = ShaderLoader.loadShader(
				GLES20.GL_VERTEX_SHADER,
				ShaderLoader.openShader(context, R.raw.diffuse_vs));
		int fragmentShader = ShaderLoader.loadShader(
				GLES20.GL_FRAGMENT_SHADER,
				ShaderLoader.openShader(context, R.raw.diffuse_fs));

		mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(mProgram);

		bind();
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

	private void parseObj(InputStreamReader inputreader, float red, float green, float blue) {
		BufferedReader buffreader1 = new BufferedReader(inputreader);
		String line;

		ArrayList<Float> vertixsList = new ArrayList<>();
		ArrayList<Float> normalsList = new ArrayList<>();
		ArrayList<Integer> vertexDrawOrderList = new ArrayList<>();
		ArrayList<Integer> normalDrawOrderList = new ArrayList<>();

		try {
			while ((line = buffreader1.readLine()) != null) {
				if (line.startsWith("vn")) {
					String[] tmp = line.split(" ");
					normalsList.add(Float.parseFloat(tmp[1]));
					normalsList.add(Float.parseFloat(tmp[2]));
					normalsList.add(Float.parseFloat(tmp[3]));
				} else if (line.startsWith("v ")) {
					String[] tmp = line.split(" ");
					vertixsList.add(Float.parseFloat(tmp[1]));
					vertixsList.add(Float.parseFloat(tmp[2]));
					vertixsList.add(Float.parseFloat(tmp[3]));
				} else if (line.startsWith("f")) {
					String[] tmp = line.split(" ");
					vertexDrawOrderList.add(Integer.parseInt(tmp[1].split("/")[0]));
					vertexDrawOrderList.add(Integer.parseInt(tmp[2].split("/")[0]));
					vertexDrawOrderList.add(Integer.parseInt(tmp[3].split("/")[0]));

					normalDrawOrderList.add(Integer.parseInt(tmp[1].split("/")[2]));
					normalDrawOrderList.add(Integer.parseInt(tmp[2].split("/")[2]));
					normalDrawOrderList.add(Integer.parseInt(tmp[3].split("/")[2]));
				}
			}

			buffreader1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		coords = new float[3 * vertexDrawOrderList.size()];
		for (int i = 0; i < vertexDrawOrderList.size(); i++) {
			coords[i * 3] = vertixsList.get((vertexDrawOrderList.get(i) - 1) * 3);
			coords[i * 3 + 1] = vertixsList.get((vertexDrawOrderList.get(i) - 1) * 3 + 1);
			coords[i * 3 + 2] = vertixsList.get((vertexDrawOrderList.get(i) - 1) * 3 + 2);
		}

		normal = new float[coords.length];
		for (int i = 0; i < normalDrawOrderList.size(); i++) {
			normal[i * 3] = normalsList.get((normalDrawOrderList.get(i) - 1) * 3);
			normal[i * 3 + 1] = normalsList.get((normalDrawOrderList.get(i) - 1) * 3 + 1);
			normal[i * 3 + 2] = normalsList.get((normalDrawOrderList.get(i) - 1) * 3 + 2);
		}

		color = new float[coords.length * 4 / 3];
		for (int i = 0; i < color.length; i += 4) {
			color[i] = red;
			color[i + 1] = green;
			color[i + 2] = blue;
			color[i + 3] = 1f;
		}

		vertexBuffer = ByteBuffer.allocateDirect(coords.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		vertexBuffer.put(coords)
				.position(0);

		normalsBuffer = ByteBuffer.allocateDirect(normal.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		normalsBuffer.put(normal)
				.position(0);

		colorBuffer = ByteBuffer.allocateDirect(color.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		colorBuffer.put(color)
				.position(0);
	}

	/**
	 * @param colorBuffer a color buffer that will be used to draw the obj 3D model
	 */
	public void setColor(FloatBuffer colorBuffer) {
		this.colorBuffer = colorBuffer;
	}

	/**
	 * @return the vertex draw list length of the obj 3D model
	 */
	public int getVertexDrawListLength() {
		return coords.length;
	}

	/**
	 * Make a FloatBuffer containing RGBA value for all vertex with the give color
	 *
	 * @param color A 4 float array RGBA color
	 * @return A new color FloatBuffer for the ObjModel instance
	 */
	public FloatBuffer makeColor(float[] color) {
		float[] tmp = new float[coords.length * 4 / 3];
		for (int i = 0; i < tmp.length; i += 4) {
			tmp[i] = color[0];
			tmp[i + 1] = color[1];
			tmp[i + 2] = color[2];
			tmp[i + 3] = color[3];
		}

		return (FloatBuffer) ByteBuffer.allocateDirect(tmp.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer().put(tmp)
				.position(0);
	}

	@Override
	public void changeColor() {
		throw new RuntimeException("Not implemented yet !");
	}

	/**
	 * @param mvpMatrix           The Model View Project matrix in which to draw this shape.
	 * @param mvMatrix            The Model View matrix
	 * @param mLightPosInEyeSpace The light position in the eye space
	 */
	public void draw(float[] mvpMatrix,
					 float[] mvMatrix,
					 float[] mLightPosInEyeSpace,
					 float[] unused) {
		// Add program to OpenGL environment
		GLES20.glUseProgram(mProgram);

		vertexBuffer.position(0);
		colorBuffer.position(0);
		normalsBuffer.position(0);

		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle,
				COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

		GLES20.glEnableVertexAttribArray(mColorHandle);
		GLES20.glVertexAttribPointer(mColorHandle,
				4, GLES20.GL_FLOAT, false, 4 * 4, colorBuffer);

		GLES20.glEnableVertexAttribArray(mNormalHandle);
		GLES20.glVertexAttribPointer(mNormalHandle,
				3, GLES20.GL_FLOAT, false, 3 * 4, normalsBuffer);

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

		// get handle to shape's transformation matrix
		GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);

		GLES20.glUniform3fv(mLightPosHandle, 1, mLightPosInEyeSpace, 0);

		GLES20.glUniform1f(mDistanceCoefHandle, distanceCoef);

		GLES20.glUniform1f(mLightCoefHandle, lightCoef);

		GLES20.glUniform1f(mAmbColorCoefHandle, ambColorCoef);

		// Draw the polygon
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, coords.length / 3);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
		GLES20.glDisableVertexAttribArray(mColorHandle);
		GLES20.glDisableVertexAttribArray(mNormalHandle);
	}
}
