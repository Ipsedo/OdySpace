package com.samuelberrien.odyspace.levels;

import com.samuelberrien.odyspace.drawable.Controls;
import com.samuelberrien.odyspace.drawable.HeightMap;
import com.samuelberrien.odyspace.drawable.Joystick;
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

    @Override
    public void init(Ship ship, HeightMap heightMap) {
        this.ship = ship;
        this.heightMap = heightMap;
        this.rockets = new ArrayList<>();
    }

    @Override
    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
        this.ship.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        this.heightMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace);
        for(Rocket r : this.rockets)
            r.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
    }

    @Override
    public void update(Joystick joystick, Controls controls) {
        float[] tmp = joystick.getStickPosition();
        this.ship.updateMaxSpeed(controls.getBoost());
        this.ship.move(tmp[0], tmp[1]);
        if(controls.isFire()){
            this.ship.fire(this.rockets);
            controls.turnOffFire();
        }
        for(Rocket r : this.rockets)
            r.move();
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
