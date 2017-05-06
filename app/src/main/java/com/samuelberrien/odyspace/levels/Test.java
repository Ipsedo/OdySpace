package com.samuelberrien.odyspace.levels;

import android.content.Context;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.controls.Controls;
import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.drawable.HeightMap;
import com.samuelberrien.odyspace.drawable.controls.Joystick;
import com.samuelberrien.odyspace.objects.BaseItem;
import com.samuelberrien.odyspace.objects.Icosahedron;
import com.samuelberrien.odyspace.objects.Rocket;
import com.samuelberrien.odyspace.objects.Ship;
import com.samuelberrien.odyspace.utils.collision.Octree;
import com.samuelberrien.odyspace.utils.game.Level;
import com.samuelberrien.odyspace.utils.game.LevelLimits;

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

    private LevelLimits levelLimits;

    private Ship ship;
    private HeightMap heightMap;
    private ArrayList<BaseItem> rockets;

    private ArrayList<BaseItem> icosahedrons;
    private int nbIcosahedron = 100;

    private ArrayList<Explosion> explosions;

    private boolean isInit = false;

    @Override
    public void init(Context context, Ship ship, float levelLimitSize) {
        this.context = context;
        this.ship = ship;

        this.heightMap = new HeightMap(context, R.drawable.canyon_6_hm_2, R.drawable.canyon_6_tex_2, 0.025f, 0.8f, 3e-5f, levelLimitSize, -100f);
        this.levelLimits = new LevelLimits(levelLimitSize / 2f, -levelLimitSize / 2f, levelLimitSize / 2f, -100f, levelLimitSize / 2f, -levelLimitSize / 2f);

        this.rockets = new ArrayList<>();
        this.icosahedrons = new ArrayList<>();
        this.explosions = new ArrayList<>();

        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < this.nbIcosahedron; i++) {
            Icosahedron ico = new Icosahedron(this.context, new float[]{rand.nextFloat() * levelLimitSize / 4f - levelLimitSize / 8f, rand.nextFloat() * 100f - 50f, rand.nextFloat() * levelLimitSize / 4f - levelLimitSize / 8f}, rand);
            ico.move();
            ico.makeExplosion(this.context);
            this.icosahedrons.add(ico);
        }

        /*CollisionThread tmp = new CollisionThread();
        tmp.start();*/

        this.isInit = true;
    }

    @Override
    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
        this.ship.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        this.heightMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace);
        for (BaseItem r : this.rockets)
            r.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        for (BaseItem i : this.icosahedrons)
            i.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        for (Explosion e : this.explosions)
            e.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
    }

    @Override
    public void update(Joystick joystick, Controls controls) {
        float[] tmp = joystick.getStickPosition();
        this.ship.updateMaxSpeed(controls.getBoost());
        this.ship.move(tmp[0], tmp[1]);
        if (controls.isFire()) {
            this.ship.fire(this.rockets);
            controls.turnOffFire();
        }
        for (BaseItem r : this.rockets)
            r.move();
        for (Explosion e : this.explosions)
            e.move();
    }

    @Override
    public void removeObjects() {
        ArrayList<BaseItem> ami = new ArrayList<>(this.rockets);
        ami.add(this.ship);
        ArrayList<BaseItem> ennemi = new ArrayList<>(this.icosahedrons);
        Octree octree = new Octree(this.levelLimits, null, ami, ennemi, 8f);
        octree.computeOctree();

        for (int i = 0; i < this.explosions.size(); i++) {
            if (!this.explosions.get(i).isAlive()) {
                this.explosions.remove(i);
            }
        }
        for (int i = 0; i < this.icosahedrons.size(); i++) {
            if (!this.icosahedrons.get(i).isAlive()) {
                Icosahedron ico = (Icosahedron) this.icosahedrons.get(i);
                ico.addExplosion(this.explosions);
                this.icosahedrons.remove(i);
            } else if (this.icosahedrons.get(i).isOutOfBound(this.levelLimits)) {
                this.icosahedrons.remove(i);
            }
        }
        for (int i = 0; i < this.rockets.size(); i++)
            if (!this.rockets.get(i).isAlive() || this.rockets.get(i).isOutOfBound(this.levelLimits))
                this.rockets.remove(i);
    }

    @Override
    public boolean isDead() {
        if(this.isInit) {
            return this.ship.isOutOfBound(this.levelLimits) || !this.ship.isAlive();
        }
        return false;
    }

    @Override
    public boolean isWinner() {
        if(this.isInit) {
            return this.nbIcosahedron - this.icosahedrons.size() > 49;
        }
        return false;
    }

    private class CollisionThread extends Thread {

        public CollisionThread(){
            super("collsionThread");
        }

        public void run(){
            while(true) {
                synchronized (Test.this.ship) {
                    synchronized (Test.this.rockets) {
                        synchronized (Test.this.icosahedrons) {
                            ArrayList<BaseItem> ami = new ArrayList<>(Test.this.rockets);
                            ami.add(Test.this.ship);
                            ArrayList<BaseItem> ennemi = new ArrayList<>(Test.this.icosahedrons);
                            Octree octree = new Octree(Test.this.levelLimits, null, ami, ennemi, 8f);
                            octree.computeOctree();
                        }
                    }
                }
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
