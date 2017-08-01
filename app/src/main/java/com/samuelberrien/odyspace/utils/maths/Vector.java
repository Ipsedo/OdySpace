package com.samuelberrien.odyspace.utils.maths;

/**
 * Created by samuel on 19/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public final class Vector {

	private static void checkBound(int length, int desiredLength) {
		if (length != desiredLength) {
			throw new IllegalArgumentException("float[" + desiredLength + "] needed (actual length : " + length + ") !");
		}
	}

	public static float[] div3f(float[] u, float x, boolean isUCounter) {
		Vector.checkBound(u.length, 3);
		if (isUCounter) {
			return new float[]{u[0] / x, u[1] / x, u[2] / x};
		} else {
			return new float[]{x / u[0], x / u[1], x / u[2]};
		}
	}

	public static float[] inv3f(float[] u) {
		Vector.checkBound(u.length, 3);
		return Vector.div3f(u, 1, false);
	}

	public static float[] mul3f(float[] u, float x) {
		Vector.checkBound(u.length, 3);
		return new float[]{u[0] * x, u[1] * x, u[2] * x};
	}

	public static float[] add3f(float[] u, float[] v) {
		Vector.checkBound(u.length, 3);
		Vector.checkBound(v.length, 3);
		return new float[]{u[0] + v[0], u[1] + v[1], u[2] + v[2]};
	}

	public static float dot3f(float[] u, float[] v) {
		Vector.checkBound(u.length, 3);
		Vector.checkBound(v.length, 3);
		return u[0] * v[0] + u[1] * v[1] + u[2] * v[2];
	}

	public static float[] cross3f(float[] u, float[] v) {
		Vector.checkBound(u.length, 3);
		Vector.checkBound(v.length, 3);
		return new float[]{u[1] * v[2] - u[2] * v[1], u[2] * v[0] - u[0] * v[2], u[0] * v[1] - u[1] * v[0]};
	}

	public static float[] normalize3f(float[] u) {
		Vector.checkBound(u.length, 3);
		float lgt = Vector.length3f(u);
		return new float[]{u[0] / lgt, u[1] / lgt, u[2] / lgt};
	}

	public static float length3f(float[] u) {
		Vector.checkBound(u.length, 3);
		return (float) Math.sqrt(Math.pow(u[0], 2d) + Math.pow(u[1], 2d) + Math.pow(u[2], 2d));
	}
}
