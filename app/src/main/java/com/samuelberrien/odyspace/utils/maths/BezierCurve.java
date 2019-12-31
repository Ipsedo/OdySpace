package com.samuelberrien.odyspace.utils.maths;

import java.util.ArrayList;

public class BezierCurve {

	private ArrayList<Float> points;

	public BezierCurve() {
		points = new ArrayList<>();
	}

	public void add(float point) {
		points.add(point);
	}

	public float get(float t) {
		if (points.size() < 2)
			throw new IllegalStateException("Bezier curve needs at least 2 points !");

		float sum = 0;
		for (int i = 0; i < points.size() - 1; i++)
			sum += BernsteinCoefficient.bernstein(t, points.size() - 1, i) * points.get(i);

		return sum;
	}

	public void clear() {
		points.clear();
	}
}
