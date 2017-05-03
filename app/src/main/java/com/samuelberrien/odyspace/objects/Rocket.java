package com.samuelberrien.odyspace.objects;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtl;
import com.samuelberrien.odyspace.utils.maths.Vector;

/**
 * Created by samuel on 20/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Rocket extends BaseItem {

    private float maxSpeed;

    public Rocket(Context context, float lightCoeff, float[] mPosition, float[] mSpeed, float[] mAcceleration, float[] mRotationMatrix, float maxSpeed) {
        super(context, "rocket.obj", "rocket.mtl", lightCoeff, 0f, 1, mPosition, mSpeed, mAcceleration);
        super.mRotationMatrix = mRotationMatrix;
        this.maxSpeed = maxSpeed * 3f;
        super.radius = 0.3f;
    }

    public Rocket(ObjModelMtl objModelMtl, float[] mPosition, float[] mSpeed, float[] mAcceleration, float[] mRotationMatrix, float maxSpeed) {
        super(objModelMtl, 1, mPosition, mSpeed, mAcceleration);
        super.mRotationMatrix = mRotationMatrix;
        this.maxSpeed = maxSpeed * 3f;
        super.radius = 0.3f;
    }

    @Override
    public void move(){
        float[] realSpeed = new float[]{super.mSpeed[0], super.mSpeed[1], super.mSpeed[2], 1f};

        Matrix.multiplyMV(realSpeed, 0, super.mRotationMatrix, 0, realSpeed.clone(), 0);

        super.mPosition[0] += Math.max(this.maxSpeed, 2f) * realSpeed[0];
        super.mPosition[1] += Math.max(this.maxSpeed, 2f) * realSpeed[1];
        super.mPosition[2] += Math.max(this.maxSpeed, 2f) * realSpeed[2];

        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, super.mPosition[0], super.mPosition[1], super.mPosition[2]);
        float[] tmpMat = mModelMatrix.clone();
        Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, super.mRotationMatrix, 0);

        super.mModelMatrix = mModelMatrix;
    }
}
