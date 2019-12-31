package com.samuelberrien.odyspace.levels;

import android.content.Context;

import com.samuelberrien.odyspace.core.Item;
import com.samuelberrien.odyspace.core.Level;
import com.samuelberrien.odyspace.core.baseitem.BaseItem;
import com.samuelberrien.odyspace.core.baseitem.boss.Boss;
import com.samuelberrien.odyspace.core.baseitem.boss.FstBoss;
import com.samuelberrien.odyspace.core.baseitem.ship.Ship;
import com.samuelberrien.odyspace.core.collision.Box;
import com.samuelberrien.odyspace.core.collision.Octree;
import com.samuelberrien.odyspace.drawable.Compass;
import com.samuelberrien.odyspace.drawable.Forest;
import com.samuelberrien.odyspace.drawable.ProgressBar;
import com.samuelberrien.odyspace.drawable.maps.CubeMap;
import com.samuelberrien.odyspace.drawable.maps.NoiseMap;
import com.samuelberrien.odyspace.utils.graphics.Color;

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

	public static String NAME = "The Master is here";

	private Context context;

	private Ship ship;
	private Box levelLimits;
	private CubeMap cubeMap;
	private NoiseMap noiseMap;
	private Boss boss;
	private List<BaseItem> rocketsShip;
	private List<BaseItem> rocketsBoss;
	private boolean isInit;
	private Compass compass;
	private Forest forest;
	private ProgressBar progressBar;
	private float levelLimitSize;

	public TestBossThread() {
		isInit = false;
		levelLimitSize = 500f;
	}

	@Override
	public void init(Context context, Ship currShip) {
		this.context = context;
		ship = currShip;
		float limitDown = -100f;
		//heightMap = new HeightMap(glContext, R.drawable.canyon_6_hm_2, R.drawable.canyon_6_tex_2, 0.025f, 0.8f, 3e-5f, levelLimitSize, -100f);
		noiseMap = new NoiseMap(context,
				new float[]{161f / 255f, 37f / 255f, 27f / 255f, 1f},
				0.45f, 0f, 8, levelLimitSize, limitDown, 0.02f);
		noiseMap.update();
		forest = new Forest(this.context,
				"obj/dead_tree.obj", "obj/dead_tree.mtl",
				100, noiseMap, levelLimitSize);
		levelLimits = new Box(-levelLimitSize,
				limitDown - 0.02f * levelLimitSize,
				-levelLimitSize,
				levelLimitSize * 2f,
				levelLimitSize,
				levelLimitSize * 2f);
		cubeMap = new CubeMap(this.context, levelLimitSize, "cube_map/ciel_rouge/");
		cubeMap.update();

		progressBar = new ProgressBar(this.context, 20, -1f + 0.15f, 0.9f,
				Color.LevelProgressBarColor);

		rocketsShip = Collections.synchronizedList(new ArrayList<BaseItem>());
		rocketsBoss = Collections.synchronizedList(new ArrayList<BaseItem>());

		boss = new FstBoss(context, new float[]{0f, 0f, 50f}, ship, rocketsBoss);
		ship.setRockets(rocketsShip);

		compass = new Compass(this.context, Float.MAX_VALUE - 10.0f);
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
		ArrayList<BaseItem> tmp = new ArrayList<>();
		tmp.addAll(rocketsShip);
		for (BaseItem r : tmp)
			r.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		tmp.clear();
		tmp.addAll(rocketsBoss);
		for (BaseItem r : tmp)
			r.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		boss.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		noiseMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, new float[0]);
		forest.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		cubeMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, new float[0]);
		//heightMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace);
	}

	@Override
	public void drawLevelInfo(float ratio) {
		progressBar.draw(ratio);
		compass.draw(ratio);
	}

	@Override
	public void update() {
		ship.update();

		ArrayList<BaseItem> tmpArr = new ArrayList<>();
		tmpArr.addAll(rocketsShip);
		for (BaseItem r : tmpArr)
			r.update();
		tmpArr.clear();
		tmpArr.addAll(rocketsBoss);
		for (BaseItem r : tmpArr)
			r.update();
		boss.update();
		compass.update(ship, boss, boss.isDanger());
		boss.updateLifeProgress(progressBar);
	}

	@Override
	public void collide() {
		ArrayList<Item> ami = new ArrayList<>();
		ami.addAll(rocketsShip);
		ami.add(ship);
		ArrayList<Item> ennemi = new ArrayList<>();
		ennemi.addAll(rocketsBoss);
		ennemi.add(boss);
		ennemi.add(noiseMap);
		Octree octree = new Octree(levelLimits, ami, ennemi, 3f);
		octree.computeOctree();
	}

	@Override
	public boolean isInit() {
		return isInit;
	}

	@Override
	public void removeAddObjects() {
		for (int i = rocketsShip.size() - 1; i >= 0; i--)
			if (!rocketsShip.get(i).isAlive() || !rocketsShip.get(i).isInside(levelLimits))
				rocketsShip.remove(i);
		for (int i = rocketsBoss.size() - 1; i >= 0; i--)
			if (!rocketsBoss.get(i).isAlive() || !rocketsBoss.get(i).isInside(levelLimits))
				rocketsBoss.remove(i);

		boss.fire();
		ship.fire();
	}

	@Override
	public int getScore() {
		return boss.isAlive() ? 0 : 500;
	}

	@Override
	public boolean isDead() {
		return !ship.isAlive() || !ship.isInside(levelLimits);
	}

	@Override
	public boolean isWinner() {
		return !boss.isAlive();
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
