package com.samuelberrien.odyspace.objects;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.utils.Vector;

import java.util.ArrayList;

/**
 * Created by samuel on 24/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Icosahedron extends BaseItem {

    private boolean isTouched;

    private ArrayList<float[][]> mSpeedExplosion;

    public Icosahedron(Context context, float[] mPosition) {
        super(context, "icosahedron.obj", "icosahedron.mtl", 1f, 0f, 1, mPosition, new float[]{0f, 0f, 0f}, new float[]{0f, 0f, 0f});
        this.isTouched = false;
        this.mSpeedExplosion = new ArrayList<>();
    }

    public boolean isCollided(BaseItem other){
        float[] dist = new float[]{this.mPosition[0] - other.mPosition[0], this.mPosition[1] - other.mPosition[1], this.mPosition[2] - other.mPosition[2]};

        if(Vector.length3f(dist) < 1f){
            this.isTouched = true;
            return true;
        }
        return false;
    }
}
