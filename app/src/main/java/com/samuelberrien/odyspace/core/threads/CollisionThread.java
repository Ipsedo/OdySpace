package com.samuelberrien.odyspace.core.threads;

import com.samuelberrien.odyspace.core.Level;

/**
 * Created by samuel on 09/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class CollisionThread extends CancelableThread {

	public CollisionThread(Level level) {
		super("CollisionThread", level);
		setPriority(Thread.NORM_PRIORITY);
		timeToWait = 5L;
	}

	@Override
	public void afterInit() {

	}

	@Override
	public void work() {
		level.collide();
	}
}
