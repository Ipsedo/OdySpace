package com.samuelberrien.odyspace.objects;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.utils.Vector;

/**
 * Created by samuel on 18/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Ship extends BaseItem {

    private float phi;
    private float theta;

    private float[] mRotAxis;

    private final float maxSpeed = 0.01f;
    private final float angleCoeff = 0.1f;

    private final float[] speedInit = new float[]{0f, 0f, this.maxSpeed};
    private final float[] perVecIni = new float[]{0f, this.maxSpeed, 0f};

    private final float[] originalSpeedVec = new float[]{0f, 0f, 1f, 1f};
    private final float[] originalUpVec = new float[]{0f, 1f, 0f, 1f};

    public Ship(Context context){
        super(context, "ship.obj", "ship.mtl", 1f, 0f, 100, new float[]{0f, 0f, 0f}, new float[]{0f, 0f, 0.0f}, new float[]{0f, 0f, 0f});
        super.mSpeed = this.speedInit;
        this.phi = 0f;
        this.theta = 0f;
    }

    public void move(float phi, float theta){
        /*this.phi += - angleCoeff * theta;
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

        float[] normIni = Vector.normalize3f(new float[]{0f, 1e-10f, maxSpeed});
        float[] normSpeed = Vector.normalize3f(super.mSpeed);
        this.mRotAxis = Vector.cross3f(normIni, normSpeed);
        float mAngle = (float) (Math.acos(Vector.dot3f(normIni, normSpeed)) * 360d / (Math.PI * 2d));
        Matrix.setRotateM(super.mRotationMatrix, 0, mAngle, this.mRotAxis[0], this.mRotAxis[1], this.mRotAxis[2]);
        float[] tmpMat = mModelMatrix.clone();
        Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, super.mRotationMatrix, 0);

        super.mModelMatrix = mModelMatrix.clone();*/

        float[] pitchMatrix = new float[16];
        float[] rollMatrix = new float[16];
        Matrix.setRotateM(rollMatrix, 0, phi * 2f, 0f, 0f, 1f);
        Matrix.setRotateM(pitchMatrix, 0, theta, 1f, 0f, 0f);

        float[] currRotMatrix = new float[16];
        Matrix.multiplyMM(currRotMatrix, 0, pitchMatrix, 0, rollMatrix, 0);

        float[] currSpeed = new float[4];
        Matrix.multiplyMV(currSpeed, 0, currRotMatrix, 0, this.originalSpeedVec, 0);

        float[] realSpeed = new float[4];
        Matrix.multiplyMV(realSpeed, 0, this.mRotationMatrix, 0, currSpeed, 0);

        float[] tmpMat = this.mRotationMatrix.clone();
        Matrix.multiplyMM(this.mRotationMatrix, 0, tmpMat, 0, currRotMatrix, 0);

        this.mPosition[0] += this.maxSpeed * realSpeed[0];
        this.mPosition[1] += this.maxSpeed * realSpeed[1];
        this.mPosition[2] += this.maxSpeed * realSpeed[2];

        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, super.mPosition[0], super.mPosition[1], super.mPosition[2]);
        tmpMat = mModelMatrix.clone();
        Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, super.mRotationMatrix, 0);

        super.mModelMatrix = mModelMatrix;
    }



    public float[] getCamPosition(){
        /*float[] tmp = Vector.normalize3f(this.perVecIni);
        float[] upVec = new float[]{tmp[0], tmp[1], tmp[2], 1f};
        Matrix.multiplyMV(upVec, 0, super.mRotationMatrix, 0, upVec.clone(), 0);

        float[] res = new float[3];
        tmp = Vector.normalize3f(super.mSpeed);
        res[0] += -10f * tmp[0] + super.mPosition[0] + 3f * upVec[0];
        res[1] += -10f * tmp[1] + super.mPosition[1] + 3f * upVec[1];
        res[2] += -10f * tmp[2] + super.mPosition[2] + 3f * upVec[2];*/

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

    public float[] getPhiTheta(){
        return new float[]{this.phi, this.theta};
    }

    @Override
    public void move(){

    }
}
