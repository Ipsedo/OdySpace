package com.samuelberrien.odyspace.utils.game;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.controls.Controls;
import com.samuelberrien.odyspace.drawable.controls.Joystick;
import com.samuelberrien.odyspace.levels.TestBossThread;
import com.samuelberrien.odyspace.levels.TestProtectionLevel;
import com.samuelberrien.odyspace.levels.TestThread;
import com.samuelberrien.odyspace.levels.TestTurrets;
import com.samuelberrien.odyspace.objects.Ship;

/**
 * Created by samuel on 18/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public interface Level {

	String[] LEVELS = {TestThread.NAME, TestProtectionLevel.NAME, TestTurrets.NAME, TestBossThread.NAME};

	void init(Context context, Ship ship, float levelLimitSize, Joystick joystick, Controls controls);

	float[] getLightPos();

	void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition);

	void drawLevelInfo(float ratio);

	void update();

	void collide();

	boolean isInit();

	void removeAddObjects();

	int getScore();

	boolean isDead();

	boolean isWinner();
}