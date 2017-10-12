package com.samuelberrien.odyspace.levels;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.Compass;
import com.samuelberrien.odyspace.drawable.ProgressBar;
import com.samuelberrien.odyspace.drawable.explosion.Explosion;
import com.samuelberrien.odyspace.drawable.maps.CubeMap;
import com.samuelberrien.odyspace.drawable.maps.NoiseMap;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.objects.baseitem.BaseItem;
import com.samuelberrien.odyspace.objects.baseitem.shooters.Ship;
import com.samuelberrien.odyspace.objects.baseitem.shooters.Turret;
import com.samuelberrien.odyspace.objects.crashable.CrashableMesh;
import com.samuelberrien.odyspace.utils.collision.Box;
import com.samuelberrien.odyspace.utils.collision.Octree;
import com.samuelberrien.odyspace.utils.game.FireType;
import com.samuelberrien.odyspace.utils.game.Item;
import com.samuelberrien.odyspace.utils.game.Level;
import com.samuelberrien.odyspace.utils.game.Shooter;
import com.samuelberrien.odyspace.utils.graphics.Color;
import com.samuelberrien.odyspace.utils.maths.Triangle;
import com.samuelberrien.odyspace.utils.maths.Vector;
import com.samuelberrien.odyspace.utils.sounds.SoundPoolBuilder;

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

	public static String NAME = "Eliminate the occupants";

	private Context context;

	private boolean isInit = false;

	private Ship ship;

	private float levelLimitSize;
	private Box levelLimits;
	private NoiseMap noiseMap;
	private CubeMap cubeMap;
	private List<BaseItem> rocketsShip;
	private int nbTurret = 40;
	private List<Turret> turrets;
	private List<BaseItem> rocketsTurret;
	private List<Explosion> explosions;

	private ProgressBar currLevelProgression;
	private Compass compass;

	private SoundPoolBuilder soundPoolBuilder;

	@Override
	public void init(Context context, Ship ship, float levelLimitSize) {
		this.context = context;
		this.ship = ship;
		ship.queueExplosion();

		this.levelLimitSize = levelLimitSize;

		currLevelProgression = new ProgressBar(context, 20, -1f + 0.15f, 0.9f, Color.LevelProgressBarColor);

		float limitDown = -100f;
		noiseMap = new NoiseMap(context, new float[]{0f, 177f / 255f, 106f / 255f, 1f}, 0.45f, 0f, 6, levelLimitSize, limitDown, 0.03f);
		noiseMap.update();
		levelLimits = new Box(-levelLimitSize, limitDown - 0.03f * levelLimitSize, -levelLimitSize, levelLimitSize * 2f, levelLimitSize, levelLimitSize * 2f);
		cubeMap = new CubeMap(context, levelLimitSize, "cube_map/ciel_2/");
		cubeMap.update();

		rocketsShip = Collections.synchronizedList(new ArrayList<BaseItem>());
		rocketsTurret = Collections.synchronizedList(new ArrayList<BaseItem>());
		explosions = Collections.synchronizedList(new ArrayList<Explosion>());
		turrets = Collections.synchronizedList(new ArrayList<Turret>());

		ship.setRockets(rocketsShip);

		ObjModelMtlVBO tmpTurret = new ObjModelMtlVBO(context, "turret.obj", "turret.mtl", 1f, 0f, false);
		CrashableMesh crashableMesh = new CrashableMesh(context, "turret.obj");
		Random rand = new Random(System.currentTimeMillis());
		for (int i = 0; i < nbTurret; i++) {
			float x = rand.nextFloat() * levelLimitSize - levelLimitSize / 2f;
			float z = rand.nextFloat() * levelLimitSize - levelLimitSize / 2f;

			float[] triangles = noiseMap.passToModelMatrix(noiseMap.getRestreintArea(new float[]{x, 0f, z}));
			float moy = Triangle.CalcY(new float[]{triangles[0], triangles[1], triangles[2]}, new float[]{triangles[3], triangles[4], triangles[5]}, new float[]{triangles[6], triangles[7], triangles[8]}, x, z) / 2f;
			moy += Triangle.CalcY(new float[]{triangles[9], triangles[10], triangles[11]}, new float[]{triangles[12], triangles[13], triangles[14]}, new float[]{triangles[15], triangles[16], triangles[17]}, x, z) / 2f;

			FireType fireType = FireType.GUIDED_MISSILE;
			//TODO modèles simplifiés pr crashable ?
			Turret tmp = new Turret(context, tmpTurret, crashableMesh, new float[]{x, moy + 3f, z}, fireType, ship, rocketsTurret);
			tmp.update();
			tmp.queueExplosion();
			turrets.add(tmp);
		}

		soundPoolBuilder = new SoundPoolBuilder(context);

		compass = new Compass(context, levelLimitSize / 12f);

		isInit = true;
	}

	@Override
	public float[] getLightPos() {
		return new float[]{0f, 250f, 0f};
	}

	@Override
	public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		noiseMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, new float[0]);
		ship.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		cubeMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, new float[0]);
		ArrayList<BaseItem> tmp = new ArrayList<>();
		tmp.addAll(rocketsShip);
		for (BaseItem r : tmp)
			r.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		tmp.clear();
		tmp.addAll(rocketsTurret);
		for (BaseItem r : tmp)
			r.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		tmp.clear();
		tmp.addAll(turrets);
		for (BaseItem t : tmp)
			t.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		ArrayList<Explosion> tmp2 = new ArrayList<>(explosions);
		for (Explosion e : tmp2)
			e.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
	}

	@Override
	public void drawLevelInfo(float ratio) {
		ArrayList<BaseItem> turrets = new ArrayList<>();
		turrets.addAll(this.turrets);
		for (BaseItem t : turrets) {
			compass.update(ship, t, t.isDanger());
			compass.draw(ratio);
		}
		currLevelProgression.draw(ratio);
	}

	@Override
	public void update() {
		ship.update();

		ArrayList<BaseItem> tmpArr = new ArrayList<>(rocketsShip);
		for (BaseItem r : tmpArr)
			r.update();
		tmpArr.clear();
		tmpArr.addAll(rocketsTurret);
		for (BaseItem r : tmpArr)
			r.update();
		tmpArr.clear();
		tmpArr.addAll(turrets);
		for (BaseItem t : tmpArr) {
			t.update();
		}
		ArrayList<Explosion> tmpArr2 = new ArrayList<>(explosions);
		for (Explosion e : tmpArr2)
			e.move();

		currLevelProgression.updateProgress(nbTurret - turrets.size());
	}

	@Override
	public void collide() {
		ArrayList<Item> ami = new ArrayList<>();
		ami.addAll(rocketsShip);
		ami.add(ship);
		ArrayList<Item> ennemi = new ArrayList<>();
		ennemi.addAll(rocketsTurret);
		ennemi.addAll(turrets);
		ennemi.add(noiseMap);
		Octree octree = new Octree(levelLimits, ami, ennemi, 3f);
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
		for (int i = turrets.size() - 1; i >= 0; i--)
			if (!turrets.get(i).isAlive()) {
				turrets.get(i).addExplosion(explosions);
				soundPoolBuilder.playSimpleBoom(getSoundLevel(turrets.get(i)), getSoundLevel(turrets.get(i)));
				turrets.remove(i);
			}
		for (int i = rocketsShip.size() - 1; i >= 0; i--)
			if (!rocketsShip.get(i).isAlive() || !rocketsShip.get(i).isInside(levelLimits))
				rocketsShip.remove(i);
		for (int i = rocketsTurret.size() - 1; i >= 0; i--)
			if (!rocketsTurret.get(i).isAlive() || !rocketsTurret.get(i).isInside(levelLimits))
				rocketsTurret.remove(i);
		if (!ship.isAlive() || !ship.isInside(levelLimits))
			ship.addExplosion(explosions);

		ArrayList<Shooter> shooters = new ArrayList<>();
		shooters.addAll(turrets);
		shooters.add(ship);
		for (Shooter s : shooters)
			s.fire();
	}

	@Override
	public int getScore() {
		return (nbTurret - turrets.size()) * 10;
	}

	@Override
	public boolean isDead() {
		return !ship.isAlive() || !ship.isInside(levelLimits);
	}

	@Override
	public boolean isWinner() {
		return nbTurret - turrets.size() > 19;
	}
}
