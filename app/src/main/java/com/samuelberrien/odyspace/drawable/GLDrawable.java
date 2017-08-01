package com.samuelberrien.odyspace.drawable;

/**
 * Created by samuel on 29/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public interface GLDrawable {

	/**
	 *
	 */
	void changeColor();

	/**
	 * @param mMVPMatrix
	 * @param mMVMatrix
	 * @param mLightPosInEyeSpace
	 * @param mCameraPosition
	 */
	void draw(float[] mMVPMatrix, float[] mMVMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition);
}
