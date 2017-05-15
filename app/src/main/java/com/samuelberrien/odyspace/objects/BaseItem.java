package com.samuelberrien.odyspace.objects;

import android.content.Context;
import android.media.MediaPlayer;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.maps.NoiseMap;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtl;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
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

public class BaseItem extends ObjModelMtlVBO {

    private native boolean areCollided(float[] mPointItem1, float[] mModelMatrix1, float[] mPointItem2, float[] mModelMatrix2);

    static {
        System.loadLibrary("collision");
    }

    protected int life;

    protected float[] mPosition;
    protected float[] mSpeed;
    protected float[] mAcceleration;

    protected float[] mRotationMatrix;

    protected float[] mModelMatrix;

    protected float radius;

    protected float scale;

    protected MediaPlayer mediaPlayer;

    public BaseItem(Context context, String objFileName, String mtlFileName, float lightAugmentation, float distanceCoef, boolean randomColor, int life, float[] mPosition, float[] mSpeed, float[] mAcceleration, float scale) {
        super(context, objFileName, mtlFileName, lightAugmentation, distanceCoef, randomColor);
        this.life = life;
        this.mPosition = mPosition;
        this.mSpeed = mSpeed;
        this.mAcceleration = mAcceleration;
        this.mRotationMatrix = new float[16];
        Matrix.setIdentityM(this.mRotationMatrix, 0);
        this.mModelMatrix = new float[16];
        Matrix.setIdentityM(this.mModelMatrix, 0);
        this.scale = scale;
        this.radius = this.scale;

        this.mediaPlayer = MediaPlayer.create(context, R.raw.simple_boom);
    }

    public BaseItem(ObjModelMtlVBO objModelMtl, int life, float[] mPosition, float[] mSpeed, float[] mAcceleration, float scale) {
        super(objModelMtl);
        this.life = life;
        this.mPosition = mPosition;
        this.mSpeed = mSpeed;
        this.mAcceleration = mAcceleration;
        this.mRotationMatrix = new float[16];
        Matrix.setIdentityM(this.mRotationMatrix, 0);
        this.mModelMatrix = new float[16];
        Matrix.setIdentityM(this.mModelMatrix, 0);
        this.scale = scale;
        this.radius = this.scale;
    }

    public boolean isAlive() {
        return this.life > 0;
    }

    public boolean isCollided(BaseItem other) {
        return this.areCollided(this.allCoords.clone(), this.mModelMatrix.clone(), other.allCoords.clone(), other.mModelMatrix.clone());
    }

    public void playExplosion() {
        this.mediaPlayer.start();
    }

    public void decrementsBothLife(BaseItem other) {
        if (this.life >= 0 && other.life >= 0) {
            int otherLife = other.life;
            other.life -= this.life;
            this.life -= otherLife;
        }
    }

    public boolean isOutOfBound(LevelLimits levelLimits) {
        return !levelLimits.isInside(this.mPosition);
    }

    public void mapCollision(NoiseMap map) {
        if (this.areCollided(this.allCoords.clone(), this.mModelMatrix.clone(), map.getRestreintArea(this.mPosition), map.getModelMatrix())) {
            this.life = 0;
        }
    }

    public float[] vector3fTo(BaseItem to) {
        return new float[]{to.mPosition[0] - this.mPosition[0], to.mPosition[1] - this.mPosition[1], to.mPosition[2] - this.mPosition[2]};
    }

    public void move() {
        this.mSpeed[0] += this.mAcceleration[0];
        this.mSpeed[1] += this.mAcceleration[1];
        this.mSpeed[2] += this.mAcceleration[2];

        this.mPosition[0] += this.mSpeed[0];
        this.mPosition[1] += this.mSpeed[1];
        this.mPosition[2] += this.mSpeed[2];

        float[] tmp = new float[16];
        Matrix.setIdentityM(tmp, 0);
        Matrix.translateM(tmp, 0, this.mPosition[0], this.mPosition[1], this.mPosition[2]);
        Matrix.scaleM(tmp, 0, this.scale, this.scale, this.scale);
        this.mModelMatrix = tmp.clone();
    }

    public void draw(float[] pMatrix, float[] vMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
        float[] mvMatrix = new float[16];
        Matrix.multiplyMM(mvMatrix, 0, vMatrix, 0, this.mModelMatrix, 0);
        float[] mvpMatrix = new float[16];
        Matrix.multiplyMM(mvpMatrix, 0, pMatrix, 0, mvMatrix, 0);
        super.draw(mvpMatrix, mvMatrix, mLightPosInEyeSpace, mCameraPosition);
    }
}
