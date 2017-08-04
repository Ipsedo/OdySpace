package com.samuelberrien.odyspace.objects.baseitem;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.GLItemDrawable;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.utils.collision.Box;
import com.samuelberrien.odyspace.utils.collision.Ray;
import com.samuelberrien.odyspace.utils.game.Item;
import com.samuelberrien.odyspace.utils.game.UpdatableItem;
import com.samuelberrien.odyspace.utils.maths.Vector;

import java.util.List;


/**
 * Created by samuel on 18/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class BaseItem extends ObjModelMtlVBO implements Item, GLItemDrawable, UpdatableItem {

	private native boolean areCollided(float[] mPointItem1, float[] mModelMatrix1, float[] mPointItem2, float[] mModelMatrix2);

	static {
		System.loadLibrary("collision");
	}

	protected final int maxLife;
	protected int life;

	protected float[] mPosition;
	protected float[] mSpeed;
	protected float[] mAcceleration;

	protected float[] mRotationMatrix;

	protected float[] mModelMatrix;

	private float radius;

	protected float scale;

	private static float RayMaxRand = Float.MAX_VALUE * 0.5f;
	private boolean isDanger;

	public BaseItem(Context context, String objFileName, String mtlFileName, float lightAugmentation, float distanceCoef, boolean randomColor, int life, float[] mPosition, float[] mSpeed, float[] mAcceleration, float scale) {
		super(context, objFileName, mtlFileName, lightAugmentation, distanceCoef, randomColor);
		this.life = life;
		this.maxLife = this.life;
		this.mPosition = mPosition;
		this.mSpeed = mSpeed;
		this.mAcceleration = mAcceleration;
		this.mRotationMatrix = new float[16];
		Matrix.setIdentityM(this.mRotationMatrix, 0);
		this.mModelMatrix = new float[16];
		Matrix.setIdentityM(this.mModelMatrix, 0);
		this.scale = scale;
		this.radius = this.scale * 2f;
		this.isDanger = false;
	}

	public BaseItem(ObjModelMtlVBO objModelMtl, int life, float[] mPosition, float[] mSpeed, float[] mAcceleration, float scale) {
		super(objModelMtl);
		this.life = life;
		this.maxLife = this.life;
		this.mPosition = mPosition;
		this.mSpeed = mSpeed;
		this.mAcceleration = mAcceleration;
		this.mRotationMatrix = new float[16];
		Matrix.setIdentityM(this.mRotationMatrix, 0);
		this.mModelMatrix = new float[16];
		Matrix.setIdentityM(this.mModelMatrix, 0);
		this.scale = scale;
		this.radius = this.scale * 2f;
		this.isDanger = false;
	}

	public boolean isAlive() {
		return this.life > 0;
	}

	@Override
	public boolean collideTest(float[] triangleArray, float[] modelMatrix, Box unused) {
		return this.areCollided(this.allCoords.clone(), this.mModelMatrix.clone(), triangleArray, modelMatrix);
	}

	@Override
	public boolean isCollided(Item other) {
		return other.collideTest(super.allCoords.clone(), this.mModelMatrix.clone(), this.makeBox());
	}

	public Box makeBox() {
		return new Box(this.mPosition[0] - this.radius * 0.5f, this.mPosition[1] - this.radius * 0.5f, this.mPosition[2] - this.radius * 0.5f, this.radius, this.radius, this.radius);
	}

	@Override
	public boolean isInside(Box box) {
		return this.makeBox().isInside(box);
	}

	@Override
	public int getDamage() {
		return this.life;
	}

	@Override
	public void decrementLife(int minus) {
		this.life = this.life - minus >= 0 ? this.life - minus : 0;
	}

	@Override
	public float[] getPosition() {
		return this.mPosition.clone();
	}

	private boolean willIntersect(BaseItem target) {
		float[] normSpeed = Vector.normalize3f(this.mSpeed);
		float[] maxSpeed = Vector.mul3f(normSpeed, BaseItem.RayMaxRand);
		float[] dir = Vector.add3f(maxSpeed, this.mPosition);
		return target.makeBox().rayIntersect(new Ray(this.mPosition, dir));
	}

	private boolean willIntersectOne(List<BaseItem> targets) {
		for (BaseItem t : targets)
			if (this.willIntersect(t))
				return true;
		return false;
	}

	public boolean isDanger() {
		return this.isDanger;
	}

	public void computeDanger(List<BaseItem> targets) {
		this.isDanger = this.willIntersectOne(targets);
	}

	public float[] vector3fTo(BaseItem to) {
		return new float[]{to.mPosition[0] - this.mPosition[0], to.mPosition[1] - this.mPosition[1], to.mPosition[2] - this.mPosition[2]};
	}

	@Override
	public void update() {
		this.mSpeed[0] += this.mAcceleration[0];
		this.mSpeed[1] += this.mAcceleration[1];
		this.mSpeed[2] += this.mAcceleration[2];

		this.mPosition[0] += this.mSpeed[0];
		this.mPosition[1] += this.mSpeed[1];
		this.mPosition[2] += this.mSpeed[2];

		float[] tmp = new float[16];
		Matrix.setIdentityM(tmp, 0);
		Matrix.translateM(tmp, 0, this.mPosition[0], this.mPosition[1], this.mPosition[2]);
		Matrix.scaleM(tmp, 0, this.scale, this.scale, this.scale);
		this.mModelMatrix = tmp.clone();
	}

	@Override
	public void draw(float[] pMatrix, float[] vMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		float[] mvMatrix = new float[16];
		Matrix.multiplyMM(mvMatrix, 0, vMatrix, 0, this.mModelMatrix, 0);
		float[] mvpMatrix = new float[16];
		Matrix.multiplyMM(mvpMatrix, 0, pMatrix, 0, mvMatrix, 0);
		super.draw(mvpMatrix, mvMatrix, mLightPosInEyeSpace, mCameraPosition);
	}
}
