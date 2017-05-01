package com.samuelberrien.odyspace.objects;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.utils.maths.Vector;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by samuel on 30/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Boss extends BaseItem {

    private Context context;

    private final int MAX_COUNT = 100;
    private int counter;

    private Random rand;

    private float maxSpeed;

    private double phi;
    private double theta;

    public Boss(Context context, String objFileName, String mtlFileName, int life, float[] mPosition) {
        super(context, objFileName, mtlFileName, 1f, 0f, life, mPosition, new float[]{0f, 0f, 0f}, new float[]{0f, 0f, 0f});
        this.context = context;
        this.counter = 0;
        this.rand = new Random(System.currentTimeMillis());
        this.maxSpeed = 0.1f;
        this.phi = 0f;
        this.theta = 0f;
    }

    public void move(Ship ship){
        if(this.counter >= this.MAX_COUNT){
            float[] shipBossVec = new float[]{ship.mPosition[0] - super.mPosition[0], ship.mPosition[0] - super.mPosition[0], ship.mPosition[0] - super.mPosition[0]};
            float length = Vector.length3f(shipBossVec);
            super.mSpeed[0] = this.maxSpeed * shipBossVec[0] / length;
            super.mSpeed[1] = this.maxSpeed * shipBossVec[1] / length;
            super.mSpeed[2] = this.maxSpeed * shipBossVec[2] / length;
            this.counter = 0;
        } else {
            this.phi += (this.rand.nextDouble() * 2d - 1d) / Math.PI;
            this.theta += (this.rand.nextDouble() * 2d - 1d) / Math.PI;
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

    public void fire(ArrayList<BaseItem> r, Ship ship){
        if(this.counter % 30 == 0) {
            float[] originalVec = new float[]{ship.mPosition[0] - super.mPosition[0], ship.mPosition[1] - super.mPosition[1], ship.mPosition[2] - super.mPosition[2]};
            float length = Vector.length3f(originalVec);
            float[] tmpMat = new float[16];
            Matrix.setIdentityM(tmpMat, 0);
            r.add(new Rocket(this.context, super.mPosition.clone(), new float[]{originalVec[0] / length, originalVec[1] / length, originalVec[2] / length}, new float[]{0f, 0f, 0f}, tmpMat, 0.005f));
        }
    }

    @Override
    public void move(){

    }
}
