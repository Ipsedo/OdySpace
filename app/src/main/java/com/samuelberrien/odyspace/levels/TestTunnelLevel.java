package com.samuelberrien.odyspace.levels;

import android.content.Context;

import com.samuelberrien.odyspace.core.Item;
import com.samuelberrien.odyspace.core.Level;
import com.samuelberrien.odyspace.core.baseitem.BaseItem;
import com.samuelberrien.odyspace.core.baseitem.Icosahedron;
import com.samuelberrien.odyspace.core.baseitem.ship.Ship;
import com.samuelberrien.odyspace.core.baseitem.Tunnel;
import com.samuelberrien.odyspace.core.collision.Box;
import com.samuelberrien.odyspace.core.collision.Octree;
import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.drawable.GLDrawable;
import com.samuelberrien.odyspace.drawable.ProgressBar;
import com.samuelberrien.odyspace.utils.graphics.Color;
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

	private final static int NbStretch = 200;

	private Ship ship;
	private Tunnel tunnel;
	private List<BaseItem> rockets;
	private List<Icosahedron> icos;
	private List<Explosion> explosions;

	private float levelLimitSize;

	private SoundPoolBuilder soundPoolBuilder;

	private ProgressBar progressBar;

	public TestTunnelLevel() {
		levelLimitSize = 1000f;
	}

	@Override
	public void init(Context context, Ship ship) {
		this.ship = ship;

		rockets = Collections.synchronizedList(new ArrayList<BaseItem>());
		icos = Collections.synchronizedList(new ArrayList<Icosahedron>());
		explosions = Collections.synchronizedList(new ArrayList<Explosion>());

		ship.setRockets(rockets);

		tunnel = new Tunnel(context, new Random(System.currentTimeMillis()), NbStretch,
				new float[]{0f, 0f, -250f});

		tunnel.putIcoAtCircleCenter(context, icos, 0.1f);

		progressBar = new ProgressBar(context, NbStretch, -1f + 0.15f, 0.9f, Color.LevelProgressBarColor);

		soundPoolBuilder = new SoundPoolBuilder(context);

		isInit = true;
	}

	private Box makeBoundingBox(BaseItem from, float sizeCollideBox) {
		float[] fromPos = from.clonePosition();
		return new Box(fromPos[0] - sizeCollideBox * 0.5f,
				fromPos[1] - sizeCollideBox * 0.5f,
				fromPos[2] - sizeCollideBox * 0.5f,
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

		ArrayList<GLDrawable> tmp = new ArrayList<>(rockets);
		tmp.addAll(icos);
		tmp.addAll(explosions);
		tmp.add(tunnel);
		tmp.add(ship);

		tmp.forEach(glDrawable -> glDrawable.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition));
	}

	@Override
	public void drawLevelInfo(float ratio) {
		progressBar.draw(ratio);
	}

	@Override
	public void update() {
		ArrayList<BaseItem> tmpArr = new ArrayList<>(rockets);
		tmpArr.addAll(icos);
		tmpArr.add(ship);
		tmpArr.forEach(BaseItem::update);

		ArrayList<Explosion> tmpArr2 = new ArrayList<>(explosions);
		tmpArr2.forEach(Explosion::move);

		progressBar.updateProgress(tunnel.getCurrentSection(ship.clonePosition()));
	}

	@Override
	public void collide() {
		ArrayList<Item> amis = new ArrayList<>();
		ArrayList<Item> ennemis = new ArrayList<>();
		Octree octree;

		amis.add(ship);
		amis.addAll(rockets);
		ennemis.addAll(icos);
		octree = new Octree(makeBoundingBox(ship, 1000.f), amis, ennemis, 10.f);
		octree.computeOctree();

		amis.clear();
		ennemis.clear();

		amis.add(ship);
		ennemis.addAll(icos);
		ennemis.addAll(tunnel.get3NearestStretchs(ship.clonePosition()));
		octree = new Octree(makeBoundingBox(ship, 5.f), amis, ennemis, 5.f);
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
		icos.forEach(i -> {
			if (!i.isAlive()) {
				i.addExplosion(explosions);
				soundPoolBuilder.playSimpleBoom(getSoundLevel(i), getSoundLevel(i));
			}
		});
		icos.removeIf(i -> !i.isAlive());


		explosions.removeIf(e -> !e.isAlive());
		rockets.removeIf(r -> !r.isAlive() || !r.isInside(makeBoundingBox(ship, 1000.f)));
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
		return levelLimitSize;
	}

	@Override
	public String toString() {
		return NAME;
	}
}
