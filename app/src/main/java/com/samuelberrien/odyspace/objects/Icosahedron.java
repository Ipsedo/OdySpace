package com.samuelberrien.odyspace.objects;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.utils.maths.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by samuel on 24/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Icosahedron extends BaseItem {

    private Explosion mExplosion;

    public Icosahedron(Context context, float[] mPosition, float scale) {
        super(context, "icosahedron.obj", "icosahedron.mtl", 0.7f, 0f, true, 1, mPosition, new float[]{0f, 0f, 0f}, new float[]{0f, 0f, 0f}, scale);
    }

    public void makeExplosion(Context context) {
        this.mExplosion = new Explosion(context, super.mPosition.clone(), super.diffColorBuffer, 1.5f, 0.05f);
    }

    public void addExplosion(List<Explosion> explosions) {
        explosions.add(this.mExplosion);
    }
}
