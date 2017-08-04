package com.samuelberrien.odyspace.objects.baseitem.group;

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

	public GroupItem(Tree root) {
		this.root = root;
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
	public int getDamage() {
		return 0;
	}

	@Override
	public void decrementLife(int minus) {

	}

	@Override
	public float[] getPosition() {
		return new float[0];
	}

	@Override
	public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		root.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
	}

	@Override
	public void update() {
		root.update();
	}
}
