package com.samuelberrien.odyspace.levels;

import android.content.Context;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.HeightMap;
import com.samuelberrien.odyspace.drawable.controls.Controls;
import com.samuelberrien.odyspace.drawable.controls.Joystick;
import com.samuelberrien.odyspace.objects.BaseItem;
import com.samuelberrien.odyspace.objects.Boss;
import com.samuelberrien.odyspace.objects.Ship;
import com.samuelberrien.odyspace.utils.collision.Octree;
import com.samuelberrien.odyspace.utils.game.Level;
import com.samuelberrien.odyspace.utils.game.LevelLimits;

import java.util.ArrayList;

/**
 * Created by samuel on 30/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class TestBoss implements Level {

    private Context context;

    private Ship ship;
    private LevelLimits levelLimits;
    private HeightMap heightMap;
    private Boss boss;
    private ArrayList<BaseItem> rocketsShip;
    private ArrayList<BaseItem> rocketsBoss;
    private boolean isInit = false;

    @Override
    public void init(Context context, Ship ship, float levelLimitSize) {
        this.context = context;
        this.ship = ship;
        this.heightMap = new HeightMap(context, R.drawable.canyon_6_hm_2, R.drawable.canyon_6_tex_2, 0.025f, 0.8f, 3e-5f, levelLimitSize, -100f);
        this.levelLimits = new LevelLimits(levelLimitSize / 2f, -levelLimitSize / 2f, levelLimitSize / 2f, -100f, levelLimitSize / 2f, -levelLimitSize / 2f);
        this.boss = new Boss(this.context, "skull.obj", "skull.mtl", 15, new float[]{0f, 0f, 50f});
        this.rocketsShip = new ArrayList<>();
        this.rocketsBoss = new ArrayList<>();
        this.isInit = true;
    }

    @Override
    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
        this.ship.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        for (BaseItem r : this.rocketsShip)
            r.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        for(BaseItem r : this.rocketsBoss)
            r.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        this.boss.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        this.heightMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace);
    }

    @Override
    public void update(Joystick joystick, Controls controls) {
        float[] tmp = joystick.getStickPosition();
        this.ship.updateMaxSpeed(controls.getBoost());
        this.ship.move(tmp[0], tmp[1]);
        if (controls.isFire()) {
            this.ship.fire(this.rocketsShip);
            controls.turnOffFire();
        }
        for (BaseItem r : this.rocketsShip)
            r.move();
        for(BaseItem r : this.rocketsBoss)
            r.move();
        this.boss.move(this.ship);
        this.boss.fire(this.rocketsBoss, this.ship);
    }

    @Override
    public void removeObjects() {
        ArrayList<BaseItem> ami = new ArrayList<>(this.rocketsShip);
        ami.add(this.ship);
        ArrayList<BaseItem> ennemi = new ArrayList<>(this.rocketsBoss);
        ennemi.add(this.boss);
        Octree octree = new Octree(this.levelLimits, null, ami, ennemi, 8f);
        octree.computeOctree();

        for (int i = 0; i < this.rocketsShip.size(); i++)
            if (!this.rocketsShip.get(i).isAlive() || this.rocketsShip.get(i).isOutOfBound(this.levelLimits))
                this.rocketsShip.remove(i);
        for(int i = 0; i < this.rocketsBoss.size(); i++)
            if(!this.rocketsBoss.get(i).isAlive() || this.rocketsBoss.get(i).isOutOfBound(this.levelLimits))
                this.rocketsBoss.remove(i);
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
            return !this.boss.isAlive();
        }
        return false;
    }
}