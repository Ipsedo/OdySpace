package com.samuelberrien.odyspace.utils.maths;

/**
 * Created by samuel on 20/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public final class Triangle {

	public static float CalcY(float[] p1, float[] p2, float[] p3, float x, float z) {
		float det = (p2[2] - p3[2]) * (p1[0] - p3[0]) + (p3[0] - p2[0]) * (p1[2] - p3[2]);

		float l1 = ((p2[2] - p3[2]) * (x - p3[0]) + (p3[0] - p2[0]) * (z - p3[2])) / det;
		float l2 = ((p3[2] - p1[2]) * (x - p3[0]) + (p1[0] - p3[0]) * (z - p3[2])) / det;
		float l3 = 1.0f - l1 - l2;

		return l1 * p1[1] + l2 * p2[1] + l3 * p3[1];
	}
}
