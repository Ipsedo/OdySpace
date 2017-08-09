package com.samuelberrien.odyspace.levels;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.Compass;
import com.samuelberrien.odyspace.drawable.Forest;
import com.samuelberrien.odyspace.drawable.ProgressBar;
import com.samuelberrien.odyspace.drawable.explosion.Explosion;
import com.samuelberrien.odyspace.drawable.maps.CubeMap;
import com.samuelberrien.odyspace.drawable.maps.NoiseMap;
import com.samuelberrien.odyspace.objects.baseitem.BaseItem;
import com.samuelberrien.odyspace.objects.baseitem.Icosahedron;
import com.samuelberrien.odyspace.objects.baseitem.shooters.Ship;
import com.samuelberrien.odyspace.utils.collision.Box;
import com.samuelberrien.odyspace.utils.collision.Octree;
import com.samuelberrien.odyspace.utils.game.Item;
import com.samuelberrien.odyspace.utils.game.Level;
import com.samuelberrien.odyspace.utils.graphics.Color;
import com.samuelberrien.odyspace.utils.maths.Vector;
import com.samuelberrien.odyspace.utils.sounds.SoundPoolBuilder;

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

	public static String NAME = "Practice";

	private Context context;

	private float levelLimitSize;
	private Box levelLimits;

	private Ship ship;
	//private HeightMap heightMap;
	private NoiseMap noiseMap;
	private Forest forest;
	private List<BaseItem> rockets;
	private CubeMap cubeMap;

	private List<BaseItem> icosahedrons;
	private int nbIcosahedron = 100;

	private List<Explosion> explosions;

	private boolean isInit = false;

	private ProgressBar currLevelProgression;
	private Compass compass;

	private SoundPoolBuilder soundPoolBuilder;

	@Override
	public void init(Context context, Ship ship, float levelLimitSize) {
		this.context = context;
		this.ship = ship;
		this.levelLimitSize = levelLimitSize;

		this.currLevelProgression = new ProgressBar(this.context, 50, -1f + 0.15f, 0.9f, Color.LevelProgressBarColor);

		float limitDown = -100f;
		//this.heightMap = new HeightMap(context, R.drawable.canyon_6_hm_2, R.drawable.canyon_6_tex_2, 0.025f, 0.8f, 3e-5f, levelLimitSize, limitDown);
		this.noiseMap = new NoiseMap(context, new float[]{0f, 177f / 255f, 106f / 255f, 1f}, 0.45f, 0f, 8, this.levelLimitSize, limitDown, 0.02f);
		this.noiseMap.update();
		this.forest = new Forest(this.context, "dead_tree.obj", "dead_tree.mtl", 100, this.noiseMap, this.levelLimitSize);
		this.levelLimits = new Box(-this.levelLimitSize, limitDown - 0.02f * this.levelLimitSize, -this.levelLimitSize, this.levelLimitSize * 2f, this.levelLimitSize, this.levelLimitSize * 2f);
		this.cubeMap = new CubeMap(this.context, this.levelLimitSize, "cube_map/ciel_1/");
		this.cubeMap.update();

		this.rockets = Collections.synchronizedList(new ArrayList<BaseItem>());
		this.icosahedrons = Collections.synchronizedList(new ArrayList<BaseItem>());
		this.explosions = Collections.synchronizedList(new ArrayList<Explosion>());

		this.ship.setRockets(this.rockets);

		Random rand = new Random(System.currentTimeMillis());
		//ObjModelMtlVBO modelIco = new ObjModelMtlVBO(this.context, "icosahedron.obj", "icosahedron.mtl", 1f, 0f, true);
		for (int i = 0; i < this.nbIcosahedron; i++) {
			Icosahedron ico = new Icosahedron(this.context, 1, new float[]{rand.nextFloat() * this.levelLimitSize - this.levelLimitSize / 2f, rand.nextFloat() * 100f - 50f, rand.nextFloat() * this.levelLimitSize - this.levelLimitSize / 2f}, rand.nextFloat() * 2f + 1f);
			ico.update();
			ico.makeExplosion();
			this.icosahedrons.add(ico);
		}

		this.ship.makeExplosion();

		this.soundPoolBuilder = new SoundPoolBuilder(this.context);

		this.compass = new Compass(this.context, this.levelLimitSize / 12f);

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
		this.noiseMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, new float[0]);
		this.forest.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		this.cubeMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, new float[0]);
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
		ArrayList<BaseItem> icos = new ArrayList<>(this.icosahedrons);
		for (BaseItem ico : icos) {
			this.compass.update(this.ship, ico, ico.isDanger());
			this.compass.draw(ratio);
		}
	}

	@Override
	public void update() {
		this.ship.update();

		ArrayList<BaseItem> tmpArr = new ArrayList<>(this.rockets);
		for (BaseItem r : tmpArr)
			r.update();
		ArrayList<Explosion> tmpArr2 = new ArrayList<>(this.explosions);
		for (Explosion e : tmpArr2)
			e.move();

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
		Octree octree = new Octree(this.levelLimits, ami, ennemi, 2f);
		octree.computeOctree();
	}

	@Override
	public boolean isInit() {
		return this.isInit;
	}

	private float getSoundLevel(BaseItem from) {
		return 1f - Vector.length3f(from.vector3fTo(this.ship)) / this.levelLimitSize;
	}

	@Override
	public void removeAddObjects() {
		for (int i = this.explosions.size() - 1; i >= 0; i--)
			if (!this.explosions.get(i).isAlive())
				this.explosions.remove(i);

		for (int i = this.icosahedrons.size() - 1; i >= 0; i--) {
			if (!this.icosahedrons.get(i).isAlive()) {
				Icosahedron ico = (Icosahedron) this.icosahedrons.get(i);
				ico.addExplosion(this.explosions);
				this.soundPoolBuilder.playSimpleBoom(this.getSoundLevel(ico), this.getSoundLevel(ico));
				this.icosahedrons.remove(i);
			} else if (!this.icosahedrons.get(i).isInside(this.levelLimits))
				this.icosahedrons.remove(i);
		}

		for (int i = this.rockets.size() - 1; i >= 0; i--)
			if (!this.rockets.get(i).isAlive() || !this.rockets.get(i).isInside(this.levelLimits))
				this.rockets.remove(i);

		if (!this.ship.isAlive() || !this.ship.isInside(this.levelLimits))
			this.ship.addExplosion(this.explosions);

		this.ship.fire();
	}

	@Override
	public int getScore() {
		return this.nbIcosahedron - this.icosahedrons.size();
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
