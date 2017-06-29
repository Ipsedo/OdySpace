package com.samuelberrien.odyspace.levels;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.controls.Controls;
import com.samuelberrien.odyspace.drawable.controls.Joystick;
import com.samuelberrien.odyspace.drawable.maps.CubeMap;
import com.samuelberrien.odyspace.objects.baseitem.BaseItem;
import com.samuelberrien.odyspace.objects.baseitem.Ship;
import com.samuelberrien.odyspace.objects.tunnel.Stretch;
import com.samuelberrien.odyspace.utils.game.Level;
import com.samuelberrien.odyspace.utils.graphics.Color;
import com.samuelberrien.odyspace.utils.graphics.ShaderLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	private Stretch stretch;
	private List<BaseItem> rockets;

	private CubeMap cubeMap;

	private float levelLimitSize;

	@Override
	public void init(Context context, Ship ship, float levelLimitSize, Joystick joystick, Controls controls) {
		this.context = context;
		this.ship = ship;
		this.levelLimitSize = levelLimitSize;
		this.joystick = joystick;
		this.controls = controls;

		this.rockets = Collections.synchronizedList(new ArrayList<BaseItem>());

		this.cubeMap = new CubeMap(this.context, this.levelLimitSize, "cube_map/ciel_1/");
		this.cubeMap.update();

		float[] mCircle1ModelMatrix = new float[16];
		float[] mCircle2ModelMatrix = new float[16];

		Matrix.setIdentityM(mCircle1ModelMatrix, 0);
		Matrix.setIdentityM(mCircle2ModelMatrix, 0);
		Matrix.translateM(mCircle1ModelMatrix, 0, 0f, 0f, -3000f);
		Matrix.translateM(mCircle2ModelMatrix, 0, 0f, 0f, 300f);
		Matrix.scaleM(mCircle1ModelMatrix, 0, 10f, 10f, 100f);
		Matrix.scaleM(mCircle2ModelMatrix, 0, 30f, 30f, 300f);

		this.stretch = new Stretch(this.context, mCircle1ModelMatrix, mCircle2ModelMatrix, 20, Color.Pumpkin, 0f, 1f, 0.5f);


		this.isInit = true;
	}

	@Override
	public float[] getLightPos() {
		return this.ship.getCamPosition().clone();
	}

	@Override
	public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		this.ship.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		ArrayList<BaseItem> tmp = new ArrayList<>(this.rockets);
		for (BaseItem r : tmp)
			r.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		this.cubeMap.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, new float[0]);
		this.stretch.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, new float[0]);
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
		return 0;
	}

	@Override
	public boolean isDead() {
		return false;
	}

	@Override
	public boolean isWinner() {
		return false;
	}
}
