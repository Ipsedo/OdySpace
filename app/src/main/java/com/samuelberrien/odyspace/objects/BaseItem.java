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
        for(float[] currMtl : super.allCoords){
            for(int i = 0; i < currMtl.length / 9 ; i++){
                float[] u0 = new float[]{currMtl[i * 9 + 0], currMtl[i * 9 + 1], currMtl[i * 9 + 2], 1f};
                float[] u1 = new float[]{currMtl[i * 9 + 3], currMtl[i * 9 + 4], currMtl[i * 9 + 5], 1f};
                float[] u2 = new float[]{currMtl[i * 9 + 6], currMtl[i * 9 + 7], currMtl[i * 9 + 8], 1f};
                Matrix.multiplyMV(u0, 0, this.mModelMatrix, 0, u0.clone(), 0);
                Matrix.multiplyMV(u1, 0, this.mModelMatrix, 0, u1.clone(), 0);
                Matrix.multiplyMV(u2, 0, this.mModelMatrix, 0, u2.clone(), 0);

                double[] U0 = new double[]{u0[0], u0[1], u0[2]};
                double[] U1 = new double[]{u1[0], u1[1], u1[2]};
                double[] U2 = new double[]{u2[0], u2[1], u2[2]};
                for(float[] otherCurrMtl : other.allCoords){
                    for(int j = 0; j < otherCurrMtl.length / 9; j++){
                        float[] v0 = new float[]{otherCurrMtl[j * 9 + 0], otherCurrMtl[j * 9 + 1], otherCurrMtl[j * 9 + 2], 1f};
                        float[] v1 = new float[]{otherCurrMtl[j * 9 + 3], otherCurrMtl[j * 9 + 4], otherCurrMtl[j * 9 + 5], 1f};
                        float[] v2 = new float[]{otherCurrMtl[j * 9 + 6], otherCurrMtl[j * 9 + 7], otherCurrMtl[j * 9 + 8], 1f};
                        Matrix.multiplyMV(v0, 0, other.mModelMatrix, 0, v0.clone(), 0);
                        Matrix.multiplyMV(v1, 0, other.mModelMatrix, 0, v1.clone(), 0);
                        Matrix.multiplyMV(v2, 0, other.mModelMatrix, 0, v2.clone(), 0);

                        double[] V0 = new double[]{v0[0], v0[1], v0[2]};
                        double[] V1 = new double[]{v1[0], v1[1], v1[2]};
                        double[] V2 = new double[]{v2[0], v2[1], v2[2]};
                        if(Triangle.tr_tri_intersect3D(U0.clone(), U1.clone(), U2.clone(), V0.clone(), V1.clone(), V2.clone()) > 0 && Triangle.tr_tri_intersect3D(V0.clone(), V1.clone(), V2.clone(), U0.clone(), U1.clone(), U2.clone()) > 0){
                            return true;
                        }
                    }
                }
            }
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
}
