package com.samuelberrien.odyspace.objects;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.ObjModelMtl;

/**
 * Created by samuel on 18/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Ship extends BaseItem {

    private float phi;
    private float theta;

    private final float maxSpeed = 0.01f;

    public Ship(Context context){
        super(context, "ship.obj", "ship.mtl", 1f, 0.01f, 100, new float[]{0f, 0f, 0f}, new float[]{0f, 0f, 0.01f}, new float[]{0f, 0f, 0f});
        this.phi = 0f;
        this.theta = 0f;
    }

    public void move(float phi, float theta){
        this.phi += phi;
        this.theta += theta;

        super.mSpeed[0] = maxSpeed * (float) (Math.cos(this.phi) * Math.sin(this.theta));
        super.mSpeed[1] = maxSpeed * (float) Math.sin(this.phi);
        super.mSpeed[2] = maxSpeed * (float) (Math.cos(this.phi) * Math.cos(this.theta));

        super.move();
    }
}
