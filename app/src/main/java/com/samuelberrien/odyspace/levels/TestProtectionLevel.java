package com.samuelberrien.odyspace.levels;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.Compass;
import com.samuelberrien.odyspace.drawable.Forest;
import com.samuelberrien.odyspace.drawable.ProgressBar;
import com.samuelberrien.odyspace.drawable.explosion.Explosion;
import com.samuelberrien.odyspace.drawable.maps.CubeMap;
import com.samuelberrien.odyspace.drawable.maps.NoiseMap;
import com.samuelberrien.odyspace.drawable.obj.ObjModel;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.objects.baseitem.Base;
import com.samuelberrien.odyspace.objects.baseitem.BaseItem;
import com.samuelberrien.odyspace.objects.baseitem.Icosahedron;
import com.samuelberrien.odyspace.objects.baseitem.SuperIcosahedron;
import com.samuelberrien.odyspace.objects.baseitem.shooters.Ship;
import com.samuelberrien.odyspace.objects.crashable.CrashableMesh;
import com.samuelberrien.odyspace.utils.collision.Box;
import com.samuelberrien.odyspace.utils.collision.Octree;
import com.samuelberrien.odyspace.utils.game.Item;
import com.samuelberrien.odyspace.utils.game.Level;
import com.samuelberrien.odyspace.utils.graphics.Color;
import com.samuelberrien.odyspace.utils.maths.Triangle;
import com.samuelberrien.odyspace.utils.maths.Vector;
import com.samuelberrien.odyspace.utils.sounds.SoundPoolBuilder;

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
	private CrashableMesh crashableIco;
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

	private boolean isInit;

	private ProgressBar currLevelProgression;
	private static int maxLevelTime = 1 << 14;
	private int currLevelTime;

	private Random rand;

	private SoundPoolBuilder soundPoolBuilder;

	public TestProtectionLevel() {
		isInit = false;
		levelLimitSize = 500f;
	}

	@Override
	public void init(Context context, Ship ship) {
		this.context = context;
		this.ship = ship;

		float limitDown = -100f;
		noiseMap = new NoiseMap(context,
				new float[]{0f, 177f / 255f, 106f / 255f, 1f},
				0.45f, 0f, 8, levelLimitSize, limitDown, 0.02f);
		noiseMap.update();
		forest = new Forest(this.context,
				"obj/dead_tree.obj", "obj/dead_tree.mtl",
				100, noiseMap, levelLimitSize);
		levelLimits = new Box(-levelLimitSize,
				limitDown - 0.02f * levelLimitSize - 100f,
				-levelLimitSize,
				levelLimitSize * 2f,
				levelLimitSize * 1.5f,
				levelLimitSize * 2f);
		cubeMap = new CubeMap(this.context, levelLimitSize, "cube_map/ciel_1/");
		cubeMap.update();
		this.levelLimitSize = levelLimitSize;

		rockets = Collections.synchronizedList(new ArrayList<BaseItem>());
		icosahedrons = Collections.synchronizedList(new ArrayList<BaseItem>());
		explosions = Collections.synchronizedList(new ArrayList<Explosion>());
		bases = Collections.synchronizedList(new ArrayList<BaseItem>());

		this.ship.setRockets(rockets);

		particule = new ObjModel(context, "obj/triangle.obj", 1f, 1f, 1f, 1f, 0f, 1f);
		/*icosahedron = new ObjModelMtlVBO(this.context,
				"obj/icosahedron.obj", "obj/icosahedron.mtl",
				1f, 0f, true);*/
		icosahedron = new ObjModelMtlVBO(this.context,
				"obj/asteroid1.obj", "obj/asteroid1.mtl",
				1f, 0f, true);
		directionToIco = new Compass(this.context, Float.MAX_VALUE - 10.f);
		//TODO crashable
		crashableIco = new CrashableMesh(context, "obj/icosahedron.obj");


		rand = new Random(System.currentTimeMillis());

		base = new ObjModelMtlVBO(this.context, "obj/base.obj", "obj/base.mtl", 1f, 0f, false);
		//TODO faire vrai crashable?
		CrashableMesh crashableMesh = new CrashableMesh(context, "obj/base.obj");
		for (int i = 0; i < nbBase; i++) {
			float x = rand.nextFloat() * (levelLimitSize - 10f) * 2f - levelLimitSize + 5f;
			float z = rand.nextFloat() * (levelLimitSize - 10f) * 2f - levelLimitSize + 5f;

			float[] triangles = noiseMap.passToModelMatrix(
					noiseMap.getRestreintArea(new float[]{x, 0f, z}));
			float moy = Triangle.CalcY(
					new float[]{triangles[0], triangles[1], triangles[2]},
					new float[]{triangles[3], triangles[4], triangles[5]},
					new float[]{triangles[6], triangles[7], triangles[8]}, x, z) / 2f;
			moy += Triangle.CalcY(
					new float[]{triangles[9], triangles[10], triangles[11]},
					new float[]{triangles[12], triangles[13], triangles[14]},
					new float[]{triangles[15], triangles[16], triangles[17]}, x, z) / 2f;

			float[] pos = new float[]{x, moy + 5f, z};

			Base tmpBase = new Base(context, base, crashableMesh, 1, pos, 25f);
			tmpBase.update();
			tmpBase.queueExplosion();
			bases.add(tmpBase);
		}

		currLevelProgression = new ProgressBar(this.context, maxLevelTime, -1f + 0.15f, 0.9f,
				Color.LevelProgressBarColor);

		soundPoolBuilder = new SoundPoolBuilder(this.context);

		this.ship.queueExplosion();

		isInit = true;
	}

	@Override
	public float[] getLightPos() {
		return new float[]{0f, levelLimitSize * 0.5f, 0f};
	}

	@Override
	public void draw(float[] mProjectionMatrix,
					 float[] mViewMatrix,
					 float[] mLightPosInEyeSpace,
					 float[] mCameraPosition) {
		ship.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		noiseMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, new float[0]);
		forest.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		cubeMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, new float[0]);
		ArrayList<BaseItem> tmp = new ArrayList<>(rockets);
		for (BaseItem r : tmp)
			r.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		tmp = new ArrayList<>(icosahedrons);
		for (BaseItem i : tmp)
			i.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		tmp = new ArrayList<>(bases);
		for (BaseItem b : tmp)
			b.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		ArrayList<Explosion> tmp2 = new ArrayList<>(explosions);
		for (Explosion e : tmp2)
			e.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
	}

	@Override
	public void drawLevelInfo(float ratio) {
		currLevelProgression.draw(ratio);
		ArrayList<BaseItem> icos = new ArrayList<>(icosahedrons);
		for (BaseItem ico : icos) {
			directionToIco.update(ship, ico, ico.isDanger());
			directionToIco.draw(ratio);
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

		tmpArr.clear();
		tmpArr.addAll(icosahedrons);
		for (BaseItem i : tmpArr)
			i.update();

		currLevelProgression.updateProgress(currLevelTime++);
	}

	@Override
	public void collide() {
		ArrayList<Item> ami = new ArrayList<>();
		ami.addAll(rockets);
		ami.add(ship);
		ArrayList<Item> ennemi = new ArrayList<>();
		ennemi.addAll(icosahedrons);
		ennemi.add(noiseMap);
		ennemi.addAll(bases);
		Octree octree = new Octree(levelLimits, ami, ennemi, 10f);
		octree.computeOctree();

		ami.clear();
		ennemi.clear();
		ami.add(noiseMap);
		ami.addAll(bases);
		ennemi.addAll(icosahedrons);
		octree = new Octree(levelLimits, ami, ennemi, 10f);
		octree.computeOctree();
	}

	@Override
	public boolean isInit() {
		return isInit;
	}

	private float[] randomIcoPosition() {
		return new float[]{rand.nextFloat() * levelLimitSize * 2f - levelLimitSize,
				-100f - 0.02f * levelLimitSize + levelLimitSize + rand.nextFloat() * levelLimitSize * 0.5f,
				rand.nextFloat() * levelLimitSize * 2f - levelLimitSize};
	}

	private float[] randomIcoSpeed(float maxSpeed) {
		float[] speed = new float[3];

		double phi = Math.PI / 2d + rand.nextDouble() * Math.PI / 6d + 5d * Math.PI / 12d;
		double theta = rand.nextDouble() * Math.PI * 2d;

		speed[0] = maxSpeed * (float) (Math.cos(theta) * Math.sin(phi));
		speed[1] = maxSpeed * (float) Math.cos(phi);
		speed[2] = maxSpeed * (float) (Math.sin(phi) * Math.sin(theta));

		return speed;
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

		List<BaseItem> targets = new ArrayList<>(bases);
		if (rand.nextFloat() < 4e-2f) {
			Icosahedron tmp = new SuperIcosahedron(context,
					icosahedron, crashableIco,
					(int) Math.ceil(rand.nextDouble() * 3),
					randomIcoPosition(),
					randomIcoSpeed(rand.nextFloat() * 0.1f + 0.2f),
					rand.nextFloat() * 10f + 10f);
			tmp.update();
			tmp.queueExplosion();
			tmp.computeDanger(targets);
			icosahedrons.add(tmp);
		}

		for (int i = rockets.size() - 1; i >= 0; i--)
			if (!rockets.get(i).isAlive() || !rockets.get(i).isInside(levelLimits))
				rockets.remove(i);

		if (!ship.isAlive() || !ship.isInside(levelLimits))
			ship.addExplosion(explosions);

		for (int i = bases.size() - 1; i >= 0; i--)
			if (!bases.get(i).isAlive()) {
				soundPoolBuilder.playBigBoom(getSoundLevel(bases.get(i)),
						getSoundLevel(bases.get(i)));
				bases.get(i).addExplosion(explosions);
				bases.remove(i);
			}

		ship.fire();
	}

	@Override
	public int getScore() {
		return ship.isAlive() && nbBase - bases.size() == 0 ? 100 : 0;
	}

	@Override
	public boolean isDead() {
		return !ship.isAlive() || !ship.isInside(levelLimits) || nbBase - bases.size() != 0;
	}

	@Override
	public boolean isWinner() {
		return currLevelTime >= maxLevelTime;
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
