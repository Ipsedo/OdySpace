package com.samuelberrien.odyspace.objects;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.utils.Vector;

import java.util.ArrayList;

/**
 * Created by samuel on 18/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Ship extends BaseItem {

    private Context context;

    private final float maxSpeed = 0.05f;
    private final float rollCoeff = 2f;
    private final float pitchCoeff = 1f;

    private final float[] originalSpeedVec = new float[]{0f, 0f, 1f, 1f};
    private final float[] originalUpVec = new float[]{0f, 1f, 0f, 1f};

    public Ship(Context context){
        super(context, "ship.obj", "ship.mtl", 1f, 0f, 100, new float[]{0f, 0f, 0f}, new float[]{0f, 0f, 1f}, new float[]{0f, 0f, 0f});
        this.context = context;
    }

    public void move(float phi, float theta){
        float[] pitchMatrix = new float[16];
        float[] rollMatrix = new float[16];
        Matrix.setRotateM(rollMatrix, 0, phi * this.rollCoeff, 0f, 0f, 1f);
        Matrix.setRotateM(pitchMatrix, 0, theta * this.pitchCoeff, 1f, 0f, 0f);

        float[] currRotMatrix = new float[16];
        Matrix.multiplyMM(currRotMatrix, 0, pitchMatrix, 0, rollMatrix, 0);

        float[] currSpeed = new float[4];
        Matrix.multiplyMV(currSpeed, 0, currRotMatrix, 0, this.originalSpeedVec, 0);

        float[] realSpeed = new float[4];
        Matrix.multiplyMV(realSpeed, 0, super.mRotationMatrix, 0, currSpeed, 0);

        float[] tmpMat = super.mRotationMatrix.clone();
        Matrix.multiplyMM(super.mRotationMatrix, 0, tmpMat, 0, currRotMatrix, 0);

        super.mPosition[0] += this.maxSpeed * realSpeed[0];
        super.mPosition[1] += this.maxSpeed * realSpeed[1];
        super.mPosition[2] += this.maxSpeed * realSpeed[2];

        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, super.mPosition[0], super.mPosition[1], super.mPosition[2]);
        tmpMat = mModelMatrix.clone();
        Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, super.mRotationMatrix, 0);

        super.mModelMatrix = mModelMatrix;
    }

    public void fire(ArrayList<Rocket> rockets){
        rockets.add(new Rocket(this.context, super.mPosition.clone(), super.mSpeed.clone(), super.mAcceleration.clone(), super.mRotationMatrix.clone()));
    }

    public float[] getCamPosition(){
        float[] res = new float[3];
        float[] u = new float[4];
        Matrix.multiplyMV(u, 0, super.mRotationMatrix, 0, this.originalSpeedVec, 0);

        float[] v = new float[4];
        Matrix.multiplyMV(v, 0, super.mRotationMatrix, 0, this.originalUpVec, 0);

        res[0] = -10f * u[0] + super.mPosition[0] + 3f * v[0];
        res[1] = -10f * u[1] + super.mPosition[1] + 3f * v[1];
        res[2] = -10f * u[2] + super.mPosition[2] + 3f * v[2];

        return res;
    }

    public float[] getCamLookAtVec(){
        float[] res = new float[3];
        float[] u = new float[4];
        Matrix.multiplyMV(u, 0, super.mRotationMatrix, 0, this.originalSpeedVec, 0);

        res[0] = u[0];
        res[1] = u[1];
        res[2] = u[2];

        return res;
    }

    public float[] getCamUpVec(){
        float[] res = new float[3];
        float[] u = new float[4];
        Matrix.multiplyMV(u, 0, super.mRotationMatrix, 0, this.originalUpVec, 0);

        res[0] = u[0];
        res[1] = u[1];
        res[2] = u[2];

        return res;
    }

    @Override
    public void move(){

    }
}
