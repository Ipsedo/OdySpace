package com.samuelberrien.odyspace.objects.baseitem;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.drawable.obj.ObjModel;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;

import java.util.List;

/**
 * Created by samuel on 23/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Base extends BaseItem {

	private Explosion mExplosion;

	public Base(Context context, String objFileName, String mtlFileName, float lightAugmentation, float distanceCoef, boolean randomColor, int life, float[] mPosition, float scale) {
		super(context, objFileName, mtlFileName, lightAugmentation, distanceCoef, randomColor, life, mPosition, new float[3], new float[3], scale);
	}

	public Base(ObjModelMtlVBO objModelMtl, int life, float[] mPosition, float scale) {
		super(objModelMtl, life, mPosition, new float[3], new float[3], scale);
	}

	@Override
	public int getDamage() {
		return Integer.MAX_VALUE - 1;
	}

	public void makeExplosion() {
		this.mExplosion = new Explosion(super.context, super.diffColorBuffer, 40, 0.16f, 5f, 7f, 6f, 10f);
	}

	public void makeExplosion(ObjModel particule) {
		this.mExplosion = new Explosion(particule, super.diffColorBuffer, 40, 0.16f, 5f, 7f, 6f, 10f);
	}

	public void addExplosion(List<Explosion> explosions) {
		this.mExplosion.setPosition(this.mPosition.clone());
		explosions.add(this.mExplosion);
	}
}
