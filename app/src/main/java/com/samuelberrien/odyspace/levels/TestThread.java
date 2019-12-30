package com.samuelberrien.odyspace.levels;

import android.content.Context;

import com.samuelberrien.odyspace.core.Item;
import com.samuelberrien.odyspace.core.Level;
import com.samuelberrien.odyspace.core.baseitem.BaseItem;
import com.samuelberrien.odyspace.core.baseitem.Icosahedron;
import com.samuelberrien.odyspace.core.baseitem.Ship;
import com.samuelberrien.odyspace.core.collision.Box;
import com.samuelberrien.odyspace.core.collision.Octree;
import com.samuelberrien.odyspace.drawable.Compass;
import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.drawable.Forest;
import com.samuelberrien.odyspace.drawable.ProgressBar;
import com.samuelberrien.odyspace.drawable.maps.CubeMap;
import com.samuelberrien.odyspace.drawable.maps.NoiseMap;
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

	public TestThread() {
		isInit = false;
		levelLimitSize = 500f;
	}

	@Override
	public void init(Context context, Ship ship) {
		this.ship = ship;

		currLevelProgression = new ProgressBar(context, 50, -1f + 0.15f, 0.9f,
				Color.LevelProgressBarColor);

		float limitDown = -100f;
		//heightMap = new HeightMap(glContext, R.drawable.canyon_6_hm_2, R.drawable.canyon_6_tex_2, 0.025f, 0.8f, 3e-5f, levelLimitSize, limitDown);
		noiseMap = new NoiseMap(context,
				new float[]{0f, 177f / 255f, 106f / 255f, 1f},
				0.45f, 0f, 8, this.levelLimitSize, limitDown, 0.02f);
		noiseMap.update();
		forest = new Forest(context,
				"obj/dead_tree.obj", "obj/dead_tree.mtl",
				100, noiseMap, levelLimitSize);
		levelLimits = new Box(-levelLimitSize,
				limitDown - 0.02f * levelLimitSize,
				-levelLimitSize,
				levelLimitSize * 2f,
				levelLimitSize,
				levelLimitSize * 2f);
		cubeMap = new CubeMap(context, levelLimitSize, "cube_map/ciel_1/");
		cubeMap.update();

		rockets = Collections.synchronizedList(new ArrayList<BaseItem>());
		icosahedrons = Collections.synchronizedList(new ArrayList<BaseItem>());
		explosions = Collections.synchronizedList(new ArrayList<Explosion>());

		this.ship.setRockets(rockets);

		Random rand = new Random(System.currentTimeMillis());
		//ObjModelMtlVBO modelIco = new ObjModelMtlVBO(glContext, "icosahedron.obj", "icosahedron.mtl", 1f, 0f, true);
		for (int i = 0; i < nbIcosahedron; i++) {
			Icosahedron ico = new Icosahedron(context, 1,
					new float[]{rand.nextFloat() * this.levelLimitSize - this.levelLimitSize / 2f,
							rand.nextFloat() * 100f - 50f,
							rand.nextFloat() * this.levelLimitSize - this.levelLimitSize / 2f},
					rand.nextFloat() * 2f + 1f);
			ico.update();
			ico.queueExplosion();
			icosahedrons.add(ico);
		}

		this.ship.queueExplosion();

		soundPoolBuilder = new SoundPoolBuilder(context);

		compass = new Compass(context, this.levelLimitSize / 12f);

		isInit = true;
	}

	@Override
	public float[] getLightPos() {
		return new float[]{0f, 250f, 0f};
	}

	@Override
	public void draw(float[] mProjectionMatrix,
					 float[] mViewMatrix,
					 float[] mLightPosInEyeSpace,
					 float[] mCameraPosition) {
		ship.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		//heightMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace);
		noiseMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, new float[0]);
		forest.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		cubeMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, new float[0]);
		ArrayList<BaseItem> tmp = new ArrayList<>(rockets);
		for (BaseItem r : tmp)
			r.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		tmp = new ArrayList<>(icosahedrons);
		for (BaseItem i : tmp)
			i.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		ArrayList<Explosion> tmp2 = new ArrayList<>(explosions);
		for (Explosion e : tmp2)
			e.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
	}

	@Override
	public void drawLevelInfo(float ratio) {
		currLevelProgression.draw(ratio);
		ArrayList<BaseItem> icos = new ArrayList<>(icosahedrons);
		for (BaseItem ico : icos) {
			compass.update(ship, ico, ico.isDanger());
			compass.draw(ratio);
		}
	}

	@Override
	public void update() {
		ship.update();

		ArrayList<BaseItem> tmpArr = new ArrayList<>(rockets);
		for (BaseItem r : tmpArr)
			r.update();
		ArrayList<Explosion> tmpArr2 = new ArrayList<>(explosions);
		for (Explosion e : tmpArr2)
			e.move();

		currLevelProgression.updateProgress(nbIcosahedron - icosahedrons.size());
	}

	@Override
	public void collide() {
		ArrayList<Item> ami = new ArrayList<>();
		ami.addAll(rockets);
		ami.add(ship);
		ArrayList<Item> ennemi = new ArrayList<>();
		ennemi.addAll(icosahedrons);
		ennemi.add(noiseMap);
		Octree octree = new Octree(levelLimits, ami, ennemi, 2f);
		octree.computeOctree();
	}

	@Override
	public boolean isInit() {
		return isInit;
	}

	private float getSoundLevel(BaseItem from) {
		return 1f - Vector.length3f(from.vector3fTo(ship)) / levelLimitSize;
	}

	@Override
	public void removeAddObjects() {
		for (int i = explosions.size() - 1; i >= 0; i--)
			if (!explosions.get(i).isAlive())
				explosions.remove(i);

		for (int i = icosahedrons.size() - 1; i >= 0; i--) {
			if (!icosahedrons.get(i).isAlive()) {
				Icosahedron ico = (Icosahedron) icosahedrons.get(i);
				ico.addExplosion(explosions);
				soundPoolBuilder.playSimpleBoom(getSoundLevel(ico), getSoundLevel(ico));
				icosahedrons.remove(i);
			} else if (!icosahedrons.get(i).isInside(levelLimits))
				icosahedrons.remove(i);
		}

		for (int i = rockets.size() - 1; i >= 0; i--)
			if (!rockets.get(i).isAlive() || !rockets.get(i).isInside(levelLimits))
				rockets.remove(i);

		if (!ship.isAlive() || !ship.isInside(levelLimits))
			ship.addExplosion(explosions);

		ship.fire();
	}

	@Override
	public int getScore() {
		return nbIcosahedron - icosahedrons.size();
	}

	@Override
	public boolean isDead() {
		return !ship.isAlive() || !ship.isInside(levelLimits);
	}

	@Override
	public boolean isWinner() {
		return nbIcosahedron - icosahedrons.size() > 49;
	}

	@Override
	public float getMaxProjection() {
		return levelLimitSize * 3f;
	}

	@Override
	public String toString() {
		return NAME;
	}
}
