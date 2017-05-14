package com.samuelberrien.odyspace.objects;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtl;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.utils.maths.Vector;

/**
 * Created by samuel on 20/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Rocket extends BaseItem {

    private static float MAX_SPEED = 0.5f;

    private float maxSpeed;

    public Rocket(Context context, float lightCoeff, float[] mPosition, float[] mSpeed, float[] mAcceleration, float[] mRotationMatrix, float maxSpeed, float scale, int life) {
        super(context, "rocket.obj", "rocket.mtl", lightCoeff, 0f, false, life, mPosition, mSpeed, mAcceleration, scale);
        super.mRotationMatrix = mRotationMatrix;
        this.maxSpeed = maxSpeed * 3f;
        super.radius = 0.3f;
    }

    public Rocket(ObjModelMtlVBO objModelMtl, float[] mPosition, float[] mSpeed, float[] mAcceleration, float[] mRotationMatrix, float maxSpeed, float scale, int life) {
        super(objModelMtl, life, mPosition, mSpeed, mAcceleration, scale);
        super.mRotationMatrix = mRotationMatrix;
        this.maxSpeed = maxSpeed * 3f;
        super.radius = 0.3f;
    }

    @Override
    public void move() {
        float[] realSpeed = new float[]{super.mSpeed[0], super.mSpeed[1], super.mSpeed[2], 1f};

        Matrix.multiplyMV(realSpeed, 0, super.mRotationMatrix, 0, realSpeed.clone(), 0);

        super.mPosition[0] += Math.max(this.maxSpeed, Rocket.MAX_SPEED) * realSpeed[0];
        super.mPosition[1] += Math.max(this.maxSpeed, Rocket.MAX_SPEED) * realSpeed[1];
        super.mPosition[2] += Math.max(this.maxSpeed, Rocket.MAX_SPEED) * realSpeed[2];

        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, super.mPosition[0], super.mPosition[1], super.mPosition[2]);
        float[] tmpMat = mModelMatrix.clone();
        Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, super.mRotationMatrix, 0);
        Matrix.scaleM(mModelMatrix, 0, super.scale, super.scale, super.scale);

        super.mModelMatrix = mModelMatrix;
    }
}
