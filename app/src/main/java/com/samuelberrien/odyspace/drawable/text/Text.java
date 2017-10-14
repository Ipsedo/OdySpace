package com.samuelberrien.odyspace.drawable.text;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.GLInfoDrawable;
import com.samuelberrien.odyspace.drawable.obj.ObjModel;
import com.samuelberrien.odyspace.utils.graphics.Color;

/**
 * Created by samuel on 01/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Text extends ObjModel implements GLInfoDrawable {

	private float scale;

	public Text(Context context, String objFileName, float scale) {
		super(context,
				objFileName,
				Color.TextsColor[0], Color.TextsColor[1], Color.TextsColor[2],
				1f, 0f, 0f);
		this.scale = scale;
	}

	@Override
	public void draw(float ratio) {
		float[] mViewMatrix = new float[16];
		Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -1, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
		float[] mVPMatrix = new float[16];
		float[] mPMatrix = new float[16];
		Matrix.orthoM(mPMatrix, 0, -1f * ratio, 1f * ratio, -1f, 1f, -1f, 1f);
		Matrix.multiplyMM(mVPMatrix, 0, mPMatrix, 0, mViewMatrix, 0);
		float[] mMVPMatrix = new float[16];
		float[] mMMatrix = new float[16];
		Matrix.setIdentityM(mMMatrix, 0);
		Matrix.translateM(mMMatrix, 0, 0f, 0f, 0f);
		Matrix.scaleM(mMMatrix, 0, scale, scale, scale);
		Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);

		super.draw(mMVPMatrix, mVPMatrix, new float[]{0f, 0f, -1f}, new float[0]);
	}
}
