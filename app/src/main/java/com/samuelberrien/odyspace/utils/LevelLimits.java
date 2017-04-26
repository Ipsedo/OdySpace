package com.samuelberrien.odyspace.utils;

import java.util.Random;

/**
 * Created by samuel on 26/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class LevelLimits {

    private float xMax;
    private float xMin;
    private float yMax;
    private float yMin;
    private float zMax;
    private float zMin;

    public LevelLimits(float xMax, float xMin, float yMax, float yMin, float zMax, float zMin){
        if(xMax < xMin || yMax < yMin || zMax < zMin){
            throw new RuntimeException("Invalid(s) value(s) Max < Min");
        }
        this.xMax = xMax;
        this.xMin = xMin;
        this.yMax = yMax;
        this.yMin = yMin;
        this.zMax = zMax;
        this.zMin = zMin;
    }

    public boolean isInside(float[] xyz){
        return this.xMax > xyz[0] && this.xMin < xyz[0] && this.yMax > xyz[1] && this.yMin < xyz[1] && this.zMax > xyz[2] && this.zMin < xyz[2];
    }

    public float[] generateRandomPos(Random rand) {
        return new float[]{rand.nextFloat() * (this.xMax - this.xMin) + this.xMin, rand.nextFloat() * (this.yMax - this.yMin) + this.yMin, rand.nextFloat() * (this.zMax - this.zMin) + this.zMin};
    }
}
