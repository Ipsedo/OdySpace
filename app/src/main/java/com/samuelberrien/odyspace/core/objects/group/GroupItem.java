package com.samuelberrien.odyspace.core.objects.group;

import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.GLDrawable;
import com.samuelberrien.odyspace.core.collision.Box;
import com.samuelberrien.odyspace.core.Item;
import com.samuelberrien.odyspace.core.UpdatableItem;

/**
 * Created by samuel on 04/08/17.
 */

public class GroupItem implements Item, GLDrawable, UpdatableItem {

	private Tree root;
	private int life;

	protected float[] mPosition;
	protected float[] mSpeed;
	protected float[] mAcceleration;
	protected float[] mModelMatrix;

	public GroupItem(Tree root, int life, float[] mPosition, float[] mSpeed, float[] mAcceleration) {
		this.root = root;
		this.life = life;
		this.mPosition = mPosition;
		this.mSpeed = mSpeed;
		this.mAcceleration = mAcceleration;
		Matrix.setIdentityM(mModelMatrix = new float[16], 0);
	}

	@Override
	public boolean collideTest(float[] triangleArray, float[] modelMatrix, Box container) {
		return root.collideTest(triangleArray, modelMatrix, container);
	}

	@Override
	public boolean isCollided(Item other) {
		return root.isCollided(other);
	}

	@Override
	public boolean isInside(Box box) {
		return root.isInside(box);
	}

	@Override
	public boolean isAlive() {
		return life > 0;
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

	@Override
	public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		root.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
	}

	@Override
	public void update() {
		root.setParentModelMatrix(mModelMatrix);
		root.update();
	}
}
