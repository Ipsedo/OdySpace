package com.samuelberrien.odyspace.objects;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtl;
import com.samuelberrien.odyspace.utils.game.LevelLimits;
import com.samuelberrien.odyspace.utils.collision.Triangle;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by samuel on 18/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class BaseItem extends ObjModelMtl {

    protected int life;

    protected float[] mPosition;
    protected float[] mSpeed;
    protected float[] mAcceleration;

    protected float[] mRotationMatrix;

    protected float[] mModelMatrix;

    protected float radius;

    public BaseItem(Context context, String objFileName, String mtlFileName, float lightAugmentation, float distanceCoef, int life, float[] mPosition, float[] mSpeed, float[] mAcceleration){
        super(context, objFileName, mtlFileName, lightAugmentation, distanceCoef);
        this.life = life;
        this.mPosition = mPosition;
        this.mSpeed = mSpeed;
        this.mAcceleration = mAcceleration;
        this.mRotationMatrix = new float[16];
        Matrix.setIdentityM(this.mRotationMatrix, 0);
        this.mModelMatrix = new float[16];
        Matrix.setIdentityM(this.mModelMatrix, 0);
        this.radius = 1f;
    }

    public void changeColor(Random rand){
        ArrayList<FloatBuffer> tmpA = super.makeColor(rand);
        ArrayList<FloatBuffer> tmpD = super.makeColor(rand);
        ArrayList<FloatBuffer> tmpS = super.makeColor(rand);
        super.setColors(tmpA, tmpD, tmpS);
    }

    public boolean isAlive(){
        return this.life > 0;
    }

    public boolean isCollided(BaseItem other){
        ArrayList<float[]> otherVertClone = new ArrayList<>();
        for(float[] mtl : other.allCoords) {
            int limit = mtl.length / 9;
            for (int j = 0; j < limit; j++) {
                int currJ = j * 9;
                float[] v0 = new float[]{mtl[currJ + 0], mtl[currJ + 1], mtl[currJ + 2], 1f};
                float[] v1 = new float[]{mtl[currJ + 3], mtl[currJ + 4], mtl[currJ + 5], 1f};
                float[] v2 = new float[]{mtl[currJ + 6], mtl[currJ + 7], mtl[currJ + 8], 1f};
                Matrix.multiplyMV(v0, 0, other.mModelMatrix, 0, v0.clone(), 0);
                Matrix.multiplyMV(v1, 0, other.mModelMatrix, 0, v1.clone(), 0);
                Matrix.multiplyMV(v2, 0, other.mModelMatrix, 0, v2.clone(), 0);

                mtl[currJ + 0] = v0[0];
                mtl[currJ + 1] = v0[1];
                mtl[currJ + 2] = v0[2];

                mtl[currJ + 3] = v1[0];
                mtl[currJ + 4] = v1[1];
                mtl[currJ + 5] = v1[2];

                mtl[currJ + 6] = v2[0];
                mtl[currJ + 7] = v2[1];
                mtl[currJ + 8] = v2[2];
            }
            otherVertClone.add(mtl);
        }

        ArrayList<ArrayList<CollisionThread>> threadsList = new ArrayList<>();

        for(float[] currMtl : super.allCoords){
            ArrayList<CollisionThread> tmp = new ArrayList<>();
            int limit = currMtl.length / 9;
            for(int i = 0; i < limit; i++){
                int currI = i * 9;
                float[] u0 = new float[]{currMtl[currI + 0], currMtl[currI + 1], currMtl[currI + 2], 1f};
                float[] u1 = new float[]{currMtl[currI + 3], currMtl[currI + 4], currMtl[currI + 5], 1f};
                float[] u2 = new float[]{currMtl[currI + 6], currMtl[currI + 7], currMtl[currI + 8], 1f};
                Matrix.multiplyMV(u0, 0, this.mModelMatrix, 0, u0.clone(), 0);
                Matrix.multiplyMV(u1, 0, this.mModelMatrix, 0, u1.clone(), 0);
                Matrix.multiplyMV(u2, 0, this.mModelMatrix, 0, u2.clone(), 0);

                CollisionThread tmpThreads = new CollisionThread(u0, u1, u2, otherVertClone);
                tmpThreads.start();
                tmp.add(tmpThreads);
                /*for(float[] otherCurrMtl : otherVertClone){
                    int limit2 = otherCurrMtl.length / 9;
                    for(int j = 0; j < limit2; j++){
                        int currJ = j * 9;
                        float[] v0 = new float[]{otherCurrMtl[currJ + 0], otherCurrMtl[currJ + 1], otherCurrMtl[currJ + 2]};
                        float[] v1 = new float[]{otherCurrMtl[currJ + 3], otherCurrMtl[currJ + 4], otherCurrMtl[currJ + 5]};
                        float[] v2 = new float[]{otherCurrMtl[currJ + 6], otherCurrMtl[currJ + 7], otherCurrMtl[currJ + 8]};

                        if(Triangle.tr_tri_intersect3D(u0, u1, u2, v0, v1, v2) > 0 && Triangle.tr_tri_intersect3D(v0, v1, v2, u0, u1, u2) > 0){
                            System.out.println("COLISIONNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
                            return true;
                        }
                    }
                }*/
            }
            threadsList.add(tmp);
        }

        try {
            for(ArrayList<CollisionThread> i : threadsList) {
                for(CollisionThread j : i) {
                    j.join();
                    if(j.areCollided()){
                        return true;
                    }
                }
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        return false;
    }

    public void decrementsBothLife(BaseItem other){
        int otherLife = other.life;
        other.life -= this.life;
        this.life -= otherLife;
    }

    public boolean isOutOfBound(LevelLimits levelLimits){
        return !levelLimits.isInside(this.mPosition);
    }

    public void move(){
        this.mSpeed[0] += this.mAcceleration[0];
        this.mSpeed[1] += this.mAcceleration[1];
        this.mSpeed[2] += this.mAcceleration[2];

        this.mPosition[0] += this.mSpeed[0];
        this.mPosition[1] += this.mSpeed[1];
        this.mPosition[2] += this.mSpeed[2];

        float[] tmp = new float[16];
        Matrix.setIdentityM(tmp, 0);
        Matrix.translateM(tmp, 0, this.mPosition[0], this.mPosition[1], this.mPosition[2]);
        this.mModelMatrix = tmp.clone();
    }

    public void draw(float[] pMatrix, float[] vMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition){
        float[] mvMatrix = new float[16];
        Matrix.multiplyMM(mvMatrix, 0, vMatrix, 0, this.mModelMatrix, 0);
        float[] mvpMatrix = new float[16];
        Matrix.multiplyMM(mvpMatrix, 0, pMatrix, 0, mvMatrix, 0);
        super.draw(mvpMatrix, mvMatrix, mLightPosInEyeSpace, mCameraPosition);
    }

    private class CollisionThread extends Thread {

        private float[] u0;
        private float[] u1;
        private float[] u2;

        private ArrayList<float[]> other;

        private boolean areCollided;

        public CollisionThread(float[] u0, float[] u1, float[] u2, ArrayList<float[]> other) {
            this.u0 = u0;
            this.u1 = u1;
            this.u2 = u2;
            this.other = other;
            this.areCollided = false;
        }

        @Override
        public void run(){
            //this.areCollided = Triangle.tr_tri_intersect3D(this.u0.clone(), this.u1.clone(), this.u2.clone(), this.v0.clone(), this.v1.clone(), this.v2.clone()) > 0 && Triangle.tr_tri_intersect3D(this.v0.clone(), this.v1.clone(), this.v2.clone(), this.u0.clone(), this.u1.clone(), this.u2.clone()) > 0;
            for(float[] otherCurrMtl : other){
                int limit = otherCurrMtl.length / 9;
                for(int j = 0; j < limit; j++){
                    int currJ = j * 9;
                    float[] v0 = new float[]{otherCurrMtl[currJ + 0], otherCurrMtl[currJ + 1], otherCurrMtl[currJ + 2]};
                    float[] v1 = new float[]{otherCurrMtl[currJ + 3], otherCurrMtl[currJ + 4], otherCurrMtl[currJ + 5]};
                    float[] v2 = new float[]{otherCurrMtl[currJ + 6], otherCurrMtl[currJ + 7], otherCurrMtl[currJ + 8]};

                    if(Triangle.tr_tri_intersect3D(this.u0, this.u1, this.u2, v0, v1, v2) > 0 && Triangle.tr_tri_intersect3D(v0, v1, v2, this.u0, this.u1, this.u2) > 0){
                        this.areCollided = true;
                        return;
                    }
                }
            }
        }

        public boolean areCollided(){
            return this.areCollided;
        }
    }
}
