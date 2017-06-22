package com.samuelberrien.odyspace.levels;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.drawable.ProgressBar;
import com.samuelberrien.odyspace.drawable.controls.Controls;
import com.samuelberrien.odyspace.drawable.controls.Joystick;
import com.samuelberrien.odyspace.drawable.maps.CubeMap;
import com.samuelberrien.odyspace.drawable.maps.Map;
import com.samuelberrien.odyspace.drawable.maps.NoiseMap;
import com.samuelberrien.odyspace.objects.BaseItem;
import com.samuelberrien.odyspace.objects.Ship;
import com.samuelberrien.odyspace.objects.Turret;
import com.samuelberrien.odyspace.utils.collision.Box;
import com.samuelberrien.odyspace.utils.collision.Octree;
import com.samuelberrien.odyspace.utils.game.FireType;
import com.samuelberrien.odyspace.utils.game.Item;
import com.samuelberrien.odyspace.utils.game.Level;
import com.samuelberrien.odyspace.utils.graphics.Color;
import com.samuelberrien.odyspace.utils.maths.Triangle;

import java.io.IOException;
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

    private Box levelLimits;
    private NoiseMap noiseMap;
    private CubeMap cubeMap;
    private List<BaseItem> rocketsShip;
    private int nbTurret = 40;
    private List<BaseItem> turrets;
    private List<BaseItem> rocketsTurret;
    private List<Explosion> explosions;

    private Joystick joystick;
    private Controls controls;

    private ProgressBar currLevelProgression;

    private SoundPool mSounds;
    private int soundId;

    @Override
    public void init(Context context, Ship ship, float levelLimitSize, Joystick joystick, Controls controls) {
        this.context = context;
        this.ship = ship;
        this.ship.makeExplosion();

        this.joystick = joystick;
        this.controls = controls;

        this.currLevelProgression = new ProgressBar(this.context, 20, -1f + 0.15f, 0.9f, Color.LevelProgressBarColor);

        float limitDown = -100f;
        this.noiseMap = new NoiseMap(context, new float[]{0f, 177f / 255f, 106f / 255f, 1f}, 0.45f, 0f, 6, levelLimitSize, limitDown, 0.03f);
        this.noiseMap.update();
        this.levelLimits = new Box(-levelLimitSize, limitDown - 0.03f * levelLimitSize, -levelLimitSize, levelLimitSize * 2f, levelLimitSize / 2f, levelLimitSize * 2f);
        this.cubeMap = new CubeMap(this.context, levelLimitSize, "cube_map/ciel_2/");
        this.cubeMap.update();

        this.rocketsShip = Collections.synchronizedList(new ArrayList<BaseItem>());
        this.rocketsTurret = Collections.synchronizedList(new ArrayList<BaseItem>());
        this.explosions = Collections.synchronizedList(new ArrayList<Explosion>());
        this.turrets = Collections.synchronizedList(new ArrayList<BaseItem>());

        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < this.nbTurret; i++) {
            float x = rand.nextFloat() * levelLimitSize - levelLimitSize / 2f;
            float z = rand.nextFloat() * levelLimitSize - levelLimitSize / 2f;

            float[] triangles = this.noiseMap.passToModelMatrix(this.noiseMap.getRestreintArea(new float[]{x, 0f, z}));
            float moy = Triangle.CalcY(new float[]{triangles[0], triangles[1], triangles[2]}, new float[]{triangles[3], triangles[4], triangles[5]}, new float[]{triangles[6], triangles[7], triangles[8]}, x, z) / 2f;
            moy += Triangle.CalcY(new float[]{triangles[9], triangles[10], triangles[11]}, new float[]{triangles[12], triangles[13], triangles[14]}, new float[]{triangles[15], triangles[16], triangles[17]}, x, z) / 2f;

            Turret tmp = new Turret(this.context, new float[]{x, moy + 3f, z}, FireType.SIMPLE_FIRE);
            tmp.move(this.ship);
            tmp.makeExplosion(this.context);
            this.turrets.add(tmp);
        }

        this.mSounds = new SoundPool.Builder().setMaxStreams(20)
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build())
                .build();
        this.soundId = this.mSounds.load(this.context, R.raw.simple_boom, 1);

        this.isInit = true;
    }

    @Override
    public float[] getLightPos() {
        return new float[]{0f, 250f, 0f};
    }

    @Override
    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
        this.noiseMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace);
        this.ship.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        this.cubeMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace);
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
        this.currLevelProgression.draw(ratio);
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
            ((Turret) t).move(this.ship);
            ((Turret) t).fire(this.rocketsTurret, this.ship);
        }
        ArrayList<Explosion> tmpArr2 = new ArrayList<>(this.explosions);
        for (Explosion e : tmpArr2)
            e.move();

        this.currLevelProgression.updateProgress(this.nbTurret - this.turrets.size());
    }

    @Override
    public void collide() {
        ArrayList<Item> ami = new ArrayList<>();
        ami.addAll(this.rocketsShip);
        ami.add(this.ship);
        ArrayList<Item> ennemi = new ArrayList<>();
        ennemi.addAll(this.rocketsTurret);
        ennemi.addAll(this.turrets);
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
        for (int i = this.turrets.size() - 1; i >= 0; i--)
            if (!this.turrets.get(i).isAlive()) {
                ((Turret) this.turrets.get(i)).addExplosion(this.explosions);
                this.mSounds.play(this.soundId, 1f, 1f, 1, 0, 1f);
                this.turrets.remove(i);
            }
        for (int i = this.rocketsShip.size() - 1; i >= 0; i--)
            if (!this.rocketsShip.get(i).isAlive() || !this.rocketsShip.get(i).isInside(this.levelLimits))
                this.rocketsShip.remove(i);
        for (int i = this.rocketsTurret.size() - 1; i >= 0; i--)
            if (!this.rocketsTurret.get(i).isAlive() || !this.rocketsTurret.get(i).isInside(this.levelLimits))
                this.rocketsTurret.remove(i);
        if (!this.ship.isAlive() || !this.ship.isInside(this.levelLimits))
            this.ship.addExplosion(this.explosions);
    }

    @Override
    public int getScore() {
        return (this.nbTurret - this.turrets.size()) * 10;
    }

    @Override
    public boolean isDead() {
        return !this.ship.isAlive() || !this.ship.isInside(this.levelLimits);
    }

    @Override
    public boolean isWinner() {
        return this.nbTurret - this.turrets.size() > 19;
    }
}
