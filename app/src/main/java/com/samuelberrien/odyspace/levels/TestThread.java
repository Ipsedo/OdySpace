package com.samuelberrien.odyspace.levels;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.drawable.Forest;
import com.samuelberrien.odyspace.drawable.ProgressBar;
import com.samuelberrien.odyspace.drawable.controls.Controls;
import com.samuelberrien.odyspace.drawable.controls.Joystick;
import com.samuelberrien.odyspace.drawable.maps.CubeMap;
import com.samuelberrien.odyspace.drawable.maps.Map;
import com.samuelberrien.odyspace.drawable.maps.NoiseMap;
import com.samuelberrien.odyspace.objects.BaseItem;
import com.samuelberrien.odyspace.objects.Icosahedron;
import com.samuelberrien.odyspace.objects.Ship;
import com.samuelberrien.odyspace.utils.collision.Octree;
import com.samuelberrien.odyspace.utils.game.Item;
import com.samuelberrien.odyspace.utils.game.Level;
import com.samuelberrien.odyspace.utils.game.LevelLimits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by samuel on 09/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class TestThread implements Level {

    Context context;

    private LevelLimits levelLimits;

    private Ship ship;
    //private HeightMap heightMap;
    private Map noiseMap;
    private Forest forest;
    private List<BaseItem> rockets;
    private CubeMap cubeMap;

    private List<BaseItem> icosahedrons;
    private int nbIcosahedron = 100;

    private List<Explosion> explosions;

    private boolean isInit = false;

    private int score;

    private Joystick joystick;
    private Controls controls;

    private ProgressBar currLevelProgression;

    @Override
    public void init(Context context, Ship ship, float levelLimitSize, Joystick joystick, Controls controls) {
        this.context = context;
        this.ship = ship;
        this.ship.move(joystick, controls);

        this.currLevelProgression = new ProgressBar(this.context, 49, -1f + 0.15f, 0.9f, new float[]{38f / 255f, 166f / 255f, 91f / 255f, 1f});

        float limitDown = -100f;
        //this.heightMap = new HeightMap(context, R.drawable.canyon_6_hm_2, R.drawable.canyon_6_tex_2, 0.025f, 0.8f, 3e-5f, levelLimitSize, limitDown);
        this.noiseMap = new NoiseMap(context, new float[]{0f, 177f / 255f, 106f / 255f, 1f}, 0.45f, 0f, 8, levelLimitSize, limitDown, 0.02f);
        this.noiseMap.update();
        this.forest = new Forest(this.context, "dead_tree.obj", "dead_tree.mtl", 100, this.noiseMap, levelLimitSize);
        this.levelLimits = new LevelLimits(levelLimitSize / 2f, -levelLimitSize / 2f, levelLimitSize / 2f, limitDown - 0.02f * levelLimitSize, levelLimitSize / 2f, -levelLimitSize / 2f);
        this.cubeMap = new CubeMap(this.context, levelLimitSize, "cube_map/ciel_1/");

        this.rockets = Collections.synchronizedList(new ArrayList<BaseItem>());
        this.icosahedrons = Collections.synchronizedList(new ArrayList<BaseItem>());
        this.explosions = Collections.synchronizedList(new ArrayList<Explosion>());

        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < this.nbIcosahedron; i++) {
            Icosahedron ico = new Icosahedron(this.context, new float[]{rand.nextFloat() * levelLimitSize / 4f - levelLimitSize / 8f, rand.nextFloat() * 100f - 50f, rand.nextFloat() * levelLimitSize / 4f - levelLimitSize / 8f}, rand.nextFloat() * 2f + 1f);
            ico.move();
            ico.makeExplosion(this.context);
            this.icosahedrons.add(ico);
        }

        this.ship.makeExplosion();

        this.score = 0;

        this.joystick = joystick;
        this.controls = controls;

        this.isInit = true;
    }

    @Override
    public float[] getLightPos() {
        return new float[]{0f, 250f, 0f};
    }

    @Override
    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
        this.ship.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        //this.heightMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace);
        this.noiseMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace);
        this.forest.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        this.cubeMap.draw(mProjectionMatrix, mViewMatrix);
        ArrayList<BaseItem> tmp = new ArrayList<>(this.rockets);
        for (BaseItem r : tmp)
            r.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        tmp = new ArrayList<>(this.icosahedrons);
        for (BaseItem i : tmp)
            i.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        ArrayList<Explosion> tmp2 = new ArrayList<>(this.explosions);
        for (Explosion e : tmp2)
            e.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
    }

    @Override
    public void drawLevelInfo(float ratio) {
        this.currLevelProgression.draw(ratio);
    }

    @Override
    public void update() {
        if (this.ship.isAlive()) {
            this.ship.move(this.joystick, this.controls);
            this.ship.fire(this.controls, this.rockets);
        }
        ArrayList<BaseItem> tmpArr = new ArrayList<>(this.rockets);
        for (BaseItem r : tmpArr)
            r.move();
        ArrayList<Explosion> tmpArr2 = new ArrayList<>(this.explosions);
        for (Explosion e : tmpArr2)
            e.move();
        if (!this.ship.isAlive() || !this.ship.isInside(this.levelLimits))
            this.ship.addExplosion(this.explosions);
        this.currLevelProgression.updateProgress(this.nbIcosahedron - this.icosahedrons.size());
    }

    @Override
    public void collide() {
        ArrayList<Item> ami = new ArrayList<>();
        ami.addAll(this.rockets);
        ami.add(this.ship);
        ArrayList<Item> ennemi = new ArrayList<>();
        ennemi.addAll(this.icosahedrons);
        ennemi.add(this.noiseMap);
        Octree octree = new Octree(this.levelLimits, null, ami, ennemi, 1f);
        octree.computeOctree();
    }

    @Override
    public boolean isInit() {
        return this.isInit;
    }

    @Override
    public void removeObjects() {
        for (int i = this.explosions.size() - 1; i >= 0; i--)
            if (!this.explosions.get(i).isAlive())
                this.explosions.remove(i);

        for (int i = this.icosahedrons.size() - 1; i >= 0; i--) {
            if (!this.icosahedrons.get(i).isAlive()) {
                Icosahedron ico = (Icosahedron) this.icosahedrons.get(i);
                ico.addExplosion(this.explosions);
                ico.playExplosion();
                this.icosahedrons.remove(i);
                this.score++;
            } else if (!this.icosahedrons.get(i).isInside(this.levelLimits))
                this.icosahedrons.remove(i);
        }

        for (int i = this.rockets.size() - 1; i >= 0; i--)
            if (!this.rockets.get(i).isAlive() || !this.rockets.get(i).isInside(this.levelLimits))
                this.rockets.remove(i);
    }

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public boolean isDead() {
        return !this.ship.isAlive() || !this.ship.isInside(this.levelLimits);
    }

    @Override
    public boolean isWinner() {
        return this.nbIcosahedron - this.icosahedrons.size() > 49;
    }
}
