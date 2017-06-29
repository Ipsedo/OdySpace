package com.samuelberrien.odyspace.drawable;

/**
 * Created by samuel on 29/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public interface GLItemDrawable {

	/**
	 *
	 * @param mProjectionMatrix
	 * @param mViewMatrix
	 * @param mLightPosInEyeSpace
	 * @param mCameraPosition
	 */
	void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition);
}
