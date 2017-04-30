package com.samuelberrien.odyspace.objects;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.utils.maths.Vector;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by samuel on 24/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Icosahedron extends BaseItem {

    private Explosion mExplosion;

    public Icosahedron(Context context, float[] mPosition, Random rand) {
        super(context, "icosahedron.obj", "icosahedron.mtl", 1f, 0f, 1, mPosition, new float[]{0f, 0f, 0f}, new float[]{0f, 0f, 0f});
        super.changeColor(rand);
        super.radius = 1f;
    }

    public float[] getPosition(){
        return super.mPosition;
    }

    public void makeExplosion(Context context){
        this.mExplosion = new Explosion(context, super.mPosition.clone(), super.allDiffColorBuffer);
    }

    public void addExplosion(ArrayList<Explosion> explosions) {
        explosions.add(this.mExplosion);
    }
}
