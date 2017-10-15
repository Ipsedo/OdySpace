package com.samuelberrien.odyspace.utils.game;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.objects.baseitem.BaseItem;
import com.samuelberrien.odyspace.objects.baseitem.ammos.Bomb;
import com.samuelberrien.odyspace.objects.baseitem.ammos.GuidedMissile;
import com.samuelberrien.odyspace.objects.baseitem.ammos.Laser;
import com.samuelberrien.odyspace.objects.baseitem.ammos.Rocket;
import com.samuelberrien.odyspace.objects.baseitem.ammos.Torus;
import com.samuelberrien.odyspace.objects.crashable.CrashableMesh;

import java.util.List;

/**
 * Created by samuel on 03/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public enum FireType {

	SIMPLE_FIRE,
	QUINT_FIRE,
	SIMPLE_BOMB,
	TRIPLE_FIRE,
	LASER,
	TORUS,
	GUIDED_MISSILE;

	/**
	 * Init ammo, crashableMesh, models, must be called on GLES Thread !
	 *
	 * @param context the application context
	 */
	public static void initAmmos(Context context) {
		ObjModelMtlVBO tmpRocket = new ObjModelMtlVBO(context, "obj/rocket.obj", "obj/rocket.mtl", 2f, 0f, false);
		CrashableMesh crashableMesh = new CrashableMesh(context, "obj/rocket.obj");
		SIMPLE_FIRE.ammo = tmpRocket;
		SIMPLE_FIRE.crashableMesh = crashableMesh;
		QUINT_FIRE.ammo = tmpRocket;
		QUINT_FIRE.crashableMesh = crashableMesh;
		SIMPLE_BOMB.ammo = new ObjModelMtlVBO(context, "obj/bomb.obj", "obj/bomb.mtl", 0.8f, 0f, false);
		SIMPLE_BOMB.crashableMesh = new CrashableMesh(context, "obj/bomb.obj");
		TRIPLE_FIRE.ammo = tmpRocket;
		TRIPLE_FIRE.crashableMesh = crashableMesh;
		LASER.ammo = new ObjModelMtlVBO(context, "obj/laser.obj", "obj/laser.mtl", 2f, 0f, false);
		LASER.crashableMesh = new CrashableMesh(context, "obj/laser.obj");
		TORUS.ammo = new ObjModelMtlVBO(context, "obj/torus.obj", "obj/torus.mtl", 2f, 0f, false);
		TORUS.crashableMesh = new CrashableMesh(context, "obj/torus.obj");
		GUIDED_MISSILE.ammo = tmpRocket;
		GUIDED_MISSILE.crashableMesh = crashableMesh;
		appContext = context;
	}

	public static void setNames(Context context) {
		SIMPLE_FIRE.name = context.getString(R.string.fire_1);
		QUINT_FIRE.name = context.getString(R.string.fire_2);
		SIMPLE_BOMB.name = context.getString(R.string.fire_3);
		TRIPLE_FIRE.name = context.getString(R.string.fire_4);
		LASER.name = context.getString(R.string.fire_5);
		TORUS.name = context.getString(R.string.fire_6);
		GUIDED_MISSILE.name = "GUIDED_MISSILE";
	}

	public static FireType getFireType(String name) {
		if (name.equals(QUINT_FIRE.name)) {
			return QUINT_FIRE;
		} else if (name.equals(SIMPLE_BOMB.name)) {
			return SIMPLE_BOMB;
		} else if (name.equals(TRIPLE_FIRE.name)) {
			return TRIPLE_FIRE;
		} else if (name.equals(LASER.name)) {
			return LASER;
		} else if (name.equals(TORUS.name)) {
			return TORUS;
		} else if (name.equals(GUIDED_MISSILE.name)) {
			return GUIDED_MISSILE;
		} else {
			return SIMPLE_FIRE;
		}
	}

	private String name;
	private static Context appContext;
	private ObjModelMtlVBO ammo;
	//TODO modèles simplifiés pr crashable ?
	private CrashableMesh crashableMesh;

	private String getName() {
		return this.name;
	}

	public void fire(List<BaseItem> rockets, float[] position, float[] originalSpeedVec, float[] rotationMatrix, float maxSpeed, Item... targets) {
		float[] tmpMat;
		switch (this) {
			case SIMPLE_FIRE:
				rockets.add(new Rocket(appContext, ammo, crashableMesh, position.clone(), originalSpeedVec.clone(), rotationMatrix.clone(), maxSpeed));
				break;
			case QUINT_FIRE:
				rockets.add(new Rocket(appContext, ammo, crashableMesh, position.clone(), originalSpeedVec.clone(), rotationMatrix.clone(), maxSpeed));

				tmpMat = new float[16];
				Matrix.setRotateM(tmpMat, 0, 2.5f, 1f, 0f, 0f);
				float[] res = new float[16];
				Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
				rockets.add(new Rocket(appContext, ammo, crashableMesh, position.clone(), originalSpeedVec.clone(), res.clone(), maxSpeed));

				Matrix.setRotateM(tmpMat, 0, -2.5f, 1f, 0f, 0f);
				res = new float[16];
				Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
				rockets.add(new Rocket(appContext, ammo, crashableMesh, position.clone(), originalSpeedVec.clone(), res.clone(), maxSpeed));

				Matrix.setRotateM(tmpMat, 0, -2.5f, 0f, 1f, 0f);
				res = new float[16];
				Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
				rockets.add(new Rocket(appContext, ammo, crashableMesh, position.clone(), originalSpeedVec.clone(), res.clone(), maxSpeed));

				Matrix.setRotateM(tmpMat, 0, 2.5f, 0f, 1f, 0f);
				res = new float[16];
				Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
				rockets.add(new Rocket(appContext, ammo, crashableMesh, position.clone(), originalSpeedVec.clone(), res.clone(), maxSpeed));
				break;
			case SIMPLE_BOMB:
				rockets.add(new Bomb(appContext, ammo, crashableMesh, position.clone(), originalSpeedVec.clone(), rotationMatrix.clone(), maxSpeed));
				break;
			case TRIPLE_FIRE:
				tmpMat = new float[16];
				Matrix.setRotateM(tmpMat, 0, -1f, 1f, 0f, 0f);
				Matrix.multiplyMM(tmpMat, 0, rotationMatrix, 0, tmpMat.clone(), 0);
				rockets.add(new Rocket(appContext, ammo, crashableMesh, position.clone(), originalSpeedVec.clone(), tmpMat.clone(), maxSpeed));

				Matrix.setRotateM(tmpMat, 0, 1f, (float) Math.cos(Math.PI / 3d), (float) Math.sin(Math.PI / 3d), 0f);
				Matrix.multiplyMM(tmpMat, 0, rotationMatrix, 0, tmpMat.clone(), 0);
				rockets.add(new Rocket(appContext, ammo, crashableMesh, position.clone(), originalSpeedVec.clone(), tmpMat.clone(), maxSpeed));

				Matrix.setRotateM(tmpMat, 0, 1f, (float) Math.cos(Math.PI / 3d), -(float) Math.sin(Math.PI / 3d), 0f);
				Matrix.multiplyMM(tmpMat, 0, rotationMatrix, 0, tmpMat.clone(), 0);
				rockets.add(new Rocket(appContext, ammo, crashableMesh, position.clone(), originalSpeedVec.clone(), tmpMat.clone(), maxSpeed));
				break;
			case LASER:
				int nbElements = 20;

				float[][] mPositions = new float[nbElements][3];

				float[] realSpeed = new float[]{originalSpeedVec[0], originalSpeedVec[1], originalSpeedVec[2], 0f};

				Matrix.multiplyMV(realSpeed, 0, rotationMatrix, 0, realSpeed.clone(), 0);

				mPositions[0][0] = position[0];
				mPositions[0][1] = position[1];
				mPositions[0][2] = position[2];

				rockets.add(new Laser(appContext, ammo, crashableMesh, new float[]{mPositions[0][0], mPositions[0][1], mPositions[0][2]}, originalSpeedVec.clone(), rotationMatrix.clone(), maxSpeed, 10f));
				for (int i = 1; i < nbElements; i++) {
					realSpeed = new float[]{originalSpeedVec[0], originalSpeedVec[1], originalSpeedVec[2], 0f};

					Matrix.multiplyMV(realSpeed, 0, rotationMatrix, 0, realSpeed.clone(), 0);

					float length = 1f / Matrix.length(realSpeed[0], realSpeed[1], realSpeed[2]);

					mPositions[i][0] = mPositions[i - 1][0] + realSpeed[0] * length * 10f;
					mPositions[i][1] = mPositions[i - 1][1] + realSpeed[1] * length * 10f;
					mPositions[i][2] = mPositions[i - 1][2] + realSpeed[2] * length * 10f;
					rockets.add(new Laser(appContext, ammo, crashableMesh, new float[]{mPositions[i][0], mPositions[i][1], mPositions[i][2]}, originalSpeedVec.clone(), rotationMatrix.clone(), maxSpeed, 10f));
				}
				break;
			case TORUS:
				float[] positions = position.clone();
				realSpeed = new float[]{originalSpeedVec[0], originalSpeedVec[1], originalSpeedVec[2], 0f};
				Matrix.multiplyMV(realSpeed, 0, rotationMatrix, 0, realSpeed.clone(), 0);

				rockets.add(new Torus(appContext, ammo, crashableMesh, positions.clone(), originalSpeedVec.clone(), rotationMatrix.clone(), maxSpeed, 0d));
				rockets.add(new Torus(appContext, ammo, crashableMesh, new float[]{positions[0] += realSpeed[0] * 6f, positions[1] += realSpeed[1] * 6f, positions[2] += realSpeed[2] * 6f}, originalSpeedVec.clone(), rotationMatrix.clone(), maxSpeed, 0.4d));
				rockets.add(new Torus(appContext, ammo, crashableMesh, new float[]{positions[0] += realSpeed[0] * 6f, positions[1] += realSpeed[1] * 6f, positions[2] += realSpeed[2] * 6f}, originalSpeedVec.clone(), rotationMatrix.clone(), maxSpeed, 0.8d));
				break;
			case GUIDED_MISSILE:
				rockets.add(new GuidedMissile(appContext, ammo, crashableMesh, position.clone(), originalSpeedVec, rotationMatrix, maxSpeed, targets[0]));
				break;

		}
	}
}
