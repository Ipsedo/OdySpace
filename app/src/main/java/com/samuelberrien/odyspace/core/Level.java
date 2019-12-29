package com.samuelberrien.odyspace.core;

import android.content.Context;

import com.samuelberrien.odyspace.levels.TestBossThread;
import com.samuelberrien.odyspace.levels.TestProtectionLevel;
import com.samuelberrien.odyspace.levels.TestSpaceTrip;
import com.samuelberrien.odyspace.levels.TestThread;
import com.samuelberrien.odyspace.levels.TestTunnelLevel;
import com.samuelberrien.odyspace.levels.TestTurrets;
import com.samuelberrien.odyspace.core.baseitem.Ship;

/**
 * Created by samuel on 18/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public interface Level {

	String[] LEVELS = {TestThread.NAME, TestProtectionLevel.NAME, TestTurrets.NAME, TestBossThread.NAME, TestTunnelLevel.NAME, TestSpaceTrip.NAME};

	void init(Context context, Ship ship);

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

	float getMaxProjection();
}