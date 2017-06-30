package com.samuelberrien.odyspace.levels;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.Matrix;
import android.os.Build;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.drawable.controls.Controls;
import com.samuelberrien.odyspace.drawable.controls.Joystick;
import com.samuelberrien.odyspace.drawable.maps.CubeMap;
import com.samuelberrien.odyspace.objects.baseitem.BaseItem;
import com.samuelberrien.odyspace.objects.baseitem.Icosahedron;
import com.samuelberrien.odyspace.objects.baseitem.Ship;
import com.samuelberrien.odyspace.objects.baseitem.SuperIcosahedron;
import com.samuelberrien.odyspace.objects.tunnel.Stretch;
import com.samuelberrien.odyspace.objects.tunnel.Tunnel;
import com.samuelberrien.odyspace.utils.collision.Box;
import com.samuelberrien.odyspace.utils.collision.Octree;
import com.samuelberrien.odyspace.utils.game.Item;
import com.samuelberrien.odyspace.utils.game.Level;
import com.samuelberrien.odyspace.utils.graphics.Color;
import com.samuelberrien.odyspace.utils.graphics.ShaderLoader;
import com.samuelberrien.odyspace.utils.maths.Vector;

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

	private Context context;

	private boolean isInit = false;

	private Joystick joystick;
	private Controls controls;

	private Ship ship;
	private Tunnel tunnel;
	private List<BaseItem> rockets;
	private List<Icosahedron> icos;
	private List<Explosion> explosions;

	private float levelLimitSize;
	private float sizeCollideBox = 100f;

	private SoundPool mSounds;
	private int soundId;

	@Override
	public void init(Context context, Ship ship, float levelLimitSize, Joystick joystick, Controls controls) {
		this.context = context;
		this.ship = ship;
		this.levelLimitSize = levelLimitSize;
		this.joystick = joystick;
		this.controls = controls;

		this.rockets = Collections.synchronizedList(new ArrayList<BaseItem>());
		this.icos = Collections.synchronizedList(new ArrayList<Icosahedron>());
		this.explosions = Collections.synchronizedList(new ArrayList<Explosion>());

		this.tunnel = new Tunnel(this.context, new Random(System.currentTimeMillis()), 200, new float[]{0f, 0f, -250f});

		this.tunnel.putIcoAtCircleCenter(this.context, this.icos, 0.1f);

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

		this.soundId = this.mSounds.load(this.context, R.raw.simple_boom, 1);

		this.isInit = true;
	}

	private Box makeBoundingBox(float sizeCollideBox) {
		float[] shipPos = this.ship.getPosition();
		Box limitBox = new Box(shipPos[0] - sizeCollideBox / 2f,
				shipPos[1] - sizeCollideBox / 2f,
				shipPos[2] - sizeCollideBox / 2f,
				sizeCollideBox,
				sizeCollideBox,
				sizeCollideBox);
		return limitBox;
	}

	@Override
	public float[] getLightPos() {
		return this.ship.getCamPosition();
	}

	@Override
	public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		this.ship.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);

		ArrayList<BaseItem> tmp = new ArrayList<>(this.rockets);
		for (BaseItem r : tmp)
			r.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);

		tmp.clear();
		tmp.addAll(this.icos);
		for (BaseItem i : tmp)
			i.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);

		ArrayList<Explosion> tmp2 = new ArrayList<>(this.explosions);
		for (Explosion e : tmp2)
			e.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);

		this.tunnel.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
	}

	@Override
	public void drawLevelInfo(float ratio) {

	}

	@Override
	public void update() {
		if (this.ship.isAlive()) {
			this.ship.move(this.joystick, this.controls);
			this.ship.fire(this.controls, this.rockets);
		}

		ArrayList<BaseItem> tmpArr = new ArrayList<>(this.rockets);
		for (BaseItem r : tmpArr)
			r.move();

		ArrayList<Explosion> tmpArr2 = new ArrayList<>(this.explosions);
		for (Explosion e : tmpArr2)
			e.move();
	}

	@Override
	public void collide() {
		ArrayList<Item> amis = new ArrayList<>();
		amis.add(this.ship);
		ArrayList<Item> ennemis = new ArrayList<>();
		ennemis.addAll(this.tunnel.getItems());
		ennemis.addAll(this.icos);
		Octree octree = new Octree(this.makeBoundingBox(this.sizeCollideBox), amis, ennemis, 4f);
		octree.computeOctree();

		amis.clear();
		ennemis.clear();
		amis.addAll(this.rockets);
		ennemis.addAll(this.icos);
		octree = new Octree(this.makeBoundingBox(this.levelLimitSize * 2f), amis, ennemis, 10f);
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
		for (int i = this.icos.size() - 1; i >= 0; i--)
			if (!this.icos.get(i).isAlive()) {
				Icosahedron ico = this.icos.get(i);
				ico.addExplosion(this.explosions);
				this.mSounds.play(this.soundId, this.getSoundLevel(ico), this.getSoundLevel(ico), 1, 0, 1f);
				this.icos.remove(i);
			}

		for (int i = this.explosions.size() - 1; i >= 0; i--)
			if (!this.explosions.get(i).isAlive())
				this.explosions.remove(i);

		for (int i = this.rockets.size() - 1; i >= 0; i--)
			if(!this.rockets.get(i).isAlive() || !this.rockets.get(i).isInside(this.makeBoundingBox(this.levelLimitSize * 2f)))
				this.rockets.remove(i);
	}

	@Override
	public int getScore() {
		return this.tunnel.isInLastStretch(this.ship) ? 1000 : 0;
	}

	@Override
	public boolean isDead() {
		return !this.ship.isAlive();
	}

	@Override
	public boolean isWinner() {
		return this.tunnel.isInLastStretch(this.ship);
	}
}
