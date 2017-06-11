package com.samuelberrien.odyspace.drawable;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.MainActivity;
import com.samuelberrien.odyspace.drawable.maps.Map;
import com.samuelberrien.odyspace.drawable.maps.NoiseMap;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtl;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.utils.game.Fire;

import java.util.Random;

/**
 * Created by samuel on 14/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Forest {

    private Context context;

    private ObjModelMtlVBO tree;

    private int nbTree;
    private float[][] mModelsMatrix;

    public Forest(Context context, String treeObjFileName, String treeMtlFileName, int nbTree, Map map, float areaSize) {
        this.context = context;
        this.tree = new ObjModelMtlVBO(this.context, treeObjFileName, treeMtlFileName, 0.7f, 0f, false);
        this.nbTree = nbTree;
        this.mModelsMatrix = new float[this.nbTree][16];
        this.initTrees(map, areaSize);
    }

    private void initTrees(Map map, float areaSize) {
        Random rand = new Random(System.currentTimeMillis());
        for(int i = 0; i < this.nbTree; i++) {
            float x = rand.nextFloat() * areaSize - areaSize * 0.5f;
            float y;
            float z = rand.nextFloat() * areaSize - areaSize * 0.5f;

            float[] triangles = map.passToModelMatrix(map.getRestreintArea(new float[]{x, 0f, z}));
            float moy = 0;
            for (int j = 0; j < triangles.length; j += 3) {
                moy += triangles[j + 1];
            }
            moy /= (triangles.length / 3f);

            y = moy;

            double angle = rand.nextDouble() * 360f;

            float[] mModelMatrix = new float[16];
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.translateM(mModelMatrix, 0, x, y , z);

            float[] mRotMatrix = new float[16];
            Matrix.setRotateM(mRotMatrix, 0, (float) angle, 0f, 1f, 0f);

            Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix.clone(), 0, mRotMatrix, 0);

            this.mModelsMatrix[i] = mModelMatrix.clone();
        }
    }

    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
        float[] tmpMVMatrix = new float[16];
        float[] tmpMVPMatrix = new float[16];
        for (int i = 0; i < this.nbTree; i++) {
            Matrix.multiplyMM(tmpMVMatrix, 0, mViewMatrix, 0, this.mModelsMatrix[i], 0);
            Matrix.multiplyMM(tmpMVPMatrix, 0, mProjectionMatrix, 0, tmpMVMatrix, 0);
            this.tree.draw(tmpMVPMatrix, tmpMVMatrix, mLightPosInEyeSpace, mCameraPosition);
        }
    }
}
