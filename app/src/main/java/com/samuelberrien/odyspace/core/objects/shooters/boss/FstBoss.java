package com.samuelberrien.odyspace.core.objects.shooters.boss;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.explosion.Explosion;
import com.samuelberrien.odyspace.core.objects.BaseItem;
import com.samuelberrien.odyspace.core.objects.shooters.Ship;
import com.samuelberrien.odyspace.core.fire.FireType;
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

	public FstBoss(Context context, float[] mPosition, Ship ship, List<BaseItem> rockets) {
		super(context, "obj/skull.obj", "obj/skull.mtl", 20, mPosition, 3f, FireType.SIMPLE_FIRE.getFire(context), rockets);
		this.ship = ship;
		phi = 0f;
		theta = 0f;
		rand = new Random(System.currentTimeMillis());
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

	@Override
	protected float[] computeModelMatrix() {
		float[] vecToShip = Vector.normalize3f(super.vector3fTo(ship));
		float[] originaleVec = new float[]{0f, 0f, 1f};
		float angle;

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

		angle = (float) Math.toDegrees(Math.acos(Vector.dot3f(vecToShip, originaleVec)));
		float[] mRotationMatrix = new float[16];
		Matrix.setRotateM(mRotationMatrix, 0, angle, 0f, 1f, 0f);
		Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix.clone(), 0, mRotationMatrix, 0);
		Matrix.scaleM(mModelMatrix, 0, super.scale, super.scale, super.scale);

		return mModelMatrix;
	}

	@Override
	protected Explosion getExplosion() {
		throw new UnsupportedOperationException("FstBoss.getExplosion()");
	}
}
