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
import com.samuelberrien.odyspace.drawable.maps.CubeMap;
import com.samuelberrien.odyspace.drawable.maps.NoiseMap;
import com.samuelberrien.odyspace.drawable.obj.ObjModel;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.objects.baseitem.Base;
import com.samuelberrien.odyspace.objects.baseitem.BaseItem;
import com.samuelberrien.odyspace.objects.baseitem.Icosahedron;
import com.samuelberrien.odyspace.objects.baseitem.SuperIcosahedron;
import com.samuelberrien.odyspace.objects.baseitem.shooters.Ship;
import com.samuelberrien.odyspace.utils.collision.Box;
import com.samuelberrien.odyspace.utils.collision.Octree;
import com.samuelberrien.odyspace.utils.game.Item;
import com.samuelberrien.odyspace.utils.game.Level;
import com.samuelberrien.odyspace.utils.graphics.Color;
import com.samuelberrien.odyspace.utils.maths.Triangle;
import com.samuelberrien.odyspace.utils.maths.Vector;

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

	public static String NAME = "Protect the bases";
	private Context context;

	private Ship ship;
	private ObjModelMtlVBO icosahedron;
	private List<BaseItem> icosahedrons;
	private ObjModel particule;
	private List<Explosion> explosions;
	private List<BaseItem> rockets;
	private Compass directionToIco;

	private int nbBase = 64;
	private ObjModelMtlVBO base;
	private List<BaseItem> bases;

	private float levelLimitSize;
	private Box levelLimits;
	private CubeMap cubeMap;
	private NoiseMap noiseMap;
	private Forest forest;

	private boolean isInit = false;

	//private long startTime;

	private ProgressBar currLevelProgression;
	private static int maxLevelTime = 1 << 14;
	private int currLevelTime;

	private Random rand;

	private SoundPool mSounds;
	private int simpleBoomSoundId;
	private int bigBoomSoundId;

	//private long levelTime = 1000L * 60L * 3L;

	@Override
	public void init(Context context, Ship ship, float levelLimitSize) {
		this.context = context;
		this.ship = ship;

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

		this.ship.setRockets(this.rockets);

		this.particule = new ObjModel(context, "triangle.obj", 1f, 1f, 1f, 1f, 0f, 1f);
		this.icosahedron = new ObjModelMtlVBO(this.context, "icosahedron.obj", "icosahedron.mtl", 1f, 0f, true);
		this.directionToIco = new Compass(this.context, Float.MAX_VALUE - 10.f);

		//this.startTime = System.currentTimeMillis();

		this.rand = new Random(System.currentTimeMillis());

		this.base = new ObjModelMtlVBO(this.context, "base.obj", "base.mtl", 1f, 0f, false);
		for (int i = 0; i < this.nbBase; i++) {
			float x = rand.nextFloat() * (levelLimitSize - 10f) * 2f - levelLimitSize + 5f;
			float z = rand.nextFloat() * (levelLimitSize - 10f) * 2f - levelLimitSize + 5f;

			float[] triangles = this.noiseMap.passToModelMatrix(this.noiseMap.getRestreintArea(new float[]{x, 0f, z}));
			float moy = Triangle.CalcY(new float[]{triangles[0], triangles[1], triangles[2]}, new float[]{triangles[3], triangles[4], triangles[5]}, new float[]{triangles[6], triangles[7], triangles[8]}, x, z) / 2f;
			moy += Triangle.CalcY(new float[]{triangles[9], triangles[10], triangles[11]}, new float[]{triangles[12], triangles[13], triangles[14]}, new float[]{triangles[15], triangles[16], triangles[17]}, x, z) / 2f;

			float[] pos = new float[]{x, moy + 5f, z};

			Base tmpBase = new Base(this.base, 1, pos, 25f);
			tmpBase.update();
			tmpBase.makeExplosion();
			this.bases.add(tmpBase);
		}

		this.currLevelProgression = new ProgressBar(this.context, maxLevelTime, -1f + 0.15f, 0.9f, Color.LevelProgressBarColor);

		if (Build.VERSION.SDK_INT >= 21) {
			this.mSounds = new SoundPool.Builder().setMaxStreams(20)
					.setAudioAttributes(new AudioAttributes.Builder()
							.setUsage(AudioAttributes.USAGE_GAME)
							.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
							.build())
					.build();
		} else {
			this.mSounds = new SoundPool(20, AudioManager.STREAM_MUSIC, 1);
		}

		this.simpleBoomSoundId = this.mSounds.load(this.context, R.raw.simple_boom, 1);
		this.bigBoomSoundId = this.mSounds.load(this.context, R.raw.big_boom, 1);

		this.ship.makeExplosion();

		this.isInit = true;
	}

	@Override
	public float[] getLightPos() {
		return new float[]{0f, this.levelLimitSize * 0.5f, 0f};
	}

	@Override
	public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		this.ship.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		this.noiseMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, new float[0]);
		this.forest.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		this.cubeMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, new float[0]);
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
			this.directionToIco.update(this.ship, ico, ico.isDanger());
			this.directionToIco.draw(ratio);
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

		tmpArr.clear();
		tmpArr.addAll(this.icosahedrons);
		for (BaseItem i : tmpArr)
			i.update();

		this.currLevelProgression.updateProgress(this.currLevelTime++);
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
		Octree octree = new Octree(this.levelLimits, ami, ennemi, 10f);
		octree.computeOctree();

		ami.clear();
		ennemi.clear();
		ami.add(this.noiseMap);
		ami.addAll(this.bases);
		ennemi.addAll(this.icosahedrons);
		octree = new Octree(this.levelLimits, ami, ennemi, 10f);
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
				this.mSounds.play(this.simpleBoomSoundId, this.getSoundLevel(ico), this.getSoundLevel(ico), 1, 0, 1f);
				this.icosahedrons.remove(i);
			} else if (!this.icosahedrons.get(i).isInside(this.levelLimits))
				this.icosahedrons.remove(i);
		}

		List<BaseItem> targets = new ArrayList<>(this.bases);
		if (this.rand.nextFloat() < 4e-2f) {
			Icosahedron tmp = new SuperIcosahedron(this.icosahedron, (int) Math.ceil(this.rand.nextDouble() * 3), this.randomIcoPosition(), this.randomIcoSpeed(this.rand.nextFloat() * 0.1f + 0.2f), this.rand.nextFloat() * 10f + 10f);
			tmp.update();
			tmp.makeExplosion(this.particule);
			tmp.computeDanger(targets);
			this.icosahedrons.add(tmp);
		}

		for (int i = this.rockets.size() - 1; i >= 0; i--)
			if (!this.rockets.get(i).isAlive() || !this.rockets.get(i).isInside(this.levelLimits))
				this.rockets.remove(i);

		if (!this.ship.isAlive() || !this.ship.isInside(this.levelLimits))
			this.ship.addExplosion(this.explosions);

		for (int i = this.bases.size() - 1; i >= 0; i--)
			if (!this.bases.get(i).isAlive()) {
				this.mSounds.play(this.bigBoomSoundId, this.getSoundLevel(this.bases.get(i)), this.getSoundLevel(this.bases.get(i)), 1, 0, 1f);
				((Base) this.bases.get(i)).addExplosion(this.explosions);
				this.bases.remove(i);
			}

		this.ship.fire();
	}

	@Override
	public int getScore() {
		return this.ship.isAlive() && this.nbBase - this.bases.size() == 0 ? 100 : 0;
	}

	@Override
	public boolean isDead() {
		return !this.ship.isAlive() || !this.ship.isInside(this.levelLimits) || this.nbBase - this.bases.size() != 0;
	}

	@Override
	public boolean isWinner() {
		return currLevelTime >= maxLevelTime;
	}
}
