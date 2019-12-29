package com.samuelberrien.odyspace.core.objects;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.core.collision.CollisionMesh;
import com.samuelberrien.odyspace.core.collision.TriangleCollision;
import com.samuelberrien.odyspace.drawable.GLDrawable;
import com.samuelberrien.odyspace.drawable.explosion.Explosion;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.core.collision.Box;
import com.samuelberrien.odyspace.core.collision.Ray;
import com.samuelberrien.odyspace.core.Item;
import com.samuelberrien.odyspace.core.UpdatableItem;
import com.samuelberrien.odyspace.utils.maths.Vector;

import java.util.List;


/**
 * Created by samuel on 18/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public abstract class BaseItem implements Item, GLDrawable, UpdatableItem {

	/*private native boolean areCollided(float[] mPointItem1, float[] mModelMatrix1,
									   float[] mPointItem2, float[] mModelMatrix2);

	static {
		System.loadLibrary("collision");
	}*/

	protected int maxLife;
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

	private Explosion mExplosion;
	private boolean exploded;

	protected ObjModelMtlVBO objModelMtlVBO;

	private CollisionMesh collisionMesh;

	protected Context glContext;

	private boolean needMAkeExplosion;

	public BaseItem(Context glContext,
					String objFileName, String mtlFileName,
					String objCrashableMeshFileName,
					float lightAugmentation, float distanceCoef, boolean randomColor,
					int life,
					float[] mPosition, float[] mSpeed, float[] mAcceleration, float scale) {
		objModelMtlVBO = new ObjModelMtlVBO(glContext,
				objFileName, mtlFileName, lightAugmentation, distanceCoef, randomColor);
		collisionMesh = new CollisionMesh(glContext, objCrashableMeshFileName);

		init(glContext, life, mPosition, mSpeed, mAcceleration, scale);
	}

	public BaseItem(Context glContext,
					ObjModelMtlVBO objModelMtl, CollisionMesh collisionMesh,
					int life,
					float[] mPosition, float[] mSpeed, float[] mAcceleration, float scale) {
		objModelMtlVBO = objModelMtl;
		this.collisionMesh = collisionMesh;

		init(glContext, life, mPosition, mSpeed, mAcceleration, scale);
	}

	private void init(Context glContext,
					  int life,
					  float[] mPosition, float[] mSpeed, float[] mAcceleration, float scale) {
		this.glContext = glContext;
		this.life = life;
		maxLife = this.life;
		this.mPosition = mPosition;
		this.mSpeed = mSpeed;
		this.mAcceleration = mAcceleration;
		mRotationMatrix = new float[16];
		Matrix.setIdentityM(mRotationMatrix, 0);
		mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);
		this.scale = scale;
		radius = this.scale * 2f;
		isDanger = false;
		needMAkeExplosion = false;
	}

	@Override
	public boolean isAlive() {
		return life > 0;
	}

	@Override
	public boolean collideTest(float[] triangleArray, float[] modelMatrix, Box unused) {
		return TriangleCollision.AreCollided(
				collisionMesh.cloneVertices(), mModelMatrix.clone(),
				triangleArray, modelMatrix);
	}

	@Override
	public boolean isCollided(Item other) {
		return other.collideTest(collisionMesh.cloneVertices(), mModelMatrix.clone(), makeBox());
	}

	public Box makeBox() {
		return new Box(
				mPosition[0] - radius * 0.5f,
				mPosition[1] - radius * 0.5f,
				mPosition[2] - radius * 0.5f,
				radius, radius, radius);
	}

	@Override
	public boolean isInside(Box box) {
		return makeBox().isInside(box);
	}

	@Override
	public int getDamage() {
		return life;
	}

	@Override
	public void decrementLife(int minus) {
		life = life - minus >= 0 ? life - minus : 0;
	}

	@Override
	public float[] clonePosition() {
		return mPosition.clone();
	}

	private boolean willIntersect(BaseItem target) {
		float[] normSpeed = Vector.normalize3f(mSpeed);
		float[] maxSpeed = Vector.mul3f(normSpeed, BaseItem.RayMaxRand);
		float[] dir = Vector.add3f(maxSpeed, mPosition);
		return target.makeBox().rayIntersect(new Ray(mPosition, dir));
	}

	private boolean willIntersectOne(List<BaseItem> targets) {
		for (BaseItem t : targets)
			if (willIntersect(t))
				return true;
		return false;
	}

	public boolean isDanger() {
		return isDanger;
	}

	public void computeDanger(List<BaseItem> targets) {
		isDanger = willIntersectOne(targets);
	}

	public float[] vector3fTo(BaseItem to) {
		return new float[]{
				to.mPosition[0] - mPosition[0],
				to.mPosition[1] - mPosition[1],
				to.mPosition[2] - mPosition[2]};
	}

	@Override
	public void update() {
		mSpeed[0] += mAcceleration[0];
		mSpeed[1] += mAcceleration[1];
		mSpeed[2] += mAcceleration[2];

		mPosition[0] += mSpeed[0];
		mPosition[1] += mSpeed[1];
		mPosition[2] += mSpeed[2];

		float[] tmp = new float[16];
		Matrix.setIdentityM(tmp, 0);
		Matrix.translateM(tmp, 0, mPosition[0], mPosition[1], mPosition[2]);

		Matrix.multiplyMM(tmp, 0, tmp.clone(), 0, mRotationMatrix, 0);
		Matrix.scaleM(tmp, 0, scale, scale, scale);
		mModelMatrix = tmp.clone();
	}

	@Override
	public void draw(float[] pMatrix,
					 float[] vMatrix,
					 float[] mLightPosInEyeSpace,
					 float[] mCameraPosition) {
		if (needMAkeExplosion) {
			makeExplosion();
			needMAkeExplosion = false;
		}
		float[] mvMatrix = new float[16];
		Matrix.multiplyMM(mvMatrix, 0, vMatrix, 0, mModelMatrix, 0);
		float[] mvpMatrix = new float[16];
		Matrix.multiplyMM(mvpMatrix, 0, pMatrix, 0, mvMatrix, 0);
		objModelMtlVBO.draw(mvpMatrix, mvMatrix, mLightPosInEyeSpace, mCameraPosition);
	}

	public final void addExplosion(List<Explosion> explosions) {
		if (!exploded) {
			mExplosion.setPosition(mPosition.clone());
			explosions.add(mExplosion);
			exploded = true;
		}
	}

	public void queueExplosion() {
		needMAkeExplosion = true;
	}

	private void makeExplosion() {
		mExplosion = getExplosion();
	}

	protected abstract Explosion getExplosion();
}
