package com.samuelberrien.odyspace.utils.game;

import android.opengl.Matrix;

import com.samuelberrien.odyspace.utils.maths.Vector;

import java.util.Random;

/**
 * Created by samuel on 14/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public enum BossMoveType {
	NAIF(0.1f), FIRST(0.1f);

	private final int MAX_COUNT = 200;
	private int counter;

	private Random rand;

	private float maxSpeed;

	private float phi;
	private float theta;

	BossMoveType(float maxSpeed) {
		this.counter = 0;
		this.rand = new Random(System.currentTimeMillis());
		this.phi = 0;
		this.theta = 0;
		this.maxSpeed = maxSpeed;
	}

	private void count() {
		this.counter = (this.counter >= this.MAX_COUNT ? 0 : this.counter + 1);
	}

	public float[] move(float[] bossPos, float[] shipPos, float[] bossSpeed, float[] bossRotMat, float scale) {
		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);
		float[] vecToShip = Vector.normalize3f(Vector.normalize3f(new float[]{shipPos[0] - bossPos[0], shipPos[1] - bossPos[1], shipPos[2] - bossPos[2]}));
		float[] originaleVec = new float[]{0f, 0f, 1f};
		float angle;
		switch (this) {
			case NAIF:
				this.phi += (this.rand.nextDouble() * 2d - 1d) / Math.PI;
				this.theta += (this.rand.nextDouble() * 2d - 1d) / Math.PI;
				bossSpeed[0] = this.maxSpeed * (float) (Math.cos(phi) * Math.sin(theta));
				bossSpeed[1] = this.maxSpeed * (float) Math.sin(phi);
				bossSpeed[2] = this.maxSpeed * (float) (Math.cos(phi) * Math.cos(theta));

				bossPos[0] += bossSpeed[0];
				bossPos[1] += bossSpeed[1];
				bossPos[2] += bossSpeed[2];

				Matrix.translateM(mModelMatrix, 0, bossPos[0], bossPos[1], bossPos[2]);
				angle = (float) Math.toDegrees(Math.acos(Vector.dot3f(vecToShip, originaleVec)));
				Matrix.setRotateM(bossRotMat, 0, angle, 0f, 1f, 0f);
				Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix.clone(), 0, bossRotMat, 0);

				break;
			case FIRST:
				if (this.counter == this.MAX_COUNT / 2) {
					bossSpeed[0] = this.maxSpeed * vecToShip[0];
					bossSpeed[1] = this.maxSpeed * vecToShip[1];
					bossSpeed[2] = this.maxSpeed * vecToShip[2];
				} else {
					this.phi += (this.rand.nextDouble() * 2d - 1d) / Math.PI;
					this.theta += (this.rand.nextDouble() * 2d - 1d) / Math.PI;
					bossSpeed[0] = this.maxSpeed * (float) (Math.cos(phi) * Math.sin(theta));
					bossSpeed[1] = this.maxSpeed * (float) Math.sin(phi);
					bossSpeed[2] = this.maxSpeed * (float) (Math.cos(phi) * Math.cos(theta));
				}

				bossPos[0] += bossSpeed[0];
				bossPos[1] += bossSpeed[1];
				bossPos[2] += bossSpeed[2];

				Matrix.translateM(mModelMatrix, 0, bossPos[0], bossPos[1], bossPos[2]);

				angle = (float) Math.toDegrees(Math.acos(Vector.dot3f(vecToShip, originaleVec)));
				Matrix.setRotateM(bossRotMat, 0, angle, 0f, 1f, 0f);
				Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix.clone(), 0, bossRotMat, 0);
				break;
		}
		Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);
		this.count();
		return mModelMatrix;
	}
}
