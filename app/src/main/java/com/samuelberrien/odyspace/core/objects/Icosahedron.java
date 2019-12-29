package com.samuelberrien.odyspace.core.objects;

import android.content.Context;

import com.samuelberrien.odyspace.core.collision.CollisionMesh;
import com.samuelberrien.odyspace.drawable.explosion.Explosion;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;

/**
 * Created by samuel on 24/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Icosahedron extends BaseItem {

	//TODO modèles simplifiés pr crashable ?
	public Icosahedron(Context context, int life, float[] mPosition, float scale) {
		super(context, "obj/icosahedron.obj", "obj/icosahedron.mtl", "obj/icosahedron.obj", 0.7f, 0f, true, life, mPosition, new float[]{0f, 0f, 0f}, new float[]{0f, 0f, 0f}, scale);
	}

	public Icosahedron(Context context, ObjModelMtlVBO model, CollisionMesh collisionMesh, int life, float[] mPosition, float[] mSpeed, float scale) {
		super(context, model, collisionMesh, life, mPosition, mSpeed, new float[3], scale);
	}

	@Override
	protected Explosion getExplosion() {
		return new Explosion.ExplosionBuilder()
				.setNbParticules((int) (40f * Math.log(scale) / Math.log(2d)))
				.setLimitScale(scale / 5f)
				.setRangeScale(scale / 5f)
				.setLimitSpeed((scale / 3f) * 0.5f)
				.setRangeSpeed((scale / 3f) * 1.5f)
				.makeExplosion(glContext, objModelMtlVBO.getRandomMtlDiffRGB());
	}

	//mExplosion = new Explosion(super.glContext, super.diffColorBuffer, (int) Math.ceil(super.scale / 2f) * 10, 0.05f, (float) Math.ceil(super.scale / 5f), (float) Math.ceil(super.scale / 2f), (float) Math.ceil(super.scale / 3f) * 0.9f, (float) Math.ceil(super.scale / 3f) * 1.7f);


}
