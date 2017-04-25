package com.samuelberrien.odyspace.levels;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.Controls;
import com.samuelberrien.odyspace.drawable.Explosion;
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

    Context context;

    private Ship ship;
    private HeightMap heightMap;
    private ArrayList<Rocket> rockets;

    private ArrayList<Icosahedron> icosahedrons;
    private int nbIcosahedron = 50;

    private ArrayList<Explosion> explosions;

    @Override
    public void init(Context context, Ship ship, HeightMap heightMap) {
        this.context = context;
        this.ship = ship;
        this.heightMap = heightMap;
        this.rockets = new ArrayList<>();
        this.icosahedrons = new ArrayList<>();
        this.explosions = new ArrayList<>();
        Random rand = new Random(System.currentTimeMillis());
        for(int i = 0; i < this.nbIcosahedron; i++){
            Icosahedron ico = new Icosahedron(this.context, new float[]{rand.nextFloat() * 250f - 125f, rand.nextFloat() * 100f - 50f, rand.nextFloat() * 250f - 125f});
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
            i.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        for(Explosion e : this.explosions)
            e.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
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
        for(Rocket r : this.rockets) {
            r.move();
        }
        for(Icosahedron i : this.icosahedrons) {
            i.move();
        }
        for(Explosion e : this.explosions) {
            e.move();
        }

    }

    @Override
    public void removeObjects() {
       for(int i = 0; i < this.icosahedrons.size(); i++){
           for(int j = 0; j < this.rockets.size(); j++){
               if(this.icosahedrons.get(i).isCollided(this.rockets.get(j))){
                   this.icosahedrons.get(i).decrementsBothLife(this.rockets.get(j));
                   if(!this.icosahedrons.get(i).isAlive()){
                       this.explosions.add(new Explosion(this.context, this.icosahedrons.get(i).getPosition().clone()));
                       this.icosahedrons.remove(i);
                   }
                   if(!this.rockets.get(j).isAlive()){
                       this.rockets.remove(this.rockets.get(j));
                   }
               }
           }
       }
       for(Explosion e : this.explosions){
           if(!e.isAlive()){
               this.explosions.remove(e);
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
