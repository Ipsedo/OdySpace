package com.samuelberrien.odyspace.levels;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.drawable.controls.Controls;
import com.samuelberrien.odyspace.drawable.controls.Joystick;
import com.samuelberrien.odyspace.drawable.maps.CubeMap;
import com.samuelberrien.odyspace.drawable.maps.NoiseMap;
import com.samuelberrien.odyspace.objects.BaseItem;
import com.samuelberrien.odyspace.objects.Ship;
import com.samuelberrien.odyspace.objects.Turret;
import com.samuelberrien.odyspace.utils.collision.Octree;
import com.samuelberrien.odyspace.utils.game.Level;
import com.samuelberrien.odyspace.utils.game.LevelLimits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by samuel on 15/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class TestTurrets implements Level {

    private Context context;

    private boolean isInit = false;

    private Ship ship;

    private LevelLimits levelLimits;
    private NoiseMap noiseMap;
    private CubeMap cubeMap;
    private List<BaseItem> rocketsShip;
    private int nbTurret = 40;
    private List<BaseItem> turrets;
    private List<BaseItem> rocketsTurret;
    private List<Explosion> explosions;

    private Joystick joystick;
    private Controls controls;

    @Override
    public void init(Context context, Ship ship, float levelLimitSize, Joystick joystick, Controls controls) {
        this.context = context;
        this.ship = ship;
        this.ship.makeExplosion();

        this.joystick = joystick;
        this.controls = controls;

        float limitDown = -100f;
        this.noiseMap = new NoiseMap(context, new float[]{0f, 177f / 255f, 106f / 255f, 1f}, 0.45f, 0f, levelLimitSize, limitDown);
        this.noiseMap.update();
        this.levelLimits = new LevelLimits(levelLimitSize / 2f, -levelLimitSize / 2f, levelLimitSize + limitDown - 10, limitDown - 10, levelLimitSize / 2f, -levelLimitSize / 2f);
        this.cubeMap = new CubeMap(this.context, levelLimitSize, "cube_map/ciel_2/");

        this.rocketsShip = Collections.synchronizedList(new ArrayList<BaseItem>());
        this.rocketsTurret = Collections.synchronizedList(new ArrayList<BaseItem>());
        this.explosions = Collections.synchronizedList(new ArrayList<Explosion>());
        this.turrets = Collections.synchronizedList(new ArrayList<BaseItem>());

        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < this.nbTurret; i++) {
            float x = rand.nextFloat() * levelLimitSize - levelLimitSize / 2f;
            float z = rand.nextFloat() * levelLimitSize - levelLimitSize / 2f;

            float[] triangles = this.noiseMap.passToModelMatrix(this.noiseMap.getRestreintArea(new float[]{x, 0f, z}));
            float moy = 0;
            for (int j = 0; j < triangles.length; j += 3) {
                moy += triangles[j + 1];
            }
            moy /= (triangles.length / 3f);

            Turret tmp = new Turret(this.context, new float[]{x, moy, z});
            tmp.move(this.ship);
            tmp.makeExplosion(this.context);
            this.turrets.add(tmp);
        }

        this.isInit = true;

        System.out.println("INIT FINI");
    }

    @Override
    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
        this.noiseMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace);
        this.ship.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        this.cubeMap.draw(mProjectionMatrix, mViewMatrix);
        ArrayList<BaseItem> tmp = new ArrayList<>();
        tmp.addAll(this.rocketsShip);
        for (BaseItem r : tmp)
            r.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        tmp.clear();
        tmp.addAll(this.rocketsTurret);
        for (BaseItem r : tmp)
            r.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        tmp.clear();
        tmp.addAll(this.turrets);
        for (BaseItem t : tmp)
            t.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        ArrayList<Explosion> tmp2 = new ArrayList<>(this.explosions);
        for (Explosion e : tmp2)
            e.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
    }

    @Override
    public void drawLevelInfo(float ratio) {

    }

    @Override
    public void update() {
        if (this.ship.isAlive()) {
            this.ship.move(this.joystick, this.controls);
            this.ship.fire(this.controls, this.rocketsShip);
        }
        ArrayList<BaseItem> tmpArr = new ArrayList<>(this.rocketsShip);
        for (BaseItem r : tmpArr)
            r.move();
        tmpArr.clear();
        tmpArr.addAll(this.rocketsTurret);
        for (BaseItem r : tmpArr)
            r.move();
        tmpArr.clear();
        tmpArr.addAll(this.turrets);
        for (BaseItem t : tmpArr) {
            t.move();
            ((Turret) t).fire(this.rocketsTurret, this.ship);
        }
        ArrayList<Explosion> tmpArr2 = new ArrayList<>(this.explosions);
        for (Explosion e : tmpArr2)
            e.move();
        if (!this.ship.isAlive())
            this.ship.addExplosion(this.explosions);
    }

    @Override
    public void collide() {
        ArrayList<BaseItem> ami = new ArrayList<>(this.rocketsShip);
        ami.add(this.ship);
        ArrayList<BaseItem> ennemi = new ArrayList<>(this.rocketsTurret);
        ennemi.addAll(this.turrets);
        Octree octree = new Octree(this.levelLimits, null, ami, ennemi, 8f);
        octree.computeOctree();

        this.ship.mapCollision(this.noiseMap, this.levelLimits);
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
        for (int i = this.turrets.size() - 1; i >= 0; i--)
            if (!this.turrets.get(i).isAlive()) {
                ((Turret) this.turrets.get(i)).addExplosion(this.explosions);
                this.turrets.remove(i);
            }
        for (int i = this.rocketsShip.size() - 1; i >= 0; i--)
            if (!this.rocketsShip.get(i).isAlive() || this.rocketsShip.get(i).isOutOfBound(this.levelLimits))
                this.rocketsShip.remove(i);
        for (int i = this.rocketsTurret.size() - 1; i >= 0; i--)
            if (!this.rocketsTurret.get(i).isAlive() || this.rocketsTurret.get(i).isOutOfBound(this.levelLimits))
                this.rocketsTurret.remove(i);
    }

    @Override
    public int getScore() {
        return (this.nbTurret - this.turrets.size()) * 10;
    }

    @Override
    public boolean isDead() {
        return !this.ship.isAlive();
    }

    @Override
    public boolean isWinner() {
        return this.nbTurret - this.turrets.size() > 19;
    }
}
