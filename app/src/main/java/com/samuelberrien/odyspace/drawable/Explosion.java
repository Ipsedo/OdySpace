package com.samuelberrien.odyspace.drawable;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.obj.ObjModel;
import com.samuelberrien.odyspace.utils.maths.Vector;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by samuel on 24/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Explosion {

    private ArrayList<Particule> particules;
    private ObjModel particule;
    private final float maxSpeed;
    private final float limitSpeed;

    public Explosion(Context context, float[] mPosition, FloatBuffer mDiffColor, float initSpeed, float limitSpeed) {
        this.maxSpeed = initSpeed;
        this.limitSpeed = limitSpeed;
        this.particules = new ArrayList<>();
        Random rand = new Random(System.currentTimeMillis());
        this.particule = new ObjModel(context, "triangle.obj", 1f, 1f, 1f, 1f, 0f, 1f);
        this.particule.setColor(mDiffColor);
        for (int i = 0; i < 10; i++) {
            this.particules.add(new Particule(rand, mPosition));
        }
    }

    public void move() {
        for (Particule p : this.particules) {
            p.move();
        }
    }

    public void setPosition(float[] mPosition) {
        for (Particule p : this.particules) {
            p.mPosition = mPosition.clone();
        }
    }

    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
        for (Particule p : this.particules) {
            p.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, this.particule);
        }
    }

    public boolean isAlive() {
        boolean res = false;
        for (Particule p : this.particules) {
            res |= p.isAlive();
        }
        return res;
    }

    private class Particule {

        private float[] mPosition;
        private float[] mSpeed;
        private float[] mModelMatrix;

        private float mAngle;
        private float[] mRotAxis;

        public Particule(Random rand, float[] mPosition) {

            this.mPosition = mPosition.clone();
            this.mSpeed = new float[3];
            double phi = rand.nextDouble() * 360d;
            double theta = rand.nextDouble() * 360d;
            this.mSpeed[0] = (float) (Math.cos(phi) * Math.sin(theta));
            this.mSpeed[1] = (float) Math.sin(phi);
            this.mSpeed[2] = (float) (Math.cos(phi) * Math.cos(theta));
            this.mModelMatrix = new float[16];
            this.mAngle = rand.nextFloat() * 360f;
            this.mRotAxis = new float[3];
            this.mRotAxis[0] = rand.nextFloat() * 2f - 1f;
            this.mRotAxis[1] = rand.nextFloat() * 2f - 1f;
            this.mRotAxis[2] = rand.nextFloat() * 2f - 1f;
        }


        public void move() {
            this.mPosition[0] += Explosion.this.maxSpeed * this.mSpeed[0];
            this.mPosition[1] += Explosion.this.maxSpeed * this.mSpeed[1];
            this.mPosition[2] += Explosion.this.maxSpeed * this.mSpeed[2];

            this.mSpeed[0] /= 1.1;
            this.mSpeed[1] /= 1.1;
            this.mSpeed[2] /= 1.1;

            float[] mModelMatrix = new float[16];
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.translateM(mModelMatrix, 0, this.mPosition[0], this.mPosition[1], this.mPosition[2]);

            float[] tmpRot = new float[16];
            Matrix.setRotateM(tmpRot, 0, this.mAngle, this.mRotAxis[0], this.mRotAxis[1], this.mRotAxis[2]);

            float[] tmpMat = mModelMatrix.clone();
            Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, tmpRot, 0);

            this.mModelMatrix = mModelMatrix.clone();
        }

        public boolean isAlive() {
            return Vector.length3f(this.mSpeed) > Explosion.this.limitSpeed;
        }

        public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, ObjModel object) {
            GLES20.glDisable(GLES20.GL_CULL_FACE);
            float[] mPVMatrix = new float[16];
            Matrix.multiplyMM(mPVMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
            float[] mPVMMatrix = new float[16];
            Matrix.multiplyMM(mPVMMatrix, 0, mPVMatrix, 0, this.mModelMatrix, 0);

            object.draw(mPVMMatrix, mPVMatrix, mLightPosInEyeSpace);
        }
    }
}
