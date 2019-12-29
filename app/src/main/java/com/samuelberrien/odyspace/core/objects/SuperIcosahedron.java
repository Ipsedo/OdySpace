package com.samuelberrien.odyspace.core.objects;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.core.collision.CollisionMesh;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;

import java.util.Random;

/**
 * Created by samuel on 30/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class SuperIcosahedron extends Icosahedron {

	private float mAngle;
	private float angularSpeed;
	private Random random;
	private float[] rotAxis;

	public SuperIcosahedron(Context context, ObjModelMtlVBO model, CollisionMesh collisionMesh, int life, float[] mPosition, float[] mSpeed, float scale) {
		super(context, model, collisionMesh, life, mPosition, mSpeed, scale);
		init();
	}

	private void init() {
		rotAxis = new float[3];
		random = new Random(System.currentTimeMillis());
		mAngle = random.nextFloat() * 360f;
		angularSpeed = random.nextFloat();
		rotAxis[0] = random.nextFloat() * 2f - 1f;
		rotAxis[1] = random.nextFloat() * 2f - 1f;
		rotAxis[2] = random.nextFloat() * 2f - 1f;
	}

	@Override
	public void update() {
		mAngle += angularSpeed;
		mAngle = mAngle < 360f ? mAngle : mAngle - 360f;
		Matrix.setRotateM(mRotationMatrix, 0, mAngle, rotAxis[0], rotAxis[1], rotAxis[2]);
		super.update();
	}


	@Override
	public int getDamage() {
		return Integer.MAX_VALUE - 1;
	}
}
