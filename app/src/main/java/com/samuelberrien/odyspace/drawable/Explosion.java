package com.samuelberrien.odyspace.drawable;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.utils.ShaderLoader;
import com.samuelberrien.odyspace.utils.Vector;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

    public Explosion(Context context, float[] mPosition, ArrayList<FloatBuffer> mDiffColor){
        this.particules = new ArrayList<>();
        Random rand = new Random(System.currentTimeMillis());
        for(int i = 0; i < 10; i++){
            Particule tmp = new Particule(context, rand, mPosition);
            tmp.setColors(mDiffColor, mDiffColor, mDiffColor);
            this.particules.add(tmp);
        }
    }

    public void move(){
        for(Particule p : this.particules){
            p.move();
        }
    }

    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition){
        for(Particule p : this.particules){
            p.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        }
    }

    public boolean isAlive(){
        boolean res = false;
        for(Particule p : this.particules){
            res |= p.isAlive();
        }
        return res;
    }

    private class Particule extends ObjModelMtl{

        private final float maxSpeed = 1.5f;

        private float[] mPosition;
        private float[] mSpeed;
        private float[] mModelMatrix;

        private float mAngle;
        private float[] mRotAxis;

        public Particule(Context context, Random rand, float[] mPosition){
            super(context, "triangle.obj", "triangle.mtl", 1f, 0f);

            this.mPosition = mPosition.clone();
            this.mSpeed = new float[3];
            double phi = rand.nextDouble() * 360d;
            double theta = rand.nextDouble() * 360d;
            this.mSpeed[0] = (float) (Math.cos(phi) * Math.sin(theta));
            this.mSpeed[1] = (float) Math.sin(phi);
            this.mSpeed[2] = (float) (Math.cos(phi) * Math.cos(theta));
            System.out.println("Speed : " + this.mSpeed[0] + ", " + this.mSpeed[1] + ", " + this.mSpeed[2]);
            this.mModelMatrix = new float[16];
            this.mAngle = rand.nextFloat() * 360f;
            this.mRotAxis = new float[3];
            this.mRotAxis[0] = rand.nextFloat() * 2f - 1f;
            this.mRotAxis[1] = rand.nextFloat() * 2f - 1f;
            this.mRotAxis[2] = rand.nextFloat() * 2f - 1f;
            super.makeProgram(context, R.raw.particule_vs, R.raw.particule_fs);
        }


        public void move(){
            this.mPosition[0] += this.maxSpeed * this.mSpeed[0];
            this.mPosition[1] += this.maxSpeed * this.mSpeed[1];
            this.mPosition[2] += this.maxSpeed * this.mSpeed[2];

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

        public boolean isAlive(){
            return Vector.length3f(this.mSpeed) > 0.05;
        }

        public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition){
            GLES20.glDisable(GLES20.GL_CULL_FACE);
            float[] mPVMatrix = new float[16];
            Matrix.multiplyMM(mPVMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
            float[] mPVMMatrix = new float[16];
            Matrix.multiplyMM(mPVMMatrix, 0, mPVMatrix, 0, this.mModelMatrix, 0);

            super.draw(mPVMMatrix, mPVMatrix, mLightPosInEyeSpace, mCameraPosition);
        }
    }
}
