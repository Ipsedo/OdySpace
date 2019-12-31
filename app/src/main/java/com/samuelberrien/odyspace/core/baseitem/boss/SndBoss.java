package com.samuelberrien.odyspace.core.baseitem.boss;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.core.baseitem.BaseItem;
import com.samuelberrien.odyspace.core.baseitem.ship.Ship;
import com.samuelberrien.odyspace.core.fire.FireType;
import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.utils.maths.BezierCurve;
import com.samuelberrien.odyspace.utils.maths.Vector;

import java.util.List;
import java.util.Random;

public class SndBoss extends Boss {

	private int counter;
	private static final int MAX_COUNT = 300;

	private Random rand;

	private Ship ship;

	private int maxBezierPoint;
	private float[] lastPoint;
	private BezierCurve bezierCurveX;
	private BezierCurve bezierCurveY;
	private BezierCurve bezierCurveZ;

	public SndBoss(Context context, float[] mPosition, List<BaseItem> rockets, Ship ship) {
		super(context, "obj/skull_2.obj", "obj/skull_2.mtl", 40, mPosition, 5.f, FireType.GUIDED_MISSILE.getFire(context), rockets);
		counter = 0;
		rand = new Random(System.currentTimeMillis());
		this.ship = ship;

		bezierCurveX = new BezierCurve();
		bezierCurveY = new BezierCurve();
		bezierCurveZ = new BezierCurve();

		lastPoint = mPosition.clone();

		maxBezierPoint = 12;

		for (int i = 0; i < maxBezierPoint; i++) {
			lastPoint[0] += rand.nextFloat();
			lastPoint[1] += rand.nextFloat();
			lastPoint[2] += rand.nextFloat();

			bezierCurveX.add(lastPoint[0]);
			bezierCurveY.add(lastPoint[1]);
			bezierCurveZ.add(lastPoint[2]);
		}

	}

	private void count() {
		counter = (counter + 1) % MAX_COUNT;
	}

	@Override
	public void fire() {
		if (rand.nextFloat() < 5e-2f) {
			float[] speedVec = Vector.normalize3f(vector3fTo(ship));
			float[] originaleVec = new float[]{0f, 0f, 1f};
			float angle = (float) (Math.acos(Vector.dot3f(speedVec, originaleVec)) * 360d / (Math.PI * 2d));
			float[] rotAxis = Vector.cross3f(originaleVec, speedVec);
			float[] tmpMat = new float[16];
			Matrix.setRotateM(tmpMat, 0, angle, rotAxis[0], rotAxis[1], rotAxis[2]);
			fire.fire(rockets, mPosition, mSpeed, tmpMat, 0.7f, ship);
		}
	}

	@Override
	protected float[] computeModelMatrix() {
		float t = (float) counter / (float) MAX_COUNT;
		mPosition[0] = bezierCurveX.get(t);
		mPosition[1] = bezierCurveY.get(t);
		mPosition[2] = bezierCurveZ.get(t);


		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, mPosition[0], mPosition[1], mPosition[2]);

		float[] from = new float[]{0.f, 0.f, -1.f};
		float[] to = Vector.normalize3f(vector3fTo(ship));

		float[] axis = Vector.cross3f(from, to);

		float angle = (float) Math.acos(Vector.dot3f(from, to));
		Matrix.setRotateM(mRotationMatrix, 0, angle * 360.f, axis[0], axis[1], axis[2]);

		Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix.clone(), 0, mRotationMatrix, 0);
		Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);

		return mModelMatrix;
	}

	@Override
	protected Explosion getExplosion() {
		throw new UnsupportedOperationException("SndBoss.getExplosion()");
	}

	@Override
	public void update() {
		count();

		if (counter == 0) {
			lastPoint = mPosition.clone();

			bezierCurveX.clear();
			bezierCurveY.clear();
			bezierCurveZ.clear();

			for (int i = 0; i < maxBezierPoint; i++) {
				bezierCurveX.add(lastPoint[0]);
				bezierCurveY.add(lastPoint[1]);
				bezierCurveZ.add(lastPoint[2]);

				lastPoint[0] += rand.nextFloat() * 10.f - 5.f;
				lastPoint[1] += rand.nextFloat() * 10.f - 5.f;
				lastPoint[2] += rand.nextFloat() * 10.f - 5.f;
			}
		}

		super.update();
	}
}
