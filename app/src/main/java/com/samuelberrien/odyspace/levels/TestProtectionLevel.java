package com.samuelberrien.odyspace.levels;

import android.content.Context;
import android.media.MediaPlayer;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.drawable.Forest;
import com.samuelberrien.odyspace.drawable.ProgressBar;
import com.samuelberrien.odyspace.drawable.controls.Controls;
import com.samuelberrien.odyspace.drawable.controls.Joystick;
import com.samuelberrien.odyspace.drawable.maps.CubeMap;
import com.samuelberrien.odyspace.drawable.maps.NoiseMap;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.objects.BaseItem;
import com.samuelberrien.odyspace.objects.Icosahedron;
import com.samuelberrien.odyspace.objects.Ship;
import com.samuelberrien.odyspace.utils.collision.Box;
import com.samuelberrien.odyspace.utils.collision.Octree;
import com.samuelberrien.odyspace.utils.game.Item;
import com.samuelberrien.odyspace.utils.game.Level;
import com.samuelberrien.odyspace.utils.graphics.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by samuel on 20/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class TestProtectionLevel implements Level {

    private Context context;

    private Ship ship;
    private ObjModelMtlVBO icosahedron;
    private List<BaseItem> icosahedrons;
    private List<Explosion> explosions;
    private List<BaseItem> rockets;

    private float levelLimitSize;
    private Box levelLimits;
    private CubeMap cubeMap;
    private NoiseMap noiseMap;
    private Forest forest;

    private Joystick joystick;
    private Controls controls;

    private boolean isInit = false;

    private long startTime;

    private ProgressBar currLevelProgression;

    private Random rand;

    private MediaPlayer mediaPlayer;

    @Override
    public void init(Context context, Ship ship, float levelLimitSize, Joystick joystick, Controls controls) {
        this.context = context;
        this.ship = ship;
        this.joystick = joystick;
        this.controls = controls;

        float limitDown = -100f;
        this.noiseMap = new NoiseMap(context, new float[]{0f, 177f / 255f, 106f / 255f, 1f}, 0.45f, 0f, 8, levelLimitSize, limitDown, 0.02f);
        this.noiseMap.update();
        this.forest = new Forest(this.context, "dead_tree.obj", "dead_tree.mtl", 100, this.noiseMap, levelLimitSize);
        this.levelLimits = new Box(-levelLimitSize, limitDown - 0.02f * levelLimitSize, -levelLimitSize, levelLimitSize * 2f, levelLimitSize / 2f, levelLimitSize * 2f);
        this.cubeMap = new CubeMap(this.context, levelLimitSize, "cube_map/ciel_1/");
        this.cubeMap.update();
        this.levelLimitSize = levelLimitSize;

        this.rockets = Collections.synchronizedList(new ArrayList<BaseItem>());
        this.icosahedrons = Collections.synchronizedList(new ArrayList<BaseItem>());
        this.explosions = Collections.synchronizedList(new ArrayList<Explosion>());

        this.icosahedron = new ObjModelMtlVBO(this.context, "icosahedron.obj", "icosahedron.mtl", 1f, 0f, true);

        this.startTime = System.currentTimeMillis();

        this.rand = new Random(this.startTime);

        this.currLevelProgression = new ProgressBar(this.context, 1000 * 60 * 2, -1f + 0.15f, 0.9f, Color.LevelProgressBarColor);

        this.mediaPlayer = MediaPlayer.create(context, R.raw.simple_boom);

        this.isInit = true;
    }

    @Override
    public float[] getLightPos() {
        return new float[]{0f, 250f, 0f};
    }

    @Override
    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
        this.ship.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        this.noiseMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace);
        this.forest.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        this.cubeMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace);
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

        tmpArr.clear();
        tmpArr.addAll(this.icosahedrons);
        for(BaseItem i : tmpArr)
            i.move();

        this.currLevelProgression.updateProgress((int) (System.currentTimeMillis() - this.startTime));
    }

    @Override
    public void collide() {
        ArrayList<Item> ami = new ArrayList<>();
        ami.addAll(this.rockets);
        ami.add(this.ship);
        ArrayList<Item> ennemi = new ArrayList<>();
        ennemi.addAll(this.icosahedrons);
        ennemi.add(this.noiseMap);
        Octree octree = new Octree(this.levelLimits, ami, ennemi, 1f);
        octree.computeOctree();
    }

    @Override
    public boolean isInit() {
        return this.isInit;
    }

    @Override
    public void removeAddObjects() {
        for (int i = this.explosions.size() - 1; i >= 0; i--)
            if (!this.explosions.get(i).isAlive())
                this.explosions.remove(i);

        for (int i = this.icosahedrons.size() - 1; i >= 0; i--) {
            if (!this.icosahedrons.get(i).isAlive()) {
                Icosahedron ico = (Icosahedron) this.icosahedrons.get(i);
                ico.makeExplosion();
                ico.addExplosion(this.explosions);
                this.mediaPlayer.start();
                this.icosahedrons.remove(i);
            } else if (!this.icosahedrons.get(i).isInside(this.levelLimits))
                this.icosahedrons.remove(i);
        }

        if(this.rand.nextInt(10) == 1) {
            Icosahedron tmp = new Icosahedron(this.context, this.icosahedron, new float[]{this.rand.nextFloat() * this.levelLimitSize * 2f - this.levelLimitSize, this.rand.nextFloat() * 0.3f * this.levelLimitSize + 50f, this.rand.nextFloat() * this.levelLimitSize * 2f - this.levelLimitSize}, new float[]{this.rand.nextFloat() * 0.5f - 0.25f, -this.rand.nextFloat() * 0.1f, this.rand.nextFloat() * 0.5f - 0.25f}, this.rand.nextFloat() * 2f + 1f);
            tmp.move();
            this.icosahedrons.add(tmp);
        }

        for (int i = this.rockets.size() - 1; i >= 0; i--)
            if (!this.rockets.get(i).isAlive() || !this.rockets.get(i).isInside(this.levelLimits))
                this.rockets.remove(i);

        if (!this.ship.isAlive() || !this.ship.isInside(this.levelLimits))
            this.ship.addExplosion(this.explosions);
    }

    @Override
    public int getScore() {
        return 0;
    }

    @Override
    public boolean isDead() {
        return !this.ship.isAlive() || !this.ship.isInside(this.levelLimits);
    }

    @Override
    public boolean isWinner() {
        return System.currentTimeMillis() - this.startTime > 1000L * 60L * 2L;
    }
}
