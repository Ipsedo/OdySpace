package com.samuelberrien.odyspace.drawable.obj;

import android.content.Context;
import android.opengl.GLES20;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.GLDrawable;
import com.samuelberrien.odyspace.utils.graphics.ShaderLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by samuel on 17/01/17.
 */

public class ObjModelMtl implements GLDrawable {

	private HashMap<String, float[]> mtlAmbColor = new HashMap<>();
	private HashMap<String, float[]> mtlDiffColor = new HashMap<>();
	private HashMap<String, float[]> mtlSpecColor = new HashMap<>();
	private HashMap<String, Float> mtlSpecShininess = new HashMap<>();

	private FloatBuffer vertexBuffer;
	private FloatBuffer normalsBuffer;
	protected FloatBuffer ambColorBuffer;
	protected FloatBuffer diffColorBuffer;
	protected FloatBuffer specColorBuffer;
	private FloatBuffer specShininess;

	private int mProgram;
	private int mPositionHandle;
	private int mNormalHandle;
	private int mAmbColorHandle;
	private int mDiffColorHandle;
	private int mSpecColorHandle;
	private int mSpecShininessHandle;
	private int mCameraPosHandle;
	private int mMVPMatrixHandle;
	private int mLightPosHandle;
	private int mMVMatrixHandle;
	private int mDistanceCoefHandle;
	private int mLightCoefHandle;

	private float lightCoef;
	private float distanceCoef;

	// number of coordinates per vertex in this array
	private final int COORDS_PER_VERTEX = 3;
	protected float[] allCoords;
	protected float[] allNormals;
	private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

	/**
	 * @param context           The application context
	 * @param objResId          The res id of the obj 3D model file
	 * @param mtlResId          The res id of the mtl model file
	 * @param lightAugmentation The light augmentation
	 * @param distanceCoef      The distance attenuation coefficient
	 */
	public ObjModelMtl(Context context, int objResId, int mtlResId, float lightAugmentation, float distanceCoef, boolean randomColor) {

		InputStream inputStream;
		inputStream = context.getResources().openRawResource(mtlResId);
		parseMtl(inputStream);
		inputStream = context.getResources().openRawResource(objResId);
		parseObj(inputStream, randomColor);

		lightCoef = lightAugmentation;
		this.distanceCoef = distanceCoef;

		makeProgram(context, R.raw.specular_vs, R.raw.specular_fs);
	}

	/**
	 * @param context           The application context
	 * @param objFileName       The obj file name in assets folder
	 * @param mtlFileName       The mtl file name in assets folder
	 * @param lightAugmentation The light augmentation
	 * @param distanceCoef      The distance attenuation coefficient
	 */
	public ObjModelMtl(Context context, String objFileName, String mtlFileName, float lightAugmentation, float distanceCoef, boolean randomColor) {

		InputStream inputStream;
		try {
			inputStream = context.getAssets().open(mtlFileName);
			parseMtl(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			inputStream = context.getAssets().open(objFileName);
			parseObj(inputStream, randomColor);
		} catch (IOException e) {
			e.printStackTrace();
		}

		lightCoef = lightAugmentation;
		this.distanceCoef = distanceCoef;

		makeProgram(context, R.raw.specular_vs, R.raw.specular_fs);
	}

	public ObjModelMtl(ObjModelMtl objModelMtl) {
		mtlAmbColor = objModelMtl.mtlAmbColor;
		mtlDiffColor = objModelMtl.mtlDiffColor;
		mtlSpecColor = objModelMtl.mtlSpecColor;

		vertexBuffer = objModelMtl.vertexBuffer;
		normalsBuffer = objModelMtl.normalsBuffer;
		ambColorBuffer = objModelMtl.ambColorBuffer;
		diffColorBuffer = objModelMtl.diffColorBuffer;
		specColorBuffer = objModelMtl.specColorBuffer;
		specShininess = objModelMtl.specShininess;

		mProgram = objModelMtl.mProgram;
		mPositionHandle = objModelMtl.mPositionHandle;
		mNormalHandle = objModelMtl.mNormalHandle;
		mAmbColorHandle = objModelMtl.mAmbColorHandle;
		mDiffColorHandle = objModelMtl.mDiffColorHandle;
		mSpecColorHandle = objModelMtl.mSpecColorHandle;
		mSpecShininessHandle = objModelMtl.mSpecShininessHandle;
		mCameraPosHandle = objModelMtl.mCameraPosHandle;
		mMVPMatrixHandle = objModelMtl.mMVPMatrixHandle;
		mLightPosHandle = objModelMtl.mLightPosHandle;
		mMVMatrixHandle = objModelMtl.mMVMatrixHandle;
		mDistanceCoefHandle = objModelMtl.mDistanceCoefHandle;
		mLightCoefHandle = objModelMtl.mLightCoefHandle;

		lightCoef = objModelMtl.lightCoef;
		distanceCoef = objModelMtl.distanceCoef;

		allCoords = objModelMtl.allCoords;
		allNormals = objModelMtl.allNormals;
	}

	public void makeProgram(Context context, int vertexShaderResId, int fragmentShaderResId) {
		int vertexShader = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShaderLoader.openShader(context, vertexShaderResId));
		int fragmentShader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderLoader.openShader(context, fragmentShaderResId));

		mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(mProgram);

		bind();
	}

	/**
	 * Get shaders var location
	 */
	private void bind() {
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
		mMVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVMatrix");
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
		mAmbColorHandle = GLES20.glGetAttribLocation(mProgram, "a_material_ambient_Color");
		mDiffColorHandle = GLES20.glGetAttribLocation(mProgram, "a_material_diffuse_Color");
		mSpecColorHandle = GLES20.glGetAttribLocation(mProgram, "a_material_specular_Color");
		mLightPosHandle = GLES20.glGetUniformLocation(mProgram, "u_LightPos");
		mNormalHandle = GLES20.glGetAttribLocation(mProgram, "a_Normal");
		mDistanceCoefHandle = GLES20.glGetUniformLocation(mProgram, "u_distance_coef");
		mLightCoefHandle = GLES20.glGetUniformLocation(mProgram, "u_light_coef");
		mCameraPosHandle = GLES20.glGetUniformLocation(mProgram, "u_CameraPosition");
		mSpecShininessHandle = GLES20.glGetAttribLocation(mProgram, "a_material_shininess");
	}

	/**
	 * @param inputStream The input stream of the file
	 */
	private void parseMtl(InputStream inputStream) {
		InputStreamReader inputreader = new InputStreamReader(inputStream);
		BufferedReader buffreader = new BufferedReader(inputreader);
		String line;
		try {
			String currentMtl = "";
			while ((line = buffreader.readLine()) != null) {
				if (line.startsWith("newmtl")) {
					currentMtl = line.split(" ")[1];
				} else if (line.startsWith("Ka")) {
					String[] tmp = line.split(" ");
					mtlAmbColor.put(currentMtl, new float[]{Float.parseFloat(tmp[1]), Float.parseFloat(tmp[2]), Float.parseFloat(tmp[3])});
				} else if (line.startsWith("Kd")) {
					String[] tmp = line.split(" ");
					mtlDiffColor.put(currentMtl, new float[]{Float.parseFloat(tmp[1]), Float.parseFloat(tmp[2]), Float.parseFloat(tmp[3])});
				} else if (line.startsWith("Ks")) {
					String[] tmp = line.split(" ");
					mtlSpecColor.put(currentMtl, new float[]{Float.parseFloat(tmp[1]), Float.parseFloat(tmp[2]), Float.parseFloat(tmp[3])});
				} else if (line.startsWith("Ns")) {
					mtlSpecShininess.put(currentMtl, Float.parseFloat(line.split(" ")[1]));
				}
			}
			buffreader.close();
			inputreader.close();
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param inputStream The input stream of the file
	 */
	private void parseObj(InputStream inputStream, boolean randomColor) {
		InputStreamReader inputreader = new InputStreamReader(inputStream);
		BufferedReader buffreader = new BufferedReader(inputreader);
		String line;

		ArrayList<Float> currVertixsList = new ArrayList<>();
		ArrayList<Float> currNormalsList = new ArrayList<>();
		ArrayList<Integer> currVertexDrawOrderList = new ArrayList<>();
		ArrayList<Integer> currNormalDrawOrderList = new ArrayList<>();
		ArrayList<ArrayList<Integer>> allVertexDrawOrderList = new ArrayList<>();
		ArrayList<ArrayList<Integer>> allNormalDrawOrderList = new ArrayList<>();
		ArrayList<String> mtlToUse = new ArrayList<>();

		int idMtl = 0;

		try {
			while ((line = buffreader.readLine()) != null) {
				if (line.startsWith("usemtl")) {
					mtlToUse.add(line.split(" ")[1]);
					if (idMtl != 0) {
						allVertexDrawOrderList.add(currVertexDrawOrderList);
						allNormalDrawOrderList.add(currNormalDrawOrderList);
					}
					currVertexDrawOrderList = new ArrayList<>();
					currNormalDrawOrderList = new ArrayList<>();
					idMtl++;
				} else if (line.startsWith("vn")) {
					String[] tmp = line.split(" ");
					currNormalsList.add(Float.parseFloat(tmp[1]));
					currNormalsList.add(Float.parseFloat(tmp[2]));
					currNormalsList.add(Float.parseFloat(tmp[3]));
				} else if (line.startsWith("v ")) {
					String[] tmp = line.split(" ");
					currVertixsList.add(Float.parseFloat(tmp[1]));
					currVertixsList.add(Float.parseFloat(tmp[2]));
					currVertixsList.add(Float.parseFloat(tmp[3]));
				} else if (line.startsWith("f")) {
					String[] tmp = line.split(" ");
					currVertexDrawOrderList.add(Integer.parseInt(tmp[1].split("/")[0]));
					currVertexDrawOrderList.add(Integer.parseInt(tmp[2].split("/")[0]));
					currVertexDrawOrderList.add(Integer.parseInt(tmp[3].split("/")[0]));

					currNormalDrawOrderList.add(Integer.parseInt(tmp[1].split("/")[2]));
					currNormalDrawOrderList.add(Integer.parseInt(tmp[2].split("/")[2]));
					currNormalDrawOrderList.add(Integer.parseInt(tmp[3].split("/")[2]));
				}
			}
			buffreader.close();
			inputreader.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		allVertexDrawOrderList.add(currVertexDrawOrderList);
		allNormalDrawOrderList.add(currNormalDrawOrderList);

		ArrayList<Float> coords = new ArrayList<>();
		ArrayList<Float> normals = new ArrayList<>();
		ArrayList<Float> ambColor = new ArrayList<>();
		ArrayList<Float> diffColor = new ArrayList<>();
		ArrayList<Float> specColor = new ArrayList<>();
		ArrayList<Float> specShin = new ArrayList<>();
		Random random = new Random(System.currentTimeMillis());
		for (int i = 0; i < allVertexDrawOrderList.size(); i++) {
			for (int j = 0; j < allVertexDrawOrderList.get(i).size(); j++) {
				coords.add(currVertixsList.get((allVertexDrawOrderList.get(i).get(j) - 1) * 3));
				coords.add(currVertixsList.get((allVertexDrawOrderList.get(i).get(j) - 1) * 3 + 1));
				coords.add(currVertixsList.get((allVertexDrawOrderList.get(i).get(j) - 1) * 3 + 2));
			}

			for (int j = 0; j < allNormalDrawOrderList.get(i).size(); j++) {
				normals.add(currNormalsList.get((allNormalDrawOrderList.get(i).get(j) - 1) * 3));
				normals.add(currNormalsList.get((allNormalDrawOrderList.get(i).get(j) - 1) * 3 + 1));
				normals.add(currNormalsList.get((allNormalDrawOrderList.get(i).get(j) - 1) * 3 + 2));
			}
			float ambRed, ambGreen, ambBlue, diffRed, diffGreen, diffBlue, specRed, specGreen, specBlue;
			if (randomColor) {
				ambRed = random.nextFloat();
				ambGreen = random.nextFloat();
				ambBlue = random.nextFloat();

				diffRed = random.nextFloat();
				diffGreen = random.nextFloat();
				diffBlue = random.nextFloat();

				specRed = random.nextFloat();
				specGreen = random.nextFloat();
				specBlue = random.nextFloat();
			} else {
				ambRed = mtlAmbColor.get(mtlToUse.get(i))[0];
				ambGreen = mtlAmbColor.get(mtlToUse.get(i))[1];
				ambBlue = mtlAmbColor.get(mtlToUse.get(i))[2];

				diffRed = mtlDiffColor.get(mtlToUse.get(i))[0];
				diffGreen = mtlDiffColor.get(mtlToUse.get(i))[1];
				diffBlue = mtlDiffColor.get(mtlToUse.get(i))[2];

				specRed = mtlSpecColor.get(mtlToUse.get(i))[0];
				specGreen = mtlSpecColor.get(mtlToUse.get(i))[1];
				specBlue = mtlSpecColor.get(mtlToUse.get(i))[2];
			}

			for (int j = 0; j < allVertexDrawOrderList.get(i).size() * 4; j += 4) {
				ambColor.add(ambRed);
				ambColor.add(ambGreen);
				ambColor.add(ambBlue);
				ambColor.add(1f);

				diffColor.add(diffRed);
				diffColor.add(diffGreen);
				diffColor.add(diffBlue);
				diffColor.add(1f);

				specColor.add(specRed);
				specColor.add(specGreen);
				specColor.add(specBlue);
				specColor.add(1f);

				specShin.add(mtlSpecShininess.get(mtlToUse.get(i)));
			}
		}

		allCoords = new float[coords.size()];
		for (int i = 0; i < allCoords.length; i++) {
			allCoords[i] = coords.get(i);
		}
		vertexBuffer = ByteBuffer.allocateDirect(allCoords.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		vertexBuffer.put(allCoords)
				.position(0);

		allNormals = new float[normals.size()];
		for (int i = 0; i < allNormals.length; i++) {
			allNormals[i] = normals.get(i);
		}
		normalsBuffer = ByteBuffer.allocateDirect(allNormals.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		normalsBuffer.put(allNormals)
				.position(0);

		float[] allAmbColor = new float[ambColor.size()];
		for (int i = 0; i < allAmbColor.length; i++) {
			allAmbColor[i] = ambColor.get(i);
		}
		ambColorBuffer = ByteBuffer.allocateDirect(allAmbColor.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		ambColorBuffer.put(allAmbColor)
				.position(0);

		float[] allDiffColor = new float[diffColor.size()];
		for (int i = 0; i < allDiffColor.length; i++) {
			allDiffColor[i] = diffColor.get(i);
		}
		diffColorBuffer = ByteBuffer.allocateDirect(allDiffColor.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		diffColorBuffer.put(allDiffColor)
				.position(0);

		float[] allSpecColor = new float[specColor.size()];
		for (int i = 0; i < allSpecColor.length; i++) {
			allSpecColor[i] = specColor.get(i);
		}
		specColorBuffer = ByteBuffer.allocateDirect(allSpecColor.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		specColorBuffer.put(allSpecColor)
				.position(0);

		float[] allSpecShin = new float[specShin.size()];
		for (int i = 0; i < allSpecShin.length; i++) {
			allSpecShin[i] = specShin.get(i);
		}
		specShininess = ByteBuffer.allocateDirect(allSpecShin.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		specShininess.put(allSpecShin)
				.position(0);
	}

	public void setColors(FloatBuffer mAmbColors, FloatBuffer mDiffColors, FloatBuffer mSpecColors) {
		ambColorBuffer = mAmbColors;
		diffColorBuffer = mDiffColors;
		specColorBuffer = mSpecColors;
	}

	@Override
	public void changeColor() {
		FloatBuffer tmp = ambColorBuffer;
		ambColorBuffer = diffColorBuffer;
		diffColorBuffer = tmp;
	}

	@Override
	public void draw(float[] mvpMatrix, float[] mvMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		GLES20.glUseProgram(mProgram);

		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

		GLES20.glEnableVertexAttribArray(mAmbColorHandle);
		GLES20.glVertexAttribPointer(mAmbColorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, ambColorBuffer);

		GLES20.glEnableVertexAttribArray(mDiffColorHandle);
		GLES20.glVertexAttribPointer(mDiffColorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, diffColorBuffer);

		GLES20.glEnableVertexAttribArray(mSpecColorHandle);
		GLES20.glVertexAttribPointer(mSpecColorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, specColorBuffer);

		GLES20.glEnableVertexAttribArray(mNormalHandle);
		GLES20.glVertexAttribPointer(mNormalHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, normalsBuffer);

		GLES20.glEnableVertexAttribArray(mSpecShininessHandle);
		GLES20.glVertexAttribPointer(mSpecShininessHandle, 1, GLES20.GL_FLOAT, false, 1 * 4, specShininess);

		GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);

		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

		GLES20.glUniform3fv(mLightPosHandle, 1, mLightPosInEyeSpace, 0);

		GLES20.glUniform3fv(mCameraPosHandle, 1, mCameraPosition, 0);

		GLES20.glUniform1f(mDistanceCoefHandle, distanceCoef);

		GLES20.glUniform1f(mLightCoefHandle, lightCoef);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, allCoords.length / 3);

		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}
}
