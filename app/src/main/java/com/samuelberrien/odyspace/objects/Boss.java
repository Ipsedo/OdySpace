package com.samuelberrien.odyspace.objects;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.utils.maths.Vector;

import java.util.Random;

/**
 * Created by samuel on 30/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Boss extends BaseItem {

    private final int MAX_COUNT = 10;
    private int counter;

    private Random rand;

    private float maxSpeed;

    public Boss(Context context, String objFileName, String mtlFileName, int life, float[] mPosition) {
        super(context, objFileName, mtlFileName, 1f, 0f, life, mPosition, new float[3], new float[3]);
        this.counter = 0;
        this.rand = new Random(System.currentTimeMillis());
        this.maxSpeed = 0.01f;
    }

    public void move(Ship ship){
        if(this.counter == this.MAX_COUNT){
            float[] shipBossVec = new float[]{ship.mPosition[0] - super.mPosition[0], ship.mPosition[0] - super.mPosition[0], ship.mPosition[0] - super.mPosition[0]};
            float length = Vector.length3f(shipBossVec);
            super.mSpeed[0] = this.maxSpeed * shipBossVec[0] / length;
            super.mSpeed[1] = this.maxSpeed * shipBossVec[1] / length;
            super.mSpeed[2] = this.maxSpeed * shipBossVec[2] / length;
            this.counter = 0;
        } else {
            double phi = Math.PI * 2d;
            double theta = Math.PI * 2d;
            super.mSpeed[0] = this.maxSpeed * (float) (Math.cos(phi) * Math.sin(theta));
            super.mSpeed[1] = this.maxSpeed * (float) Math.sin(phi);
            super.mSpeed[2] = this.maxSpeed * (float) (Math.cos(phi) * Math.cos(theta));
            this.counter++;
        }

        super.mPosition[0] += super.mSpeed[0];
        super.mPosition[1] += super.mSpeed[1];
        super.mPosition[2] += super.mSpeed[2];

        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, super.mPosition[0], super.mPosition[1], super.mPosition[2]);

        super.mModelMatrix = mModelMatrix.clone();
    }

    @Override
    public void move(){

    }
}
