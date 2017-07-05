package com.samuelberrien.odyspace.utils.game;

import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.objects.baseitem.BaseItem;
import com.samuelberrien.odyspace.objects.baseitem.Rocket;

import java.util.List;

/**
 * Created by samuel on 03/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public enum FireType {

	SIMPLE_FIRE, QUINT_FIRE, SIMPLE_BOMB, TRIPLE_FIRE;

	public void fire(ObjModelMtlVBO rocketModel, List<BaseItem> rockets, float[] position, float[] originalSpeedVec, float[] rotationMatrix, float maxSpeed) {
		float[] tmpMat;
		switch (this) {
			case SIMPLE_FIRE:
				rockets.add(new Rocket(rocketModel, position, originalSpeedVec, new float[]{0f, 0f, 0f}, rotationMatrix, maxSpeed, 1f, 1));
				break;
			case QUINT_FIRE:
				rockets.add(new Rocket(rocketModel, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, rotationMatrix.clone(), maxSpeed, 1f, 1));

				tmpMat = new float[16];
				Matrix.setRotateM(tmpMat, 0, 2.5f, 1f, 0f, 0f);
				float[] res = new float[16];
				Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
				rockets.add(new Rocket(rocketModel, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, res.clone(), maxSpeed, 1f, 1));

				Matrix.setRotateM(tmpMat, 0, -2.5f, 1f, 0f, 0f);
				res = new float[16];
				Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
				rockets.add(new Rocket(rocketModel, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, res.clone(), maxSpeed, 1f, 1));

				Matrix.setRotateM(tmpMat, 0, -2.5f, 0f, 1f, 0f);
				res = new float[16];
				Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
				rockets.add(new Rocket(rocketModel, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, res.clone(), maxSpeed, 1f, 1));

				Matrix.setRotateM(tmpMat, 0, 2.5f, 0f, 1f, 0f);
				res = new float[16];
				Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
				rockets.add(new Rocket(rocketModel, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, res.clone(), maxSpeed, 1f, 1));
				break;
			case SIMPLE_BOMB:
				rockets.add(new Rocket(rocketModel, position, originalSpeedVec, new float[]{0f, 0f, 0f}, rotationMatrix, maxSpeed, 2.5f, 3));
				break;
			case TRIPLE_FIRE:
				tmpMat = new float[16];
				Matrix.setRotateM(tmpMat, 0, -1f, 1f, 0f, 0f);
				Matrix.multiplyMM(tmpMat, 0, rotationMatrix, 0, tmpMat.clone(), 0);
				rockets.add(new Rocket(rocketModel, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, tmpMat.clone(), maxSpeed, 1f, 1));

				Matrix.setRotateM(tmpMat, 0, 1f, (float) Math.cos(Math.PI / 3d), (float) Math.sin(Math.PI / 3d), 0f);
				Matrix.multiplyMM(tmpMat, 0, rotationMatrix, 0, tmpMat.clone(), 0);
				rockets.add(new Rocket(rocketModel, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, tmpMat.clone(), maxSpeed, 1f, 1));

				Matrix.setRotateM(tmpMat, 0, 1f, (float) Math.cos(Math.PI / 3d), -(float) Math.sin(Math.PI / 3d), 0f);
				Matrix.multiplyMM(tmpMat, 0, rotationMatrix, 0, tmpMat.clone(), 0);
				rockets.add(new Rocket(rocketModel, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, tmpMat.clone(), maxSpeed, 1f, 1));

				break;
		}
	}
}
