package com.samuelberrien.odyspace.objects.tunnel;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.GLDrawable;
import com.samuelberrien.odyspace.objects.baseitem.Icosahedron;
import com.samuelberrien.odyspace.core.collision.Box;
import com.samuelberrien.odyspace.core.Item;
import com.samuelberrien.odyspace.utils.graphics.Color;
import com.samuelberrien.odyspace.utils.maths.SimplexNoise;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by samuel on 29/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Tunnel implements GLDrawable {

	private Context context;

	private Random random;

	private int nbStretch;
	private ArrayList<Stretch> stretches;

	private float[] initPos;
	private float[][] mPoints;
	private float[][] mRotationMatrix;
	private float[][] mVec;

	private static float LIMIT_LENGTH = 30f;
	private static float MAX_LENGTH = 70f;

	public Tunnel(Context context, Random random, int nbStretch, float[] initPos) {
		this.context = context;
		this.random = random;
		this.nbStretch = nbStretch;
		this.initPos = initPos.clone();

		makePoints();
		makeStretches();
	}

	private void makePoints() {
		mPoints = new float[nbStretch + 1][3];
		mRotationMatrix = new float[mPoints.length][16];
		Matrix.setIdentityM(mRotationMatrix[0], 0);
		mPoints[0] = initPos.clone();
		mVec = new float[mPoints.length][4];
		for (int i = 1; i < mPoints.length; i++) {
			float length = random.nextFloat() * (MAX_LENGTH - LIMIT_LENGTH) + LIMIT_LENGTH;

			float[] pitchMatrix = new float[16];
			float[] yawMatrix = new float[16];
			Matrix.setRotateM(yawMatrix, 0, random.nextFloat() * 20f - 10f, 0f, 1f, 0f);
			Matrix.setRotateM(pitchMatrix, 0, random.nextFloat() * 20f - 10f, 1f, 0f, 0f);

			float[] currRotMatrix = new float[16];
			Matrix.multiplyMM(currRotMatrix, 0, pitchMatrix, 0, yawMatrix, 0);

			float[] currVec = new float[4];
			Matrix.multiplyMV(currVec, 0, currRotMatrix, 0, new float[]{0f, 0f, length, 0f}, 0);

			Matrix.multiplyMV(mVec[i], 0, mRotationMatrix[i - 1], 0, currVec, 0);

			float[] tmpMat = mRotationMatrix[i - 1].clone();
			Matrix.multiplyMM(mRotationMatrix[i], 0, tmpMat, 0, currRotMatrix, 0);

			mPoints[i][0] = mPoints[i - 1][0] + mVec[i][0];
			mPoints[i][1] = mPoints[i - 1][1] + mVec[i][1];
			mPoints[i][2] = mPoints[i - 1][2] + mVec[i][2];
		}
	}

	private void makeStretches() {
		stretches = new ArrayList<>();

		float[][] scale = new float[mPoints.length][3];

		for (int i = 0; i < scale.length; i++) {
			float tmp = (float) SimplexNoise.noise(mPoints[i][0] / (LIMIT_LENGTH * (float) nbStretch), mPoints[i][1] / (LIMIT_LENGTH * (float) nbStretch), mPoints[i][2] / (LIMIT_LENGTH * (float) nbStretch)) * 20f;
			scale[i][0] = tmp + random.nextFloat() * 5f + 20f;
			scale[i][1] = tmp + random.nextFloat() * 5f + 20f;
			scale[i][2] = tmp + random.nextFloat() * 5f + 20f;
		}

		for (int i = 0; i < mPoints.length - 1; i++) {
			float[] mCircle1ModelMatrix = new float[16];
			Matrix.setIdentityM(mCircle1ModelMatrix, 0);
			Matrix.translateM(mCircle1ModelMatrix, 0, mPoints[i][0], mPoints[i][1], mPoints[i][2]);
			Matrix.multiplyMM(mCircle1ModelMatrix, 0, mCircle1ModelMatrix.clone(), 0, mRotationMatrix[i], 0);
			Matrix.scaleM(mCircle1ModelMatrix, 0, scale[i][0], scale[i][1], scale[i][2]);


			float[] mCircle2ModelMatrix = new float[16];
			Matrix.setIdentityM(mCircle2ModelMatrix, 0);
			Matrix.translateM(mCircle2ModelMatrix, 0, mPoints[i + 1][0], mPoints[i + 1][1], mPoints[i + 1][2]);
			Matrix.multiplyMM(mCircle2ModelMatrix, 0, mCircle2ModelMatrix.clone(), 0, mRotationMatrix[i + 1], 0);
			Matrix.scaleM(mCircle2ModelMatrix, 0, scale[i + 1][0], scale[i + 1][1], scale[i + 1][2]);

			stretches.add(new Stretch(context, mCircle1ModelMatrix, mCircle2ModelMatrix, 20, Color.Pumpkin, 0f, 1f, 0.2f));
		}
	}

	public List<Item> getItems() {
		ArrayList<Item> res = new ArrayList<>();
		res.addAll(stretches);
		return res;
	}

	public List<Item> getItemsInBox(Box box) {
		ArrayList<Item> res = new ArrayList<>();
		for (Stretch s : stretches)
			if (s.isInside(box))
				res.add(s);
		return res;
	}

	public void putIcoAtCircleCenter(Context context, List<Icosahedron> icos, float probability) {
		for (int i = 3; i < mPoints.length; i++) {
			if (random.nextFloat() < probability) {
				Icosahedron tmp = new Icosahedron(context, 1, mPoints[i].clone(), random.nextFloat() * 3f + 3f);
				tmp.update();
				tmp.queueExplosion();
				icos.add(tmp);
			}
		}
	}

	public boolean isInLastStretch(Item item) {
		return item.isInside(stretches.get(stretches.size() - 1).getBox());
	}

	@Override
	public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		for (Stretch s : stretches)
			s.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
	}
}
