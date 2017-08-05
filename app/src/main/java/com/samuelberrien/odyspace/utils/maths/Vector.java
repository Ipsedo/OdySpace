package com.samuelberrien.odyspace.utils.maths;

import android.opengl.Matrix;

/**
 * Created by samuel on 19/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public final class Vector {

	/**
	 * Float functions
	 */

	public final static float[] originalUp3f = new float[]{0f, 1f, 0f};
	public final static float[] originalUp4f = new float[]{0f, 1f, 0f, 0f};

	private static void checkBound(float[] vec, int desiredLength) {
		if (vec.length != desiredLength) {
			throw new IllegalArgumentException("float[" + desiredLength + "] needed (actual length : " + vec.length + ") !");
		}
	}

	public static float[] div3f(float[] u, float x, boolean isUCounter) {
		Vector.checkBound(u, 3);
		if (isUCounter) {
			return new float[]{u[0] / x, u[1] / x, u[2] / x};
		} else {
			return new float[]{x / u[0], x / u[1], x / u[2]};
		}
	}

	public static float[] inv3f(float[] u) {
		Vector.checkBound(u, 3);
		return Vector.div3f(u, 1, false);
	}

	public static float[] mul3f(float[] u, float x) {
		Vector.checkBound(u, 3);
		return new float[]{u[0] * x, u[1] * x, u[2] * x};
	}

	public static float[] add3f(float[] u, float[] v) {
		Vector.checkBound(u, 3);
		Vector.checkBound(v, 3);
		return new float[]{u[0] + v[0], u[1] + v[1], u[2] + v[2]};
	}

	public static float dot3f(float[] u, float[] v) {
		Vector.checkBound(u, 3);
		Vector.checkBound(v, 3);
		return u[0] * v[0] + u[1] * v[1] + u[2] * v[2];
	}

	public static float[] cross3f(float[] u, float[] v) {
		Vector.checkBound(u, 3);
		Vector.checkBound(v, 3);
		return new float[]{u[1] * v[2] - u[2] * v[1], u[2] * v[0] - u[0] * v[2], u[0] * v[1] - u[1] * v[0]};
	}

	public static float[] normalize3f(float[] u) {
		Vector.checkBound(u, 3);
		float lgt = Vector.length3f(u);
		return new float[]{u[0] / lgt, u[1] / lgt, u[2] / lgt};
	}

	public static float length3f(float[] u) {
		Vector.checkBound(u, 3);
		return (float) Math.sqrt(Math.pow(u[0], 2d) + Math.pow(u[1], 2d) + Math.pow(u[2], 2d));
	}

	public static float[] make3f(float[] from, float[] to) {
		Vector.checkBound(from, 3);
		Vector.checkBound(to, 3);
		return new float[]{to[0] - from[0], to[1] - from[1], to[2] - from[2]};
	}

	/**
	 * Double functions
	 */

	private static void checkBound(double[] vec, int desiredLength) {
		if (vec.length != desiredLength) {
			throw new IllegalArgumentException("float[" + desiredLength + "] needed (actual length : " + vec.length + ") !");
		}
	}

	public static double[] div3d(double[] u, double x, boolean isUCounter) {
		Vector.checkBound(u, 3);
		if (isUCounter) {
			return new double[]{u[0] / x, u[1] / x, u[2] / x};
		} else {
			return new double[]{x / u[0], x / u[1], x / u[2]};
		}
	}

	public static double[] inv3d(double[] u) {
		Vector.checkBound(u, 3);
		return Vector.div3d(u, 1, false);
	}

	public static double[] mul3d(double[] u, double x) {
		Vector.checkBound(u, 3);
		return new double[]{u[0] * x, u[1] * x, u[2] * x};
	}

	public static double[] add3d(double[] u, double[] v) {
		Vector.checkBound(u, 3);
		Vector.checkBound(v, 3);
		return new double[]{u[0] + v[0], u[1] + v[1], u[2] + v[2]};
	}

	public static double dot3d(double[] u, double[] v) {
		Vector.checkBound(u, 3);
		Vector.checkBound(v, 3);
		return u[0] * v[0] + u[1] * v[1] + u[2] * v[2];
	}

	public static double[] cross3d(double[] u, double[] v) {
		Vector.checkBound(u, 3);
		Vector.checkBound(v, 3);
		return new double[]{u[1] * v[2] - u[2] * v[1], u[2] * v[0] - u[0] * v[2], u[0] * v[1] - u[1] * v[0]};
	}

	public static double[] normalize3d(double[] u) {
		Vector.checkBound(u, 3);
		double lgt = Vector.length3d(u);
		return new double[]{u[0] / lgt, u[1] / lgt, u[2] / lgt};
	}

	public static double length3d(double[] u) {
		Vector.checkBound(u, 3);
		return Math.sqrt(Math.pow(u[0], 2d) + Math.pow(u[1], 2d) + Math.pow(u[2], 2d));
	}

	public static double[] make3d(double[] from, double[] to) {
		Vector.checkBound(from, 3);
		Vector.checkBound(to, 3);
		return new double[]{to[0] - from[0], to[1] - from[1], to[2] - from[2]};
	}
}
