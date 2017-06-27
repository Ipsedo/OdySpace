package com.samuelberrien.odyspace.drawable.maps.tunnel;

import com.samuelberrien.odyspace.drawable.GLDrawable;
import com.samuelberrien.odyspace.utils.collision.Box;
import com.samuelberrien.odyspace.utils.game.Item;

/**
 * Created by samuel on 27/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Stretch implements Item, GLDrawable {

	private float[] fstPoint;
	private float[] sndPoint;

	private float[] fstDirVec;
	private float[] sndDirVec;

	private int nbPointsCircle;

	public Stretch(float[] fstPoint, float[] sndPoint, float[] fstDirVec, float[] sndDirVec, int nbPointsCircle) {
		this.fstPoint = fstPoint;
		this.sndPoint = sndPoint;

		this.fstDirVec = fstDirVec;
		this.sndDirVec = sndDirVec;

		this.nbPointsCircle = nbPointsCircle;
	}


	@Override
	public boolean collideTest(float[] triangleArray, float[] modelMatrix) {
		return false;
	}

	@Override
	public boolean isCollided(Item other) {
		return false;
	}

	@Override
	public boolean isInside(Box Box) {
		return false;
	}

	@Override
	public int getDamage() {
		return Integer.MAX_VALUE - 1;
	}

	@Override
	public void decrementLife(int minus) {

	}

	@Override
	public float[] getPosition() {
		return new float[0];
	}

	@Override
	public void draw(float[] mvpMatrix, float[] mvMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {

	}
}
