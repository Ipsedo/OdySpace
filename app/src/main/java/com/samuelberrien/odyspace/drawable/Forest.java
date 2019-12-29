package com.samuelberrien.odyspace.drawable;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.maps.Map;
import com.samuelberrien.odyspace.utils.maths.Triangle;

import java.util.Random;

/**
 * Created by samuel on 14/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Forest implements GLDrawable {

	private Context context;

	private ObjModelMtlVBO tree;

	private int nbTree;
	private float[][] mModelsMatrix;

	public Forest(Context context,
				  String treeObjFileName, String treeMtlFileName,
				  int nbTree, Map map, float areaSize) {
		this.context = context;
		tree = new ObjModelMtlVBO(this.context,
				treeObjFileName, treeMtlFileName,
				0.7f, 0f, false);
		this.nbTree = nbTree;
		mModelsMatrix = new float[this.nbTree][16];
		initTrees(map, areaSize);
	}

	private void initTrees(Map map, float areaSize) {
		Random rand = new Random(System.currentTimeMillis());
		for (int i = 0; i < nbTree; i++) {
			float x = rand.nextFloat() * areaSize - areaSize * 0.5f;
			float y;
			float z = rand.nextFloat() * areaSize - areaSize * 0.5f;

			float[] triangles = map.passToModelMatrix(
					map.getRestreintArea(new float[]{x, 0f, z}));
			float moy = Triangle.CalcY(
					new float[]{triangles[0], triangles[1], triangles[2]},
					new float[]{triangles[3], triangles[4], triangles[5]},
					new float[]{triangles[6], triangles[7], triangles[8]},
					x, z) / 2f;
			moy += Triangle.CalcY(
					new float[]{triangles[9], triangles[10], triangles[11]},
					new float[]{triangles[12], triangles[13], triangles[14]},
					new float[]{triangles[15], triangles[16], triangles[17]},
					x, z) / 2f;

			y = moy;

			double angle = rand.nextDouble() * 360f;

			float[] mModelMatrix = new float[16];
			Matrix.setIdentityM(mModelMatrix, 0);
			Matrix.translateM(mModelMatrix, 0, x, y, z);

			float[] mRotMatrix = new float[16];
			Matrix.setRotateM(mRotMatrix, 0, (float) angle, 0f, 1f, 0f);

			Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix.clone(), 0, mRotMatrix, 0);

			mModelsMatrix[i] = mModelMatrix.clone();
		}
	}

	@Override
	public void draw(float[] mProjectionMatrix,
					 float[] mViewMatrix,
					 float[] mLightPosInEyeSpace,
					 float[] mCameraPosition) {
		float[] tmpMVMatrix = new float[16];
		float[] tmpMVPMatrix = new float[16];
		for (int i = 0; i < nbTree; i++) {
			Matrix.multiplyMM(tmpMVMatrix, 0, mViewMatrix, 0, mModelsMatrix[i], 0);
			Matrix.multiplyMM(tmpMVPMatrix, 0, mProjectionMatrix, 0, tmpMVMatrix, 0);
			tree.draw(tmpMVPMatrix, tmpMVMatrix, mLightPosInEyeSpace, mCameraPosition);
		}
	}
}
