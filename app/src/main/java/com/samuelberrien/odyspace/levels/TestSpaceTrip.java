package com.samuelberrien.odyspace.levels;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.GLDrawable;
import com.samuelberrien.odyspace.drawable.explosion.Explosion;
import com.samuelberrien.odyspace.drawable.maps.CubeMap;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.objects.baseitem.Base;
import com.samuelberrien.odyspace.objects.baseitem.BaseItem;
import com.samuelberrien.odyspace.objects.baseitem.SuperIcosahedron;
import com.samuelberrien.odyspace.objects.baseitem.ammos.Ammos;
import com.samuelberrien.odyspace.objects.baseitem.shooters.Ship;
import com.samuelberrien.odyspace.objects.crashable.CrashableMesh;
import com.samuelberrien.odyspace.utils.collision.Box;
import com.samuelberrien.odyspace.utils.collision.Octree;
import com.samuelberrien.odyspace.utils.game.Item;
import com.samuelberrien.odyspace.utils.game.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by samuel on 15/10/17.
 */

public class TestSpaceTrip implements Level {

	public static String NAME = "Space Trip";

	private CubeMap deepSpace;

	private Ship ship;

	private Box levelLimits;

	private boolean isInit;

	private float levelLimitSize;

	private final int nbAsteroids;
	private List<BaseItem> asteroids;

	private List<BaseItem> rockets;

	private List<Explosion> explosions;

	public TestSpaceTrip() {
		isInit = false;
		nbAsteroids = 200;
		levelLimitSize = 5000f;
	}

	@Override
	public void init(Context context, Ship ship) {
		this.ship = ship;

		deepSpace = new CubeMap(context, levelLimitSize, "cube_map/magma_planet/");
		deepSpace.update();

		levelLimits = new Box(-levelLimitSize,
				-levelLimitSize,
				-levelLimitSize,
				levelLimitSize * 2f,
				levelLimitSize * 2f,
				levelLimitSize * 2f);

		rockets = Collections.synchronizedList(new ArrayList<BaseItem>());
		asteroids = Collections.synchronizedList(new ArrayList<BaseItem>(nbAsteroids));
		explosions = Collections.synchronizedList(new ArrayList<Explosion>());

		ObjModelMtlVBO model = new ObjModelMtlVBO(context,
				"obj/asteroid2.obj", "obj/asteroid2.mtl", 1f, 0f, true);
		CrashableMesh crashableMesh = new CrashableMesh(context, "obj/icosahedron.obj");
		Random random = new Random(System.currentTimeMillis());
		for (int i = 0; i < nbAsteroids; i++) {
			float[] randomPos = new float[3];
			float[] randomSpeed = new float[3];
			for (int j = 0; j < 3; j++) {
				randomPos[j] = random.nextFloat() * levelLimitSize * 2f / (3f * 5f) - levelLimitSize * 1f / (3f * 5f);
				randomSpeed[j] = random.nextFloat() * 0.01f;
			}
			asteroids.add(new SuperIcosahedron(context, model, crashableMesh, 1,
					randomPos, randomSpeed, random.nextFloat() * 4f));
			asteroids.get(i).queueExplosion();
		}

		ship.setRockets(rockets);
		ship.queueExplosion();

		isInit = true;
	}

	@Override
	public float[] getLightPos() {
		return new float[3];
	}

	@Override
	public void draw(float[] mProjectionMatrix,
					 float[] mViewMatrix,
					 float[] mLightPosInEyeSpace,
					 float[] mCameraPosition) {

		ArrayList<GLDrawable> drawables = new ArrayList<>();
		drawables.add(deepSpace);
		drawables.add(ship);
		drawables.addAll(asteroids);
		drawables.addAll(rockets);
		drawables.addAll(explosions);

		for(GLDrawable d : drawables)
			d.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
	}

	@Override
	public void drawLevelInfo(float ratio) {
		ship.drawLife(ratio);
	}

	@Override
	public void update() {
		ArrayList<BaseItem> ast = new ArrayList<>(asteroids);

		for(BaseItem si : ast)
			si.update();

		ast.clear();
		ast.addAll(rockets);
		for (BaseItem r : ast)
			r.update();

		ArrayList<Explosion> tmpArr2 = new ArrayList<>(explosions);
		for (Explosion e : tmpArr2)
			e.move();

		ship.update();
	}

	@Override
	public void collide() {
		ArrayList<Item> amis = new ArrayList<>();
		amis.add(ship);
		amis.addAll(rockets);

		ArrayList<Item> ennemis = new ArrayList<>();
		ennemis.addAll(asteroids);

		Octree octree = new Octree(levelLimits, amis, ennemis, 4f);
		octree.computeOctree();
	}

	@Override
	public boolean isInit() {
		return isInit;
	}

	@Override
	public void removeAddObjects() {
		ship.fire();

		for (int i = rockets.size() - 1; i >= 0; i--)
			if (!rockets.get(i).isAlive() || !rockets.get(i).isInside(levelLimits))
				rockets.remove(i);

		if (!ship.isAlive() || !ship.isInside(levelLimits))
			ship.addExplosion(explosions);

		for (int i = explosions.size() - 1; i >= 0; i--)
			if (!explosions.get(i).isAlive())
				explosions.remove(i);

		for (int i = asteroids.size() - 1; i >= 0; i--) {
			if (!asteroids.get(i).isAlive()) {
				asteroids.get(i).addExplosion(explosions);
				//soundPoolBuilder.playSimpleBoom(getSoundLevel(ico), getSoundLevel(ico));
				asteroids.remove(i);
			} else if (!asteroids.get(i).isInside(levelLimits))
				asteroids.remove(i);
		}
	}

	@Override
	public int getScore() {
		return 0;
	}

	@Override
	public boolean isDead() {
		return !ship.isAlive() || !ship.isInside(levelLimits);
	}

	@Override
	public boolean isWinner() {
		return false;
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
