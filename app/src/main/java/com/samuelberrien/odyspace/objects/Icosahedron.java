package com.samuelberrien.odyspace.objects;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.utils.maths.Vector;

import java.util.Random;

/**
 * Created by samuel on 24/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Icosahedron extends BaseItem {

    public Icosahedron(Context context, float[] mPosition, Random rand) {
        super(context, "icosahedron.obj", "icosahedron.mtl", 1f, 0f, 1, mPosition, new float[]{0f, 0f, 0f}, new float[]{0f, 0f, 0f});
        super.changeColor(rand);
        super.radius = 1f;
    }

    @Override
    public boolean isCollided(BaseItem other){
        float[] dist = new float[]{super.mPosition[0] - other.mPosition[0], super.mPosition[1] - other.mPosition[1], super.mPosition[2] - other.mPosition[2]};
        return Vector.length3f(dist) - other.radius < super.radius;
    }

    public float[] getPosition(){
        return super.mPosition;
    }

    public Explosion makeExplosion(Context context){
        return new Explosion(context, super.mPosition.clone(), super.allDiffColorBuffer);
    }
}
