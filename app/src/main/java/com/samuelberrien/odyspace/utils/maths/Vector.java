package com.samuelberrien.odyspace.utils.maths;

/**
 * Created by samuel on 19/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Vector {

    public static float dot3f(float[] u, float[] v){
        return u[0] * v[0] + u[1] * v[1] + u[2] * v[2];
    }

    public static float[] cross3f(float[] u, float[] v){
        return new float[]{u[1] * v[2] - u[2] * v[1], u[2] * v[0] - u[0] * v[2], u[0] * v[1] - u[1] * v[0]};
    }

    public static float[] normalize3f(float[] u){
        float lgt = Vector.length3f(u);
        return new float[]{u[0] / lgt, u[1] / lgt, u[2] / lgt};
    }

    public static float length3f(float[] u){
        return (float) Math.sqrt(Math.pow(u[0], 2d) + Math.pow(u[1], 2d) + Math.pow(u[2], 2d));
    }
}
