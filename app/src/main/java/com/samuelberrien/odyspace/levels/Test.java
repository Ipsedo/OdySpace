package com.samuelberrien.odyspace.levels;

import android.content.Context;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.controls.Controls;
import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.drawable.maps.HeightMap;
import com.samuelberrien.odyspace.drawable.controls.Joystick;
import com.samuelberrien.odyspace.objects.BaseItem;
import com.samuelberrien.odyspace.objects.Icosahedron;
import com.samuelberrien.odyspace.objects.Ship;
import com.samuelberrien.odyspace.utils.collision.Octree;
import com.samuelberrien.odyspace.utils.game.Level;
import com.samuelberrien.odyspace.utils.game.LevelLimits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private List<BaseItem> rockets;

    private List<BaseItem> icosahedrons;
    private int nbIcosahedron = 100;

    private List<Explosion> explosions;

    private boolean isInit = false;

    private int score;

    private Joystick joystick;
    private Controls controls;

    @Override
    public void init(Context context, Ship ship, float levelLimitSize, Joystick joystick, Controls controls) {
        this.context = context;
        this.ship = ship;

        this.heightMap = new HeightMap(context, R.drawable.canyon_6_hm_2, R.drawable.canyon_6_tex_2, 0.025f, 0.8f, 3e-5f, levelLimitSize, -100f);
        this.levelLimits = new LevelLimits(levelLimitSize / 2f, -levelLimitSize / 2f, levelLimitSize / 2f, -100f, levelLimitSize / 2f, -levelLimitSize / 2f);

        this.rockets = Collections.synchronizedList(new ArrayList<BaseItem>());
        this.icosahedrons = Collections.synchronizedList(new ArrayList<BaseItem>());
        this.explosions = Collections.synchronizedList(new ArrayList<Explosion>());

        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < this.nbIcosahedron; i++) {
            Icosahedron ico = new Icosahedron(this.context, new float[]{rand.nextFloat() * levelLimitSize / 4f - levelLimitSize / 8f, rand.nextFloat() * 100f - 50f, rand.nextFloat() * levelLimitSize / 4f - levelLimitSize / 8f}, rand, rand.nextFloat() * 2 + 1);
            ico.move();
            ico.makeExplosion(this.context);
            this.icosahedrons.add(ico);
        }


        this.score = 0;

        this.joystick = joystick;
        this.controls = controls;

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
    public void drawLevelInfo(float ratio) {

    }

    @Override
    public void update() {
        this.ship.move(this.joystick, this.controls);
        this.ship.fire(this.controls, this.rockets);
        for (BaseItem r : this.rockets)
            r.move();
        for (Explosion e : this.explosions)
            e.move();
    }

    @Override
    public void collide() {
        ArrayList<BaseItem> ami = new ArrayList<>(this.rockets);
        ami.add(this.ship);
        ArrayList<BaseItem> ennemi = new ArrayList<>(this.icosahedrons);
        Octree octree = new Octree(this.levelLimits, null, ami, ennemi, 8f);
        octree.computeOctree();
    }

    @Override
    public boolean isInit() {
        return this.isInit;
    }

    @Override
    public void removeObjects() {
        for (int i = this.explosions.size() - 1; i >= 0; i--) {
            if (!this.explosions.get(i).isAlive()) {
                this.explosions.remove(i);
            }

        }
        for (int i = this.icosahedrons.size() - 1; i >= 0; i--) {
            if (!this.icosahedrons.get(i).isAlive()) {
                Icosahedron ico = (Icosahedron) this.icosahedrons.get(i);
                ico.addExplosion(this.explosions);
                this.icosahedrons.remove(i);
                this.score++;
            } else if (this.icosahedrons.get(i).isOutOfBound(this.levelLimits)) {
                this.icosahedrons.remove(i);
            }
        }

        for (int i = this.rockets.size() - 1; i >= 0; i--)
            if (!this.rockets.get(i).isAlive() || this.rockets.get(i).isOutOfBound(this.levelLimits))
                this.rockets.remove(i);

    }

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public boolean isDead() {
        if (this.isInit) {
            return this.ship.isOutOfBound(this.levelLimits) || !this.ship.isAlive();
        }
        return false;
    }

    @Override
    public boolean isWinner() {
        if (this.isInit) {
            return this.nbIcosahedron - this.icosahedrons.size() > 49;
        }
        return false;
    }
}
