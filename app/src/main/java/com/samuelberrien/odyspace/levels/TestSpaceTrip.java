package com.samuelberrien.odyspace.levels;

import android.content.Context;

import com.samuelberrien.odyspace.core.Item;
import com.samuelberrien.odyspace.core.Level;
import com.samuelberrien.odyspace.core.baseitem.BaseItem;
import com.samuelberrien.odyspace.core.baseitem.boss.SndBoss;
import com.samuelberrien.odyspace.core.baseitem.ship.Ship;
import com.samuelberrien.odyspace.core.baseitem.SuperIcosahedron;
import com.samuelberrien.odyspace.core.collision.Box;
import com.samuelberrien.odyspace.core.collision.CollisionMesh;
import com.samuelberrien.odyspace.core.collision.Octree;
import com.samuelberrien.odyspace.drawable.Compass;
import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.drawable.GLDrawable;
import com.samuelberrien.odyspace.drawable.ObjModelMtlVBO;
import com.samuelberrien.odyspace.drawable.ProgressBar;
import com.samuelberrien.odyspace.drawable.maps.CubeMap;
import com.samuelberrien.odyspace.utils.graphics.Color;

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

	private SndBoss boss;

	private Box levelLimits;

	private Compass compass;
	private ProgressBar progressBar;

	private boolean isInit;

	private float levelLimitSize;

	private final int nbAsteroids;
	private List<BaseItem> asteroids;

	private List<BaseItem> shipRockets;
	private List<BaseItem> bossRockets;

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

		shipRockets = Collections.synchronizedList(new ArrayList<>());
		bossRockets = Collections.synchronizedList(new ArrayList<>());
		asteroids = Collections.synchronizedList(new ArrayList<>(nbAsteroids));
		explosions = Collections.synchronizedList(new ArrayList<>());

		ObjModelMtlVBO model = new ObjModelMtlVBO(context,
				"obj/asteroid2.obj", "obj/asteroid2.mtl", 1f, 0f, true);
		CollisionMesh collisionMesh = new CollisionMesh(context, "obj/icosahedron.obj");
		Random random = new Random(System.currentTimeMillis());
		for (int i = 0; i < nbAsteroids; i++) {
			float[] randomPos = new float[3];
			float[] randomSpeed = new float[3];
			for (int j = 0; j < 3; j++) {
				randomPos[j] = random.nextFloat() * levelLimitSize * 2f / (3f * 5f) - levelLimitSize * 1f / (3f * 5f);
				randomSpeed[j] = random.nextFloat() * 0.01f;
			}
			asteroids.add(new SuperIcosahedron(context, model, collisionMesh, 1,
					randomPos, randomSpeed, random.nextFloat() * 4f));
			asteroids.get(i).queueExplosion();
		}

		ship.setRockets(shipRockets);
		ship.queueExplosion();

		boss = new SndBoss(context, new float[]{0f, 0f, 50f}, bossRockets, ship);

		compass = new Compass(context, Float.MAX_VALUE - 10.0f);

		progressBar = new ProgressBar(context, 40, -1f + 0.15f, 0.9f,
				Color.LevelProgressBarColor);

		isInit = true;
	}

	@Override
	public float[] getLightPos() {
		return new float[]{
				0, 0, -levelLimitSize
		};
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
		drawables.addAll(bossRockets);
		drawables.addAll(shipRockets);
		drawables.addAll(explosions);
		drawables.add(boss);

		drawables.forEach(glDrawable -> glDrawable.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition));
	}

	@Override
	public void drawLevelInfo(float ratio) {
		ship.drawLife(ratio);
		progressBar.draw(ratio);
		compass.draw(ratio);
	}

	@Override
	public void update() {
		ArrayList<BaseItem> items = new ArrayList<>(asteroids);
		items.addAll(bossRockets);
		items.addAll(shipRockets);

		items.forEach(BaseItem::update);

		ArrayList<Explosion> tmpArr2 = new ArrayList<>(explosions);
		for (Explosion e : tmpArr2)
			e.move();

		ship.update();
		boss.update();

		boss.updateLifeProgress(progressBar);
		compass.update(ship, boss, boss.isDanger());
	}

	@Override
	public void collide() {
		ArrayList<Item> amis = new ArrayList<>(shipRockets);
		amis.add(ship);

		ArrayList<Item> ennemis = new ArrayList<>(asteroids);
		ennemis.add(boss);
		ennemis.addAll(bossRockets);

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
		boss.fire();

		bossRockets.removeIf(baseItem -> !baseItem.isAlive() || !baseItem.isInside(levelLimits));
		shipRockets.removeIf(baseItem -> !baseItem.isAlive() || !baseItem.isInside(levelLimits));

		if (!ship.isAlive() || !ship.isInside(levelLimits))
			ship.addExplosion(explosions);

		explosions.removeIf(explosion -> !explosion.isAlive());

		asteroids.forEach(ast -> { if (!ast.isAlive()) ast.addExplosion(explosions); });

		asteroids.removeIf(ast -> !ast.isAlive() || !ast.isInside(levelLimits));
	}

	@Override
	public int getScore() {
		return 1000000;
	}

	@Override
	public boolean isDead() {
		return !ship.isAlive() || !ship.isInside(levelLimits);
	}

	@Override
	public boolean isWinner() {
		return ship.isAlive() && !boss.isAlive();
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
