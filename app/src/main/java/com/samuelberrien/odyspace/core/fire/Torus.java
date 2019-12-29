package com.samuelberrien.odyspace.core.fire;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.core.Item;
import com.samuelberrien.odyspace.core.baseitem.BaseItem;
import com.samuelberrien.odyspace.core.baseitem.ammos.TorusItem;

import java.util.List;

public class Torus extends Fire {

	Torus(Context glContext) {
		super(glContext, "obj/torus.obj", "obj/torus.mtl");
	}

	@Override
	public void fire(List<BaseItem> rockets, float[] position, float[] originalSpeedVec, float[] rotationMatrix, float maxSpeed, Item... targets) {
		float[] positions = position.clone();
		float[] realSpeed = new float[]{originalSpeedVec[0], originalSpeedVec[1], originalSpeedVec[2], 0f};
		Matrix.multiplyMV(realSpeed, 0, rotationMatrix, 0, realSpeed.clone(), 0);

		rockets.add(new TorusItem(glContext, ammo, collisionMesh, positions.clone(), originalSpeedVec.clone(), rotationMatrix.clone(), maxSpeed, 0d));
		rockets.add(new TorusItem(glContext, ammo, collisionMesh, new float[]{positions[0] += realSpeed[0] * 6f, positions[1] += realSpeed[1] * 6f, positions[2] += realSpeed[2] * 6f}, originalSpeedVec.clone(), rotationMatrix.clone(), maxSpeed, 0.4d));
		rockets.add(new TorusItem(glContext, ammo, collisionMesh, new float[]{positions[0] += realSpeed[0] * 6f, positions[1] += realSpeed[1] * 6f, positions[2] += realSpeed[2] * 6f}, originalSpeedVec.clone(), rotationMatrix.clone(), maxSpeed, 0.8d));

	}
}
