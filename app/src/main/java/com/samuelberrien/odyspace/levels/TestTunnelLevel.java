package com.samuelberrien.odyspace.levels;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.core.baseitem.BaseItem;
import com.samuelberrien.odyspace.core.baseitem.Icosahedron;
import com.samuelberrien.odyspace.core.baseitem.Ship;
import com.samuelberrien.odyspace.core.baseitem.Tunnel;
import com.samuelberrien.odyspace.core.collision.Box;
import com.samuelberrien.odyspace.core.collision.Octree;
import com.samuelberrien.odyspace.core.Item;
import com.samuelberrien.odyspace.core.Level;
import com.samuelberrien.odyspace.utils.maths.Vector;
import com.samuelberrien.odyspace.utils.sounds.SoundPoolBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by samuel on 29/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class TestTunnelLevel implements Level {

	public static String NAME = "Tunnel Test";

	private boolean isInit = false;

	private Ship ship;
	private Tunnel tunnel;
	private List<BaseItem> rockets;
	private List<Icosahedron> icos;
	private List<Explosion> explosions;

	private float levelLimitSize;

	private SoundPoolBuilder soundPoolBuilder;

	public TestTunnelLevel() {
		levelLimitSize = 100f;
	}

	@Override
	public void init(Context context, Ship ship) {
		this.ship = ship;

		rockets = Collections.synchronizedList(new ArrayList<BaseItem>());
		icos = Collections.synchronizedList(new ArrayList<Icosahedron>());
		explosions = Collections.synchronizedList(new ArrayList<Explosion>());

		ship.setRockets(rockets);

		tunnel = new Tunnel(context, new Random(System.currentTimeMillis()), 200,
				new float[]{0f, 0f, -250f});

		tunnel.putIcoAtCircleCenter(context, icos, 0.1f);

		soundPoolBuilder = new SoundPoolBuilder(context);

		isInit = true;
	}

	private Box makeBoundingBox(float sizeCollideBox) {
		float[] shipPos = ship.clonePosition();
		return new Box(shipPos[0] - sizeCollideBox * 0.5f,
				shipPos[1] - sizeCollideBox * 0.5f,
				shipPos[2] - sizeCollideBox * 0.5f,
				sizeCollideBox,
				sizeCollideBox,
				sizeCollideBox);
	}

	@Override
	public float[] getLightPos() {
		return ship.getCamPosition();
	}

	@Override
	public void draw(float[] mProjectionMatrix,
					 float[] mViewMatrix,
					 float[] mLightPosInEyeSpace,
					 float[] mCameraPosition) {
		ship.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);

		ArrayList<BaseItem> tmp = new ArrayList<>(rockets);
		for (BaseItem r : tmp)
			r.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);

		tmp.clear();
		tmp.addAll(icos);
		for (BaseItem i : tmp)
			i.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);

		ArrayList<Explosion> tmp2 = new ArrayList<>(explosions);
		for (Explosion e : tmp2)
			e.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);

		tunnel.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
	}

	@Override
	public void drawLevelInfo(float ratio) {

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
	}

	@Override
	public void collide() {
		ArrayList<Item> amis = new ArrayList<>();
		ArrayList<Item> ennemis = new ArrayList<>();
		Octree octree;

		amis.addAll(rockets);
		ennemis.addAll(icos);
		octree = new Octree(makeBoundingBox(levelLimitSize * 10f), amis, ennemis, 2f);
		octree.computeOctree();

		amis.clear();
		ennemis.clear();

		amis.add(ship);
		ennemis.addAll(tunnel.getItemsInBox(makeBoundingBox(2f)));
		ennemis.addAll(icos);
		octree = new Octree(makeBoundingBox(levelLimitSize), amis, ennemis, 4f);
		octree.computeOctree();
	}

	@Override
	public boolean isInit() {
		return isInit;
	}

	private float getSoundLevel(BaseItem from) {
		return 1f - Vector.length3f(from.vector3fTo(ship)) / (levelLimitSize * 5f);
	}

	@Override
	public void removeAddObjects() {
		for (int i = icos.size() - 1; i >= 0; i--)
			if (!icos.get(i).isAlive()) {
				Icosahedron ico = icos.get(i);
				ico.addExplosion(explosions);
				soundPoolBuilder.playSimpleBoom(getSoundLevel(ico), getSoundLevel(ico));
				icos.remove(i);
			}

		for (int i = explosions.size() - 1; i >= 0; i--)
			if (!explosions.get(i).isAlive())
				explosions.remove(i);

		for (int i = rockets.size() - 1; i >= 0; i--)
			if (!rockets.get(i).isAlive()
					|| !rockets.get(i).isInside(makeBoundingBox(levelLimitSize * 2f)))
				rockets.remove(i);

		ship.fire();
	}

	@Override
	public int getScore() {
		return tunnel.isInLastStretch(ship) ? 1000 : 0;
	}

	@Override
	public boolean isDead() {
		return !ship.isAlive();
	}

	@Override
	public boolean isWinner() {
		return tunnel.isInLastStretch(ship);
	}

	@Override
	public float getMaxProjection() {
		return levelLimitSize * 10f;
	}

	@Override
	public String toString() {
		return NAME;
	}
}
