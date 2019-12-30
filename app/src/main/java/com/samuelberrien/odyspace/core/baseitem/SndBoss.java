package com.samuelberrien.odyspace.core.baseitem;

import android.content.Context;

import com.samuelberrien.odyspace.core.baseitem.ship.Ship;
import com.samuelberrien.odyspace.core.fire.FireType;
import com.samuelberrien.odyspace.drawable.Explosion;

import java.util.List;
import java.util.Random;

public class SndBoss extends Boss {

	private int counter;
	private static final int MAX_COUNT = 200;

	private Random rand;

	private Ship ship;

	public SndBoss(Context context, float[] mPosition, List<BaseItem> rockets, Ship ship) {
		super(context, "", "", 40, mPosition, 5.f, FireType.GUIDED_MISSILE.getFire(context), rockets);
		counter = 0;
		rand = new Random(System.currentTimeMillis());
		this.ship = ship;
	}

	private void count() {
		counter = (counter + 1) % MAX_COUNT;
	}

	@Override
	public void fire() {
		if (rand.nextFloat() < 5e-2f)
			fire.fire(rockets, mPosition, mSpeed, mRotationMatrix, 0.7f, ship);
	}

	@Override
	protected float[] computeModelMatrix() {
		return new float[16];
	}

	@Override
	protected Explosion getExplosion() {
		throw new UnsupportedOperationException("SndBoss.getExplosion()");
	}

	@Override
	public void update() {
		count();
		super.update();
	}
}
