package com.samuelberrien.odyspace.core.fire;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.core.Item;
import com.samuelberrien.odyspace.core.objects.BaseItem;
import com.samuelberrien.odyspace.core.objects.ammos.RocketItem;

import java.util.List;

public class QuintFire extends Fire {

	public QuintFire(Context glContext) {
		super(glContext, "obj/rocket.obj", "obj/rocket.mtl");
	}

	@Override
	public void fire(List<BaseItem> rockets, float[] position, float[] originalSpeedVec, float[] rotationMatrix, float maxSpeed, Item... targets) {
		float[] tmpMat;

		rockets.add(new RocketItem(glContext, ammo, crashableMesh, position.clone(), originalSpeedVec.clone(), rotationMatrix.clone(), maxSpeed));

		tmpMat = new float[16];
		Matrix.setRotateM(tmpMat, 0, 2.5f, 1f, 0f, 0f);
		float[] res = new float[16];
		Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
		rockets.add(new RocketItem(glContext, ammo, crashableMesh, position.clone(), originalSpeedVec.clone(), res.clone(), maxSpeed));

		Matrix.setRotateM(tmpMat, 0, -2.5f, 1f, 0f, 0f);
		res = new float[16];
		Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
		rockets.add(new RocketItem(glContext, ammo, crashableMesh, position.clone(), originalSpeedVec.clone(), res.clone(), maxSpeed));

		Matrix.setRotateM(tmpMat, 0, -2.5f, 0f, 1f, 0f);
		res = new float[16];
		Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
		rockets.add(new RocketItem(glContext, ammo, crashableMesh, position.clone(), originalSpeedVec.clone(), res.clone(), maxSpeed));

		Matrix.setRotateM(tmpMat, 0, 2.5f, 0f, 1f, 0f);
		res = new float[16];
		Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
		rockets.add(new RocketItem(glContext, ammo, crashableMesh, position.clone(), originalSpeedVec.clone(), res.clone(), maxSpeed));
	}
}
