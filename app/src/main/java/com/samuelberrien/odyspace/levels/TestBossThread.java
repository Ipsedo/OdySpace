package com.samuelberrien.odyspace.levels;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.Compass;
import com.samuelberrien.odyspace.drawable.Forest;
import com.samuelberrien.odyspace.drawable.controls.Controls;
import com.samuelberrien.odyspace.drawable.controls.Joystick;
import com.samuelberrien.odyspace.drawable.maps.CubeMap;
import com.samuelberrien.odyspace.drawable.maps.Map;
import com.samuelberrien.odyspace.drawable.maps.NoiseMap;
import com.samuelberrien.odyspace.objects.BaseItem;
import com.samuelberrien.odyspace.objects.Boss;
import com.samuelberrien.odyspace.objects.Ship;
import com.samuelberrien.odyspace.utils.collision.Box;
import com.samuelberrien.odyspace.utils.collision.Octree;
import com.samuelberrien.odyspace.utils.game.BossMoveType;
import com.samuelberrien.odyspace.utils.game.FireType;
import com.samuelberrien.odyspace.utils.game.Item;
import com.samuelberrien.odyspace.utils.game.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by samuel on 09/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class TestBossThread implements Level {

    private Context context;

    private Ship ship;
    private Box levelLimits;
    //private HeightMap heightMap;
    private CubeMap cubeMap;
    private NoiseMap noiseMap;
    private Boss boss;
    private List<BaseItem> rocketsShip;
    private List<BaseItem> rocketsBoss;
    private boolean isInit = false;
    private Joystick joystick;
    private Controls controls;
    private Compass compass;
    private Forest forest;

    @Override
    public void init(Context context, Ship ship, float levelLimitSize, Joystick joystick, Controls controls) {
        this.context = context;
        this.ship = ship;
        float limitDown = -100f;
        //this.heightMap = new HeightMap(context, R.drawable.canyon_6_hm_2, R.drawable.canyon_6_tex_2, 0.025f, 0.8f, 3e-5f, levelLimitSize, -100f);
        this.noiseMap = new NoiseMap(context, new float[]{161f / 255f, 37f / 255f, 27f / 255f, 1f}, 0.45f, 0f, 8, levelLimitSize, limitDown, 0.02f);
        this.noiseMap.update();
        this.forest = new Forest(this.context, "dead_tree.obj", "dead_tree.mtl", 100, this.noiseMap, levelLimitSize);
        this.levelLimits = new Box(-levelLimitSize / 2f, limitDown - 0.02f * levelLimitSize, - levelLimitSize / 2f, levelLimitSize, levelLimitSize / 2f, levelLimitSize);
        this.cubeMap = new CubeMap(this.context, levelLimitSize, "cube_map/ciel_rouge/");
        this.cubeMap.update();
        this.boss = new Boss(this.context, "skull.obj", "skull.mtl", 20, new float[]{0f, 0f, 50f}, 3f, FireType.SIMPLE_FIRE, BossMoveType.NAIF);
        this.rocketsShip = Collections.synchronizedList(new ArrayList<BaseItem>());
        this.rocketsBoss = Collections.synchronizedList(new ArrayList<BaseItem>());
        this.joystick = joystick;
        this.controls = controls;
        this.compass = new Compass(this.context);
        this.isInit = true;
    }

    @Override
    public float[] getLightPos() {
        return new float[]{0f, 250f, 0f};
    }

    @Override
    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
        this.ship.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        ArrayList<BaseItem> tmp = new ArrayList<>();
        tmp.addAll(this.rocketsShip);
        for (BaseItem r : tmp)
            r.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        tmp = new ArrayList<>();
        tmp.addAll(this.rocketsBoss);
        for (BaseItem r : tmp)
            r.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        this.boss.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        this.noiseMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace);
        this.forest.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        this.cubeMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace);
        //this.heightMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace);
    }

    @Override
    public void drawLevelInfo(float ratio) {
        this.compass.draw(ratio);
    }

    @Override
    public void update() {
        if (this.ship.isAlive()) {
            this.ship.move(this.joystick, this.controls);
            this.ship.fire(this.controls, this.rocketsShip);
        }
        ArrayList<BaseItem> tmpArr = new ArrayList<>();
        tmpArr.addAll(this.rocketsShip);
        for (BaseItem r : tmpArr)
            r.move();
        tmpArr = new ArrayList<>();
        tmpArr.addAll(this.rocketsBoss);
        for (BaseItem r : tmpArr)
            r.move();
        this.boss.move(this.ship);
        this.boss.fire(this.rocketsBoss, this.ship);
        this.compass.update(this.ship, this.boss);
    }

    @Override
    public void collide() {
        ArrayList<Item> ami = new ArrayList<>();
        ami.addAll(this.rocketsShip);
        ami.add(this.ship);
        ArrayList<Item> ennemi = new ArrayList<>();
        ennemi.addAll(this.rocketsBoss);
        ennemi.add(this.boss);
        ennemi.add(this.noiseMap);
        Octree octree = new Octree(this.levelLimits, ami, ennemi, 1f);
        octree.computeOctree();
    }

    @Override
    public boolean isInit() {
        return this.isInit;
    }

    @Override
    public void removeObjects() {
        for (int i = this.rocketsShip.size() - 1; i >= 0; i--)
            if (!this.rocketsShip.get(i).isAlive() || !this.rocketsShip.get(i).isInside(this.levelLimits))
                this.rocketsShip.remove(i);
        for (int i = this.rocketsBoss.size() - 1; i >= 0; i--)
            if (!this.rocketsBoss.get(i).isAlive() || !this.rocketsBoss.get(i).isInside(this.levelLimits))
                this.rocketsBoss.remove(i);
    }

    @Override
    public int getScore() {
        return this.boss.isAlive() ? 0 : 100;
    }

    @Override
    public boolean isDead() {
        return !this.ship.isAlive() || !this.ship.isInside(this.levelLimits);
    }

    @Override
    public boolean isWinner() {
        return !this.boss.isAlive();
    }
}
