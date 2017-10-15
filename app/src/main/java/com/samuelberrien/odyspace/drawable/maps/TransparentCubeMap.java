package com.samuelberrien.odyspace.drawable.maps;

import android.content.Context;
import android.opengl.GLES20;

/**
 * Created by samuel on 15/10/17.
 */

public class TransparentCubeMap extends CubeMap {

	public TransparentCubeMap(Context context, float levelLimits, String assetsPathName) {
		super(context, levelLimits, assetsPathName);
	}

	@Override
	public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] unused1, float[] unused2) {
		GLES20.glEnable(GLES20.GL_BLEND);
		super.draw(mProjectionMatrix, mViewMatrix, unused1, unused2);
		GLES20.glDisable(GLES20.GL_BLEND);
	}
}
