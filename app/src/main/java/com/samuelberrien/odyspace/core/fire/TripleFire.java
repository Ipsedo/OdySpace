package com.samuelberrien.odyspace.core.fire;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.core.Item;
import com.samuelberrien.odyspace.core.objects.BaseItem;
import com.samuelberrien.odyspace.core.objects.ammos.RocketItem;

import java.util.List;

public class TripleFire extends Fire {
	public TripleFire(Context glContext) {
		super(glContext, "obj/rocket.obj", "obj/rocket.mtl");
	}

	@Override
	public void fire(List<BaseItem> rockets, float[] position, float[] originalSpeedVec, float[] rotationMatrix, float maxSpeed, Item... targets) {
		float[] tmpMat = new float[16];
		Matrix.setRotateM(tmpMat, 0, -1f, 1f, 0f, 0f);
		Matrix.multiplyMM(tmpMat, 0, rotationMatrix, 0, tmpMat.clone(), 0);
		rockets.add(new RocketItem(glContext, ammo, crashableMesh, position.clone(), originalSpeedVec.clone(), tmpMat.clone(), maxSpeed));

		Matrix.setRotateM(tmpMat, 0, 1f, (float) Math.cos(Math.PI / 3d), (float) Math.sin(Math.PI / 3d), 0f);
		Matrix.multiplyMM(tmpMat, 0, rotationMatrix, 0, tmpMat.clone(), 0);
		rockets.add(new RocketItem(glContext, ammo, crashableMesh, position.clone(), originalSpeedVec.clone(), tmpMat.clone(), maxSpeed));

		Matrix.setRotateM(tmpMat, 0, 1f, (float) Math.cos(Math.PI / 3d), -(float) Math.sin(Math.PI / 3d), 0f);
		Matrix.multiplyMM(tmpMat, 0, rotationMatrix, 0, tmpMat.clone(), 0);
		rockets.add(new RocketItem(glContext, ammo, crashableMesh, position.clone(), originalSpeedVec.clone(), tmpMat.clone(), maxSpeed));
	}
}
