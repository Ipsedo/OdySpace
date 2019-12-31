package com.samuelberrien.odyspace.core.baseitem.boss;

import android.content.Context;

import com.samuelberrien.odyspace.core.Shooter;
import com.samuelberrien.odyspace.core.baseitem.BaseItem;
import com.samuelberrien.odyspace.core.fire.Fire;
import com.samuelberrien.odyspace.drawable.ProgressBar;

import java.util.List;

/**
 * Created by samuel on 30/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public abstract class Boss extends BaseItem implements Shooter {

	protected Fire fire;
	protected List<BaseItem> rockets;

	//TODO modèles simplifiés pr crashable ?
	public Boss(Context context, String objFileName, String mtlFileName, int life, float[] mPosition, float scale, Fire fire, List<BaseItem> rockets) {
		super(context, objFileName, mtlFileName, objFileName, 1f, 0f, false, life, mPosition, new float[]{0f, 0f, 0f}, new float[]{0f, 0f, 0f}, scale);
		this.fire = fire;
		this.rockets = rockets;
	}

	@Override
	public int getDamage() {
		return Integer.MAX_VALUE - 1;
	}


	public void updateLifeProgress(ProgressBar progressBar) {
		progressBar.updateProgress(super.maxLife - super.life);
	}

	@Override
	public abstract void fire();

	@Override
	public void update() {
		super.mModelMatrix = computeModelMatrix();
	}

	protected abstract float[] computeModelMatrix();
}
