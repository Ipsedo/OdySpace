package com.samuelberrien.odyspace.objects.baseitem.ammos;

import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.utils.game.Item;
import com.samuelberrien.odyspace.utils.maths.Vector;

/**
 * Created by samuel on 04/08/17.
 */

public class GuidedMissile extends Ammos {

	private static int Life = 1;
	private static float Scale = 1f;
	private static int Max_Duration = 900;
	private static float coefAngle = 180f;

	private Item target;
	private int currentDuration;


	public GuidedMissile(ObjModelMtlVBO objModelMtl, float[] mPosition, float[] mSpeed, float[] mRotationMatrix, float maxSpeed, Item target) {
		super(objModelMtl, mPosition, mSpeed, new float[3], mRotationMatrix, maxSpeed, Scale, Life);
		this.target = target;
		this.currentDuration = 0;
	}


	@Override
	public void update() {
		float[] toTargetVec = Vector.normalize3f(Vector.make3f(super.clonePosition(), target.clonePosition()));
		float[] realSpeed = new float[]{super.mSpeed[0], super.mSpeed[1], super.mSpeed[2], 0f};
		float[] realUp = new float[4];
		Matrix.multiplyMV(realUp, 0, super.mRotationMatrix, 0, Vector.originalUp4f, 0);
		Matrix.multiplyMV(realSpeed, 0, super.mRotationMatrix, 0, realSpeed.clone(), 0);
		realSpeed = Vector.normalize3f(new float[]{realSpeed[0], realSpeed[1], realSpeed[2]});
		realUp = Vector.normalize3f(new float[]{realUp[0], realUp[1], realUp[2]});

		float[] rotAxis = new float[3];
		float angle = Vector.computeRotationAngle(realSpeed, toTargetVec, realUp, rotAxis); //Math.toDegrees(Math.atan2(Vector.dot3f(cross, new float[]{0f, 1f, 0f}), Vector.dot3f(toTargetVec, realSpeed)));
		if (Vector.length3f(Vector.make3f(super.mPosition, target.clonePosition())) > 10f) {
			float[] tmpMat = new float[16];
			Matrix.setRotateM(tmpMat, 0, /*angle < coefAngle && angle > -coefAngle ? angle : Math.signum(angle) * coefAngle*/ angle, rotAxis[0], rotAxis[1], rotAxis[2]);
			Matrix.multiplyMM(tmpMat, 0, super.mRotationMatrix, 0, tmpMat.clone(), 0);
			super.mRotationMatrix = tmpMat;
		}

		super.update();
	}
}
