package com.samuelberrien.odyspace.objects.baseitem;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.drawable.ProgressBar;
import com.samuelberrien.odyspace.drawable.controls.Controls;
import com.samuelberrien.odyspace.drawable.controls.Joystick;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.utils.game.FireType;
import com.samuelberrien.odyspace.utils.graphics.Color;

import java.util.List;

/**
 * Created by samuel on 18/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Ship extends BaseItem {

	public static float SHIP_MAX_SPEED = 0.0125f;
	private float mMaxSpeed = Ship.SHIP_MAX_SPEED;
	private float mBoostSpeed;
	private final float ROCKET_MAX_SPEED = 0.020f;
	private final float rollCoeff = 1f;
	private final float pitchCoeff = 0.5f;
	private final float boostCoeff = 2.0f;

	private final float[] originalSpeedVec = new float[]{0f, 0f, 1f, 0f};
	private final float[] originalUpVec = new float[]{0f, 1f, 0f, 0f};

	private int maxLife;
	private ProgressBar lifeDraw;

	private Explosion mExplosion;
	private boolean exploded;

	private ObjModelMtlVBO rocket;

	private FireType fireType;

	public static Ship makeShip(Context context) {
		SharedPreferences savedShip = context.getSharedPreferences(context.getString(R.string.saved_ship_info), Context.MODE_PRIVATE);
		SharedPreferences savedShop = context.getSharedPreferences(context.getString(R.string.saved_shop), Context.MODE_PRIVATE);
		int currBoughtLife = savedShop.getInt(context.getString(R.string.bought_life), context.getResources().getInteger(R.integer.saved_ship_life_shop_default));
		int currShipLife = savedShip.getInt(context.getString(R.string.current_life_number), context.getResources().getInteger(R.integer.saved_ship_life_default));

		int life = currBoughtLife + currShipLife;

		String defaultValue = context.getString(R.string.saved_fire_type_default);
		String fireType = savedShip.getString(context.getString(R.string.current_fire_type), defaultValue);
		FireType shipFireType;
		if (fireType.equals(context.getString(R.string.fire_bonus_1))) {
			shipFireType = FireType.SIMPLE_FIRE;
		} else if (fireType.equals(context.getString(R.string.fire_bonus_2))) {
			shipFireType = FireType.QUINT_FIRE;
		} else /*if (fireType.equals(context.getString(R.string.fire_bonus_3)))*/ {
			shipFireType = FireType.SIMPLE_BOMB;
		}

		String shipUsed = savedShip.getString(context.getString(R.string.current_ship_used), context.getString(R.string.saved_ship_used_default));

		if (shipUsed.equals(context.getString(R.string.ship_bird))) {
			return new Ship(context, "ship_bird.obj", "ship_bird.mtl", life, shipFireType);
		} else if (shipUsed.equals(context.getString(R.string.ship_supreme))) {
			return new Ship(context, "ship_supreme.obj", "ship_supreme.mtl", life, shipFireType);
		} else {
			return new Ship(context, "ship_3.obj", "ship_3.mtl", life, shipFireType);
		}
	}

	private Ship(Context context, String objFileName, String mtlFileName, int life, FireType fireType) {
		super(context, objFileName, mtlFileName, 1f, 0f, false, life, new float[]{0f, 0f, -250f}, new float[]{0f, 0f, 1f}, new float[]{0f, 0f, 0f}, 1f);
		this.maxLife = life;
		this.lifeDraw = new ProgressBar(this.context, this.maxLife, 0.9f, 0.9f, Color.LifeRed);
		this.rocket = new ObjModelMtlVBO(this.context, "rocket.obj", "rocket.mtl", 2f, 0f, false);
		this.fireType = fireType;
		this.exploded = false;
		this.mBoostSpeed = 0f;
	}

	public void addExplosion(List<Explosion> explosions) {
		if (!this.exploded) {
			this.mExplosion.setPosition(this.mPosition.clone());
			explosions.add(this.mExplosion);
			this.exploded = true;
		}
	}

	public void makeExplosion() {
		this.mExplosion = new Explosion(context, super.mPosition.clone(), super.diffColorBuffer, 10, 0.16f, 1f, 1f, 0.4f, 0.6f);
	}

	public void move(Joystick joystick, Controls controls) {
		this.mBoostSpeed = (float) Math.exp(controls.getBoost() + 2f) * this.boostCoeff;
		this.mMaxSpeed = this.SHIP_MAX_SPEED * this.mBoostSpeed;

		float[] tmp = joystick.getStickPosition();

		float phi = tmp[0];
		float theta = tmp[1];

		float[] pitchMatrix = new float[16];
		float[] rollMatrix = new float[16];
		Matrix.setRotateM(rollMatrix, 0, (phi >= 0 ? (float) Math.exp(phi) - 1f : (float) -Math.exp(Math.abs(phi)) + 1f) * this.rollCoeff, 0f, 0f, 1f);
		Matrix.setRotateM(pitchMatrix, 0, (theta >= 0 ? (float) Math.exp(theta) - 1f : (float) -Math.exp(Math.abs(theta)) + 1f) * this.pitchCoeff, 1f, 0f, 0f);

		float[] currRotMatrix = new float[16];
		Matrix.multiplyMM(currRotMatrix, 0, pitchMatrix, 0, rollMatrix, 0);

		float[] currSpeed = new float[4];
		Matrix.multiplyMV(currSpeed, 0, currRotMatrix, 0, this.originalSpeedVec, 0);

		float[] realSpeed = new float[4];
		Matrix.multiplyMV(realSpeed, 0, super.mRotationMatrix, 0, currSpeed, 0);

		float[] tmpMat = super.mRotationMatrix.clone();
		Matrix.multiplyMM(super.mRotationMatrix, 0, tmpMat, 0, currRotMatrix, 0);

		super.mPosition[0] += this.mMaxSpeed * realSpeed[0];
		super.mPosition[1] += this.mMaxSpeed * realSpeed[1];
		super.mPosition[2] += this.mMaxSpeed * realSpeed[2];

		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, super.mPosition[0], super.mPosition[1], super.mPosition[2]);
		tmpMat = mModelMatrix.clone();
		Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, super.mRotationMatrix, 0);
		Matrix.scaleM(mModelMatrix, 0, super.scale, super.scale, super.scale);

		super.mModelMatrix = mModelMatrix;
	}

	public void fire(Controls controls, List<BaseItem> rockets) {
		if (controls.isFire()) {
			this.fireType.fire(this.rocket, rockets, super.mPosition.clone(), super.mSpeed.clone(), super.mRotationMatrix.clone(), (this.mBoostSpeed >= 32f ? this.mBoostSpeed : 32f) * this.ROCKET_MAX_SPEED);
			controls.turnOffFire();
		}
	}

	public float[] fromCamTo(BaseItem to) {
		float[] camPos = this.getCamPosition();
		return new float[]{to.mPosition[0] - camPos[0], to.mPosition[1] - camPos[1], to.mPosition[2] - camPos[2]};
	}

	public float[] getCamFrontVec() {
		float[] lookAtPos = new float[3];
		float[] u = new float[4];
		Matrix.multiplyMV(u, 0, super.mRotationMatrix, 0, this.originalSpeedVec, 0);

		lookAtPos[0] = u[0] + this.mPosition[0];
		lookAtPos[1] = u[1] + this.mPosition[1];
		lookAtPos[2] = u[2] + this.mPosition[2];

		float[] camPos = this.getCamPosition();

		return new float[]{lookAtPos[0] - camPos[0], lookAtPos[1] - camPos[1], lookAtPos[2] - camPos[2]};
	}

	public float[] getCamPosition() {
		float[] res = new float[3];
		float[] u = new float[4];
		Matrix.multiplyMV(u, 0, super.mRotationMatrix, 0, this.originalSpeedVec, 0);

		float[] v = new float[4];
		Matrix.multiplyMV(v, 0, super.mRotationMatrix, 0, this.originalUpVec, 0);

		res[0] = -10f * u[0] + super.mPosition[0] + 3f * v[0];
		res[1] = -10f * u[1] + super.mPosition[1] + 3f * v[1];
		res[2] = -10f * u[2] + super.mPosition[2] + 3f * v[2];

		return res;
	}

	public float[] getCamLookAtVec() {
		float[] res = new float[3];
		float[] u = new float[4];
		Matrix.multiplyMV(u, 0, super.mRotationMatrix, 0, this.originalSpeedVec, 0);

		res[0] = u[0];
		res[1] = u[1];
		res[2] = u[2];

		return res;
	}

	public float[] getCamUpVec() {
		float[] res = new float[3];
		float[] u = new float[4];
		Matrix.multiplyMV(u, 0, super.mRotationMatrix, 0, this.originalUpVec, 0);

		res[0] = u[0];
		res[1] = u[1];
		res[2] = u[2];

		return res;
	}

	public float[] invVecWithRotMatrix(float[] vec) {
		float[] tmp = new float[]{vec[0], vec[1], vec[2], 0f};
		float[] invModel = new float[16];
		Matrix.invertM(invModel, 0, this.mRotationMatrix, 0);

		float[] tmpRes = new float[4];
		Matrix.multiplyMV(tmpRes, 0, invModel, 0, tmp, 0);

		return new float[]{tmpRes[0], tmpRes[1], tmpRes[2]};
	}

	public void drawLife(float ratio) {
		this.lifeDraw.updateProgress(this.life);
		this.lifeDraw.draw(ratio);
	}
}
