package com.samuelberrien.odyspace.levels;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.Compass;
import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.drawable.Forest;
import com.samuelberrien.odyspace.drawable.ProgressBar;
import com.samuelberrien.odyspace.drawable.controls.Controls;
import com.samuelberrien.odyspace.drawable.controls.Joystick;
import com.samuelberrien.odyspace.drawable.maps.CubeMap;
import com.samuelberrien.odyspace.drawable.maps.NoiseMap;
import com.samuelberrien.odyspace.drawable.obj.ObjModel;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.objects.Base;
import com.samuelberrien.odyspace.objects.BaseItem;
import com.samuelberrien.odyspace.objects.Icosahedron;
import com.samuelberrien.odyspace.objects.Ship;
import com.samuelberrien.odyspace.utils.collision.Box;
import com.samuelberrien.odyspace.utils.collision.Octree;
import com.samuelberrien.odyspace.utils.game.Item;
import com.samuelberrien.odyspace.utils.game.Level;
import com.samuelberrien.odyspace.utils.graphics.Color;
import com.samuelberrien.odyspace.utils.maths.Triangle;

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
    private ObjModel particule;
    private List<Explosion> explosions;
    private List<BaseItem> rockets;
    private Compass directionToIco;


    private int nbBase = 16;
    private ObjModelMtlVBO base;
    private List<BaseItem> bases;

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

    private SoundPool mSounds;
    private int simpleBoomSoundId;
    private int bigBoomSoundId;

    private long levelTime = 1000L * 60L * 3L;

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
        this.levelLimits = new Box(-levelLimitSize, limitDown - 0.02f * levelLimitSize - 100f, -levelLimitSize, levelLimitSize * 2f, levelLimitSize * 1.5f, levelLimitSize * 2f);
        this.cubeMap = new CubeMap(this.context, levelLimitSize, "cube_map/ciel_1/");
        this.cubeMap.update();
        this.levelLimitSize = levelLimitSize;

        this.rockets = Collections.synchronizedList(new ArrayList<BaseItem>());
        this.icosahedrons = Collections.synchronizedList(new ArrayList<BaseItem>());
        this.explosions = Collections.synchronizedList(new ArrayList<Explosion>());
        this.bases = Collections.synchronizedList(new ArrayList<BaseItem>());

        this.particule = new ObjModel(context, "triangle.obj", 1f, 1f, 1f, 1f, 0f, 1f);
        this.icosahedron = new ObjModelMtlVBO(this.context, "icosahedron.obj", "icosahedron.mtl", 1f, 0f, true);
        this.directionToIco = new Compass(this.context, Float.MAX_VALUE - 10.f);

        this.startTime = System.currentTimeMillis();

        this.rand = new Random(this.startTime);

        this.base = new ObjModelMtlVBO(this.context, "base.obj", "base.mtl", 1f, 0f, false);
        for (int i = 0; i < this.nbBase; i++) {
            float x = rand.nextFloat() * levelLimitSize - levelLimitSize / 2f;
            float z = rand.nextFloat() * levelLimitSize - levelLimitSize / 2f;

            float[] triangles = this.noiseMap.passToModelMatrix(this.noiseMap.getRestreintArea(new float[]{x, 0f, z}));
            float moy = Triangle.CalcY(new float[]{triangles[0], triangles[1], triangles[2]}, new float[]{triangles[3], triangles[4], triangles[5]}, new float[]{triangles[6], triangles[7], triangles[8]}, x, z) / 2f;
            moy += Triangle.CalcY(new float[]{triangles[9], triangles[10], triangles[11]}, new float[]{triangles[12], triangles[13], triangles[14]}, new float[]{triangles[15], triangles[16], triangles[17]}, x, z) / 2f;

            float[] pos = new float[]{x, moy + 5f, z};

            Base tmpBase = new Base(this.base, 1, pos, 15f);
            tmpBase.move();
            tmpBase.makeExplosion();
            this.bases.add(tmpBase);
        }

        this.currLevelProgression = new ProgressBar(this.context, (int) this.levelTime, -1f + 0.15f, 0.9f, Color.LevelProgressBarColor);

        if (Build.VERSION.SDK_INT >= 21 ) {
            this.mSounds = new SoundPool.Builder().setMaxStreams(20)
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build())
                    .build();
        } else {
            this.mSounds = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
        }

        this.simpleBoomSoundId = this.mSounds.load(this.context, R.raw.simple_boom, 1);
        this.bigBoomSoundId = this.mSounds.load(this.context, R.raw.big_boom, 1);

        this.ship.makeExplosion();

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
        tmp = new ArrayList<>(this.bases);
        for (BaseItem b : tmp)
            b.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
        ArrayList<Explosion> tmp2 = new ArrayList<>(this.explosions);
        for (Explosion e : tmp2)
            e.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
    }

    @Override
    public void drawLevelInfo(float ratio) {
        this.currLevelProgression.draw(ratio);
        ArrayList<BaseItem> icos = new ArrayList<>(this.icosahedrons);
        for (BaseItem ico : icos) {
            this.directionToIco.update(this.ship, ico);
            this.directionToIco.draw(ratio);
        }
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
        for (BaseItem i : tmpArr)
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
        ennemi.addAll(this.bases);
        Octree octree = new Octree(this.levelLimits, ami, ennemi, 30f);
        octree.computeOctree();

        ami.clear();
        ennemi.clear();
        ami.add(this.noiseMap);
        ami.addAll(this.bases);
        ennemi.addAll(this.icosahedrons);
        octree = new Octree(this.levelLimits, ami, ennemi, 30f);
        octree.computeOctree();
    }

    @Override
    public boolean isInit() {
        return this.isInit;
    }

    private float[] randomIcoPosition() {
        return new float[]{this.rand.nextFloat() * this.levelLimitSize * 2f - this.levelLimitSize, -100f - 0.02f * this.levelLimitSize + this.levelLimitSize + this.rand.nextFloat() * this.levelLimitSize * 0.5f, this.rand.nextFloat() * this.levelLimitSize * 2f - this.levelLimitSize};
    }

    private float[] randomIcoSpeed(float maxSpeed) {
        float[] speed = new float[3];

        double phi = Math.PI / 2d + this.rand.nextDouble() * Math.PI / 6d + 5d * Math.PI / 12d;
        double theta = this.rand.nextDouble() * Math.PI * 2d;

        speed[0] = maxSpeed * (float) (Math.cos(theta) * Math.sin(phi));
        speed[1] = maxSpeed * (float) Math.cos(phi);
        speed[2] = maxSpeed * (float) (Math.sin(phi) * Math.sin(theta));

        return speed;
    }

    @Override
    public void removeAddObjects() {
        for (int i = this.explosions.size() - 1; i >= 0; i--)
            if (!this.explosions.get(i).isAlive())
                this.explosions.remove(i);

        for (int i = this.icosahedrons.size() - 1; i >= 0; i--) {
            if (!this.icosahedrons.get(i).isAlive()) {
                Icosahedron ico = (Icosahedron) this.icosahedrons.get(i);
                ico.makeExplosion(this.particule);
                ico.addExplosion(this.explosions);
                this.mSounds.play(this.simpleBoomSoundId, 1f, 1f, 1, 0, 1f);
                this.icosahedrons.remove(i);
            } else if (!this.icosahedrons.get(i).isInside(this.levelLimits))
                this.icosahedrons.remove(i);
        }

        if (this.rand.nextFloat() < 0.06f) {
            Icosahedron tmp = new Icosahedron(this.icosahedron, this.randomIcoPosition(), this.randomIcoSpeed(this.rand.nextFloat() * 0.1f + 0.2f), this.rand.nextFloat() * 10f + 10f);
            tmp.move();
            this.icosahedrons.add(tmp);
        }

        for (int i = this.rockets.size() - 1; i >= 0; i--)
            if (!this.rockets.get(i).isAlive() || !this.rockets.get(i).isInside(this.levelLimits))
                this.rockets.remove(i);

        if (!this.ship.isAlive() || !this.ship.isInside(this.levelLimits))
            this.ship.addExplosion(this.explosions);

        for (int i = this.bases.size() - 1; i >= 0; i--)
            if (!this.bases.get(i).isAlive()) {
                this.mSounds.play(this.bigBoomSoundId, 1f, 1f, 1, 0, 1f);
                ((Base) this.bases.get(i)).addExplosion(this.explosions);
                this.bases.remove(i);
            }
    }

    @Override
    public int getScore() {
        return 100;
    }

    @Override
    public boolean isDead() {
        return !this.ship.isAlive() || !this.ship.isInside(this.levelLimits) || this.nbBase - this.bases.size() != 0;
    }

    @Override
    public boolean isWinner() {
        return System.currentTimeMillis() - this.startTime > this.levelTime;
    }
}
