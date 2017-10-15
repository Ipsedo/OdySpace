package com.samuelberrien.odyspace.levels;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.maps.CubeMap;
import com.samuelberrien.odyspace.objects.baseitem.shooters.Ship;
import com.samuelberrien.odyspace.utils.collision.Box;
import com.samuelberrien.odyspace.utils.game.Level;

/**
 * Created by samuel on 15/10/17.
 */

public class TestSpaceTrip implements Level {

	public static String NAME = "Space Trip";

	private CubeMap deepSpace;

	private Ship ship;

	private Box levelLimits;

	private boolean isInit;

	float levelLimitSize;

	public TestSpaceTrip() {
		isInit = false;
		levelLimitSize = 3000f;
	}

	@Override
	public void init(Context context, Ship ship) {
		this.ship = ship;

		deepSpace = new CubeMap(context, levelLimitSize, "cube_map/space_1/");
		deepSpace.update();

		levelLimits = new Box(-levelLimitSize,
				-levelLimitSize,
				-levelLimitSize,
				levelLimitSize * 2f,
				levelLimitSize * 2f,
				levelLimitSize * 2f);

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
		deepSpace.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		ship.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
	}

	@Override
	public void drawLevelInfo(float ratio) {
		ship.drawLife(ratio);
	}

	@Override
	public void update() {
		ship.update();
	}

	@Override
	public void collide() {

	}

	@Override
	public boolean isInit() {
		return isInit;
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
}
