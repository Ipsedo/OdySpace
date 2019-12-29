package com.samuelberrien.odyspace.core.objects;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.explosion.Explosion;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;

/**
 * Created by samuel on 23/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Base extends BaseItem {

	private Explosion mExplosion;

	//TODO vrai Crashable ?
	public Base(Context context, String objFileName, String mtlFileName, float lightAugmentation, float distanceCoef, boolean randomColor, int life, float[] mPosition, float scale) {
		super(context, objFileName, mtlFileName, objFileName, lightAugmentation, distanceCoef, randomColor, life, mPosition, new float[3], new float[3], scale);
	}

	public Base(Context context, ObjModelMtlVBO objModelMtl, CrashableMesh crashableMesh, int life, float[] mPosition, float scale) {
		super(context, objModelMtl, crashableMesh, life, mPosition, new float[3], new float[3], scale);
	}

	@Override
	public int getDamage() {
		return Integer.MAX_VALUE - 1;
	}

	@Override
	protected Explosion getExplosion() {
		return new Explosion.ExplosionBuilder()
				.setNbParticules(40)
				.setLimitScale(5f)
				.setRangeScale(2f)
				.setLimitSpeed(6f)
				.setRangeSpeed(4f)
				.makeExplosion(glContext, objModelMtlVBO.getRandomMtlDiffRGB());
	}
	//mExplosion = new Explosion(particule, super.diffColorBuffer, 40, 0.16f, 5f, 7f, 6f, 10f);
}
