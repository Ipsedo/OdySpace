package com.samuelberrien.odyspace.core.baseitem.group;

import android.opengl.Matrix;

import com.samuelberrien.odyspace.core.Item;
import com.samuelberrien.odyspace.core.UpdatableItem;
import com.samuelberrien.odyspace.core.collision.Box;
import com.samuelberrien.odyspace.drawable.GLDrawable;

import java.util.ArrayList;

/**
 * Created by samuel on 04/08/17.
 */

public class Tree implements UpdatableItem, GLDrawable {

	private boolean isLeaf;
	private Leaf leaf;

	private float[] nodeModelMatrix;
	private float[] modelMatrixToPass;
	private ArrayList<Tree> sons;

	public Tree(Leaf leaf) {
		isLeaf = true;
		this.leaf = leaf;
	}

	public Tree(ArrayList<Tree> sons, float[] nodeModelMatrix) {
		isLeaf = false;
		this.nodeModelMatrix = nodeModelMatrix;
		this.modelMatrixToPass = nodeModelMatrix;
	}

	public void setParentModelMatrix(float[] parentModelMatrix) {
		float[] res = new float[16];
		Matrix.multiplyMM(res, 0, parentModelMatrix, 0, nodeModelMatrix, 0);
		modelMatrixToPass = res;
	}

	@Override
	public void update() {
		if (isLeaf) {
			leaf.setParentModelMatrix(modelMatrixToPass);
			leaf.update();
		} else
			for (Tree son : sons) {
				son.setParentModelMatrix(modelMatrixToPass);
				son.update();
			}
	}

	public boolean isInside(Box box) {
		if (isLeaf)
			return leaf.isInside(box);
		else {
			for (Tree son : sons)
				if (son.isInside(box))
					return true;
			return false;
		}
	}

	public boolean isCollided(Item other) {
		if (isLeaf)
			return other.collideTest(leaf.cloneTriangleArray(), leaf.cloneModelMatrix(), leaf.makeBox());
		else {
			for (Tree son : sons)
				if (son.isCollided(other))
					return true;
			return false;
		}
	}

	public boolean collideTest(float[] triangleArray, float[] modelMatrix, Box container) {
		if (isLeaf && leaf.isInside(container))
			return leaf.collideTest(triangleArray, modelMatrix, container);
		else {
			for (Tree son : sons)
				if (son.collideTest(triangleArray, modelMatrix, container))
					return true;
			return false;
		}
	}

	@Override
	public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		if (isLeaf)
			leaf.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		else
			for (Tree son : sons)
				son.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);
	}
}
