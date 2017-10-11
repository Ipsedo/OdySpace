package com.samuelberrien.odyspace.objects.baseitem.shooters;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.explosion.Explosion;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.objects.baseitem.BaseItem;
import com.samuelberrien.odyspace.objects.crashable.CrashableMesh;
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

	private Explosion explosion;

	private FireType fireType;

	private Ship ship;

	private List<BaseItem> rockets;

	//TODO modèles simplifiés pr crashable ?
	public Turret(Context context, float[] mPosition, FireType fireType, Ship ship, List<BaseItem> rockets) {
		super(context, "turret.obj", "turret.mtl", "turret.obj", 1f, 0f, false, 1, mPosition, new float[3], new float[3], 4f);
		rand = new Random(System.currentTimeMillis());
		this.fireType = fireType;
		this.ship = ship;
		this.rockets = rockets;
	}

	public Turret(Context context, ObjModelMtlVBO turret, CrashableMesh crashableMesh, float[] mPosition, FireType fireType, Ship ship, List<BaseItem> rockets) {
		super(context, turret, crashableMesh, 1, mPosition, new float[3], new float[3], 4f);
		rand = new Random(System.currentTimeMillis());
		this.fireType = fireType;
		this.ship = ship;
		this.rockets = rockets;
	}

	@Override
	protected Explosion getExplosion() {
		return new Explosion.ExplosionBuilder().setNbParticules(10)
				.setLimitSpeedAlife(0.05f)
				.setLimitScale(1f)
				.setMaxScale(2f)
				.setLimitSpeed(1f)
				.setMaxSpeed(1.5f)
				.makeExplosion(context, objModelMtlVBO.getRandomMtlDiffRGBA());
	}

	@Override
	public void fire() {
		float[] vectorTo = super.vector3fTo(ship);
		if (Vector.length3f(vectorTo) < 200f && rand.nextFloat() < 1e-2f) {
			float[] speedVec = Vector.normalize3f(vectorTo);
			float[] originaleVec = new float[]{0f, 0f, 1f};
			float angle = (float) (Math.acos(Vector.dot3f(speedVec, originaleVec)) * 360d / (Math.PI * 2d));
			float[] rotAxis = Vector.cross3f(originaleVec, speedVec);
			float[] tmpMat = new float[16];
			Matrix.setRotateM(tmpMat, 0, angle, rotAxis[0], rotAxis[1], rotAxis[2]);
			fireType.fire(rockets, super.mPosition.clone(), originaleVec.clone(), tmpMat.clone(), 0.5f, ship);
		}
	}

	@Override
	public void update() {
		float[] shipPos = ship.clonePosition();
		float[] u = new float[]{shipPos[0] - super.mPosition[0], 0f, shipPos[2] - super.mPosition[2]};
		float[] v = new float[]{0f, 0f, 1f};

		float[] cross = Vector.normalize3f(Vector.cross3f(Vector.normalize3f(v), Vector.normalize3f(u)));
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
