package com.samuelberrien.odyspace.objects.baseitem.group;

import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.GLItemDrawable;
import com.samuelberrien.odyspace.utils.collision.Box;
import com.samuelberrien.odyspace.utils.game.Item;
import com.samuelberrien.odyspace.utils.game.UpdatableItem;

import java.util.ArrayList;

/**
 * Created by samuel on 04/08/17.
 */

public class GroupItem implements Item, GLItemDrawable, UpdatableItem {

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
		return this.life > 0;
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

	@Override
	public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		root.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
	}

	@Override
	public void update() {
		root.setParentModelMatrix(this.mModelMatrix);
		root.update();
	}
}
