package com.samuelberrien.odyspace.utils.maths;

class BernsteinCoefficient {

	private static int fact(int n) {
		int res = 1;
		for (int i = 1; i <= n; i++)
			res *= i;
		return res;
	}

	private static int binomial(int n, int k) {
		return fact(n) / (fact(k) * fact(n - k));
	}

	static float bernstein(float u, int m, int i) {
		return binomial(m, i) * (float) (Math.pow(u, i) * Math.pow(1 - u, m - i));
	}
}
