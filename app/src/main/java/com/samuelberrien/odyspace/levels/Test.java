package com.samuelberrien.odyspace.levels;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.Controls;
import com.samuelberrien.odyspace.drawable.HeightMap;
import com.samuelberrien.odyspace.drawable.Joystick;
import com.samuelberrien.odyspace.objects.Icosahedron;
import com.samuelberrien.odyspace.objects.Rocket;
import com.samuelberrien.odyspace.objects.Ship;
import com.samuelberrien.odyspace.utils.Level;

import java.util.ArrayList;

/**
 * Created by samuel on 20/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Test implements Level {

    private Ship ship;
    private HeightMap heightMap;
    private ArrayList<Rocket> rockets;

    private Icosahedron icosahedron;
    private boolean isIcosahedronAlive = true;

    @Override
    public void init(Context context, Ship ship, HeightMap heightMap) {
        this.ship = ship;
        this.heightMap = heightMap;
        this.rockets = new ArrayList<>();
        this.icosahedron = new Icosahedron(context, new float[]{0f, 0f, 50f});
    }

    @Override
    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
        this.ship.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        this.heightMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace);
        for(Rocket r : this.rockets)
            r.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        if(this.isIcosahedronAlive)
            this.icosahedron.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
    }

    @Override
    public void update(Joystick joystick, Controls controls) {
        float[] tmp = joystick.getStickPosition();
        this.ship.updateMaxSpeed(controls.getBoost());
        this.ship.move(tmp[0], tmp[1]);
        this.icosahedron.move();
        if(controls.isFire()){
            this.ship.fire(this.rockets);
            controls.turnOffFire();
        }
        for(Rocket r : this.rockets) {
            if(r.isCollided(this.icosahedron)){
                this.isIcosahedronAlive = false;
            }
            r.move();
        }
    }

    @Override
    public boolean isDead() {
        return this.ship.isOutOfBound(this.heightMap.getLimitHeight());
    }

    @Override
    public boolean isWinner() {
        return false;
    }
}
