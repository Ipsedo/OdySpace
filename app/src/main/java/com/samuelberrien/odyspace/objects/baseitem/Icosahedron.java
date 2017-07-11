package com.samuelberrien.odyspace.objects.baseitem;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.drawable.obj.ObjModel;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;

import java.util.List;

/**
 * Created by samuel on 24/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Icosahedron extends BaseItem {

	private Explosion mExplosion;

	public Icosahedron(Context context, int life, float[] mPosition, float scale) {
		super(context, "icosahedron.obj", "icosahedron.mtl", 0.7f, 0f, true, life, mPosition, new float[]{0f, 0f, 0f}, new float[]{0f, 0f, 0f}, scale);
	}

	public Icosahedron(ObjModelMtlVBO model, int life, float[] mPosition, float[] mSpeed, float scale) {
		super(model, life, mPosition, mSpeed, new float[3], scale);
	}

	public void makeExplosion() {
		this.mExplosion = new Explosion(super.context, super.mPosition.clone(), super.diffColorBuffer, (int) Math.ceil(super.scale / 2f) * 10, 0.05f, (float) Math.ceil(super.scale / 5f), (float) Math.ceil(super.scale / 2f), (float) Math.ceil(super.scale / 3f) * 0.9f, (float) Math.ceil(super.scale / 3f) * 1.7f);
	}

	public void makeExplosion(ObjModel particule) {
		this.mExplosion = new Explosion(particule, super.mPosition.clone(), super.diffColorBuffer, (int) Math.ceil(super.scale / 2f) * 10, 0.05f, (float) Math.ceil(super.scale / 5f), (float) Math.ceil(super.scale / 2f), (float) Math.ceil(super.scale / 3f) * 0.9f, (float) Math.ceil(super.scale / 3f) * 1.7f);
	}

	public void addExplosion(List<Explosion> explosions) {
		this.mExplosion.setPosition(this.mPosition.clone());
		explosions.add(this.mExplosion);
	}
}
