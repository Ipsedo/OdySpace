package com.samuelberrien.odyspace.objects.baseitem;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.utils.game.FireType;
import com.samuelberrien.odyspace.utils.game.Shooter;
import com.samuelberrien.odyspace.utils.maths.Vector;

import java.util.List;
import java.util.Random;

/**
 * Created by samuel on 15/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Turret extends BaseItem implements Shooter {

	private Random rand;

	private ObjModelMtlVBO rocket;

	private Explosion explosion;

	private FireType fireType;

	private Ship ship;

	private List<BaseItem> rockets;

	public Turret(Context context, float[] mPosition, FireType fireType, Ship ship, List<BaseItem> rockets) {
		super(context, "turret.obj", "turret.mtl", 1f, 0f, false, 1, mPosition, new float[3], new float[3], 4f);
		this.rand = new Random(System.currentTimeMillis());
		this.rocket = new ObjModelMtlVBO(context, "rocket.obj", "rocket.mtl", 1f, 0f, false);
		this.fireType = fireType;
		this.ship = ship;
		this.rockets = rockets;
	}

	public Turret(ObjModelMtlVBO turret, ObjModelMtlVBO rocket, float[] mPosition, FireType fireType, Ship ship, List<BaseItem> rockets) {
		super(turret, 1, mPosition, new float[3], new float[3], 4f);
		this.rand = new Random(System.currentTimeMillis());
		this.rocket = rocket;
		this.fireType = fireType;
		this.ship = ship;
		this.rockets = rockets;
	}

	public void makeExplosion(Context context) {
		this.explosion = new Explosion(context, super.mPosition.clone(), super.diffColorBuffer, 10, 0.05f, 1f, 2f, 1.0f, 1.5f);
	}

	public void addExplosion(List<Explosion> explosions) {
		explosions.add(this.explosion);
	}

	@Override
	public void fire() {
		if (this.rand.nextFloat() < 1e-2f) {
			float[] speedVec = Vector.normalize3f(super.vector3fTo(this.ship));
			float[] originaleVec = new float[]{0f, 0f, 1f};
			float angle = (float) (Math.acos(Vector.dot3f(speedVec, originaleVec)) * 360d / (Math.PI * 2d));
			float[] rotAxis = Vector.cross3f(originaleVec, speedVec);
			float[] tmpMat = new float[16];
			Matrix.setRotateM(tmpMat, 0, angle, rotAxis[0], rotAxis[1], rotAxis[2]);
			this.fireType.fire(this.rocket, this.rockets, super.mPosition.clone(), originaleVec, tmpMat, 0.5f);
		}
	}

	@Override
	public void move() {
		float[] u = new float[]{this.ship.mPosition[0] - super.mPosition[0], 0f, this.ship.mPosition[2] - super.mPosition[2]};
		float[] v = new float[]{0f, 0f, 1f};

		float[] cross = Vector.normalize3f(Vector.cross3f(Vector.normalize3f(u), Vector.normalize3f(v)));
		double angle = Math.acos(Vector.dot3f(Vector.normalize3f(u), Vector.normalize3f(v)));

		Matrix.setRotateM(super.mRotationMatrix, 0, (float) Math.toDegrees(angle), cross[0], cross[1], cross[2]);

		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, super.mPosition[0], super.mPosition[1], super.mPosition[2]);
		Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix.clone(), 0, super.mRotationMatrix, 0);
		Matrix.scaleM(mModelMatrix, 0, super.scale, super.scale, super.scale);

		super.mModelMatrix = mModelMatrix.clone();
	}
}
