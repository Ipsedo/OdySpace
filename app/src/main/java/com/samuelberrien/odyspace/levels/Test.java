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
import java.util.Random;

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

    private ArrayList<Icosahedron> icosahedrons;
    private int nbIcosahedron = 50;

    @Override
    public void init(Context context, Ship ship, HeightMap heightMap) {
        this.ship = ship;
        this.heightMap = heightMap;
        this.rockets = new ArrayList<>();
        this.icosahedrons = new ArrayList<>();
        Random rand = new Random(System.currentTimeMillis());
        for(int i = 0; i < this.nbIcosahedron; i++){
            Icosahedron ico = new Icosahedron(context, new float[]{rand.nextFloat() * 250f - 125f, rand.nextFloat() * 100f - 50f, rand.nextFloat() * 250f - 125f});
            ico.changeColor(rand);
            this.icosahedrons.add(ico);
        }
    }

    @Override
    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
        this.ship.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        this.heightMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace);
        for(Rocket r : this.rockets)
            r.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);

        for(Icosahedron i : this.icosahedrons)
            if(i.isAlive())
                i.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
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
        for(Icosahedron i : this.icosahedrons)
            i.move();
    }

    @Override
    public void collision() {
       for(int i = 0; i < this.icosahedrons.size(); i++){
           for(int j = 0; j < this.rockets.size(); j++){
               if(this.icosahedrons.get(i).isCollided(this.rockets.get(j))){
                   this.icosahedrons.remove(i);
                   this.rockets.remove(j);
               }
           }
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
