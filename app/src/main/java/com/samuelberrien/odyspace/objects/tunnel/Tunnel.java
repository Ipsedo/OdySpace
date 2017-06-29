package com.samuelberrien.odyspace.objects.tunnel;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.GLItemDrawable;
import com.samuelberrien.odyspace.utils.game.Item;
import com.samuelberrien.odyspace.utils.graphics.Color;
import com.samuelberrien.odyspace.utils.maths.SimplexNoise;
import com.samuelberrien.odyspace.utils.maths.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by samuel on 29/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Tunnel implements GLItemDrawable {

	private Context context;

	private Random random;

	private int nbStretch;
	private ArrayList<Stretch> stretches;

	private float[] initPos;
	private float[][] mPoints;
	private float[][] mRotationMatrix;
	private float[][] mVec;

	private float limitLength;
	private float maxLength;

	public Tunnel(Context context, Random random, int nbStretch, float[] initPos) {
		this.context = context;
		this.random = random;
		this.nbStretch = nbStretch;
		this.initPos = initPos.clone();

		this.limitLength = 30f;
		this.maxLength = 70f;

		this.makePoints();
		this.makeStretches();
	}

	private void makePoints() {
		this.mPoints = new float[this.nbStretch + 1][3];
		this.mRotationMatrix = new float[this.mPoints.length][16];
		Matrix.setIdentityM(this.mRotationMatrix[0], 0);
		this.mPoints[0] = this.initPos.clone();
		this.mVec = new float[this.mPoints.length][4];
		for (int i = 1; i < this.mPoints.length; i++) {
			float length = this.random.nextFloat() * (this.maxLength - this.limitLength) + this.limitLength;

			float[] pitchMatrix = new float[16];
			float[] yawMatrix = new float[16];
			Matrix.setRotateM(yawMatrix, 0, this.random.nextFloat() * 20f - 10f, 0f, 1f, 0f);
			Matrix.setRotateM(pitchMatrix, 0, this.random.nextFloat() * 20f - 10f, 1f, 0f, 0f);

			float[] currRotMatrix = new float[16];
			Matrix.multiplyMM(currRotMatrix, 0, pitchMatrix, 0, yawMatrix, 0);

			float[] currVec = new float[4];
			Matrix.multiplyMV(currVec, 0, currRotMatrix, 0, new float[]{0f, 0f, length, 0f}, 0);

			Matrix.multiplyMV(this.mVec[i], 0, this.mRotationMatrix[i - 1], 0, currVec, 0);

			float[] tmpMat = this.mRotationMatrix[i - 1].clone();
			Matrix.multiplyMM(this.mRotationMatrix[i], 0, tmpMat, 0, currRotMatrix, 0);

			this.mPoints[i][0] = this.mPoints[i - 1][0] + this.mVec[i][0];
			this.mPoints[i][1] = this.mPoints[i - 1][1] + this.mVec[i][1];
			this.mPoints[i][2] = this.mPoints[i - 1][2] + this.mVec[i][2];
		}
	}

	private void makeStretches() {
		this.stretches = new ArrayList<>();

		float[][] scale = new float[this.mPoints.length][3];

		for (int i = 0; i < scale.length; i++) {
			float tmp = (float) SimplexNoise.noise(this.mPoints[i][0] / (this.limitLength * (float) this.nbStretch), this.mPoints[i][1] / (this.limitLength * (float) this.nbStretch), this.mPoints[i][2] / (this.limitLength * (float) this.nbStretch)) * 20f;
			scale[i][0] = tmp + this.random.nextFloat() * 5f + 20f;
			scale[i][1] = tmp + this.random.nextFloat() * 5f + 20f;
			scale[i][2] = tmp + this.random.nextFloat() * 5f + 20f;
		}

		for (int i = 0; i < this.mPoints.length - 1; i++) {
			float[] mCircle1ModelMatrix = new float[16];
			Matrix.setIdentityM(mCircle1ModelMatrix, 0);
			Matrix.translateM(mCircle1ModelMatrix, 0, this.mPoints[i][0], this.mPoints[i][1], this.mPoints[i][2]);
			Matrix.multiplyMM(mCircle1ModelMatrix, 0, mCircle1ModelMatrix.clone(), 0, this.mRotationMatrix[i], 0);
			Matrix.scaleM(mCircle1ModelMatrix, 0, scale[i][0], scale[i][1], scale[i][2]);


			float[] mCircle2ModelMatrix = new float[16];
			Matrix.setIdentityM(mCircle2ModelMatrix, 0);
			Matrix.translateM(mCircle2ModelMatrix, 0, this.mPoints[i + 1][0], this.mPoints[i + 1][1], this.mPoints[i + 1][2]);
			Matrix.multiplyMM(mCircle2ModelMatrix, 0, mCircle2ModelMatrix.clone(), 0, this.mRotationMatrix[i + 1], 0);
			Matrix.scaleM(mCircle2ModelMatrix, 0, scale[i + 1][0], scale[i + 1][1], scale[i + 1][2]);

			this.stretches.add(new Stretch(this.context, mCircle1ModelMatrix, mCircle2ModelMatrix, 20, Color.Pumpkin, 0f, 1f, 0.2f));
		}
	}

	public List<Item> getItems() {
		ArrayList<Item> res = new ArrayList<>();
		res.addAll(this.stretches);
		return res;
	}

	public boolean isInLastStretch(Item item) {
		return item.isInside(this.stretches.get(this.stretches.size() - 1).getBox());
	}

	@Override
	public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		for (Stretch s : this.stretches)
			s.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
	}
}
