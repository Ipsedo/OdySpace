package com.samuelberrien.odyspace.core.baseitem.boss;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.core.baseitem.BaseItem;
import com.samuelberrien.odyspace.core.baseitem.ship.Ship;
import com.samuelberrien.odyspace.core.fire.FireType;
import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.utils.maths.Vector;

import java.util.List;
import java.util.Random;

/**
 * Created by samuel on 10/07/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class FstBoss extends Boss {

	private Ship ship;
	private float phi;
	private float theta;
	private Random rand;

	private int colorCounter;
	private boolean changingColor;

	private int counter;

	private final int MAX_COUNT = 200;

	public FstBoss(Context context, float[] mPosition, Ship ship, List<BaseItem> rockets) {
		super(context, "obj/skull.obj", "obj/skull.mtl", 20, mPosition, 3f, FireType.SIMPLE_FIRE.getFire(context), rockets);
		this.ship = ship;
		phi = 0f;
		theta = 0f;
		counter = 0;
		rand = new Random(System.currentTimeMillis());
		colorCounter = 0;
		changingColor = false;
	}

	@Override
	public void fire() {
		if (rand.nextFloat() < 1e-1f) {
			float[] speedVec = Vector.normalize3f(super.vector3fTo(ship));
			float[] originaleVec = new float[]{0f, 0f, 1f};
			float angle = (float) (Math.acos(Vector.dot3f(speedVec, originaleVec)) * 360d / (Math.PI * 2d));
			float[] rotAxis = Vector.cross3f(originaleVec, speedVec);
			float[] tmpMat = new float[16];
			Matrix.setRotateM(tmpMat, 0, angle, rotAxis[0], rotAxis[1], rotAxis[2]);
			fire.fire(super.rockets, super.mPosition.clone(), originaleVec.clone(), tmpMat.clone(), 0.3f);
		}
	}

	private void count() {
		counter = (counter > MAX_COUNT ? 0 : counter + 1);
		if (changingColor && colorCounter > 75) {
			objModelMtlVBO.changeColor();
			changingColor = false;
			colorCounter = 0;
		} else if (changingColor) {
			colorCounter++;
		}
	}

	@Override
	public void decrementLife(int minus) {
		if (minus > 0 && !changingColor) {
			changingColor = true;
			objModelMtlVBO.changeColor();
		}
		super.life = super.life - minus >= 0 ? super.life - minus : 0;
	}

	@Override
	protected float[] computeModelMatrix() {
		float[] vecToShip = Vector.normalize3f(super.vector3fTo(ship));

		if (rand.nextFloat() < 1e-2f) {
			super.mSpeed[0] = 0.1f * vecToShip[0];
			super.mSpeed[1] = 0.1f * vecToShip[1];
			super.mSpeed[2] = 0.1f * vecToShip[2];
		} else {
			phi += (rand.nextDouble() * 2d - 1d) / Math.PI;
			theta += (rand.nextDouble() * 2d - 1d) / Math.PI;
			super.mSpeed[0] = 0.1f * (float) (Math.cos(phi) * Math.sin(theta));
			super.mSpeed[1] = 0.1f * (float) Math.sin(phi);
			super.mSpeed[2] = 0.1f * (float) (Math.cos(phi) * Math.cos(theta));
		}

		super.mPosition[0] += super.mSpeed[0];
		super.mPosition[1] += super.mSpeed[1];
		super.mPosition[2] += super.mSpeed[2];

		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, super.mPosition[0], super.mPosition[1], super.mPosition[2]);

		float[] from = new float[]{0.f, 0.f, -1.f};
		float[] to = Vector.normalize3f(vector3fTo(ship));

		float[] axis = Vector.cross3f(from, to);

		float angle = (float) Math.acos(Vector.dot3f(from, to));
		Matrix.setRotateM(mRotationMatrix, 0, angle * 360.f, axis[0], axis[1], axis[2]);

		Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix.clone(), 0, mRotationMatrix, 0);
		Matrix.scaleM(mModelMatrix, 0, super.scale, super.scale, super.scale);

		return mModelMatrix;
	}

	@Override
	public void update() {
		super.update();
		count();
	}

	@Override
	protected Explosion getExplosion() {
		throw new UnsupportedOperationException("FstBoss.getExplosion()");
	}
}
