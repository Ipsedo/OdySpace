package com.samuelberrien.odyspace.objects;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.utils.ShaderLoader;

/**
 * Created by samuel on 20/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Rocket extends BaseItem {
    private final float maxSpeed = 0.5f;

    public Rocket(Context context, float[] mPosition, float[] mSpeed, float[] mAcceleration, float[] mRotationMatrix) {
        super(context, "rocket.obj", "rocket.mtl", 1f, 0f, 1, mPosition, mSpeed, mAcceleration);
        super.mRotationMatrix = mRotationMatrix;
    }

    public void move(){
        super.mPosition[0] += this.maxSpeed * super.mSpeed[0];
        super.mPosition[1] += this.maxSpeed * super.mSpeed[1];
        super.mPosition[2] += this.maxSpeed * super.mSpeed[2];

        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, super.mPosition[0], super.mPosition[1], super.mPosition[2]);
        float[] tmpMat = mModelMatrix.clone();
        Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, super.mRotationMatrix, 0);

        super.mModelMatrix = mModelMatrix;
    }

    public void draw(float[] pMatrix, float[] vMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition){
        super.draw(pMatrix, vMatrix, mLightPosInEyeSpace, mCameraPosition);
        ShaderLoader.checkGlError("aaaaa");
    }
}
