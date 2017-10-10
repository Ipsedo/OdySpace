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
		this.ship.queueExplosion();

		this.levelLimitSize = levelLimitSize;

		this.currLevelProgression = new ProgressBar(this.context, 20, -1f + 0.15f, 0.9f, Color.LevelProgressBarColor);

		float limitDown = -100f;
		this.noiseMap = new NoiseMap(context, new float[]{0f, 177f / 255f, 106f / 255f, 1f}, 0.45f, 0f, 6, this.levelLimitSize, limitDown, 0.03f);
		this.noiseMap.update();
		this.levelLimits = new Box(-this.levelLimitSize, limitDown - 0.03f * this.levelLimitSize, -this.levelLimitSize, this.levelLimitSize * 2f, this.levelLimitSize, this.levelLimitSize * 2f);
		this.cubeMap = new CubeMap(this.context, levelLimitSize, "cube_map/ciel_2/");
		this.cubeMap.update();

		this.rocketsShip = Collections.synchronizedList(new ArrayList<BaseItem>());
		this.rocketsTurret = Collections.synchronizedList(new ArrayList<BaseItem>());
		this.explosions = Collections.synchronizedList(new ArrayList<Explosion>());
		this.turrets = Collections.synchronizedList(new ArrayList<Turret>());

		this.ship.setRockets(this.rocketsShip);

		ObjModelMtlVBO tmpTurret = new ObjModelMtlVBO(context, "bunker1.obj", "bunker1.mtl", 1f, 0f, false);
		CrashableMesh crashableMesh = new CrashableMesh(context, "bunker1.obj");
		Random rand = new Random(System.currentTimeMillis());
		for (int i = 0; i < this.nbTurret; i++) {
			float x = rand.nextFloat() * this.levelLimitSize - this.levelLimitSize / 2f;
			float z = rand.nextFloat() * this.levelLimitSize - this.levelLimitSize / 2f;

			float[] triangles = this.noiseMap.passToModelMatrix(this.noiseMap.getRestreintArea(new float[]{x, 0f, z}));
			float moy = Triangle.CalcY(new float[]{triangles[0], triangles[1], triangles[2]}, new float[]{triangles[3], triangles[4], triangles[5]}, new float[]{triangles[6], triangles[7], triangles[8]}, x, z) / 2f;
			moy += Triangle.CalcY(new float[]{triangles[9], triangles[10], triangles[11]}, new float[]{triangles[12], triangles[13], triangles[14]}, new float[]{triangles[15], triangles[16], triangles[17]}, x, z) / 2f;

			FireType fireType = FireType.GUIDED_MISSILE;
			//TODO modèles simplifiés pr crashable ?
			Turret tmp = new Turret(context, tmpTurret, crashableMesh, new float[]{x, moy + 3f, z}, fireType, this.ship, this.rocketsTurret);
			tmp.update();
			tmp.queueExplosion();
			this.turrets.add(tmp);
		}

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
		this.noiseMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, new float[0]);
		this.ship.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		this.cubeMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, new float[0]);
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
		ArrayList<BaseItem> turrets = new ArrayList<>();
		turrets.addAll(this.turrets);
		for (BaseItem t : turrets) {
			this.compass.update(this.ship, t, t.isDanger());
			this.compass.draw(ratio);
		}
		this.currLevelProgression.draw(ratio);
	}

	@Override
	public void update() {
		this.ship.update();

		ArrayList<BaseItem> tmpArr = new ArrayList<>(this.rocketsShip);
		for (BaseItem r : tmpArr)
			r.update();
		tmpArr.clear();
		tmpArr.addAll(this.rocketsTurret);
		for (BaseItem r : tmpArr)
			r.update();
		tmpArr.clear();
		tmpArr.addAll(this.turrets);
		for (BaseItem t : tmpArr) {
			t.update();
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
		Octree octree = new Octree(this.levelLimits, ami, ennemi, 3f);
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
		for (int i = this.turrets.size() - 1; i >= 0; i--)
			if (!this.turrets.get(i).isAlive()) {
				this.turrets.get(i).addExplosion(this.explosions);
				this.soundPoolBuilder.playSimpleBoom(this.getSoundLevel(this.turrets.get(i)), this.getSoundLevel(this.turrets.get(i)));
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

		ArrayList<Shooter> shooters = new ArrayList<>();
		shooters.addAll(this.turrets);
		shooters.add(this.ship);
		for (Shooter s : shooters)
			s.fire();
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
