package com.samuelberrien.odyspace.core.baseitem.ship;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.Matrix;
import android.os.Vibrator;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.controls.GamePad;
import com.samuelberrien.odyspace.core.Bonus;
import com.samuelberrien.odyspace.core.Shooter;
import com.samuelberrien.odyspace.core.baseitem.BaseItem;
import com.samuelberrien.odyspace.core.fire.Fire;
import com.samuelberrien.odyspace.core.fire.FireType;
import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.drawable.ProgressBar;
import com.samuelberrien.odyspace.utils.graphics.Color;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by samuel on 18/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Ship extends BaseItem implements Shooter, SharedPreferences.OnSharedPreferenceChangeListener {

	private static float SHIP_MAX_SPEED = 0.0125f;
	private float mMaxSpeed = Ship.SHIP_MAX_SPEED;
	private float mBoostSpeed;
	private final float ROCKET_MAX_SPEED = 0.020f;
	private final float rollCoeff = 1f;
	private final float pitchCoeff = 0.5f;
	private final float yawCoeff = 0.5f;
	private final float boostCoeff = 2.0f;

	private final float[] originalSpeedVec = new float[]{0f, 0f, 1f, 0f};
	private final float[] originalUpVec = new float[]{0f, 1f, 0f, 0f};

	private GamePad gamePad;

	private ProgressBar lifeDraw;

	private List<BaseItem> rockets;

	private Fire fire;
	private Bonus bonus;
	private int bonusDuration;
	private int bonusDurationBought;

	private Vibrator vibrator;

	private Queue<Runnable> toRunOnGLThread;



		//Bonus bonus;
		//String savedBonus = savedShip.getString(context.getString(R.string.current_bonus_used), context.getString(R.string.bonus_1));
		//int currBonusDur = savedShip.getInt(context.getString(R.string.current_bonus_duration), context.getResources().getInteger(R.integer.bonus_1_duration));
		//int bonusDurationBought = savedShop.getInt(context.getString(R.string.bought_duration), context.getResources().getInteger(R.integer.zero));

		//if (savedBonus.equals(context.getString(R.string.bonus_1))) bonus = Bonus.SPEED;
		//else bonus = Bonus.SHIELD;


	//TODO modèles simplifiés pr crashable ?
	protected Ship(Context context, String objFileName, String mtlFileName, int shipLife,
				   GamePad gamePad) {

		super(context, objFileName, mtlFileName, objFileName, 1f, 0f, false,
				context.getSharedPreferences(context.getString(R.string.shop_preferences), Context.MODE_PRIVATE)
						.getInt(context.getString(R.string.bought_life), context.getResources().getInteger(R.integer.saved_ship_life_shop_default)) + shipLife,
				new float[]{0f, 0f, -250f}, new float[]{0f, 0f, 1f}, new float[]{0f, 0f, 0f}, 1f);

		maxLife = life;
		lifeDraw = new ProgressBar(context, maxLife, 0.9f, 0.9f, Color.LifeRed);

		SharedPreferences savedShip = context.getSharedPreferences(context.getString(R.string.ship_info_preferences), Context.MODE_PRIVATE);
		String defaultValue = context.getString(R.string.saved_fire_type_default);
		String fireType = savedShip.getString(context.getString(R.string.current_fire_type), defaultValue);
		FireType shipFireType = FireType.valueOf(fireType.toUpperCase().replace(" ", "_"));
		this.fire = shipFireType.getFire(context);

		mBoostSpeed = 0f;
		this.gamePad = gamePad;
		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		this.bonus = Bonus.SPEED;

		//TODO utiliser enum avec temps bonus plutot ?
		this.bonusDuration = context.getSharedPreferences(context.getString(R.string.shop_preferences), Context.MODE_PRIVATE)
				.getInt(context.getString(R.string.current_bonus_duration), context.getResources().getInteger(R.integer.bonus_1_duration));

		this.bonusDurationBought = context.getSharedPreferences(context.getString(R.string.shop_preferences), Context.MODE_PRIVATE)
				.getInt(context.getString(R.string.bought_duration), context.getResources().getInteger(R.integer.zero));
		toRunOnGLThread = new LinkedList<>();
	}

	public void setRockets(List<BaseItem> rockets) {
		this.rockets = rockets;
	}

	@Override
	public void update() {
		if (super.isAlive()) {
			mBoostSpeed = (float) Math.exp(gamePad.getBoost() + 2f) * boostCoeff;
			mMaxSpeed = Ship.SHIP_MAX_SPEED * mBoostSpeed;

			float[] pitchMatrix = new float[16];
			float[] rollMatrix = new float[16];
			float[] yawMatrix = new float[16];
			float roll = gamePad.getRoll();
			float pitch = gamePad.getPitch();
			float yaw = gamePad.getYaw();
			Matrix.setRotateM(rollMatrix, 0, (roll >= 0 ? (float) Math.exp(roll) - 1f : (float) -Math.exp(Math.abs(roll)) + 1f) * rollCoeff, 0f, 0f, 1f);
			Matrix.setRotateM(pitchMatrix, 0, (pitch >= 0 ? (float) Math.exp(pitch) - 1f : (float) -Math.exp(Math.abs(pitch)) + 1f) * pitchCoeff, 1f, 0f, 0f);
			Matrix.setRotateM(yawMatrix, 0, (yaw >= 0 ? (float) Math.exp(yaw) - 1f : (float) -Math.exp(Math.abs(yaw)) + 1f) * yawCoeff, 0f, 1f, 0f);

			float[] currRotMatrix = new float[16];
			Matrix.multiplyMM(currRotMatrix, 0, pitchMatrix, 0, rollMatrix, 0);
			Matrix.multiplyMM(currRotMatrix, 0, currRotMatrix.clone(), 0, yawMatrix, 0);

			float[] currSpeed = new float[4];
			Matrix.multiplyMV(currSpeed, 0, currRotMatrix, 0, originalSpeedVec, 0);

			float[] realSpeed = new float[4];
			Matrix.multiplyMV(realSpeed, 0, super.mRotationMatrix, 0, currSpeed, 0);

			Matrix.multiplyMM(super.mRotationMatrix, 0, super.mRotationMatrix.clone(), 0, currRotMatrix, 0);

			super.mPosition[0] += mMaxSpeed * realSpeed[0];
			super.mPosition[1] += mMaxSpeed * realSpeed[1];
			super.mPosition[2] += mMaxSpeed * realSpeed[2];

			float[] mModelMatrix = new float[16];
			Matrix.setIdentityM(mModelMatrix, 0);
			Matrix.translateM(mModelMatrix, 0, super.mPosition[0], super.mPosition[1], super.mPosition[2]);
			Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix.clone(), 0, super.mRotationMatrix, 0);
			Matrix.scaleM(mModelMatrix, 0, super.scale, super.scale, super.scale);

			super.mModelMatrix = mModelMatrix;
		}
	}

	@Override
	protected Explosion getExplosion() {
		return new Explosion.ExplosionBuilder().makeExplosion(glContext, super.objModelMtlVBO.getRandomMtlDiffRGB());
	}

	@Override
	public void fire() {
		if (super.isAlive() && gamePad.fire()) {
			fire.fire(rockets, super.mPosition.clone(), super.mSpeed.clone(), super.mRotationMatrix.clone(), (mBoostSpeed >= 32f ? mBoostSpeed : 32f) * ROCKET_MAX_SPEED);
		}
	}

	@Override
	public void decrementLife(int minus) {
		if (minus > 0 && super.life > 0)
			vibrator.vibrate(50);
		super.decrementLife(minus);
	}

	public float[] fromCamTo(BaseItem to) {
		float[] camPos = getCamPosition();
		float[] toPos = to.clonePosition();
		return new float[]{toPos[0] - camPos[0], toPos[1] - camPos[1], toPos[2] - camPos[2]};
	}

	public float[] getCamFrontVec() {
		float[] lookAtPos = new float[3];
		float[] u = new float[4];
		Matrix.multiplyMV(u, 0, super.mRotationMatrix, 0, originalSpeedVec, 0);

		lookAtPos[0] = u[0] + mPosition[0];
		lookAtPos[1] = u[1] + mPosition[1];
		lookAtPos[2] = u[2] + mPosition[2];

		float[] camPos = getCamPosition();

		return new float[]{lookAtPos[0] - camPos[0], lookAtPos[1] - camPos[1], lookAtPos[2] - camPos[2]};
	}

	public float[] getCamPosition() {
		float[] res = new float[3];
		float[] u = new float[4];
		Matrix.multiplyMV(u, 0, super.mRotationMatrix, 0, originalSpeedVec, 0);

		float[] v = new float[4];
		Matrix.multiplyMV(v, 0, super.mRotationMatrix, 0, originalUpVec, 0);

		res[0] = -10f * u[0] + super.mPosition[0] + 1f * v[0];
		res[1] = -10f * u[1] + super.mPosition[1] + 1f * v[1];
		res[2] = -10f * u[2] + super.mPosition[2] + 1f * v[2];

		return res;
	}

	public float[] getCamLookAtVec() {
		float[] res = new float[3];
		float[] u = new float[4];
		Matrix.multiplyMV(u, 0, super.mRotationMatrix, 0, originalSpeedVec, 0);

		res[0] = u[0];
		res[1] = u[1];
		res[2] = u[2];

		return res;
	}

	public float[] getCamUpVec() {
		float[] res = new float[3];
		float[] u = new float[4];
		Matrix.multiplyMV(u, 0, super.mRotationMatrix, 0, originalUpVec, 0);

		res[0] = u[0];
		res[1] = u[1];
		res[2] = u[2];

		return res;
	}

	public float[] invVecWithRotMatrix(float[] vec) {
		float[] tmp = new float[]{vec[0], vec[1], vec[2], 0f};
		float[] invModel = new float[16];
		Matrix.invertM(invModel, 0, mRotationMatrix, 0);

		float[] tmpRes = new float[4];
		Matrix.multiplyMV(tmpRes, 0, invModel, 0, tmp, 0);

		return new float[]{tmpRes[0], tmpRes[1], tmpRes[2]};
	}

	public float[] passToRotMatrix(float[] vec3) {
		float[] tmp = new float[]{vec3[0], vec3[1], vec3[2], 0f};
		Matrix.multiplyMV(tmp, 0, mRotationMatrix, 0, tmp.clone(), 0);
		return new float[]{tmp[0], tmp[1], tmp[2]};
	}

	public float[] getFrontVec() {
		return passToRotMatrix(originalSpeedVec);
	}

	public float[] getSpeed() {
		return mSpeed.clone();
	}

	public void drawLife(float ratio) {
		lifeDraw.updateProgress(life);
		lifeDraw.draw(ratio);
	}

	@Override
	public void draw(float[] pMatrix, float[] vMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		while (!toRunOnGLThread.isEmpty())
			toRunOnGLThread.remove().run();
		super.draw(pMatrix, vMatrix, mLightPosInEyeSpace, mCameraPosition);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(glContext.getString(R.string.current_fire_type))) {
			String defaultValue = glContext.getString(R.string.saved_fire_type_default);
			String fireType = sharedPreferences.getString(key, defaultValue);
			FireType shipFireType = FireType.valueOf(fireType.toUpperCase().replace(" ", "_"));

			toRunOnGLThread.add(() -> fire = shipFireType.getFire(glContext));
		} else if (key.equals(glContext.getString(R.string.current_bonus_used))) {
			String savedBonus = sharedPreferences.getString(glContext.getString(R.string.current_bonus_used), glContext.getString(R.string.bonus_1));
			if (savedBonus.equals(glContext.getString(R.string.bonus_1))) {
				bonus = Bonus.SPEED;
			} else {
				bonus = Bonus.SHIELD;
			}
		} else if (key.equals(glContext.getString(R.string.current_bonus_duration))) {
			bonusDuration = sharedPreferences.getInt(key, glContext.getResources().getInteger(R.integer.bonus_1_duration));
		}
	}
}
