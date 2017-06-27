package com.samuelberrien.odyspace.drawable;

/**
 * Created by samuel on 27/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public interface GLDrawable {

	/**
	 * @param mvpMatrix
	 * @param mvMatrix
	 * @param mLightPosInEyeSpace
	 * @param mCameraPosition
	 */
	void draw(float[] mvpMatrix, float[] mvMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition);
}
