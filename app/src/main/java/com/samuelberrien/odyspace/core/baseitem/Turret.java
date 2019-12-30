package com.samuelberrien.odyspace.core.baseitem;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.core.Shooter;
import com.samuelberrien.odyspace.core.baseitem.ship.Ship;
import com.samuelberrien.odyspace.core.collision.CollisionMesh;
import com.samuelberrien.odyspace.core.fire.Fire;
import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.drawable.ObjModelMtlVBO;
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

	private Fire fire;

	private Ship ship;

	private List<BaseItem> rockets;

	//TODO modèles simplifiés pr crashable ?
	public Turret(Context context, float[] mPosition, Fire fire, Ship ship, List<BaseItem> rockets) {
		super(context, "obj/turret.obj", "obj/turret.mtl", "obj/turret.obj", 1f, 0f, false, 1, mPosition, new float[3], new float[3], 4f);
		rand = new Random(System.currentTimeMillis());
		this.fire = fire;
		this.ship = ship;
		this.rockets = rockets;
	}

	public Turret(Context context, ObjModelMtlVBO turret, CollisionMesh collisionMesh, float[] mPosition, Fire fire, Ship ship, List<BaseItem> rockets) {
		super(context, turret, collisionMesh, 1, mPosition, new float[3], new float[3], 4f);
		rand = new Random(System.currentTimeMillis());
		this.fire = fire;
		this.ship = ship;
		this.rockets = rockets;
	}

	@Override
	protected Explosion getExplosion() {
		return new Explosion.ExplosionBuilder().setNbParticules(10)
				.setLimitScale(1f)
				.setRangeScale(1f)
				.setLimitSpeed(1f)
				.setRangeSpeed(0.5f)
				.makeExplosion(glContext, objModelMtlVBO.getRandomMtlDiffRGB());
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
			fire.fire(rockets, super.mPosition.clone(), originaleVec.clone(), tmpMat.clone(), 0.5f, ship);
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

		super.update();
	}
}
