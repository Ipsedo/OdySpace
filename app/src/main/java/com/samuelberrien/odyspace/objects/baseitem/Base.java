package com.samuelberrien.odyspace.objects.baseitem;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.explosion.Explosion;
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

	@Override
	protected Explosion getExplosion() {
		return new Explosion.ExplosionBuilder().setNbParticules(40)
				.setLimitSpeedAlife(0.16f)
				.setLimitScale(5f)
				.setMaxScale(7f)
				.setLimitSpeed(6f)
				.setMaxSpeed(10f)
				.makeExplosion(context, diffColorBuffer);
	}

	@Override
	protected Explosion getExplosion(ObjModel particule) {
		return new Explosion.ExplosionBuilder().setNbParticules(40)
				.setLimitSpeedAlife(0.16f)
				.setLimitScale(5f)
				.setMaxScale(7f)
				.setLimitSpeed(6f)
				.setMaxSpeed(10f)
				.makeExplosion(particule, diffColorBuffer);
	}


	//this.mExplosion = new Explosion(particule, super.diffColorBuffer, 40, 0.16f, 5f, 7f, 6f, 10f);

}
