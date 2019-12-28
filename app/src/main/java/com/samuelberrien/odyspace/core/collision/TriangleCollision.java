package com.samuelberrien.odyspace.core.collision;

import android.opengl.Matrix;

import com.samuelberrien.odyspace.utils.maths.Vector;

import java.util.Arrays;


/*
 *
 *  Triangle-Triangle Overlap Test Routines
 *  July, 2002
 *  Updated December 2003
 *
 *  This file contains C implementation of algorithms for
 *  performing two and three-dimensional triangle-triangle intersection test
 *  The algorithms and underlying theory are described in
 *
 * "Fast and Robust Triangle-Triangle Overlap Test
 *  Using Orientation Predicates"  P. Guigue - O. Devillers
 *
 *  Journal of Graphics Tools, 8(1), 2003
 *
 *  Several geometric predicates are defined.  Their parameters are all
 *  points.  Each point is an array of two or three double precision
 *  floating point numbers. The geometric predicates implemented in
 *  this file are:
 *
 *    int tri_tri_overlap_test_3d(p1,q1,r1,p2,q2,r2)
 *    int tri_tri_overlap_test_2d(p1,q1,r1,p2,q2,r2)
 *
 *    int tri_tri_intersection_test_3d(p1,q1,r1,p2,q2,r2,
 *                                     coplanar,source,target)
 *
 *       is a version that computes the segment of intersection when
 *       the triangles overlap (and are not coplanar)
 *
 *    each function returns 1 if the triangles (including their
 *    boundary) intersect, otherwise 0
 *
 *
 *  Other information are available from the Web page
 *  http://www.acm.org/jgt/papers/GuigueDevillers03/
 *
 */

public class TriangleCollision {

	private static boolean intersectionTestEdge(float[] P1, float[] Q1, float[] R1,
												float[] P2, float[] Q2, float[] R2) {
		if (orient2D(R2, P2, Q1) >= 0.0f) {
			if (orient2D(P1, P2, Q1) >= 0.0f) {
				return orient2D(P1, Q1, R2) >= 0.0f;
			} else {
				if (orient2D(Q1, R1, P2) >= 0.0f) {
					return orient2D(R1, P1, P2) >= 0.0f;
				} else return false;
			}
		} else {
			if (orient2D(R2, P2, R1) >= 0.0f) {
				if (orient2D(P1, P2, R1) >= 0.0f) {
					if (orient2D(P1, R1, R2) >= 0.0f) return true;
					else {
						return orient2D(Q1, R1, R2) >= 0.0f;
					}
				} else return false;
			} else return false;
		}
	}

	private static boolean intersectionTestVertex(float[] P1, float[] Q1, float[] R1,
												  float[] P2, float[] Q2, float[] R2) {
		if (orient2D(R2, P2, Q1) >= 0.0f)
			if (orient2D(R2, Q2, Q1) <= 0.0f)
				if (orient2D(P1, P2, Q1) > 0.0f) {
					return orient2D(P1, Q2, Q1) <= 0.0f;
				} else {
					if (orient2D(P1, P2, R1) >= 0.0f)
						return orient2D(Q1, R1, P2) >= 0.0f;
					else return false;
				}
			else if (orient2D(P1, Q2, Q1) <= 0.0f)
				if (orient2D(R2, Q2, R1) <= 0.0f)
					return orient2D(Q1, R1, Q2) >= 0.0f;
				else return false;
			else return false;
		else if (orient2D(R2, P2, R1) >= 0.0f)
			if (orient2D(Q1, R1, R2) >= 0.0f)
				return orient2D(P1, P2, R1) >= 0.0f;
			else if (orient2D(Q1, R1, Q2) >= 0.0f)
				return orient2D(R2, R1, Q2) >= 0.0f;
			else return false;
		else return false;
	}

	private static boolean ccwTriTriIntersection2D(float[] p1, float[] q1, float[] r1,
												   float[] p2, float[] q2, float[] r2) {
		if (orient2D(p2, q2, p1) >= 0.0f) {
			if (orient2D(q2, r2, p1) >= 0.0f) {
				if (orient2D(r2, p2, p1) >= 0.0f) return true;
				else return intersectionTestEdge(p1, q1, r1, p2, q2, r2);
			} else {
				if (orient2D(r2, p2, p1) >= 0.0f)
					return intersectionTestEdge(p1, q1, r1, r2, p2, q2);
				else return intersectionTestVertex(p1, q1, r1, p2, q2, r2);
			}
		} else {
			if (orient2D(q2, r2, p1) >= 0.0f) {
				if (orient2D(r2, p2, p1) >= 0.0f)
					return intersectionTestEdge(p1, q1, r1, q2, r2, p2);
				else return intersectionTestVertex(p1, q1, r1, q2, r2, p2);
			} else return intersectionTestVertex(p1, q1, r1, r2, p2, q2);
		}
	}

	private static float orient2D(float[] a, float[] b, float[] c) {
		return (a[0] - c[0]) * (b[1] - c[1]) - (a[1] - c[1]) * (b[0] - c[0]);
	}

	private static boolean triTriOverlapTest2D(float[] p1, float[] q1, float[] r1,
											   float[] p2, float[] q2, float[] r2) {
		if (orient2D(p1, q1, r1) < 0.0f)
			if (orient2D(p2, q2, r2) < 0.0f)
				return ccwTriTriIntersection2D(p1, r1, q1, p2, r2, q2);
			else
				return ccwTriTriIntersection2D(p1, r1, q1, p2, q2, r2);
		else if (orient2D(p2, q2, r2) < 0.0f)
			return ccwTriTriIntersection2D(p1, q1, r1, p2, r2, q2);
		else
			return ccwTriTriIntersection2D(p1, q1, r1, p2, q2, r2);

	}

	private static boolean coplanarTriTri3D(float[] p1, float[] q1, float[] r1,
											float[] p2, float[] q2, float[] r2,
											float[] normal_1, float[] normal_2) {
		float[] P1 = new float[2], Q1 = new float[2], R1 = new float[2];
		float[] P2 = new float[2], Q2 = new float[2], R2 = new float[2];

		double n_x, n_y, n_z;

		n_x = ((normal_1[0] < 0) ? -normal_1[0] : normal_1[0]);
		n_y = ((normal_1[1] < 0) ? -normal_1[1] : normal_1[1]);
		n_z = ((normal_1[2] < 0) ? -normal_1[2] : normal_1[2]);


    /* Projection of the triangles in 3D onto 2D such that the area of
       the projection is maximized. */


		if ((n_x > n_z) && (n_x >= n_y)) {
			// Project onto plane YZ

			P1[0] = q1[2];
			P1[1] = q1[1];
			Q1[0] = p1[2];
			Q1[1] = p1[1];
			R1[0] = r1[2];
			R1[1] = r1[1];

			P2[0] = q2[2];
			P2[1] = q2[1];
			Q2[0] = p2[2];
			Q2[1] = p2[1];
			R2[0] = r2[2];
			R2[1] = r2[1];

		} else if ((n_y > n_z) && (n_y >= n_x)) {
			// Project onto plane XZ

			P1[0] = q1[0];
			P1[1] = q1[2];
			Q1[0] = p1[0];
			Q1[1] = p1[2];
			R1[0] = r1[0];
			R1[1] = r1[2];

			P2[0] = q2[0];
			P2[1] = q2[2];
			Q2[0] = p2[0];
			Q2[1] = p2[2];
			R2[0] = r2[0];
			R2[1] = r2[2];

		} else {
			// Project onto plane XY

			P1[0] = p1[0];
			P1[1] = p1[1];
			Q1[0] = q1[0];
			Q1[1] = q1[1];
			R1[0] = r1[0];
			R1[1] = r1[1];

			P2[0] = p2[0];
			P2[1] = p2[1];
			Q2[0] = q2[0];
			Q2[1] = q2[1];
			R2[0] = r2[0];
			R2[1] = r2[1];
		}

		return triTriOverlapTest2D(P1, Q1, R1, P2, Q2, R2);
	}

	private static boolean checkMinMax(float[] p1, float[] q1, float[] r1,
									   float[] p2, float[] q2, float[] r2) {
		float[] v1, v2;
		float[] N1;
		v1 = Vector.sub3f(p2, q1);
		v2 = Vector.sub3f(p1, q1);
		N1 = Vector.cross3f(v1, v2);
		v1 = Vector.sub3f(q2, q1);

		if (Vector.dot3f(v1, N1) > 0.0f) return false;

		v1 = Vector.sub3f(p2, p1);
		v2 = Vector.sub3f(r1, p1);
		N1 = Vector.cross3f(v1, v2);
		v1 = Vector.sub3f(r2, p1);

		return !(Vector.dot3f(v1, N1) > 0.0f);
	}

	private static boolean triTri3D(float[] p1, float[] q1, float[] r1,
									float[] p2, float[] q2, float[] r2,
									float dp2, float dq2, float dr2,
									float[] N1, float[] N2) {
		if (dp2 > 0.0f) {
			if (dq2 > 0.0f) return checkMinMax(p1, r1, q1, r2, p2, q2);
			else if (dr2 > 0.0f) return checkMinMax(p1, r1, q1, q2, r2, p2);
			else return checkMinMax(p1, q1, r1, p2, q2, r2);
		} else if (dp2 < 0.0f) {
			if (dq2 < 0.0f) return checkMinMax(p1, q1, r1, r2, p2, q2);
			else if (dr2 < 0.0f) return checkMinMax(p1, q1, r1, q2, r2, p2);
			else return checkMinMax(p1, r1, q1, p2, q2, r2);
		} else {
			if (dq2 < 0.0f) {
				if (dr2 >= 0.0f) return checkMinMax(p1, r1, q1, q2, r2, p2);
				else return checkMinMax(p1, q1, r1, p2, q2, r2);
			} else if (dq2 > 0.0f) {
				if (dr2 > 0.0f) return checkMinMax(p1, r1, q1, p2, q2, r2);
				else return checkMinMax(p1, q1, r1, q2, r2, p2);
			} else {
				if (dr2 > 0.0f) return checkMinMax(p1, q1, r1, r2, p2, q2);
				else if (dr2 < 0.0f) return checkMinMax(p1, r1, q1, r2, p2, q2);
				else return coplanarTriTri3D(p1, q1, r1, p2, q2, r2, N1, N2);
			}
		}
	}

	private static boolean triTriOverlapTest3D(float[] p1, float[] q1, float[] r1,
											   float[] p2, float[] q2, float[] r2) {

		float dp1, dq1, dr1, dp2, dq2, dr2;
		float[] v1, v2;
		float[] N1, N2;

		v1 = Vector.sub3f(p2, r2);
		v2 = Vector.sub3f(q2, r2);

		N2 = Vector.cross3f(v1, v2);

		v1 = Vector.sub3f(p1, r2);
		dp1 = Vector.dot3f(v1, N2);
		v1 = Vector.sub3f(q1, r2);
		dq1 = Vector.dot3f(v1, N2);
		v1 = Vector.sub3f(r1, r2);
		dr1 = Vector.dot3f(v1, N2);

		if ((dp1 * dq1 > 0.0f) && (dp1 * dr1 > 0.0f)) return false;

		/* Compute distance signs  of p2, q2 and r2 to the plane of
       triangle(p1,q1,r1) */


		v1 = Vector.sub3f(q1, p1);
		v2 = Vector.sub3f(r1, p1);
		N1 = Vector.cross3f(v1, v2);

		v1 = Vector.sub3f(p2, r1);
		dp2 = Vector.dot3f(v1, N1);
		v1 = Vector.sub3f(q2, r1);
		dq2 = Vector.dot3f(v1, N1);
		v1 = Vector.sub3f(r2, r1);
		dr2 = Vector.dot3f(v1, N1);

		if ((dp2 * dq2 > 0.0f) && (dp2 * dr2 > 0.0f)) return false;

		if (dp1 > 0.0f) {
			if (dq1 > 0.0f) return triTri3D(r1, p1, q1, p2, r2, q2, dp2, dr2, dq2, N1, N2);
			else if (dr1 > 0.0f) return triTri3D(q1, r1, p1, p2, r2, q2, dp2, dr2, dq2, N1, N2);
			else return triTri3D(p1, q1, r1, p2, q2, r2, dp2, dq2, dr2, N1, N2);
		} else if (dp1 < 0.0f) {
			if (dq1 < 0.0f) return triTri3D(r1, p1, q1, p2, q2, r2, dp2, dq2, dr2, N1, N2);
			else if (dr1 < 0.0f) return triTri3D(q1, r1, p1, p2, q2, r2, dp2, dq2, dr2, N1, N2);
			else return triTri3D(p1, q1, r1, p2, r2, q2, dp2, dr2, dq2, N1, N2);
		} else {
			if (dq1 < 0.0f) {
				if (dr1 >= 0.0f) return triTri3D(q1, r1, p1, p2, r2, q2, dp2, dr2, dq2, N1, N2);
				else return triTri3D(p1, q1, r1, p2, q2, r2, dp2, dq2, dr2, N1, N2);
			} else if (dq1 > 0.0f) {
				if (dr1 > 0.0f) return triTri3D(p1, q1, r1, p2, r2, q2, dp2, dr2, dq2, N1, N2);
				else return triTri3D(q1, r1, p1, p2, q2, r2, dp2, dq2, dr2, N1, N2);
			} else {
				if (dr1 > 0.0f) return triTri3D(r1, p1, q1, p2, q2, r2, dp2, dq2, dr2, N1, N2);
				else if (dr1 < 0.0f)
					return triTri3D(r1, p1, q1, p2, r2, q2, dp2, dr2, dq2, N1, N2);
				else return coplanarTriTri3D(p1, q1, r1, p2, q2, r2, N1, N2);
			}
		}
	}

	public static boolean AreCollided(float[] item1Points, float[] item1ModelMatrix,
									  float[] item2Points, float[] item2ModelMatrix) {

		if (item1Points.length % 9 != 0 || item2Points.length % 9 != 0)
			throw new IllegalStateException("Points array must be modulo 9 (ie flatten array of triangle)");

		for (int i = 0; i < item1Points.length / 9; i++) {
			int curr_t = i * 9;

			float[] u0 = {item1Points[curr_t], item1Points[curr_t + 1], item1Points[curr_t + 2], 1.f},
					u1 = {item1Points[curr_t + 3], item1Points[curr_t + 4], item1Points[curr_t + 5], 1.f},
					u2 = {item1Points[curr_t + 6], item1Points[curr_t + 7], item1Points[curr_t + 8], 1.f};

			Matrix.multiplyMV(u0, 0, item1ModelMatrix, 0, u0.clone(), 0);
			Matrix.multiplyMV(u1, 0, item1ModelMatrix, 0, u1.clone(), 0);
			Matrix.multiplyMV(u2, 0, item1ModelMatrix, 0, u2.clone(), 0);

			System.arraycopy(u0, 0, item1Points, curr_t, 3);
			System.arraycopy(u1, 0, item1Points, curr_t + 3, 3);
			System.arraycopy(u2, 0, item1Points, curr_t + 6, 3);
		}

		for (int i = 0; i < item2Points.length / 9; i++) {
			int curr_t_1 = i * 9;

			float[] v0 = {item2Points[curr_t_1], item2Points[curr_t_1 + 1], item2Points[curr_t_1 + 2], 1.f},
					v1 = {item2Points[curr_t_1 + 3], item2Points[curr_t_1 + 4], item2Points[curr_t_1 + 5], 1.f},
					v2 = {item2Points[curr_t_1 + 6], item2Points[curr_t_1 + 7], item2Points[curr_t_1 + 8], 1.f};

			Matrix.multiplyMV(v0, 0, item2ModelMatrix, 0, v0.clone(), 0);
			Matrix.multiplyMV(v1, 0, item2ModelMatrix, 0, v1.clone(), 0);
			Matrix.multiplyMV(v2, 0, item2ModelMatrix, 0, v2.clone(), 0);

			v0 = Arrays.copyOfRange(v0, 0, 3);
			v1 = Arrays.copyOfRange(v1, 0, 3);
			v2 = Arrays.copyOfRange(v2, 0, 3);

			for (int j = 0; j < item1Points.length / 9; j++) {
				int curr_t_2 = j * 9;

				float[] u0 = {item1Points[curr_t_2], item1Points[curr_t_2 + 1], item1Points[curr_t_2 + 2]},
						u1 = {item1Points[curr_t_2 + 3], item1Points[curr_t_2 + 4], item1Points[curr_t_2 + 5]},
						u2 = {item1Points[curr_t_2 + 6], item1Points[curr_t_2 + 7], item1Points[curr_t_2 + 8]};

				if (triTriOverlapTest3D(v0, v1, v2, u0, u1, u2)) return true;
			}
		}

		return false;
	}
}
