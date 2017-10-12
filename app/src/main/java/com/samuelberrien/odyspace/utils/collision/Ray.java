package com.samuelberrien.odyspace.utils.collision;

import com.samuelberrien.odyspace.utils.maths.Vector;

/**
 * Created by samuel on 11/07/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Ray {

	private float[] orig;
	private float[] dir;
	private int[] sign;
	private float[] invDir;

	public Ray(float[] orig, float[] dir) {
		this.orig = orig.clone();
		this.dir = dir.clone();
		sign = new int[3];
		invDir = Vector.inv3f(dir);
		sign[0] = invDir[0] < 0 ? 1 : 0;
		sign[1] = invDir[1] < 0 ? 1 : 0;
		sign[2] = invDir[2] < 0 ? 1 : 0;
	}

	private void checkBound(int i) {
		if (i < 0 || i > 3) {
			throw new ArrayIndexOutOfBoundsException("Ray use float[3] (index : " + i + ") !");
		}
	}

	public float originGet(int i) {
		checkBound(i);
		return orig[i];
	}

	public float directionGet(int i) {
		checkBound(i);
		return dir[i];
	}

	public int signGet(int i) {
		checkBound(i);
		return sign[i];
	}

	public float invDirGet(int i) {
		checkBound(i);
		return invDir[i];
	}
}
