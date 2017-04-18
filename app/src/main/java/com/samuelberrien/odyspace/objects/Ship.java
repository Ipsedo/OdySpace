package com.samuelberrien.odyspace.objects;

import android.content.Context;
import android.opengl.Matrix;
import android.provider.Settings;

import com.samuelberrien.odyspace.drawable.ObjModelMtl;

/**
 * Created by samuel on 18/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Ship extends BaseItem {

    private float phi;
    private float theta;

    private final float maxSpeed = 0.01f;
    private final float angleCoeff = 0.01f;

    public Ship(Context context){
        super(context, "ship.obj", "ship.mtl", 1f, 0f, 100, new float[]{0f, 0f, 0f}, new float[]{0f, 0f, 0.0f}, new float[]{0f, 0f, 0f});
        super.mSpeed = new float[]{0f, 0f, maxSpeed};
        this.phi = 0f;
        this.theta = 0f;
    }

    public void move(float phi, float theta){
        this.phi += - angleCoeff * theta;
        this.theta += - angleCoeff * phi;

        super.mSpeed[0] = maxSpeed * (float) (Math.cos(this.phi) * Math.sin(this.theta));
        super.mSpeed[1] = maxSpeed * (float) Math.sin(this.phi);
        super.mSpeed[2] = maxSpeed * (float) (Math.cos(this.phi) * Math.cos(this.theta));

        super.mSpeed[0] += super.mAcceleration[0];
        super.mSpeed[1] += super.mAcceleration[1];
        super.mSpeed[2] += super.mAcceleration[2];

        super.mPosition[0] += super.mSpeed[0];
        super.mPosition[1] += super.mSpeed[1];
        super.mPosition[2] += super.mSpeed[2];

        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, super.mPosition[0], super.mPosition[1], super.mPosition[2]);

        float[] normIni = this.normalize(new float[]{0f, 1e-10f, maxSpeed});
        float[] normSpeed = this.normalize(super.mSpeed);
        float[] mRotAxis = this.cross(normIni, normSpeed);
        float mAngle = (float) (Math.acos(this.dot(normIni, normSpeed)) * 360d / (Math.PI * 2d));
        Matrix.setRotateM(super.mRotationMatrix, 0, mAngle, mRotAxis[0], mRotAxis[1], mRotAxis[2]);
        float[] tmpMat = mModelMatrix.clone();
        Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, super.mRotationMatrix, 0);

        super.mModelMatrix = mModelMatrix.clone();
    }

    private float dot(float[] u, float[] v){
        return u[0] * v[0] + u[1] * v[1] + u[2] * v[2];
    }

    private float[] cross(float[] u, float[] v){
        return new float[]{u[1] * v[2] - u[2] * v[1], u[2] * v[0] - u[0] * v[2], u[0] * v[1] - u[1] * v[0]};
    }

    private float[] normalize(float[] u){
        float length = this.length(u);
        return new float[]{u[0] / length, u[1] / length, u[2] / length};
    }

    private float length(float[] u){
        return (float) Math.sqrt(Math.pow(u[0], 2d) + Math.pow(u[1], 2d) + Math.pow(u[2], 2d));
    }

    @Override
    public void move(){

    }
}
