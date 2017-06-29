package com.samuelberrien.odyspace.levels;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.controls.Controls;
import com.samuelberrien.odyspace.drawable.controls.Joystick;
import com.samuelberrien.odyspace.drawable.maps.CubeMap;
import com.samuelberrien.odyspace.objects.baseitem.BaseItem;
import com.samuelberrien.odyspace.objects.baseitem.Ship;
import com.samuelberrien.odyspace.objects.tunnel.Stretch;
import com.samuelberrien.odyspace.objects.tunnel.Tunnel;
import com.samuelberrien.odyspace.utils.collision.Box;
import com.samuelberrien.odyspace.utils.collision.Octree;
import com.samuelberrien.odyspace.utils.game.Item;
import com.samuelberrien.odyspace.utils.game.Level;
import com.samuelberrien.odyspace.utils.graphics.Color;
import com.samuelberrien.odyspace.utils.graphics.ShaderLoader;

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

	private float levelLimitSize;

	@Override
	public void init(Context context, Ship ship, float levelLimitSize, Joystick joystick, Controls controls) {
		this.context = context;
		this.ship = ship;
		this.levelLimitSize = levelLimitSize;
		this.joystick = joystick;
		this.controls = controls;

		this.rockets = Collections.synchronizedList(new ArrayList<BaseItem>());

		this.tunnel = new Tunnel(this.context, new Random(System.currentTimeMillis()), 200, new float[]{0f, 0f, -250f});

		this.isInit = true;
	}

	@Override
	public float[] getLightPos() {
		return this.ship.getPosition();
	}

	@Override
	public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		this.ship.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		ArrayList<BaseItem> tmp = new ArrayList<>(this.rockets);
		for (BaseItem r : tmp)
			r.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
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
	}

	@Override
	public void collide() {
		ArrayList<Item> amis = new ArrayList<>();
		amis.add(this.ship);
		amis.addAll(this.rockets);
		ArrayList<Item> ennemis = new ArrayList<>();
		ennemis.addAll(this.tunnel.getItems());

		float[] shipPos = this.ship.getPosition();
		float sizeCollideBox = 100f;

		Octree octree = new Octree(
				new Box(shipPos[0] - sizeCollideBox / 2f,
						shipPos[1] - sizeCollideBox / 2f,
						shipPos[2] - sizeCollideBox / 2f,
						sizeCollideBox,
						sizeCollideBox,
						sizeCollideBox),
				amis,
				ennemis,
				1f);
		octree.computeOctree();
	}

	@Override
	public boolean isInit() {
		return this.isInit;
	}

	@Override
	public void removeAddObjects() {

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
