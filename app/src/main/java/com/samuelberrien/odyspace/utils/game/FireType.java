package com.samuelberrien.odyspace.utils.game;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.support.v4.content.ContextCompat;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.game.LevelActivity;
import com.samuelberrien.odyspace.objects.baseitem.BaseItem;
import com.samuelberrien.odyspace.objects.baseitem.ammos.Laser;
import com.samuelberrien.odyspace.objects.baseitem.ammos.Rocket;

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
	LASER;

	public static void initAmmos(Context context) {
		SIMPLE_FIRE.ammo = new ObjModelMtlVBO(context, "rocket.obj", "rocket.mtl", 2f, 0f, false);
		QUINT_FIRE.ammo = new ObjModelMtlVBO(context, "rocket.obj", "rocket.mtl", 2f, 0f, false);
		SIMPLE_BOMB.ammo = new ObjModelMtlVBO(context, "rocket.obj", "rocket.mtl", 2f, 0f, false);
		TRIPLE_FIRE.ammo = new ObjModelMtlVBO(context, "rocket.obj", "rocket.mtl", 2f, 0f, false);
		LASER.ammo = new ObjModelMtlVBO(context, "laser.obj", "laser.mtl", 2f, 0f, false);
	}

	private ObjModelMtlVBO ammo;

	public void fire(List<BaseItem> rockets, float[] position, float[] originalSpeedVec, float[] rotationMatrix, float maxSpeed) {
		float[] tmpMat;
		switch (this) {
			case SIMPLE_FIRE:
				rockets.add(new Rocket(ammo, position, originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, rotationMatrix.clone(), maxSpeed, 1f, 1));
				break;
			case QUINT_FIRE:
				rockets.add(new Rocket(ammo, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, rotationMatrix.clone(), maxSpeed, 1f, 1));

				tmpMat = new float[16];
				Matrix.setRotateM(tmpMat, 0, 2.5f, 1f, 0f, 0f);
				float[] res = new float[16];
				Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
				rockets.add(new Rocket(ammo, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, res.clone(), maxSpeed, 1f, 1));

				Matrix.setRotateM(tmpMat, 0, -2.5f, 1f, 0f, 0f);
				res = new float[16];
				Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
				rockets.add(new Rocket(ammo, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, res.clone(), maxSpeed, 1f, 1));

				Matrix.setRotateM(tmpMat, 0, -2.5f, 0f, 1f, 0f);
				res = new float[16];
				Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
				rockets.add(new Rocket(ammo, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, res.clone(), maxSpeed, 1f, 1));

				Matrix.setRotateM(tmpMat, 0, 2.5f, 0f, 1f, 0f);
				res = new float[16];
				Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
				rockets.add(new Rocket(ammo, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, res.clone(), maxSpeed, 1f, 1));
				break;
			case SIMPLE_BOMB:
				rockets.add(new Rocket(ammo, position, originalSpeedVec, new float[]{0f, 0f, 0f}, rotationMatrix, maxSpeed, 2.5f, 3));
				break;
			case TRIPLE_FIRE:
				tmpMat = new float[16];
				Matrix.setRotateM(tmpMat, 0, -1f, 1f, 0f, 0f);
				Matrix.multiplyMM(tmpMat, 0, rotationMatrix, 0, tmpMat.clone(), 0);
				rockets.add(new Rocket(ammo, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, tmpMat.clone(), maxSpeed, 1f, 1));

				Matrix.setRotateM(tmpMat, 0, 1f, (float) Math.cos(Math.PI / 3d), (float) Math.sin(Math.PI / 3d), 0f);
				Matrix.multiplyMM(tmpMat, 0, rotationMatrix, 0, tmpMat.clone(), 0);
				rockets.add(new Rocket(ammo, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, tmpMat.clone(), maxSpeed, 1f, 1));

				Matrix.setRotateM(tmpMat, 0, 1f, (float) Math.cos(Math.PI / 3d), -(float) Math.sin(Math.PI / 3d), 0f);
				Matrix.multiplyMM(tmpMat, 0, rotationMatrix, 0, tmpMat.clone(), 0);
				rockets.add(new Rocket(ammo, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, tmpMat.clone(), maxSpeed, 1f, 1));
				break;
			case LASER:
				int nbElements = 10;

				float[][] mPositions = new float[nbElements][3];

				float[] realSpeed = new float[]{originalSpeedVec[0], originalSpeedVec[1], originalSpeedVec[2], 0f};

				Matrix.multiplyMV(realSpeed, 0, rotationMatrix, 0, realSpeed.clone(), 0);

				mPositions[0][0] = position[0];
				mPositions[0][1] = position[1];
				mPositions[0][2] = position[2];

				rockets.add(new Laser(ammo, new float[]{mPositions[0][0], mPositions[0][1], mPositions[0][2]}, originalSpeedVec.clone(), rotationMatrix.clone(), maxSpeed, 10f));
				for (int i = 1; i < nbElements; i++) {
					realSpeed = new float[]{originalSpeedVec[0], originalSpeedVec[1], originalSpeedVec[2], 0f};

					Matrix.multiplyMV(realSpeed, 0, rotationMatrix, 0, realSpeed.clone(), 0);

					float length = 1f / Matrix.length(realSpeed[0], realSpeed[1], realSpeed[2]);

					mPositions[i][0] = mPositions[i - 1][0] + realSpeed[0] * length * 10f;
					mPositions[i][1] = mPositions[i - 1][1] + realSpeed[1] * length * 10f;
					mPositions[i][2] = mPositions[i - 1][2] + realSpeed[2] * length * 10f;
					rockets.add(new Laser(ammo, new float[]{mPositions[i][0], mPositions[i][1], mPositions[i][2]}, originalSpeedVec.clone(), rotationMatrix.clone(), maxSpeed, 10f));
				}
				break;
		}
	}
}
