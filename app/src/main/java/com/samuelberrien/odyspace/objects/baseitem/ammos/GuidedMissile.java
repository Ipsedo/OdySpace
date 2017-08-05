package com.samuelberrien.odyspace.objects.baseitem.ammos;

import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.utils.game.Item;
import com.samuelberrien.odyspace.utils.maths.Vector;

/**
 * Created by samuel on 04/08/17.
 */

public class GuidedMissile extends Ammos {

	private final static int Life = 1;
	private final static float Scale = 1f;
	private final static double AngleLimitInit = 50d;
	private final static float LimitLength = 20f;
	private final static int Auto_Destruction_Max_Duration = 200;

	private Item target;
	private int currentDuration;
	private double angle;
	private boolean willAutoDestruct;
	private boolean willReduceAngle;

	public GuidedMissile(ObjModelMtlVBO objModelMtl, float[] mPosition, float[] mSpeed, float[] mRotationMatrix, float maxSpeed, Item target) {
		super(objModelMtl, mPosition, mSpeed, new float[3], mRotationMatrix, maxSpeed, Scale, Life);
		this.target = target;
		this.currentDuration = 0;
		this.angle = AngleLimitInit;
		this.willAutoDestruct = false;
		this.willReduceAngle = false;
	}


	@Override
	public void update() {
		float[] speedVec = Vector.normalize3f(Vector.make3f(super.clonePosition(), target.clonePosition()));
		float[] originaleVec = new float[]{0f, 0f, 1f};

		float[] vecRepereMissile = new float[]{0f, 0f, 1f, 0f};
		Matrix.multiplyMV(vecRepereMissile, 0, super.mRotationMatrix, 0, vecRepereMissile.clone(), 0);

		float length = Vector.length3f(Vector.make3f(super.clonePosition(), target.clonePosition()));
		if(length < LimitLength) {
			this.willReduceAngle = true;
		}
		if (this.willReduceAngle) {
			this.angle -= length * AngleLimitInit / LimitLength;
			this.angle = this.angle <= 0d ? 0d : this.angle;
		}

		double angleWithTarget = Math.acos(Vector.dot3f(speedVec, new float[]{vecRepereMissile[0], vecRepereMissile[1], vecRepereMissile[2]})) * 360d / (Math.PI * 2d);
		if (this.angle > angleWithTarget) {
			float angle = (float) (Math.acos(Vector.dot3f(speedVec, originaleVec)) * 360d / (Math.PI * 2d));
			float[] rotAxis = Vector.cross3f(originaleVec, speedVec);
			float[] tmpMat = new float[16];
			Matrix.setRotateM(tmpMat, 0, angle, rotAxis[0], rotAxis[1], rotAxis[2]);
			super.mRotationMatrix = tmpMat;
		} else {
			this.willAutoDestruct = true;
		}

		if (this.willAutoDestruct) {
			this.currentDuration++;
			if (this.currentDuration > Auto_Destruction_Max_Duration) {
				super.life = 0;
			}
		}

		super.update();
	}
}
