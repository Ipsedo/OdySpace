package com.samuelberrien.odyspace.core.fire;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.core.Item;
import com.samuelberrien.odyspace.core.baseitem.BaseItem;
import com.samuelberrien.odyspace.core.baseitem.ammos.LaserItem;

import java.util.List;

public class Laser extends Fire {

	Laser(Context glContext) {
		super(glContext, "obj/laser.obj", "obj/laser.mtl");
	}

	@Override
	public void fire(List<BaseItem> rockets, float[] position, float[] originalSpeedVec, float[] rotationMatrix, float maxSpeed, Item... targets) {
		int nbElements = 20;

		float[][] mPositions = new float[nbElements][3];

		float[] realSpeed = new float[]{originalSpeedVec[0], originalSpeedVec[1], originalSpeedVec[2], 0f};

		Matrix.multiplyMV(realSpeed, 0, rotationMatrix, 0, realSpeed.clone(), 0);

		mPositions[0][0] = position[0];
		mPositions[0][1] = position[1];
		mPositions[0][2] = position[2];

		rockets.add(new LaserItem(glContext, ammo, collisionMesh, new float[]{mPositions[0][0], mPositions[0][1], mPositions[0][2]}, originalSpeedVec.clone(), rotationMatrix.clone(), maxSpeed, 10f));
		for (int i = 1; i < nbElements; i++) {
			realSpeed = new float[]{originalSpeedVec[0], originalSpeedVec[1], originalSpeedVec[2], 0f};

			Matrix.multiplyMV(realSpeed, 0, rotationMatrix, 0, realSpeed.clone(), 0);

			float length = 1f / Matrix.length(realSpeed[0], realSpeed[1], realSpeed[2]);

			mPositions[i][0] = mPositions[i - 1][0] + realSpeed[0] * length * 10f;
			mPositions[i][1] = mPositions[i - 1][1] + realSpeed[1] * length * 10f;
			mPositions[i][2] = mPositions[i - 1][2] + realSpeed[2] * length * 10f;
			rockets.add(new LaserItem(glContext, ammo, collisionMesh, new float[]{mPositions[i][0], mPositions[i][1], mPositions[i][2]}, originalSpeedVec.clone(), rotationMatrix.clone(), maxSpeed, 10f));
		}
	}
}
